package com.facilon.app.module.dsr.model;

import com.facilon.app.model.TenantEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A Data Subject / Data Principal Request (DSR) case.
 *
 * <p>Self-contained DSR module entity. Relies on {@link TenantEntity} / Auditable for
 * tenant scoping and created_at / modified_at (matching the ServiceAgent convention) -
 * it deliberately does NOT redeclare those audit columns.</p>
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "dsr_cases")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsrCase extends TenantEntity {

    /** Right being exercised - aligned to the BRD's 11 request-type codes. */
    public enum RequestType {
        ACCESS,
        DATA_COPY,
        CORRECTION,
        ERASURE,
        CONSENT_WITHDRAWAL,
        GRIEVANCE,
        NOMINATION,
        MARKETING_OPTOUT,
        COOKIE_TRACKING,
        RESTRICT,
        OTHER
    }

    public enum Jurisdiction {
        INDIA,
        CANADA,
        UK,
        UAE,
        HONG_KONG,
        SINGAPORE
    }

    /**
     * Internal workflow status (Privacy Ops view). Service-Provider routing states
     * (ROUTED_TO_SP / CONTROLLER_ASSESSMENT_PENDING) are intentionally omitted this phase.
     */
    public enum CaseStatus {
        NEW,
        SUBMITTED,
        ACKNOWLEDGED,
        VERIFICATION_PENDING,
        CLARIFICATION_PENDING,
        UNDER_REVIEW,
        DATA_SEARCH_IN_PROGRESS,
        LEGAL_REVIEW,
        ACTION_IN_PROGRESS,
        RESPONSE_SENT,
        PARTIALLY_FULFILLED,
        REJECTED,
        CLOSED,
        REOPENED
    }

    /** Reserved for the future Service-Provider phase; always NO_SP in V1. */
    public enum SpRelationFlag {
        NO_SP,
        SP_SELECTED,
        NOT_SURE
    }

    public enum VerificationStatus {
        PENDING,
        VERIFIED,
        FAILED,
        NOT_REQUIRED
    }

    public enum Decision {
        PENDING,
        ACCEPTED,
        REJECTED,
        PARTIALLY_FULFILLED,
        ROUTED
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

    /** Comma-separated data-area codes (e.g. "INVESTOR_ACCOUNT,ONBOARD"). */
    @Column(name = "data_area", length = 512)
    private String dataArea;

    @Enumerated(EnumType.STRING)
    @Column(name = "sp_relation_flag", length = 32)
    private SpRelationFlag spRelationFlag;

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

    /** Absolute/relative path to this case's local evidence folder (the DSR Case ID folder). */
    @Column(name = "evidence_folder_path", length = 512)
    private String evidenceFolderPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 64)
    private CaseStatus status;

    @Column(name = "sla_deadline", nullable = false)
    private LocalDateTime slaDeadline;

    @Column(name = "assigned_to", length = 255)
    private String assignedTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", length = 32)
    private VerificationStatus verificationStatus;

    @Column(name = "verification_method", length = 64)
    private String verificationMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", length = 32)
    private Decision decision;

    @Column(name = "final_outcome", length = 255)
    private String finalOutcome;

    /** Investor-visible resolution summary, surfaced on the detail page once resolved. */
    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
}
