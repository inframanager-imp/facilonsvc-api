package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalInformationDto {

    @NotBlank(message = "Name title is required")
    private String nameTitle;

    @NotBlank(message = "First name is required")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Gender is required")
    private String gender;

    @NotNull(message = "Marital status is required")
    private String maritalStatus;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotBlank(message = "City of birth is required")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "City of birth must contain only letters and spaces")
    private String cityOfBirth;

    @NotBlank(message = "Country of birth is required")
    private String countryOfBirth;

    @NotBlank(message = "PAN number is required")
    @Size(min = 10, max = 10, message = "PAN number must be exactly 10 characters")
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN number format")
    private String panNumber;

    // Father's Details
    @NotBlank(message = "Father's first name is required")
    @Pattern(regexp = "^[a-zA-Z\\s\\.\\'-]+$", message = "Father's first name must contain only letters and spaces")
    private String fathersFirstName;

    private String fathersMiddleName;

    @NotBlank(message = "Father's last name is required")
    @Pattern(regexp = "^[a-zA-Z\\s\\.\\'-]+$", message = "Father's last name must contain only letters and spaces")
    private String fathersLastName;

    // Mother's Details
    @NotBlank(message = "Mother's first name is required")
    @Pattern(regexp = "^[a-zA-Z\\s\\.\\'-]+$", message = "Mother's first name must contain only letters and spaces")
    private String motherFirstName;

    private String motherMiddleName;

    @NotBlank(message = "Mother's last name is required")
    @Pattern(regexp = "^[a-zA-Z\\s\\.\\'-]+$", message = "Mother's last name must contain only letters and spaces")
    private String motherLastName;

    // Maiden Name Details (Conditional validation in Validator)
    private String maidenTitle;
    private String maidenName;
    private String maidenMiddleName;
    private String maidenLastName;

    // Spouse Details (Conditional validation in Validator)
    private String spouseNameTitle;
    private String spouseName;
    private String spouseMaidenName;
    private String spouseMiddleName;
    private String spouseLastName;

    private String citizenship;
    private String countryOfResidence;

    private String fatherNameTitle;
    private String motherNameTitle;

    // Legacy fields or internal flags
    private Integer personalInfo;
    private String personalInfoTab;
    private Integer formSubmission;
}
