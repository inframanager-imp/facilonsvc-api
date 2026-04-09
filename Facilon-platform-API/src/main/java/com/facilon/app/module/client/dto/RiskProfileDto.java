package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskProfileDto {

    @NotBlank(message = "Source of funds is required")
    private String sourceOfFunds;

    private String sourceOfFundsDetails;

    @NotBlank(message = "Educational qualification is required")
    private String educationalQualification;

    @NotBlank(message = "Gross income is required")
    private String grossIncome;

    @NotBlank(message = "Net worth is required")
    private String netWorth;

    @NotBlank(message = "Occupation is required")
    private String occupation;

    @NotNull(message = "Investment experience in is required")
    private String[] investmentExperienceIn; // Array of strings: "stocks", "mutual_funds", etc.

    // Legacy/Other fields
    private String riskProfile; // "conservative", "moderate", "aggressive"
    private String investmentObjective;
    private String investmentHorizon;
    private String riskTolerance;
    private Double expectedInvestmentAmount;
    private Boolean hasInvestedInMutualFunds;
    private Boolean hasInvestedInStocks;
    private Boolean hasInvestedInBonds;
    private Boolean hasInvestedInDerivatives;
    private Boolean hasInvestedInRealEstate;
    private String otherInvestments;
    private Integer yearsOfInvestmentExperience;
}
