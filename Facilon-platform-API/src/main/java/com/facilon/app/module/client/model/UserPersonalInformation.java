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
 * User personal information for investors.
 * Stores KYC and identity details.
 * Tenant-scoped.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_personal_information")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPersonalInformation extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Links to investor.unique_code
    @Column(name = "investor_unique_id")
    private String investorUniqueId;

    @Column(name = "investorid", columnDefinition = "TEXT")
    private String investorId;

    @Column(name = "title")
    private Integer title; // Reference to master_title

    /** Free-text salutation (Laravel: name_title). Distinct from the numeric `title` master reference. */
    @Column(name = "name_title", columnDefinition = "TEXT")
    private String nameTitle;

    @Column(name = "investor_first_name")
    private String investorFirstName;

    @Column(name = "investor_middle_name")
    private String investorMiddleName;

    @Column(name = "investor_last_name")
    private String investorLastName;

    @Column(name = "investor_gender")
    private String investorGender;

    @Column(name = "user_dob")
    private LocalDate userDob;

    // Gender and Marital Status
    @Column(name = "gender")
    private String gender;

    @Column(name = "marital_status")
    private String maritalStatus;

    // Maiden Name Fields
    @Column(name = "maiden_title")
    private String maidenTitle;

    @Column(name = "maiden_name")
    private String maidenName;

    @Column(name = "maiden_middle_name")
    private String maidenMiddleName;

    @Column(name = "maiden_last_name")
    private String maidenLastName;

    // Birth Location
    @Column(name = "city_of_dob")
    private String cityOfDob;

    @Column(name = "country_dob")
    private String countryDob;

    @Column(name = "citizenship")
    private String citizenship;

    // Father's Details
    @Column(name = "father_name_title")
    private String fatherNameTitle;

    @Column(name = "fathers_first_name")
    private String fathersFirstName;

    @Column(name = "fathers_middle_name")
    private String fathersMiddleName;

    @Column(name = "fathers_last_name")
    private String fathersLastName;

    // Mother's Details
    @Column(name = "mother_name_title")
    private String motherNameTitle;

    @Column(name = "mother_first_name")
    private String motherFirstName;

    @Column(name = "mother_middle_name")
    private String motherMiddleName;

    @Column(name = "mother_last_name")
    private String motherLastName;

    // Spouse Details
    @Column(name = "spouse_name_title")
    private String spouseNameTitle;

    @Column(name = "spouse_name")
    private String spouseName;

    @Column(name = "spouse_middle_name")
    private String spouseMiddleName;

    @Column(name = "spouse_last_name")
    private String spouseLastName;

    @Column(name = "spouse_maiden_name")
    private String spouseMaidenName;

    @Column(name = "whatsapp_number")
    private String whatsappNumber;

    // Address fields
    @Column(name = "address_line1", columnDefinition = "TEXT")
    private String addressLine1;

    @Column(name = "address_line2", columnDefinition = "TEXT")
    private String addressLine2;

    @Column(name = "address_line3", columnDefinition = "TEXT")
    private String addressLine3;

    @Column(name = "user_city", columnDefinition = "TEXT")
    private String userCity;

    @Column(name = "user_state", columnDefinition = "TEXT")
    private String userState;

    @Column(name = "user_country", columnDefinition = "TEXT")
    private String userCountry;

    @Column(name = "user_zip_code", columnDefinition = "TEXT")
    private String userZipCode;

    // Correspondence address
    @Column(name = "corr_address_line1", columnDefinition = "TEXT")
    private String corrAddressLine1;

    @Column(name = "corr_address_line2", columnDefinition = "TEXT")
    private String corrAddressLine2;

    @Column(name = "corr_address_line3", columnDefinition = "TEXT")
    private String corrAddressLine3;

    @Column(name = "corr_user_city", columnDefinition = "TEXT")
    private String corrUserCity;

    @Column(name = "corr_user_state", columnDefinition = "TEXT")
    private String corrUserState;

    @Column(name = "corr_user_country", columnDefinition = "TEXT")
    private String corrUserCountry;

    @Column(name = "corr_user_zip_code", columnDefinition = "TEXT")
    private String corrUserZipCode;

    // Identity document fields
    @Column(name = "user_aadhar_no", columnDefinition = "TEXT")
    private String userAadharNo;

    @Column(name = "user_pan_no", columnDefinition = "TEXT")
    private String userPanNo;

    @Column(name = "user_oci_card_no", columnDefinition = "TEXT")
    private String userOciCardNo;

    @Column(name = "user_oci_issue_date")
    private LocalDate userOciIssueDate;

    @Column(name = "user_oci_valid_upto")
    private LocalDate userOciValidUpto;

    // Visa fields
    @Column(name = "user_visa_type", columnDefinition = "TEXT")
    private String userVisaType;

    @Column(name = "user_visa_number", columnDefinition = "TEXT")
    private String userVisaNumber;

    @Column(name = "user_visa_issuer_date")
    private LocalDate userVisaIssuerDate;

    @Column(name = "user_visa_expiry_date")
    private LocalDate userVisaExpiryDate;

    /** Country name (free text), aligned with country_dob / citizenship — not a master id */
    @Column(name = "country_of_residence", columnDefinition = "TEXT")
    private String countryOfResidence;

    @Column(name = "proof_of_address", columnDefinition = "TEXT")
    private String proofOfAddress;

    @Column(name = "address_type", columnDefinition = "TEXT")
    private String addressType;

    // --- Residential Status block (Laravel parity) ---
    @Column(name = "residential_status", columnDefinition = "TEXT")
    private String residentialStatus;

    @Column(name = "person_origin", columnDefinition = "TEXT")
    private String personOrigin;

    @Column(name = "aadhar_number_option", columnDefinition = "TEXT")
    private String aadharNumberOption;

    @Column(name = "aadhar_number", columnDefinition = "TEXT")
    private String aadharNumber;

    @Column(name = "oci_available", columnDefinition = "TEXT")
    private String ociAvailable;

    @Column(name = "date_of_oci")
    private LocalDate dateOfOci;

    // --- Tax block (Laravel parity) ---
    @Column(name = "tax_info", columnDefinition = "TEXT")
    private String taxInfo;

    @Column(name = "tax_pan_no", columnDefinition = "TEXT")
    private String taxPanNo;

    @Column(name = "us_person_fatca", columnDefinition = "TEXT")
    private String usPersonFatca;

    // Verification fields (for in-person verification)
    @Column(name = "ss_verification_done", columnDefinition = "TEXT")
    private String ssVerificationDone; // "1" = done, "2" = not done

    @Column(name = "ss_verification_done_by", columnDefinition = "TEXT")
    private String ssVerificationDoneBy;

    @Column(name = "ss_verificationdateandtime")
    private LocalDateTime ssVerificationDateandTime;

    // Final submission fields (aligned with Laravel)
    @Column(name = "final_submit_status")
    private Integer finalSubmitStatus;

    @Column(name = "final_submit")
    private Integer finalSubmit;

    @Column(name = "personal_info_tab", columnDefinition = "TEXT")
    private String personalInfoTab;

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
