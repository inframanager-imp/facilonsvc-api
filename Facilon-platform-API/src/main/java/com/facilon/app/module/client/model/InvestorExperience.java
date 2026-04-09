package com.facilon.app.module.client.model;

import com.facilon.app.model.TenantEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Investor experience and financial profile.
 * Stores investment history, risk profile, occupation.
 * Tenant-scoped.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "investor_experience")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorExperience extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "investor_unique_id")
    private String investorUniqueId;

    @Column(name = "educational_qualification")
    private String educationalQualification;

    @Column(name = "annual_income")
    private String annualIncome;

    @Column(name = "net_worth")
    private String netWorth;

    @Column(name = "investment_objective")
    private String investmentObjective;

    @Column(name = "risk_tolerance")
    private String riskTolerance;

    @Column(name = "risk_profile")
    private String riskProfile;

    @Column(name = "investment_experience")
    private String investmentExperience;

    @Column(name = "investment_horizon")
    private String investmentHorizon;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(name = "previous_investments", columnDefinition = "TEXT")
    private String previousInvestments;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "employer_name")
    private String employerName;

    @Column(name = "designation")
    private String designation;

    @Column(name = "source_of_funds")
    private String sourceOfFunds;

    @Column(name = "source_of_funds_details")
    private String sourceOfFundsDetails;

    @Column(name = "expected_investment_amount")
    private Integer expectedInvestmentAmount;
}
