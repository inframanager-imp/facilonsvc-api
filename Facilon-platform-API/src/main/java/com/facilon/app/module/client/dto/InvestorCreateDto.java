package com.facilon.app.module.client.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new investor (client).
 * Links to an existing AuthorizedUser.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorCreateDto {

    @NotNull(message = "Authorized user ID is required")
    private Long authorizedUserId;
    
    @NotBlank(message = "Unique code is required")
    private String uniqueCode;
    
    @NotNull(message = "Register as is required (1=individual, 2=entity)")
    private Integer registerAs;
    
    private Integer market;
    private Integer nationality;
    private String residenceType;
    private String pancardStatus;
    private String indianOrigin;
    private String ociCardStatus;
    
    // For corporate/entity investors
    private String entityName;
    private Integer incorpCountry;
    private String entityNameRepresentative;
    private String companyCapacity;
    private String securityRegulated;
    private String registrationId;
    
    private Integer confirmation;
    private Integer termsRead;
    private String dvInvestorSsId;
}
