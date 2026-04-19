package com.facilon.app.module.client.service.pdf;

import com.facilon.app.module.client.dto.KycFormDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Expands a flat {@link KycFormDataDto} into the ~800-field PDF-widget-keyed map
 * that JAVAPDFill's {@code /api/v2/fill} endpoint consumes for the Ventura NRI
 * Account Opening booklet.
 *
 * <h3>Where the mapping lives</h3>
 * Simple cases (direct copies, per-char splits, date splits, static constants) are
 * externalised in {@code kyc-form-mapping-nri.json} and loaded by
 * {@link KycFormMappingLoader}. Non-developers can audit those without reading Java.
 *
 * <p>Complex cases — checkbox-set expansion (gender, marital, citizenship,
 * residential status, occupation, POI/POA doc type, income bracket, PEP, demat
 * account type) and NRI-specific overrides — stay in this file because they
 * need value-matching logic the JSON schema cannot express.
 *
 * <p>Does NOT touch the database — consumes only the DTO that
 * {@code KycPdfService.buildFormData} already produced.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KycFormFieldExpander {

    private static final DateTimeFormatter DMY = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final String FIELD_TRUE = "1";

    private final KycFormMappingLoader mappingLoader;

    /** Produce the full PDF-keyed field map. Never returns null; unmapped widgets simply stay /Off. */
    public Map<String, Object> expand(KycFormDataDto d) {
        Map<String, Object> m = new LinkedHashMap<>();
        BeanWrapper bw = new BeanWrapperImpl(d);
        KycFormMapping mapping = mappingLoader.getMapping();

        // ─── JSON-driven simple mappings ──────────────────────────────
        applyDirect(m, bw, mapping);
        applyMultiDirect(m, bw, mapping);
        applyDateSplits(m, bw, mapping);
        applyCharSplits(m, bw, mapping);
        applyConstants(m, mapping);

        // ─── Java-driven complex logic ────────────────────────────────
        applyCheckboxSetsFirstHolder(m, d);
        applyCheckboxSetsTrading(m, d);
        applyCheckboxSetsDemat(m, d);
        applyPoi(m, d);
        applyPoa(m, d);
        applyAddressType(m, d);
        applyConditionals(m, d);
        applyFatcaDerived(m, d);

        log.debug("KycFormFieldExpander produced {} field entries", m.size());
        return m;
    }

    // ─────────────────────────────────────────────────────────────────
    // JSON-driven mapping appliers
    // ─────────────────────────────────────────────────────────────────

    private void applyDirect(Map<String, Object> m, BeanWrapper bw, KycFormMapping mapping) {
        for (var e : mapping.getDirect().entrySet()) {
            m.put(e.getValue(), nz(readString(bw, e.getKey())));
        }
    }

    private void applyMultiDirect(Map<String, Object> m, BeanWrapper bw, KycFormMapping mapping) {
        for (var e : mapping.getMultiDirect().entrySet()) {
            String dtoKey = stripAliasSuffix(e.getKey());
            String value = nz(readString(bw, dtoKey));
            for (String widget : e.getValue()) {
                m.put(widget, value);
            }
        }
    }

    private void applyDateSplits(Map<String, Object> m, BeanWrapper bw, KycFormMapping mapping) {
        for (var e : mapping.getDateSplits().entrySet()) {
            String value = readString(bw, e.getKey());
            if (value == null || value.isBlank()) continue;
            try {
                LocalDate dt = LocalDate.parse(value.trim(), DMY);
                String dd = String.format("%02d", dt.getDayOfMonth());
                String mm = String.format("%02d", dt.getMonthValue());
                String yyyy = String.valueOf(dt.getYear());
                for (List<String> triplet : e.getValue()) {
                    if (triplet.size() >= 1 && triplet.get(0) != null) m.put(triplet.get(0), dd);
                    if (triplet.size() >= 2 && triplet.get(1) != null) m.put(triplet.get(1), mm);
                    if (triplet.size() >= 3 && triplet.get(2) != null) m.put(triplet.get(2), yyyy);
                }
            } catch (Exception ex) {
                log.debug("dateSplits: could not parse '{}' for key {}: {}", value, e.getKey(), ex.getMessage());
            }
        }
    }

    private void applyCharSplits(Map<String, Object> m, BeanWrapper bw, KycFormMapping mapping) {
        for (var e : mapping.getCharSplits().entrySet()) {
            String value = nz(readString(bw, e.getKey()));
            for (KycFormMapping.CharSplit split : e.getValue()) {
                for (int i = 0; i < split.getCount(); i++) {
                    String key = split.getPrefix() + String.format("%02d", i + 1);
                    m.put(key, i < value.length() ? String.valueOf(value.charAt(i)) : "");
                }
            }
        }
    }

    private void applyConstants(Map<String, Object> m, KycFormMapping mapping) {
        m.putAll(mapping.getConstants());
    }

    // ─────────────────────────────────────────────────────────────────
    // Java-driven checkbox-set expansions (first holder)
    // ─────────────────────────────────────────────────────────────────

    private void applyCheckboxSetsFirstHolder(Map<String, Object> m, KycFormDataDto d) {
        // Gender
        switch (normalizeGender(d.getGender())) {
            case "male" -> m.put("first_holder_gender_male", FIELD_TRUE);
            case "female" -> m.put("first_holder_gender_female", FIELD_TRUE);
            case "transgender" -> m.put("first_holder_gender_transgender", FIELD_TRUE);
            default -> {}
        }

        // Marital — KycPdfService.mapMaritalStatus normalises to "Married"/"Unmarried"/"Others"
        switch (nz(d.getMaritalStatus()).toLowerCase()) {
            case "married" -> m.put("first_holder_marital_married", FIELD_TRUE);
            case "unmarried" -> m.put("first_holder_marital_unmarried", FIELD_TRUE);
            case "others" -> m.put("first_holder_marital_others", FIELD_TRUE);
            default -> {}
        }

        // Citizenship
        if (isIndianCitizen(d)) {
            m.put("first_holder_citizenship_indian", FIELD_TRUE);
        } else {
            m.put("first_holder_citizenship_others", FIELD_TRUE);
            m.put("first_holder_country_code_1", nz(d.getCitizenshipIsoCode()));
        }

        // Residential status — NRI-aware
        applyResidentialStatus(m, d);

        // Occupation checkbox set
        applyOccupation(m, d, "first_holder_");
    }

    private void applyCheckboxSetsTrading(Map<String, Object> m, KycFormDataDto d) {
        applyIncomeBracket(m, d.getIncomeRange(), "trading_gross_income_");
        applyOccupation(m, d, "trading_");
        applyPep(m, d);

        String exp = nz(d.getInvestmentExperienceYears());
        if (exp.isBlank() || "0".equals(exp)) {
            m.put("trading_no_prior_experience_checkbox", FIELD_TRUE);
        }
    }

    private void applyCheckboxSetsDemat(Map<String, Object> m, KycFormDataDto d) {
        // Demat account type — NRI-repatriable override for NRI investors
        switch (resolveDematAccountType(d)) {
            case "nri_repatriable" -> m.put("demat_account_type_nri_repatriable", FIELD_TRUE);
            case "nri_non_repatriable" -> m.put("demat_account_type_nri_non_repatriable", FIELD_TRUE);
            case "foreign_national" -> m.put("demat_account_type_foreign_national", FIELD_TRUE);
            default -> m.put("demat_account_type_ordinary", FIELD_TRUE);
        }

        applyIncomeBracket(m, d.getIncomeRange(), "demat_annual_income_");

        String pep = nz(d.getPepStatus()).toLowerCase();
        if ("pep".equals(pep)) m.put("demat_politically_exposed_person", FIELD_TRUE);
        else if ("related to pep".equals(pep)) m.put("demat_related_to_politically_exposed_person", FIELD_TRUE);

        applyOccupation(m, d, "demat_sole_first_holder_");

        // Demat occupation text fields
        m.put("demat_sole_first_holder_occupation_name", nz(d.getOccupationType()));
        m.put("demat_sole_first_holder_occupation_brief_details", nz(d.getOccupationType()));

        // Demat bank account type
        String bat = nz(d.getBankAccountType()).toLowerCase();
        if (bat.contains("saving")) m.put("demat_bank_account_type_savings", FIELD_TRUE);
        else if (bat.contains("current")) m.put("demat_bank_account_type_current", FIELD_TRUE);
        else if (!bat.isBlank()) m.put("demat_bank_account_type_others", FIELD_TRUE);
    }

    // ─────────────────────────────────────────────────────────────────
    // Java-driven conditional / derived fields
    // ─────────────────────────────────────────────────────────────────

    private void applyConditionals(Map<String, Object> m, KycFormDataDto d) {
        // Tax residence outside India
        if (hasForeignTaxResidence(d)) {
            m.put("first_holder_tax_residence_outside_india", FIELD_TRUE);
            m.put("first_holder_additional_details_required", FIELD_TRUE);
        }

        // Correspondence address — line 3 composite + same-as flag
        m.put("first_holder_kyc_corres_line_3",
                compose(d.getCorrCity(), d.getCorrState(), d.getCorrPinCode()));
        String sameAs = nz(d.getCorrSameAsPerm());
        if ("1".equals(sameAs) || "true".equalsIgnoreCase(sameAs)) {
            m.put("first_holder_correspondence_same_as_address", FIELD_TRUE);
        }

        // Office use — IPV flags + verification metadata
        if (!nz(d.getIpvDoneBy()).isBlank()) {
            m.put("first_holder_office_in_person_verification", FIELD_TRUE);
            m.put("first_holder_office_client_interviewed", FIELD_TRUE);
            m.put("first_holder_office_certified_copies", FIELD_TRUE);
            m.put("first_holder_office_employee_name", nz(d.getIpvDoneBy()));
            splitDateDirect(m, d.getIpvDate(),
                    "first_holder_office_kyc_verification_date_dd",
                    "first_holder_office_kyc_verification_date_mm",
                    "first_holder_office_kyc_verification_date_yyyy");
        }

        // Demat signature date (2-char split: DD only)
        splitDateDirect(m, d.getCurrentDate(),
                "demat_signature_date_char_01", "demat_signature_date_char_02", null);

        // RBI approval date — 8 per-char widgets expecting DDMMYYYY (no separators)
        splitCompactDate(m, d.getRbiApprovalDate(), "demat_rbi_approval_date_char_", 8);
    }

    private void applyFatcaDerived(Map<String, Object> m, KycFormDataDto d) {
        String us = nz(d.getFatcaUsPerson());
        m.put("first_holder_fatca_us_person_yes_no",
                "1".equals(us) || "true".equalsIgnoreCase(us) ? "Yes" : "No");
    }

    // ─────────────────────────────────────────────────────────────────
    // Checkbox-set helpers
    // ─────────────────────────────────────────────────────────────────

    private void applyResidentialStatus(Map<String, Object> m, KycFormDataDto d) {
        String invType = nz(d.getInvestorType()).toUpperCase();
        String rs = nz(d.getResidentialStatus()).toLowerCase();

        if (invType.contains("NRI") || (rs.contains("non") && rs.contains("resident"))) {
            m.put("first_holder_residential_non_resident_indian", FIELD_TRUE);
        } else if (invType.contains("FOREIGN") || rs.contains("foreign")) {
            m.put("first_holder_residential_foreign_national", FIELD_TRUE);
        } else if (invType.contains("OCI") || rs.contains("origin") || rs.contains("pio")) {
            m.put("first_holder_residential_person_of_indian_origin", FIELD_TRUE);
        } else {
            m.put("first_holder_residential_resident_individual", FIELD_TRUE);
        }
    }

    private void applyOccupation(Map<String, Object> m, KycFormDataDto d, String prefix) {
        String occ = nz(d.getOccupationType()).toLowerCase();
        if (occ.isBlank()) occ = nz(d.getTradingOccupation()).toLowerCase();
        if (occ.isBlank()) return;

        boolean isKyc = "first_holder_".equals(prefix);
        boolean isTrading = "trading_".equals(prefix);

        if (occ.contains("private")) {
            if (isKyc) { m.put(prefix + "occupation_service", FIELD_TRUE); m.put(prefix + "occupation_service_private", FIELD_TRUE); }
            else m.put(prefix + "occupation_private_sector", FIELD_TRUE);
        } else if (occ.contains("public")) {
            if (isKyc) { m.put(prefix + "occupation_service", FIELD_TRUE); m.put(prefix + "occupation_service_public", FIELD_TRUE); }
            else m.put(prefix + "occupation_public_sector", FIELD_TRUE);
        } else if (occ.contains("government") || occ.contains("govt")) {
            if (isKyc) { m.put(prefix + "occupation_service", FIELD_TRUE); m.put(prefix + "occupation_service_government", FIELD_TRUE); }
            else m.put(prefix + "occupation_government_sector", FIELD_TRUE);
        } else if (occ.contains("business")) {
            m.put(prefix + "occupation_business", FIELD_TRUE);
        } else if (occ.contains("professional")) {
            if (isKyc) { m.put(prefix + "occupation_others", FIELD_TRUE); m.put(prefix + "occupation_others_professional", FIELD_TRUE); }
            else m.put(prefix + "occupation_professional", FIELD_TRUE);
        } else if (occ.contains("self")) {
            if (isKyc) { m.put(prefix + "occupation_others", FIELD_TRUE); m.put(prefix + "occupation_others_self_employed", FIELD_TRUE); }
            else m.put(prefix + "occupation_business", FIELD_TRUE);
        } else if (occ.contains("retired")) {
            if (isKyc) { m.put(prefix + "occupation_others", FIELD_TRUE); m.put(prefix + "occupation_others_retired", FIELD_TRUE); }
            else m.put(prefix + "occupation_retired", FIELD_TRUE);
        } else if (occ.contains("housewife") || occ.contains("homemaker")) {
            if (isKyc) { m.put(prefix + "occupation_others", FIELD_TRUE); m.put(prefix + "occupation_others_housewife", FIELD_TRUE); }
            else m.put(prefix + "occupation_housewife", FIELD_TRUE);
        } else if (occ.contains("student")) {
            if (isKyc) { m.put(prefix + "occupation_others", FIELD_TRUE); m.put(prefix + "occupation_others_student", FIELD_TRUE); }
            else m.put(prefix + "occupation_student", FIELD_TRUE);
        } else if (occ.contains("agricultur")) {
            if (isTrading) m.put(prefix + "occupation_agriculturist", FIELD_TRUE);
            else m.put(prefix + "occupation_not_categorised", FIELD_TRUE);
        } else {
            if (isKyc) m.put(prefix + "occupation_not_categorised", FIELD_TRUE);
            else m.put(prefix + "occupation_others", FIELD_TRUE);
        }
    }

    private void applyPoi(Map<String, Object> m, KycFormDataDto d) {
        String idType = nz(d.getIdType()).toLowerCase();
        String idNumber = nz(d.getIdNumber());

        if (idType.contains("passport") || !nz(d.getPassportNumber()).isBlank()) {
            m.put("first_holder_poi_passport", FIELD_TRUE);
            m.put("first_holder_poi_passport_number",
                    nz(d.getPassportNumber()).isBlank() ? idNumber : nz(d.getPassportNumber()));
            splitDateDirect(m, d.getPassportValidUpto(),
                    "first_holder_poi_passport_expiry_dd",
                    "first_holder_poi_passport_expiry_mm",
                    "first_holder_poi_passport_expiry_yyyy");
        } else if (idType.contains("pan")) {
            m.put("first_holder_poi_pan", FIELD_TRUE);
            m.put("first_holder_poi_pan_number", nz(d.getPanNumber()));
        } else if (idType.contains("aadhaar") || idType.contains("uid")) {
            m.put("first_holder_poi_uid_aadhaar", FIELD_TRUE);
            m.put("first_holder_poi_uid_aadhaar_number", nz(d.getAadhaarNumber()));
        } else if (idType.contains("voter")) {
            m.put("first_holder_poi_voter_id", FIELD_TRUE);
            m.put("first_holder_poi_voter_id_number", nz(d.getVoterIdNumber()));
        } else if (idType.contains("driving") || idType.contains("licence") || idType.contains("license")) {
            m.put("first_holder_poi_driving_licence", FIELD_TRUE);
            m.put("first_holder_poi_driving_licence_number", nz(d.getDrivingLicenseNumber()));
            splitDateDirect(m, d.getDrivingLicenseExpiry(),
                    "first_holder_poi_driving_licence_expiry_dd",
                    "first_holder_poi_driving_licence_expiry_mm",
                    "first_holder_poi_driving_licence_expiry_yyyy");
        } else if (idType.contains("nrega")) {
            m.put("first_holder_poi_nrega_job_card", FIELD_TRUE);
            m.put("first_holder_poi_nrega_number", idNumber);
        }
    }

    private void applyPoa(Map<String, Object> m, KycFormDataDto d) {
        String poa = nz(d.getPermAddressProof()).toLowerCase();
        if (poa.isBlank()) poa = nz(d.getProofOfAddress()).toLowerCase();

        if (poa.contains("passport")) m.put("first_holder_poa_passport", FIELD_TRUE);
        else if (poa.contains("voter")) m.put("first_holder_poa_voter_id", FIELD_TRUE);
        else if (poa.contains("aadhaar") || poa.contains("uid")) m.put("first_holder_poa_uid_aadhaar", FIELD_TRUE);
        else if (poa.contains("driving") || poa.contains("licence") || poa.contains("license"))
            m.put("first_holder_poa_driving_licence", FIELD_TRUE);
        else if (poa.contains("nrega")) m.put("first_holder_poa_nrega_job_card", FIELD_TRUE);
        else if (!poa.isBlank()) m.put("first_holder_poa_others", FIELD_TRUE);
    }

    private void applyAddressType(Map<String, Object> m, KycFormDataDto d) {
        String at = nz(d.getAddressType()).toLowerCase();
        if (at.contains("residential") && at.contains("business")) {
            m.put("first_holder_address_type_residential_business", FIELD_TRUE);
        } else if (at.contains("residential")) {
            m.put("first_holder_address_type_residential", FIELD_TRUE);
        } else if (at.contains("business")) {
            m.put("first_holder_address_type_business", FIELD_TRUE);
        } else if (at.contains("registered") || at.contains("office")) {
            m.put("first_holder_address_type_registered_office", FIELD_TRUE);
        } else {
            // NRI overseas default
            m.put("first_holder_address_type_residential", FIELD_TRUE);
        }
    }

    private void applyIncomeBracket(Map<String, Object> m, String incomeRange, String prefix) {
        String key = mapIncomeBracket(incomeRange);
        if (key != null) m.put(prefix + key, FIELD_TRUE);
    }

    private String mapIncomeBracket(String range) {
        if (range == null) return null;
        String r = range.toLowerCase().replace(" ", "").replace(",", "").replace("rs", "");
        if (r.contains("below1") || r.contains("<1") || r.contains("under1")) return "below_1_lakh";
        if (r.contains("1to5") || r.contains("1-5")) return "1_to_5_lakhs";
        if (r.contains("5to10") || r.contains("5-10")) return "5_to_10_lakhs";
        if (r.contains("10to25") || r.contains("10-25")) return "10_to_25_lakhs";
        if (r.contains("above25") || r.contains(">25") || r.contains("25+")) return "above_25_lakhs";
        return null;
    }

    private void applyPep(Map<String, Object> m, KycFormDataDto d) {
        String pep = nz(d.getPepStatus()).toLowerCase();
        switch (pep) {
            case "pep" -> {
                m.put("trading_pep_yes", FIELD_TRUE);
                m.put("trading_related_to_pep_no", FIELD_TRUE);
            }
            case "related to pep" -> {
                m.put("trading_pep_no", FIELD_TRUE);
                m.put("trading_related_to_pep_yes", FIELD_TRUE);
            }
            default -> {
                m.put("trading_pep_no", FIELD_TRUE);
                m.put("trading_related_to_pep_no", FIELD_TRUE);
            }
        }
    }

    private String resolveDematAccountType(KycFormDataDto d) {
        String invType = nz(d.getInvestorType()).toUpperCase();
        if (invType.contains("NRI")) return "nri_repatriable";
        if (invType.contains("FOREIGN")) return "foreign_national";
        return "ordinary";
    }

    // ─────────────────────────────────────────────────────────────────
    // Utilities
    // ─────────────────────────────────────────────────────────────────

    private static String readString(BeanWrapper bw, String property) {
        try {
            Object v = bw.getPropertyValue(property);
            return v == null ? "" : String.valueOf(v);
        } catch (Exception e) {
            log.debug("readString: unknown DTO property '{}': {}", property, e.getMessage());
            return "";
        }
    }

    /** Strip a trailing "_aliasHint" from a JSON key so the alias still resolves to a real DTO getter. */
    private static String stripAliasSuffix(String key) {
        int u = key.lastIndexOf('_');
        if (u <= 0) return key;
        return key.substring(0, u);
    }

    private static String nz(String v) {
        return v == null ? "" : v.trim();
    }

    private static String compose(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            String v = nz(p);
            if (!v.isBlank()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(v);
            }
        }
        return sb.toString();
    }

    private static String normalizeGender(String g) {
        String v = nz(g).toLowerCase();
        if (v.isBlank()) return "";
        if (v.startsWith("m") || "1".equals(v)) return "male";
        if (v.startsWith("f") || "2".equals(v)) return "female";
        if (v.startsWith("t") || "3".equals(v)) return "transgender";
        return "";
    }

    private static boolean isIndianCitizen(KycFormDataDto d) {
        String code = nz(d.getCitizenshipIsoCode()).toUpperCase();
        if ("IN".equals(code)) return true;
        String name = nz(d.getCitizenshipName()).toLowerCase();
        return name.equals("indian") || name.equals("india");
    }

    private static boolean hasForeignTaxResidence(KycFormDataDto d) {
        String tax = nz(d.getFatcaTaxResidenceName()).toLowerCase();
        if (tax.isBlank()) return false;
        return !tax.equals("india") && !tax.equals("indian");
    }

    private static void splitDateDirect(Map<String, Object> m, String ddMMyyyy,
                                        String dayField, String monthField, String yearField) {
        if (ddMMyyyy == null || ddMMyyyy.isBlank()) return;
        try {
            LocalDate dt = LocalDate.parse(ddMMyyyy.trim(), DMY);
            if (dayField != null) m.put(dayField, String.format("%02d", dt.getDayOfMonth()));
            if (monthField != null) m.put(monthField, String.format("%02d", dt.getMonthValue()));
            if (yearField != null) m.put(yearField, String.valueOf(dt.getYear()));
        } catch (Exception e) {
            log.debug("splitDateDirect: could not parse '{}': {}", ddMMyyyy, e.getMessage());
        }
    }

    /**
     * Normalise a date string to DDMMYYYY (separators removed) and split one char per widget.
     * Accepts dd-MM-yyyy, dd/MM/yyyy, or already-compact DDMMYYYY. Blank on parse failure.
     */
    private static void splitCompactDate(Map<String, Object> m, String dateStr, String prefix, int count) {
        String compact = "";
        if (dateStr != null && !dateStr.isBlank()) {
            String cleaned = dateStr.trim().replaceAll("[-/\\s]", "");
            if (cleaned.length() == 8 && cleaned.chars().allMatch(Character::isDigit)) {
                compact = cleaned;
            } else {
                try {
                    LocalDate dt = LocalDate.parse(dateStr.trim(), DMY);
                    compact = String.format("%02d%02d%04d",
                            dt.getDayOfMonth(), dt.getMonthValue(), dt.getYear());
                } catch (Exception e) {
                    log.debug("splitCompactDate: could not parse '{}': {}", dateStr, e.getMessage());
                }
            }
        }
        for (int i = 0; i < count; i++) {
            String key = prefix + String.format("%02d", i + 1);
            m.put(key, i < compact.length() ? String.valueOf(compact.charAt(i)) : "");
        }
    }
}
