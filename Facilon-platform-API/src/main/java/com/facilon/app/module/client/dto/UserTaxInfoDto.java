package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTaxInfoDto {
    private Integer taxResidencyCountryId;
    private String panNumber;
    private String tinNumber;
    private String taxIdentificationNumberType;
    private String fatcaStatus;
    private String crsDeclaration;
    private Boolean usCitizen;
    private Boolean usResident;
    private String taxIdNumber;
    private String taxResidencyCountry;
    private String taxResidencyStatus;
    private String gstNumber;
    private String incomeSource;
    private String annualIncome;
    private String taxPanFirstName;
    private String taxPanFatherName;
    private String taxResidencyCertificateNo;
    private String taxResidencyCertificateDate;
}
