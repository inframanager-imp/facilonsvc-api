package com.facilon.app.module.client.dto.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Prefill data for PMS step 2 (personal details).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsStep2PrefillDto {
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String mobile;
    private String registerAs;
}
