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
 * KYC documents uploaded by investors/clients.
 * Tracks document status, type, and storage location.
 * Tenant-scoped.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "kyc_documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycDocuments extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Links to investor.unique_code
    @Column(name = "investor_unique_id", columnDefinition = "TEXT")
    private String investorUniqueId;

    @Column(name = "ss_investor_id", columnDefinition = "TEXT")
    private String ssInvestorId;

    // Document status: pending, approved, rejected, etc.
    @Column(name = "status", columnDefinition = "TEXT")
    private String status;

    // URL or path to document storage
    @Column(name = "document_url", columnDefinition = "TEXT")
    private String documentUrl;

    // Document type: PAN, Aadhar, Passport, Bank Statement, etc.
    @Column(name = "document_type", columnDefinition = "TEXT", nullable = false)
    private String documentType;

    @Column(name = "doc_description", columnDefinition = "TEXT")
    private String docDescription;

    // Upload type: 1=online, 2=physical, etc.
    @Column(name = "upload_type", nullable = false)
    @Builder.Default
    private Integer uploadType = 1;

    // Reference to document master/template
    @Column(name = "document_master_id", length = 45)
    private String documentMasterId;

    // Rejection reason if status = rejected
    @Column(name = "reason", length = 100)
    private String reason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
