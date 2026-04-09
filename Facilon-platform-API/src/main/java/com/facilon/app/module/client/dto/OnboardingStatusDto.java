package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Onboarding progress status for investor dashboard.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingStatusDto {

    private int totalSteps;
    private int completedSteps;
    private int percentageComplete;

    private boolean personalInfoComplete;
    private boolean passportComplete;
    private boolean experienceComplete;
    private boolean consentsComplete;
    private boolean kycDocumentsComplete;

    private int kycDocumentsUploaded;
    private int kycDocumentsRequired;

    /** Suggested next actions for the investor */
    private List<String> nextSteps;
}
