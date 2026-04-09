package com.facilon.app.module.client.dto.onboarding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Step 2: Individual Investor registration details.
 * Collected after email verification and OTP.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndividualRegistrationDto {

    @NotNull(message = "Market interest is required")
    private Boolean interestedInIndianMarket; // True if interested in appointing Broker/PM/Custodian in Indian Securities Market

    private Integer title; // Optional: Reference to master_title (Mr., Ms., Mrs., etc.)

    @NotBlank(message = "First name is required")
    private String firstName; // First name - should match identity documents

    private String middleName; // Optional middle name

    @NotBlank(message = "Last name is required")
    private String lastName; // Last name - should match identity documents

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Gender is required")
    private String gender; // Male, Female, Transgender

    @NotNull(message = "Nationality is required")
    private Integer nationality; // Reference to master nationality

    @NotNull(message = "Country of residence is required")
    private Integer countryOfResidence; // Reference to master country

    @NotNull(message = "PAN card status is required")
    private Boolean hasPanCard; // Do you have a PAN card?

    // Conditional fields based on nationality
    private String residencyType; // "resident_indian" or "non_resident_indian" (if Indian nationality)
    
    private Boolean isPersonOfIndianOrigin; // If foreign nationality
    
    private Boolean hasOciCard; // If isPersonOfIndianOrigin = true

    @NotNull(message = "Terms and conditions must be accepted")
    private Boolean termsAccepted; // I have read the Terms & Conditions and provide my consent
}
