package com.facilon.app.module.client.model;

import com.facilon.app.model.TenantEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Investor information status - tracks completion status of various KYC sections.
 * Used for onboarding progress tracking.
 * Tenant-scoped.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "investor_information_status")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorInformationStatus extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "investor_email", columnDefinition = "TEXT")
    private String investorEmail;

    @Column(name = "broker_id", columnDefinition = "TEXT")
    private String brokerId;

    // Status fields: 1=complete, 2=pending/incomplete
    @Column(name = "bank_details")
    @Builder.Default
    private Integer bankDetails = 2;

    @Column(name = "bank_yes_no")
    @Builder.Default
    private Integer bankYesNo = 2;

    @Column(name = "personal_info")
    @Builder.Default
    private Integer personalInfo = 2;

    @Column(name = "passport")
    @Builder.Default
    private Integer passport = 2;

    @Column(name = "residential_status")
    @Builder.Default
    private Integer residentialStatus = 2;

    @Column(name = "tax_information")
    @Builder.Default
    private Integer taxInformation = 2;

    @Column(name = "contact_details")
    @Builder.Default
    private Integer contactDetails = 2;

    @Column(name = "nomination")
    @Builder.Default
    private Integer nomination = 2;

    @Column(name = "risk_profile")
    @Builder.Default
    private Integer riskProfile = 2;
}
