package com.facilon.app.module.client.dto.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for OTP verification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerificationResponseDto {

    private Boolean success;
    private String message;
    private String uniqueCode;
    private Integer registerAs; // 1=Individual, 2=Legal Entity
}
