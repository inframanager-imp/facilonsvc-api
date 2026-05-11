package com.facilon.app.module.serviceprovider.model;

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
 * Mirrors Laravel `sp_consents_user` written by `BrokerController::service_provider_step3_submit`
 * — captures the SP user's consent at the user-level Privacy & Consent page.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sp_consents_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpConsentUser extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_code", nullable = false)
    private String uniqueCode;

    @Column(name = "consent_version", nullable = false, length = 32)
    private String consentVersion;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "ip_address", length = 64)
    private String ipAddress;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "escalation_name")
    private String escalationName;

    @Column(name = "escalation_email")
    private String escalationEmail;

    @Column(name = "escalation_phone", length = 64)
    private String escalationPhone;

    @Column(name = "consent_given", nullable = false)
    private Boolean consentGiven;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
