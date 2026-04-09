package com.facilon.app.module.client.dto.onboarding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Step 1b: OTP verification with consent recording.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpConsentVerificationDto {

    @NotBlank(message = "Unique code is required")
    private String uniqueCode;

    @NotBlank(message = "OTP is required")
    private String emailOtp;

    @NotNull(message = "Consent is required")
    private Boolean consentGiven; // User has viewed and agreed to consent form
}
