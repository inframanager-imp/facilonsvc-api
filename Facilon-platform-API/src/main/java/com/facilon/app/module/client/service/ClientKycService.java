package com.facilon.app.module.client.service;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.module.client.dto.KycDocumentsDto;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.model.KycDocuments;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.module.client.repository.KycDocumentsRepository;
import com.facilon.app.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClientKycService {

    private static final List<String> ALLOWED_TYPES = Arrays.asList("application/pdf", "image/jpeg", "image/jpg", "image/png");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final List<String> DOCUMENT_TYPES = List.of("PAN_CARD", "AADHAR_CARD", "PASSPORT", "BANK_STATEMENT", "ADDRESS_PROOF", "OCI_CARD", "VISA", "INVESTOR_INFO");

    private final KycDocumentsRepository kycRepository;
    private final InvestorRepository investorRepository;
    private final ObjectProvider<ClientDocumentService> documentServiceProvider;

    @Value("${investor.kyc.upload-dir:uploads/kyc}")
    private String uploadDir;

    public KycDocumentsDto uploadDocument(Long userId, MultipartFile file, String documentType, String description) {
        Investor investor = investorRepository.findByAuthorizedUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Investor not found for user"));

        validateFile(file, documentType);

        String documentUrl;
        ClientDocumentService docService = documentServiceProvider.getIfAvailable();

        if (docService != null) {
            documentUrl = docService.uploadFile(file, investor.getUniqueCode(), documentType);
        } else {
            Path dir = Paths.get(uploadDir);
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload directory", e);
            }
            String originalName = file.getOriginalFilename();
            String ext = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf('.')) : "";
            String storedName = UUID.randomUUID() + ext;
            Path target = dir.resolve(storedName);
            try {
                file.transferTo(target.toFile());
            } catch (IOException e) {
                throw new RuntimeException("Failed to save file", e);
            }
            documentUrl = "/kyc/" + storedName;
        }
        KycDocuments doc = KycDocuments.builder()
                .investorUniqueId(investor.getUniqueCode())
                .ssInvestorId(String.valueOf(investor.getId()))
                .status("Submitted")
                .documentUrl(documentUrl)
                .documentType(documentType)
                .docDescription(description != null ? description : documentType)
                .uploadType(1)
                .build();
        doc.setTenant(TenantContextHolder.getContext().getTenant());
        doc = kycRepository.save(doc);

        return toDto(doc);
    }

    public List<KycDocumentsDto> getDocuments(Long userId) {
        Investor investor = investorRepository.findByAuthorizedUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Investor not found"));
        return kycRepository.findByInvestorUniqueIdAndDeletedAtIsNull(investor.getUniqueCode())
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public void deleteDocument(Long userId, Long documentId) {
        KycDocuments doc = kycRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        Investor investor = investorRepository.findByAuthorizedUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Investor not found"));
        if (!doc.getInvestorUniqueId().equals(investor.getUniqueCode())) {
            throw new RuntimeException("Document does not belong to this investor");
        }
        doc.setDeletedAt(LocalDateTime.now());
        kycRepository.save(doc);
    }

    public byte[] downloadDocument(Long userId, Long documentId) throws IOException {
        KycDocuments doc = kycRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        Investor investor = investorRepository.findByAuthorizedUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Investor not found"));
        if (!doc.getInvestorUniqueId().equals(investor.getUniqueCode())) {
            throw new RuntimeException("Document does not belong to this investor");
        }
        String documentUrl = doc.getDocumentUrl();
        ClientDocumentService docService = documentServiceProvider.getIfAvailable();
        if (docService != null && ClientDocumentService.isSharePointUrl(documentUrl)) {
            return docService.downloadFile(documentUrl);
        }
        String path = documentUrl != null && documentUrl.startsWith("/kyc/")
                ? uploadDir + "/" + documentUrl.replace("/kyc/", "") : documentUrl;
        return Files.readAllBytes(Paths.get(path));
    }

    public List<String> getRequirements() {
        return DOCUMENT_TYPES;
    }

    /**
     * Admin: Get KYC documents for a client by investor ID (tenant-scoped).
     */
    public List<KycDocumentsDto> getDocumentsByClientId(Long clientId) {
        Investor investor = investorRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client/Investor not found: " + clientId));
        Long tenantId = TenantContextHolder.getContext().getTenant().getTenantId();
        if (!investor.getTenant().getTenantId().equals(tenantId)) {
            throw new RuntimeException("Client not found in current tenant");
        }
        return kycRepository.findByTenantIdAndInvestorUniqueId(tenantId, investor.getUniqueCode())
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Admin: Verify a KYC document.
     */
    public KycDocumentsDto verifyDocument(Long documentId) {
        KycDocuments doc = kycRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
        Long tenantId = TenantContextHolder.getContext().getTenant().getTenantId();
        if (doc.getTenant() == null || !doc.getTenant().getTenantId().equals(tenantId)) {
            throw new RuntimeException("Document not found in current tenant");
        }
        doc.setStatus("Approved");
        doc.setReason(null);
        doc = kycRepository.save(doc);
        log.info("Admin verified KYC document ID: {}", documentId);
        return toDto(doc);
    }

    /**
     * Admin: Reject a KYC document.
     */
    public KycDocumentsDto rejectDocument(Long documentId, String reason) {
        KycDocuments doc = kycRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
        Long tenantId = TenantContextHolder.getContext().getTenant().getTenantId();
        if (doc.getTenant() == null || !doc.getTenant().getTenantId().equals(tenantId)) {
            throw new RuntimeException("Document not found in current tenant");
        }
        doc.setStatus("Rejected");
        doc.setReason(reason != null ? reason : "Rejected by admin");
        doc = kycRepository.save(doc);
        log.info("Admin rejected KYC document ID: {}, reason: {}", documentId, reason);
        return toDto(doc);
    }

    /**
     * Admin: Download document by ID (tenant-scoped).
     */
    public byte[] downloadDocumentByAdmin(Long documentId) throws IOException {
        KycDocuments doc = kycRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
        Long tenantId = TenantContextHolder.getContext().getTenant().getTenantId();
        if (doc.getTenant() == null || !doc.getTenant().getTenantId().equals(tenantId)) {
            throw new RuntimeException("Document not found in current tenant");
        }
        String documentUrl = doc.getDocumentUrl();
        ClientDocumentService docService = documentServiceProvider.getIfAvailable();
        if (docService != null && documentUrl != null && ClientDocumentService.isSharePointUrl(documentUrl)) {
            return docService.downloadFile(documentUrl);
        }
        String path = documentUrl != null && documentUrl.startsWith("/kyc/")
                ? uploadDir + "/" + documentUrl.replace("/kyc/", "") : documentUrl;
        return Files.readAllBytes(Paths.get(path));
    }

    private void validateFile(MultipartFile file, String documentType) {
        if (file.isEmpty()) throw new RuntimeException("File is empty");
        if (file.getSize() > MAX_FILE_SIZE) throw new RuntimeException("File exceeds 10 MB");
        if (!ALLOWED_TYPES.contains(file.getContentType()) && !file.getContentType().startsWith("image/"))
            throw new RuntimeException("Invalid file type. Allowed: PDF, JPG, JPEG, PNG");
        if (!DOCUMENT_TYPES.contains(documentType)) throw new RuntimeException("Invalid document type");
    }

    private KycDocumentsDto toDto(KycDocuments d) {
        return KycDocumentsDto.builder()
                .id(d.getId())
                .investorUniqueId(d.getInvestorUniqueId())
                .ssInvestorId(d.getSsInvestorId())
                .status(d.getStatus())
                .documentUrl(d.getDocumentUrl())
                .documentType(d.getDocumentType())
                .docDescription(d.getDocDescription())
                .uploadType(d.getUploadType())
                .documentMasterId(d.getDocumentMasterId())
                .reason(d.getReason())
                .createdAt(d.getCreatedAt())
                .deletedAt(d.getDeletedAt())
                .build();
    }
}
