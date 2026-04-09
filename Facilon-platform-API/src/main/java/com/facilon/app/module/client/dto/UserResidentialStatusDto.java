package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for residential status information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResidentialStatusDto {

    private String residentialStatus;
    // Laravel parity
    private String personOrigin;          // Person of Indian Origin (yes/no)
    private String proofOfAddress;
    private String aadharNumberOption;    // yes/no
    private String aadharNumber;
    private String userAadharNo;
    private String ociAvailable;          // yes/no
    private String dateOfOci;
    private String addressProofType;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private Integer state;
    private Integer country;
    private String postalCode;
    private Integer yearsAtCurrentAddress;
    private String addressProofDocumentId;
    private String userVisaNumber;
    private String userVisaIssuerDate;
    private String userVisaExpiryDate;
    private String userOciCardNo;
    private String userOciIssueDate;
    private String userOciValidUpto;
    private String userTypeOfProof;
    private String userVisaType;
    private String userVisaDateOfIssue;
    private String userVisaValidUpto;

    private String residenceType; // resident, non-resident, NRI, etc.
    private String residenceCountry;
    private String residenceAddress;
    private String residenceCity;
    private String residenceState;
    private String residencePostalCode;
    private String residencePhone;
    private String residenceSince; // Date or year
}
