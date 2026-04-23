package com.facilon.app.module.client.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Audit row written each time a confirmed KYC document imports a field
 * into the investor's profile (Investor / UserPersonalInformation).
 * Provides "imported from KYC" provenance.
 */
@Entity
@Table(name = "kyc_profile_import_audit", indexes = {
        @Index(name = "idx_kpia_investor", columnList = "investor_unique_id"),
        @Index(name = "idx_kpia_source_doc", columnList = "source_document_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycProfileImportAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "investor_unique_id", length = 64, nullable = false)
    private String investorUniqueId;

    @Column(name = "source_document_id", nullable = false)
    private Long sourceDocumentId;

    /** PAN_CARD | PASSPORT | AADHAR_CARD | OCI_CARD | ADDRESS_PROOF */
    @Column(name = "source_document_type", length = 30, nullable = false)
    private String sourceDocumentType;

    /** "investor.nationality" / "user_personal_information.investor_first_name" etc. */
    @Column(name = "target_field", length = 80, nullable = false)
    private String targetField;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "imported_at", nullable = false)
    private LocalDateTime importedAt;

    @PrePersist
    protected void onCreate() {
        if (importedAt == null) importedAt = LocalDateTime.now();
    }
}
