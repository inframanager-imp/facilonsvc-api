package com.facilon.app.module.serviceagent.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorConsentDto {
    
    @NotNull(message = "Delegation ID is required")
    private Long delegationId;
    
    @NotNull(message = "Accept status is required")
    private Boolean accept;
    
    @NotNull(message = "Consent must be given")
    private Boolean consentGiven;
    
    private String scope;
    private Boolean canViewProfile;
    private Boolean canEditKyc;
    private Boolean canUploadDocuments;
    private Boolean canSubmitForms;
    
    private String consentIpAddress;
    private String consentVersion;
    private String rejectionReason;
}
