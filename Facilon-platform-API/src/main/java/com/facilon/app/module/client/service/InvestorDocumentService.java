package com.facilon.app.module.client.service;

import com.facilon.app.integration.dynamics.DynamicsCrmService;
import com.facilon.app.integration.sharepoint.SharePointService;
import com.facilon.app.module.client.dto.DocumentResponseDto;
import com.facilon.app.module.client.dto.KycDocumentRequirementDto;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.model.IntroInvestorTemp;
import com.facilon.app.module.client.model.KycDocuments;
import com.facilon.app.module.client.model.UserPersonalInformation;
import com.facilon.app.module.client.repository.IntroInvestorTempRepository;
import com.facilon.app.module.client.repository.KycDocumentsRepository;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.module.client.repository.UserPersonalInformationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestorDocumentService {

    private final InvestorRepository investorRepository;
    private final KycDocumentsRepository kycDocumentsRepository;
    private final IntroInvestorTempRepository introInvestorTempRepository;
    private final UserPersonalInformationRepository userPersonalInformationRepository;
    
    @Autowired(required = false)
    private SharePointService sharePointService;
    
    @Autowired(required = false)
    private DynamicsCrmService dynamicsCrmService;

    /** Laravel-style URL segment for ss_documenturl (see InvestorInformationSubmissionService). */
    @Value("${sharepoint.investor-documents.base-url:https://facilon.sharepoint.com/sites/CRM/ss_investordocuments}")
    private String sharePointDocumentsBaseUrl;

    @Value("${dataverse.sharepoint.parent-location-id:112fd6a2-12f1-f011-8406-7ced8d285638}")
    private String parentSharePointLocationId;

    @Value("${dataverse.sharepoint.site-collection-id:43de59de-265e-f011-877b-7c1e5232a375}")
    private String siteCollectionId;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "application/pdf", "image/jpeg", "image/jpg", "image/png");

    @jakarta.annotation.PostConstruct
    public void logServiceStatus() {
        log.info("📦 InvestorDocumentService initialized | SharePoint: {} | Dataverse: {}",
                sharePointService != null ? "AVAILABLE" : "NOT AVAILABLE (will use local storage)",
                dynamicsCrmService != null ? "AVAILABLE" : "NOT AVAILABLE");
    }

    /**
     * Overload for backward compatibility (when documentMasterId is not provided).
     */
    @Transactional
    public DocumentResponseDto uploadKycDocument(String uniqueCode, MultipartFile file, String documentType) {
        return uploadKycDocument(uniqueCode, file, documentType, null);
    }

    @Transactional
    public DocumentResponseDto uploadKycDocument(String uniqueCode, MultipartFile file, String documentType, String documentMasterId) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Uploading KYC document for investor: {}, type: {}, dynamicsId: {}", uniqueCode, documentType, documentMasterId);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size must not exceed 10 MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Only PDF, JPG, JPEG, or PNG files are allowed");
        }

        // Upload to SharePoint if available, otherwise save locally
        String documentUrl;
        String sharePointItemId = null;
        String fileName = documentType + "_" + System.currentTimeMillis() + "_" + sanitizeFileName(file.getOriginalFilename());
        
        try {
            if (sharePointService != null) {
                // Primary: Upload to SharePoint
                String folderPath = "KYC/" + uniqueCode;
                sharePointItemId = sharePointService.uploadFile(folderPath, fileName, file.getBytes());
                documentUrl = sharePointItemId;
                log.info("✅ Uploaded to SharePoint: {}", sharePointItemId);
            } else {
                // Fallback: Save to local filesystem
                documentUrl = saveFileLocally(file, uniqueCode, documentType, fileName);
                log.warn("⚠️ SharePoint not available, saved to local filesystem: {}", documentUrl);
            }
        } catch (Exception e) {
            // Fallback: Save to local filesystem if SharePoint upload fails or config missing
            log.error("❌ SharePoint upload failed ({}), falling back to local storage", e.getMessage());
            try {
                documentUrl = saveFileLocally(file, uniqueCode, documentType, fileName);
                log.info("✅ Fallback: Saved to local filesystem: {}", documentUrl);
            } catch (IOException fallbackException) {
                log.error("❌ Critical: Failed to save file locally: {}", fallbackException.getMessage());
                throw new RuntimeException("Failed to save file: " + fallbackException.getMessage());
            }
        }

        // Find existing KYC doc for this investor + document type (uploadType=1) to update or create new
        List<KycDocuments> existing = kycDocumentsRepository.findByInvestorUniqueIdAndDeletedAtIsNull(uniqueCode);
        Optional<KycDocuments> existingDoc = existing.stream()
                .filter(d -> documentType.equals(d.getDocumentType()) && Integer.valueOf(1).equals(d.getUploadType()))
                .findFirst();

        KycDocuments doc;
        if (existingDoc.isPresent()) {
            doc = existingDoc.get();
            doc.setDocumentUrl(documentUrl);
            doc.setStatus("Submitted");
            doc.setDocDescription(file.getOriginalFilename());
            if (documentMasterId != null && !documentMasterId.isEmpty()) {
                doc.setDocumentMasterId(documentMasterId);
            }
        } else {
            doc = KycDocuments.builder()
                    .investorUniqueId(uniqueCode)
                    .ssInvestorId(investor.getId() != null ? investor.getId().toString() : null)
                    .documentType(documentType)
                    .documentUrl(documentUrl)
                    .docDescription(file.getOriginalFilename())
                    .status("Submitted")
                    .uploadType(1)
                    .documentMasterId(documentMasterId)
                    .build();
            if (investor.getTenant() != null) {
                doc.setTenant(investor.getTenant());
            }
        }

        doc = kycDocumentsRepository.save(doc);
        log.info("KYC document saved to DB | investor_unique_id={} | storage={} | document_master_id={} | document_url={}",
                uniqueCode, sharePointItemId != null ? "SharePoint" : "local", documentMasterId, doc.getDocumentUrl());

        // Sync with Dataverse (align with Laravel)
        if (dynamicsCrmService != null && documentMasterId != null && !documentMasterId.isEmpty()) {
            try {
                log.info("Syncing upload to Dataverse for investor: {}, docType: {}, dynamicsId: {}", uniqueCode, documentType, documentMasterId);
                dynamicsCrmService.updateInvestorDocumentUrl(documentMasterId, documentUrl);
                log.info("✅ Successfully synced document upload to Dataverse");
                
                // Sync verification status after upload (like Laravel)
                updateVerificationStatus(uniqueCode);
            } catch (Exception e) {
                log.error("❌ Failed to sync to Dataverse: {}", e.getMessage());
                // Non-fatal: upload to local DB succeeded
            }
        } else {
            log.warn("⚠️ Skipping Dataverse sync: dynamicsCrmService={}, documentMasterId={}", 
                    dynamicsCrmService != null, documentMasterId);
        }

        return DocumentResponseDto.builder()
                .id(doc.getId())
                .documentType(doc.getDocumentType())
                .documentCategory("kyc")
                .fileName(file.getOriginalFilename())
                .documentUrl(doc.getDocumentUrl())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .status(doc.getStatus() != null ? doc.getStatus() : "Submitted")
                .uploadedAt(doc.getCreatedAt() != null ? doc.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .build();
    }

    private static String sanitizeFileName(String name) {
        if (name == null) return "document";
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    /**
     * Onboarding upload (legacy): description-only, no Dataverse id.
     */
    @Transactional
    public DocumentResponseDto uploadOnboardingDocument(String uniqueCode, MultipartFile file, String documentType) {
        return uploadOnboardingDocument(uniqueCode, file, documentType, null);
    }

    /**
     * Onboarding upload aligned with Laravel {@code onboard_document_submit}:
     * multipart keys by {@code ss_investordocumentsid}, SharePoint folder {@code {ss_name}_{CLEAN_GUID}},
     * {@code kyc_documents.upload_type=2}, {@code document_master_id}, Dataverse PATCH + optional SharePoint location.
     */
    @Transactional
    public DocumentResponseDto uploadOnboardingDocument(String uniqueCode, MultipartFile file, String documentType,
                                                        String investorDocumentId) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Uploading onboarding document for investor: {}, type: {}, investorDocumentId: {}",
                uniqueCode, documentType, investorDocumentId);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size must not exceed 10 MB");
        }
        String contentType = file.getContentType();
        if (contentType != null && !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Only PDF, JPG, JPEG, or PNG files are allowed");
        }

        boolean dataverseEnabled = dynamicsCrmService != null;
        if (dataverseEnabled && (investorDocumentId == null || investorDocumentId.isBlank())) {
            throw new IllegalArgumentException(
                    "investorDocumentId (Dataverse ss_investordocumentsid) is required when Dataverse integration is enabled");
        }

        if (dataverseEnabled && investorDocumentId != null && !investorDocumentId.isBlank()) {
            return uploadOnboardingDocumentWithDataverse(investor, uniqueCode, file, documentType, investorDocumentId.trim());
        }

        return uploadOnboardingDocumentLegacy(investor, uniqueCode, file, documentType);
    }

    private DocumentResponseDto uploadOnboardingDocumentWithDataverse(Investor investor, String uniqueCode,
                                                                        MultipartFile file, String documentType,
                                                                        String investorDocumentId) {
        Map<String, String> crmRow = dynamicsCrmService.fetchInvestorDocumentByInvestorDocumentsId(investorDocumentId);
        if (crmRow == null || crmRow.isEmpty()) {
            throw new IllegalArgumentException(
                    "Onboarding document not found in Dataverse for id: " + investorDocumentId + ". Check requirements and intro_investor_temp.");
        }
        String docName = crmRow.get("ss_name");
        if (docName == null || docName.isBlank()) {
            docName = crmRow.get("ss_description");
        }
        if (docName == null || docName.isBlank()) {
            docName = documentType;
        }
        docName = sanitizeSharePointFolderSegment(docName);

        String cleanId = investorDocumentId.replace("-", "").toUpperCase();
        String folderName = docName + "_" + cleanId;
        String storedFileName = System.currentTimeMillis() + "_" + sanitizeFileName(file.getOriginalFilename());

        String documentUrl;
        try {
            if (sharePointService != null) {
                sharePointService.uploadFile(folderName, storedFileName, file.getBytes());
                documentUrl = sharePointDocumentsBaseUrl + "/" + folderName;
                log.info("✅ Onboarding file uploaded to SharePoint folder {}", folderName);
            } else {
                documentUrl = saveFileLocally(file, uniqueCode, "onboarding/" + documentType, storedFileName);
                log.warn("⚠️ SharePoint not available, saved locally: {}", documentUrl);
            }
        } catch (Exception e) {
            log.error("❌ SharePoint onboarding upload failed: {}", e.getMessage());
            try {
                documentUrl = saveFileLocally(file, uniqueCode, "onboarding/" + documentType, storedFileName);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to save onboarding document: " + ex.getMessage());
            }
        }

        List<KycDocuments> existingAll = kycDocumentsRepository.findByInvestorUniqueIdAndDeletedAtIsNull(uniqueCode);
        Optional<KycDocuments> existingDoc = existingAll.stream()
                .filter(d -> Integer.valueOf(2).equals(d.getUploadType())
                        && investorDocumentId.equalsIgnoreCase(d.getDocumentMasterId()))
                .findFirst();

        KycDocuments doc;
        if (existingDoc.isPresent()) {
            doc = existingDoc.get();
            doc.setDocumentUrl(documentUrl);
            doc.setStatus("Submitted");
            doc.setDocDescription(file.getOriginalFilename());
            doc.setDocumentType(documentType);
            doc.setDocumentMasterId(investorDocumentId);
        } else {
            doc = KycDocuments.builder()
                    .investorUniqueId(uniqueCode)
                    .ssInvestorId(investor.getId() != null ? investor.getId().toString() : null)
                    .documentType(documentType)
                    .documentUrl(documentUrl)
                    .docDescription(file.getOriginalFilename())
                    .status("Submitted")
                    .uploadType(2)
                    .documentMasterId(investorDocumentId)
                    .build();
            if (investor.getTenant() != null) {
                doc.setTenant(investor.getTenant());
            }
        }
        doc = kycDocumentsRepository.save(doc);

        try {
            log.info("Syncing onboarding upload to Dataverse for dynamics id {}", investorDocumentId);
            dynamicsCrmService.updateInvestorDocumentUrl(investorDocumentId, documentUrl);
        } catch (Exception e) {
            log.error("❌ Dataverse PATCH failed (local row saved): {}", e.getMessage());
            throw new RuntimeException("Document stored but CRM update failed: " + e.getMessage());
        }

        try {
            dynamicsCrmService.createSharePointDocumentLocation(
                    investorDocumentId, folderName, parentSharePointLocationId, siteCollectionId);
        } catch (Exception e) {
            log.warn("SharePoint document location not created (non-fatal): {}", e.getMessage());
        }

        try {
            updateVerificationStatus(uniqueCode);
        } catch (Exception e) {
            log.warn("Verification status sync skipped: {}", e.getMessage());
        }

        return DocumentResponseDto.builder()
                .id(doc.getId())
                .documentType(doc.getDocumentType())
                .documentCategory("onboarding")
                .fileName(file.getOriginalFilename())
                .documentUrl(doc.getDocumentUrl())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .status(doc.getStatus())
                .uploadedAt(doc.getCreatedAt() != null ? doc.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .build();
    }

    private DocumentResponseDto uploadOnboardingDocumentLegacy(Investor investor, String uniqueCode,
                                                             MultipartFile file, String documentType) {
        String fileName = documentType + "_" + System.currentTimeMillis() + "_" + sanitizeFileName(file.getOriginalFilename());
        String documentUrl;
        String sharePointItemId = null;

        try {
            if (sharePointService != null) {
                String folderPath = "Onboarding/" + uniqueCode;
                sharePointItemId = sharePointService.uploadFile(folderPath, fileName, file.getBytes());
                documentUrl = sharePointItemId;
                log.info("✅ Uploaded to SharePoint (legacy path): {}", sharePointItemId);
            } else {
                documentUrl = saveFileLocally(file, uniqueCode, "onboarding/" + documentType, fileName);
                log.warn("⚠️ SharePoint not available, saved to local filesystem: {}", documentUrl);
            }
        } catch (Exception e) {
            log.error("❌ SharePoint upload failed ({}), falling back to local storage", e.getMessage());
            try {
                documentUrl = saveFileLocally(file, uniqueCode, "onboarding/" + documentType, fileName);
            } catch (IOException fallbackException) {
                throw new RuntimeException("Failed to save file: " + fallbackException.getMessage());
            }
        }

        KycDocuments doc = KycDocuments.builder()
                .investorUniqueId(uniqueCode)
                .ssInvestorId(investor.getId() != null ? investor.getId().toString() : null)
                .documentType(documentType)
                .documentUrl(documentUrl)
                .docDescription(file.getOriginalFilename())
                .status("Submitted")
                .uploadType(2)
                .build();

        if (investor.getTenant() != null) {
            doc.setTenant(investor.getTenant());
        }

        doc = kycDocumentsRepository.save(doc);
        log.info("Onboarding document saved (legacy) | investor_unique_id={} | document_url={}", uniqueCode, doc.getDocumentUrl());

        return DocumentResponseDto.builder()
                .id(doc.getId())
                .documentType(doc.getDocumentType())
                .documentCategory("onboarding")
                .fileName(file.getOriginalFilename())
                .documentUrl(doc.getDocumentUrl())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .status(doc.getStatus())
                .uploadedAt(doc.getCreatedAt() != null ? doc.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .build();
    }

    private static String sanitizeSharePointFolderSegment(String name) {
        if (name == null || name.isBlank()) {
            return "Document";
        }
        return name.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
    }

    public List<DocumentResponseDto> getInvestorDocuments(String uniqueCode) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Fetching all documents for investor: {}", uniqueCode);

        List<KycDocuments> documents = kycDocumentsRepository.findByInvestorUniqueIdAndDeletedAtIsNull(uniqueCode);
        return documents.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get KYC documents only (uploadType = 1)
     */
    public List<DocumentResponseDto> getKycDocuments(String uniqueCode) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Fetching KYC documents for investor: {}", uniqueCode);

        List<KycDocuments> documents = kycDocumentsRepository.findByInvestorUniqueIdAndDeletedAtIsNull(uniqueCode);
        return documents.stream()
                .filter(doc -> doc.getUploadType() != null && doc.getUploadType() == 1)
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get Onboarding documents only (uploadType = 2)
     */
    public List<DocumentResponseDto> getOnboardingDocuments(String uniqueCode) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Fetching onboarding documents for investor: {}", uniqueCode);

        List<KycDocuments> documents = kycDocumentsRepository.findByInvestorUniqueIdAndDeletedAtIsNull(uniqueCode);
        return documents.stream()
                .filter(doc -> doc.getUploadType() != null && doc.getUploadType() == 2)
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get onboarding document requirements from Dataverse (similar to KYC but category = Onboarding)
     * uploadType = 2 for onboarding documents
     */
    public KycDocumentRequirementDto getOnboardingRequirements(String uniqueCode) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Fetching onboarding requirements for investor: {}", uniqueCode);

        // Get all uploaded onboarding documents (uploadType = 2)
        List<KycDocuments> uploadedDocs = kycDocumentsRepository.findByInvestorUniqueIdAndDeletedAtIsNull(uniqueCode)
                .stream()
                .filter(doc -> doc.getUploadType() != null && doc.getUploadType() == 2)
                .collect(Collectors.toList());

        // Load intro_investor_temp to get Dataverse GUIDs (same as KYC requirements)
        IntroInvestorTemp introInvestor = introInvestorTempRepository.findByUniqueCodeDb(uniqueCode).orElse(null);
        if (introInvestor == null) {
            String email = investor.getAuthorizedUser() != null ? investor.getAuthorizedUser().getEmailId() : null;
            if (email != null) {
                introInvestor = introInvestorTempRepository.findByIntroEmail(email).orElse(null);
            }
        }

        List<KycDocumentRequirementDto.RequiredDocument> requirements = new ArrayList<>();
        
        if (dynamicsCrmService == null) {
            log.warn("⚠️ Dataverse service not available, using local data only");
            requirements = buildRequirementsFromLocalOnly(uploadedDocs);
        } else if (introInvestor == null || introInvestor.getIntroInvestorId() == null) {
            log.warn("⚠️ intro_investor_temp missing or intro_investorid null - cannot filter Dataverse onboarding docs");
            requirements = buildRequirementsFromLocalOnly(uploadedDocs);
        } else {
            try {
                String ssInvestorGuid = introInvestor.getIntroInvestorId();
                String ssBrokerGuid = introInvestor.getSsBrokerValue();
                String ssInvestorTypeGuid = introInvestor.getSsInvestorTypeValue();
                String ssProductGuid = introInvestor.getSsProductValue();
                
                log.info("🌐 Calling Dataverse for ONBOARDING docs: investorGuid={}, broker={}, type={}, product={}", 
                        ssInvestorGuid, ssBrokerGuid, ssInvestorTypeGuid, ssProductGuid);
                
                List<Map<String, Object>> dynamicsRequirements = dynamicsCrmService.getInvestorOnboardingDocuments(
                        ssInvestorGuid, ssBrokerGuid, ssInvestorTypeGuid, ssProductGuid);
                
                log.info("✅ Fetched {} onboarding document requirements from Dataverse", dynamicsRequirements.size());
                requirements = buildRequirementsFromDynamics(dynamicsRequirements, uploadedDocs);
                
            } catch (Exception e) {
                log.error("❌ Failed to fetch onboarding requirements from Dataverse: {}", e.getMessage(), e);
                requirements = buildRequirementsFromLocalOnly(uploadedDocs);
            }
        }

        // Calculate summary stats
        long totalRequired = requirements.size();
        long uploaded = requirements.stream().filter(KycDocumentRequirementDto.RequiredDocument::getLocalRecordExists).count();
        long approved = requirements.stream().filter(doc -> "Approved".equalsIgnoreCase(doc.getLocalStatus())).count();
        long rejected = requirements.stream().filter(doc -> "Rejected".equalsIgnoreCase(doc.getLocalStatus()) || "Sent back".equalsIgnoreCase(doc.getLocalStatus())).count();
        long pending = uploaded - approved - rejected;
        double completionPercentage = totalRequired > 0 ? ((double) uploaded / totalRequired) * 100 : 0;

        String serviceProviderName = investor.getTenant() != null ? investor.getTenant().getTenantName() : "Service Provider";

        return KycDocumentRequirementDto.builder()
                .totalRequired((int) totalRequired)
                .uploaded((int) uploaded)
                .pending((int) pending)
                .approved((int) approved)
                .rejected((int) rejected)
                .completionPercentage(completionPercentage)
                .documents(requirements)
                .serviceProviderName(serviceProviderName)
                .build();
    }

    /**
     * Build requirements from local uploaded documents only (when Dataverse is unavailable).
     */
    private List<KycDocumentRequirementDto.RequiredDocument> buildRequirementsFromLocalOnly(List<KycDocuments> uploadedDocs) {
        List<KycDocumentRequirementDto.RequiredDocument> requirements = new ArrayList<>();
        
        for (KycDocuments doc : uploadedDocs) {
            KycDocumentRequirementDto.RequiredDocument req = KycDocumentRequirementDto.RequiredDocument.builder()
                    .dynamicsId(doc.getDocumentMasterId() != null ? doc.getDocumentMasterId() : String.valueOf(doc.getId()))
                    .description(doc.getDocumentType())
                    .documentTypeCode(doc.getDocumentMasterId())
                    .mandatory(true)
                    .localRecordExists(true)
                    .localStatus(doc.getStatus() != null ? doc.getStatus() : "Pending")
                    .reason(doc.getReason())
                    .localDocumentId(doc.getId())
                    .fileName(extractFileName(doc.getDocumentUrl()))
                    .documentUrl(doc.getDocumentUrl())
                    .uploadedAt(doc.getCreatedAt() != null ? doc.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                    .acceptedFormats(List.of("PDF", "JPG", "JPEG"))
                    .maxSize("10 MB")
                    .build();
            
            requirements.add(req);
        }
        
        return requirements;
    }

    public DocumentResponseDto getDocumentStatus(String uniqueCode, Long documentId) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Fetching document status for investor: {}, documentId: {}", uniqueCode, documentId);

        // TODO: Fetch from database
        // Document document = documentRepository.findByIdAndInvestorId(documentId,
        // investor.getId())
        // .orElseThrow(() -> new RuntimeException("Document not found"));

        // return mapToDto(document);

        return DocumentResponseDto.builder()
                .id(documentId)
                .status("pending")
                .build();
    }

    @Transactional
    public void deleteDocument(String uniqueCode, Long documentId) {
        getInvestorByUniqueCode(uniqueCode);

        KycDocuments doc = kycDocumentsRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
        if (!uniqueCode.equals(doc.getInvestorUniqueId())) {
            throw new RuntimeException("Document does not belong to this investor");
        }
        doc.setDeletedAt(LocalDateTime.now());
        kycDocumentsRepository.save(doc);
        log.info("Deleted (soft) document for investor: {}, documentId: {}", uniqueCode, documentId);
    }

    /**
     * Download document from SharePoint by URL.
     * Laravel equivalent: route('download.sharepoint.doc', ['url' => urlencode($onboardDocUrl)])
     * 
     * @param sharePointUrl Full SharePoint URL from ss_doc_master_url
     * @return File bytes
     */
    public byte[] downloadSharePointDocument(String sharePointUrl) {
        if (sharePointUrl == null || sharePointUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("SharePoint URL is required");
        }

        log.info("Downloading document from SharePoint: {}", sharePointUrl);

        if (sharePointService == null) {
            throw new RuntimeException("SharePoint service is not available. Cannot download document.");
        }

        try {
            return sharePointService.downloadFileByUrl(sharePointUrl.trim());
        } catch (Exception e) {
            log.error("Failed to download from SharePoint: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to download document from SharePoint: " + e.getMessage());
        }
    }

    private Investor getInvestorByUniqueCode(String uniqueCode) {
        return investorRepository.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new RuntimeException("Investor not found with unique code: " + uniqueCode));
    }

    /**
     * Map KycDocuments entity to DocumentResponseDto
     */
    private DocumentResponseDto mapToDto(KycDocuments doc) {
        String fileName = (doc.getDocDescription() != null && !doc.getDocDescription().isEmpty())
                ? doc.getDocDescription()
                : extractFileName(doc.getDocumentUrl());
        return DocumentResponseDto.builder()
                .id(doc.getId())
                .documentType(doc.getDocumentType())
                .documentCategory(doc.getUploadType() == 1 ? "kyc" : "onboarding")
                .fileName(fileName)
                .documentUrl(doc.getDocumentUrl())
                .status(doc.getStatus() != null ? doc.getStatus() : "pending")
                .uploadedAt(doc.getCreatedAt() != null ? doc.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .description(doc.getDocDescription())
                .rejectionReason(doc.getReason())
                .build();
    }


    /**
     * Get KYC document requirements with upload status.
     * Aligns with Laravel: fetches ss_investordocumentses from Dynamics,
     * then merges with local kyc_documents for uploaded files.
     */
    public KycDocumentRequirementDto getKycRequirements(String uniqueCode) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Fetching KYC requirements for investor: {}", uniqueCode);

        // Get existing uploaded documents from local DB
        List<KycDocuments> uploadedDocs = kycDocumentsRepository.findByInvestorUniqueIdAndDeletedAtIsNull(uniqueCode);
        
        // Load intro_investor_temp to get Dataverse GUIDs (broker, product, intro_investorid)
        // Mirror of Laravel document_submission_show:
        //   $fetch_broker_id = DB::table('intro_investor_temp')->where('intro_email', '=', $email_id)->first();
        //   $ss_investoridfordoc = $fetch_broker_id->intro_investorid;  ← Dataverse ss_investorid GUID
        IntroInvestorTemp introInvestor = introInvestorTempRepository.findByUniqueCodeDb(uniqueCode).orElse(null);
        if (introInvestor == null) {
            String email = investor.getAuthorizedUser() != null ? investor.getAuthorizedUser().getEmailId() : null;
            if (email != null) {
                introInvestor = introInvestorTempRepository.findByIntroEmail(email).orElse(null);
            }
        }
        
        List<KycDocumentRequirementDto.RequiredDocument> requirements;
        
        if (dynamicsCrmService == null) {
            log.warn("❌ DynamicsCrmService bean is NULL - cannot fetch from Dataverse");
            requirements = buildStandardKycRequirements(uploadedDocs);
        } else if (introInvestor == null || introInvestor.getIntroInvestorId() == null) {
            log.warn("❌ intro_investor_temp missing or intro_investorid null - cannot filter Dataverse documents (uniqueCode: {})", uniqueCode);
            requirements = buildStandardKycRequirements(uploadedDocs);
        } else {
            // Laravel document_submission_show (line 1201–1206):
            //   $filter = "_ss_investor_value eq {intro_investorid} and _ss_broker_value eq {broker} ..."
            String ssInvestorGuid = introInvestor.getIntroInvestorId();       // ← Dataverse ss_investorid GUID
            String ssBrokerGuid = introInvestor.getSsBrokerValue();
            String ssInvestorTypeGuid = introInvestor.getSsInvestorTypeValue();
            String ssProductGuid = introInvestor.getSsProductValue();
            
            log.info("📡 Calling Dataverse for KYC docs: investorGuid={}, broker={}, type={}, product={}", 
                    ssInvestorGuid, ssBrokerGuid, ssInvestorTypeGuid, ssProductGuid);
            
            List<Map<String, Object>> dynamicsRequirements = dynamicsCrmService.getInvestorDocumentRequirements(
                    ssInvestorGuid, ssBrokerGuid, ssInvestorTypeGuid, ssProductGuid);
            
            if (!dynamicsRequirements.isEmpty()) {
                log.info("✅ Fetched {} document requirements from Dataverse", dynamicsRequirements.size());
                requirements = buildRequirementsFromDynamics(dynamicsRequirements, uploadedDocs);
            } else {
                log.warn("⚠️ No Dynamics requirements found, falling back to standard list");
                requirements = buildStandardKycRequirements(uploadedDocs);
            }
        }
        
        // Calculate summary stats
        int totalRequired = requirements.size();
        long uploaded = requirements.stream().filter(KycDocumentRequirementDto.RequiredDocument::getLocalRecordExists).count();
        long approved = requirements.stream().filter(r -> "Approved".equals(r.getLocalStatus())).count();
        long rejected = requirements.stream().filter(r -> "Rejected".equals(r.getLocalStatus()) || "Sent Back".equals(r.getLocalStatus())).count();
        long pending = uploaded - approved - rejected;
        double completionPercentage = totalRequired > 0 ? (uploaded * 100.0 / totalRequired) : 0;

        return KycDocumentRequirementDto.builder()
                .totalRequired(totalRequired)
                .uploaded((int) uploaded)
                .approved((int) approved)
                .rejected((int) rejected)
                .pending((int) pending)
                .completionPercentage(completionPercentage)
                .serviceProviderName("Service Provider") // TODO: Fetch from tenant/investor config
                .documents(requirements)
                .build();
    }

    /**
     * Build requirements from Dynamics ss_investordocumentses records.
     * Aligns with Laravel: merging $allDocuments from Dynamics with uploaded local docs.
     */
    private List<KycDocumentRequirementDto.RequiredDocument> buildRequirementsFromDynamics(
            List<Map<String, Object>> dynamicsRequirements, List<KycDocuments> uploadedDocs) {
        
        List<KycDocumentRequirementDto.RequiredDocument> requirements = new ArrayList<>();
        
        for (Map<String, Object> dynDoc : dynamicsRequirements) {
            String dynamicsId = (String) dynDoc.get("ss_investordocumentsid");
            String description = (String) dynDoc.getOrDefault("ss_description", dynDoc.get("ss_name"));
            String docMasterId = (String) dynDoc.get("_ss_documentmaster_value");
            Integer documentType = (Integer) dynDoc.get("ss_documenttype");
            Boolean verifiedInDynamics = (Boolean) dynDoc.getOrDefault("ss_verification_done", false);
            String documentMasterUrl = (String) dynDoc.get("ss_doc_master_url"); // SharePoint URL for downloadable templates
            
            // Try to find matching local upload by description or document_master_id
            KycDocuments uploaded = uploadedDocs.stream()
                    .filter(doc -> {
                        if (description != null && description.equalsIgnoreCase(doc.getDocumentType())) {
                            return true;
                        }
                        if (dynamicsId != null && dynamicsId.equals(doc.getDocumentMasterId())) {
                            return true;
                        }
                        return false;
                    })
                    .findFirst()
                    .orElse(null);
            
            KycDocumentRequirementDto.RequiredDocument req = KycDocumentRequirementDto.RequiredDocument.builder()
                    .dynamicsId(dynamicsId)
                    .description(description != null ? description : "Unknown Document")
                    .documentTypeCode(docMasterId)
                    .documentType(documentType)
                    .mandatory(true) // Dynamics records typically represent mandatory docs
                    .localRecordExists(uploaded != null)
                    .localStatus(uploaded != null ? (uploaded.getStatus() != null ? uploaded.getStatus() : "Pending") : null)
                    .reason(uploaded != null ? uploaded.getReason() : null)
                    .localDocumentId(uploaded != null ? uploaded.getId() : null)
                    .fileName(uploaded != null ? extractFileName(uploaded.getDocumentUrl()) : null)
                    .documentUrl(uploaded != null ? uploaded.getDocumentUrl() : null)
                    .documentMasterUrl(documentMasterUrl) // SharePoint URL for downloadable templates
                    .uploadedAt(uploaded != null && uploaded.getCreatedAt() != null ? uploaded.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                    .inputId("actual-btn-" + dynamicsId)
                    .spanId("file-chosen-" + dynamicsId)
                    .errorId("error-message-" + dynamicsId)
                    .fileInputName("document_" + dynamicsId)
                    .acceptedFormats(List.of("PDF", "JPG", "JPEG"))
                    .maxSize("10 MB")
                    .build();
            
            requirements.add(req);
        }
        
        return requirements;
    }

    /**
     * Build standard KYC requirements with upload status
     * TODO: Replace with Dataverse fetch
     */
    private List<KycDocumentRequirementDto.RequiredDocument> buildStandardKycRequirements(List<KycDocuments> uploadedDocs) {
        // Standard KYC document types
        String[][] standardDocs = {
            {"PASSPORT", "Passport", "100000001", "true"},
            {"PAN_CARD", "PAN Card", "100000002", "true"},
            {"ADDRESS_PROOF", "Address Proof (Utility Bill/Bank Statement)", "100000003", "true"},
            {"PHOTO", "Passport Size Photograph", "100000004", "true"},
            {"BANK_STATEMENT", "Bank Statement (Last 6 months)", "100000005", "false"},
            {"INCOME_PROOF", "Income Proof (ITR/Salary Slips)", "100000006", "false"}
        };

        List<KycDocumentRequirementDto.RequiredDocument> requirements = new ArrayList<>();
        
        for (String[] docInfo : standardDocs) {
            String docType = docInfo[0];
            String description = docInfo[1];
            String typeCode = docInfo[2];
            Boolean mandatory = Boolean.parseBoolean(docInfo[3]);
            String dynamicsId = "DYN_" + typeCode;
            
            // Find uploaded document for this type
            KycDocuments uploaded = uploadedDocs.stream()
                    .filter(doc -> docType.equals(doc.getDocumentType()))
                    .findFirst()
                    .orElse(null);
            
            KycDocumentRequirementDto.RequiredDocument req = KycDocumentRequirementDto.RequiredDocument.builder()
                    .dynamicsId(dynamicsId)
                    .description(description)
                    .documentTypeCode(typeCode)
                    .mandatory(mandatory)
                    .localRecordExists(uploaded != null)
                    .localStatus(uploaded != null ? (uploaded.getStatus() != null ? uploaded.getStatus() : "Pending") : null)
                    .reason(uploaded != null ? uploaded.getReason() : null)
                    .localDocumentId(uploaded != null ? uploaded.getId() : null)
                    .fileName(uploaded != null ? extractFileName(uploaded.getDocumentUrl()) : null)
                    .documentUrl(uploaded != null ? uploaded.getDocumentUrl() : null)
                    .uploadedAt(uploaded != null && uploaded.getCreatedAt() != null ? uploaded.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                    .inputId("actual-btn-" + dynamicsId)
                    .spanId("file-chosen-" + dynamicsId)
                    .errorId("error-message-" + dynamicsId)
                    .fileInputName("document_" + dynamicsId)
                    .acceptedFormats(List.of("PDF", "JPG", "JPEG"))
                    .maxSize("10 MB")
                    .build();
            
            requirements.add(req);
        }
        
        return requirements;
    }

    /**
     * Extract file name from document URL
     */
    private String extractFileName(String documentUrl) {
        if (documentUrl == null || documentUrl.isEmpty()) {
            return "Unknown";
        }
        int lastSlash = documentUrl.lastIndexOf('/');
        return lastSlash >= 0 ? documentUrl.substring(lastSlash + 1) : documentUrl;
    }

    /**
     * Get KYC requirements specifically from Dataverse (for public token-based access).
     * Similar to getKycRequirements() but always fetches from Dataverse.
     * Used by PublicDocumentController for encrypted token-based submissions.
     */
    public KycDocumentRequirementDto getKycRequirementsFromDataverse(String uniqueCode) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Fetching KYC requirements from Dataverse for investor: {}", uniqueCode);

        if (dynamicsCrmService == null) {
            throw new IllegalStateException("Dataverse integration not configured");
        }

        // Load intro_investor_temp for GUIDs
        IntroInvestorTemp introInvestor = introInvestorTempRepository.findByUniqueCodeDb(uniqueCode).orElse(null);
        if (introInvestor == null) {
            String email = investor.getAuthorizedUser() != null ? investor.getAuthorizedUser().getEmailId() : null;
            if (email != null) {
                introInvestor = introInvestorTempRepository.findByIntroEmail(email).orElse(null);
            }
        }

        if (introInvestor == null || introInvestor.getIntroInvestorId() == null) {
            throw new IllegalStateException("intro_investor_temp record missing or intro_investorid null. Cannot fetch Dataverse documents.");
        }

        // Get existing uploaded documents from local DB
        List<KycDocuments> uploadedDocs = kycDocumentsRepository.findByInvestorUniqueIdAndDeletedAtIsNull(uniqueCode);
        
        // Fetch from Dataverse using intro_investor_temp GUIDs
        String ssInvestorGuid = introInvestor.getIntroInvestorId();
        String ssBrokerGuid = introInvestor.getSsBrokerValue();
        String ssInvestorTypeGuid = introInvestor.getSsInvestorTypeValue();
        String ssProductGuid = introInvestor.getSsProductValue();
        
        List<Map<String, Object>> dynamicsRequirements = dynamicsCrmService.getInvestorDocumentRequirements(
                ssInvestorGuid, ssBrokerGuid, ssInvestorTypeGuid, ssProductGuid);
        
        if (dynamicsRequirements.isEmpty()) {
            log.warn("No document requirements found in Dataverse for investor: {}", uniqueCode);
            throw new IllegalStateException("No document requirements found in Dataverse for this investor");
        }
        
        log.info("Fetched {} document requirements from Dataverse", dynamicsRequirements.size());
        List<KycDocumentRequirementDto.RequiredDocument> requirements = buildRequirementsFromDynamics(dynamicsRequirements, uploadedDocs);
        
        // Calculate summary stats
        int totalRequired = requirements.size();
        long uploaded = requirements.stream().filter(KycDocumentRequirementDto.RequiredDocument::getLocalRecordExists).count();
        long approved = requirements.stream().filter(r -> "Approved".equals(r.getLocalStatus())).count();
        long rejected = requirements.stream().filter(r -> "Rejected".equals(r.getLocalStatus()) || "Sent Back".equals(r.getLocalStatus())).count();
        long pending = uploaded - approved - rejected;
        double completionPercentage = totalRequired > 0 ? (uploaded * 100.0 / totalRequired) : 0;

        return KycDocumentRequirementDto.builder()
                .totalRequired(totalRequired)
                .uploaded((int) uploaded)
                .approved((int) approved)
                .rejected((int) rejected)
                .pending((int) pending)
                .completionPercentage(completionPercentage)
                .serviceProviderName("Service Provider") // TODO: Fetch from tenant/investor config
                .documents(requirements)
                .build();
    }

    /**
     * Upload KYC document and sync with Dataverse ss_investordocumentses.
     * Used by PublicDocumentController for token-based submissions.
     * After successful upload, updates the corresponding Dataverse record.
     */
    @Transactional
    public DocumentResponseDto uploadKycDocumentAndSyncDataverse(String uniqueCode, MultipartFile file, String documentType) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Uploading KYC document with Dataverse sync for investor: {}, type: {}", uniqueCode, documentType);

        // Step 1: Upload document (same as uploadKycDocument)
        DocumentResponseDto response = uploadKycDocument(uniqueCode, file, documentType);

        // Step 2: Sync with Dataverse if available
        IntroInvestorTemp introInvestor = introInvestorTempRepository.findByUniqueCodeDb(uniqueCode).orElse(null);
        if (introInvestor == null) {
            String email = investor.getAuthorizedUser() != null ? investor.getAuthorizedUser().getEmailId() : null;
            if (email != null) introInvestor = introInvestorTempRepository.findByIntroEmail(email).orElse(null);
        }

        if (dynamicsCrmService != null && introInvestor != null && introInvestor.getIntroInvestorId() != null) {
            try {
                String ssInvestorGuid = introInvestor.getIntroInvestorId();
                String ssBrokerGuid = introInvestor.getSsBrokerValue();
                String ssInvestorTypeGuid = introInvestor.getSsInvestorTypeValue();
                String ssProductGuid = introInvestor.getSsProductValue();

                List<Map<String, Object>> dynamicsRequirements = dynamicsCrmService.getInvestorDocumentRequirements(
                        ssInvestorGuid, ssBrokerGuid, ssInvestorTypeGuid, ssProductGuid);

                // Find matching Dynamics record by document name/description
                Optional<Map<String, Object>> matchingRecord = dynamicsRequirements.stream()
                        .filter(dynDoc -> {
                            String name = (String) dynDoc.get("ss_name");
                            return name != null && name.equalsIgnoreCase(documentType);
                        })
                        .findFirst();

                if (matchingRecord.isPresent()) {
                    String dynamicsId = (String) matchingRecord.get().get("ss_investordocumentsid");
                    log.info("Found matching Dynamics record: {}", dynamicsId);

                    // Update Dataverse record with file location and status
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("ss_filelocation", response.getDocumentUrl());
                    updateData.put("ss_verification_done", true);
                    updateData.put("ss_documentstatus", 100000000); // Submitted status code

                    dynamicsCrmService.updateInvestorDocumentRecord(dynamicsId, updateData);
                    log.info("Successfully synced document to Dataverse: {}", dynamicsId);

                    // Also update local record with dynamicsId for future reference
                    if (response.getId() != null) {
                        kycDocumentsRepository.findById(response.getId()).ifPresent(kycDoc -> {
                            kycDoc.setDocumentMasterId(dynamicsId);
                            kycDocumentsRepository.save(kycDoc);
                        });
                    }
                } else {
                    log.warn("No matching Dataverse record found for document type: {}", documentType);
                }

            } catch (Exception e) {
                log.error("Failed to sync to Dataverse (non-fatal): {}", e.getMessage(), e);
                // Non-fatal: local upload succeeded, Dataverse sync can be retried later
            }
        } else {
            log.warn("Dataverse not available or investor not synced, skipping Dataverse update");
        }

        return response;
    }
    
    /**
     * Save uploaded file to local filesystem when SharePoint is not available
     * 
     * @param file The uploaded file
     * @param uniqueCode Investor unique code
     * @param documentType Document type path (e.g., "Passport", "onboarding/PAN Card")
     * @param fileName Generated filename with timestamp
     * @return Relative file path for database storage
     * @throws IOException If file save fails
     */
    private String saveFileLocally(MultipartFile file, String uniqueCode, String documentType, String fileName) throws IOException {
        // Base upload directory (configurable via application.properties)
        String uploadBasePath = System.getProperty("facilon.upload.path", "uploads");
        
        // Create directory structure: uploads/{uniqueCode}/{documentType}/
        // documentType can be "Passport" or "onboarding/PAN Card"
        Path uploadDir = Paths.get(uploadBasePath, uniqueCode, documentType);
        Files.createDirectories(uploadDir);
        
        // Save file to disk
        Path filePath = uploadDir.resolve(fileName);
        Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        
        log.info("💾 File saved locally: {} (size: {} bytes)", filePath.toAbsolutePath(), file.getSize());
        
        // Return relative path for database (can be used for serving files later)
        return "uploads/" + uniqueCode + "/" + documentType + "/" + fileName;
    }

    /**
     * Sync verification status from Dataverse (aligns with Laravel updateVerificationStatus).
     * Fetches ss_verification_done from Dataverse ss_investors record and updates local user_personal_information.
     */
    private void updateVerificationStatus(String uniqueCode) {
        try {
            log.info("Syncing verification status from Dataverse for uniqueCode: {}", uniqueCode);
            
            // Load investor and personal info
            Investor investor = getInvestorByUniqueCode(uniqueCode);
            Optional<UserPersonalInformation> personalInfoOpt = userPersonalInformationRepository.findByInvestorUniqueId(uniqueCode);
            
            if (personalInfoOpt.isEmpty()) {
                log.warn("No user_personal_information found for uniqueCode: {}", uniqueCode);
                return;
            }
            
            UserPersonalInformation personalInfo = personalInfoOpt.get();
            
            // Get Dataverse GUID from investor.dv_investor_ss_id (NOT intro_investor_temp which may have display name like "INV-1744")
            if (investor == null || investor.getDvInvestorSsId() == null || investor.getDvInvestorSsId().isBlank()) {
                log.warn("investor.dv_investor_ss_id (GUID) missing for uniqueCode: {}", uniqueCode);
                return;
            }
            
            String dataverseInvestorGuid = investor.getDvInvestorSsId();
            
            // Fetch from Dataverse
            Map<String, Object> investorData = dynamicsCrmService.fetchInvestorByGuid(dataverseInvestorGuid);
            
            if (investorData == null) {
                log.warn("No investor data found in Dataverse for GUID: {}", dataverseInvestorGuid);
                return;
            }
            
            // Extract ss_verification_done (boolean)
            Boolean verificationDone = (Boolean) investorData.get("ss_verificationdone");
            
            // Map to string: true="1", false="2", null="0"
            String status;
            if (verificationDone == null) {
                status = "0";
            } else if (verificationDone) {
                status = "1";
            } else {
                status = "2";
            }
            
            // Update local DB
            personalInfo.setSsVerificationDone(status);
            userPersonalInformationRepository.save(personalInfo);
            
            log.info("✅ Verification status updated: investorGuid={}, ss_verification_done={}", dataverseInvestorGuid, status);
            
        } catch (Exception e) {
            log.error("❌ Failed to sync verification status: {}", e.getMessage());
            // Non-fatal: document upload already succeeded
        }
    }
}
