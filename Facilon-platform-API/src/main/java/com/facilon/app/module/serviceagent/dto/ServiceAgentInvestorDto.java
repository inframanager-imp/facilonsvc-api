package com.facilon.app.module.serviceagent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceAgentInvestorDto {
    private Long investorId;
    private String investorName;
    private String investorEmail;
    private String investorUniqueCode;
    private Long delegationId;
    private String delegationScope;
    private String scope;
    private Boolean canViewProfile;
    private Boolean canEditKyc;
    private Boolean canUploadDocuments;
    private Boolean canSubmitForms;
    private LocalDate validFrom;
    private LocalDate validTo;
    private Boolean delegationActive;
    private Boolean isActive;
}
