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
public class PassportInformationDto {

    @NotNull(message = "Passport nationality is required")
    private String passportNationality;

    @NotBlank(message = "Passport number is required")
    private String passportNumber;

    @NotNull(message = "Passport date of issue is required")
    private LocalDate passportDateOfIssue;

    @NotBlank(message = "Passport place of issue is required")
    private String passportPlaceOfIssue;

    @NotNull(message = "Passport expiry date is required")
    private LocalDate passportValidUpto;

    @NotNull(message = "Date of becoming non-resident is required")
    private LocalDate passportDateNonResident;

    @NotNull(message = "Number of years abroad is required")
    private Integer passportNoYearsAbroad;

    private String passportCopyDocumentId;
}
