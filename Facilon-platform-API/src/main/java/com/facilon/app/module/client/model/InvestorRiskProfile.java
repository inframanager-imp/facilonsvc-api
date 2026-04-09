package com.facilon.app.module.client.model;

import com.facilon.app.model.TenantEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Investor risk profile and investment preferences.
 * Stores risk assessment and investment objectives.
 * Tenant-scoped.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "investor_risk_profile")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorRiskProfile extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Links to investor.unique_code
    @Column(name = "investor_unique_id")
    private String investorUniqueId;

    @Column(name = "risk_tolerance")
    private String riskTolerance; // Low, Medium, High

    @Column(name = "investment_horizon")
    private String investmentHorizon; // Short-term, Medium-term, Long-term

    @Column(name = "investment_objective")
    private String investmentObjective; // Growth, Income, Balanced

    @Column(name = "annual_income")
    private String annualIncome; // Range: <5L, 5-10L, 10-25L, >25L

    @Column(name = "net_worth")
    private String netWorth; // Range categories

    @Column(name = "source_of_funds")
    private String sourceOfFunds; // Salary, Business, Inheritance, etc.

    @Column(name = "age_group")
    private Integer ageGroup;

    @Column(name = "market_knowledge")
    private String marketKnowledge;

    @Column(name = "loss_comfort_level")
    private String lossComfortLevel;

    @Column(name = "previous_losses")
    private String previousLosses;

    @Column(name = "understands_risk")
    private Boolean understandsRisk;

    @Column(name = "risk_score")
    private String riskScore;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "educational_qualification")
    private String educationalQualification;

    @Column(name = "investment_experience_in", columnDefinition = "TEXT")
    private String investmentExperienceIn;

    @Column(name = "investment_experience_years")
    private Integer investmentExperienceYears;

    @Column(name = "source_of_wealth")
    private String sourceOfWealth;

    @Column(name = "line_of_business")
    private String lineOfBusiness;

    @Column(name = "nature_of_organisation")
    private String natureOfOrganisation;

    @Column(name = "pol_exposed")
    private Boolean polExposed;

    @Column(name = "pol_exposed_related")
    private Boolean polExposedRelated;

    @Column(name = "activity")
    private Boolean activity;

    @Column(name = "money_change_service")
    private Boolean moneyChangeService;

    @Column(name = "gambling_service")
    private Boolean gamblingService;

    @Column(name = "pawning_service")
    private Boolean pawningService;

    @Column(name = "instance_violation")
    private Boolean instanceViolation;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
