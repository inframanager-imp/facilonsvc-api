package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NextholderDto {

    private Long id;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Relationship is required")
    private String relationship; // "spouse", "child", "parent", "sibling", "other"

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Email(message = "Valid email is required")
    private String email;

    private String phoneNumber;

    private String identificationType; // "passport", "national_id", "drivers_license"

    private String identificationNumber;

    private String address;

    private String city;

    private String country;

    private String postalCode;

    private Boolean isMinor;

    private String guardianName; // Required if isMinor = true

    private String guardianRelationship;

    private Boolean consentGiven;

    private String consentDate;

    private String status; // "pending", "invited", "registered", "verified"
}
