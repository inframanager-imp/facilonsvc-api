package com.facilon.app.module.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPassportDetailsDto {

    private Long id;
    private String investorUniqueCode;
    private String investorId;
    private String passportNumber;
    private LocalDate passportIssueDate;
    private LocalDate passportExpiryDate;
    private String passportPlaceOfIssue;
    private String passportCountryOfIssue;
    private String passportFrontCopy;
    private String passportBackCopy;

    // Laravel additional fields
    private String passportNationality;
    private LocalDate passportDateNonResident;
    private Integer passportNoYearsAbroad;

    /** Proof-of-identity document type (e.g. Passport, PAN, Aadhaar). */
    private String documentType;
}
