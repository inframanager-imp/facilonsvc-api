package com.facilon.app.module.client.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Discrepancy flagged by KycValidationEngine cross-checks
 * (see KYC_DOCUMENT_PLAN §3.6 rules table).
 */
@Entity
@Table(name = "kyc_document_discrepancies", indexes = {
        @Index(name = "idx_kdd_document_id", columnList = "kyc_document_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycDocumentDiscrepancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kyc_document_id", nullable = false)
    private Long kycDocumentId;

    /** PAN | REGISTRATION | OCR_DOC_TYPE | EXPIRY | PREREQ */
    @Column(name = "canonical_source", length = 30, nullable = false)
    private String canonicalSource;

    @Column(name = "field_name", length = 100)
    private String fieldName;

    @Column(name = "expected_value", columnDefinition = "TEXT")
    private String expectedValue;

    @Column(name = "observed_value", columnDefinition = "TEXT")
    private String observedValue;

    /** BLOCKING | WARNING */
    @Column(name = "severity", length = 20, nullable = false)
    private String severity;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
