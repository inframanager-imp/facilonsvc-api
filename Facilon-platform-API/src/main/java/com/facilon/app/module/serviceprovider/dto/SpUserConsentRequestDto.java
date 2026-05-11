package com.facilon.app.module.serviceprovider.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Step-2 → Step-3 transition. Mirrors form fields posted to Laravel
 * `BrokerController::service_provider_step3_submit`:
 * {@code unique_code}, {@code consent_version}, {@code effective_date}.
 *
 * <p>The React user-consent page submits this. On success the API inserts
 * `sp_consents_user`, generates the consent PDF, sends the consent confirmation
 * mail via Graph, and returns the encrypted token to use for the final user form.</p>
 */
@Data
public class SpUserConsentRequestDto {
    /** Laravel `Crypt::encrypt(email)` token forwarded from the landing page. */
    @NotBlank
    private String uniqueCode;

    @NotBlank
    private String consentVersion;

    @NotBlank
    private String effectiveDate;  // "yyyy-MM-dd" — kept as String to match Laravel's hidden input
}
