package com.facilon.app.module.client.dto.onboarding;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntroInvestorStep4Dto {

    @NotBlank
    private String password;
    private Boolean agreeToTerms;
}
