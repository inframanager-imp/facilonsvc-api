package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Investor (Client) data transfer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorDto {

    private Long id;
    private Long authorizedUserId;
    private Long tenantId;
    
    // User information
    private String firstName;
    private String lastName;
    private String emailId;
    private String mobilePhone;
    
    // Investor specific fields
    private String uniqueCode;
    private Integer registerAs;
    private Integer market;
    private Integer nationality;
    private String residenceType;
    private String pancardStatus;
    private String indianOrigin;
    private String ociCardStatus;
    
    // Entity fields (for corporate investors)
    private String entityName;
    private Integer incorpCountry;
    private String entityNameRepresentative;
    private String companyCapacity;
    private String securityRegulated;
    private String registrationId;
    
    // Status fields
    private Integer confirmation;
    private Integer termsRead;
    private Integer verifyStatus;
    private String verifyStatusLabel;
    
    // External system integration
    private String dvInvestorSsId;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
