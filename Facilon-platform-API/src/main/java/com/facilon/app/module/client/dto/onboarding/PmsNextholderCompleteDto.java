package com.facilon.app.module.client.dto.onboarding;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Step 4 of PMS investor nextholder flow (docs/Investor).
 * Password and consents (WhatsApp, marketing, privacy, terms).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsNextholderCompleteDto {

    @NotBlank
    private String password;
    private Boolean agreeToWhatsapp;
    private Boolean agreeToMarketing;
    private Boolean agreePrivacy;
    private Boolean agreeTerms;
}
