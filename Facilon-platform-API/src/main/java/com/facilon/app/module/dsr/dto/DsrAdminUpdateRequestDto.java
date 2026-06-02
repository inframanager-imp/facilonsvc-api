package com.facilon.app.module.dsr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin mutation payload. All fields optional - only non-null values are applied,
 * so the same DTO serves status change, assignment, verification and resolution.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsrAdminUpdateRequestDto {

    private String status;
    private String assignedTo;
    private String verificationStatus;
    private String verificationMethod;
    private String decision;
    private String finalOutcome;
    /** Investor-visible resolution summary. */
    private String resolutionNotes;
    /** Free-text note recorded on the timeline event for this change. */
    private String note;
    /** When true, the timeline event for this change is hidden from the investor. */
    private Boolean internalOnly;
}
