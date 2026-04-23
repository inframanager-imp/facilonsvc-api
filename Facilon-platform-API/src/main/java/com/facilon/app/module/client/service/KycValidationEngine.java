package com.facilon.app.module.client.service;

import com.facilon.app.integration.ocr.OcrExtractionResult;
import com.facilon.app.integration.ocr.OcrField;
import com.facilon.app.module.client.model.*;
import com.facilon.app.module.client.repository.KycDocumentFieldRepository;
import com.facilon.app.module.client.repository.KycDocumentsRepository;
import com.facilon.app.module.client.repository.UserPersonalInformationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * Cross-document validation engine (KYC_DOCUMENT_PLAN §3.6).
 *
 * Rules 1-11 run after OCR returns successfully. Each produces
 * an Optional<Discrepancy>; BLOCKING ones cause the controller
 * to return HTTP 409.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KycValidationEngine {

    public static final String SRC_PAN = "PAN";
    public static final String SRC_REGISTRATION = "REGISTRATION";
    public static final String SRC_OCR_DOC_TYPE = "OCR_DOC_TYPE";
    public static final String SRC_EXPIRY = "EXPIRY";
    public static final String SRC_PREREQ = "PREREQ";
    public static final String SRC_OCR_INFRA = "OCR_INFRA";

    public static final String SEV_BLOCKING = "BLOCKING";
    public static final String SEV_WARNING = "WARNING";

    private final KycDocumentsRepository kycDocumentsRepository;
    private final KycDocumentFieldRepository fieldRepository;
    private final UserPersonalInformationRepository personalInfoRepository;

    /**
     * Validate an about-to-be-persisted upload. Caller supplies the parsed OCR
     * result and the selected document type; the engine returns a list of
     * discrepancies that should be persisted alongside the document.
     */
    public List<KycDocumentDiscrepancy> validate(Investor investor,
                                                 String selectedDocType,
                                                 OcrExtractionResult ocr,
                                                 LocalDate expiryDate) {
        return validate(investor, selectedDocType, null, ocr, expiryDate);
    }

    public List<KycDocumentDiscrepancy> validate(Investor investor,
                                                 String selectedDocType,
                                                 String addressProofSubtype,
                                                 OcrExtractionResult ocr,
                                                 LocalDate expiryDate) {
        List<KycDocumentDiscrepancy> issues = new ArrayList<>();

        // Rule 1 - PAN must be uploaded first for non-PAN docs
        if (!KycRequirementService.DOC_PAN.equals(selectedDocType)) {
            Optional<KycDocuments> pan = kycDocumentsRepository
                    .findCurrentByInvestorUniqueIdAndDocumentType(investor.getUniqueCode(),
                            KycRequirementService.DOC_PAN);
            if (pan.isEmpty()) {
                issues.add(build(SRC_PREREQ, "pan_card", "uploaded", "missing", SEV_BLOCKING));
            }
        }

        // Rule 11 - OCR detected type matches selected type.
        // For ADDRESS_PROOF, accept any of the allowed sub-types (Bank Statement,
        // Utility Bill, Passport, Aadhaar, Driving License, Rent Agreement).
        if (ocr != null && ocr.isSuccess() && ocr.detectedType() != null) {
            if (!ocrTypeMatches(selectedDocType, addressProofSubtype, ocr.detectedType())) {
                String expected = toOcrTypeCode(selectedDocType);
                issues.add(build(SRC_OCR_DOC_TYPE, "document_type",
                        expected == null ? selectedDocType : expected,
                        ocr.detectedType(), SEV_BLOCKING));
            }
        }

        // Rule 10 - expiry must be in the future
        if (expiryDate != null && expiryDate.isBefore(LocalDate.now())) {
            issues.add(build(SRC_EXPIRY, "expiry_date",
                    "future", expiryDate.toString(), SEV_BLOCKING));
        }

        if (ocr == null || !ocr.isSuccess()) {
            return issues;
        }
        Map<String, String> extracted = indexFields(ocr.fields());

        // Rules 2, 3 - passport name/DOB vs PAN
        // Rules 4 - Aadhaar name vs PAN
        // Rules 5, 6 - OCI name/DOB vs PAN
        if (!KycRequirementService.DOC_PAN.equals(selectedDocType)) {
            Optional<KycDocuments> pan = kycDocumentsRepository
                    .findCurrentByInvestorUniqueIdAndDocumentType(investor.getUniqueCode(),
                            KycRequirementService.DOC_PAN);
            pan.ifPresent(panDoc -> {
                Map<String, String> panFields = indexFields(loadFields(panDoc.getId()));
                String panName = firstNonBlank(panFields.get("full_name"), panFields.get("name"));
                String panDob = panFields.get("date_of_birth");

                String ocrName = firstNonBlank(extracted.get("full_name"),
                        extracted.get("name"), extracted.get("given_name"));
                String ocrDob = extracted.get("date_of_birth");

                if (panName != null && ocrName != null
                        && !nameEquals(panName, ocrName)) {
                    issues.add(build(SRC_PAN, "name", panName, ocrName, SEV_WARNING));
                }
                boolean checksDob = KycRequirementService.DOC_PASSPORT.equals(selectedDocType)
                        || KycRequirementService.DOC_OCI.equals(selectedDocType);
                if (checksDob && panDob != null && ocrDob != null && !panDob.equalsIgnoreCase(ocrDob)) {
                    issues.add(build(SRC_PAN, "date_of_birth", panDob, ocrDob, SEV_WARNING));
                }
            });
        }

        // Rule 7 - nationality on passport vs registration
        // Rule 8 - gender on passport/aadhaar/oci vs registration
        // Rule 9 - country of residence on address proof vs registration
        personalInfoRepository.findByInvestorUniqueId(investor.getUniqueCode())
                .ifPresent(info -> {
                    if (KycRequirementService.DOC_PASSPORT.equals(selectedDocType)) {
                        String ocrNat = extracted.get("nationality");
                        String regNat = resolveNationality(investor);
                        if (ocrNat != null && regNat != null && !ocrNat.equalsIgnoreCase(regNat)) {
                            issues.add(build(SRC_REGISTRATION, "nationality", regNat, ocrNat, SEV_WARNING));
                        }
                    }
                    if (isGenderChecked(selectedDocType)) {
                        String ocrGender = extracted.get("gender");
                        String regGender = info.getGender() != null ? info.getGender() : info.getInvestorGender();
                        if (ocrGender != null && regGender != null
                                && !ocrGender.equalsIgnoreCase(regGender)) {
                            issues.add(build(SRC_REGISTRATION, "gender", regGender, ocrGender, SEV_WARNING));
                        }
                    }
                    if (KycRequirementService.DOC_ADDRESS.equals(selectedDocType)) {
                        String ocrCountry = extracted.get("country_of_residence");
                        String regCountry = resolveCountryOfResidence(investor);
                        if (ocrCountry != null && regCountry != null
                                && !ocrCountry.equalsIgnoreCase(regCountry)) {
                            issues.add(build(SRC_REGISTRATION, "country_of_residence",
                                    regCountry, ocrCountry, SEV_WARNING));
                        }
                    }
                });

        return issues;
    }

    public boolean hasBlocking(List<KycDocumentDiscrepancy> issues) {
        return issues.stream().anyMatch(d -> SEV_BLOCKING.equalsIgnoreCase(d.getSeverity()));
    }

    // ------------ helpers ------------

    private KycDocumentDiscrepancy build(String source, String field,
                                         String expected, String observed, String severity) {
        return KycDocumentDiscrepancy.builder()
                .canonicalSource(source)
                .fieldName(field)
                .expectedValue(expected)
                .observedValue(observed)
                .severity(severity)
                .build();
    }

    private Map<String, String> indexFields(List<?> fields) {
        Map<String, String> map = new HashMap<>();
        if (fields == null) return map;
        for (Object o : fields) {
            if (o instanceof OcrField f) {
                if (f.name() != null) map.put(f.name().toLowerCase(Locale.ROOT), f.value());
            } else if (o instanceof KycDocumentField kf) {
                if (kf.getFieldName() != null) {
                    map.put(kf.getFieldName().toLowerCase(Locale.ROOT), kf.getFieldValue());
                }
            }
        }
        return map;
    }

    private List<KycDocumentField> loadFields(Long kycDocumentId) {
        return fieldRepository.findByKycDocumentId(kycDocumentId);
    }

    private boolean nameEquals(String a, String b) {
        return KycNameNormalizer.equivalent(a, b);
    }

    private boolean ocrTypeMatches(String selectedDocType, String addressProofSubtype, String detected) {
        if (detected == null) return true;
        String d = detected.trim().toLowerCase(Locale.ROOT);
        if (!KycRequirementService.DOC_ADDRESS.equals(selectedDocType)) {
            String expected = toOcrTypeCode(selectedDocType);
            return expected != null && expected.equalsIgnoreCase(d);
        }
        // ADDRESS_PROOF accepts any of the allowed sub-types.
        Set<String> addressTypes = Set.of(
                "address_proof", "bank_statement", "utility_bill", "passport",
                "aadhaar", "driving_license", "rent_agreement");
        if (!addressTypes.contains(d)) return false;
        if (addressProofSubtype == null || addressProofSubtype.isBlank()) return true;
        // If the user told us which sub-type to expect, confirm OCR agrees.
        String expectedSub = addressSubtypeToOcrCode(addressProofSubtype);
        return expectedSub == null || expectedSub.equals(d);
    }

    private String addressSubtypeToOcrCode(String subtype) {
        if (subtype == null) return null;
        return switch (subtype.trim().toLowerCase(Locale.ROOT)) {
            case "bank statement" -> "bank_statement";
            case "utility bill" -> "utility_bill";
            case "passport" -> "passport";
            case "aadhaar" -> "aadhaar";
            case "driving license" -> "driving_license";
            case "rent agreement" -> "rent_agreement";
            default -> null;
        };
    }

    private boolean isGenderChecked(String type) {
        return KycRequirementService.DOC_PASSPORT.equals(type)
                || KycRequirementService.DOC_AADHAAR.equals(type)
                || KycRequirementService.DOC_OCI.equals(type);
    }

    private String resolveNationality(Investor investor) {
        // investor.nationality is an Integer FK to master_nationality; UI-facing
        // comparison is best-effort until master lookup is wired.
        return investor.getNationality() == null ? null : String.valueOf(investor.getNationality());
    }

    private String resolveCountryOfResidence(Investor investor) {
        return investor.getCountryOfResidence() == null ? null
                : String.valueOf(investor.getCountryOfResidence());
    }

    private String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }

    private String toOcrTypeCode(String docType) {
        if (docType == null) return null;
        return switch (docType) {
            case "PAN_CARD" -> "pan_card";
            case "PASSPORT" -> "passport";
            case "AADHAR_CARD" -> "aadhaar";
            case "OCI_CARD" -> "oci_card";
            case "ADDRESS_PROOF" -> "address_proof";
            default -> null;
        };
    }
}
