package com.facilon.app.module.serviceagent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DelegationDto {
    private Long id;
    private Long investorId;
    private String investorName;
    private String investorEmail;
    private String investorUniqueCode;
    private Long serviceAgentId;
    private String serviceAgentName;
    private String serviceAgentEmail;
    private String serviceAgentCode;
    private String scope;
    private Boolean canViewProfile;
    private Boolean canEditKyc;
    private Boolean canUploadDocuments;
    private Boolean canSubmitForms;
    private LocalDate validFrom;
    private LocalDate validTo;
    private Boolean isActive;
    private String status;
    private Long assignedBySpId;
    private String consentVersion;
    private String notes;
    private LocalDateTime consentGivenAt;
    private String consentIpAddress;
    private LocalDateTime revokedAt;
    private String revokedBy;
    private String revocationReason;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
