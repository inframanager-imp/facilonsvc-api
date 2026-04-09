package com.facilon.app.module.client.dto;

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
public class InvestorConsentsDto {

    private Long id;
    private String investorUniqueId;
    private String email;
    private LocalDate userDob;
    private Boolean informationCorrectConsent;
    private Boolean termsAccepted;
    private Boolean privacyPolicyAccepted;
    private Boolean legalCapacityConsent;
    private Boolean modificationAwarenessConsent;
    private Boolean marketingConsent;
    private Boolean dataSharingConsent;
    private LocalDateTime consentDate;
    private String ipAddress;
    private String userAgent;
    private String consentVersion;
}
