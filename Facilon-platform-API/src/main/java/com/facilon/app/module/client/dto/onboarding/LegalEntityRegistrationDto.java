package com.facilon.app.module.client.dto.onboarding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Step 3: Legal Entity registration details.
 * Collected after email verification and OTP.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalEntityRegistrationDto {

    @NotBlank(message = "Entity name is required")
    private String entityName; // Should be same as in incorporation/formation documents

    @NotNull(message = "Country of incorporation is required")
    private Integer countryOfIncorporation; // Reference to master_country

    private Boolean hasPanCard; // Required if countryOfIncorporation is India

    @NotBlank(message = "Entity representative name is required")
    private String entityRepresentativeName; // Name should be same as in identity document

    @NotBlank(message = "Representative capacity is required")
    private String representativeCapacity; // Director, Employee, POA Holder

    @NotNull(message = "Securities regulation status is required")
    private Boolean isSecuritiesRegulated; // Whether entity is regulated as Securities or Banking Company

    @NotNull(message = "Terms and conditions must be accepted")
    private Boolean termsAccepted; // I have read the Terms & Conditions and provide my consent
}
