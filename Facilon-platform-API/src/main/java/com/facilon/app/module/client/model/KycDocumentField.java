package com.facilon.app.module.client.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Per-field OCR extraction result persisted alongside a {@link KycDocuments} row.
 * One row per field returned by ORCReader's ExtractedField[] payload.
 */
@Entity
@Table(name = "kyc_document_fields", indexes = {
        @Index(name = "idx_kdf_document_id", columnList = "kyc_document_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycDocumentField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kyc_document_id", nullable = false)
    private Long kycDocumentId;

    @Column(name = "field_name", length = 100, nullable = false)
    private String fieldName;

    @Column(name = "field_value", columnDefinition = "TEXT")
    private String fieldValue;

    @Column(name = "confidence", precision = 3, scale = 2)
    private BigDecimal confidence;

    @Column(name = "page")
    private Integer page;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
