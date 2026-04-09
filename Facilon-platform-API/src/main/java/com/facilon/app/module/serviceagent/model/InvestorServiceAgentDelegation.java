package com.facilon.app.module.serviceagent.model;

import com.facilon.app.model.TenantEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "investor_service_agent_delegations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestorServiceAgentDelegation extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "investor_id", nullable = false)
    private Long investorId;

    @Column(name = "service_agent_id", nullable = false)
    private Long serviceAgentId;

    @Column(name = "scope", nullable = false, length = 50)
    private String scope;

    @Column(name = "can_view_profile")
    private Boolean canViewProfile;

    @Column(name = "can_edit_kyc")
    private Boolean canEditKyc;

    @Column(name = "can_upload_documents")
    private Boolean canUploadDocuments;

    @Column(name = "can_submit_forms")
    private Boolean canSubmitForms;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "assigned_by_sp_id")
    private Long assignedBySpId;

    @Column(name = "consent_version", length = 20)
    private String consentVersion;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "consent_reference", length = 100)
    private String consentReference;

    @Column(name = "consent_given_at")
    private LocalDateTime consentGivenAt;

    @Column(name = "consent_ip_address", length = 50)
    private String consentIpAddress;

    @Column(name = "consent_user_agent", columnDefinition = "TEXT")
    private String consentUserAgent;

    @Column(name = "dv_delegation_id", length = 100)
    private String dvDelegationId;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "revoked_by", length = 200)
    private String revokedBy;

    @Column(name = "revocation_reason", columnDefinition = "TEXT")
    private String revocationReason;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
