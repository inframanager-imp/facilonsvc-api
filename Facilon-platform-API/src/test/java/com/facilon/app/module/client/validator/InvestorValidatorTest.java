package com.facilon.app.module.client.validator;

import com.facilon.app.module.client.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InvestorValidatorTest {

    private InvestorValidator validator;

    @BeforeEach
    void setUp() {
        validator = new InvestorValidator();
    }

    @Test
    void validateResidentialStatus_Visa_MissingFields_ShouldThrow() {
        ResidentialStatusDto dto = ResidentialStatusDto.builder()
                .residentialStatus("resident")
                .userVisaNumber("V12345") // Visa number present implies Visa type logic
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.validateResidentialStatus(dto);
        });

        assertTrue(exception.getMessage().contains("Visa issuer date is required"));
    }

    @Test
    void validateResidentialStatus_Visa_Valid_ShouldPass() {
        ResidentialStatusDto dto = ResidentialStatusDto.builder()
                .residentialStatus("resident")
                .userVisaNumber("V12345")
                .userVisaIssuerDate(LocalDate.now())
                .userVisaExpiryDate(LocalDate.now().plusYears(1))
                .build();

        assertDoesNotThrow(() -> validator.validateResidentialStatus(dto));
    }

    @Test
    void validateBankDetails_SettlementYes_MissingFields_ShouldThrow() {
        BankDetailsDto dto = BankDetailsDto.builder()
                .settlementAccountType("yes")
                .bankName("My Bank")
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.validateBankDetails(dto);
        });

        assertTrue(exception.getMessage().contains("Beneficiary name is required"));
    }

    @Test
    void validateBankDetails_SettlementNo_ShouldPass() {
        BankDetailsDto dto = BankDetailsDto.builder()
                .settlementAccountType("no")
                .build();

        assertDoesNotThrow(() -> validator.validateBankDetails(dto));
    }

    @Test
    void validateContactDetails_CorrAddressDiff_MissingFields_ShouldThrow() {
        ContactDetailsDto dto = ContactDetailsDto.builder()
                .corrAddressSameAsPerm("false")
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.validateContactDetails(dto);
        });

        assertTrue(exception.getMessage().contains("Correspondence address line 1 is required"));
    }

    @Test
    void validateContactDetails_CorrAddressSame_ShouldPass() {
        ContactDetailsDto dto = ContactDetailsDto.builder()
                .corrAddressSameAsPerm("true")
                .build();

        assertDoesNotThrow(() -> validator.validateContactDetails(dto));
    }

    @Test
    void validateNomination_AppointTrue_NoNominee_ShouldThrow() {
        NominationDetailsDto dto = NominationDetailsDto.builder()
                .appointNominee(true)
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.validateNominationDetails(dto);
        });

        assertTrue(exception.getMessage().contains("At least one nominee is required"));
    }

    @Test
    void validateNomination_NomineePresent_MissingRelation_ShouldThrow() {
        NominationDetailsDto dto = NominationDetailsDto.builder()
                .appointNominee(true)
                .nomineeName1("John Doe")
                // Missing relation
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.validateNominationDetails(dto);
        });

        assertTrue(exception.getMessage().contains("Relationship for nominee 1 is required"));
    }
}
