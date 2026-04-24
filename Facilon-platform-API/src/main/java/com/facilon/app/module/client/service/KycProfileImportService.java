package com.facilon.app.module.client.service;

import com.facilon.app.module.client.model.*;
import com.facilon.app.module.client.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Copies user-confirmed OCR fields into the investor's profile
 * (Investor + UserPersonalInformation) with per-field audit.
 *
 * Rules:
 *   - Non-blank user-typed data is NEVER silently overwritten. When OCR
 *     disagrees with an existing value, the import is SKIPPED and a conflict
 *     is returned so the UI can ask the investor to reconcile.
 *   - Fields flagged as discrepancies on the source document are skipped
 *     entirely - we only auto-populate validated, undisputed fields.
 *   - Stored name values are canonicalised (salutations stripped, whitespace
 *     normalised) so "MR. SAMANTA BHOI" lands as "SAMANTA BHOI" - matching
 *     what the validation engine already considers equivalent.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KycProfileImportService {

    private final KycDocumentFieldRepository fieldRepository;
    private final KycDocumentDiscrepancyRepository discrepancyRepository;
    private final UserPersonalInformationRepository personalInfoRepository;
    private final InvestorRepository investorRepository;
    private final KycProfileImportAuditRepository auditRepository;
    private final InvestorContactDetailsRepository contactDetailsRepository;
    private final InvestorBankDetailsRepository bankDetailsRepository;
    private final UserPassportDetailsRepository passportDetailsRepository;
    private final InvestorResidentialStatusRepository residentialStatusRepository;

    public record ImportResult(int imported, List<ImportConflict> conflicts) {}

    public record ImportConflict(String targetField, String existingValue,
                                 String documentValue, String reason) {}

    public ImportResult importFromDocument(KycDocuments doc) {
        List<KycDocumentField> fields = fieldRepository.findByKycDocumentId(doc.getId());
        if (fields.isEmpty()) {
            return new ImportResult(0, List.of());
        }
        Map<String, String> f = indexFields(fields);
        Set<String> disputed = disputedFieldNames(doc);

        // If the OCR-detected type itself disagrees with the selected type,
        // the whole document is suspect - import nothing.
        if (disputed.contains("document_type")) {
            log.info("KYC import: doc={} type={} skipped entirely (document_type disputed)",
                    doc.getId(), doc.getDocumentType());
            return new ImportResult(0, List.of(new ImportConflict(
                    "document_type", doc.getDocumentType(),
                    f.get("document_type"),
                    "OCR detected a different document type - not importing")));
        }

        List<ImportConflict> conflicts = new ArrayList<>();
        int imported = applyToPersonalInfo(doc, f, disputed, conflicts);
        imported += applyHomeAddress(doc, f, disputed, conflicts);
        imported += applyToBankDetails(doc, f, disputed, conflicts);
        imported += applyToPassportDetails(doc, f, disputed, conflicts);
        imported += applyToOciInfo(doc, f, disputed, conflicts);
        imported += applyToResidentialStatus(doc, f, disputed, conflicts);
        log.info("KYC import: doc={} type={} fields_imported={} conflicts={}",
                doc.getId(), doc.getDocumentType(), imported, conflicts.size());
        return new ImportResult(imported, conflicts);
    }

    // ---------- core ----------

    private int applyToPersonalInfo(KycDocuments doc, Map<String, String> f,
                                    Set<String> disputed, List<ImportConflict> conflicts) {
        Optional<UserPersonalInformation> opt =
                personalInfoRepository.findByInvestorUniqueId(doc.getInvestorUniqueId());
        if (opt.isEmpty()) return 0;
        UserPersonalInformation info = opt.get();
        int changes = 0;

        boolean nameDisputed = disputed.contains("name") || disputed.contains("full_name")
                || disputed.contains("given_name");
        boolean dobDisputed = disputed.contains("date_of_birth") || disputed.contains("dob");
        boolean genderDisputed = disputed.contains("gender");

        // Canonicalise extracted name parts (strip salutations, collapse whitespace).
        String firstName = KycNameNormalizer.canonical(
                firstNonBlank(f.get("first_name"), f.get("given_name")));
        String middleName = KycNameNormalizer.canonical(f.get("middle_name"));
        String lastName = KycNameNormalizer.canonical(
                firstNonBlank(f.get("last_name"), f.get("surname")));
        String gender = trimOrNull(f.get("gender"));
        LocalDate dob = parseDate(f.get("date_of_birth"));

        if (isIdentityDoc(doc.getDocumentType())) {
            if (!nameDisputed) {
                changes += tryImport(doc, "user_personal_information.investor_first_name",
                        info.getInvestorFirstName(), firstName,
                        info::setInvestorFirstName, conflicts, KycNameNormalizer::equivalent);
                changes += tryImport(doc, "user_personal_information.investor_middle_name",
                        info.getInvestorMiddleName(), middleName,
                        info::setInvestorMiddleName, conflicts, KycNameNormalizer::equivalent);
                changes += tryImport(doc, "user_personal_information.investor_last_name",
                        info.getInvestorLastName(), lastName,
                        info::setInvestorLastName, conflicts, KycNameNormalizer::equivalent);
            }
            if (!genderDisputed) {
                changes += tryImport(doc, "user_personal_information.investor_gender",
                        info.getInvestorGender(), gender,
                        info::setInvestorGender, conflicts,
                        (a, b) -> equivalentCaseInsensitive(a, b));
            }
            if (!dobDisputed && dob != null) {
                LocalDate existing = info.getUserDob();
                if (existing == null) {
                    audit(doc, "user_personal_information.user_dob", null, dob.toString());
                    info.setUserDob(dob);
                    changes++;
                } else if (!existing.equals(dob)) {
                    conflicts.add(new ImportConflict("user_personal_information.user_dob",
                            existing.toString(), dob.toString(),
                            "Your profile already has a different date of birth"));
                }
            }
        }

        if ("PAN_CARD".equals(doc.getDocumentType())
                && !disputed.contains("pan_number")) {
            changes += tryImport(doc, "user_personal_information.user_pan_no",
                    info.getUserPanNo(), trimOrNull(f.get("pan_number")),
                    info::setUserPanNo, conflicts,
                    (a, b) -> equivalentCaseInsensitive(a, b));
        }
        if ("AADHAR_CARD".equals(doc.getDocumentType())
                && !disputed.contains("aadhaar_number")) {
            changes += tryImport(doc, "user_personal_information.user_aadhar_no",
                    info.getUserAadharNo(), trimOrNull(f.get("aadhaar_number")),
                    info::setUserAadharNo, conflicts,
                    (a, b) -> equivalentCaseInsensitive(a, b));
        }

        // Father / Mother / Spouse — only fill from identity docs (PAN is the
        // usual source for father's name) and only when OCR actually returned
        // a value. Each relative name is split into first/middle/last either
        // from the pre-split fields ORCReader emits now, or by tokenising the
        // joined *_name field for older uploads.
        if (isIdentityDoc(doc.getDocumentType())) {
            changes += applyRelativeName(doc, f, disputed, conflicts, "father",
                    "user_personal_information.fathers_first_name",
                    "user_personal_information.fathers_middle_name",
                    "user_personal_information.fathers_last_name",
                    info::getFathersFirstName, info::setFathersFirstName,
                    info::getFathersMiddleName, info::setFathersMiddleName,
                    info::getFathersLastName, info::setFathersLastName);
            changes += applyRelativeName(doc, f, disputed, conflicts, "mother",
                    "user_personal_information.mother_first_name",
                    "user_personal_information.mother_middle_name",
                    "user_personal_information.mother_last_name",
                    info::getMotherFirstName, info::setMotherFirstName,
                    info::getMotherMiddleName, info::setMotherMiddleName,
                    info::getMotherLastName, info::setMotherLastName);
            // Spouse: profile stores the first name in spouse_name, not
            // spouse_first_name — match the actual schema in audit labels.
            changes += applyRelativeName(doc, f, disputed, conflicts, "spouse",
                    "user_personal_information.spouse_name",
                    "user_personal_information.spouse_middle_name",
                    "user_personal_information.spouse_last_name",
                    info::getSpouseName, info::setSpouseName,
                    info::getSpouseMiddleName, info::setSpouseMiddleName,
                    info::getSpouseLastName, info::setSpouseLastName);
        }

        if (changes > 0) personalInfoRepository.save(info);
        return changes;
    }

    /**
     * Resolve a relative's first/middle/last from the OCR field map and
     * tryImport each into the profile. Prefers pre-split fields (e.g.
     * father_first_name) and falls back to tokenising the joined
     * *_name field when the split fields are absent.
     *
     * @param ocrPrefix OCR field prefix: "father", "mother", or "spouse"
     * @param firstAuditLabel / middleAuditLabel / lastAuditLabel audit labels
     *        that match the actual DB columns (schemas differ — e.g. spouse
     *        uses "spouse_name" for first name, not "spouse_first_name")
     */
    private int applyRelativeName(KycDocuments doc, Map<String, String> f,
                                  Set<String> disputed, List<ImportConflict> conflicts,
                                  String ocrPrefix,
                                  String firstAuditLabel,
                                  String middleAuditLabel,
                                  String lastAuditLabel,
                                  java.util.function.Supplier<String> getFirst,
                                  java.util.function.Consumer<String> setFirst,
                                  java.util.function.Supplier<String> getMiddle,
                                  java.util.function.Consumer<String> setMiddle,
                                  java.util.function.Supplier<String> getLast,
                                  java.util.function.Consumer<String> setLast) {
        String joinedKey = ocrPrefix + "_name";
        if (disputed.contains(joinedKey)) return 0;

        String first = KycNameNormalizer.canonical(f.get(ocrPrefix + "_first_name"));
        String middle = KycNameNormalizer.canonical(f.get(ocrPrefix + "_middle_name"));
        String last = KycNameNormalizer.canonical(f.get(ocrPrefix + "_last_name"));

        // Fallback: split the joined name on whitespace when ORCReader didn't
        // pre-split it. Matches the subject-name split rule: first token →
        // first_name, last token → last_name, middle tokens → middle_name.
        if (first == null && middle == null && last == null) {
            String joined = KycNameNormalizer.canonical(f.get(joinedKey));
            if (joined == null || joined.isBlank()) return 0;
            String[] parts = joined.split("\\s+");
            if (parts.length == 1) {
                first = parts[0];
            } else if (parts.length == 2) {
                first = parts[0];
                last = parts[1];
            } else {
                first = parts[0];
                last = parts[parts.length - 1];
                StringBuilder mid = new StringBuilder();
                for (int i = 1; i < parts.length - 1; i++) {
                    if (mid.length() > 0) mid.append(' ');
                    mid.append(parts[i]);
                }
                middle = mid.toString();
            }
        }

        if (first == null && middle == null && last == null) return 0;

        int changes = 0;
        changes += tryImport(doc, firstAuditLabel,
                getFirst.get(), first, setFirst, conflicts, KycNameNormalizer::equivalent);
        changes += tryImport(doc, middleAuditLabel,
                getMiddle.get(), middle, setMiddle, conflicts, KycNameNormalizer::equivalent);
        changes += tryImport(doc, lastAuditLabel,
                getLast.get(), last, setLast, conflicts, KycNameNormalizer::equivalent);
        return changes;
    }

    private static final Set<String> ADDRESS_CAPABLE_DOCS = Set.of(
            "AADHAR_CARD", "ADDRESS_PROOF", "PASSPORT", "BANK_STATEMENT",
            "DRIVING_LICENSE", "VOTER_ID", "OCI_CARD");

    private int applyHomeAddress(KycDocuments doc, Map<String, String> f,
                                 Set<String> disputed, List<ImportConflict> conflicts) {
        if (!ADDRESS_CAPABLE_DOCS.contains(doc.getDocumentType())) return 0;
        if (disputed.contains("address")
                || disputed.contains("permanent_address")
                || disputed.contains("present_address")) return 0;

        AddressParser.ParsedAddress parsed = resolveParsedAddress(f);
        if (parsed == null || parsed.isEmpty()) return 0;
        log.info("KYC import: doc={} applying home address={}", doc.getId(), parsed);

        int changes = 0;
        changes += writeAddressToContactDetails(doc, parsed, conflicts);
        changes += writeAddressToPersonalInfo(doc, parsed, conflicts);
        return changes;
    }

    /**
     * Pick the home address out of the OCR field map. Prefers the already-
     * split fields persisted at upload time; falls back to parsing the joined
     * "permanent_address" / "address" / "present_address" blob otherwise.
     * Returns null when no address data is present.
     */
    private AddressParser.ParsedAddress resolveParsedAddress(Map<String, String> f) {
        String line1 = f.get("address_line1");
        String line2 = f.get("address_line2");
        String line3 = f.get("address_line3");
        String city = f.get("city");
        String state = f.get("state");
        String zip = firstNonBlank(f.get("zip_code"), f.get("pincode"), f.get("postal_code"));
        String country = f.get("country");

        boolean splitMissing = (line1 == null || line1.isBlank())
                && (city == null || city.isBlank())
                && (zip == null || zip.isBlank());
        if (splitMissing) {
            String raw = firstNonBlank(
                    f.get("permanent_address"),
                    f.get("address"),
                    f.get("present_address"));
            if (raw == null || raw.isBlank()) return null;
            AddressParser.ParsedAddress parsed = AddressParser.parse(raw);
            if (parsed.isEmpty()) return null;
            line1 = parsed.addressLine1();
            line2 = parsed.addressLine2();
            line3 = parsed.addressLine3();
            city = parsed.city();
            state = parsed.state();
            zip = parsed.zipCode();
            country = parsed.country();
        }
        return new AddressParser.ParsedAddress(
                line1, line2, line3, city, state,
                country == null || country.isBlank() ? "India" : country, zip);
    }

    private int writeAddressToContactDetails(KycDocuments doc,
                                             AddressParser.ParsedAddress parsed,
                                             List<ImportConflict> conflicts) {
        InvestorContactDetails contact = contactDetailsRepository
                .findByInvestorUniqueId(doc.getInvestorUniqueId())
                .orElseGet(() -> InvestorContactDetails.builder()
                        .investorUniqueId(doc.getInvestorUniqueId())
                        .build());

        int changes = 0;
        boolean linesPolluted = looksLikeBlobAddress(contact.getAddressLine1())
                || looksLikeGarbage(contact.getAddressLine2())
                || looksLikeGarbage(contact.getAddressLine3());
        if (linesPolluted) {
            log.info("KYC import: doc={} legacy pollution in investor_contact_details; overwriting line1/2/3",
                    doc.getId());
            changes += overwrite(doc, "investor_contact_details.address_line1",
                    contact.getAddressLine1(), parsed.addressLine1(), contact::setAddressLine1);
            changes += overwrite(doc, "investor_contact_details.address_line2",
                    contact.getAddressLine2(), parsed.addressLine2(), contact::setAddressLine2);
            changes += overwrite(doc, "investor_contact_details.address_line3",
                    contact.getAddressLine3(), parsed.addressLine3(), contact::setAddressLine3);
        } else {
            changes += tryImport(doc, "investor_contact_details.address_line1",
                    contact.getAddressLine1(), parsed.addressLine1(),
                    contact::setAddressLine1, conflicts, KycProfileImportService::equivalentCaseInsensitive);
            changes += tryImport(doc, "investor_contact_details.address_line2",
                    contact.getAddressLine2(), parsed.addressLine2(),
                    contact::setAddressLine2, conflicts, KycProfileImportService::equivalentCaseInsensitive);
            changes += tryImport(doc, "investor_contact_details.address_line3",
                    contact.getAddressLine3(), parsed.addressLine3(),
                    contact::setAddressLine3, conflicts, KycProfileImportService::equivalentCaseInsensitive);
        }
        changes += tryImport(doc, "investor_contact_details.user_city",
                contact.getUserCity(), parsed.city(),
                contact::setUserCity, conflicts, KycProfileImportService::equivalentCaseInsensitive);
        changes += tryImport(doc, "investor_contact_details.user_state",
                contact.getUserState(), parsed.state(),
                contact::setUserState, conflicts, KycProfileImportService::equivalentCaseInsensitive);
        changes += tryImport(doc, "investor_contact_details.user_zip_code",
                contact.getUserZipCode(), parsed.zipCode(),
                contact::setUserZipCode, conflicts, KycProfileImportService::equivalentCaseInsensitive);
        changes += tryImport(doc, "investor_contact_details.user_country",
                contact.getUserCountry(), parsed.country(),
                contact::setUserCountry, conflicts, KycProfileImportService::equivalentCaseInsensitive);

        if (changes > 0) contactDetailsRepository.save(contact);
        return changes;
    }

    /**
     * user_personal_information duplicates the address columns that also
     * live on investor_contact_details. The profile UI reads both tables
     * (different tabs), so to keep them in sync we mirror the parsed address
     * here as well. Same pollution carve-out as contact details.
     */
    private int writeAddressToPersonalInfo(KycDocuments doc,
                                           AddressParser.ParsedAddress parsed,
                                           List<ImportConflict> conflicts) {
        Optional<UserPersonalInformation> opt =
                personalInfoRepository.findByInvestorUniqueId(doc.getInvestorUniqueId());
        if (opt.isEmpty()) return 0;
        UserPersonalInformation info = opt.get();

        int changes = 0;
        boolean linesPolluted = looksLikeBlobAddress(info.getAddressLine1())
                || looksLikeGarbage(info.getAddressLine2())
                || looksLikeGarbage(info.getAddressLine3());
        if (linesPolluted) {
            log.info("KYC import: doc={} legacy pollution in user_personal_information; overwriting line1/2/3",
                    doc.getId());
            changes += overwrite(doc, "user_personal_information.address_line1",
                    info.getAddressLine1(), parsed.addressLine1(), info::setAddressLine1);
            changes += overwrite(doc, "user_personal_information.address_line2",
                    info.getAddressLine2(), parsed.addressLine2(), info::setAddressLine2);
            changes += overwrite(doc, "user_personal_information.address_line3",
                    info.getAddressLine3(), parsed.addressLine3(), info::setAddressLine3);
        } else {
            changes += tryImport(doc, "user_personal_information.address_line1",
                    info.getAddressLine1(), parsed.addressLine1(),
                    info::setAddressLine1, conflicts, KycProfileImportService::equivalentCaseInsensitive);
            changes += tryImport(doc, "user_personal_information.address_line2",
                    info.getAddressLine2(), parsed.addressLine2(),
                    info::setAddressLine2, conflicts, KycProfileImportService::equivalentCaseInsensitive);
            changes += tryImport(doc, "user_personal_information.address_line3",
                    info.getAddressLine3(), parsed.addressLine3(),
                    info::setAddressLine3, conflicts, KycProfileImportService::equivalentCaseInsensitive);
        }
        changes += tryImport(doc, "user_personal_information.user_city",
                info.getUserCity(), parsed.city(),
                info::setUserCity, conflicts, KycProfileImportService::equivalentCaseInsensitive);
        changes += tryImport(doc, "user_personal_information.user_state",
                info.getUserState(), parsed.state(),
                info::setUserState, conflicts, KycProfileImportService::equivalentCaseInsensitive);
        changes += tryImport(doc, "user_personal_information.user_zip_code",
                info.getUserZipCode(), parsed.zipCode(),
                info::setUserZipCode, conflicts, KycProfileImportService::equivalentCaseInsensitive);
        changes += tryImport(doc, "user_personal_information.user_country",
                info.getUserCountry(), parsed.country(),
                info::setUserCountry, conflicts, KycProfileImportService::equivalentCaseInsensitive);

        if (changes > 0) personalInfoRepository.save(info);
        return changes;
    }

    /**
     * Populate investor_bank_details from a Bank Statement OCR. Only triggers
     * for BANK_STATEMENT document type. Writes the PRIMARY bank row (creating
     * it if absent). The bank branch address on this table is the BANK's
     * address, not the investor's home address — it is sourced from separate
     * OCR fields (bank branch address fields), not from permanent_address.
     */
    private int applyToBankDetails(KycDocuments doc, Map<String, String> f,
                                   Set<String> disputed, List<ImportConflict> conflicts) {
        if (!"BANK_STATEMENT".equals(doc.getDocumentType())) return 0;

        String accountHolder = trimOrNull(firstNonBlank(
                f.get("account_holder_name"), f.get("beneficiary_name"), f.get("name")));
        String accountNumber = trimOrNull(firstNonBlank(
                f.get("account_number"), f.get("acc_no"), f.get("account_no"),
                f.get("bank_account_number")));
        String ifsc = trimOrNull(firstNonBlank(
                f.get("ifsc_code"), f.get("ifsc"), f.get("bank_ifsc")));
        String bankName = trimOrNull(f.get("bank_name"));
        String branchName = trimOrNull(f.get("branch_name"));
        String accountType = trimOrNull(f.get("account_type"));
        String micr = trimOrNull(firstNonBlank(f.get("micr"), f.get("micr_code")));
        String branchAddress = trimOrNull(firstNonBlank(
                f.get("bank_address"), f.get("branch_address")));

        boolean anyField = accountHolder != null || accountNumber != null || ifsc != null
                || bankName != null || branchName != null || branchAddress != null;
        if (!anyField) return 0;

        InvestorBankDetails bank = bankDetailsRepository
                .findByInvestorUniqueIdAndIsPrimaryTrue(doc.getInvestorUniqueId())
                .or(() -> bankDetailsRepository.findFirstByInvestorUniqueId(doc.getInvestorUniqueId()))
                .orElseGet(() -> InvestorBankDetails.builder()
                        .investorUniqueId(doc.getInvestorUniqueId())
                        .isPrimary(true)
                        .build());

        log.info("KYC import: doc={} applying bank details", doc.getId());

        int changes = 0;
        if (!disputed.contains("account_holder_name") && !disputed.contains("beneficiary_name")) {
            changes += tryImport(doc, "investor_bank_details.account_holder_name",
                    bank.getAccountHolderName(), accountHolder,
                    bank::setAccountHolderName, conflicts, KycNameNormalizer::equivalent);
        }
        if (!disputed.contains("account_number")) {
            changes += tryImport(doc, "investor_bank_details.account_number",
                    bank.getAccountNumber(), accountNumber,
                    bank::setAccountNumber, conflicts, KycProfileImportService::equivalentCaseInsensitive);
        }
        if (!disputed.contains("ifsc_code") && !disputed.contains("ifsc")) {
            changes += tryImport(doc, "investor_bank_details.ifsc_code",
                    bank.getIfscCode(), ifsc,
                    bank::setIfscCode, conflicts, KycProfileImportService::equivalentCaseInsensitive);
        }
        if (!disputed.contains("bank_name")) {
            changes += tryImport(doc, "investor_bank_details.bank_name",
                    bank.getBankName(), bankName,
                    bank::setBankName, conflicts, KycProfileImportService::equivalentCaseInsensitive);
        }
        if (!disputed.contains("branch_name")) {
            changes += tryImport(doc, "investor_bank_details.branch_name",
                    bank.getBranchName(), branchName,
                    bank::setBranchName, conflicts, KycProfileImportService::equivalentCaseInsensitive);
        }
        if (accountType != null && !disputed.contains("account_type")) {
            changes += tryImport(doc, "investor_bank_details.account_type",
                    bank.getAccountType(), accountType,
                    bank::setAccountType, conflicts, KycProfileImportService::equivalentCaseInsensitive);
        }
        if (micr != null) {
            changes += tryImport(doc, "investor_bank_details.bank_details_micr",
                    bank.getBankDetailsMicr(), micr,
                    bank::setBankDetailsMicr, conflicts, KycProfileImportService::equivalentCaseInsensitive);
        }
        if (branchAddress != null) {
            changes += tryImport(doc, "investor_bank_details.bank_address",
                    bank.getBankAddress(), branchAddress,
                    bank::setBankAddress, conflicts, KycProfileImportService::equivalentCaseInsensitive);
        }

        if (changes > 0) bankDetailsRepository.save(bank);
        return changes;
    }

    /**
     * Populate user_passport_details from a Passport OCR. Only triggers for
     * PASSPORT document type. Creates the row if absent. Names are already
     * handled by applyToPersonalInfo; this method only handles the travel-
     * document-specific fields.
     */
    private int applyToPassportDetails(KycDocuments doc, Map<String, String> f,
                                       Set<String> disputed, List<ImportConflict> conflicts) {
        if (!"PASSPORT".equals(doc.getDocumentType())) return 0;

        String passportNumber = trimOrNull(firstNonBlank(
                f.get("passport_number"), f.get("passport_no"), f.get("document_number")));
        LocalDate issueDate = parseDate(firstNonBlank(
                f.get("date_of_issue"), f.get("issue_date"), f.get("passport_issue_date")));
        LocalDate expiryDate = parseDate(firstNonBlank(
                f.get("date_of_expiry"), f.get("expiry_date"), f.get("valid_until"),
                f.get("valid_to"), f.get("passport_expiry_date")));
        String placeOfIssue = trimOrNull(firstNonBlank(
                f.get("place_of_issue"), f.get("passport_place_of_issue")));
        String countryOfIssue = trimOrNull(firstNonBlank(
                f.get("country_of_issue"), f.get("passport_country_of_issue"), f.get("issuing_authority")));
        String nationality = trimOrNull(firstNonBlank(
                f.get("nationality"), f.get("passport_nationality")));

        boolean anyField = passportNumber != null || issueDate != null || expiryDate != null
                || placeOfIssue != null || countryOfIssue != null || nationality != null;
        if (!anyField) return 0;

        UserPassportDetails passport = passportDetailsRepository
                .findByInvestorUniqueCode(doc.getInvestorUniqueId())
                .orElseGet(() -> UserPassportDetails.builder()
                        .investorUniqueCode(doc.getInvestorUniqueId())
                        .build());

        log.info("KYC import: doc={} applying passport details", doc.getId());

        int changes = 0;
        if (!disputed.contains("passport_number") && !disputed.contains("document_number")) {
            changes += tryImport(doc, "user_passport_details.passport_number",
                    passport.getPassportNumber(), passportNumber,
                    passport::setPassportNumber, conflicts, KycProfileImportService::equivalentCaseInsensitive);
        }
        if (!disputed.contains("date_of_issue") && !disputed.contains("issue_date") && issueDate != null) {
            changes += tryImportDate(doc, "user_passport_details.passport_issue_date",
                    passport.getPassportIssueDate(), issueDate,
                    passport::setPassportIssueDate, conflicts);
        }
        if (!disputed.contains("date_of_expiry") && !disputed.contains("expiry_date")
                && !disputed.contains("valid_until") && expiryDate != null) {
            changes += tryImportDate(doc, "user_passport_details.passport_expiry_date",
                    passport.getPassportExpiryDate(), expiryDate,
                    passport::setPassportExpiryDate, conflicts);
        }
        if (!disputed.contains("place_of_issue")) {
            changes += tryImport(doc, "user_passport_details.passport_place_of_issue",
                    passport.getPassportPlaceOfIssue(), placeOfIssue,
                    passport::setPassportPlaceOfIssue, conflicts, KycProfileImportService::equivalentCaseInsensitive);
        }
        if (!disputed.contains("country_of_issue")) {
            changes += tryImport(doc, "user_passport_details.passport_country_of_issue",
                    passport.getPassportCountryOfIssue(), countryOfIssue,
                    passport::setPassportCountryOfIssue, conflicts, KycProfileImportService::equivalentCaseInsensitive);
        }
        if (!disputed.contains("nationality")) {
            changes += tryImport(doc, "user_passport_details.passport_nationality",
                    passport.getPassportNationality(), nationality,
                    passport::setPassportNationality, conflicts, KycProfileImportService::equivalentCaseInsensitive);
        }

        if (changes > 0) passportDetailsRepository.save(passport);
        return changes;
    }

    /**
     * Populate OCI columns on user_personal_information from an OCI_CARD OCR.
     * The OCI section of the profile lives on user_personal_information (not
     * a dedicated table), so this reuses that row. `oci_available` is set to
     * "Yes" when we have any OCI data to write.
     */
    private int applyToOciInfo(KycDocuments doc, Map<String, String> f,
                               Set<String> disputed, List<ImportConflict> conflicts) {
        if (!"OCI_CARD".equals(doc.getDocumentType())) return 0;

        String ociNumber = trimOrNull(firstNonBlank(
                f.get("oci_card_no"), f.get("oci_number"), f.get("oci_card_number"),
                f.get("document_number")));
        LocalDate ociIssueDate = parseDate(firstNonBlank(
                f.get("date_of_issue"), f.get("issue_date"), f.get("oci_issue_date")));
        LocalDate ociValidUpto = parseDate(firstNonBlank(
                f.get("valid_until"), f.get("date_of_expiry"), f.get("expiry_date"),
                f.get("valid_to"), f.get("oci_valid_upto")));

        boolean anyField = ociNumber != null || ociIssueDate != null || ociValidUpto != null;
        if (!anyField) return 0;

        Optional<UserPersonalInformation> opt =
                personalInfoRepository.findByInvestorUniqueId(doc.getInvestorUniqueId());
        if (opt.isEmpty()) return 0;
        UserPersonalInformation info = opt.get();

        log.info("KYC import: doc={} applying OCI details", doc.getId());

        int changes = 0;
        if (!disputed.contains("oci_card_no") && !disputed.contains("document_number")) {
            changes += tryImport(doc, "user_personal_information.user_oci_card_no",
                    info.getUserOciCardNo(), ociNumber,
                    info::setUserOciCardNo, conflicts, KycProfileImportService::equivalentCaseInsensitive);
        }
        if (!disputed.contains("date_of_issue") && !disputed.contains("issue_date") && ociIssueDate != null) {
            changes += tryImportDate(doc, "user_personal_information.user_oci_issue_date",
                    info.getUserOciIssueDate(), ociIssueDate,
                    info::setUserOciIssueDate, conflicts);
            // date_of_oci mirrors the issue date on the profile form.
            changes += tryImportDate(doc, "user_personal_information.date_of_oci",
                    info.getDateOfOci(), ociIssueDate,
                    info::setDateOfOci, conflicts);
        }
        if (!disputed.contains("valid_until") && !disputed.contains("expiry_date")
                && !disputed.contains("date_of_expiry") && ociValidUpto != null) {
            changes += tryImportDate(doc, "user_personal_information.user_oci_valid_upto",
                    info.getUserOciValidUpto(), ociValidUpto,
                    info::setUserOciValidUpto, conflicts);
        }
        if (changes > 0) {
            // Confirm the "OCI available" flag so the residential status tab
            // reflects the uploaded card. Only set when still blank — never
            // flip an investor's explicit No to Yes.
            if (info.getOciAvailable() == null || info.getOciAvailable().isBlank()) {
                audit(doc, "user_personal_information.oci_available", null, "Yes");
                info.setOciAvailable("Yes");
                changes++;
            }
            personalInfoRepository.save(info);
        }
        return changes;
    }

    /**
     * Populate the Aadhaar fields on investor_residential_status from an
     * AADHAR_CARD OCR so the RI Aadhaar Details tab is pre-filled after
     * confirm. Writes Aadhaar number (duplicated across aadhar_number and
     * user_aadhar_no to match the V10 parity columns) plus the printed
     * "Name on Aadhaar". Residential-status enum field is only touched
     * when the investor is RI and the column is blank — never overwrites
     * an explicit NRI / OCI / Foreign-National selection.
     */
    private int applyToResidentialStatus(KycDocuments doc, Map<String, String> f,
                                         Set<String> disputed, List<ImportConflict> conflicts) {
        if (!"AADHAR_CARD".equals(doc.getDocumentType())) return 0;

        String aadhaar = trimOrNull(firstNonBlank(
                f.get("aadhaar_number"), f.get("aadhar_number"), f.get("document_number")));
        String nameOnAadhaar = trimOrNull(firstNonBlank(
                f.get("name_on_aadhaar"), f.get("full_name"), f.get("name")));
        if (nameOnAadhaar == null) {
            // Reconstruct from split pieces when full_name wasn't emitted.
            String first = trimOrNull(firstNonBlank(f.get("first_name"), f.get("given_name")));
            String middle = trimOrNull(f.get("middle_name"));
            String last = trimOrNull(firstNonBlank(f.get("last_name"), f.get("surname")));
            StringBuilder sb = new StringBuilder();
            if (first != null) sb.append(first);
            if (middle != null) {
                if (sb.length() > 0) sb.append(' ');
                sb.append(middle);
            }
            if (last != null) {
                if (sb.length() > 0) sb.append(' ');
                sb.append(last);
            }
            if (sb.length() > 0) nameOnAadhaar = sb.toString();
        }

        boolean anyField = aadhaar != null || nameOnAadhaar != null;
        if (!anyField) return 0;

        InvestorResidentialStatus status = residentialStatusRepository
                .findByInvestorUniqueId(doc.getInvestorUniqueId())
                .orElseGet(() -> InvestorResidentialStatus.builder()
                        .investorUniqueId(doc.getInvestorUniqueId())
                        .build());

        log.info("KYC import: doc={} applying Aadhaar details to investor_residential_status", doc.getId());

        int changes = 0;
        if (!disputed.contains("aadhaar_number") && !disputed.contains("aadhar_number")) {
            changes += tryImport(doc, "investor_residential_status.aadhar_number",
                    status.getAadharNumber(), aadhaar,
                    status::setAadharNumber, conflicts, KycProfileImportService::equivalentCaseInsensitive);
            changes += tryImport(doc, "investor_residential_status.user_aadhar_no",
                    status.getUserAadharNo(), aadhaar,
                    status::setUserAadharNo, conflicts, KycProfileImportService::equivalentCaseInsensitive);
        }
        if (!disputed.contains("full_name") && !disputed.contains("name")) {
            changes += tryImport(doc, "investor_residential_status.name_on_aadhaar",
                    status.getNameOnAadhaar(), nameOnAadhaar,
                    status::setNameOnAadhaar, conflicts, KycNameNormalizer::equivalent);
        }
        if (changes > 0) {
            // Flip "Do you have Aadhaar?" to yes if still blank, since we
            // just imported Aadhaar data. Never flip an explicit No.
            if (status.getAadharNumberOption() == null || status.getAadharNumberOption().isBlank()) {
                audit(doc, "investor_residential_status.aadhar_number_option", null, "yes");
                status.setAadharNumberOption("yes");
                changes++;
            }
            // For RI, stamp residential_status = "Resident Indian" when blank
            // so the RI Aadhaar Details tab saves with the correct value on
            // the first Aadhaar confirmation — mirrors the force-stamp in
            // ClientProfileService.updateResidentialStatus.
            Investor investor = investorRepository.findByUniqueCode(doc.getInvestorUniqueId()).orElse(null);
            if (investor != null && "RESIDENT_INDIVIDUAL".equalsIgnoreCase(investor.getInvestorType())
                    && (status.getResidentialStatus() == null || status.getResidentialStatus().isBlank())) {
                audit(doc, "investor_residential_status.residential_status", null, "Resident Indian");
                status.setResidentialStatus("Resident Indian");
                changes++;
            }
            residentialStatusRepository.save(status);
        }
        return changes;
    }

    /**
     * Date-typed variant of {@link #tryImport} — the equivalence check is
     * {@code LocalDate#equals} so 2020-01-01 from OCR matches 2020-01-01 on
     * the profile without stringification.
     */
    private int tryImportDate(KycDocuments doc, String targetField,
                              LocalDate existing, LocalDate newValue,
                              java.util.function.Consumer<LocalDate> setter,
                              List<ImportConflict> conflicts) {
        if (newValue == null) return 0;
        if (existing == null) {
            audit(doc, targetField, null, newValue.toString());
            setter.accept(newValue);
            return 1;
        }
        if (existing.equals(newValue)) return 0;
        conflicts.add(new ImportConflict(targetField, existing.toString(), newValue.toString(),
                "Your profile already has a different date"));
        return 0;
    }

    /**
     * Heuristic: address_line1 typically holds a single street line like
     * "Flat 302, Jamnagar Society" or "NEAR JUHU CIRCLE" — short, at most one
     * comma. When it contains two or more commas or runs longer than 60
     * characters, it was almost certainly the entire OCR'd address pasted
     * into line1 (legacy behaviour before the address parser existed).
     */
    private static boolean looksLikeBlobAddress(String line1) {
        if (line1 == null || line1.isBlank()) return false;
        String s = line1.trim();
        long commas = s.chars().filter(c -> c == ',').count();
        return commas >= 2 || s.length() > 60;
    }

    /**
     * Heuristic: line2/line3 are either blank, or real fragments like
     * "BARIKA TIKRA" (has spaces) or "Sarsara" (contains vowels). Short
     * vowel-free strings such as "czs", "sdcs", "jhkj", "kjkj" are keyboard
     * noise pasted in to bypass form validation.
     */
    private static boolean looksLikeGarbage(String v) {
        if (v == null || v.isBlank()) return false;
        String s = v.trim();
        if (s.length() > 6) return false;
        if (s.contains(" ") || s.contains(",")) return false;
        return !s.toLowerCase(Locale.ROOT).matches(".*[aeiou].*");
    }

    /**
     * Unconditional replace used by the Phase-C pollution carve-out. Audits
     * the change like a normal import but skips the "don't clobber typed
     * data" guard — the caller has already decided the existing value is
     * legacy garbage worth replacing.
     */
    private int overwrite(KycDocuments doc, String targetField,
                          String existing, String newValue,
                          java.util.function.Consumer<String> setter) {
        if (newValue == null || newValue.isBlank()) return 0;
        if (equivalentCaseInsensitive(existing, newValue)) return 0;
        audit(doc, targetField, existing, newValue);
        setter.accept(newValue);
        return 1;
    }

    /**
     * Attempt to import one field. Three outcomes:
     *   - newValue is blank                              -> no-op (return 0)
     *   - existing is blank                              -> import, audit, return 1
     *   - existing equivalent to newValue (per matcher)  -> no-op (return 0)
     *   - existing non-blank AND differs                 -> SKIP + conflict (return 0)
     */
    private int tryImport(KycDocuments doc, String targetField,
                          String existing, String newValue,
                          java.util.function.Consumer<String> setter,
                          List<ImportConflict> conflicts,
                          java.util.function.BiPredicate<String, String> matcher) {
        if (newValue == null || newValue.isBlank()) return 0;
        if (existing == null || existing.isBlank()) {
            audit(doc, targetField, existing, newValue);
            setter.accept(newValue);
            return 1;
        }
        if (matcher.test(existing, newValue)) return 0;
        conflicts.add(new ImportConflict(targetField, existing, newValue,
                "Your profile already has a different value - please reconcile"));
        return 0;
    }

    // ---------- helpers ----------

    private Set<String> disputedFieldNames(KycDocuments doc) {
        Set<String> names = new HashSet<>();
        List<KycDocumentDiscrepancy> rows = discrepancyRepository.findByKycDocumentId(doc.getId());
        for (KycDocumentDiscrepancy d : rows) {
            // Both BLOCKING and WARNING discrepancies are treated as "do not import".
            // OCR_INFRA is about the reader, not a field conflict - ignore.
            if (KycValidationEngine.SRC_OCR_INFRA.equals(d.getCanonicalSource())) continue;
            if (d.getFieldName() != null) {
                names.add(d.getFieldName().toLowerCase(Locale.ROOT));
            }
        }
        return names;
    }

    private boolean isIdentityDoc(String type) {
        return "PAN_CARD".equals(type) || "PASSPORT".equals(type)
                || "AADHAR_CARD".equals(type) || "OCI_CARD".equals(type);
    }

    private static boolean equivalentCaseInsensitive(String a, String b) {
        if (a == null || b == null) return a == b;
        return a.trim().equalsIgnoreCase(b.trim());
    }

    private void audit(KycDocuments doc, String targetField, String oldValue, String newValue) {
        auditRepository.save(KycProfileImportAudit.builder()
                .investorUniqueId(doc.getInvestorUniqueId())
                .sourceDocumentId(doc.getId())
                .sourceDocumentType(doc.getDocumentType())
                .targetField(targetField)
                .oldValue(oldValue)
                .newValue(newValue)
                .build());
    }

    private Map<String, String> indexFields(List<KycDocumentField> fields) {
        Map<String, String> m = new HashMap<>();
        for (KycDocumentField kf : fields) {
            if (kf.getFieldName() != null && kf.getFieldValue() != null) {
                m.put(kf.getFieldName().toLowerCase(Locale.ROOT), kf.getFieldValue());
            }
        }
        return m;
    }

    private LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String t = raw.trim();
        for (String p : new String[]{"yyyy-MM-dd", "dd/MM/yyyy", "dd-MM-yyyy",
                "dd MMM yyyy", "MM/dd/yyyy"}) {
            try { return LocalDate.parse(t, DateTimeFormatter.ofPattern(p)); }
            catch (Exception ignored) { }
        }
        return null;
    }

    private String firstNonBlank(String... vs) {
        for (String v : vs) if (v != null && !v.isBlank()) return v;
        return null;
    }

    private String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
