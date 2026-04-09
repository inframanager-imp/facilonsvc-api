package com.facilon.app.module.serviceagent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DelegationCreateDto {
    
    @NotBlank(message = "Service agent code/email is required")
    private String serviceAgentCode;
    
    @NotNull(message = "Scope is required")
    private String scope;
    
    private LocalDate validFrom;
    private LocalDate validTo;
    
    @Builder.Default
    private Boolean canViewProfile = true;
    
    @Builder.Default
    private Boolean canEditKyc = false;
    
    @Builder.Default
    private Boolean canUploadDocuments = false;
    
    @Builder.Default
    private Boolean canSubmitForms = false;
    
    private String notes;
    
    @NotNull(message = "Consent must be given")
    private Boolean consentGiven;
    
    private String consentIpAddress;
    
    private String consentVersion;
}
