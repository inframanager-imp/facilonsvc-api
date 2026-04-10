package com.facilon.app.module.client.dto.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response envelope for PMS OTP send / verify operations.
 *
 * Gives the frontend enough context to show accurate feedback:
 * - {@code success}: whether the operation succeeded
 * - {@code code}: machine-readable reason code ("ok" | "mismatch" | "expired" | "locked" | "rate_limited" | "not_issued")
 * - {@code message}: human-readable message safe to display to the end user
 * - {@code emailSent} / {@code smsSent}: (send only) which channels actually dispatched
 * - {@code expiresInMinutes}: (send only) OTP TTL
 * - {@code remainingAttempts}: (verify only) remaining verify attempts before lockout
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsOtpResponseDto {
    private boolean success;
    private String code;
    private String message;
    private Boolean emailSent;
    private Boolean smsSent;
    private Integer expiresInMinutes;
    private Integer remainingAttempts;
}
