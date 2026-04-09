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
 * Tracks physical document submission via courier/mail.
 * Used when investors send hard copies of documents.
 * Tenant-scoped.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "investor_physical_submission")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorPhysicalSubmission extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Physical submission indicator/value
    @Column(name = "physical_submission_value", columnDefinition = "TEXT")
    private String physicalSubmissionValue;

    // Physical submission method: "inperson" or "courier"
    @Column(name = "physical_submission", columnDefinition = "TEXT")
    private String physicalSubmission;

    // Courier details
    @Column(name = "courier_name", columnDefinition = "TEXT")
    private String courierName;

    @Column(name = "dispatch_date")
    private LocalDateTime dispatchDate;

    // Air Waybill number for tracking
    @Column(name = "awb_number", columnDefinition = "TEXT")
    private String awbNumber;

    // Links to investor.unique_code
    @Column(name = "investor_unique_id", columnDefinition = "TEXT")
    private String investorUniqueId;

    @Column(name = "ss_investorid", columnDefinition = "TEXT")
    private String ssInvestorId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
