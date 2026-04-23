package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * One row of the per-investor-type requirements matrix (plan §2 + §3.4).
 * Returned by GET /api/clients/me/kyc/requirements.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycRequirementSlotDto {

    /** PAN_CARD | PASSPORT | AADHAR_CARD | OCI_CARD | ADDRESS_PROOF */
    private String documentType;

    /** MANDATORY | CONDITIONAL | OPTIONAL */
    private String requirement;

    /** NOT_UPLOADED | PENDING | VALID | DISCREPANCY | EXPIRED | EXPIRES_SOON | OCR_FAILED */
    private String state;

    /** Only set when state != NOT_UPLOADED */
    private Long currentDocumentId;
    private LocalDate expiryDate;
    private Integer daysUntilExpiry;

    /** Short human-readable label for the UI. */
    private String label;

    /** Explanatory note (e.g. "Required for Aadhaar e-sign"). */
    private String note;
}
