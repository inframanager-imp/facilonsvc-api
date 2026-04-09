package com.facilon.app.module.client.dto.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for email registration step.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRegistrationResponseDto {

    private Boolean success;
    private String message;
    private String uniqueCode; // Generated unique code for this registration session
    private Boolean emailAlreadyExists; // True if email is already registered
    private Boolean otpSent; // True if OTP was sent successfully
    private Integer expiresInMinutes; // OTP expiry time
}
