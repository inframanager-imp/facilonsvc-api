package com.facilon.app.module.client.dto.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsOtpVerifyDto {
    private String email;
    private String mobileNumber;
    private String emailOtp;
    private String smsOtp;
}
