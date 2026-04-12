package com.facilon.app.module.client.service;

import com.facilon.app.module.client.dto.KycFormDataDto;
import com.facilon.app.module.client.model.*;
import com.facilon.app.module.client.model.master.*;
import com.facilon.app.module.client.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.InputStream;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * KYC PDF data and rendering service.
 *
 * Collects investor data from 9+ repositories, resolves master-data lookups
 * (nationality, country, state, title), and builds a flat {@link KycFormDataDto}.
 *
 * Two rendering approaches available:
 * <ul>
 *   <li><b>AcroForm stamping</b> — {@link KycPdfStampService#stampPdf} fills the static
 *       {@code pdf/kyc-form-fillable.pdf}. Used by the frontend download.</li>
 *   <li><b>FreeMarker HTML→PDF</b> — {@link #generateKycPdf} renders
 *       {@code templates/pdf/kyc-form/kyc-form-master.ftl} via Flying Saucer.
 *       Converted from Laravel {@code pdf.blade}.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KycPdfService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final String MASTER_TEMPLATE = "pdf/kyc-form/kyc-form-master.ftl";

    private final InvestorRepository investorRepository;
    private final UserPersonalInformationRepository personalInfoRepository;
    private final UserPassportDetailsRepository passportRepository;
    private final InvestorBankDetailsRepository bankDetailsRepository;
    private final InvestorContactDetailsRepository contactDetailsRepository;
    private final InvestorTaxInformationRepository taxInfoRepository;
    private final InvestorResidentialStatusRepository residentialStatusRepository;
    private final InvestorNominationRepository nominationRepository;
    private final InvestorRiskProfileRepository riskProfileRepository;
    private final MasterNationalityRepository masterNationalityRepository;
    private final MasterCountriesRepository masterCountriesRepository;
    private final MasterStatesRepository masterStatesRepository;
    private final MasterTitleRepository masterTitleRepository;
    private final FreeMarkerConfigurer freeMarkerConfigurer;

    // ────────────────────────────────────────────────────────────────────
    // Public API
    // ────────────────────────────────────────────────────────────────────

    /** Build the flat form-data DTO for a given investor. */
    public KycFormDataDto buildFormData(String uniqueCode) {
        Investor investor = investorRepository.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new RuntimeException("Investor not found: " + uniqueCode));

        UserPersonalInformation pi = personalInfoRepository.findByInvestorUniqueId(uniqueCode).orElse(null);
        UserPassportDetails pp = passportRepository.findByInvestorUniqueCode(uniqueCode).orElse(null);
        InvestorBankDetails bk = bankDetailsRepository.findByInvestorUniqueIdAndIsPrimaryTrue(uniqueCode)
                .or(() -> bankDetailsRepository.findFirstByInvestorUniqueId(uniqueCode)).orElse(null);
        InvestorContactDetails cd = contactDetailsRepository.findByInvestorUniqueId(uniqueCode).orElse(null);
        InvestorTaxInformation tx = taxInfoRepository.findByInvestorUniqueId(uniqueCode).orElse(null);
        InvestorRiskProfile rp = riskProfileRepository.findByInvestorUniqueId(uniqueCode).orElse(null);
        List<InvestorNomination> noms = nominationRepository.findByInvestorUniqueId(uniqueCode);

        // ─── Master-data lookups ───
        String citizenshipId = safe(pi, p -> p.getCitizenship());
        String citizenshipName = "";
        String citizenshipIsoCode = "";
        if (!citizenshipId.isEmpty()) {
            Optional<MasterNationality> natOpt = masterNationalityRepository.findBySsNationalityId(citizenshipId);
            if (natOpt.isPresent()) {
                citizenshipName = natOpt.get().getSsNationality() != null ? natOpt.get().getSsNationality() : "";
                citizenshipIsoCode = natOpt.get().getSsNationalityId() != null ? natOpt.get().getSsNationalityId() : "";
            }
        }

        String countryOfBirthRaw = safe(pi, p -> p.getCountryDob());
        String countryOfBirthName = resolveCountryName(countryOfBirthRaw);

        String fatherTitleRaw = safe(pi, p -> p.getFatherNameTitle());
        String fatherTitleName = resolveTitleName(fatherTitleRaw);

        String motherTitleRaw = safe(pi, p -> p.getMotherNameTitle());
        String motherTitleName = resolveTitleName(motherTitleRaw);

        String bankStateRaw = safe(bk, b -> b.getBankDetailsState());
        String bankStateName = resolveStateName(bankStateRaw);

        String bankCountryRaw = safe(bk, b -> b.getBankDetailsCountry());
        String bankCountryName = resolveCountryName(bankCountryRaw);

        // Nominee state/country name lookups
        String nomState1 = nomField(noms, 0, n -> n.getNomineeState());
        String nomState2 = nomField(noms, 1, n -> n.getNomineeState());
        String nomState3 = nomField(noms, 2, n -> n.getNomineeState());
        String nomCountry1 = nomField(noms, 0, n -> n.getNomineeCountrycode() != null
                ? String.valueOf(n.getNomineeCountrycode()) : null);
        String nomCountry2 = nomField(noms, 1, n -> n.getNomineeCountrycode() != null
                ? String.valueOf(n.getNomineeCountrycode()) : null);
        String nomCountry3 = nomField(noms, 2, n -> n.getNomineeCountrycode() != null
                ? String.valueOf(n.getNomineeCountrycode()) : null);

        // FATCA resolved names
        String fatcaCountryBirthName = countryOfBirthName; // same source
        String fatcaCitizenshipName = citizenshipName;      // same source
        String fatcaTaxResRaw = safe(tx, t -> t.getTaxCountry() != null ? String.valueOf(t.getTaxCountry()) : null);
        String fatcaTaxResidenceName = resolveCountryName(fatcaTaxResRaw);

        // Investment experience
        String investmentExp = safe(rp, r -> r.getInvestmentExperienceYears() != null
                ? String.valueOf(r.getInvestmentExperienceYears()) : null);

        // Current date
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String currentDateDmY = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy"));

        // Image base64 encoding
        String venturaLogoBase64 = loadImageAsBase64("static/pdf/images/ventura_logo.jpg");
        String centralKycLogoBase64 = loadImageAsBase64("static/pdf/images/central_KYC_logo.png");
        String checkmarkBase64 = loadImageAsBase64("static/pdf/images/checkmark-new-1.png");
        String profilePicBase64 = loadImageAsBase64("static/pdf/images/user_profile_pic.png");
        String signaturePicBase64 = loadImageAsBase64("static/pdf/images/signature_user_pic.png");

        return KycFormDataDto.builder()
                // ─── Client Identifiers ───
                .clientCode("")
                .clientId("")
                // ─── Personal ───
                .namePrefix(safe(pi, p -> p.getNameTitle()))
                .firstName(safe(pi, p -> p.getInvestorFirstName()))
                .middleName(safe(pi, p -> p.getInvestorMiddleName()))
                .lastName(safe(pi, p -> p.getInvestorLastName()))
                .applicantName(compose(safe(pi, p -> p.getInvestorFirstName()),
                        safe(pi, p -> p.getInvestorMiddleName()),
                        safe(pi, p -> p.getInvestorLastName())))
                .fatherSpouseName(compose(safe(pi, p -> p.getFathersFirstName()),
                        safe(pi, p -> p.getFathersMiddleName()),
                        safe(pi, p -> p.getFathersLastName())))
                .fathersFirstName(safe(pi, p -> p.getFathersFirstName()))
                .fathersMiddleName(safe(pi, p -> p.getFathersMiddleName()))
                .fathersLastName(safe(pi, p -> p.getFathersLastName()))
                .fatherNameTitle(fatherTitleName)
                .motherName(compose(safe(pi, p -> p.getMotherFirstName()),
                        safe(pi, p -> p.getMotherMiddleName()),
                        safe(pi, p -> p.getMotherLastName())))
                .motherFirstName(safe(pi, p -> p.getMotherFirstName()))
                .motherMiddleName(safe(pi, p -> p.getMotherMiddleName()))
                .motherLastName(safe(pi, p -> p.getMotherLastName()))
                .motherNameTitle(motherTitleName)
                .maidenName(safe(pi, p -> p.getMaidenName()))
                .maidenMiddleName(safe(pi, p -> p.getMaidenMiddleName()))
                .maidenLastName(safe(pi, p -> p.getMaidenLastName()))
                .maidenTitle(safe(pi, p -> p.getMaidenTitle()))
                .dateOfBirth(fmtDate(pi != null ? pi.getUserDob() : null))
                .gender(safe(pi, p -> p.getInvestorGender()))
                .maritalStatus(mapMaritalStatus(safe(pi, p -> p.getMaritalStatus())))
                .citizenship(citizenshipId)
                .citizenshipName(citizenshipName)
                .citizenshipIsoCode(citizenshipIsoCode)
                .residentialStatus(safe(pi, p -> p.getResidentialStatus()))
                .occupationType(safe(rp, r -> r.getOccupation()))
                .panNumber(safe(pi, p -> p.getUserPanNo()))
                .aadhaarNumber(safe(pi, p -> p.getUserAadharNo()))
                .cityOfBirth(safe(pi, p -> p.getCityOfDob()))
                .countryOfBirth(countryOfBirthRaw)
                .countryOfBirthName(countryOfBirthName)
                .proofOfAddress(safe(pi, p -> p.getProofOfAddress()))
                .addressType(safe(pi, p -> p.getAddressType()))
                .simplifiedMeasuresAddressCode("")
                .simplifiedMeasuresDocumentType("")
                .simplifiedMeasuresIdentificationNumber("")
                // ─── Tax ───
                .taxCountryCode(safe(tx, t -> t.getTaxCountry() != null ? String.valueOf(t.getTaxCountry()) : null))
                .taxIdNumber(safe(tx, t -> t.getTaxIdentificationNumber()))
                .taxIdType(safe(tx, t -> t.getTaxIdentificationNumberType()))
                // ─── ID ───
                .idType(safe(pp, p -> p.getDocumentType()))
                .idNumber(safe(pp, p -> p.getPassportNumber()))
                .idExpiryDate(fmtDate(pp != null ? pp.getPassportExpiryDate() : null))
                // ─── Identity Documents ───
                .passportNumber(safe(pp, p -> p.getPassportNumber()))
                .passportValidUpto(fmtDate(pp != null ? pp.getPassportExpiryDate() : null))
                .voterIdNumber("")
                .drivingLicenseNumber("")
                .drivingLicenseExpiry("")
                .otherIdNumber("")
                .othersIdentificationNumber("")
                .identificationNumber("")
                .taxPanNo(safe(pi, p -> p.getTaxPanNo()))
                // ─── Checkbox / Flag values ───
                .passportNumberValue("")
                .voterIdCardValue("")
                .panCardValue("")
                .drivingLicenceValue("")
                .uidAadhaarValue("")
                .othersValue("")
                .polExposed(rp != null && rp.getPolExposed() != null
                        ? (Boolean.TRUE.equals(rp.getPolExposed()) ? "1" : "0") : "")
                .polExposedRelated(rp != null && rp.getPolExposedRelated() != null
                        ? (Boolean.TRUE.equals(rp.getPolExposedRelated()) ? "1" : "0") : "")
                .appointNominee("")
                .typeOfAccount("")
                // ─── Permanent Address ───
                .permAddress1(safe(cd, c -> c.getAddressLine1()))
                .permAddress2(safe(cd, c -> c.getAddressLine2()))
                .permAddress3(safe(cd, c -> c.getAddressLine3()))
                .permCity(safe(cd, c -> c.getUserCity()))
                .permState(safe(cd, c -> c.getUserState()))
                .permCountry(safe(cd, c -> c.getUserCountry()))
                .permPinCode(safe(cd, c -> c.getUserZipCode()))
                .permAddressProof(safe(cd, c -> c.getProofOfAddress()))
                // ─── Correspondence Address ───
                .corrSameAsPerm(safe(cd, c -> c.getCorrAddressSameAsPerm()))
                .corrAddress1(safe(cd, c -> c.getCorrAddressLine1()))
                .corrAddress2(safe(cd, c -> c.getCorrAddressLine2()))
                .corrCity(safe(cd, c -> c.getCorrUserCity()))
                .corrState(safe(cd, c -> c.getCorrUserState()))
                .corrCountry(safe(cd, c -> c.getCorrUserCountry()))
                .corrPinCode(safe(cd, c -> c.getCorrUserZipCode()))
                // ─── Contact ───
                .mobile(safe(cd, c -> c.getMobileNumber()))
                .email(safe(cd, c -> c.getEmailAddress()))
                .isdCode(safe(cd, c -> c.getIsdCode()))
                .mobileBelongsTo("")
                .emailBelongsTo("")
                // ─── IPV ───
                .ipvDoneBy(safe(pi, p -> p.getSsVerificationDoneBy()))
                .ipvDate(fmtDateTime(pi != null ? pi.getSsVerificationDateandTime() : null))
                // ─── Trading — Income ───
                .incomeRange(safe(rp, r -> r.getAnnualIncome()))
                .grossIncome("")
                .netWorth(safe(rp, r -> r.getNetWorth()))
                .tradingOccupation(safe(rp, r -> r.getOccupation()))
                .pepStatus(mapPep(rp))
                // ─── Trading — Bank ───
                .bankName(safe(bk, b -> b.getBankName()))
                .bankBranch(safe(bk, b -> b.getBankAddress()))
                .bankBranchAddress(safe(bk, b -> b.getBankAddress()))
                .bankCity(safe(bk, b -> b.getBankDetailsCity()))
                .bankAccountNo(safe(bk, b -> b.getAccountNumber()))
                .bankAccountType(safe(bk, b -> b.getAccountType()))
                .micrNo(safe(bk, b -> b.getBankDetailsMicr()))
                .ifscCode(safe(bk, b -> b.getIfscCode()))
                .bankDetailsCity(safe(bk, b -> b.getBankDetailsCity()))
                .bankDetailsState(bankStateRaw)
                .bankDetailsStateName(bankStateName)
                .bankDetailsCountry(bankCountryRaw)
                .bankDetailsCountryName(bankCountryName)
                .bankDetailsZipCode(safe(bk, b -> b.getBankDetailsZipCode()))
                .bankDetailsMicr(safe(bk, b -> b.getBankDetailsMicr()))
                // ─── PIS Bank Details (not available on entity — defaults) ───
                .pisBankName("")
                .pisBankBranchAddress("")
                .pisBankAccountNumber("")
                .pisBankIfscCode("")
                .pisBankMicr("")
                .pisBankCity("")
                .pisBankZipCode("")
                // ─── Trading — Depository ───
                .dpName("Ventura Securities Limited")
                .depository("NSDL")
                .dpId("IN303116")
                // ─── Trading — Experience ───
                .investmentExperience(investmentExp)
                .investmentExperienceIn(safe(rp, r -> r.getInvestmentExperienceIn()))
                .investmentExperienceYears(investmentExp)
                // ─── Demat ───
                .dematPan(safe(pi, p -> p.getUserPanNo()))
                .dematAccountType("Ordinary Resident")
                .rbiApproval(safe(bk, b -> b.getRbiApproval()))
                .rbiApprovalDate(safe(bk, b -> b.getRbiApprovalDate()))
                .rbiApprovalNumber(safe(bk, b -> b.getRbiApprovalOrderNumber()))
                // ─── Nomination ───
                .nominee1Name(nomField(noms, 0, n -> n.getNomineeFirstName()))
                .nominee1Relation(nomField(noms, 0, n -> n.getRelationship()))
                .nominee1Dob(nomDateField(noms, 0))
                .nominee1Share(nomField(noms, 0, n -> n.getAllocationPercentage() != null
                        ? String.valueOf(n.getAllocationPercentage()) : null))
                .nominee1Address(nomField(noms, 0, n -> n.getNomineeAddress()))
                .nominee1Mobile(nomField(noms, 0, n -> n.getNomineeMobile()))
                .nominee1Email(nomField(noms, 0, n -> n.getNomineeEmail()))
                .nominee2Name(nomField(noms, 1, n -> n.getNomineeFirstName()))
                .nominee2Relation(nomField(noms, 1, n -> n.getRelationship()))
                .nominee2Dob(nomDateField(noms, 1))
                .nominee2Share(nomField(noms, 1, n -> n.getAllocationPercentage() != null
                        ? String.valueOf(n.getAllocationPercentage()) : null))
                .nominee2Address(nomField(noms, 1, n -> n.getNomineeAddress()))
                .nominee2Mobile(nomField(noms, 1, n -> n.getNomineeMobile()))
                .nominee2Email(nomField(noms, 1, n -> n.getNomineeEmail()))
                .nominee3Name(nomField(noms, 2, n -> n.getNomineeFirstName()))
                .nominee3Relation(nomField(noms, 2, n -> n.getRelationship()))
                .nominee3Dob(nomDateField(noms, 2))
                .nominee3Share(nomField(noms, 2, n -> n.getAllocationPercentage() != null
                        ? String.valueOf(n.getAllocationPercentage()) : null))
                .nominee3Address(nomField(noms, 2, n -> n.getNomineeAddress()))
                .nominee3Mobile(nomField(noms, 2, n -> n.getNomineeMobile()))
                .nominee3Email(nomField(noms, 2, n -> n.getNomineeEmail()))
                // ─── Nominee City / State / Country / Pincode ───
                .nomineeCity1(nomField(noms, 0, n -> n.getNomineeCity()))
                .nomineeCity2(nomField(noms, 1, n -> n.getNomineeCity()))
                .nomineeCity3(nomField(noms, 2, n -> n.getNomineeCity()))
                .nomineeState1(nomState1)
                .nomineeState2(nomState2)
                .nomineeState3(nomState3)
                .nomineeStateName1(resolveStateName(nomState1))
                .nomineeStateName2(resolveStateName(nomState2))
                .nomineeStateName3(resolveStateName(nomState3))
                .nomineeCountry1(nomCountry1)
                .nomineeCountry2(nomCountry2)
                .nomineeCountry3(nomCountry3)
                .nomineeCountryName1(resolveCountryName(nomCountry1))
                .nomineeCountryName2(resolveCountryName(nomCountry2))
                .nomineeCountryName3(resolveCountryName(nomCountry3))
                .nomineePincode1(nomField(noms, 0, n -> n.getNomineePostalCode()))
                .nomineePincode2(nomField(noms, 1, n -> n.getNomineePostalCode()))
                .nomineePincode3(nomField(noms, 2, n -> n.getNomineePostalCode()))
                .nomineeDocType1(nomField(noms, 0, n -> n.getNomineeDocType()))
                .nomineeDocType2(nomField(noms, 1, n -> n.getNomineeDocType()))
                .nomineeDocType3(nomField(noms, 2, n -> n.getNomineeDocType()))
                .nomineeDocNo1(nomField(noms, 0, n -> n.getNomineeDocNo()))
                .nomineeDocNo2(nomField(noms, 1, n -> n.getNomineeDocNo()))
                .nomineeDocNo3(nomField(noms, 2, n -> n.getNomineeDocNo()))
                // ─── Guardian ───
                .guardianName(nomField(noms, 0, n -> n.getGuardianName()))
                .guardianRelation(nomField(noms, 0, n -> n.getGuardianRelationship()))
                .guardian1(nomField(noms, 0, n -> n.getGuardianName()))
                .guardian2(nomField(noms, 1, n -> n.getGuardianName()))
                .guardian3(nomField(noms, 2, n -> n.getGuardianName()))
                // ─── ECN ───
                .ecnEmail(safe(cd, c -> c.getEmailAddress()))
                .ecnSecondaryEmail(safe(cd, c -> c.getAlternateEmail()))
                // ─── FATCA ───
                .fatcaClientCode(investor.getUniqueCode())
                .fatcaCountryBirth(safe(pi, p -> p.getCountryDob()))
                .fatcaCountryBirthName(fatcaCountryBirthName)
                .fatcaCitizenship(safe(pi, p -> p.getCitizenship()))
                .fatcaCitizenshipName(fatcaCitizenshipName)
                .fatcaTaxResidence(fatcaTaxResRaw)
                .fatcaTaxResidenceName(fatcaTaxResidenceName)
                .fatcaUsPerson(safe(pi, p -> p.getUsPersonFatca()))
                .fatcaTin(safe(tx, t -> t.getTaxIdentificationNumber()))
                // ─── Aadhaar Consent ───
                .aadhaarConsent(safe(pi, p -> p.getUserAadharNo()))
                // ─── Acknowledgement ───
                .ackApplicantName(compose(safe(pi, p -> p.getInvestorFirstName()),
                        safe(pi, p -> p.getInvestorLastName())))
                // ─── DIS Option ───
                .disOption("Option 1")
                // ─── Related Persons / KYC ───
                .additionRelatedPerson("")
                .deletionRelatedPerson("")
                .kycRelatedPerson("")
                .relatedPersonType("")
                .relatedPrefix("")
                .relatedFirstName("")
                .relatedMiddleName("")
                .relatedLastName("")
                .natureOfOrganisation(safe(rp, r -> r.getNatureOfOrganisation()))
                // ─── Remarks ───
                .remarks("")
                // ─── Meta ───
                .investorUniqueCode(investor.getUniqueCode())
                .investorType(investor.getInvestorType())
                .investorTypeId("")
                // ─── Images ───
                .venturaLogoBase64(venturaLogoBase64)
                .centralKycLogoBase64(centralKycLogoBase64)
                .checkmarkBase64(checkmarkBase64)
                .profilePicBase64(profilePicBase64)
                .signaturePicBase64(signaturePicBase64)
                // ─── Current Date ───
                .currentDate(currentDate)
                .currentDateDmY(currentDateDmY)
                .build();
    }

    /** Render the KYC form as HTML (for preview). Uses FreeMarker template converted from Laravel pdf.blade. */
    public String generateKycPreviewHtml(String uniqueCode) {
        KycFormDataDto dto = buildFormData(uniqueCode);
        return renderTemplate(dto);
    }

    /** Render the KYC form as a PDF byte array via FreeMarker + Flying Saucer. */
    public byte[] generateKycPdf(String uniqueCode) {
        String html = generateKycPreviewHtml(uniqueCode);
        return convertHtmlToPdf(html);
    }

    // ────────────────────────────────────────────────────────────────────
    // Template rendering
    // ────────────────────────────────────────────────────────────────────

    private String renderTemplate(KycFormDataDto dto) {
        try {
            freemarker.template.Configuration cfg = freeMarkerConfigurer.getConfiguration();
            freemarker.template.Template template = cfg.getTemplate(MASTER_TEMPLATE);

            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Object> dtoMap = mapper.convertValue(dto, Map.class);
            Map<String, Object> model = new HashMap<>();
            model.put("data", dtoMap);

            StringWriter writer = new StringWriter();
            template.process(model, writer);
            return writer.toString();
        } catch (Exception e) {
            log.error("KYC template rendering failed: {}", e.getMessage(), e);
            throw new RuntimeException("KYC template rendering failed: " + e.getMessage(), e);
        }
    }

    /**
     * Convert HTML to PDF using Headless Chromium via Playwright.
     * Accepts standard HTML5 — no XHTML constraints.
     * Produces output visually identical to Laravel DomPDF/wkhtmltopdf.
     */
    private byte[] convertHtmlToPdf(String html) {
        try (Playwright playwright = Playwright.create()) {
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                    .setHeadless(true);

            try (Browser browser = playwright.chromium().launch(launchOptions)) {
                BrowserContext context = browser.newContext();
                Page page = context.newPage();

                page.setContent(html, new Page.SetContentOptions()
                        .setWaitUntil(com.microsoft.playwright.options.WaitUntilState.NETWORKIDLE));

                byte[] pdf = page.pdf(new Page.PdfOptions()
                        .setFormat("A4")
                        .setPrintBackground(true)
                        .setPreferCSSPageSize(false)
                        .setMargin(new com.microsoft.playwright.options.Margin()
                                .setTop("10mm")
                                .setBottom("10mm")
                                .setLeft("5mm")
                                .setRight("5mm")));

                log.info("KYC PDF generated via Playwright Chromium: {} bytes", pdf.length);
                return pdf;
            }
        } catch (Exception e) {
            log.error("KYC PDF conversion failed: {}", e.getMessage(), e);
            throw new RuntimeException("KYC PDF conversion failed: " + e.getMessage(), e);
        }
    }

    // ────────────────────────────────────────────────────────────────────
    // Helpers
    // ────────────────────────────────────────────────────────────────────

    @FunctionalInterface
    private interface SafeGetter<T> { String get(T obj); }

    private <T> String safe(T obj, SafeGetter<T> getter) {
        if (obj == null) return "";
        try {
            String v = getter.get(obj);
            return v != null ? v.trim() : "";
        } catch (Exception e) {
            return "";
        }
    }

    private String compose(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p != null && !p.isBlank()) {
                if (sb.length() > 0) sb.append(' ');
                sb.append(p.trim());
            }
        }
        return sb.toString();
    }

    private String fmtDate(LocalDate d) {
        return d != null ? d.format(DATE_FMT) : "";
    }



    private String fmtDateTime(LocalDateTime dt) {
        return dt != null ? dt.format(DATE_FMT) : "";
    }

    private String nomField(List<InvestorNomination> noms, int idx, SafeGetter<InvestorNomination> getter) {
        if (noms == null || idx >= noms.size()) return "";
        return safe(noms.get(idx), getter);
    }

    private String nomDateField(List<InvestorNomination> noms, int idx) {
        if (noms == null || idx >= noms.size() || noms.get(idx).getDateOfBirth() == null) return "";
        return noms.get(idx).getDateOfBirth().format(DATE_FMT);
    }

    private String mapMaritalStatus(String code) {
        if (code == null || code.isBlank()) return "";
        return switch (code) {
            case "1" -> "Unmarried";
            case "2" -> "Married";
            case "3" -> "Others";
            default -> code;
        };
    }

    private String mapPep(InvestorRiskProfile rp) {
        if (rp == null) return "Not PEP";
        Boolean pol = rp.getPolExposed();
        Boolean polRel = rp.getPolExposedRelated();
        if (Boolean.TRUE.equals(pol)) return "PEP";
        if (Boolean.TRUE.equals(polRel)) return "Related to PEP";
        return "Not PEP";
    }

    // ────────────────────────────────────────────────────────────────────
    // Master-data resolution helpers
    // ────────────────────────────────────────────────────────────────────

    /**
     * Resolve a country ID (numeric string) to a country name via master_countries.
     * Returns the raw value if lookup fails or is non-numeric.
     */
    private String resolveCountryName(String countryIdStr) {
        if (countryIdStr == null || countryIdStr.isBlank()) return "";
        try {
            // Try by ss_countryid first (Dataverse GUID or string ID), then by numeric id
            List<MasterCountries> allCountries = masterCountriesRepository.findAll();
            return allCountries.stream()
                    .filter(c -> countryIdStr.trim().equals(c.getSsCountryId())
                            || countryIdStr.trim().equals(c.getId() != null ? String.valueOf(c.getId()) : ""))
                    .findFirst()
                    .map(c -> c.getSsName() != null ? c.getSsName() : countryIdStr)
                    .orElse(countryIdStr);
        } catch (Exception e) {
            log.warn("Failed to resolve country name for '{}': {}", countryIdStr, e.getMessage());
            return countryIdStr;
        }
    }

    /**
     * Resolve a state ID (numeric string) to a state name via master_states.
     * The MasterStatesRepository doesn't have findById(Integer) for the ss_stateid,
     * so we iterate all states. Returns the raw value if lookup fails.
     */
    private String resolveStateName(String stateIdStr) {
        if (stateIdStr == null || stateIdStr.isBlank()) return "";
        try {
            List<MasterStates> allStates = masterStatesRepository.findAll();
            return allStates.stream()
                    .filter(s -> stateIdStr.trim().equals(s.getSsStateId()))
                    .findFirst()
                    .map(s -> s.getSsName() != null ? s.getSsName() : stateIdStr)
                    .orElse(stateIdStr);
        } catch (Exception e) {
            log.warn("Failed to resolve state name for '{}': {}", stateIdStr, e.getMessage());
            return stateIdStr;
        }
    }

    /**
     * Resolve a title ID to a title name via master_title.
     * Returns the raw value if lookup fails.
     */
    private String resolveTitleName(String titleIdStr) {
        if (titleIdStr == null || titleIdStr.isBlank()) return "";
        try {
            List<MasterTitle> allTitles = masterTitleRepository.findAll();
            return allTitles.stream()
                    .filter(t -> titleIdStr.trim().equals(t.getSsTitleId()))
                    .findFirst()
                    .map(t -> t.getSsName() != null ? t.getSsName() : titleIdStr)
                    .orElse(titleIdStr);
        } catch (Exception e) {
            log.warn("Failed to resolve title name for '{}': {}", titleIdStr, e.getMessage());
            return titleIdStr;
        }
    }

    // ────────────────────────────────────────────────────────────────────
    // Image helpers
    // ────────────────────────────────────────────────────────────────────

    /**
     * Load a classpath resource as a Base64-encoded string.
     * Returns empty string on failure.
     */
    private String loadImageAsBase64(String classpathLocation) {
        try {
            ClassPathResource resource = new ClassPathResource(classpathLocation);
            try (InputStream is = resource.getInputStream()) {
                byte[] bytes = is.readAllBytes();
                return Base64.getEncoder().encodeToString(bytes);
            }
        } catch (Exception e) {
            log.warn("Failed to load image '{}': {}", classpathLocation, e.getMessage());
            return "";
        }
    }
}
