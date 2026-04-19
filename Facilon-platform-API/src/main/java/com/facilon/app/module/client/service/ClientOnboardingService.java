package com.facilon.app.module.client.service;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.integration.dynamics.DynamicsCrmService;
import com.facilon.app.integration.email.EmailServiceApiClient;
import com.facilon.app.integration.graphemail.GraphEmailService;
import com.facilon.app.integration.sms.SmsServiceApiClient;
import com.facilon.app.integration.usermgmt.UserMgmtApiClient;
import com.facilon.app.integration.usermgmt.dto.SignUpDto;
import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.model.Tenant;
import com.facilon.app.module.client.dto.onboarding.*;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.model.InvestorConsents;
import com.facilon.app.module.client.model.InvestorType;
import com.facilon.app.module.client.model.UserPersonalInformation;
import com.facilon.app.module.client.model.content.InvestorTypeCategory;
import com.facilon.app.module.client.repository.InvestorConsentsRepository;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.module.client.repository.InvestorTypeCategoryRepository;
import com.facilon.app.module.client.repository.MarketTypeRepository;
import com.facilon.app.module.client.repository.IntroInvestorTempRepository;
import com.facilon.app.module.client.repository.MasterBrokersRepository;
import com.facilon.app.module.client.model.IntroInvestorTemp;
import com.facilon.app.module.client.model.master.MarketType;
import com.facilon.app.module.client.model.master.MasterBrokers;
import com.facilon.app.module.client.repository.UserPersonalInformationRepository;
import com.facilon.app.repository.AuthorizedUserRepository;
import com.facilon.app.repository.UserGroupRepository;
import com.facilon.app.service.TenantB2CConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClientOnboardingService {

    private final InvestorRepository investorRepository;
    private final AuthorizedUserRepository userRepository;
    private final UserPersonalInformationRepository personalInfoRepository;
    private final InvestorTypeCategoryRepository investorTypeCategoryRepository;
    private final MarketTypeRepository marketTypeRepository;
    private final InvestorConsentsRepository consentRepository;
    private final IntroInvestorTempRepository introInvestorTempRepository;
    private final MasterBrokersRepository masterBrokersRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<UserMgmtApiClient> userMgmtApiClientProvider;
    private final ObjectProvider<EmailServiceApiClient> emailServiceApiClientProvider;
    private final ObjectProvider<GraphEmailService> graphEmailServiceProvider;
    private final ObjectProvider<SmsServiceApiClient> smsServiceApiClientProvider;
    private final InvestorNotificationService notificationService;
    private final UserGroupRepository userGroupRepository;
    private final TenantB2CConfigService tenantB2CConfigService;
    private final ObjectProvider<DynamicsCrmService> dynamicsCrmServiceProvider;

    @Value("${investor.onboarding.india-country-code:240}")
    private Integer indiaCountryCode;
    @Value("${investor.onboarding.india-isd-code:91}")
    private String indiaIsdCode;

    @Value("${app.base-url:http://localhost:3000}")
    private String baseUrl;

    private static final Random RANDOM = new Random();

    /**
     * Step 1: Main step - Basic information. Returns unique code.
     */
    public String registerMainStep(MainStepDto dto) {
        log.info("Processing main step: registerAs={}, market={}", dto.getRegisterAs(), dto.getMarket());

        // Validate legal entity specific fields
        if (dto.getRegisterAs() != null && dto.getRegisterAs() == 2) { // Legal Entity
            if (dto.getCountryOfIncorporation() == null) {
                throw new RuntimeException("Country of incorporation is required for legal entity");
            }
            if (dto.getCountryOfTaxResidency() == null) {
                throw new RuntimeException("Country of tax residency is required for legal entity");
            }
            // Validate website format if provided
            if (dto.getEntityWebsite() != null && !dto.getEntityWebsite().isEmpty()) {
                if (!isValidWebsiteUrl(dto.getEntityWebsite())) {
                    throw new RuntimeException("Invalid website URL format. Expected format: www.example.com");
                }
            }
        }

        String uniqueCode = generateUniqueCode();
        Tenant tenant = TenantContextHolder.getContext().getTenant();

        AuthorizedUser user = AuthorizedUser.builder()
                .firstName(dto.getFullName())
                .isActive(false)
                .build();
        user.setTenant(tenant);
        user = userRepository.save(user);

        Investor investor = Investor.builder()
                .authorizedUser(user)
                .uniqueCode(uniqueCode)
                .registerAs(dto.getRegisterAs())
                .market(dto.getMarket())
                .incorpCountry(
                        dto.getRegisterAs() != null && dto.getRegisterAs() == 2 ? dto.getCountryOfIncorporation() : 0)
                .countryOfTaxResidency(
                        dto.getRegisterAs() != null && dto.getRegisterAs() == 2 ? dto.getCountryOfTaxResidency() : null)
                .entityName(dto.getRegisterAs() != null && dto.getRegisterAs() == 2 ? dto.getFullName() : null)
                .entityWebsite(dto.getRegisterAs() != null && dto.getRegisterAs() == 2 ? dto.getEntityWebsite() : null)
                .verifyStatus(2)
                .build();
        investor.setTenant(tenant);
        investorRepository.save(investor);

        log.info("Main step completed. Unique code: {}", uniqueCode);
        return uniqueCode;
    }

    /**
     * Validate website URL format (www.xxxx.xxx)
     */
    private boolean isValidWebsiteUrl(String url) {
        String regex = "^(www\\.)([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
        return url.matches(regex);
    }

    /**
     * Validate corporate email - reject common personal email domains
     */
    private boolean isCorporateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        String[] personalEmailDomains = {
                "gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "live.com",
                "aol.com", "icloud.com", "mail.com", "protonmail.com", "zoho.com",
                "yandex.com", "gmx.com", "inbox.com", "mail.ru", "qq.com",
                "163.com", "126.com", "rediffmail.com", "yahoo.co.in", "yahoo.co.uk"
        };

        String emailLower = email.toLowerCase();
        String domain = emailLower.substring(emailLower.indexOf("@") + 1);

        for (String personalDomain : personalEmailDomains) {
            if (domain.equals(personalDomain)) {
                return false;
            }
        }

        return true; // Assumed to be corporate if not in personal domains list
    }

    /**
     * Step 2: Personal details + Store OTP (email/SMS OTP sent via external service
     * - stub for now).
     */
    public OtpResponseDto registerStep1(String uniqueCode, Step1Dto dto) {
        log.info("Processing step 1 for code: {}", uniqueCode);

        Investor investor = investorRepository.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new RuntimeException("Investor not found: " + uniqueCode));

        // Validate corporate email for legal entity
        if (investor.getRegisterAs() != null && investor.getRegisterAs() == 2) { // Legal Entity
            if (!isCorporateEmail(dto.getEmail())) {
                throw new RuntimeException(
                        "Corporate email required for legal entity registration. Personal email domains (gmail, yahoo, hotmail, etc.) are not allowed.");
            }
        }

        AuthorizedUser user = investor.getAuthorizedUser();
        if (userRepository.findByEmailId(dto.getEmail()).filter(u -> !u.getId().equals(user.getId())).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmailId(dto.getEmail());
        user.setMobilePhone(dto.getMobileNumber());
        userRepository.save(user);

        // For legal entity, save representative details
        if (investor.getRegisterAs() != null && investor.getRegisterAs() == 2) {
            String repName = dto.getFirstName() + (dto.getMiddleName() != null ? " " + dto.getMiddleName() : "") + " "
                    + dto.getLastName();
            investor.setEntityNameRepresentative(repName);
            investor.setCompanyCapacity(dto.getRepresentativeCapacity());
            investorRepository.save(investor);
        }

        UserPersonalInformation personalInfo = personalInfoRepository.findByInvestorUniqueId(uniqueCode)
                .orElseGet(() -> {
                    UserPersonalInformation p = UserPersonalInformation.builder()
                            .investorUniqueId(uniqueCode)
                            .investorId(String.valueOf(investor.getId()))
                            .build();
                    p.setTenant(user.getTenant());
                    return p;
                });
        personalInfo.setTitle(dto.getTitle());
        personalInfo.setInvestorFirstName(dto.getFirstName());
        personalInfo.setInvestorMiddleName(dto.getMiddleName());
        personalInfo.setInvestorLastName(dto.getLastName());
        personalInfo.setInvestorGender(dto.getGender() != null ? String.valueOf(dto.getGender()) : null);
        personalInfo.setUserDob(dto.getUserDob());
        personalInfo.setWhatsappNumber(dto.getWhatsappNumber());
        personalInfo.setTenant(user.getTenant());
        personalInfoRepository.save(personalInfo);

        String emailOtp = otpService.generateOtp();
        boolean isIndia = dto.getCountryCode() != null && dto.getCountryCode().equals(indiaCountryCode);

        // Check if SMS service is available
        SmsServiceApiClient smsClient = smsServiceApiClientProvider.getIfAvailable();
        boolean smsServiceAvailable = smsClient != null;

        // Generate SMS OTP only if service is available AND country is India
        String smsOtp = (isIndia && smsServiceAvailable) ? otpService.generateOtp() : "1111";
        otpService.storeOtp(uniqueCode, emailOtp, smsOtp, 15);

        if (!smsServiceAvailable) {
            log.info("SMS service not configured - using default OTP 1111 for code: {}", uniqueCode);
        }

        boolean emailSent = false;

        // Try Graph Email Service first (Microsoft 365)
        GraphEmailService graphEmailService = graphEmailServiceProvider.getIfAvailable();
        if (graphEmailService != null) {
            try {
                graphEmailService.sendOtpEmail(dto.getEmail(), dto.getFirstName(), emailOtp);
                emailSent = true;
                log.info("OTP email sent via Graph Email to {} for code: {}", dto.getEmail(), uniqueCode);
            } catch (Exception e) {
                log.warn("Failed to send OTP via Graph Email, trying fallback: {}", e.getMessage());
                // Try fallback to EmailServiceApiClient
                EmailServiceApiClient emailClient = emailServiceApiClientProvider.getIfAvailable();
                if (emailClient != null) {
                    try {
                        emailClient.sendOtpEmail(dto.getEmail(), dto.getFirstName(), emailOtp);
                        emailSent = true;
                        log.info("OTP email sent via EmailService fallback to {} for code: {}", dto.getEmail(),
                                uniqueCode);
                    } catch (Exception e2) {
                        log.warn("Fallback email also failed (OTP still valid): {}", e2.getMessage());
                    }
                }
            }
        } else {
            // Fallback to EmailServiceApiClient
            EmailServiceApiClient emailClient = emailServiceApiClientProvider.getIfAvailable();
            if (emailClient != null) {
                try {
                    emailClient.sendOtpEmail(dto.getEmail(), dto.getFirstName(), emailOtp);
                    emailSent = true;
                    log.info("OTP email sent via EmailService to {} for code: {}", dto.getEmail(), uniqueCode);
                } catch (Exception e) {
                    log.warn("Failed to send OTP email (OTP still valid): {}", e.getMessage());
                }
            } else {
                log.warn("No email service available (Graph Email or EmailService); OTP stored but not sent");
            }
        }

        boolean smsSent = false;
        if (isIndia && smsServiceAvailable) {
            try {
                String fullNumber = (indiaIsdCode != null ? indiaIsdCode : "91")
                        + dto.getMobileNumber().replaceAll("\\D", "");
                smsClient.sendOtpSms(fullNumber, smsOtp);
                smsSent = true;
                log.info("SMS OTP sent to {} for India (code: {})", dto.getMobileNumber(), uniqueCode);
            } catch (Exception e) {
                log.warn("Failed to send SMS OTP (OTP still valid): {}", e.getMessage());
            }
        } else if (isIndia && !smsServiceAvailable) {
            log.info("SMS service not configured for India - using default OTP 1111");
        } else {
            log.debug("Non-India country ({}), using default SMS OTP 1111", dto.getCountryCode());
        }

        log.info("OTP stored for code: {}", uniqueCode);
        return OtpResponseDto.builder()
                .message("OTP sent successfully")
                .emailSent(emailSent)
                .smsSent(smsSent)
                .expiresInMinutes(15)
                .build();
    }

    /**
     * Step 3: Verify OTP and complete registration (Azure AD creation via external
     * API - stub).
     */
    public VerificationResponseDto verifyOtp(OtpVerificationDto dto) {
        log.info("Verifying OTP for code: {}", dto.getUniqueCode());

        Investor investor = investorRepository.findByUniqueCode(dto.getUniqueCode())
                .orElseThrow(() -> new RuntimeException("Investor not found"));

        if (!otpService.verifyOtp(dto.getUniqueCode(), dto.getEmailOtp(), "email")) {
            throw new RuntimeException("Invalid email OTP");
        }
        // SMS OTP verification disabled for now - mobile OTP not required
        // if (!otpService.verifyOtp(dto.getUniqueCode(), dto.getSmsOtp(), "sms")) {
        // throw new RuntimeException("Invalid SMS OTP");
        // }

        otpService.markAsVerified(dto.getUniqueCode());
        AuthorizedUser user = investor.getAuthorizedUser();

        // Generate temporary password
        String temporaryPassword = generateTemporaryPassword();
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        user.setActive(true);
        user.setMustChangePassword(true); // Force password change on first login
        user.setLoginId(user.getEmailId());

        // Assign to default "User Group" (ID: 3)
        userGroupRepository.findById(3L).ifPresent(group -> user.getUserGroups().add(group));

        userRepository.save(user);

        investor.setVerifyStatus(2);
        investorRepository.save(investor);

        UserMgmtApiClient userMgmtClient = userMgmtApiClientProvider.getIfAvailable();
        if (userMgmtClient != null) {
            try {
                Long tenantId = (user.getTenant() != null && user.getTenant().getTenantId() != null)
                        ? user.getTenant().getTenantId()
                        : 1L;
                var b2cConfigOpt = tenantB2CConfigService.getConfigForTenant(tenantId);

                if (b2cConfigOpt.isEmpty() || !Boolean.TRUE.equals(b2cConfigOpt.get().getEnabled())) {
                    log.warn("Azure AD config missing/disabled in DB for tenant {}. Skipping Azure user creation.", tenantId);
                } else {
                    var b2cConfig = b2cConfigOpt.get();
                    SignUpDto signUpDto = SignUpDto.builder()
                            .email(user.getEmailId())
                            .password(temporaryPassword)
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .mobilePhone(user.getMobilePhone())
                            .clientId(b2cConfig.getClientId())
                            .clientSecret(b2cConfig.getClientSecret())
                            .tenantId(b2cConfig.getB2cTenantId())
                            .country(investor.getNationality() != null ? String.valueOf(investor.getNationality()) : null)
                            .build();
                    var result = userMgmtClient.createUserLatest(signUpDto);
                    if (result != null && result.getErrorMsg() != null && !result.getErrorMsg().isEmpty()) {
                        log.warn("User-mgmt Azure AD creation reported: {}", result.getErrorMsg());
                    } else {
                        log.info("User created in Azure AD for investor {}", investor.getId());
                    }
                }
            } catch (Exception e) {
                log.warn("Azure AD user creation failed (local user saved): {}", e.getMessage());
            }
        }

        // Send login credentials email with temporary password
        try {
            String fullName = user.getFirstName() + (user.getLastName() != null ? " " + user.getLastName() : "");
            notificationService.sendLoginDetailsEmail(
                    user.getEmailId(),
                    fullName,
                    user.getLoginId(),
                    temporaryPassword,
                    investor.getUniqueCode());
            log.info("Login credentials email sent to {}", user.getEmailId());
        } catch (Exception e) {
            log.warn("Failed to send login credentials email: {}", e.getMessage());
        }

        // Dataverse write-back (aligned to Laravel InvestorController::investor_register_step4_insert_data L727)
        // Three-call sequence: POST /contacts → POST /ss_investors (with contact bind) → PATCH /contacts (link back).
        // Only run when CRM config is available and the investor hasn't already been pushed.
        DynamicsCrmService crm = dynamicsCrmServiceProvider.getIfAvailable();
        if (crm != null && investor.getDvContactId() == null) {
            try {
                String middleName = personalInfoRepository.findByInvestorUniqueId(investor.getUniqueCode())
                        .map(UserPersonalInformation::getInvestorMiddleName)
                        .orElse(null);
                DynamicsCrmService.InvestorCrmIds ids = crm.registerInvestorInDataverse(
                        user.getFirstName(),
                        middleName,
                        user.getLastName(),
                        user.getEmailId(),
                        user.getMobilePhone()
                );
                if (ids != null) {
                    investor.setDvContactId(ids.contactId);
                    investor.setDvInvestorGuid(ids.investorGuid);
                    investor.setDvInvestorSsId(ids.investorSsName);
                    investorRepository.save(investor);
                    log.info("Dataverse investor record created for {}: contactId={}, investorGuid={}, ss_name={}",
                            investor.getUniqueCode(), ids.contactId, ids.investorGuid, ids.investorSsName);
                }
            } catch (Exception e) {
                // Laravel calls die() on CRM failure (L724), which aborts the request.  Here we log
                // and continue so the local user stays activated and can log in — the Dataverse push
                // can be retried (phase A backfill) without forcing the user to re-register.
                log.error("Dataverse registration write-back failed for {}: {}",
                        investor.getUniqueCode(), e.getMessage(), e);
            }
        }

        return VerificationResponseDto.builder()
                .success(true)
                .message("Registration successful")
                .investorId(investor.getId())
                .uniqueCode(investor.getUniqueCode())
                .build();
    }

    /**
     * Step 4: Registration details (nationality, PAN/OCI, resident status) and
     * investor categorization.
     * This step should be called AFTER OTP verification.
     */
    public VerificationResponseDto registerStep3(String uniqueCode, Step3RegistrationDetailsDto dto, String ipAddress,
            String userAgent) {
        log.info("Processing step 3 (registration details) for code: {}", uniqueCode);

        Investor investor = investorRepository.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new RuntimeException("Investor not found: " + uniqueCode));

        // Update investor with registration details
        investor.setCitizenship(dto.getCitizenship());
        investor.setCountryOfResidence(dto.getCountryOfResidence());
        investor.setNationality(dto.getNationality());
        investor.setResidenceType(dto.getResidenceType());
        investor.setPancardStatus(dto.getPancardStatus());
        investor.setOciCardStatus(dto.getOciCardStatus());
        investor.setIndianOrigin(dto.getIndianOrigin());
        investor.setConfirmation(dto.getConfirmation() ? 1 : 0);
        investor.setPrivacyPolicyAccepted(dto.getPrivacyPolicyAccepted() ? 1 : 0);
        investor.setNotificationConsent(dto.getNotificationConsent() ? 1 : 0);
        investor.setWhatsappConsent(dto.getWhatsappConsent() != null && dto.getWhatsappConsent() ? 1 : 0);

        // Retrieve personal information first (needed for consent record)
        UserPersonalInformation personalInfo = personalInfoRepository.findByInvestorUniqueId(uniqueCode)
                .orElse(null);
        if (personalInfo != null) {
            personalInfo.setCountryOfResidence(
                    dto.getCountryOfResidence() != null ? String.valueOf(dto.getCountryOfResidence()) : null);
            personalInfoRepository.save(personalInfo);
        }

        // Save consent record with audit trail
        saveConsentRecord(investor, dto, ipAddress, userAgent, personalInfo);

        // Auto-categorize investor based on criteria (legacy system)
        String categoryCode = categorizeInvestor(investor);
        if (categoryCode != null) {
            log.info("Investor {} categorized as: {}", uniqueCode, categoryCode);
        } else {
            log.warn("No matching category found for investor {}", uniqueCode);
        }

        // Determine investor type based on registration parameters (new system)
        InvestorType investorType = determineInvestorType(investor);
        investor.setInvestorType(investorType.name());
        investor.setDocumentType(investorType.getDocumentType());
        log.info("Investor {} determined as type: {} (Document: {})", uniqueCode, investorType.getDisplayName(),
                investorType.getDocumentType());

        // Update verification status to indicate registration is complete
        investor.setVerifyStatus(1); // 1 = verified/complete

        investorRepository.save(investor);

        // Send conditional emails based on investor type
        sendCategorizedEmail(investor, categoryCode);

        log.info("Registration details saved for investor: {}", uniqueCode);

        return VerificationResponseDto.builder()
                .success(true)
                .message("Registration completed successfully")
                .investorId(investor.getId())
                .uniqueCode(investor.getUniqueCode())
                .build();
    }

    /**
     * Save consent record with audit trail
     */
    private void saveConsentRecord(Investor investor, Step3RegistrationDetailsDto dto, String ipAddress,
            String userAgent, UserPersonalInformation personalInfo) {
        try {
            LocalDate userDob = (personalInfo != null) ? personalInfo.getUserDob() : null;

            if (userDob == null) {
                log.error("Cannot save consent record - user date of birth is missing for investor: {}",
                        investor.getUniqueCode());
                throw new RuntimeException("User date of birth is required to complete registration");
            }

            InvestorConsents consent = InvestorConsents.builder()
                    .investorUniqueId(investor.getUniqueCode())
                    .investorId(investor.getId())
                    .email(investor.getAuthorizedUser().getEmailId())
                    .userDob(userDob)
                    .informationCorrectConsent(dto.getConfirmation())
                    .privacyPolicyAccepted(dto.getPrivacyPolicyAccepted())
                    .notificationConsent(dto.getNotificationConsent())
                    .whatsappConsent(dto.getWhatsappConsent() != null ? dto.getWhatsappConsent() : false)
                    .consentDate(LocalDateTime.now())
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .consentVersion("1.0")
                    .build();
            consent.setTenant(investor.getTenant());
            consentRepository.save(consent);
            log.info("Consent record saved for investor: {}", investor.getUniqueCode());
        } catch (Exception e) {
            log.error("Failed to save consent record for investor: {}", investor.getUniqueCode(), e);
            throw e;
        }
    }

    /**
     * Determine investor type based on registration parameters.
     * Uses the mapping from docs/Mapping_for_typeof_investore.csv
     * 
     * Types:
     * - RESIDENT_INDIVIDUAL: Individual, Indian, India residence, has PAN
     * - RESIDENT_NON_INDIVIDUAL: Entity, India incorporation, has PAN
     * - NRI: Individual, Indian, Non-India residence, has PAN
     * - OCI: Individual, Non-Indian, Non-India residence, has PAN, Indian Origin,
     * has OCI
     * - FOREIGN_NATIONAL: Individual, Non-Indian, Non-India residence, NOT Indian
     * Origin
     * - FOREIGN_NON_INDIVIDUAL: Entity, Non-India incorporation
     */
    private InvestorType determineInvestorType(Investor investor) {
        boolean isIndividual = investor.getRegisterAs() != null && investor.getRegisterAs() == 1;

        // Determine citizenship (Indian vs Non-Indian)
        boolean isIndianCitizenship = isIndiaNationality(investor.getCitizenship());

        // For entities: check incorporation country
        boolean isIndiaIncorporation = investor.getIncorpCountry() != null &&
                isIndiaNationality(investor.getIncorpCountry());

        // Check residence
        boolean isIndiaResidence = "resident".equalsIgnoreCase(investor.getResidenceType()) ||
                (investor.getCountryOfResidence() != null &&
                        isIndiaNationality(investor.getCountryOfResidence()));

        // Check PAN
        boolean hasPan = "yes".equalsIgnoreCase(investor.getPancardStatus());

        // Check Person of Indian Origin
        Boolean isPersonOfIndianOrigin = null;
        if (investor.getIndianOrigin() != null) {
            isPersonOfIndianOrigin = "yes".equalsIgnoreCase(investor.getIndianOrigin());
        }

        // Check OCI card
        boolean hasOciCard = "yes".equalsIgnoreCase(investor.getOciCardStatus());

        // Determine investor type using enum logic
        InvestorType type = InvestorType.determineType(
                isIndividual,
                isIndianCitizenship,
                isIndiaIncorporation,
                isIndiaResidence,
                hasPan,
                isPersonOfIndianOrigin,
                hasOciCard);

        log.info("Investor type determination - Individual: {}, Indian Citizenship: {}, India Incorporation: {}, " +
                "India Residence: {}, Has PAN: {}, Person of Indian Origin: {}, Has OCI: {} => Type: {}",
                isIndividual, isIndianCitizenship, isIndiaIncorporation, isIndiaResidence,
                hasPan, isPersonOfIndianOrigin, hasOciCard, type.getDisplayName());

        return type;
    }

    /**
     * Categorize investor based on criteria matching InvestorTypeCategory table.
     * Matches on: registerAs, nationality, resident/non-resident, PAN, OCI, Indian
     * origin.
     * LEGACY METHOD - keeping for backward compatibility with InvestorTypeCategory
     * table
     */
    private String categorizeInvestor(Investor investor) {
        // Get all active categories
        List<InvestorTypeCategory> categories = investorTypeCategoryRepository.findByStatusOrderById(1);

        for (InvestorTypeCategory category : categories) {
            boolean matches = true;

            // Match registerAs (1=Self, 2=Legal Entity)
            if (category.getRegisterAs() != null && !category.getRegisterAs().isEmpty()) {
                String expectedRegisterAs = String.valueOf(investor.getRegisterAs());
                if (!category.getRegisterAs().equals(expectedRegisterAs)) {
                    matches = false;
                    continue;
                }
            }

            // Match nationality (if category specifies)
            if (category.getNationality() != null && !category.getNationality().isEmpty()) {
                String expectedNationality = String.valueOf(investor.getNationality());
                if (!category.getNationality().equals(expectedNationality)) {
                    matches = false;
                    continue;
                }
            }

            // Match resident status
            if ("resident".equalsIgnoreCase(investor.getResidenceType())) {
                if (category.getResident() == null || !"yes".equalsIgnoreCase(category.getResident())) {
                    matches = false;
                    continue;
                }
            } else if ("non-resident".equalsIgnoreCase(investor.getResidenceType())) {
                if (category.getNonResident() == null || !"yes".equalsIgnoreCase(category.getNonResident())) {
                    matches = false;
                    continue;
                }
            }

            // Match PAN card status
            if (category.getPancard() != null && !category.getPancard().isEmpty()) {
                String expectedPan = investor.getPancardStatus() != null ? investor.getPancardStatus() : "no";
                if (!category.getPancard().equalsIgnoreCase(expectedPan)) {
                    matches = false;
                    continue;
                }
            }

            // Match OCI card status
            if (category.getOciCard() != null && !category.getOciCard().isEmpty()) {
                String expectedOci = investor.getOciCardStatus() != null ? investor.getOciCardStatus() : "no";
                if (!category.getOciCard().equalsIgnoreCase(expectedOci)) {
                    matches = false;
                    continue;
                }
            }

            // Match Indian origin
            if (category.getIndianOrigin() != null && !category.getIndianOrigin().isEmpty()) {
                String expectedIndianOrigin = investor.getIndianOrigin() != null ? investor.getIndianOrigin() : "no";
                if (!category.getIndianOrigin().equalsIgnoreCase(expectedIndianOrigin)) {
                    matches = false;
                    continue;
                }
            }

            // If all criteria match, return this category
            if (matches) {
                return category.getCatCode();
            }
        }

        return null; // No matching category found
    }

    /**
     * Send conditional emails based on investor category and status.
     * Implements all 10 messaging scenarios for individuals + 6 scenarios for legal
     * entities.
     */
    private void sendCategorizedEmail(Investor investor, String categoryCode) {
        AuthorizedUser user = investor.getAuthorizedUser();
        String email = user.getEmailId();
        String investorName = user.getFirstName();
        if (user.getLastName() != null) {
            investorName += " " + user.getLastName();
        }

        boolean isLegalEntity = investor.getRegisterAs() != null && investor.getRegisterAs() == 2;
        boolean isIntroduced = checkIfIntroducedByServiceProvider(investor);
        boolean isIndiaMarket = investor.getMarket() != null && investor.getMarket() == 1; // Assuming 1 = India
        boolean hasPan = "yes".equalsIgnoreCase(investor.getPancardStatus());
        boolean hasOci = "yes".equalsIgnoreCase(investor.getOciCardStatus());
        boolean isIndianOrigin = "yes".equalsIgnoreCase(investor.getIndianOrigin());
        boolean isIndiaNationality = isIndiaNationality(investor.getCitizenship());
        boolean isIndiaResidence = "resident".equalsIgnoreCase(investor.getResidenceType())
                || (investor.getCountryOfResidence() != null && isIndiaNationality(investor.getCountryOfResidence()));
        boolean residentIndianWithPan = isIndiaNationality && isIndiaResidence && hasPan;

        try {
            // LEGAL ENTITY SCENARIOS (6 scenarios)
            if (isLegalEntity) {
                String entityName = investor.getEntityName() != null ? investor.getEntityName() : investorName;
                String representativeName = investor.getEntityNameRepresentative() != null
                        ? investor.getEntityNameRepresentative()
                        : investorName;
                boolean isForeignEntity = !isIndiaNationality(investor.getIncorpCountry());

                // LEGAL ENTITY SCENARIO A: Market other than India
                if (!isIndiaMarket) {
                    String marketName = getMarketName(investor.getMarket());
                    notificationService.sendLegalEntityMarketOtherThanIndiaEmail(email, entityName, marketName);
                    log.info("Legal Entity Scenario A: Market other than India - email sent to: {}", email);
                    return;
                }

                // LEGAL ENTITY SCENARIO B: India market, no PAN (not introduced)
                if (isIndiaMarket && !hasPan && !isIntroduced) {
                    notificationService.sendLegalEntityIndiaNoPanEmail(email, entityName, representativeName);
                    log.info("Legal Entity Scenario B: India market, no PAN - email sent to: {}", email);
                    return;
                }

                // LEGAL ENTITY SCENARIO C: Foreign Legal Entity
                if (isIndiaMarket && isForeignEntity && !isIntroduced) {
                    notificationService.sendForeignLegalEntityEmail(email, entityName, representativeName);
                    log.info("Legal Entity Scenario C: Foreign entity - email sent to: {}", email);
                    return;
                }

                // LEGAL ENTITY SCENARIO D: PAN Yes, not introduced by SP
                if (isIndiaMarket && hasPan && !isIntroduced) {
                    String loginUrl = baseUrl + "/login";
                    notificationService.sendLegalEntitySuccessfulSelfRegistrationEmail(email, entityName,
                            representativeName, investor.getUniqueCode(), loginUrl);
                    log.info("Legal Entity Scenario D: Successful registration (self) - email sent to: {}", email);
                    return;
                }

                // LEGAL ENTITY SCENARIO E: PAN Yes, introduced by SP
                if (isIndiaMarket && hasPan && isIntroduced) {
                    String serviceProviderName = getServiceProviderName(investor);
                    String appointmentUrl = baseUrl + "/investor/appointments";
                    notificationService.sendLegalEntitySuccessfulIntroducedEmail(email, entityName, representativeName,
                            serviceProviderName, appointmentUrl);
                    log.info("Legal Entity Scenario E: Successful registration (introduced) - email sent to: {}",
                            email);
                    return;
                }

                // LEGAL ENTITY SCENARIO F: PAN No, introduced by SP
                if (isIndiaMarket && !hasPan && isIntroduced) {
                    String spEmail = getServiceProviderEmail(investor);
                    String spName = getServiceProviderName(investor);
                    if (spEmail != null && spName != null) {
                        notificationService.sendLegalEntityNoPanNotificationToSP(spEmail, spName, entityName,
                                representativeName, email);
                        log.info("Legal Entity Scenario F: No PAN (introduced) - notification sent to SP: {}", spEmail);
                    }
                    // Also send to entity
                    notificationService.sendLegalEntityIndiaNoPanEmail(email, entityName, representativeName);
                    return;
                }

                // Fallback for legal entities
                if (categoryCode != null) {
                    String loginUrl = baseUrl + "/login";
                    notificationService.sendLegalEntitySuccessfulSelfRegistrationEmail(email, entityName,
                            representativeName, investor.getUniqueCode(), loginUrl);
                    log.info("Legal Entity Default: Successful registration - email sent to: {}", email);
                }
                return;
            }

            // INDIVIDUAL SCENARIOS (10 scenarios) - existing logic
            // SCENARIO 1: Market other than India
            if (!isIndiaMarket) {
                String marketName = getMarketName(investor.getMarket());
                notificationService.sendMarketOtherThanIndiaEmail(email, investorName, marketName);
                log.info("Scenario 1: Market other than India - email sent to: {}", email);
                return;
            }

            // SCENARIO 2: India market, no PAN (not introduced)
            if (isIndiaMarket && !hasPan && !isIntroduced) {
                notificationService.sendIndiaNoPanEmail(email, investorName);
                log.info("Scenario 2: India market, no PAN - email sent to: {}", email);
                return;
            }

            // SCENARIO 3: Person of Indian Origin, no OCI (not introduced)
            if (isIndiaMarket && isIndianOrigin && !isIndiaNationality && !hasOci && !isIntroduced && hasPan) {
                notificationService.sendIndianOriginNoOciEmail(email, investorName);
                log.info("Scenario 3: Indian origin, no OCI - email sent to: {}", email);
                return;
            }

            // SCENARIO 4: Person of Indian Origin, no PAN and no OCI (not introduced)
            if (isIndiaMarket && isIndianOrigin && !isIndiaNationality && !hasPan && !hasOci && !isIntroduced) {
                notificationService.sendIndianOriginNoPanAndOciEmail(email, investorName);
                log.info("Scenario 4: Indian origin, no PAN and no OCI - email sent to: {}", email);
                return;
            }

            // SCENARIO 5: Foreign Individual (non-Indian origin, not PIO)
            if (isIndiaMarket && !isIndiaNationality && !isIndianOrigin && investor.getRegisterAs() == 1) {
                notificationService.sendOutsideIndiaAssistanceSelfEmail(email, investorName);
                log.info("Scenario 5: Foreign individual - email sent to: {}", email);
                return;
            }

            // SCENARIO 6: PAN Yes (and OCI Yes if needed for NRI), not introduced by SP -
            // Successful registration
            // Resident Indian (India citizen + India residence) with PAN: always thank you
            // (OCI not required)
            // NRI of Indian origin: needs OCI for thank you
            // Not Indian origin: thank you
            boolean eligibleForThankYou = !isIndianOrigin || (isIndianOrigin && hasOci) || residentIndianWithPan;
            if (isIndiaMarket && hasPan && !isIntroduced && eligibleForThankYou) {
                String loginUrl = baseUrl + "/login";
                notificationService.sendSuccessfulRegistrationSelfEmail(email, investorName, investor.getUniqueCode(),
                        loginUrl);
                log.info("Scenario 6: Successful registration (self) - email sent to: {}", email);
                return;
            }

            // SCENARIO 7: PAN Yes, OCI Yes (if needed for NRI), introduced by SP - Direct
            // to appointment
            if (isIndiaMarket && hasPan && isIntroduced
                    && (residentIndianWithPan || !isIndianOrigin || (isIndianOrigin && hasOci))) {
                String serviceProviderName = getServiceProviderName(investor);
                String appointmentUrl = baseUrl + "/investor/appointments";
                notificationService.sendSuccessfulRegistrationIntroducedEmail(email, investorName, serviceProviderName,
                        appointmentUrl);
                log.info("Scenario 7: Successful registration (introduced) - email sent to: {}", email);
                return;
            }

            // SCENARIO 8: PAN No, introduced by SP - Notify SP
            if (isIndiaMarket && !hasPan && isIntroduced) {
                String spEmail = getServiceProviderEmail(investor);
                String spName = getServiceProviderName(investor);
                if (spEmail != null && spName != null) {
                    notificationService.sendNoPanIntroducedNotificationToSP(spEmail, spName, investorName, email);
                    log.info("Scenario 8: No PAN (introduced) - notification sent to SP: {}", spEmail);
                }
                // Also send to investor
                notificationService.sendIndiaNoPanEmail(email, investorName);
                return;
            }

            // SCENARIO 9: OCI No by Person of Indian Origin, introduced by SP - Notify SP
            if (isIndiaMarket && isIndianOrigin && !isIndiaNationality && !hasOci && isIntroduced && hasPan) {
                String spEmail = getServiceProviderEmail(investor);
                String spName = getServiceProviderName(investor);
                if (spEmail != null && spName != null) {
                    notificationService.sendNoOciIntroducedNotificationToSP(spEmail, spName, investorName, email);
                    log.info("Scenario 9: No OCI (introduced) - notification sent to SP: {}", spEmail);
                }
                // Also send to investor
                notificationService.sendIndianOriginNoOciEmail(email, investorName);
                return;
            }

            // SCENARIO 10: PAN No and OCI No, introduced by SP - Notify SP
            if (isIndiaMarket && isIndianOrigin && !isIndiaNationality && !hasPan && !hasOci && isIntroduced) {
                String spEmail = getServiceProviderEmail(investor);
                String spName = getServiceProviderName(investor);
                if (spEmail != null && spName != null) {
                    notificationService.sendNoPanAndOciIntroducedNotificationToSP(spEmail, spName, investorName, email);
                    log.info("Scenario 10: No PAN and OCI (introduced) - notification sent to SP: {}", spEmail);
                }
                // Also send to investor
                notificationService.sendIndianOriginNoPanAndOciEmail(email, investorName);
                return;
            }

            // Fallback: Default successful registration
            // Send thank you when: categoryCode exists, OR self-registration individual
            // with India market and PAN
            // (latter covers case when investor_type_category has no matching row)
            boolean isIndividualWithPan = investor.getRegisterAs() != null && investor.getRegisterAs() == 1;
            if (categoryCode != null || (isIndiaMarket && hasPan && !isIntroduced && isIndividualWithPan)) {
                String loginUrl = baseUrl + "/login";
                notificationService.sendSuccessfulRegistrationSelfEmail(email, investorName, investor.getUniqueCode(),
                        loginUrl);
                log.info("Default: Successful registration - email sent to: {}", email);
            } else {
                log.warn("No matching category and no fallback scenario for investor {} - thank you email not sent",
                        email);
            }
        } catch (Exception e) {
            log.warn("Failed to send categorized email: {}", e.getMessage(), e);
        }
    }

    /**
     * Check if investor was introduced by a service provider
     */
    private boolean checkIfIntroducedByServiceProvider(Investor investor) {
        try {
            // Check by unique code
            if (investor.getUniqueCode() != null) {
                return introInvestorTempRepository.findByUniqueCodeDb(investor.getUniqueCode()).isPresent();
            }

            // Check by email
            if (investor.getAuthorizedUser() != null && investor.getAuthorizedUser().getEmailId() != null) {
                return introInvestorTempRepository.findByIntroEmail(investor.getAuthorizedUser().getEmailId())
                        .isPresent();
            }

            return false;
        } catch (Exception e) {
            log.warn("Error checking if investor was introduced: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get service provider email for introduced investor
     */
    private String getServiceProviderEmail(Investor investor) {
        try {
            // Find introduction record
            IntroInvestorTemp introRecord = null;
            if (investor.getUniqueCode() != null) {
                introRecord = introInvestorTempRepository.findByUniqueCodeDb(investor.getUniqueCode()).orElse(null);
            }

            if (introRecord == null && investor.getAuthorizedUser() != null
                    && investor.getAuthorizedUser().getEmailId() != null) {
                introRecord = introInvestorTempRepository.findByIntroEmail(investor.getAuthorizedUser().getEmailId())
                        .orElse(null);
            }

            if (introRecord != null && introRecord.getSsBrokerValue() != null) {
                // Get broker email from MasterBrokers
                MasterBrokers broker = masterBrokersRepository.findById(Long.valueOf(introRecord.getSsBrokerValue()))
                        .orElse(null);
                if (broker != null) {
                    return buildBrokerEmail(broker.getSsBrokerId());
                }
            }

            return null;
        } catch (Exception e) {
            log.warn("Error getting service provider email: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get service provider name for introduced investor
     */
    private String getServiceProviderName(Investor investor) {
        try {
            // Find introduction record
            IntroInvestorTemp introRecord = null;
            if (investor.getUniqueCode() != null) {
                introRecord = introInvestorTempRepository.findByUniqueCodeDb(investor.getUniqueCode()).orElse(null);
            }

            if (introRecord == null && investor.getAuthorizedUser() != null
                    && investor.getAuthorizedUser().getEmailId() != null) {
                introRecord = introInvestorTempRepository.findByIntroEmail(investor.getAuthorizedUser().getEmailId())
                        .orElse(null);
            }

            if (introRecord != null && introRecord.getSsBrokerValue() != null) {
                // Get broker name from MasterBrokers
                MasterBrokers broker = masterBrokersRepository.findById(Long.valueOf(introRecord.getSsBrokerValue()))
                        .orElse(null);
                if (broker != null && broker.getSsName() != null) {
                    return broker.getSsName();
                }
            }

            return "Your Service Provider";
        } catch (Exception e) {
            log.warn("Error getting service provider name: {}", e.getMessage());
            return "Your Service Provider";
        }
    }

    /**
     * Get market name by ID from market_type table
     */
    private String getMarketName(Integer marketId) {
        if (marketId == null)
            return "International Market";
        return marketTypeRepository.findByMarketId(marketId)
                .map(MarketType::getMarketName)
                .orElse(marketId == 1 ? "India" : "International Market");
    }

    /**
     * Check if citizenship is India
     */
    private boolean isIndiaNationality(Integer citizenshipId) {
        // TODO: Query nationality/country table
        // For now, assume 1 = India (update based on actual data)
        return citizenshipId != null && (citizenshipId == 1 || citizenshipId == 240);
    }

    /**
     * Helper method to get nationality name (simplified - in production should
     * query nationality table)
     */
    private String getNationalityName(Integer nationalityId) {
        // TODO: Query actual nationality table
        // For now, assume 1 = India, anything else is non-India
        return (nationalityId != null && nationalityId == 1) ? "India" : "Other";
    }

    /**
     * Temporary fallback until broker contact emails are maintained explicitly.
     */
    private String buildBrokerEmail(String brokerId) {
        if (brokerId == null || brokerId.isBlank()) {
            return null;
        }
        String cleanId = brokerId.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        return cleanId.isBlank() ? null : cleanId + "@broker.facilon.com";
    }

    private String generateUniqueCode() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String code = datePrefix + (1000 + RANDOM.nextInt(9000));
        while (investorRepository.findByUniqueCode(code).isPresent()) {
            code = datePrefix + (1000 + RANDOM.nextInt(9000));
        }
        return code;
    }

    /**
     * Generate secure temporary password
     * Format: Invest@#### (4 random digits)
     * Example: Invest@1234
     */
    private String generateTemporaryPassword() {
        String randomDigits = String.format("%04d", RANDOM.nextInt(10000));
        return "Invest@" + randomDigits;
    }
}
