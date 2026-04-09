package com.facilon.app.module.client.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorExperienceDto {

    private Long id;
    private String investorUniqueId;
    
    private String educationalQualification;
    
    @JsonProperty("grossIncome")
    @JsonAlias("annualIncome")
    private String annualIncome;
    
    private String netWorth;
    private String investmentObjective;
    private String riskTolerance;
    private String riskProfile;
    
    @JsonProperty("investmentExperienceIn")
    private List<String> investmentExperienceIn;
    
    private String investmentHorizon;
    
    @JsonProperty("yearsOfInvestmentExperience")
    @JsonAlias("yearsOfExperience")
    private Integer yearsOfExperience;

    // Supports Laravel-style labels from frontend:
    // "Less than 2 years" | "2-5 year" | "More than 5 year"
    @JsonProperty("investmentExperienceYears")
    private String investmentExperienceYears;
    
    private String previousInvestments;
    private String occupation;
    private String employerName;
    private String designation;
    
    private String sourceOfFunds;
    private String sourceOfFundsDetails;
    private Integer expectedInvestmentAmount;
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
