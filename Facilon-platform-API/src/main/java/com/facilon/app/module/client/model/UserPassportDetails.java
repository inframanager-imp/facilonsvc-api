package com.facilon.app.module.client.model;

import com.facilon.app.model.TenantEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Passport and travel document details for investors.
 * Tenant-scoped.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_passport_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPassportDetails extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Links to investor.unique_code
    @Column(name = "investor_unique_code")
    private String investorUniqueCode;

    @Column(name = "investorid", columnDefinition = "TEXT")
    private String investorId;

    @Column(name = "passport_number")
    private String passportNumber;

    @Column(name = "passport_issue_date")
    private LocalDate passportIssueDate;

    @Column(name = "passport_expiry_date")
    private LocalDate passportExpiryDate;

    @Column(name = "passport_place_of_issue")
    private String passportPlaceOfIssue;

    @Column(name = "passport_country_of_issue")
    private String passportCountryOfIssue;

    @Column(name = "passport_front_copy", columnDefinition = "TEXT")
    private String passportFrontCopy;

    @Column(name = "passport_back_copy", columnDefinition = "TEXT")
    private String passportBackCopy;

    // Laravel additional fields
    @Column(name = "passport_nationality")
    private String passportNationality;

    @Column(name = "passport_date_non_resident")
    private LocalDate passportDateNonResident;

    @Column(name = "passport_no_years_abroad")
    private Integer passportNoYearsAbroad;

    /** Proof-of-identity document type (e.g. Passport, PAN, Aadhaar). */
    @Column(name = "document_type", length = 100)
    private String documentType;
}
