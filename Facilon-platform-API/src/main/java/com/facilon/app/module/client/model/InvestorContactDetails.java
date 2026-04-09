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
 * Investor contact information.
 * Stores phone numbers, email addresses, and preferred contact methods.
 * Tenant-scoped.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "investor_contact_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorContactDetails extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Links to investor.unique_code
    @Column(name = "investor_unique_id")
    private String investorUniqueId;

    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "address_line3")
    private String addressLine3;

    @Column(name = "user_city")
    private String userCity;

    @Column(name = "user_state")
    private String userState;

    @Column(name = "user_country")
    private String userCountry;

    @Column(name = "user_zip_code")
    private String userZipCode;

    @Column(name = "corr_address_same_as_perm")
    private String corrAddressSameAsPerm;

    @Column(name = "corr_address_line1")
    private String corrAddressLine1;

    @Column(name = "corr_address_line2")
    private String corrAddressLine2;

    @Column(name = "corr_address_line3")
    private String corrAddressLine3;

    @Column(name = "corr_user_city")
    private String corrUserCity;

    @Column(name = "corr_user_state")
    private String corrUserState;

    @Column(name = "corr_user_country")
    private String corrUserCountry;

    @Column(name = "corr_user_zip_code")
    private String corrUserZipCode;

    @Column(name = "isd_code")
    private String isdCode;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "alternate_mobile")
    private String alternateMobile;

    @Column(name = "whatsapp_number")
    private String whatsappNumber;

    @Column(name = "landline_number")
    private String landlineNumber;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "alternate_email")
    private String alternateEmail;

    @Column(name = "preferred_contact_method")
    private String preferredContactMethod; // Mobile, Email, WhatsApp

    @Column(name = "preferred_contact_time")
    private String preferredContactTime;

    @Column(name = "proof_of_address")
    private String proofOfAddress;

    @Column(name = "address_type")
    private String addressType;

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
