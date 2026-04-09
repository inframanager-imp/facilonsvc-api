package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsRegistrationDto {

    private Long id;

    @NotNull(message = "Investment amount is required")
    @Min(value = 1, message = "Investment amount must be positive")
    private BigDecimal investmentAmount;

    @NotBlank(message = "Portfolio type is required")
    private String portfolioType; // "conservative", "balanced", "aggressive", "custom"

    @NotBlank(message = "Risk profile is required")
    private String riskProfile; // "low", "medium", "high"

    private String investmentObjective; // "wealth_preservation", "income_generation", "capital_appreciation",
                                        // "balanced"

    private String investmentHorizon; // "short_term", "medium_term", "long_term"

    private String portfolioManagerId;

    private String pmsPlanId;

    private String pmsBankId;

    private String servicePreferences;

    private Boolean eligibilityConfirmed;

    private String registrationStatus; // "pending", "approved", "rejected", "active"

    private String submittedDate;

    private String approvedDate;

    private String rejectionReason;
}
