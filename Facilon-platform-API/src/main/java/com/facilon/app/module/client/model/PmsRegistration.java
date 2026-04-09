package com.facilon.app.module.client.model;

import com.facilon.app.model.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "investor_pms_registration")
public class PmsRegistration extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "investor_id")
    private Long investorId;

    @Column(name = "investment_amount")
    private BigDecimal investmentAmount;

    @Column(name = "portfolio_type")
    private String portfolioType;

    @Column(name = "risk_profile")
    private String riskProfile;

    @Column(name = "investment_objective")
    private String investmentObjective;

    @Column(name = "investment_horizon")
    private String investmentHorizon;

    // Links to master data if needed, but usually redundant if InvestorPmsDetails
    // exists.
    // However, PmsService DTO has them.
    @Column(name = "pms_manager_id")
    private Long pmsManagerId;

    @Column(name = "pms_plan_id")
    private Long pmsPlanId;

    @Column(name = "pms_bank_id")
    private Long pmsBankId;

    @Column(name = "service_preferences")
    private String servicePreferences;

    @Column(name = "eligibility_confirmed")
    private Boolean eligibilityConfirmed;

    @Column(name = "registration_status")
    private String registrationStatus;

    @Column(name = "submitted_date")
    private LocalDateTime submittedDate;
}
