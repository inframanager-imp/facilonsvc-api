package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Answer for the journey "Continue/play" gate:
 *  - kycComplete: are all mandatory KYC slots valid + confirmed (investor.kyc_verified_at)?
 *  - consentGiven: has this journey already been granted KYC-reuse consent?
 *
 * Plan: implemented_docs/kyc-blob-journey-consent-plan.md (Phase 3).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JourneyKycGateDto {
    private boolean kycComplete;
    private boolean consentGiven;
}
