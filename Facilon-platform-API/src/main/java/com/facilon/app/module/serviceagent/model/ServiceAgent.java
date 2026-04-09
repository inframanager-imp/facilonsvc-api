package com.facilon.app.module.serviceagent.model;

import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.model.TenantEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "service_agents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceAgent extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorized_user_id", nullable = false, unique = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private AuthorizedUser authorizedUser;

    @Column(name = "agent_code", unique = true, length = 50)
    private String agentCode;

    @Column(name = "agent_type", length = 50)
    private String agentType;

    @Column(name = "full_name", length = 200)
    private String fullName;

    @Column(name = "email", length = 200)
    private String email;

    @Column(name = "mobile", length = 50)
    private String mobile;

    @Column(name = "assigned_region", length = 100)
    private String assignedRegion;

    @Column(name = "assigned_segment", length = 100)
    private String assignedSegment;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "onboarding_status", length = 50)
    private String onboardingStatus;

    @Column(name = "service_provider_id")
    private Long serviceProviderId;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "pan_number", length = 20)
    private String panNumber;

    @Column(name = "address_proof_url", length = 500)
    private String addressProofUrl;

    @Column(name = "registration_number", length = 100)
    private String registrationNumber;

    @Column(name = "dv_service_agent_id", length = 100)
    private String dvServiceAgentId;

    @Column(name = "onboarded_at")
    private LocalDateTime onboardedAt;

    @Column(name = "onboarded_by", length = 200)
    private String onboardedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
