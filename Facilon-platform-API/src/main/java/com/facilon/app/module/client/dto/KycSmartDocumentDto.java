package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Rich KYC document view for Smart Upload:
 * includes metadata + OCR fields + discrepancies.
 * Plan: KYC_DOCUMENT_PLAN.md §3.4 (upload and detail responses).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycSmartDocumentDto {

    private Long id;
    private String investorUniqueId;
    private String documentType;
    private String status;
    private String validationStatus;
    private String documentUrl;
    private String documentNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String addressProofType;
    private Boolean usesAadhaarForAddress;
    private String ocrRunId;
    private String ocrModelVersion;
    private BigDecimal ocrConfidence;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private Long replacedByDocumentId;
    private List<KycDocumentFieldDto> fields;
    private List<KycDocumentDiscrepancyDto> discrepancies;

    /** Fields the confirm step did NOT import because profile already had different values. */
    private List<ProfileConflict> profileConflicts;

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ProfileConflict {
        private String targetField;
        private String existingValue;
        private String documentValue;
        private String reason;
    }
}
