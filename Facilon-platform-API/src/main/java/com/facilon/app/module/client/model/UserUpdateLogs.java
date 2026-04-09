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
 * Audit log for investor/client data changes.
 * Tracks field-level changes for compliance and audit purposes.
 * Tenant-scoped.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_update_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateLogs extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Links to investor.unique_code
    @Column(name = "investor_unique_id", columnDefinition = "TEXT")
    private String investorUniqueId;

    // Field that was changed
    @Column(name = "field_name", columnDefinition = "TEXT")
    private String fieldName;

    // Old value before change
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    // New value after change
    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    // User who made the change (authorized_user_id)
    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
    }
}
