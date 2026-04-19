package com.facilon.app.module.client.service.pdf;

import com.facilon.app.module.client.dto.KycFormDataDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * On application startup, validates the KYC form expander output against the PDF widget
 * inventory. Catches two classes of bugs:
 *
 * <ul>
 *   <li><b>Typos (ERROR)</b> — widget names the expander emits that don't exist in the PDF.
 *       These are definite bugs: the value is silently dropped at fill time.</li>
 *   <li><b>Coverage gaps (WARN)</b> — PDF widgets the expander never sets for any input.
 *       May be intentional (signature blocks, nomination section, unmodeled fields).</li>
 * </ul>
 *
 * Runs once per JVM start. No runtime overhead on actual PDF generation.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KycFormMappingValidator {

    private static final String INVENTORY_PATH = "templates/pdf/kyc-form/nri/field-inventory.json";

    private final KycFormFieldExpander expander;
    private final ObjectMapper objectMapper;

    @EventListener(ApplicationReadyEvent.class)
    public void validate() {
        Set<String> inventory = loadInventoryKeys();
        if (inventory.isEmpty()) {
            log.warn("KYC mapping validator skipped: field inventory is empty or missing at {}", INVENTORY_PATH);
            return;
        }

        Map<String, Object> produced;
        try {
            produced = expander.expand(buildMaximalFixtureDto());
        } catch (Exception e) {
            log.error("KYC mapping validator: expander threw on fixture DTO — mapping is broken", e);
            return;
        }

        Set<String> producedKeys = new LinkedHashSet<>(produced.keySet());

        // Typos: produced keys that aren't in the inventory
        Set<String> typos = new TreeSet<>(producedKeys);
        typos.removeAll(inventory);

        // Coverage gaps: inventory widgets the expander never sets
        Set<String> gaps = new TreeSet<>(inventory);
        gaps.removeAll(producedKeys);

        int correct = producedKeys.size() - typos.size();

        if (!typos.isEmpty()) {
            log.error("KYC mapping validation: {} TYPO(S) — widgets sent to JAVAPDFill that do not exist in {}:",
                    typos.size(), INVENTORY_PATH);
            typos.forEach(k -> log.error("  typo: {}", k));
        }

        if (!gaps.isEmpty()) {
            log.warn("KYC mapping validation: {} coverage gap(s) — PDF widgets never populated by the expander:",
                    gaps.size());
            gaps.forEach(k -> log.warn("  unset: {}", k));
        }

        log.info("KYC mapping validation complete — inventory={}, produced={}, correct={}, typos={}, gaps={}",
                inventory.size(), producedKeys.size(), correct, typos.size(), gaps.size());
    }

    private Set<String> loadInventoryKeys() {
        try {
            ClassPathResource res = new ClassPathResource(INVENTORY_PATH);
            if (!res.exists()) {
                return Set.of();
            }
            try (InputStream is = res.getInputStream()) {
                Map<String, String> inv = objectMapper.readValue(is, new TypeReference<>() {});
                return new LinkedHashSet<>(inv.keySet());
            }
        } catch (Exception e) {
            log.warn("KYC mapping validator: could not load inventory '{}': {}", INVENTORY_PATH, e.getMessage());
            return Set.of();
        }
    }

    /**
     * Synthetic DTO with every branch-trigger populated, so the expander exercises every
     * conditional code path. Values are not meant to be realistic — they're just non-blank
     * so nothing is skipped by null/blank guards.
     */
    private KycFormDataDto buildMaximalFixtureDto() {
        return KycFormDataDto.builder()
                .clientCode("VAL-CLIENT-01")
                .clientId("VAL-ID-01")
                .namePrefix("Mr")
                .firstName("Validation")
                .middleName("Fixture")
                .lastName("User")
                .applicantName("Validation Fixture User")
                .fatherNameTitle("Mr")
                .fathersFirstName("Father")
                .fathersMiddleName("F")
                .fathersLastName("User")
                .motherNameTitle("Mrs")
                .motherFirstName("Mother")
                .motherMiddleName("M")
                .motherLastName("User")
                .maidenTitle("Ms")
                .maidenName("Maiden")
                .maidenMiddleName("M")
                .maidenLastName("User")
                .dateOfBirth("15-06-1985")
                .gender("Male")
                .maritalStatus("Married")
                .citizenship("IN")
                .citizenshipName("Indian")
                .citizenshipIsoCode("IN")
                .residentialStatus("Non Resident Indian")
                .occupationType("Private Sector")
                .tradingOccupation("Private Sector")
                .panNumber("ABCDE1234F")
                .aadhaarNumber("123456789012")
                .cityOfBirth("Mumbai")
                .countryOfBirth("240")
                .countryOfBirthName("India")
                .proofOfAddress("Passport")
                .permAddressProof("Passport")
                .addressType("Residential")
                .idType("Passport")
                .idNumber("P1234567")
                .passportNumber("P1234567")
                .passportValidUpto("31-12-2030")
                .voterIdNumber("")
                .drivingLicenseNumber("")
                .drivingLicenseExpiry("")
                .permAddress1("Line 1")
                .permAddress2("Line 2")
                .permCity("Mumbai")
                .permState("Maharashtra")
                .permCountry("India")
                .permPinCode("400001")
                .corrAddress1("Corr Line 1")
                .corrAddress2("Corr Line 2")
                .corrCity("Mumbai")
                .corrState("Maharashtra")
                .corrPinCode("400001")
                .corrSameAsPerm("1")
                .mobile("9876543210")
                .email("val@example.com")
                .isdCode("91")
                .ipvDoneBy("Ravi Kumar")
                .ipvDate("14-04-2026")
                .incomeRange("5 to 10 Lakhs")
                .netWorth("5000000")
                .pepStatus("Not PEP")
                .bankName("HDFC Bank")
                .bankBranch("Mumbai Main")
                .bankBranchAddress("123 Main Road")
                .bankCity("Mumbai")
                .bankAccountNo("12345678901234")
                .bankAccountType("Savings")
                .micrNo("400240001")
                .ifscCode("HDFC0000123")
                .bankDetailsCity("Mumbai")
                .bankDetailsState("Maharashtra")
                .bankDetailsStateName("Maharashtra")
                .bankDetailsCountry("India")
                .bankDetailsCountryName("India")
                .bankDetailsZipCode("400001")
                .bankDetailsMicr("400240001")
                .dpName("Ventura Securities Limited")
                .depository("NSDL")
                .dpId("IN303116")
                .investmentExperience("5")
                .investmentExperienceYears("5")
                .dematAccountType("NRI-Repatriable")
                .fatcaClientCode("VAL-CLIENT-01")
                .fatcaCountryBirthName("India")
                .fatcaCitizenshipName("Indian")
                .fatcaTaxResidenceName("United States")
                .fatcaUsPerson("1")
                .fatcaTin("US-TIN-12345")
                .ecnEmail("val@example.com")
                .ecnSecondaryEmail("val2@example.com")
                .aadhaarConsent("123456789012")
                .ackApplicantName("Validation Fixture User")
                .disOption("Option 1")
                .investorUniqueCode("VAL-CLIENT-01")
                .investorType("NRI")
                .currentDate("18-04-2026")
                .currentDateDmY("18042026")
                .build();
    }
}
