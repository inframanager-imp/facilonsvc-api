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
public class OtpVerificationDto {

    @NotBlank
    private String uniqueCode;
    @NotBlank
    private String emailOtp;
    @NotBlank
    private String smsOtp;
}
