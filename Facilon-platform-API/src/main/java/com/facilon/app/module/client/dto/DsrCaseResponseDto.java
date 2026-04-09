package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsrCaseResponseDto {

    private String caseId;
    private String investorUniqueCode;
    private String requestType;
    private String jurisdiction;
    private String requestDescription;
    private String requesterName;
    private String requesterEmail;
    private String requesterPhone;
    private String requesterRole;
    private String status;
    private String supportingFilePath;
    private String submittedAt;
    private String slaDeadline;
    private String resolvedAt;
    private String resolutionNotes;
}
