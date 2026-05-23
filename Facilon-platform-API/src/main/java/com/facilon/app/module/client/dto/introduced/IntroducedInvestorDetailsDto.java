package com.facilon.app.module.client.dto.introduced;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntroducedInvestorDetailsDto {
    private String uniqueCode;
    private String dataverseInvestorId;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String mobile;
    private String brokerName;
    private String serviceProviderType;
    private String productName;
    private String planName;
    private String schemeName;
    private String nationalityName;
    private String countryOfResidenceName;
    private String investorTypeName;
    private Boolean emailAlreadyExists;
    private String isdCode;      // Dataverse country-of-residence ISD GUID
    private String countryCode;  // resolved dialing code, e.g. "+65"
}
