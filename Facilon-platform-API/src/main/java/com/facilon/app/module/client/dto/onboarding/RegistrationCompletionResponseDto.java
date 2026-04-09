package com.facilon.app.module.client.dto.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response after completing registration (Step 2 or Step 3).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationCompletionResponseDto {

    private Boolean success;
    private String message;
    private String uniqueCode;
    private Long investorId;
    private String email; // Login credentials will be sent to this email
    private Boolean requiresPanCard; // True if user needs PAN card before proceeding
}
