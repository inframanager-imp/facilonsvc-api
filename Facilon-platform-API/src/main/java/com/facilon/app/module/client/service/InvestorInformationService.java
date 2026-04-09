package com.facilon.app.module.client.service;

import com.facilon.app.module.client.dto.*;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.model.InvestorInformationStatus;
import com.facilon.app.module.client.repository.InvestorInformationStatusRepository;
import com.facilon.app.module.client.repository.InvestorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestorInformationService {

    private final InvestorRepository investorRepository;
    private final InvestorInformationStatusRepository informationStatusRepository;
    private final com.facilon.app.module.client.validator.InvestorValidator validator;

    @Transactional
    public void submitPersonalInformation(String uniqueCode, PersonalInformationDto dto) {
        log.info("Submitting personal information for investor: {}", uniqueCode);
        validator.validatePersonalInformation(dto);
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        // TODO: Map DTO to Entity and Save to investor_personal_info table
        // For now, just update the status

        updateSectionStatus(investor, "personalInfo");
        log.info("Personal information submitted successfully for investor: {}", uniqueCode);
    }

    @Transactional
    public void submitPassportInformation(String uniqueCode, PassportInformationDto dto) {
        log.info("Submitting passport information for investor: {}", uniqueCode);
        validator.validatePassportInformation(dto);
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        // TODO: Save to investor_passport_info table

        updateSectionStatus(investor, "passport");
        log.info("Passport information submitted successfully for investor: {}", uniqueCode);
    }

    @Transactional
    public void submitResidentialStatus(String uniqueCode, ResidentialStatusDto dto) {
        log.info("Submitting residential status for investor: {}", uniqueCode);
        validator.validateResidentialStatus(dto);
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        // TODO: Save to investor_residential_status table

        updateSectionStatus(investor, "residential");
        log.info("Residential status submitted successfully for investor: {}", uniqueCode);
    }

    @Transactional
    public void submitTaxInformation(String uniqueCode, TaxInformationDto dto) {
        log.info("Submitting tax information for investor: {}", uniqueCode);
        validator.validateTaxInformation(dto);
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        // TODO: Save to investor_tax_info table

        updateSectionStatus(investor, "taxInfo");
        log.info("Tax information submitted successfully for investor: {}", uniqueCode);
    }

    @Transactional
    public void submitBankDetails(String uniqueCode, BankDetailsDto dto) {
        log.info("Submitting bank details for investor: {}", uniqueCode);
        validator.validateBankDetails(dto);
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        // TODO: Save to investor_bank_details table

        updateSectionStatus(investor, "bankDetails");
        log.info("Bank details submitted successfully for investor: {}", uniqueCode);
    }

    @Transactional
    public void submitContactDetails(String uniqueCode, ContactDetailsDto dto) {
        log.info("Submitting contact details for investor: {}", uniqueCode);
        validator.validateContactDetails(dto);
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        // TODO: Save to investor_contact_details table

        updateSectionStatus(investor, "contactDetails");
        log.info("Contact details submitted successfully for investor: {}", uniqueCode);
    }

    @Transactional
    public void submitNominationDetails(String uniqueCode, NominationDetailsDto dto) {
        log.info("Submitting nomination details for investor: {}", uniqueCode);
        validator.validateNominationDetails(dto);
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        // TODO: Save to investor_nomination table

        updateSectionStatus(investor, "nomination");
        log.info("Nomination details submitted successfully for investor: {}", uniqueCode);
    }

    @Transactional
    public void submitRiskProfile(String uniqueCode, RiskProfileDto dto) {
        log.info("Submitting risk profile for investor: {}", uniqueCode);
        validator.validateRiskProfile(dto);
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        // TODO: Save to investor_risk_profile table

        updateSectionStatus(investor, "riskProfile");
        log.info("Risk profile submitted successfully for investor: {}", uniqueCode);
    }

    @Transactional
    public void finalSubmit(String uniqueCode) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        // Verify all required sections are completed
        InvestorInformationStatus status = informationStatusRepository
                .findByInvestorEmail(
                        investor.getAuthorizedUser() != null ? investor.getAuthorizedUser().getEmailId() : "")
                .orElseThrow(() -> new RuntimeException("Information status not found"));

        if (!areAllRequiredSectionsCompleted(status)) {
            throw new RuntimeException("All required sections must be completed before final submission");
        }

        // TODO: Update investor status to indicate information collection is complete
        // TODO: Trigger next step in onboarding (document upload)

        log.info("Final information submission completed for investor: {}", uniqueCode);
    }

    private void updateSectionStatus(Investor investor, String section) {
        String investorEmail = investor.getAuthorizedUser() != null ? investor.getAuthorizedUser().getEmailId() : "";
        InvestorInformationStatus status = informationStatusRepository
                .findByInvestorEmail(investorEmail)
                .orElse(InvestorInformationStatus.builder().investorEmail(investorEmail).build());

        // Status: 1 = complete, 2 = incomplete (as per existing schema)
        switch (section) {
            case "personalInfo":
                status.setPersonalInfo(1);
                break;
            case "passport":
                status.setPassport(1);
                break;
            case "residential":
                status.setResidentialStatus(1);
                break;
            case "taxInfo":
                status.setTaxInformation(1);
                break;
            case "bankDetails":
                status.setBankDetails(1);
                break;
            case "contactDetails":
                status.setContactDetails(1);
                break;
            case "nomination":
                status.setNomination(1);
                break;
            case "riskProfile":
                status.setRiskProfile(1);
                break;
        }

        informationStatusRepository.save(status);
    }

    private boolean areAllRequiredSectionsCompleted(InvestorInformationStatus status) {
        return Integer.valueOf(1).equals(status.getPersonalInfo()) &&
                Integer.valueOf(1).equals(status.getPassport()) &&
                Integer.valueOf(1).equals(status.getResidentialStatus()) &&
                Integer.valueOf(1).equals(status.getTaxInformation()) &&
                Integer.valueOf(1).equals(status.getBankDetails()) &&
                Integer.valueOf(1).equals(status.getContactDetails()) &&
                Integer.valueOf(1).equals(status.getRiskProfile());
        // Note: nomination is optional
    }

    private Investor getInvestorByUniqueCode(String uniqueCode) {
        return investorRepository.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new RuntimeException("Investor not found with unique code: " + uniqueCode));
    }
}
