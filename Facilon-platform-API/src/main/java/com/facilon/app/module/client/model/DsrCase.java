package com.facilon.app.module.client.model;

import com.facilon.app.model.TenantEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "dsr_cases")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsrCase extends TenantEntity {

    public enum RequestType {
        ACCESS,
        CORRECTION,
        DELETION,
        CONSENT_WITHDRAWAL,
        OBJECTION,
        COMPLAINT
    }

    public enum Jurisdiction {
        INDIA,
        CANADA,
        UK,
        UAE,
        HONG_KONG,
        SINGAPORE
    }

    public enum CaseStatus {
        SUBMITTED,
        UNDER_REVIEW,
        IDENTITY_VERIFIED,
        IN_PROGRESS,
        RESOLVED,
        CLOSED,
        REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false, unique = true, length = 64)
    private String caseId;

    @Column(name = "investor_unique_code", nullable = false, length = 64)
    private String investorUniqueCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false, length = 64)
    private RequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(name = "jurisdiction", nullable = false, length = 64)
    private Jurisdiction jurisdiction;

    @Column(name = "request_description", nullable = false, columnDefinition = "TEXT")
    private String requestDescription;

    @Column(name = "requester_name", nullable = false, length = 255)
    private String requesterName;

    @Column(name = "requester_email", nullable = false, length = 255)
    private String requesterEmail;

    @Column(name = "requester_phone", length = 64)
    private String requesterPhone;

    @Column(name = "requester_role", length = 64)
    private String requesterRole;

    @Column(name = "supporting_file_path", length = 512)
    private String supportingFilePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 64)
    private CaseStatus status;

    @Column(name = "sla_deadline", nullable = false)
    private LocalDateTime slaDeadline;

    @Column(name = "assigned_to", length = 255)
    private String assignedTo;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

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
