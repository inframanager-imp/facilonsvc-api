package com.facilon.app.module.client.model;

import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.model.TenantEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Investor entity - extends AuthorizedUser for users who are investors/clients.
 * This is a 1:1 optional relationship (not every user is an investor).
 * Tenant-scoped via TenantEntity.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "investor")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Investor extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "investor_id")
    private Long id;

    // One-to-one with AuthorizedUser (bidirectional). Excluded to avoid cycle in equals/hashCode/toString.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorized_user_id", nullable = false, unique = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private AuthorizedUser authorizedUser;

    @Column(name = "unique_code")
    private String uniqueCode;

    // Registration type: 1=individual, 2=entity, etc.
    @Column(name = "register_as")
    private Integer registerAs;

    @Column(name = "market")
    private Integer market;

    @Column(name = "citizenship")
    private Integer citizenship;

    @Column(name = "country_of_residence")
    private Integer countryOfResidence;

    @Column(name = "nationality")
    private Integer nationality;

    @Column(name = "residence_type")
    private String residenceType;

    @Column(name = "pancard_status")
    private String pancardStatus;

    @Column(name = "indian_origin")
    private String indianOrigin;

    @Column(name = "oci_card_status")
    private String ociCardStatus;

    // For corporate/entity investors
    @Column(name = "entity_name")
    private String entityName;

    @Column(name = "entity_website")
    private String entityWebsite;

    @Column(name = "incorp_country")
    private Integer incorpCountry;

    @Column(name = "country_of_tax_residency")
    private Integer countryOfTaxResidency;

    @Column(name = "entity_name_representative")
    private String entityNameRepresentative;

    @Column(name = "company_capacity")
    private String companyCapacity;

    @Column(name = "security_regulated")
    private String securityRegulated;

    @Column(name = "registration_id")
    private String registrationId;

    @Column(name = "confirmation")
    private Integer confirmation;

    @Column(name = "terms_read")
    private Integer termsRead;

    @Column(name = "privacy_policy_accepted")
    private Integer privacyPolicyAccepted;

    @Column(name = "notification_consent")
    private Integer notificationConsent;

    @Column(name = "whatsapp_consent")
    private Integer whatsappConsent;

    // Investor type determination (based on registration parameters)
    @Column(name = "investor_type")
    private String investorType; // RESIDENT_INDIVIDUAL, NRI, OCI, FOREIGN_NATIONAL, etc.

    @Column(name = "document_type")
    private String documentType; // CKYC - Individual, CKYC - Non-Individual, CAF

    // Verification status: 1=verified, 2=pending, 3=rejected, etc.
    @Column(name = "verify_status")
    @Builder.Default
    private Integer verifyStatus = 2;

    // Dataverse/external system integration
    @Column(name = "dv_investor_ss_id", columnDefinition = "TEXT")
    private String dvInvestorSsId;

    /** Dataverse Contact ID (contacts.contactid). Required for PATCH contact and Contact–Investor link. */
    @Column(name = "dv_contact_id", length = 36)
    private String dvContactId;

    /** Dataverse ss_investors GUID (ss_investorid). Required for PATCH on ss_investors (bank, intro updates). */
    @Column(name = "dv_investor_guid", length = 36)
    private String dvInvestorGuid;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** Set when all mandatory KYC slots are VALID + user-confirmed. Drives the "KYC complete" gate. */
    @Column(name = "kyc_verified_at")
    private LocalDateTime kycVerifiedAt;

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
