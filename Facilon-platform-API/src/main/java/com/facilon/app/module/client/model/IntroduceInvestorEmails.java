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
 * Tracks investor invitation/introduction emails.
 * Used for email campaign tracking and automation.
 * Tenant-scoped.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "introduce_investor_emails")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntroduceInvestorEmails extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", columnDefinition = "TEXT", nullable = false)
    private String name;

    @Column(name = "code", columnDefinition = "TEXT", nullable = false)
    private String code;

    @Column(name = "email_id", columnDefinition = "TEXT", nullable = false)
    private String emailId;

    // Auto email flag: 1=enabled, 2=disabled
    @Column(name = "auto_email", nullable = false)
    @Builder.Default
    private Integer autoEmail = 2;

    @Column(name = "auto_mail_sent_count", nullable = false)
    @Builder.Default
    private Integer autoMailSentCount = 0;

    // Manual email flag: 1=sent, 2=not sent
    @Column(name = "manual_email", nullable = false)
    @Builder.Default
    private Integer manualEmail = 2;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
