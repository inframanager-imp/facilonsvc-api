package com.facilon.app.module.dsr.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * An immutable audit/timeline event for a {@link DsrCase}.
 *
 * <p>Plain entity (not tenant-filtered) - always queried by its parent {@code dsrCaseId},
 * which is itself resolved from a tenant-checked case. Investor-visible rows double as the
 * investor-facing timeline; internal rows are visible only to Privacy Ops / Admin.</p>
 */
@Entity
@Table(name = "dsr_case_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsrCaseEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dsr_case_id", nullable = false)
    private Long dsrCaseId;

    @Column(name = "case_id", nullable = false, length = 64)
    private String caseId;

    /** Machine code for the event - usually the resulting status name. */
    @Column(name = "event_code", nullable = false, length = 64)
    private String eventCode;

    @Column(name = "actor", length = 255)
    private String actor;

    @Column(name = "actor_role", length = 64)
    private String actorRole;

    @Column(name = "from_status", length = 64)
    private String fromStatus;

    @Column(name = "to_status", length = 64)
    private String toStatus;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "investor_visible", nullable = false)
    private boolean investorVisible;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
