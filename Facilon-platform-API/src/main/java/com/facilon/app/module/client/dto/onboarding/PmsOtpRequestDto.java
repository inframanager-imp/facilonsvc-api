package com.facilon.app.module.client.dto.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsOtpRequestDto {
    private String email;
    private String mobileNumber;
    /** Optional: used to personalize the email OTP template. */
    private String firstName;
}
