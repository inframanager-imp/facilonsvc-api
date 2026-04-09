package com.facilon.app.module.serviceagent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceAgentDto {
    private Long id;
    private Long authorizedUserId;
    private String agentCode;
    private String agentType;
    private String fullName;
    private String email;
    private String mobile;
    private String assignedRegion;
    private String assignedSegment;
    private Boolean isActive;
    private String onboardingStatus;
    private Long serviceProviderId;
    private String photoUrl;
    private String panNumber;
    private String addressProofUrl;
    private String registrationNumber;
    private LocalDateTime onboardedAt;
    private String onboardedBy;
    private Long totalInvestors;
    private Long activeDelegations;
}
