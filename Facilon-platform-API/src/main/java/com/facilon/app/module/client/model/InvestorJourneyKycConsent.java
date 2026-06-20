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
 * Per-journey consent to reuse the investor's confirmed KYC documents for a
 * specific journey. One row per (investor, journey); recorded the first time the
 * investor agrees on a journey's Continue/play gate.
 *
 * Plan: implemented_docs/kyc-blob-journey-consent-plan.md (Phase 3).
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "investor_journey_kyc_consent",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_journey_kyc_consent",
                columnNames = {"investor_unique_id", "journey_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorJourneyKycConsent extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "investor_unique_id", length = 64, nullable = false)
    private String investorUniqueId;

    /** The journey identifier (JourneyListItem.journeyId) the consent applies to. */
    @Column(name = "journey_id", length = 128, nullable = false)
    private String journeyId;

    @Column(name = "consent_given_at", nullable = false)
    private LocalDateTime consentGivenAt;

    /** TRUE = "I Agree" (triggers archive); FALSE = "Skip for now" (decision recorded, no archive). */
    @Column(name = "consent_given", nullable = false)
    private Boolean consentGiven;

    @Column(name = "ip_address", length = 64)
    private String ipAddress;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "consent_version", length = 20)
    private String consentVersion;
}
