package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for risk profile questionnaire.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRiskProfileDto {

    private String investmentHorizon; // Short-term, medium-term, long-term
    private String riskAppetite; // Conservative, Moderate, Aggressive
    private String investmentPurpose; // Retirement, Education, Wealth creation, etc.
    private String liquidityNeeds; // Immediate, Medium-term, Long-term
    private String sourceOfFunds;
    private String occupation;
    private String grossIncome;
    private String netWorth;
    private String educationalQualification;
    private java.util.List<String> investmentExperienceIn;
    private Integer ageGroup; // Age range
    private String marketKnowledge; // Beginner, Intermediate, Advanced, Expert
    private String lossComfortLevel; // Percentage or description
    private String previousLosses; // Yes/No description
    private Boolean understandsRisk; // Acknowledges market risks
    private String riskScore; // Computed risk score
    private Integer investmentExperienceYears;
    private String sourceOfWealth;
    private String lineOfBusiness;
    private String natureOfOrganisation;
    private Boolean polExposed;
    private Boolean polExposedRelated;
    private Boolean activity;
    private Boolean moneyChangeService;
    private Boolean gamblingService;
    private Boolean pawningService;
    private Boolean instanceViolation;
}
