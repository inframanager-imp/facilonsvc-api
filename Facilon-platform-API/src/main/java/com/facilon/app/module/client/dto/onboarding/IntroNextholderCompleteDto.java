package com.facilon.app.module.client.dto.onboarding;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Step 4 of introduced investor nextholder flow (docs/Investor).
 * Password and consents to complete registration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntroNextholderCompleteDto {

    @NotBlank
    private String password;
    private Boolean agreeToTerms;
    private Boolean agreeToWhatsapp;
    private Boolean confirmation;
}
