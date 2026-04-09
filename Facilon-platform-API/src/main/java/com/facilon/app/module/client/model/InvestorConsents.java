package com.facilon.app.module.client.model;

import com.facilon.app.model.TenantEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Investor consents for terms & conditions, data usage, etc.
 * Tracks user agreements and consent status.
 * Tenant-scoped.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "investor_consents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorConsents extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "investor_unique_id")
    private String investorUniqueId;

    @Column(name = "investor_id")
    private Long investorId;

    @Column(name = "email")
    private String email;

    @Column(name = "user_dob")
    private LocalDate userDob;

    // Legacy + profile consent flags merged into one model
    @Column(name = "information_correct_consent")
    private Boolean informationCorrectConsent;

    @Column(name = "terms_accepted")
    private Boolean termsAccepted;

    @Column(name = "privacy_policy_accepted")
    private Boolean privacyPolicyAccepted;

    @Column(name = "legal_capacity_consent")
    private Boolean legalCapacityConsent;

    @Column(name = "modification_awareness_consent")
    private Boolean modificationAwarenessConsent;

    @Column(name = "notification_consent")
    private Boolean notificationConsent;

    @Column(name = "whatsapp_consent")
    private Boolean whatsappConsent;

    @Column(name = "marketing_consent")
    private Boolean marketingConsent;

    @Column(name = "data_sharing_consent")
    private Boolean dataSharingConsent;

    @Column(name = "consent_date")
    private LocalDateTime consentDate;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "consent_version")
    private String consentVersion;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (consentDate == null) {
            consentDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
