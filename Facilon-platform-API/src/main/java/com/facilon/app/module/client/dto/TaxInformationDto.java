package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxInformationDto {

    private String panNumber; // Optional - only for Indian residents

    @NotNull(message = "Tax residency country is required")
    private Integer taxResidencyCountry;

    @NotBlank(message = "Tax Identification Number is required")
    private String tinNumber;

    @NotBlank(message = "Tax Identification Number type is required")
    private String taxIdentificationNumberType;

    @NotBlank(message = "FATCA status is required")
    private String fatcaStatus; // "yes" or "no"

    @NotBlank(message = "CRS declaration is required")
    private String crsDeclaration; // "yes" or "no"

    private String taxResidencyCertificateDocumentId; // Document ID from SharePoint

    private Boolean usCitizen;

    private Boolean usResident;

    private String additionalTaxInfo;
}
