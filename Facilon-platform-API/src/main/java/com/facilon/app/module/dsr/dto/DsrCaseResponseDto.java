package com.facilon.app.module.dsr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Investor-facing view of a DSR case. Carries the mapped, plain-language
 * {@code investorStatus} - the raw internal status is never exposed here.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsrCaseResponseDto {

    private String caseId;
    private String investorUniqueCode;
    private String requestType;
    private String jurisdiction;
    private String dataArea;
    private String requestDescription;
    private String requesterName;
    private String requesterEmail;
    private String requesterPhone;
    private String requesterRole;
    /** Plain-language status shown to the investor (mapped from the internal status). */
    private String investorStatus;
    private boolean actionRequired;
    private String supportingFilePath;
    private String submittedAt;
    private String slaDeadline;
    private String resolvedAt;
    private String resolutionNotes;
    private String finalOutcome;
}
