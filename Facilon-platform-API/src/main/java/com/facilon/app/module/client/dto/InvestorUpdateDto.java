package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating investor (client) information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorUpdateDto {

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
    
    private Integer confirmation;
    private Integer termsRead;
}
