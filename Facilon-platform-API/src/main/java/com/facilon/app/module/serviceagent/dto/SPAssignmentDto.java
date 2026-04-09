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
public class SPAssignmentDto {
    
    @NotNull(message = "Investor ID is required")
    private Long investorId;
    
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
}
