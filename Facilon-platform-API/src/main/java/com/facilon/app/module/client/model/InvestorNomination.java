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
 * Investor nominee details.
 * Stores information about beneficiaries in case of investor's death.
 * Tenant-scoped.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "investor_nomination")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorNomination extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Links to investor.unique_code
    @Column(name = "investor_unique_id")
    private String investorUniqueId;

    @Column(name = "nominee_first_name")
    private String nomineeFirstName;

    @Column(name = "nominee_middle_name")
    private String nomineeMiddleName;

    @Column(name = "nominee_last_name")
    private String nomineeLastName;

    @Column(name = "relationship")
    private String relationship; // Father, Mother, Spouse, Child, etc.

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "allocation_percentage")
    private Integer allocationPercentage; // 0-100

    @Column(name = "nominee_email")
    private String nomineeEmail;

    @Column(name = "nominee_mobile")
    private String nomineeMobile;

    @Column(name = "nominee_address", columnDefinition = "TEXT")
    private String nomineeAddress;

    @Column(name = "nominee_city")
    private String nomineeCity;

    @Column(name = "nominee_state")
    private String nomineeState;

    @Column(name = "nominee_postal_code")
    private String nomineePostalCode;

    @Column(name = "is_minor")
    private Boolean isMinor;

    // For minor nominees
    @Column(name = "guardian_name")
    private String guardianName;

    @Column(name = "guardian_relationship")
    private String guardianRelationship;

    @Column(name = "nominee_doc_type")
    private String nomineeDocType;

    @Column(name = "nominee_doc_no")
    private String nomineeDocNo;

    @Column(name = "nominee_countrycode")
    private Integer nomineeCountrycode;

    @Column(name = "guardian_doc_type")
    private String guardianDocType;

    @Column(name = "guardian_doc_no")
    private String guardianDocNo;

    @Column(name = "guardian_countrycode")
    private Integer guardianCountrycode;

    @Column(name = "guardian_mobile")
    private String guardianMobile;

    @Column(name = "guardian_email")
    private String guardianEmail;

    @Column(name = "guardian_dob")
    private LocalDate guardianDob;

    @Column(name = "guardian_pan_no")
    private String guardianPanNo;

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
