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
 * Investor tax information.
 * Stores PAN, TIN, and tax-related details.
 * Tenant-scoped.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "investor_tax_information")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorTaxInformation extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Links to investor.unique_code
    @Column(name = "investor_unique_id")
    private String investorUniqueId;

    // Encrypted in service layer
    @Column(name = "pan_number", columnDefinition = "TEXT")
    private String panNumber;

    @Column(name = "tax_identification_number")
    private String taxIdentificationNumber;

    // FK to master_countries
    @Column(name = "tax_country")
    private Integer taxCountry;

    @Column(name = "tax_residency_status")
    private String taxResidencyStatus;

    @Column(name = "gst_number")
    private String gstNumber;

    @Column(name = "income_source")
    private String incomeSource;

    @Column(name = "annual_income")
    private String annualIncome;

    @Column(name = "tax_identification_number_type")
    private String taxIdentificationNumberType;

    @Column(name = "fatca_status")
    private String fatcaStatus;

    @Column(name = "crs_declaration")
    private String crsDeclaration;

    @Column(name = "us_citizen")
    private Boolean usCitizen;

    @Column(name = "us_resident")
    private Boolean usResident;

    @Column(name = "is_tax_exempt")
    @Builder.Default
    private Boolean isTaxExempt = false;

    @Column(name = "tax_pan_first_name")
    private String taxPanFirstName;

    @Column(name = "tax_pan_father_name")
    private String taxPanFatherName;

    @Column(name = "tax_residency_certificate_no")
    private String taxResidencyCertificateNo;

    @Column(name = "tax_residency_certificate_date")
    private String taxResidencyCertificateDate;

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
