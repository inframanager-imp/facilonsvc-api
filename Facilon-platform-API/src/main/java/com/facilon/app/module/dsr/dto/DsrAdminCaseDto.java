package com.facilon.app.module.dsr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Privacy Ops / Admin view of a DSR case - exposes the internal status and
 * full workflow fields (never sent to investors).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsrAdminCaseDto {

    private String caseId;
    private String investorUniqueCode;
    private String requestType;
    private String jurisdiction;
    private String dataArea;
    private String spRelationFlag;
    private String requestDescription;
    private String requesterName;
    private String requesterEmail;
    private String requesterPhone;
    private String requesterRole;

    /** Internal workflow status. */
    private String status;
    /** Mapped plain-language label (for reference / display parity with the investor view). */
    private String investorStatus;
    private boolean actionRequired;
    private boolean slaOverdue;

    private String assignedTo;
    private String verificationStatus;
    private String verificationMethod;
    private String decision;
    private String finalOutcome;
    private String resolutionNotes;

    private boolean hasSupportingFile;
    private String evidenceFolderPath;

    private String submittedAt;
    private String slaDeadline;
    private String resolvedAt;
    private String updatedAt;

    /** Full timeline incl. internal-only events (admin detail only). */
    private List<DsrCaseEventDto> timeline;
}
