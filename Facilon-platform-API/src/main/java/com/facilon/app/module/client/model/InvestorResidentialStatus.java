package com.facilon.app.module.client.model;

import com.facilon.app.model.TenantEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Investor residential and tax status.
 * Stores residency type and politically exposed person (PEP) status.
 * Tenant-scoped.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "investor_residential_status")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorResidentialStatus extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Links to investor.unique_code
    @Column(name = "investor_unique_id")
    private String investorUniqueId;

    @Column(name = "residential_status")
    private String residentialStatus; // Resident, NRI, PIO, OCI

    // FK to master_countries
    @Column(name = "country_of_residence")
    private Integer countryOfResidence;

    @Column(name = "residence_address", columnDefinition = "TEXT")
    private String residenceAddress;

    @Column(name = "residence_city")
    private String residenceCity;

    @Column(name = "residence_state")
    private String residenceState;

    @Column(name = "residence_postal_code")
    private String residencePostalCode;

    @Column(name = "residence_phone")
    private String residencePhone;

    @Column(name = "residence_since")
    private String residenceSince;

    @Column(name = "address_proof_type")
    private String addressProofType;

    @Column(name = "address_proof_document_id")
    private String addressProofDocumentId;

    @Column(name = "years_at_current_address")
    private Integer yearsAtCurrentAddress;

    @Column(name = "user_visa_number")
    private String userVisaNumber;

    @Column(name = "user_visa_issuer_date")
    private String userVisaIssuerDate;

    @Column(name = "user_visa_expiry_date")
    private String userVisaExpiryDate;

    @Column(name = "user_oci_card_no")
    private String userOciCardNo;

    @Column(name = "user_oci_issue_date")
    private String userOciIssueDate;

    @Column(name = "user_oci_valid_upto")
    private String userOciValidUpto;

    @Column(name = "user_type_of_proof")
    private String userTypeOfProof;

    @Column(name = "user_visa_type")
    private String userVisaType;

    @Column(name = "user_visa_date_of_issue")
    private String userVisaDateOfIssue;

    @Column(name = "user_visa_valid_upto")
    private String userVisaValidUpto;

    // Laravel parity fields
    @Column(name = "person_origin", columnDefinition = "TEXT")
    private String personOrigin;

    @Column(name = "proof_of_address", columnDefinition = "TEXT")
    private String proofOfAddress;

    @Column(name = "aadhar_number_option", columnDefinition = "TEXT")
    private String aadharNumberOption;

    @Column(name = "aadhar_number", columnDefinition = "TEXT")
    private String aadharNumber;

    @Column(name = "user_aadhar_no", columnDefinition = "TEXT")
    private String userAadharNo;

    /** Name as printed on the Aadhaar card (RI-flow Aadhaar Details tab). */
    @Column(name = "name_on_aadhaar", columnDefinition = "TEXT")
    private String nameOnAadhaar;

    @Column(name = "oci_available", columnDefinition = "TEXT")
    private String ociAvailable;

    @Column(name = "date_of_oci")
    private LocalDate dateOfOci;

    @Column(name = "is_politically_exposed")
    @Builder.Default
    private Boolean isPoliticallyExposed = false;

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
