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
 * Service Recipient Terms of Use (SR TOU) consent record, captured when an introduced
 * investor completes registration (step4). Mirrors Laravel's {@code investor_tou_consents}
 * table insert in {@code introduce_investor_register_step4_insert_data}.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "investor_tou_consents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorTouConsent extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "consent_version")
    private String consentVersion;

    @Column(name = "consented_at")
    private LocalDateTime consentedAt;

    @Column(name = "email")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "unique_code")
    private String uniqueCode;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
}
