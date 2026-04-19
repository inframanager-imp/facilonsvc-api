package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Flat DTO carrying every template variable consumed by the KYC form
 * FreeMarker fragments ({@code templates/pdf/kyc-form/sections/*.ftl}).
 *
 * Field names match the {@code templateVar} values in
 * {@code kyc-form-mapping.json} exactly — no nesting; the FTL fragments
 * reference them as top-level variables ({@code ${firstName!''}}).
 *
 * Built by {@link com.facilon.app.module.client.service.KycPdfService#buildFormData}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycFormDataDto {

    // ─── Client Identifiers ───
    private String clientCode;
    private String clientId;

    // ─── Personal Details (Section 04) ───
    private String namePrefix;
    private String firstName;
    private String middleName;
    private String lastName;
    private String applicantName;       // composed: first + middle + last
    private String fatherSpouseName;    // composed
    private String fathersFirstName;
    private String fathersMiddleName;
    private String fathersLastName;
    private String fatherNameTitle;     // resolved title name, not ID
    private String motherName;          // composed
    private String motherFirstName;
    private String motherMiddleName;
    private String motherLastName;
    private String motherNameTitle;     // resolved title name
    private String maidenName;
    private String maidenMiddleName;
    private String maidenLastName;
    private String maidenTitle;
    private String dateOfBirth;         // dd-MM-yyyy
    private String gender;
    private String maritalStatus;
    private String citizenship;
    private String citizenshipName;     // resolved nationality name, e.g. "Indian"
    private String citizenshipIsoCode;  // e.g. "IN"
    private String residentialStatus;
    private String occupationType;
    private String panNumber;
    private String aadhaarNumber;
    private String cityOfBirth;
    private String countryOfBirth;
    private String countryOfBirthName;
    private String countryOfBirthIsoCode;  // 2-letter ISO 3166
    private String proofOfAddress;      // from user_main
    private String addressType;
    private String simplifiedMeasuresAddressCode;
    private String simplifiedMeasuresDocumentType;
    private String simplifiedMeasuresIdentificationNumber;

    // ─── Tax Residence (Section tax_residence) ───
    private String taxCountryCode;
    private String taxCountryIsoCode;  // 2-letter ISO 3166
    private String taxIdNumber;
    private String taxIdType;

    // ─── Proof of Identity (Section proof_of_identity) ───
    private String idType;
    private String idNumber;
    private String idExpiryDate;

    // ─── Identity Documents ───
    private String passportNumber;
    private String passportValidUpto;
    private String voterIdNumber;
    private String drivingLicenseNumber;
    private String drivingLicenseExpiry;
    private String otherIdNumber;
    private String othersIdentificationNumber;
    private String identificationNumber;
    private String taxPanNo;

    // ─── Checkbox / Flag values (for Blade checkbox images) ───
    private String passportNumberValue;
    private String voterIdCardValue;
    private String panCardValue;
    private String drivingLicenceValue;
    private String uidAadhaarValue;
    private String othersValue;
    private String polExposed;          // "1" or "0"
    private String polExposedRelated;   // "1" or "0"
    private String appointNominee;      // "yes" or "no"
    private String typeOfAccount;

    // ─── Permanent Address (Section address_permanent) ───
    private String permAddress1;
    private String permAddress2;
    private String permAddress3;
    private String permCity;
    private String permState;
    private String permCountry;
    private String permPinCode;
    private String permAddressProof;

    // ─── Correspondence Address (Section address_correspondence) ───
    private String corrSameAsPerm;
    private String corrAddress1;
    private String corrAddress2;
    private String corrCity;
    private String corrState;
    private String corrCountry;
    private String corrPinCode;

    // ─── Contact Details (Section contact_details) ───
    private String mobile;
    private String email;
    private String isdCode;
    private String mobileBelongsTo;
    private String emailBelongsTo;

    // ─── In-Person Verification ───
    private String ipvDoneBy;
    private String ipvDate;

    // ─── Trading — Income / Occupation (Section 08) ───
    private String incomeRange;
    private String grossIncome;
    private String netWorth;
    private String tradingOccupation;
    private String pepStatus;

    // ─── Trading — Bank (Section 08) ───
    private String bankName;
    private String bankBranch;
    private String bankBranchAddress;   // full branch address
    private String bankCity;
    private String bankAccountNo;
    private String bankAccountType;
    private String micrNo;
    private String ifscCode;
    private String bankDetailsCity;
    private String bankDetailsState;
    private String bankDetailsStateName;
    private String bankDetailsCountry;
    private String bankDetailsCountryName;
    private String bankDetailsZipCode;
    private String bankDetailsMicr;

    // ─── PIS Bank Details ───
    private String pisBankName;
    private String pisBankBranchAddress;
    private String pisBankAccountNumber;
    private String pisBankIfscCode;
    private String pisBankMicr;
    private String pisBankCity;
    private String pisBankZipCode;

    // ─── Trading — Depository ───
    private String dpName;
    private String depository;
    private String dpId;

    // ─── Trading — Experience ───
    private String investmentExperience;
    private String investmentExperienceIn;
    private String investmentExperienceYears;

    // ─── Demat Account (Section 09) ───
    private String dematPan;
    private String dematAccountType;
    private String rbiApproval;
    private String rbiApprovalDate;
    private String rbiApprovalNumber;

    // ─── Nomination (Section 10) ───
    private String nominee1Name;
    private String nominee1Relation;
    private String nominee1Dob;
    private String nominee1Share;
    private String nominee1Address;
    private String nominee1Mobile;
    private String nominee1Email;
    private String nominee2Name;
    private String nominee2Relation;
    private String nominee2Dob;
    private String nominee2Share;
    private String nominee2Address;
    private String nominee2Mobile;
    private String nominee2Email;
    private String nominee3Name;
    private String nominee3Relation;
    private String nominee3Dob;
    private String nominee3Share;
    private String nominee3Address;
    private String nominee3Mobile;
    private String nominee3Email;

    // ─── Nominee City / State / Country / Pincode ───
    private String nomineeCity1;
    private String nomineeCity2;
    private String nomineeCity3;
    private String nomineeState1;
    private String nomineeState2;
    private String nomineeState3;
    private String nomineeStateName1;
    private String nomineeStateName2;
    private String nomineeStateName3;
    private String nomineeCountry1;
    private String nomineeCountry2;
    private String nomineeCountry3;
    private String nomineeCountryName1;
    private String nomineeCountryName2;
    private String nomineeCountryName3;
    private String nomineePincode1;
    private String nomineePincode2;
    private String nomineePincode3;
    private String nomineeDocType1;
    private String nomineeDocType2;
    private String nomineeDocType3;
    private String nomineeDocNo1;
    private String nomineeDocNo2;
    private String nomineeDocNo3;

    // ─── Guardian ───
    private String guardianName;
    private String guardianRelation;
    private String guardian1;
    private String guardian2;
    private String guardian3;

    // ─── ECN Declaration ───
    private String ecnEmail;
    private String ecnSecondaryEmail;

    // ─── FATCA (Section 17) ───
    private String fatcaClientCode;
    private String fatcaCountryBirth;
    private String fatcaCountryBirthName;   // resolved country name
    private String fatcaCitizenship;
    private String fatcaCitizenshipName;    // resolved nationality name
    private String fatcaTaxResidence;
    private String fatcaTaxResidenceName;   // resolved country name
    private String fatcaUsPerson;
    private String fatcaTin;

    // ─── Aadhaar Consent (Section 18) ───
    private String aadhaarConsent;

    // ─── Acknowledgement ───
    private String ackApplicantName;

    // ─── DIS Option ───
    private String disOption;

    // ─── Related Persons / KYC ───
    private String additionRelatedPerson;
    private String deletionRelatedPerson;
    private String kycRelatedPerson;
    private String relatedPersonType;
    private String relatedPrefix;
    private String relatedFirstName;
    private String relatedMiddleName;
    private String relatedLastName;
    private String natureOfOrganisation;

    // ─── Remarks ───
    private String remarks;

    // ─── Investor meta ───
    private String investorUniqueCode;
    private String investorType;
    private String investorTypeId;      // numeric ID from master_investor_types

    // ─── Images (base64 encoded, set by service) ───
    private String profilePicBase64;
    private String signaturePicBase64;
    private String venturaLogoBase64;
    private String centralKycLogoBase64;
    private String checkmarkBase64;

    // ─── Current Date (formatted for template) ───
    private String currentDate;         // dd-MM-yyyy
    private String currentDateDmY;      // ddMMyyyy (for character boxes)

    // ─── Mapping metadata (returned in /data endpoint for React UI) ───
    private List<Map<String, Object>> sections;
}
