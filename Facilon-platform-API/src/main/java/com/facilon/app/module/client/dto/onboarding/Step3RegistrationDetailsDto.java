package com.facilon.app.module.client.dto.onboarding;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Step 3 DTO: Registration details (nationality, PAN/OCI, resident status).
 * This step comes AFTER OTP verification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Step3RegistrationDetailsDto {

    @NotNull(message = "Citizenship is required")
    private Integer citizenship; // Citizenship (from Dataverse - master_country)

    @NotNull(message = "Country of residence is required")
    private Integer countryOfResidence; // Country where investor currently resides

    @NotNull(message = "Nationality is required")
    private Integer nationality;

    @NotNull(message = "Residence type is required (resident/non-resident)")
    private String residenceType; // "resident" or "non-resident"

    private String pancardStatus; // "yes", "no", or null

    private String ociCardStatus; // "yes", "no", or null

    private String indianOrigin; // "yes", "no", or null

    @NotNull(message = "Confirmation is required")
    private Boolean confirmation; // User confirms information is correct

    @NotNull(message = "Privacy policy and terms must be accepted")
    private Boolean privacyPolicyAccepted; // User has read and accepted privacy policy and terms

    @NotNull(message = "Notification consent is required")
    private Boolean notificationConsent; // User agrees to receive notifications from Facilon

    private Boolean whatsappConsent; // Optional for legal entities - agree to receive WhatsApp notifications
}
