package com.facilon.app.module.client.service;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.integration.ocr.OcrClient;
import com.facilon.app.integration.ocr.OcrExtractionResult;
import com.facilon.app.integration.ocr.OcrField;
import com.facilon.app.module.client.dto.KycDocumentDiscrepancyDto;
import com.facilon.app.module.client.dto.KycDocumentFieldDto;
import com.facilon.app.module.client.dto.KycSmartDocumentDto;
import com.facilon.app.module.client.model.*;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.module.client.repository.KycDocumentDiscrepancyRepository;
import com.facilon.app.module.client.repository.KycDocumentFieldRepository;
import com.facilon.app.module.client.repository.KycDocumentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Orchestrator for Smart Upload (KYC_DOCUMENT_PLAN §3.3):
 *   accept file -> persist -> OcrClient -> KycValidationEngine -> rich response.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KycSmartUploadService {

    private static final List<String> ALLOWED_TYPES =
            Arrays.asList("application/pdf", "image/jpeg", "image/jpg", "image/png");
    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024;
    private static final Set<String> DOC_TYPES = Set.of(
            "PAN_CARD", "PASSPORT", "AADHAR_CARD", "OCI_CARD", "ADDRESS_PROOF");
    private static final Set<String> ADDRESS_PROOF_TYPES = Set.of(
            "Utility Bill", "Rent Agreement", "Bank Statement",
            "Driving License", "Passport", "Aadhaar", "Other");

    private final KycDocumentsRepository kycRepository;
    private final KycDocumentFieldRepository fieldRepository;
    private final KycDocumentDiscrepancyRepository discrepancyRepository;
    private final InvestorRepository investorRepository;
    private final ObjectProvider<ClientDocumentService> documentServiceProvider;
    private final OcrClient ocrClient;
    private final KycValidationEngine validationEngine;
    private final KycProfileImportService profileImportService;
    private final KycCompletenessService completenessService;

    @Value("${investor.kyc.upload-dir:uploads/kyc}")
    private String uploadDir;

    /** How long a utility bill / bank statement remains valid as address proof. */
    @Value("${kyc.address-proof.recent-doc-validity-days:90}")
    private int recentDocValidityDays;

    /** Default validity window for a rent agreement when OCR does not extract its end date. */
    @Value("${kyc.address-proof.rent-agreement-validity-days:335}")
    private int rentAgreementValidityDays;

    // ---------- upload ----------

    public KycSmartDocumentDto upload(Long userId,
                                      MultipartFile file,
                                      String documentType,
                                      String addressProofType,
                                      Boolean usesAadhaarForAddress,
                                      Long supersedesId) {
        Investor investor = investorRepository.findByAuthorizedUser_Id(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Investor not found"));

        validateInputs(file, documentType, addressProofType);

        String documentUrl = storeFile(file, investor.getUniqueCode(), documentType);

        // Persist the row first so OCR failures don't lose the file.
        KycDocuments doc = KycDocuments.builder()
                .investorUniqueId(investor.getUniqueCode())
                .ssInvestorId(String.valueOf(investor.getId()))
                .status("Submitted")
                .documentUrl(documentUrl)
                .documentType(documentType)
                .docDescription(documentType)
                .uploadType(1)
                .addressProofType(addressProofType)
                .usesAadhaarForAddress(usesAadhaarForAddress)
                .validationStatus(KycValidationStatus.PENDING.name())
                .build();
        doc.setTenant(TenantContextHolder.getContext().getTenant());
        doc = kycRepository.save(doc);

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read uploaded file", e);
        }

        // ORCReader call - plan §3.1 / §3.5
        OcrExtractionResult ocr = ocrClient.extract(bytes, file.getContentType(),
                file.getOriginalFilename(), documentType);

        LocalDate issueDate = null;
        LocalDate expiryDate = null;
        String documentNumber = null;
        BigDecimal avgConfidence = null;

        if (ocr.isSuccess()) {
            // Persist extracted fields
            List<KycDocumentField> persistedFields = new ArrayList<>();
            double confidenceSum = 0.0;
            int confidenceCount = 0;
            for (OcrField f : ocr.fields()) {
                KycDocumentField kf = KycDocumentField.builder()
                        .kycDocumentId(doc.getId())
                        .fieldName(f.name())
                        .fieldValue(f.value())
                        .confidence(BigDecimal.valueOf(f.confidence()).setScale(2, RoundingMode.HALF_UP))
                        .page(f.page())
                        .build();
                persistedFields.add(fieldRepository.save(kf));
                confidenceSum += f.confidence();
                confidenceCount++;
            }
            if (confidenceCount > 0) {
                avgConfidence = BigDecimal.valueOf(confidenceSum / confidenceCount)
                        .setScale(2, RoundingMode.HALF_UP);
            }

            persistSplitAddressFields(doc.getId(), persistedFields);

            issueDate = parseDate(findField(persistedFields,
                    "issue_date", "date_of_issue", "bill_date", "statement_date",
                    "issued_on", "invoice_date", "document_date"));
            expiryDate = parseDate(findField(persistedFields, "expiry_date",
                    "date_of_expiry", "valid_until", "valid_to"));
            documentNumber = firstNonBlank(
                    findField(persistedFields, "pan_number", "passport_number",
                            "aadhaar_number", "oci_number", "document_number"));

            // For address proofs whose own metadata has no expiry (utility bills,
            // bank statements, rent agreements), derive an effective expiry from
            // the issue date so the 3-month validity rule applies.
            LocalDate derivedExpiry = deriveExpiry(documentType, addressProofType,
                    issueDate, expiryDate);
            if (derivedExpiry != null) {
                expiryDate = derivedExpiry;
            }

            doc.setOcrRunId(ocr.runId());
            doc.setOcrModelVersion(ocr.modelVersion());
            doc.setOcrConfidence(avgConfidence);
            doc.setDocumentNumber(documentNumber);
            doc.setIssueDate(issueDate);
            doc.setExpiryDate(expiryDate);
        } else {
            log.warn("ORCReader failed for doc {} ({}): {}", doc.getId(), documentType, ocr.error());
        }

        // Validation - plan §3.6
        List<KycDocumentDiscrepancy> issues =
                validationEngine.validate(investor, documentType, addressProofType, ocr, expiryDate);

        // Surface OCR failure as a non-blocking discrepancy so the UI can show the reason
        // (otherwise a re-upload that also fails OCR looks identical to the first failure).
        if (ocr == null || !ocr.isSuccess()) {
            issues.add(KycDocumentDiscrepancy.builder()
                    .canonicalSource(KycValidationEngine.SRC_OCR_INFRA)
                    .fieldName("ocr_extraction")
                    .expectedValue("success")
                    .observedValue(ocr == null ? "no response" : firstNonBlank(ocr.error(), "unknown error"))
                    .severity(KycValidationEngine.SEV_WARNING)
                    .build());
        }
        for (KycDocumentDiscrepancy d : issues) {
            d.setKycDocumentId(doc.getId());
            discrepancyRepository.save(d);
        }

        String status = computeStatus(ocr, issues, expiryDate);
        doc.setValidationStatus(status);

        // Rule 1/10/11 BLOCKING - reject upload
        if (validationEngine.hasBlocking(issues)) {
            kycRepository.save(doc);
            KycSmartDocumentDto body = toDto(doc);
            throw new BlockingDiscrepancyException("Upload failed validation", body);
        }

        // Re-upload chain (§3.2 version chain + §3.4 re-upload endpoint)
        final Long newDocId = doc.getId();
        final String investorCode = investor.getUniqueCode();
        if (supersedesId != null) {
            kycRepository.findByIdAndDeletedAtIsNull(supersedesId).ifPresent(prev -> {
                if (prev.getInvestorUniqueId().equals(investorCode)) {
                    prev.setReplacedByDocumentId(newDocId);
                    kycRepository.save(prev);
                }
            });
        } else {
            kycRepository.findCurrentByInvestorUniqueIdAndDocumentType(investorCode, documentType)
                    .ifPresent(prev -> {
                        if (!Objects.equals(prev.getId(), newDocId)) {
                            prev.setReplacedByDocumentId(newDocId);
                            kycRepository.save(prev);
                        }
                    });
        }

        doc = kycRepository.save(doc);
        return toDto(doc);
    }

    // ---------- listing ----------

    @Transactional(readOnly = true)
    public List<KycSmartDocumentDto> list(Long userId) {
        Investor investor = requireInvestor(userId);
        return kycRepository.findCurrentByInvestorUniqueId(investor.getUniqueCode())
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public KycSmartDocumentDto detail(Long userId, Long documentId) {
        Investor investor = requireInvestor(userId);
        KycDocuments doc = kycRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
        if (!doc.getInvestorUniqueId().equals(investor.getUniqueCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your document");
        }
        return toDto(doc);
    }

    @Transactional(readOnly = true)
    public List<KycSmartDocumentDto> history(Long userId, Long documentId) {
        Investor investor = requireInvestor(userId);
        KycDocuments anchor = kycRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
        if (!anchor.getInvestorUniqueId().equals(investor.getUniqueCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your document");
        }
        return kycRepository.findHistoryByInvestorUniqueIdAndDocumentType(
                        investor.getUniqueCode(), anchor.getDocumentType())
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public void delete(Long userId, Long documentId) {
        Investor investor = requireInvestor(userId);
        KycDocuments doc = kycRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
        if (!doc.getInvestorUniqueId().equals(investor.getUniqueCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your document");
        }
        if ("Approved".equalsIgnoreCase(doc.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete an approved document");
        }
        doc.setDeletedAt(LocalDateTime.now());
        kycRepository.save(doc);
    }

    /**
     * Investor clicked "Looks right" in the review modal. Marks the document
     * confirmed ATOMICALLY (so a double-click doesn't import twice), imports
     * undisputed OCR fields into the profile with audit trail, and re-evaluates
     * the KYC-complete gate. Plan §3.8 "investor confirms before final commit".
     */
    public KycSmartDocumentDto confirm(Long userId, Long documentId) {
        Investor investor = requireInvestor(userId);
        KycDocuments doc = kycRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
        if (!doc.getInvestorUniqueId().equals(investor.getUniqueCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your document");
        }
        if (!KycValidationStatus.VALID.name().equals(doc.getValidationStatus())
                && !KycValidationStatus.DISCREPANCY.name().equals(doc.getValidationStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Only VALID or DISCREPANCY documents can be confirmed; current=" + doc.getValidationStatus());
        }

        // Atomic "first confirm wins". 0 rows updated means another request already
        // confirmed - we skip the import but still return the current state.
        LocalDateTime now = LocalDateTime.now();
        int updated = kycRepository.markConfirmedIfUnconfirmed(documentId, now);
        List<KycProfileImportService.ImportConflict> conflicts = List.of();
        if (updated > 0) {
            doc.setConfirmedAt(now);
            KycProfileImportService.ImportResult result = profileImportService.importFromDocument(doc);
            conflicts = result.conflicts();
            log.info("Confirm imported {} field(s), {} conflict(s) skipped for doc {}",
                    result.imported(), conflicts.size(), documentId);
        } else {
            doc = kycRepository.findByIdAndDeletedAtIsNull(documentId).orElse(doc);
        }

        completenessService.reEvaluate(investor);
        KycSmartDocumentDto dto = toDto(doc);
        if (!conflicts.isEmpty()) {
            dto.setProfileConflicts(conflicts.stream()
                    .map(c -> KycSmartDocumentDto.ProfileConflict.builder()
                            .targetField(c.targetField())
                            .existingValue(c.existingValue())
                            .documentValue(c.documentValue())
                            .reason(c.reason())
                            .build())
                    .toList());
        }
        return dto;
    }

    /**
     * Investor clicked "Something's wrong" in the review modal. Soft-deletes
     * the document so the slot falls back to NOT_UPLOADED / prior version
     * and the user can re-upload without a stale VALID doc blocking them.
     * Also clears the confirmed chain from any superseded parent.
     */
    public void reject(Long userId, Long documentId) {
        Investor investor = requireInvestor(userId);
        KycDocuments doc = kycRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
        if (!doc.getInvestorUniqueId().equals(investor.getUniqueCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your document");
        }
        if ("Approved".equalsIgnoreCase(doc.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot reject an approved document");
        }
        doc.setDeletedAt(LocalDateTime.now());
        kycRepository.save(doc);
        // Restore the prior version of this slot (if any) so the slot is not
        // left in NOT_UPLOADED when a previous valid version existed.
        kycRepository.findHistoryByInvestorUniqueIdAndDocumentType(
                        investor.getUniqueCode(), doc.getDocumentType())
                .stream()
                .filter(k -> k.getDeletedAt() == null
                        && k.getReplacedByDocumentId() != null
                        && k.getReplacedByDocumentId().equals(doc.getId()))
                .findFirst()
                .ifPresent(prev -> {
                    prev.setReplacedByDocumentId(null);
                    kycRepository.save(prev);
                });
        completenessService.reEvaluate(investor);
    }

    // ---------- helpers ----------

    private Investor requireInvestor(Long userId) {
        return investorRepository.findByAuthorizedUser_Id(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Investor not found"));
    }

    private void validateInputs(MultipartFile file, String documentType, String addressProofType) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "File exceeds 10 MB");
        }
        String contentType = file.getContentType() == null ? "" : file.getContentType();
        if (!ALLOWED_TYPES.contains(contentType) && !contentType.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Allowed types: PDF, JPG, JPEG, PNG");
        }
        if (!DOC_TYPES.contains(documentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown documentType " + documentType);
        }
        if ("ADDRESS_PROOF".equals(documentType)) {
            if (addressProofType == null || !ADDRESS_PROOF_TYPES.contains(addressProofType)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "addressProofType required for ADDRESS_PROOF, one of " + ADDRESS_PROOF_TYPES);
            }
        }
    }

    private String storeFile(MultipartFile file, String investorCode, String documentType) {
        ClientDocumentService docService = documentServiceProvider.getIfAvailable();
        if (docService != null) {
            return docService.uploadFile(file, investorCode, documentType);
        }
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
        return "/kyc/" + storedName;
    }

    private String computeStatus(OcrExtractionResult ocr,
                                 List<KycDocumentDiscrepancy> issues,
                                 LocalDate expiry) {
        if (ocr == null || !ocr.isSuccess()) {
            return KycValidationStatus.OCR_FAILED.name();
        }
        if (expiry != null && expiry.isBefore(LocalDate.now())) {
            return KycValidationStatus.EXPIRED.name();
        }
        if (issues.stream().anyMatch(d ->
                KycValidationEngine.SEV_BLOCKING.equals(d.getSeverity())
                        || KycValidationEngine.SEV_WARNING.equals(d.getSeverity()))) {
            return KycValidationStatus.DISCREPANCY.name();
        }
        return KycValidationStatus.VALID.name();
    }

    /**
     * If OCR returned a single joined address string (Aadhaar letter, utility
     * bill, passport bio-page, etc.), split it via {@link AddressParser} and
     * persist the split pieces as separate {@code kyc_document_field} rows so
     * the review modal shows each piece and the profile import can apply them
     * column-by-column. Skips any piece whose field name is already present
     * (ORCReader emitted it natively) to avoid duplicates.
     */
    private void persistSplitAddressFields(Long docId, List<KycDocumentField> persistedFields) {
        String raw = findField(persistedFields, "permanent_address", "address", "present_address");
        if (raw == null || raw.isBlank()) return;

        AddressParser.ParsedAddress p = AddressParser.parse(raw);
        if (p.isEmpty()) return;

        Set<String> existing = new HashSet<>();
        for (KycDocumentField f : persistedFields) {
            if (f.getFieldName() != null) existing.add(f.getFieldName().toLowerCase(Locale.ROOT));
        }

        addSplitField(docId, existing, persistedFields, "address_line1", p.addressLine1());
        addSplitField(docId, existing, persistedFields, "address_line2", p.addressLine2());
        addSplitField(docId, existing, persistedFields, "address_line3", p.addressLine3());
        addSplitField(docId, existing, persistedFields, "city", p.city());
        addSplitField(docId, existing, persistedFields, "state", p.state());
        addSplitField(docId, existing, persistedFields, "zip_code", p.zipCode());
        addSplitField(docId, existing, persistedFields, "country", p.country());
    }

    private void addSplitField(Long docId, Set<String> existing,
                               List<KycDocumentField> sink, String name, String value) {
        if (value == null || value.isBlank()) return;
        if (existing.contains(name)) return;
        KycDocumentField kf = KycDocumentField.builder()
                .kycDocumentId(docId)
                .fieldName(name)
                .fieldValue(value)
                .confidence(BigDecimal.valueOf(0.90).setScale(2, RoundingMode.HALF_UP))
                .page(1)
                .build();
        sink.add(fieldRepository.save(kf));
        existing.add(name);
    }

    private String findField(List<KycDocumentField> fields, String... names) {
        Set<String> keys = new HashSet<>();
        for (String n : names) keys.add(n.toLowerCase(Locale.ROOT));
        for (KycDocumentField f : fields) {
            if (f.getFieldName() != null
                    && keys.contains(f.getFieldName().toLowerCase(Locale.ROOT))) {
                return f.getFieldValue();
            }
        }
        return null;
    }

    /**
     * Compute an effective "valid until" date for documents that have no native
     * expiry, per address-proof business rules:
     *   - Utility Bill / Bank Statement : issue_date + {@code recentDocValidityDays} (default 90)
     *   - Rent Agreement                : ocr expiry if present, else issue_date + {@code rentAgreementValidityDays}
     *   - Passport / Aadhaar / Driving License / OCI (as address proof) : keep OCR expiry
     * Returns null when no derivation is needed (the OCR expiry is fine as-is).
     */
    private LocalDate deriveExpiry(String docType, String subtype,
                                   LocalDate issueDate, LocalDate ocrExpiry) {
        if (!"ADDRESS_PROOF".equals(docType)) return null;
        if (subtype == null) return null;
        String s = subtype.trim().toLowerCase(Locale.ROOT);
        return switch (s) {
            case "utility bill", "bank statement" ->
                    issueDate == null ? null : issueDate.plusDays(recentDocValidityDays);
            case "rent agreement" ->
                    ocrExpiry != null ? ocrExpiry
                            : (issueDate == null ? null : issueDate.plusDays(rentAgreementValidityDays));
            // passport / aadhaar / driving license / other -> OCR expiry already covers it
            default -> null;
        };
    }

    private LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String trimmed = raw.trim();
        String[] patterns = {"yyyy-MM-dd", "dd/MM/yyyy", "dd-MM-yyyy", "dd MMM yyyy", "MM/dd/yyyy"};
        for (String p : patterns) {
            try {
                return LocalDate.parse(trimmed, java.time.format.DateTimeFormatter.ofPattern(p));
            } catch (Exception ignored) { }
        }
        return null;
    }

    private String firstNonBlank(String... vals) {
        for (String v : vals) if (v != null && !v.isBlank()) return v;
        return null;
    }

    // ---------- mapping ----------

    KycSmartDocumentDto toDto(KycDocuments d) {
        List<KycDocumentFieldDto> fieldDtos = fieldRepository.findByKycDocumentId(d.getId())
                .stream().map(f -> KycDocumentFieldDto.builder()
                        .fieldName(f.getFieldName())
                        .fieldValue(f.getFieldValue())
                        .confidence(f.getConfidence())
                        .page(f.getPage())
                        .build())
                .collect(Collectors.toList());
        List<KycDocumentDiscrepancyDto> discDtos = discrepancyRepository.findByKycDocumentId(d.getId())
                .stream().map(x -> KycDocumentDiscrepancyDto.builder()
                        .id(x.getId())
                        .canonicalSource(x.getCanonicalSource())
                        .fieldName(x.getFieldName())
                        .expectedValue(x.getExpectedValue())
                        .observedValue(x.getObservedValue())
                        .severity(x.getSeverity())
                        .build())
                .collect(Collectors.toList());
        return KycSmartDocumentDto.builder()
                .id(d.getId())
                .investorUniqueId(d.getInvestorUniqueId())
                .documentType(d.getDocumentType())
                .status(d.getStatus())
                .validationStatus(d.getValidationStatus())
                .documentUrl(d.getDocumentUrl())
                .documentNumber(d.getDocumentNumber())
                .issueDate(d.getIssueDate())
                .expiryDate(d.getExpiryDate())
                .addressProofType(d.getAddressProofType())
                .usesAadhaarForAddress(d.getUsesAadhaarForAddress())
                .ocrRunId(d.getOcrRunId())
                .ocrModelVersion(d.getOcrModelVersion())
                .ocrConfidence(d.getOcrConfidence())
                .createdAt(d.getCreatedAt())
                .confirmedAt(d.getConfirmedAt())
                .replacedByDocumentId(d.getReplacedByDocumentId())
                .fields(fieldDtos)
                .discrepancies(discDtos)
                .build();
    }

    /** Thrown when validation flags BLOCKING discrepancies - controller maps to 409. */
    public static class BlockingDiscrepancyException extends RuntimeException {
        private final KycSmartDocumentDto document;

        public BlockingDiscrepancyException(String message, KycSmartDocumentDto document) {
            super(message);
            this.document = document;
        }

        public KycSmartDocumentDto getDocument() {
            return document;
        }
    }
}
