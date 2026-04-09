package com.facilon.app.module.client.dto.onboarding;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Step 3 of introduced investor nextholder flow (docs/Investor).
 * OTP verification (email 4-digit + mobile 4-digit).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntroNextholderVerifyOtpDto {

    @NotBlank
    private String uniqueCode;
    @NotBlank
    private String emailOtp;  // 4 digits
    @NotBlank
    private String smsOtp;    // 4 digits
}
