package com.facilon.app.module.client.service;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.integration.dynamics.DynamicsCrmService;
import com.facilon.app.module.client.dto.*;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.model.InvestorConsents;
import com.facilon.app.module.client.model.InvestorExperience;
import com.facilon.app.module.client.model.InvestorContactDetails;
import com.facilon.app.module.client.model.InvestorInformationStatus;
import com.facilon.app.module.client.model.InvestorNomination;
import com.facilon.app.module.client.model.InvestorResidentialStatus;
import com.facilon.app.module.client.model.InvestorRiskProfile;
import com.facilon.app.module.client.model.InvestorTaxInformation;
import com.facilon.app.module.client.model.InvestorBankDetails;
import com.facilon.app.module.client.model.UserPassportDetails;
import com.facilon.app.module.client.model.UserPersonalInformation;
import com.facilon.app.module.client.model.IntroInvestorTemp;
import com.facilon.app.module.client.model.master.MasterAccounts;
import com.facilon.app.module.client.model.master.MasterBanks;
import com.facilon.app.module.client.model.master.MasterBrokerBanks;
import com.facilon.app.module.client.repository.IntroInvestorTempRepository;
import com.facilon.app.module.client.repository.InvestorBankDetailsRepository;
import com.facilon.app.module.client.repository.InvestorContactDetailsRepository;
import com.facilon.app.module.client.repository.InvestorConsentsRepository;
import com.facilon.app.module.client.repository.InvestorExperienceRepository;
import com.facilon.app.module.client.repository.InvestorNominationRepository;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.module.client.repository.InvestorResidentialStatusRepository;
import com.facilon.app.module.client.repository.InvestorRiskProfileRepository;
import com.facilon.app.module.client.repository.InvestorTaxInformationRepository;
import com.facilon.app.module.client.repository.KycDocumentsRepository;
import com.facilon.app.module.client.repository.MasterAccountsRepository;
import com.facilon.app.module.client.repository.MasterBanksRepository;
import com.facilon.app.module.client.repository.MasterBrokerBanksRepository;
import com.facilon.app.module.client.repository.UserPassportDetailsRepository;
import com.facilon.app.module.client.repository.UserPersonalInformationRepository;
import com.facilon.app.module.client.repository.InvestorInformationStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClientProfileService {

    private final InvestorRepository investorRepository;
    private final UserPersonalInformationRepository personalInfoRepository;
    private final UserPassportDetailsRepository passportRepository;
    private final InvestorExperienceRepository experienceRepository;
    private final InvestorConsentsRepository consentsRepository;
    private final InvestorResidentialStatusRepository residentialStatusRepository;
    private final InvestorTaxInformationRepository taxInformationRepository;
    private final InvestorBankDetailsRepository bankDetailsRepository;
    private final InvestorContactDetailsRepository contactDetailsRepository;
    private final InvestorNominationRepository nominationRepository;
    private final InvestorRiskProfileRepository riskProfileRepository;
    private final KycDocumentsRepository kycDocumentsRepository;
    private final InvestorNotificationService notificationService;
    private final InvestorInformationStatusRepository statusRepository;

    // Preferred-bank resolver chain (Laravel parity: broker_prefferedbank → master_broker_banks → master_banks → master_accounts)
    private final IntroInvestorTempRepository introInvestorTempRepository;
    private final MasterBrokerBanksRepository masterBrokerBanksRepository;
    private final MasterBanksRepository masterBanksRepository;
    private final MasterAccountsRepository masterAccountsRepository;
    
    @Autowired(required = false)
    private DynamicsCrmService dynamicsCrmService;
    
    @Autowired(required = false)
    private InvestorInformationSubmissionService submissionService;

    private static final int KYC_DOCUMENTS_REQUIRED = 2; // Minimum required documents (e.g. PAN + Aadhar)

    /**
     * Resolve investor from user ID. Throws if not found.
     */
    public Investor getInvestorForUser(Long userId) {
        return investorRepository.findByAuthorizedUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Investor not found for user"));
    }

    /**
     * Admin: Resolve investor from client ID and verify tenant. Throws if not
     * found.
     */
    public Investor getInvestorForAdmin(Long clientId) {
        Investor investor = investorRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client/Investor not found: " + clientId));
        Long tenantId = TenantContextHolder.getContext().getTenant().getTenantId();
        if (investor.getTenant() == null || !investor.getTenant().getTenantId().equals(tenantId)) {
            throw new RuntimeException("Client not found in current tenant");
        }
        return investor;
    }

    /**
     * Admin: Get full profile (personal info, passport, experience, consents) for a
     * client.
     */
    @Transactional(readOnly = true)
    public InvestorFullProfileDto getFullProfileForAdmin(Long clientId) {
        Investor investor = getInvestorForAdmin(clientId);
        Long userId = investor.getAuthorizedUser().getId();
        return InvestorFullProfileDto.builder()
                .personalInfo(getPersonalInfo(userId).orElse(null))
                .passport(getPassport(userId).orElse(null))
                .experience(getExperience(userId).orElse(null))
                .consents(getConsents(userId).orElse(null))
                .build();
    }

    /**
     * Get onboarding progress status for the authenticated investor.
     */
    @Transactional(readOnly = true)
    public OnboardingStatusDto getOnboardingStatus(Long userId) {
        Investor investor = getInvestorForUser(userId);
        String uniqueCode = investor.getUniqueCode();

        boolean personalInfoComplete = personalInfoRepository.findByInvestorUniqueId(uniqueCode)
                .map(info -> isNotBlank(info.getInvestorFirstName()) && isNotBlank(info.getInvestorLastName()))
                .orElse(false);

        boolean passportComplete = passportRepository.findByInvestorUniqueCode(uniqueCode)
                .map(p -> isNotBlank(p.getPassportNumber()))
                .orElse(false);

        boolean experienceComplete = experienceRepository.findByInvestorUniqueId(uniqueCode)
                .map(e -> isNotBlank(e.getOccupation()) || isNotBlank(e.getAnnualIncome())
                        || isNotBlank(e.getNetWorth()))
                .orElse(false);

        boolean consentsComplete = consentsRepository.findByInvestorUniqueId(uniqueCode)
                .map(c -> Boolean.TRUE.equals(c.getTermsAccepted())
                        && Boolean.TRUE.equals(c.getPrivacyPolicyAccepted()))
                .orElse(false);

        int kycCount = kycDocumentsRepository.findByInvestorUniqueIdAndDeletedAtIsNull(uniqueCode).size();
        boolean kycDocumentsComplete = kycCount >= KYC_DOCUMENTS_REQUIRED;

        int completed = (personalInfoComplete ? 1 : 0) + (passportComplete ? 1 : 0) + (experienceComplete ? 1 : 0)
                + (consentsComplete ? 1 : 0) + (kycDocumentsComplete ? 1 : 0);
        int total = 5;
        int percentage = total > 0 ? (completed * 100) / total : 0;

        List<String> nextSteps = new ArrayList<>();
        if (!personalInfoComplete)
            nextSteps.add("Complete your personal information in Profile");
        if (!passportComplete)
            nextSteps.add("Add passport details (optional) in Profile");
        if (!experienceComplete)
            nextSteps.add("Fill in investment experience in Profile");
        if (!consentsComplete)
            nextSteps.add("Accept terms and privacy policy in Profile");
        if (!kycDocumentsComplete)
            nextSteps.add("Upload at least " + KYC_DOCUMENTS_REQUIRED + " KYC documents");

        return OnboardingStatusDto.builder()
                .totalSteps(total)
                .completedSteps(completed)
                .percentageComplete(percentage)
                .personalInfoComplete(personalInfoComplete)
                .passportComplete(passportComplete)
                .experienceComplete(experienceComplete)
                .consentsComplete(consentsComplete)
                .kycDocumentsComplete(kycDocumentsComplete)
                .kycDocumentsUploaded(kycCount)
                .kycDocumentsRequired(KYC_DOCUMENTS_REQUIRED)
                .nextSteps(nextSteps)
                .build();
    }

    private static boolean isNotBlank(String s) {
        return s != null && !s.isBlank();
    }

    /**
     * Get personal information for the authenticated investor.
     */
    @Transactional(readOnly = true)
    public Optional<UserPersonalInformationDto> getPersonalInfo(Long userId) {
        Investor investor = getInvestorForUser(userId);
        return personalInfoRepository.findByInvestorUniqueId(investor.getUniqueCode())
                .map(info -> {
                    UserPersonalInformationDto dto = toPersonalInfoDto(info);
                    // Onboarding stores citizenship on investor (id); personal row may be empty until first profile save
                    if (!isNotBlank(dto.getCitizenship()) && investor.getCitizenship() != null) {
                        dto.setCitizenship(String.valueOf(investor.getCitizenship()));
                    }
                    return dto;
                });
    }

    /**
     * Update personal information. Creates or updates the record.
     */
    public UserPersonalInformationDto updatePersonalInfo(Long userId, UserPersonalInformationDto dto) {
        Investor investor = getInvestorForUser(userId);
        var tenant = TenantContextHolder.getContext().getTenant();

        UserPersonalInformation info = personalInfoRepository.findByInvestorUniqueId(investor.getUniqueCode())
                .orElseGet(() -> {
                    UserPersonalInformation p = UserPersonalInformation.builder()
                            .investorUniqueId(investor.getUniqueCode())
                            .investorId(String.valueOf(investor.getId()))
                            .build();
                    p.setTenant(tenant);
                    return p;
                });

        if (dto.getInvestorFirstName() != null)
            info.setInvestorFirstName(dto.getInvestorFirstName());
        if (dto.getInvestorMiddleName() != null)
            info.setInvestorMiddleName(dto.getInvestorMiddleName());
        if (dto.getInvestorLastName() != null)
            info.setInvestorLastName(dto.getInvestorLastName());
        if (dto.getInvestorGender() != null)
            info.setInvestorGender(dto.getInvestorGender());
        if (dto.getGender() != null)
            info.setGender(dto.getGender());
        if (dto.getMaritalStatus() != null)
            info.setMaritalStatus(dto.getMaritalStatus());
        if (dto.getMaidenTitle() != null)
            info.setMaidenTitle(dto.getMaidenTitle());
        if (dto.getMaidenName() != null)
            info.setMaidenName(dto.getMaidenName());
        if (dto.getMaidenMiddleName() != null)
            info.setMaidenMiddleName(dto.getMaidenMiddleName());
        if (dto.getMaidenLastName() != null)
            info.setMaidenLastName(dto.getMaidenLastName());
        if (dto.getCityOfDob() != null)
            info.setCityOfDob(dto.getCityOfDob());
        if (dto.getCountryDob() != null)
            info.setCountryDob(dto.getCountryDob());
        if (dto.getCitizenship() != null)
            info.setCitizenship(dto.getCitizenship());
        if (dto.getFatherNameTitle() != null)
            info.setFatherNameTitle(dto.getFatherNameTitle());
        if (dto.getFathersFirstName() != null)
            info.setFathersFirstName(dto.getFathersFirstName());
        if (dto.getFathersMiddleName() != null)
            info.setFathersMiddleName(dto.getFathersMiddleName());
        if (dto.getFathersLastName() != null)
            info.setFathersLastName(dto.getFathersLastName());
        if (dto.getMotherNameTitle() != null)
            info.setMotherNameTitle(dto.getMotherNameTitle());
        if (dto.getMotherFirstName() != null)
            info.setMotherFirstName(dto.getMotherFirstName());
        if (dto.getMotherMiddleName() != null)
            info.setMotherMiddleName(dto.getMotherMiddleName());
        if (dto.getMotherLastName() != null)
            info.setMotherLastName(dto.getMotherLastName());
        if (dto.getSpouseNameTitle() != null)
            info.setSpouseNameTitle(dto.getSpouseNameTitle());
        if (dto.getSpouseName() != null)
            info.setSpouseName(dto.getSpouseName());
        if (dto.getSpouseMiddleName() != null)
            info.setSpouseMiddleName(dto.getSpouseMiddleName());
        if (dto.getSpouseLastName() != null)
            info.setSpouseLastName(dto.getSpouseLastName());
        if (dto.getSpouseMaidenName() != null)
            info.setSpouseMaidenName(dto.getSpouseMaidenName());
        if (dto.getUserDob() != null)
            info.setUserDob(dto.getUserDob());
        if (dto.getAddressLine1() != null)
            info.setAddressLine1(dto.getAddressLine1());
        if (dto.getAddressLine2() != null)
            info.setAddressLine2(dto.getAddressLine2());
        if (dto.getAddressLine3() != null)
            info.setAddressLine3(dto.getAddressLine3());
        if (dto.getUserCity() != null)
            info.setUserCity(dto.getUserCity());
        if (dto.getUserState() != null)
            info.setUserState(dto.getUserState());
        if (dto.getUserCountry() != null)
            info.setUserCountry(dto.getUserCountry());
        if (dto.getUserZipCode() != null)
            info.setUserZipCode(dto.getUserZipCode());
        if (dto.getCorrAddressLine1() != null)
            info.setCorrAddressLine1(dto.getCorrAddressLine1());
        if (dto.getCorrAddressLine2() != null)
            info.setCorrAddressLine2(dto.getCorrAddressLine2());
        if (dto.getCorrAddressLine3() != null)
            info.setCorrAddressLine3(dto.getCorrAddressLine3());
        if (dto.getCorrUserCity() != null)
            info.setCorrUserCity(dto.getCorrUserCity());
        if (dto.getCorrUserState() != null)
            info.setCorrUserState(dto.getCorrUserState());
        if (dto.getCorrUserCountry() != null)
            info.setCorrUserCountry(dto.getCorrUserCountry());
        if (dto.getCorrUserZipCode() != null)
            info.setCorrUserZipCode(dto.getCorrUserZipCode());
        if (dto.getUserAadharNo() != null)
            info.setUserAadharNo(dto.getUserAadharNo());
        if (dto.getUserPanNo() != null)
            info.setUserPanNo(dto.getUserPanNo());
        if (dto.getUserOciCardNo() != null)
            info.setUserOciCardNo(dto.getUserOciCardNo());
        if (dto.getUserOciIssueDate() != null)
            info.setUserOciIssueDate(dto.getUserOciIssueDate());
        if (dto.getUserOciValidUpto() != null)
            info.setUserOciValidUpto(dto.getUserOciValidUpto());
        if (dto.getUserVisaType() != null)
            info.setUserVisaType(dto.getUserVisaType());
        if (dto.getUserVisaNumber() != null)
            info.setUserVisaNumber(dto.getUserVisaNumber());
        if (dto.getUserVisaIssuerDate() != null)
            info.setUserVisaIssuerDate(dto.getUserVisaIssuerDate());
        if (dto.getUserVisaExpiryDate() != null)
            info.setUserVisaExpiryDate(dto.getUserVisaExpiryDate());
        if (dto.getCountryOfResidence() != null)
            info.setCountryOfResidence(dto.getCountryOfResidence());
        if (dto.getProofOfAddress() != null)
            info.setProofOfAddress(dto.getProofOfAddress());
        if (dto.getAddressType() != null)
            info.setAddressType(dto.getAddressType());

        info.setTenant(tenant);
        info = personalInfoRepository.save(info);

        // Update status
        updateSectionStatus(investor, "personalInfo");

        return toPersonalInfoDto(info);
    }

    /**
     * Get passport details for the authenticated investor.
     */
    @Transactional(readOnly = true)
    public Optional<UserPassportDetailsDto> getPassport(Long userId) {
        Investor investor = getInvestorForUser(userId);
        return passportRepository.findByInvestorUniqueCode(investor.getUniqueCode())
                .map(this::toPassportDto);
    }

    /**
     * Update passport details. Creates or updates the record.
     */
    public UserPassportDetailsDto updatePassport(Long userId, UserPassportDetailsDto dto) {
        Investor investor = getInvestorForUser(userId);
        var tenant = TenantContextHolder.getContext().getTenant();

        UserPassportDetails passport = passportRepository.findByInvestorUniqueCode(investor.getUniqueCode())
                .orElseGet(() -> {
                    UserPassportDetails p = UserPassportDetails.builder()
                            .investorUniqueCode(investor.getUniqueCode())
                            .investorId(String.valueOf(investor.getId()))
                            .build();
                    p.setTenant(tenant);
                    return p;
                });

        if (dto.getPassportNumber() != null)
            passport.setPassportNumber(dto.getPassportNumber());
        if (dto.getPassportIssueDate() != null)
            passport.setPassportIssueDate(dto.getPassportIssueDate());
        if (dto.getPassportExpiryDate() != null)
            passport.setPassportExpiryDate(dto.getPassportExpiryDate());
        if (dto.getPassportPlaceOfIssue() != null)
            passport.setPassportPlaceOfIssue(dto.getPassportPlaceOfIssue());
        if (dto.getPassportCountryOfIssue() != null)
            passport.setPassportCountryOfIssue(dto.getPassportCountryOfIssue());
        if (dto.getPassportNationality() != null)
            passport.setPassportNationality(dto.getPassportNationality());
        if (dto.getPassportDateNonResident() != null)
            passport.setPassportDateNonResident(dto.getPassportDateNonResident());
        if (dto.getPassportNoYearsAbroad() != null)
            passport.setPassportNoYearsAbroad(dto.getPassportNoYearsAbroad());
        if (dto.getPassportFrontCopy() != null)
            passport.setPassportFrontCopy(dto.getPassportFrontCopy());
        if (dto.getPassportBackCopy() != null)
            passport.setPassportBackCopy(dto.getPassportBackCopy());
        if (dto.getDocumentType() != null)
            passport.setDocumentType(dto.getDocumentType().isBlank() ? null : dto.getDocumentType());

        passport.setTenant(tenant);
        passport = passportRepository.save(passport);

        // Update status
        updateSectionStatus(investor, "passport");

        return toPassportDto(passport);
    }

    /**
     * Get investor experience for the authenticated investor.
     */
    @Transactional(readOnly = true)
    public Optional<InvestorExperienceDto> getExperience(Long userId) {
        Investor investor = getInvestorForUser(userId);
        return experienceRepository.findByInvestorUniqueId(investor.getUniqueCode())
                .map(this::toExperienceDto);
    }

    /**
     * Update investor experience. Creates or updates the record.
     */
    public InvestorExperienceDto updateExperience(Long userId, InvestorExperienceDto dto) {
        Investor investor = getInvestorForUser(userId);
        var tenant = TenantContextHolder.getContext().getTenant();

        InvestorExperience exp = experienceRepository.findByInvestorUniqueId(investor.getUniqueCode())
                .orElseGet(() -> {
                    InvestorExperience e = InvestorExperience.builder()
                            .investorUniqueId(investor.getUniqueCode())
                            .build();
                    e.setTenant(tenant);
                    return e;
                });

        if (dto.getEducationalQualification() != null)
            exp.setEducationalQualification(dto.getEducationalQualification());
        if (dto.getAnnualIncome() != null)
            exp.setAnnualIncome(dto.getAnnualIncome());
        if (dto.getNetWorth() != null)
            exp.setNetWorth(dto.getNetWorth());
        if (dto.getInvestmentObjective() != null)
            exp.setInvestmentObjective(dto.getInvestmentObjective());
        if (dto.getRiskTolerance() != null)
            exp.setRiskTolerance(dto.getRiskTolerance());
        if (dto.getRiskProfile() != null)
            exp.setRiskProfile(dto.getRiskProfile());
        if (dto.getInvestmentExperienceIn() != null && !dto.getInvestmentExperienceIn().isEmpty())
            exp.setInvestmentExperience(String.join(",", dto.getInvestmentExperienceIn()));
        if (dto.getInvestmentHorizon() != null)
            exp.setInvestmentHorizon(dto.getInvestmentHorizon());
        if (dto.getYearsOfExperience() != null) {
            exp.setYearsOfExperience(dto.getYearsOfExperience());
        } else if (dto.getInvestmentExperienceYears() != null) {
            exp.setYearsOfExperience(mapExperienceYearsLabelToBucket(dto.getInvestmentExperienceYears()));
        }
        if (dto.getPreviousInvestments() != null)
            exp.setPreviousInvestments(dto.getPreviousInvestments());
        if (dto.getOccupation() != null)
            exp.setOccupation(dto.getOccupation());
        if (dto.getEmployerName() != null)
            exp.setEmployerName(dto.getEmployerName());
        if (dto.getDesignation() != null)
            exp.setDesignation(dto.getDesignation());
        if (dto.getSourceOfFunds() != null)
            exp.setSourceOfFunds(dto.getSourceOfFunds());
        if (dto.getSourceOfFundsDetails() != null)
            exp.setSourceOfFundsDetails(dto.getSourceOfFundsDetails());
        if (dto.getExpectedInvestmentAmount() != null)
            exp.setExpectedInvestmentAmount(dto.getExpectedInvestmentAmount());

        exp.setTenant(tenant);
        exp = experienceRepository.save(exp);

        InvestorRiskProfile risk = riskProfileRepository.findByInvestorUniqueId(investor.getUniqueCode())
                .orElseGet(() -> {
                    InvestorRiskProfile r = InvestorRiskProfile.builder()
                            .investorUniqueId(investor.getUniqueCode())
                            .build();
                    r.setTenant(tenant);
                    return r;
                });

        if (dto.getSourceOfWealth() != null)
            risk.setSourceOfWealth(dto.getSourceOfWealth());
        if (dto.getLineOfBusiness() != null)
            risk.setLineOfBusiness(dto.getLineOfBusiness());
        if (dto.getNatureOfOrganisation() != null)
            risk.setNatureOfOrganisation(dto.getNatureOfOrganisation());
        if (dto.getPolExposed() != null)
            risk.setPolExposed(dto.getPolExposed());
        if (dto.getPolExposedRelated() != null)
            risk.setPolExposedRelated(dto.getPolExposedRelated());
        if (dto.getActivity() != null)
            risk.setActivity(dto.getActivity());
        if (dto.getMoneyChangeService() != null)
            risk.setMoneyChangeService(dto.getMoneyChangeService());
        if (dto.getGamblingService() != null)
            risk.setGamblingService(dto.getGamblingService());
        if (dto.getPawningService() != null)
            risk.setPawningService(dto.getPawningService());
        if (dto.getInstanceViolation() != null)
            risk.setInstanceViolation(dto.getInstanceViolation());
        if (dto.getInvestmentExperienceYears() != null) {
            risk.setInvestmentExperienceYears(mapExperienceYearsLabelToBucket(dto.getInvestmentExperienceYears()));
        }

        risk.setTenant(tenant);
        riskProfileRepository.save(risk);

        updateSectionStatus(investor, "riskProfile");

        return toExperienceDto(exp);
    }

    /**
     * Get consents for the authenticated investor.
     */
    @Transactional(readOnly = true)
    public Optional<InvestorConsentsDto> getConsents(Long userId) {
        Investor investor = getInvestorForUser(userId);
        return consentsRepository.findByInvestorUniqueId(investor.getUniqueCode())
                .map(this::toConsentsDto);
    }

    /**
     * Record consent. Creates or updates the record.
     * userDob is fetched from personal info when not provided.
     */
    public InvestorConsentsDto recordConsent(Long userId, InvestorConsentsDto dto) {
        Investor investor = getInvestorForUser(userId);
        var tenant = TenantContextHolder.getContext().getTenant();

        InvestorConsents consents = consentsRepository.findByInvestorUniqueId(investor.getUniqueCode())
                .orElseGet(() -> {
                    InvestorConsents c = InvestorConsents.builder()
                            .investorUniqueId(investor.getUniqueCode())
                            .build();
                    c.setTenant(tenant);
                    return c;
                });

        LocalDate userDob = dto.getUserDob();
        if (userDob == null) {
            userDob = personalInfoRepository.findByInvestorUniqueId(investor.getUniqueCode())
                    .map(UserPersonalInformation::getUserDob)
                    .orElse(null);
        }
        if (userDob != null)
            consents.setUserDob(userDob);

        if (dto.getEmail() != null)
            consents.setEmail(dto.getEmail());
        if (dto.getTermsAccepted() != null)
            consents.setTermsAccepted(dto.getTermsAccepted());
        if (dto.getPrivacyPolicyAccepted() != null)
            consents.setPrivacyPolicyAccepted(dto.getPrivacyPolicyAccepted());
        if (dto.getMarketingConsent() != null)
            consents.setMarketingConsent(dto.getMarketingConsent());
        if (dto.getDataSharingConsent() != null)
            consents.setDataSharingConsent(dto.getDataSharingConsent());
        if (dto.getIpAddress() != null)
            consents.setIpAddress(dto.getIpAddress());
        if (dto.getUserAgent() != null)
            consents.setUserAgent(dto.getUserAgent());
        if (dto.getConsentVersion() != null)
            consents.setConsentVersion(dto.getConsentVersion());

        if (consents.getUserDob() == null) {
            throw new IllegalStateException(
                    "Date of birth is required for consent. Please complete personal information first.");
        }

        consents.setTenant(tenant);
        consents = consentsRepository.save(consents);
        return toConsentsDto(consents);
    }

    private InvestorExperienceDto toExperienceDto(InvestorExperience e) {
        List<String> experienceList = null;
        if (e.getInvestmentExperience() != null && !e.getInvestmentExperience().isEmpty()) {
            experienceList = Arrays.asList(e.getInvestmentExperience().split(","));
        }
        
        var builder = InvestorExperienceDto.builder()
                .id(e.getId())
                .investorUniqueId(e.getInvestorUniqueId())
                .educationalQualification(e.getEducationalQualification())
                .annualIncome(e.getAnnualIncome())
                .netWorth(e.getNetWorth())
                .investmentObjective(e.getInvestmentObjective())
                .riskTolerance(e.getRiskTolerance())
                .riskProfile(e.getRiskProfile())
                .investmentExperienceIn(experienceList)
                .investmentHorizon(e.getInvestmentHorizon())
                .yearsOfExperience(e.getYearsOfExperience())
                .investmentExperienceYears(mapExperienceYearsBucketToLabel(e.getYearsOfExperience()))
                .previousInvestments(e.getPreviousInvestments())
                .occupation(e.getOccupation())
                .employerName(e.getEmployerName())
                .designation(e.getDesignation())
                .sourceOfFunds(e.getSourceOfFunds())
                .sourceOfFundsDetails(e.getSourceOfFundsDetails())
                .expectedInvestmentAmount(e.getExpectedInvestmentAmount());

        riskProfileRepository.findByInvestorUniqueId(e.getInvestorUniqueId())
                .ifPresent(risk -> {
                    builder.sourceOfWealth(risk.getSourceOfWealth())
                            .lineOfBusiness(risk.getLineOfBusiness())
                            .natureOfOrganisation(risk.getNatureOfOrganisation())
                            .polExposed(risk.getPolExposed())
                            .polExposedRelated(risk.getPolExposedRelated())
                            .activity(risk.getActivity())
                            .moneyChangeService(risk.getMoneyChangeService())
                            .gamblingService(risk.getGamblingService())
                            .pawningService(risk.getPawningService())
                            .instanceViolation(risk.getInstanceViolation());
                });

        return builder.build();
    }

    private InvestorConsentsDto toConsentsDto(InvestorConsents c) {
        return InvestorConsentsDto.builder()
                .id(c.getId())
                .investorUniqueId(c.getInvestorUniqueId())
                .email(c.getEmail())
                .userDob(c.getUserDob())
                .informationCorrectConsent(c.getInformationCorrectConsent())
                .termsAccepted(c.getTermsAccepted())
                .privacyPolicyAccepted(c.getPrivacyPolicyAccepted())
                .legalCapacityConsent(c.getLegalCapacityConsent())
                .modificationAwarenessConsent(c.getModificationAwarenessConsent())
                .marketingConsent(c.getMarketingConsent())
                .dataSharingConsent(c.getDataSharingConsent())
                .consentDate(c.getConsentDate())
                .ipAddress(c.getIpAddress())
                .userAgent(c.getUserAgent())
                .consentVersion(c.getConsentVersion())
                .build();
    }

    private UserPersonalInformationDto toPersonalInfoDto(UserPersonalInformation info) {
        return UserPersonalInformationDto.builder()
                .id(info.getId())
                .investorUniqueId(info.getInvestorUniqueId())
                .investorId(info.getInvestorId())
                .investorFirstName(info.getInvestorFirstName())
                .investorMiddleName(info.getInvestorMiddleName())
                .investorLastName(info.getInvestorLastName())
                .investorGender(info.getInvestorGender())
                .gender(info.getGender())
                .maritalStatus(info.getMaritalStatus())
                .maidenTitle(info.getMaidenTitle())
                .maidenName(info.getMaidenName())
                .maidenMiddleName(info.getMaidenMiddleName())
                .maidenLastName(info.getMaidenLastName())
                .cityOfDob(info.getCityOfDob())
                .countryDob(info.getCountryDob())
                .citizenship(info.getCitizenship())
                .fatherNameTitle(info.getFatherNameTitle())
                .fathersFirstName(info.getFathersFirstName())
                .fathersMiddleName(info.getFathersMiddleName())
                .fathersLastName(info.getFathersLastName())
                .motherNameTitle(info.getMotherNameTitle())
                .motherFirstName(info.getMotherFirstName())
                .motherMiddleName(info.getMotherMiddleName())
                .motherLastName(info.getMotherLastName())
                .spouseNameTitle(info.getSpouseNameTitle())
                .spouseName(info.getSpouseName())
                .spouseMiddleName(info.getSpouseMiddleName())
                .spouseLastName(info.getSpouseLastName())
                .spouseMaidenName(info.getSpouseMaidenName())
                .userDob(info.getUserDob())
                .addressLine1(info.getAddressLine1())
                .addressLine2(info.getAddressLine2())
                .addressLine3(info.getAddressLine3())
                .userCity(info.getUserCity())
                .userState(info.getUserState())
                .userCountry(info.getUserCountry())
                .userZipCode(info.getUserZipCode())
                .corrAddressLine1(info.getCorrAddressLine1())
                .corrAddressLine2(info.getCorrAddressLine2())
                .corrAddressLine3(info.getCorrAddressLine3())
                .corrUserCity(info.getCorrUserCity())
                .corrUserState(info.getCorrUserState())
                .corrUserCountry(info.getCorrUserCountry())
                .corrUserZipCode(info.getCorrUserZipCode())
                .userAadharNo(info.getUserAadharNo())
                .userPanNo(info.getUserPanNo())
                .userOciCardNo(info.getUserOciCardNo())
                .userOciIssueDate(info.getUserOciIssueDate())
                .userOciValidUpto(info.getUserOciValidUpto())
                .userVisaType(info.getUserVisaType())
                .userVisaNumber(info.getUserVisaNumber())
                .userVisaIssuerDate(info.getUserVisaIssuerDate())
                .userVisaExpiryDate(info.getUserVisaExpiryDate())
                .countryOfResidence(info.getCountryOfResidence())
                .proofOfAddress(info.getProofOfAddress())
                .addressType(info.getAddressType())
                .createdAt(info.getCreatedAt())
                .updatedAt(info.getUpdatedAt())
                .build();
    }

    private UserPassportDetailsDto toPassportDto(UserPassportDetails p) {
        return UserPassportDetailsDto.builder()
                .id(p.getId())
                .investorUniqueCode(p.getInvestorUniqueCode())
                .investorId(p.getInvestorId())
                .passportNumber(p.getPassportNumber())
                .passportIssueDate(p.getPassportIssueDate())
                .passportExpiryDate(p.getPassportExpiryDate())
                .passportPlaceOfIssue(p.getPassportPlaceOfIssue())
                .passportCountryOfIssue(p.getPassportCountryOfIssue())
                .passportNationality(p.getPassportNationality())
                .passportDateNonResident(p.getPassportDateNonResident())
                .passportNoYearsAbroad(p.getPassportNoYearsAbroad())
                .passportFrontCopy(p.getPassportFrontCopy())
                .passportBackCopy(p.getPassportBackCopy())
                .documentType(p.getDocumentType())
                .build();
    }

    // Residential Status
    public Optional<UserResidentialStatusDto> getResidentialStatus(Long userId) {
        Investor investor = getInvestorForUser(userId);
        return residentialStatusRepository.findByInvestorUniqueId(investor.getUniqueCode())
                .map(this::toResidentialDto);
    }

    public UserResidentialStatusDto updateResidentialStatus(Long userId, UserResidentialStatusDto dto) {
        Investor investor = getInvestorForUser(userId);
        var tenant = TenantContextHolder.getContext().getTenant();

        InvestorResidentialStatus status = residentialStatusRepository.findByInvestorUniqueId(investor.getUniqueCode())
                .orElseGet(() -> {
                    InvestorResidentialStatus s = InvestorResidentialStatus.builder()
                            .investorUniqueId(investor.getUniqueCode())
                            .build();
                    s.setTenant(tenant);
                    return s;
                });

        status.setResidentialStatus(firstNonBlank(dto.getResidentialStatus(), dto.getResidenceType()));
        status.setCountryOfResidence(dto.getCountry() != null ? dto.getCountry() : parseInteger(dto.getResidenceCountry()));
        status.setResidenceAddress(firstNonBlank(dto.getAddressLine1(), dto.getResidenceAddress()));
        status.setResidenceCity(firstNonBlank(dto.getCity(), dto.getResidenceCity()));
        status.setResidenceState(firstNonBlank(dto.getState() != null ? String.valueOf(dto.getState()) : null, dto.getResidenceState()));
        status.setResidencePostalCode(firstNonBlank(dto.getPostalCode(), dto.getResidencePostalCode()));
        status.setResidencePhone(dto.getResidencePhone());
        status.setResidenceSince(dto.getResidenceSince());
        status.setAddressProofType(dto.getAddressProofType());
        status.setAddressProofDocumentId(dto.getAddressProofDocumentId());
        status.setYearsAtCurrentAddress(dto.getYearsAtCurrentAddress());
        status.setUserTypeOfProof(dto.getUserTypeOfProof());
        status.setUserVisaType(dto.getUserVisaType());
        status.setUserVisaNumber(dto.getUserVisaNumber());
        status.setUserVisaIssuerDate(dto.getUserVisaIssuerDate());
        status.setUserVisaExpiryDate(dto.getUserVisaExpiryDate());
        status.setUserVisaDateOfIssue(dto.getUserVisaDateOfIssue());
        status.setUserVisaValidUpto(dto.getUserVisaValidUpto());
        status.setUserOciCardNo(dto.getUserOciCardNo());
        status.setUserOciIssueDate(dto.getUserOciIssueDate());
        status.setUserOciValidUpto(dto.getUserOciValidUpto());
        status.setPersonOrigin(dto.getPersonOrigin());
        status.setProofOfAddress(dto.getProofOfAddress());
        status.setAadharNumberOption(dto.getAadharNumberOption());
        status.setAadharNumber(dto.getAadharNumber());
        status.setUserAadharNo(dto.getUserAadharNo());
        status.setOciAvailable(dto.getOciAvailable());
        status.setDateOfOci(dto.getDateOfOci() != null && !dto.getDateOfOci().isBlank() ? java.time.LocalDate.parse(dto.getDateOfOci()) : null);
        status.setTenant(tenant);
        status = residentialStatusRepository.save(status);

        log.info("Updated residential status for user: {}", userId);

        // Update status for residentialStatus
        updateSectionStatus(investor, "residentialStatus");

        return toResidentialDto(status);
    }

    // Tax Information
    public Optional<UserTaxInfoDto> getTaxInfo(Long userId) {
        Investor investor = getInvestorForUser(userId);
        return taxInformationRepository.findByInvestorUniqueId(investor.getUniqueCode())
                .map(this::toTaxDto);
    }

    public UserTaxInfoDto updateTaxInfo(Long userId, UserTaxInfoDto dto) {
        Investor investor = getInvestorForUser(userId);
        var tenant = TenantContextHolder.getContext().getTenant();

        InvestorTaxInformation taxInfo = taxInformationRepository.findByInvestorUniqueId(investor.getUniqueCode())
                .orElseGet(() -> {
                    InvestorTaxInformation t = InvestorTaxInformation.builder()
                            .investorUniqueId(investor.getUniqueCode())
                            .build();
                    t.setTenant(tenant);
                    return t;
                });

        taxInfo.setPanNumber(dto.getPanNumber());
        taxInfo.setTaxIdentificationNumber(firstNonBlank(dto.getTaxIdNumber(), dto.getTinNumber()));
        taxInfo.setTaxCountry(dto.getTaxResidencyCountryId() != null ? dto.getTaxResidencyCountryId()
                : parseInteger(dto.getTaxResidencyCountry()));
        taxInfo.setTaxResidencyStatus(dto.getTaxResidencyStatus());
        taxInfo.setGstNumber(dto.getGstNumber());
        taxInfo.setIncomeSource(dto.getIncomeSource());
        taxInfo.setAnnualIncome(dto.getAnnualIncome());
        taxInfo.setTaxIdentificationNumberType(dto.getTaxIdentificationNumberType());
        taxInfo.setFatcaStatus(dto.getFatcaStatus());
        taxInfo.setCrsDeclaration(dto.getCrsDeclaration());
        taxInfo.setUsCitizen(dto.getUsCitizen());
        taxInfo.setUsResident(dto.getUsResident());
        taxInfo.setTaxPanFirstName(dto.getTaxPanFirstName());
        taxInfo.setTaxPanFatherName(dto.getTaxPanFatherName());
        taxInfo.setTaxResidencyCertificateNo(dto.getTaxResidencyCertificateNo());
        taxInfo.setTaxResidencyCertificateDate(dto.getTaxResidencyCertificateDate());
        taxInfo.setTaxInfo(dto.getTaxInfo());
        taxInfo.setTaxPanNo(dto.getTaxPanNo());
        taxInfo.setUsPersonFatca(dto.getUsPersonFatca());
        taxInfo.setIsTaxExempt("exempt".equalsIgnoreCase(dto.getTaxResidencyStatus()));
        taxInfo.setTenant(tenant);
        taxInfo = taxInformationRepository.save(taxInfo);

        log.info("Updated tax info for user: {}", userId);

        // Update status for taxInfo
        updateSectionStatus(investor, "taxInformation");

        return toTaxDto(taxInfo);
    }

    // Bank Details
    public Optional<UserBankDetailsDto> getBankDetails(Long userId) {
        Investor investor = getInvestorForUser(userId);
        return bankDetailsRepository.findByInvestorUniqueIdAndIsPrimaryTrue(investor.getUniqueCode())
                .or(() -> bankDetailsRepository.findFirstByInvestorUniqueId(investor.getUniqueCode()))
                .map(this::toBankDto);
    }

    public UserBankDetailsDto updateBankDetails(Long userId, UserBankDetailsDto dto) {
        Investor investor = getInvestorForUser(userId);
        var tenant = TenantContextHolder.getContext().getTenant();

        InvestorBankDetails bank = bankDetailsRepository.findByInvestorUniqueIdAndIsPrimaryTrue(investor.getUniqueCode())
                .or(() -> bankDetailsRepository.findFirstByInvestorUniqueId(investor.getUniqueCode()))
                .orElseGet(() -> {
                    InvestorBankDetails b = InvestorBankDetails.builder()
                            .investorUniqueId(investor.getUniqueCode())
                            .build();
                    b.setTenant(tenant);
                    return b;
                });

        bank.setBankName(dto.getBankName());
        bank.setBankAddress(firstNonBlank(dto.getBankBranchAddress(), dto.getBranchAddress()));
        bank.setAccountNumber(firstNonBlank(dto.getBankAccountNumber(), dto.getAccountNumber()));
        bank.setAccountHolderName(firstNonBlank(dto.getBeneficiaryName(), dto.getAccountHolderName()));
        bank.setIfscCode(firstNonBlank(dto.getBankIfscCode(), dto.getIfscCode()));
        bank.setSwiftCode(dto.getSwiftCode());
        bank.setAccountType(dto.getAccountType());
        bank.setBranchName(dto.getBranchName());
        bank.setSettlementAccountType(dto.getSettlementAccountType());
        bank.setBankCountry(dto.getBankCountry());
        bank.setRbiApproval(dto.getRbiApproval());
        bank.setRbiApprovalOrderNumber(dto.getRbiApprovalOrderNumber());
        bank.setRbiApprovalDate(dto.getRbiApprovalDate());
        // Laravel parity: structured bank branch address
        bank.setBankDetailsCity(dto.getBankDetailsCity());
        bank.setBankDetailsState(dto.getBankDetailsState());
        bank.setBankDetailsCountry(dto.getBankDetailsCountry());
        bank.setBankDetailsZipCode(dto.getBankDetailsZipCode());
        bank.setBankDetailsMicr(dto.getBankDetailsMicr());
        bank.setIsPrimary(Boolean.TRUE.equals(dto.getIsPrimaryAccount()) || Boolean.TRUE.equals(dto.getIsPrimary()));
        bank.setTenant(tenant);
        bank = bankDetailsRepository.save(bank);

        log.info("Updated bank details for user: {}", userId);

        // Update status for bankDetails
        updateSectionStatus(investor, "bankDetails");

        return toBankDto(bank);
    }

    /**
     * Resolve the broker's preferred-bank display name.
     *
     * Laravel chain (information-update.blade.php lines 1653-1674):
     * <pre>
     *   intro_investor_temp.broker_prefferedbank  →  ss_brokerbankid
     *      → master_broker_banks.ss_bank_value    →  ss_bankid
     *        → master_banks.ss_nameofbank         →  accountid
     *          → master_accounts.name             →  display name
     * </pre>
     *
     * Returns null when any step in the chain is missing. Only applies when
     * the service provider type is {@code 100000000} (Broker).
     */
    public Optional<PreferredBankDto> getPreferredBankName(Long userId) {
        Investor investor = getInvestorForUser(userId);

        // Find the intro_investor_temp record for this investor
        IntroInvestorTemp intro = introInvestorTempRepository
                .findByUniqueCodeDb(investor.getUniqueCode())
                .orElse(null);

        if (intro == null) {
            return Optional.empty();
        }

        // Laravel only resolves when service_provider_type == 100000000 (Broker)
        String spType = intro.getServiceProviderType();
        boolean isBroker = "100000000".equals(spType);

        String brokerPreferredBankId = intro.getBrokerPreferredBank();
        if (brokerPreferredBankId == null || brokerPreferredBankId.isBlank()) {
            return Optional.of(PreferredBankDto.builder().isBroker(isBroker).build());
        }

        // Step 1: master_broker_banks
        MasterBrokerBanks brokerBank = masterBrokerBanksRepository
                .findFirstBySsBrokerBankId(brokerPreferredBankId)
                .orElse(null);
        if (brokerBank == null || brokerBank.getSsBankValue() == null) {
            return Optional.of(PreferredBankDto.builder().isBroker(isBroker).build());
        }

        // Step 2: master_banks (look up by ss_bankid = brokerBank.ss_bank_value)
        MasterBanks bank = masterBanksRepository
                .findFirstBySsBankId(brokerBank.getSsBankValue())
                .orElse(null);
        if (bank == null || bank.getSsNameOfBank() == null) {
            return Optional.of(PreferredBankDto.builder().isBroker(isBroker).build());
        }

        // Step 3: master_accounts (look up by accountid = bank.ss_nameofbank)
        MasterAccounts account = masterAccountsRepository
                .findByAccountId(bank.getSsNameOfBank())
                .orElse(null);
        String displayName = account != null ? account.getName() : null;

        return Optional.of(PreferredBankDto.builder()
                .isBroker(isBroker)
                .bankName(displayName)
                .build());
    }

    // Contact Details
    public Optional<UserContactDetailsDto> getContactDetails(Long userId) {
        Investor investor = getInvestorForUser(userId);
        return contactDetailsRepository.findByInvestorUniqueId(investor.getUniqueCode())
                .map(this::toContactDto);
    }

    public UserContactDetailsDto updateContactDetails(Long userId, UserContactDetailsDto dto) {
        Investor investor = getInvestorForUser(userId);
        var tenant = TenantContextHolder.getContext().getTenant();

        InvestorContactDetails contact = contactDetailsRepository.findByInvestorUniqueId(investor.getUniqueCode())
                .orElseGet(() -> {
                    InvestorContactDetails c = InvestorContactDetails.builder()
                            .investorUniqueId(investor.getUniqueCode())
                            .build();
                    c.setTenant(tenant);
                    return c;
                });

        contact.setAddressLine1(dto.getAddressLine1());
        contact.setAddressLine2(dto.getAddressLine2());
        contact.setAddressLine3(dto.getAddressLine3());
        contact.setUserCity(dto.getUserCity());
        contact.setUserState(dto.getUserState());
        contact.setUserCountry(dto.getUserCountry());
        contact.setUserZipCode(dto.getUserZipCode());
        contact.setProofOfAddress(dto.getProofOfAddress());
        contact.setCorrAddressSameAsPerm(dto.getCorrAddressSameAsPerm());
        contact.setAddressType(dto.getAddressType());
        contact.setCorrAddressLine1(dto.getCorrAddressLine1());
        contact.setCorrAddressLine2(dto.getCorrAddressLine2());
        contact.setCorrAddressLine3(dto.getCorrAddressLine3());
        contact.setCorrUserCity(dto.getCorrUserCity());
        contact.setCorrUserState(dto.getCorrUserState());
        contact.setCorrUserCountry(dto.getCorrUserCountry());
        contact.setCorrUserZipCode(dto.getCorrUserZipCode());
        contact.setIsdCode(dto.getIsdCode());
        contact.setMobileNumber(firstNonBlank(dto.getPrimaryPhone(), dto.getMobilePrimary()));
        contact.setAlternateMobile(firstNonBlank(dto.getSecondaryPhone(), dto.getMobileSecondary()));
        contact.setWhatsappNumber(dto.getWhatsappNumber());
        contact.setEmailAddress(firstNonBlank(dto.getEmail(), dto.getEmailPrimary()));
        contact.setAlternateEmail(firstNonBlank(dto.getAlternateEmail(), dto.getEmailSecondary()));
        contact.setPreferredContactMethod(dto.getPreferredContactMethod());
        contact.setLandlineNumber(dto.getLandlineNumber());
        contact.setPreferredContactTime(dto.getPreferredContactTime());
        contact.setTenant(tenant);
        contact = contactDetailsRepository.save(contact);

        log.info("Updated contact details for user: {}", userId);

        // Update status for contactDetails
        updateSectionStatus(investor, "contactDetails");

        return toContactDto(contact);
    }

    // Nomination
    public Optional<UserNominationDto> getNomination(Long userId) {
        Investor investor = getInvestorForUser(userId);
        List<InvestorNomination> nominations = nominationRepository.findByInvestorUniqueId(investor.getUniqueCode());
        if (nominations == null || nominations.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toNominationDto(nominations));
    }

    public UserNominationDto updateNomination(Long userId, UserNominationDto dto) {
        Investor investor = getInvestorForUser(userId);
        var tenant = TenantContextHolder.getContext().getTenant();

        List<InvestorNomination> existing = nominationRepository.findByInvestorUniqueId(investor.getUniqueCode());
        if (!existing.isEmpty()) {
            nominationRepository.deleteAll(existing);
        }

        boolean appointNominee = Boolean.TRUE.equals(dto.getAppointNominee());
        if (!appointNominee) {
            log.info("Cleared nomination for user: {}", userId);
            updateSectionStatus(investor, "nomination");
            return UserNominationDto.builder().appointNominee(false).build();
        }

        List<InvestorNomination> nominationsToSave = new ArrayList<>();

        if (firstNonBlank(dto.getNomineeFirstName(), dto.getNomineeName1()) != null) {
            InvestorNomination nominee1 = InvestorNomination.builder()
                    .investorUniqueId(investor.getUniqueCode())
                    .nomineeFirstName(firstNonBlank(dto.getNomineeFirstName(), dto.getNomineeName1()))
                    .nomineeMiddleName(dto.getNomineeMiddleName())
                    .nomineeLastName(dto.getNomineeLastName())
                    .relationship(firstNonBlank(dto.getNomineeRelationship(), dto.getNomineeRelation1()))
                    .dateOfBirth(parseDate(firstNonBlank(dto.getNomineeDob(), dto.getNomineeDob1())))
                    .allocationPercentage(dto.getNomineeShare() != null ? dto.getNomineeShare() : dto.getNomineeShare1())
                    .nomineeEmail(firstNonBlank(dto.getNomineeEmail(), dto.getNomineeEmail1()))
                    .nomineeMobile(firstNonBlank(dto.getNomineeMobile(), dto.getNomineeMobile1()))
                    .nomineeDocType(dto.getNomineeDocType1())
                    .nomineeDocNo(dto.getNomineeDocNo1())
                    .nomineeCountrycode(dto.getNomineeCountrycode1())
                    .nomineeAddress(dto.getNomineeAddress())
                    .nomineeCity(dto.getNomineeCity())
                    .nomineeState(dto.getNomineeState())
                    .nomineePostalCode(dto.getNomineePostalCode())
                    .isMinor(dto.getIsMinor())
                    .guardianName(firstNonBlank(dto.getGuardianName(), dto.getGuardianName1()))
                    .guardianRelationship(dto.getGuardianRelationship())
                    .guardianDocType(dto.getGuardianDocType1())
                    .guardianDocNo(dto.getGuardianDocNo1())
                    .guardianCountrycode(dto.getGuardianCountrycode1())
                    .guardianMobile(dto.getGuardianMobile1())
                    .guardianEmail(dto.getGuardianEmail1())
                    .guardianDob(parseDate(dto.getGuardianDob1()))
                    .guardianPanNo(dto.getGuardianPanNo1())
                    .build();
            nominee1.setTenant(tenant);
            nominationsToSave.add(nominee1);
        }

        if (dto.getNomineeName2() != null && !dto.getNomineeName2().isBlank()) {
            InvestorNomination nominee2 = InvestorNomination.builder()
                    .investorUniqueId(investor.getUniqueCode())
                    .nomineeFirstName(dto.getNomineeName2())
                    .relationship(dto.getNomineeRelation2())
                    .dateOfBirth(parseDate(dto.getNomineeDob2()))
                    .allocationPercentage(dto.getNomineeShare2())
                    .nomineeEmail(dto.getNomineeEmail2())
                    .nomineeMobile(dto.getNomineeMobile2())
                    .nomineeDocType(dto.getNomineeDocType2())
                    .nomineeDocNo(dto.getNomineeDocNo2())
                    .nomineeCountrycode(dto.getNomineeCountrycode2())
                    .guardianDocType(dto.getGuardianDocType2())
                    .guardianDocNo(dto.getGuardianDocNo2())
                    .guardianCountrycode(dto.getGuardianCountrycode2())
                    .guardianMobile(dto.getGuardianMobile2())
                    .guardianEmail(dto.getGuardianEmail2())
                    .guardianDob(parseDate(dto.getGuardianDob2()))
                    .build();
            nominee2.setTenant(tenant);
            nominationsToSave.add(nominee2);
        }

        if (dto.getNomineeName3() != null && !dto.getNomineeName3().isBlank()) {
            InvestorNomination nominee3 = InvestorNomination.builder()
                    .investorUniqueId(investor.getUniqueCode())
                    .nomineeFirstName(dto.getNomineeName3())
                    .relationship(dto.getNomineeRelation3())
                    .dateOfBirth(parseDate(dto.getNomineeDob3()))
                    .allocationPercentage(dto.getNomineeShare3())
                    .nomineeEmail(dto.getNomineeEmail3())
                    .nomineeMobile(dto.getNomineeMobile3())
                    .nomineeDocType(dto.getNomineeDocType3())
                    .nomineeDocNo(dto.getNomineeDocNo3())
                    .nomineeCountrycode(dto.getNomineeCountrycode3())
                    .guardianDocType(dto.getGuardianDocType3())
                    .guardianDocNo(dto.getGuardianDocNo3())
                    .guardianCountrycode(dto.getGuardianCountrycode3())
                    .guardianMobile(dto.getGuardianMobile3())
                    .guardianEmail(dto.getGuardianEmail3())
                    .guardianDob(parseDate(dto.getGuardianDob3()))
                    .build();
            nominee3.setTenant(tenant);
            nominationsToSave.add(nominee3);
        }

        List<InvestorNomination> savedNominations = nominationRepository.saveAll(nominationsToSave);

        log.info("Updated nomination for user: {}", userId);

        // Update status for nomination
        updateSectionStatus(investor, "nomination");

        return toNominationDto(savedNominations);
    }

    // Risk Profile
    public Optional<UserRiskProfileDto> getRiskProfile(Long userId) {
        Investor investor = getInvestorForUser(userId);
        return riskProfileRepository.findByInvestorUniqueId(investor.getUniqueCode())
                .map(this::toRiskProfileDto);
    }

    public UserRiskProfileDto updateRiskProfile(Long userId, UserRiskProfileDto dto) {
        Investor investor = getInvestorForUser(userId);
        var tenant = TenantContextHolder.getContext().getTenant();

        InvestorRiskProfile risk = riskProfileRepository.findByInvestorUniqueId(investor.getUniqueCode())
                .orElseGet(() -> {
                    InvestorRiskProfile r = InvestorRiskProfile.builder()
                            .investorUniqueId(investor.getUniqueCode())
                            .build();
                    r.setTenant(tenant);
                    return r;
                });

        risk.setInvestmentHorizon(dto.getInvestmentHorizon());
        risk.setRiskTolerance(dto.getRiskAppetite());
        risk.setInvestmentObjective(dto.getInvestmentPurpose());
        risk.setSourceOfFunds(firstNonBlank(dto.getSourceOfFunds(), dto.getLiquidityNeeds()));
        risk.setAnnualIncome(dto.getGrossIncome());
        risk.setNetWorth(dto.getNetWorth());
        risk.setOccupation(dto.getOccupation());
        risk.setEducationalQualification(dto.getEducationalQualification());
        risk.setInvestmentExperienceIn(dto.getInvestmentExperienceIn() != null ? String.join(",", dto.getInvestmentExperienceIn()) : null);
        risk.setAgeGroup(dto.getAgeGroup());
        risk.setMarketKnowledge(dto.getMarketKnowledge());
        risk.setLossComfortLevel(dto.getLossComfortLevel());
        risk.setPreviousLosses(dto.getPreviousLosses());
        risk.setUnderstandsRisk(dto.getUnderstandsRisk());
        risk.setRiskScore(dto.getRiskScore());
        risk.setInvestmentExperienceYears(dto.getInvestmentExperienceYears());
        risk.setSourceOfWealth(dto.getSourceOfWealth());
        risk.setLineOfBusiness(dto.getLineOfBusiness());
        risk.setNatureOfOrganisation(dto.getNatureOfOrganisation());
        risk.setPolExposed(dto.getPolExposed());
        risk.setPolExposedRelated(dto.getPolExposedRelated());
        risk.setActivity(dto.getActivity());
        risk.setMoneyChangeService(dto.getMoneyChangeService());
        risk.setGamblingService(dto.getGamblingService());
        risk.setPawningService(dto.getPawningService());
        risk.setInstanceViolation(dto.getInstanceViolation());
        risk.setTenant(tenant);
        risk = riskProfileRepository.save(risk);

        log.info("Updated risk profile for user: {}", userId);

        // Update status for riskProfile
        updateSectionStatus(investor, "riskProfile");

        return toRiskProfileDto(risk);
    }

    private UserResidentialStatusDto toResidentialDto(InvestorResidentialStatus s) {
        return UserResidentialStatusDto.builder()
                .residenceType(s.getResidentialStatus())
                .residentialStatus(s.getResidentialStatus())
                .residenceCountry(s.getCountryOfResidence() != null ? String.valueOf(s.getCountryOfResidence()) : null)
                .country(s.getCountryOfResidence())
                .addressLine1(s.getResidenceAddress())
                .residenceAddress(s.getResidenceAddress())
                .city(s.getResidenceCity())
                .residenceCity(s.getResidenceCity())
                .state(parseInteger(s.getResidenceState()))
                .residenceState(s.getResidenceState())
                .postalCode(s.getResidencePostalCode())
                .residencePostalCode(s.getResidencePostalCode())
                .residencePhone(s.getResidencePhone())
                .residenceSince(s.getResidenceSince())
                .addressProofType(s.getAddressProofType())
                .addressProofDocumentId(s.getAddressProofDocumentId())
                .yearsAtCurrentAddress(s.getYearsAtCurrentAddress())
                .userTypeOfProof(s.getUserTypeOfProof())
                .userVisaType(s.getUserVisaType())
                .userVisaNumber(s.getUserVisaNumber())
                .userVisaIssuerDate(s.getUserVisaIssuerDate())
                .userVisaExpiryDate(s.getUserVisaExpiryDate())
                .userVisaDateOfIssue(s.getUserVisaDateOfIssue())
                .userVisaValidUpto(s.getUserVisaValidUpto())
                .userOciCardNo(s.getUserOciCardNo())
                .userOciIssueDate(s.getUserOciIssueDate())
                .userOciValidUpto(s.getUserOciValidUpto())
                .personOrigin(s.getPersonOrigin())
                .proofOfAddress(s.getProofOfAddress())
                .aadharNumberOption(s.getAadharNumberOption())
                .aadharNumber(s.getAadharNumber())
                .userAadharNo(s.getUserAadharNo())
                .ociAvailable(s.getOciAvailable())
                .dateOfOci(s.getDateOfOci() != null ? s.getDateOfOci().toString() : null)
                .build();
    }

    private UserTaxInfoDto toTaxDto(InvestorTaxInformation t) {
        return UserTaxInfoDto.builder()
                .panNumber(t.getPanNumber())
                .tinNumber(t.getTaxIdentificationNumber())
                .taxIdNumber(t.getTaxIdentificationNumber())
                .taxResidencyCountryId(t.getTaxCountry())
                .taxResidencyCountry(t.getTaxCountry() != null ? String.valueOf(t.getTaxCountry()) : null)
                .taxResidencyStatus(t.getTaxResidencyStatus())
                .gstNumber(t.getGstNumber())
                .incomeSource(t.getIncomeSource())
                .annualIncome(t.getAnnualIncome())
                .taxIdentificationNumberType(t.getTaxIdentificationNumberType())
                .fatcaStatus(t.getFatcaStatus())
                .crsDeclaration(t.getCrsDeclaration())
                .usCitizen(t.getUsCitizen())
                .usResident(t.getUsResident())
                .taxPanFirstName(t.getTaxPanFirstName())
                .taxPanFatherName(t.getTaxPanFatherName())
                .taxResidencyCertificateNo(t.getTaxResidencyCertificateNo())
                .taxResidencyCertificateDate(t.getTaxResidencyCertificateDate())
                .taxInfo(t.getTaxInfo())
                .taxPanNo(t.getTaxPanNo())
                .usPersonFatca(t.getUsPersonFatca())
                .build();
    }

    private UserBankDetailsDto toBankDto(InvestorBankDetails b) {
        return UserBankDetailsDto.builder()
                .bankName(b.getBankName())
                .beneficiaryName(b.getAccountHolderName())
                .bankAccountNumber(b.getAccountNumber())
                .accountNumber(b.getAccountNumber())
                .accountHolderName(b.getAccountHolderName())
                .accountType(b.getAccountType())
                .bankIfscCode(b.getIfscCode())
                .ifscCode(b.getIfscCode())
                .swiftCode(b.getSwiftCode())
                .branchName(b.getBranchName())
                .bankBranchAddress(b.getBankAddress())
                .branchAddress(b.getBankAddress())
                .settlementAccountType(b.getSettlementAccountType())
                .bankCountry(b.getBankCountry())
                .isPrimaryAccount(b.getIsPrimary())
                .isPrimary(b.getIsPrimary())
                .rbiApproval(b.getRbiApproval())
                .rbiApprovalOrderNumber(b.getRbiApprovalOrderNumber())
                .rbiApprovalDate(b.getRbiApprovalDate())
                .build();
    }

    private UserContactDetailsDto toContactDto(InvestorContactDetails c) {
        return UserContactDetailsDto.builder()
                .addressLine1(c.getAddressLine1())
                .addressLine2(c.getAddressLine2())
                .addressLine3(c.getAddressLine3())
                .userCity(c.getUserCity())
                .userState(c.getUserState())
                .userCountry(c.getUserCountry())
                .userZipCode(c.getUserZipCode())
                .proofOfAddress(c.getProofOfAddress())
                .corrAddressSameAsPerm(c.getCorrAddressSameAsPerm())
                .addressType(c.getAddressType())
                .corrAddressLine1(c.getCorrAddressLine1())
                .corrAddressLine2(c.getCorrAddressLine2())
                .corrAddressLine3(c.getCorrAddressLine3())
                .corrUserCity(c.getCorrUserCity())
                .corrUserState(c.getCorrUserState())
                .corrUserCountry(c.getCorrUserCountry())
                .corrUserZipCode(c.getCorrUserZipCode())
                .isdCode(c.getIsdCode())
                .email(c.getEmailAddress())
                .emailPrimary(c.getEmailAddress())
                .alternateEmail(c.getAlternateEmail())
                .emailSecondary(c.getAlternateEmail())
                .primaryPhone(c.getMobileNumber())
                .mobilePrimary(c.getMobileNumber())
                .secondaryPhone(c.getAlternateMobile())
                .mobileSecondary(c.getAlternateMobile())
                .whatsappNumber(c.getWhatsappNumber())
                .landlineNumber(c.getLandlineNumber())
                .preferredContactMethod(c.getPreferredContactMethod())
                .preferredContactTime(c.getPreferredContactTime())
                .build();
    }

    private UserNominationDto toNominationDto(List<InvestorNomination> nominations) {
        UserNominationDto.UserNominationDtoBuilder builder = UserNominationDto.builder()
                .appointNominee(nominations != null && !nominations.isEmpty());

        if (nominations == null || nominations.isEmpty()) {
            return builder.build();
        }

        nominations.sort((a, b) -> {
            Long aId = a.getId() == null ? Long.MAX_VALUE : a.getId();
            Long bId = b.getId() == null ? Long.MAX_VALUE : b.getId();
            return aId.compareTo(bId);
        });

        InvestorNomination n1 = nominations.get(0);
        builder.nomineeFirstName(n1.getNomineeFirstName())
                .nomineeMiddleName(n1.getNomineeMiddleName())
                .nomineeLastName(n1.getNomineeLastName())
                .nomineeName1(n1.getNomineeFirstName())
                .nomineeRelationship(n1.getRelationship())
                .nomineeRelation1(n1.getRelationship())
                .nomineeDob(n1.getDateOfBirth() != null ? n1.getDateOfBirth().toString() : null)
                .nomineeDob1(n1.getDateOfBirth() != null ? n1.getDateOfBirth().toString() : null)
                .nomineeEmail1(n1.getNomineeEmail())
                .nomineeEmail(n1.getNomineeEmail())
                .nomineeMobile1(n1.getNomineeMobile())
                .nomineeMobile(n1.getNomineeMobile())
                .nomineeDocType1(n1.getNomineeDocType())
                .nomineeDocNo1(n1.getNomineeDocNo())
                .nomineeCountrycode1(n1.getNomineeCountrycode())
                .nomineeAddress(n1.getNomineeAddress())
                .nomineeCity(n1.getNomineeCity())
                .nomineeState(n1.getNomineeState())
                .nomineePostalCode(n1.getNomineePostalCode())
                .nomineeShare1(n1.getAllocationPercentage())
                .nomineeShare(n1.getAllocationPercentage())
                .isMinor(n1.getIsMinor())
                .guardianName1(n1.getGuardianName())
                .guardianName(n1.getGuardianName())
                .guardianRelationship(n1.getGuardianRelationship())
                .guardianDocType1(n1.getGuardianDocType())
                .guardianDocNo1(n1.getGuardianDocNo())
                .guardianCountrycode1(n1.getGuardianCountrycode())
                .guardianMobile1(n1.getGuardianMobile())
                .guardianEmail1(n1.getGuardianEmail())
                .guardianDob1(n1.getGuardianDob() != null ? n1.getGuardianDob().toString() : null)
                .guardianPanNo1(n1.getGuardianPanNo());

        if (nominations.size() > 1) {
            InvestorNomination n2 = nominations.get(1);
            builder.nomineeName2(n2.getNomineeFirstName())
                    .nomineeRelation2(n2.getRelationship())
                    .nomineeDob2(n2.getDateOfBirth() != null ? n2.getDateOfBirth().toString() : null)
                    .nomineeEmail2(n2.getNomineeEmail())
                    .nomineeMobile2(n2.getNomineeMobile())
                    .nomineeShare2(n2.getAllocationPercentage())
                    .nomineeDocType2(n2.getNomineeDocType())
                    .nomineeDocNo2(n2.getNomineeDocNo())
                    .nomineeCountrycode2(n2.getNomineeCountrycode())
                    .guardianDocType2(n2.getGuardianDocType())
                    .guardianDocNo2(n2.getGuardianDocNo())
                    .guardianCountrycode2(n2.getGuardianCountrycode())
                    .guardianMobile2(n2.getGuardianMobile())
                    .guardianEmail2(n2.getGuardianEmail())
                    .guardianDob2(n2.getGuardianDob() != null ? n2.getGuardianDob().toString() : null);
        }

        if (nominations.size() > 2) {
            InvestorNomination n3 = nominations.get(2);
            builder.nomineeName3(n3.getNomineeFirstName())
                    .nomineeRelation3(n3.getRelationship())
                    .nomineeDob3(n3.getDateOfBirth() != null ? n3.getDateOfBirth().toString() : null)
                    .nomineeEmail3(n3.getNomineeEmail())
                    .nomineeMobile3(n3.getNomineeMobile())
                    .nomineeShare3(n3.getAllocationPercentage())
                    .nomineeDocType3(n3.getNomineeDocType())
                    .nomineeDocNo3(n3.getNomineeDocNo())
                    .nomineeCountrycode3(n3.getNomineeCountrycode())
                    .guardianDocType3(n3.getGuardianDocType())
                    .guardianDocNo3(n3.getGuardianDocNo())
                    .guardianCountrycode3(n3.getGuardianCountrycode())
                    .guardianMobile3(n3.getGuardianMobile())
                    .guardianEmail3(n3.getGuardianEmail())
                    .guardianDob3(n3.getGuardianDob() != null ? n3.getGuardianDob().toString() : null);
        }

        return builder.build();
    }

    private UserRiskProfileDto toRiskProfileDto(InvestorRiskProfile r) {
        return UserRiskProfileDto.builder()
                .investmentHorizon(r.getInvestmentHorizon())
                .riskAppetite(r.getRiskTolerance())
                .investmentPurpose(r.getInvestmentObjective())
                .liquidityNeeds(r.getSourceOfFunds())
                .sourceOfFunds(r.getSourceOfFunds())
                .occupation(r.getOccupation())
                .grossIncome(r.getAnnualIncome())
                .netWorth(r.getNetWorth())
                .educationalQualification(r.getEducationalQualification())
                .investmentExperienceIn(splitCsv(r.getInvestmentExperienceIn()))
                .ageGroup(r.getAgeGroup())
                .marketKnowledge(r.getMarketKnowledge())
                .lossComfortLevel(r.getLossComfortLevel())
                .previousLosses(r.getPreviousLosses())
                .understandsRisk(r.getUnderstandsRisk())
                .riskScore(r.getRiskScore())
                .investmentExperienceYears(r.getInvestmentExperienceYears())
                .sourceOfWealth(r.getSourceOfWealth())
                .lineOfBusiness(r.getLineOfBusiness())
                .natureOfOrganisation(r.getNatureOfOrganisation())
                .polExposed(r.getPolExposed())
                .polExposedRelated(r.getPolExposedRelated())
                .activity(r.getActivity())
                .moneyChangeService(r.getMoneyChangeService())
                .gamblingService(r.getGamblingService())
                .pawningService(r.getPawningService())
                .instanceViolation(r.getInstanceViolation())
                .build();
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (Exception ex) {
            return null;
        }
    }

    private String firstNonBlank(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        return (fallback != null && !fallback.isBlank()) ? fallback : null;
    }

    private List<String> splitCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return null;
        }
        String[] values = csv.split(",");
        List<String> out = new ArrayList<>();
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                out.add(value.trim());
            }
        }
        return out.isEmpty() ? null : out;
    }

    private Integer mapExperienceYearsLabelToBucket(String label) {
        if (label == null || label.isBlank()) {
            return null;
        }
        String normalized = label.trim().toLowerCase();
        if (normalized.contains("less than 2")) {
            return 1;
        }
        if (normalized.contains("2-5")) {
            return 2;
        }
        if (normalized.contains("more than 5")) {
            return 3;
        }
        return parseInteger(label);
    }

    private String mapExperienceYearsBucketToLabel(Integer bucket) {
        if (bucket == null) {
            return null;
        }
        return switch (bucket) {
            case 1 -> "Less than 2 years";
            case 2 -> "2-5 year";
            case 3 -> "More than 5 year";
            default -> String.valueOf(bucket);
        };
    }

    // Final Submit
    public OnboardingStatusDto finalSubmit(Long userId, InvestorConsentsDto consentsDto) {
        log.info("Final submission for user: {}", userId);
        Investor investor = getInvestorForUser(userId);
        var tenant = TenantContextHolder.getContext().getTenant();

        // Save final submit consents
        InvestorConsents consents = consentsRepository.findByInvestorUniqueId(investor.getUniqueCode())
                .orElseGet(() -> {
                    InvestorConsents c = InvestorConsents.builder()
                            .investorUniqueId(investor.getUniqueCode())
                            .build();
                    c.setTenant(tenant);
                    return c;
                });

        // Fetch user_dob from personal info if not already set
        if (consents.getUserDob() == null) {
            LocalDate userDob = personalInfoRepository.findByInvestorUniqueId(investor.getUniqueCode())
                    .map(UserPersonalInformation::getUserDob)
                    .orElse(null);
            if (userDob != null) {
                consents.setUserDob(userDob);
            }
        }

        if (consentsDto.getInformationCorrectConsent() != null)
            consents.setInformationCorrectConsent(consentsDto.getInformationCorrectConsent());
        if (consentsDto.getLegalCapacityConsent() != null)
            consents.setLegalCapacityConsent(consentsDto.getLegalCapacityConsent());
        if (consentsDto.getModificationAwarenessConsent() != null)
            consents.setModificationAwarenessConsent(consentsDto.getModificationAwarenessConsent());
        
        consents.setConsentDate(LocalDateTime.now());
        consents.setTenant(tenant);
        consentsRepository.save(consents);

        // Mark as completed
        investor.setVerifyStatus(1); // 1 = verified/complete
        investorRepository.save(investor);

        // Generate PDF and submit to Dataverse
        if (submissionService != null) {
            try {
                log.info("Starting PDF generation and Dataverse submission for: {}", investor.getUniqueCode());
                submissionService.submitInvestorInformation(investor.getUniqueCode());
                log.info("PDF generation and Dataverse submission completed");
            } catch (Exception e) {
                log.error("Failed PDF/Dataverse submission: {}", e.getMessage(), e);
            }
        } else {
            log.warn("InvestorInformationSubmissionService not available");
        }

        // Send final submission confirmation email
        try {
            String investorName = investor.getAuthorizedUser().getFirstName();
            if (investor.getAuthorizedUser().getLastName() != null) {
                investorName += " " + investor.getAuthorizedUser().getLastName();
            }
            notificationService.sendFinalSubmissionEmail(
                    investor.getAuthorizedUser().getEmailId(),
                    investorName,
                    investor.getUniqueCode());
        } catch (Exception e) {
            log.warn("Failed to send final submission email", e);
        }

        // Sync verification status to Dynamics CRM (align with Laravel)
        try {
            String dvContactId = investor.getDvContactId();
            if (dynamicsCrmService != null && dvContactId != null && !dvContactId.trim().isEmpty()) {
                log.info("Syncing verification status to Dynamics for contact: {}", dvContactId);
                dynamicsCrmService.updateContactVerificationStatus(dvContactId, true);
                
                // Also update local user_personal_information.ss_verification_done ("1" = done)
                personalInfoRepository.findByInvestorUniqueId(investor.getUniqueCode()).ifPresent(personalInfo -> {
                    personalInfo.setSsVerificationDone("1");
                    personalInfo.setSsVerificationDoneBy("System");
                    personalInfo.setSsVerificationDateandTime(LocalDateTime.now());
                    personalInfoRepository.save(personalInfo);
                    log.info("Updated local verification status for investor: {}", investor.getUniqueCode());
                });
            } else {
                log.warn("Skipping Dynamics CRM sync - contact ID not available for investor: {}", investor.getUniqueCode());
            }
        } catch (Exception e) {
            log.warn("Failed to sync verification status to Dynamics: {}", e.getMessage());
            // Non-fatal: local submission succeeded
        }

        log.info("Final submission completed for user: {}", userId);
        return getOnboardingStatus(userId);
    }

    // --- Helper for status update ---
    private void updateSectionStatus(Investor investor, String sectionKey) {
        try {
            var tenant = TenantContextHolder.getContext().getTenant();
            String email = investor.getAuthorizedUser() != null ? investor.getAuthorizedUser().getEmailId() : "";

            InvestorInformationStatus status = statusRepository.findByInvestorEmail(email)
                    .orElseGet(() -> {
                        InvestorInformationStatus s = InvestorInformationStatus.builder()
                                .investorEmail(email)
                                .build();
                        s.setTenant(tenant);
                        return s;
                    });

            // 1 = complete, 2 = incomplete/pending
            switch (sectionKey) {
                case "personalInfo" -> status.setPersonalInfo(1);
                case "passport" -> status.setPassport(1);
                case "residentialStatus" -> status.setResidentialStatus(1);
                case "taxInformation" -> status.setTaxInformation(1);
                case "bankDetails" -> status.setBankDetails(1);
                case "contactDetails" -> status.setContactDetails(1);
                case "nomination" -> status.setNomination(1);
                case "riskProfile" -> status.setRiskProfile(1);
            }

            status.setTenant(tenant);
            statusRepository.save(status);
            log.info("Updated status for section {} to completed for investor {}", sectionKey, email);

        } catch (Exception e) {
            log.error("Failed to update section status for investor: " + investor.getId(), e);
        }
    }
}
