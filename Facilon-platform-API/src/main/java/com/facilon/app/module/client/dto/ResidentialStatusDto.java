package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidentialStatusDto {

    @NotBlank(message = "Residential status is required")
    private String residentialStatus;

    @NotBlank(message = "Address proof type is required")
    private String addressProofType;

    @NotBlank(message = "Address line 1 is required")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "City is required")
    private String city;

    @NotNull(message = "State is required")
    private Integer state;

    @NotNull(message = "Country is required")
    private Integer country;

    @NotBlank(message = "Postal code is required")
    private String postalCode;

    @NotNull(message = "Years at current address is required")
    private Integer yearsAtCurrentAddress;

    private String addressProofDocumentId;

    // Visa Details (Conditional)
    private String userVisaNumber;
    private LocalDate userVisaIssuerDate;
    private LocalDate userVisaExpiryDate;

    // OCI Details (Conditional)
    private String userOciCardNo;
    private LocalDate userOciIssueDate;
}
