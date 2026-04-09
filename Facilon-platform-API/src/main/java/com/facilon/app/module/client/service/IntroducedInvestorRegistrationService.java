package com.facilon.app.module.client.service;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.integration.dynamics.DynamicsCrmService;
import com.facilon.app.integration.laravel.LaravelCryptPayloadDecryptor;
import com.facilon.app.integration.graphemail.GraphEmailService;
import com.facilon.app.integration.usermgmt.UserMgmtApiClient;
import com.facilon.app.integration.usermgmt.dto.MicrosoftGraphResponseDto;
import com.facilon.app.integration.usermgmt.dto.SignUpDto;
import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.model.Tenant;
import com.facilon.app.model.TenantB2CConfig;
import com.facilon.app.module.client.dto.introduced.*;
import com.facilon.app.module.client.model.*;
import com.facilon.app.module.client.model.master.MasterCountryOfResidence;
import com.facilon.app.module.client.model.master.MasterNationality;
import com.facilon.app.module.client.repository.*;
import com.facilon.app.repository.AuthorizedUserRepository;
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
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * Service for Introduced Investor Registration Flow
 * Matches Laravel: introduce-multiple-register-main-step → consent → step1 →
 * step2 → step4 → B2C creation
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class IntroducedInvestorRegistrationService {

    private static final Random RANDOM = new Random();
    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final String DEFAULT_PASSWORD = "Invest@1234"; // Matches Laravel

    private final DynamicsCrmService dynamicsCrmService;
    private final LaravelCryptPayloadDecryptor laravelCryptPayloadDecryptor;
    private final IntroInvestorTempRepository introInvestorTempRepository;
    private final AuthorizedUserRepository authorizedUserRepository;
    private final InvestorRepository investorRepository;
    private final OtpVerificationRepository otpVerificationRepository;
    private final IntroducedRegistrationSessionRepository sessionRepository;
    private final MasterNationalityRepository nationalityRepository;
    private final MasterCountryOfResidenceRepository countryOfResidenceRepository;
    private final InvestorConsentsRepository investorConsentsRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<GraphEmailService> graphEmailServiceProvider;
    private final ObjectProvider<UserMgmtApiClient> userMgmtClientProvider;
    private final TenantB2CConfigService tenantB2CConfigService;

    @Value("${app.client_url:http://localhost:3000}")
    private String clientUrl;

    /**
     * Resolves URL segment from Laravel
     * {@code introduce-investor1/{Crypt::encrypt(introduce_id)}} or plain
     * {@code ss_name}.
     */
    public String resolveInvestorReference(String investorRef, String dataverseInvestorId) {
        String raw = null;
        if (investorRef != null && !investorRef.isBlank()) {
            raw = investorRef.trim();
        } else if (dataverseInvestorId != null && !dataverseInvestorId.isBlank()) {
            raw = dataverseInvestorId.trim();
        }
        if (raw == null) {
            throw new IllegalArgumentException("investorRef or dataverseInvestorId is required");
        }
        if (laravelCryptPayloadDecryptor.isLaravelEncryptedPayload(raw)) {
            return laravelCryptPayloadDecryptor.decrypt(raw);
        }
        return raw;
    }

    /**
     * STEP 0: Initiate registration with Dataverse Investor ID
     * Matches Laravel: introduce_investor_register_pms_main_step_show()
     */
    @Transactional
    public IntroducedInvestorDetailsDto initiateRegistration(String dataverseInvestorId) {
        Tenant tenant = TenantContextHolder.getContext().getTenant();

        log.info("🔍 Initiating introduced investor registration for Dataverse ID: {}", dataverseInvestorId);

        // Fetch investor data from Dataverse
        Map<String, Object> dataverseInvestor = dynamicsCrmService.fetchInvestorFromDataverse(dataverseInvestorId);

        if (dataverseInvestor == null || dataverseInvestor.isEmpty()) {
            throw new IllegalArgumentException("Investor not found in Dataverse: " + dataverseInvestorId);
        }

        // Extract details
        String investorGuid = (String) dataverseInvestor.get("ss_investorid");
        String introEmail = (String) dataverseInvestor.get("ss_emailintroduceind");
        String introFirstName = (String) dataverseInvestor.get("ss_firstnameintroduceind");
        String introMiddleName = (String) dataverseInvestor.get("ss_middlenameintroduceind");
        String introLastName = (String) dataverseInvestor.get("ss_lastnameintroduceind");
        String introMobile = (String) dataverseInvestor.get("ss_mobilephoneintroduceind");
        String introDvInvestorSsId = (String) dataverseInvestor.get("ss_name");
        String introDvNationality = dynamicsCrmService.extractNationalityLookupGuid(dataverseInvestor);
        String ssBrokerValue = (String) dataverseInvestor.get("_ss_nameofportfoliomanager_value");
        String brokerPreferredBank = (String) dataverseInvestor.get("_ss_brokerpreferredbank_value");
        String investRouteValue = (String) dataverseInvestor.get("_ss_investorroute_value");

        // Handle option set fields (can be Integer or String)
        String serviceProviderType = dataverseInvestor.get("ss_serviceprovidertype") != null
                ? String.valueOf(dataverseInvestor.get("ss_serviceprovidertype"))
                : null;
        String ssIpRecords = dataverseInvestor.get("ss_iprecords") != null
                ? String.valueOf(dataverseInvestor.get("ss_iprecords"))
                : null;
        String ssApplicableToSlt = dataverseInvestor.get("ss_applicabletoslt") != null
                ? String.valueOf(dataverseInvestor.get("ss_applicabletoslt"))
                : null;

        String ssInvestorTypeValue = (String) dataverseInvestor.get("_ss_investortype_value");
        String ssProductValue = (String) dataverseInvestor.get("_ss_product_value");
        String ssBrokeragePlanValue = (String) dataverseInvestor.get("_ss_portfoliomanagerplan_value");
        String ssSchemeValue = (String) dataverseInvestor.get("_ss_scheme_value");
        String ssCountryOfResidenceValue = (String) dataverseInvestor.get("_ss_countryofresidence_value");

        // Check if email already exists
        Optional<AuthorizedUser> existingUser = authorizedUserRepository.findByEmailId(introEmail);
        boolean emailExists = existingUser.isPresent();

        // Create or update intro_investor_temp
        IntroInvestorTemp introInvestor = introInvestorTempRepository
                .findByIntroDvInvestorSsId(introDvInvestorSsId)
                .orElse(new IntroInvestorTemp());

        introInvestor.setIntroFirstName(introFirstName);
        introInvestor.setIntroMiddleName(introMiddleName);
        introInvestor.setIntroLastName(introLastName);
        introInvestor.setIntroEmail(introEmail);
        introInvestor.setIntroMobile(introMobile);
        introInvestor.setIntroDvInvestorSsId(introDvInvestorSsId);
        introInvestor.setIntroDvNationality(introDvNationality);
        introInvestor.setIntroInvestorId(investorGuid);
        introInvestor.setSsBrokerValue(ssBrokerValue);
        introInvestor.setBrokerPreferredBank(brokerPreferredBank);
        introInvestor.setInvestRouteValue(investRouteValue);
        introInvestor.setServiceProviderType(serviceProviderType);
        introInvestor.setSsIpRecords(ssIpRecords);
        introInvestor.setSsApplicableToSlt(ssApplicableToSlt);
        introInvestor.setSsInvestorTypeValue(ssInvestorTypeValue);
        introInvestor.setSsProductValue(ssProductValue);
        introInvestor.setSsBrokeragePlanValue(ssBrokeragePlanValue);
        introInvestor.setIntroSchemeName(ssSchemeValue);
        introInvestor.setIntroCountryOfResidence(ssCountryOfResidenceValue);
        introInvestor.setTenant(tenant);

        introInvestor = introInvestorTempRepository.save(introInvestor);

        // Fetch display names
        String brokerName = dynamicsCrmService.fetchBrokerName(ssBrokerValue);
        String productName = dynamicsCrmService.fetchProductName(ssProductValue).orElse(null);
        String planName = ssBrokeragePlanValue != null
                ? dynamicsCrmService.fetchPlanName(ssBrokeragePlanValue).orElse(null)
                : null;
        String schemeName = ssSchemeValue != null ? dynamicsCrmService.fetchSchemeName(ssSchemeValue) : null;
        String nationalityName = dynamicsCrmService.fetchNationalityName(introDvNationality);
        String countryOfResidenceName = ssCountryOfResidenceValue != null
                ? dynamicsCrmService.fetchCountryOfResidenceName(ssCountryOfResidenceValue)
                : null;
        String investorTypeName = dynamicsCrmService.fetchInvestorTypeName(ssInvestorTypeValue);

        log.info("✅ Introduced investor details fetched: email={}, exists={}", introEmail, emailExists);

        return IntroducedInvestorDetailsDto.builder()
                .dataverseInvestorId(introDvInvestorSsId)
                .email(introEmail)
                .firstName(introFirstName)
                .middleName(introMiddleName)
                .lastName(introLastName)
                .mobile(introMobile)
                .brokerName(brokerName)
                .serviceProviderType(getServiceProviderTypeName(serviceProviderType))
                .productName(productName)
                .planName(planName != null ? planName : "Not Applicable")
                .schemeName(schemeName != null ? schemeName : "Not Applicable")
                .nationalityName(nationalityName)
                .countryOfResidenceName(countryOfResidenceName)
                .investorTypeName(investorTypeName)
                .emailAlreadyExists(emailExists)
                .build();
    }

    /**
     * STEP 1: Record data consent
     * Matches Laravel: data-consent-management.blade.php
     */
    @Transactional
    public ApiResponseDto recordConsent(String dataverseInvestorId) {
        Tenant tenant = TenantContextHolder.getContext().getTenant();

        IntroInvestorTemp introInvestor = introInvestorTempRepository
                .findByIntroDvInvestorSsId(dataverseInvestorId)
                .orElseThrow(() -> new IllegalArgumentException("Investor not found"));

        // Generate unique code
        String uniqueCode = generateUniqueCode();

        // Create session
        IntroducedRegistrationSession session = IntroducedRegistrationSession.builder()
                .uniqueCode(uniqueCode)
                .dataverseInvestorId(dataverseInvestorId)
                .dataverseInvestorGuid(introInvestor.getIntroInvestorId())
                .email(introInvestor.getIntroEmail())
                .currentStep(1)
                .consentGiven(true)
                .consentGivenAt(LocalDateTime.now())
                .emailOtpVerified(false)
                .mobileOtpVerified(false)
                .registrationCompleted(false)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();

        session.setTenant(tenant);
        session = sessionRepository.save(session);

        log.info("✅ Consent recorded for Dataverse ID: {}, uniqueCode: {}", dataverseInvestorId, uniqueCode);

        return ApiResponseDto.builder()
                .success(true)
                .message("Consent recorded successfully")
                .uniqueCode(uniqueCode)
                .nextStep("step1")
                .build();
    }

    /**
     * Get session prefill data for Step1 form.
     * Returns email, firstName, lastName etc. from the session +
     * intro_investor_temp.
     */
    public IntroducedInvestorDetailsDto getSessionPrefill(String uniqueCode) {
        IntroducedRegistrationSession session = getSession(uniqueCode);

        IntroInvestorTemp introInvestor = introInvestorTempRepository
                .findByIntroDvInvestorSsId(session.getDataverseInvestorId())
                .orElse(null);

        return IntroducedInvestorDetailsDto.builder()
                .uniqueCode(uniqueCode)
                .dataverseInvestorId(session.getDataverseInvestorId())
                .email(session.getEmail())
                .firstName(introInvestor != null ? introInvestor.getIntroFirstName() : session.getFirstName())
                .middleName(introInvestor != null ? introInvestor.getIntroMiddleName() : session.getMiddleName())
                .lastName(introInvestor != null ? introInvestor.getIntroLastName() : session.getLastName())
                .mobile(introInvestor != null ? introInvestor.getIntroMobile() : session.getMobileNumber())
                .build();
    }

    /**
     * STEP 2: Submit personal details and send OTP
     * Matches Laravel: introduce_investor_register_step1_insert_data()
     */
    @Transactional
    public ApiResponseDto submitStep1AndSendOtp(Step1RequestDto dto) {
        IntroducedRegistrationSession session = getSession(dto.getUniqueCode());

        if (!session.getConsentGiven()) {
            throw new IllegalArgumentException("Please provide consent first");
        }

        // Update session with personal details
        session.setFirstName(dto.getFirstName());
        session.setMiddleName(dto.getMiddleName());
        session.setLastName(dto.getLastName());
        session.setDateOfBirth(dto.getDateOfBirth());
        session.setGender(dto.getGender());
        session.setMobileNumber(dto.getMobileNumber());
        session.setCountryCode(dto.getCountryCode());
        session.setCurrentStep(2);
        sessionRepository.save(session);

        // Generate OTP
        String emailOtp = generateOtp();

        // Store OTP
        OtpVerification otp = otpVerificationRepository.findByUniqueCode(dto.getUniqueCode())
                .orElse(new OtpVerification());
        otp.setUniqueCode(dto.getUniqueCode());
        otp.setEmailOtp(emailOtp);
        otp.setSmsOtp("1111"); // Placeholder
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        otp.setVerified(false);
        otp.setAttempts(0);
        otpVerificationRepository.save(otp);

        // Send OTP email
        GraphEmailService graphEmailService = graphEmailServiceProvider.getIfAvailable();
        boolean emailSent = false;
        if (graphEmailService != null) {
            try {
                emailSent = graphEmailService.sendOtpEmail(session.getEmail(), dto.getFirstName(), emailOtp);
                log.info("📧 Email OTP sent to: {}", session.getEmail());
            } catch (Exception e) {
                log.error("Failed to send email OTP: {}", e.getMessage());
            }
        }

        log.info("✅ Step1 submitted and OTP sent for uniqueCode: {}", dto.getUniqueCode());

        return ApiResponseDto.builder()
                .success(true)
                .message(
                        "An email with a One-Time Password has been sent to your email address. This OTP is valid for 10 minutes.")
                .uniqueCode(dto.getUniqueCode())
                .nextStep("step2")
                .build();
    }

    /**
     * STEP 3: Verify OTP
     * Matches Laravel: introduce_investor_register_step2_verify_otp()
     */
    @Transactional
    public ApiResponseDto verifyOtp(Step2OtpVerificationDto dto) {
        IntroducedRegistrationSession session = getSession(dto.getUniqueCode());

        OtpVerification otp = otpVerificationRepository.findByUniqueCode(dto.getUniqueCode())
                .orElseThrow(() -> new IllegalArgumentException("OTP not found"));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP has expired. Please request a new OTP.");
        }

        if (!otp.getEmailOtp().equals(dto.getEmailOtp())) {
            throw new IllegalArgumentException("Invalid email OTP");
        }

        otp.setVerified(true);
        otpVerificationRepository.save(otp);

        session.setEmailOtpVerified(true);
        session.setCurrentStep(3);
        sessionRepository.save(session);

        log.info("✅ OTP verified for uniqueCode: {}", dto.getUniqueCode());

        return ApiResponseDto.builder()
                .success(true)
                .message("OTP verified successfully")
                .uniqueCode(dto.getUniqueCode())
                .nextStep("step4")
                .build();
    }

    /**
     * STEP 4: Complete registration with nationality details + B2C account creation
     * Matches Laravel: introduce_investor_register_step4_insert_data()
     */
    @Transactional
    public ApiResponseDto completeRegistration(Step4CompletionDto dto) {
        IntroducedRegistrationSession session = getSession(dto.getUniqueCode());

        if (!session.getEmailOtpVerified()) {
            throw new IllegalArgumentException("Please verify OTP first");
        }

        if (session.getRegistrationCompleted()) {
            throw new IllegalArgumentException("Registration already completed");
        }

        Tenant tenant = TenantContextHolder.getContext().getTenant();

        // Fetch intro_investor_temp data
        IntroInvestorTemp introInvestor = introInvestorTempRepository
                .findByIntroDvInvestorSsId(session.getDataverseInvestorId())
                .orElseThrow(() -> new IllegalArgumentException("Introduced investor data not found"));

        // Fetch contact ID from Dataverse
        String contactId = dynamicsCrmService.fetchContactIdFromDataverse(session.getDataverseInvestorGuid());

        // Generate password (Invest@1234)
        String password = DEFAULT_PASSWORD;

        // Create authorized user
        AuthorizedUser user = AuthorizedUser.builder()
                .firstName(session.getFirstName())
                .lastName(session.getLastName())
                .emailId(session.getEmail())
                .mobilePhone(session.getMobileNumber())
                .password(passwordEncoder.encode(password))
                .loginId(session.getEmail())
                .isActive(true)
                .mustChangePassword(false)
                .lastPasswordChange(LocalDateTime.now())
                .passwordChangeStatus(false)
                .failedLogins(0)
                .build();
        user.setTenant(tenant);
        user = authorizedUserRepository.save(user);

        // Create investor record
        Investor investor = Investor.builder()
                .authorizedUser(user)
                .uniqueCode(dto.getUniqueCode())
                .registerAs("Self".equals(dto.getSelfOrLegalEntity()) ? 1 : 2)
                .market(1)
                .nationality(parseNationality(dto.getNationality()))
                .pancardStatus(dto.getPanCardStatus())
                .indianOrigin(dto.getIndianOrigin())
                .ociCardStatus(dto.getOciCardStatus())
                .termsRead(dto.getTermsAccepted() ? 1 : 0)
                .confirmation(dto.getTermsAccepted() ? 1 : 0)
                .verifyStatus(2)
                .dvInvestorSsId(session.getDataverseInvestorGuid())
                .dvContactId(contactId)
                .countryOfResidence(parseCountryOfResidence(dto.getCountryOfResidence()))
                .whatsappConsent(dto.getAgreeForWhatsapp() != null && dto.getAgreeForWhatsapp() ? 1 : 0)
                .entityName(dto.getEntityName())
                .entityNameRepresentative(
                        dto.getEntityName() != null ? session.getFirstName() + " " + session.getLastName() : null)
                .companyCapacity(dto.getRepresentativeCapacity())
                .securityRegulated(dto.getSecurityRegulated())
                .build();
        investor.setTenant(tenant);
        investor = investorRepository.save(investor);

        // Save investor consents to investor_consents table
        saveInvestorConsents(dto.getUniqueCode(), session, dto, investor.getId(), tenant);

        // Update intro_investor_temp with unique_code
        introInvestor.setUniqueCodeDb(dto.getUniqueCode());
        introInvestorTempRepository.save(introInvestor);

        // **CRITICAL: Create B2C Account**
        boolean b2cAccountCreated = createB2CAccount(user, password,
                session.getFirstName() + " " + session.getLastName());

        // Send registration credentials email ONLY if B2C account created
        if (b2cAccountCreated) {
            sendRegistrationCredentialsEmail(session.getEmail(),
                    session.getFirstName() + " " + session.getLastName(), password);
        } else {
            log.warn("⚠️ Registration credentials email NOT sent because B2C account creation failed");
        }

        // Mark session as completed
        session.setRegistrationCompleted(true);
        session.setCompletedAt(LocalDateTime.now());
        session.setCurrentStep(5);
        sessionRepository.save(session);

        log.info("✅ Introduced investor registration completed: uniqueCode={}, email={}, B2C={}",
                dto.getUniqueCode(), session.getEmail(), b2cAccountCreated ? "✓" : "✗");

        return ApiResponseDto.builder()
                .success(true)
                .message(b2cAccountCreated
                        ? "Registration completed successfully! Login credentials have been sent to your email."
                        : "Registration completed, but there was an issue creating your login account. Please contact support.")
                .uniqueCode(dto.getUniqueCode())
                .investorId(investor.getId())
                .b2cAccountCreated(b2cAccountCreated)
                .build();
    }

    /**
     * Resend OTP
     */
    @Transactional
    public ApiResponseDto resendOtp(String uniqueCode) {
        IntroducedRegistrationSession session = getSession(uniqueCode);

        if (session.getEmailOtpVerified()) {
            throw new IllegalArgumentException("OTP already verified");
        }

        // Generate new OTP
        String emailOtp = generateOtp();

        // Update OTP
        OtpVerification otp = otpVerificationRepository.findByUniqueCode(uniqueCode)
                .orElse(new OtpVerification());
        otp.setUniqueCode(uniqueCode);
        otp.setEmailOtp(emailOtp);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        otp.setVerified(false);
        otpVerificationRepository.save(otp);

        // Send OTP email
        GraphEmailService graphEmailService = graphEmailServiceProvider.getIfAvailable();
        boolean sent = false;
        if (graphEmailService != null) {
            try {
                sent = graphEmailService.sendOtpEmail(session.getEmail(), session.getFirstName(), emailOtp);
                log.info("📧 OTP resent to: {}", session.getEmail());
            } catch (Exception e) {
                log.error("Failed to resend OTP: {}", e.getMessage());
            }
        }

        return ApiResponseDto.builder()
                .success(true)
                .message("OTP has been resent to your email")
                .build();
    }

    // ============================================
    // B2C Account Creation
    // ============================================

    /**
     * Create B2C account - aligned with PublicRegistrationService.createAzureUser()
     */
    private boolean createB2CAccount(AuthorizedUser user, String password, String displayName) {
        UserMgmtApiClient userMgmtClient = userMgmtClientProvider.getIfAvailable();
        if (userMgmtClient == null) {
            log.error("❌ User management service not available. B2C account NOT created for: {}", user.getEmailId());
            return false;
        }

        try {
            TenantB2CConfig b2cConfig = tenantB2CConfigService.getConfigForCurrentTenantOrDefault();
            if (b2cConfig == null) {
                log.error("❌ Azure B2C configuration not found. B2C account NOT created.");
                return false;
            }

            // Determine correct tenant identifier — same as self-registration
            String tenantIdentifier;
            if (b2cConfig.getB2cTenantId() != null && !b2cConfig.getB2cTenantId().isEmpty()) {
                tenantIdentifier = b2cConfig.getB2cTenantId();
            } else {
                tenantIdentifier = b2cConfig.getB2cTenantName() + ".onmicrosoft.com";
            }

            log.info("🔐 Creating Azure B2C user with tenant: {}", tenantIdentifier);

            // Build SignUpDto — aligned with self-registration (no hardcoded issuer)
            SignUpDto signUpDto = SignUpDto.builder()
                    .email(user.getEmailId())
                    .password(password)
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .mobilePhone(user.getMobilePhone())
                    .clientId(b2cConfig.getClientId())
                    .clientSecret(b2cConfig.getClientSecret())
                    .tenantId(tenantIdentifier)
                    .build();

            MicrosoftGraphResponseDto response = userMgmtClient.createUserLatest(signUpDto);

            if (response != null && (response.getErrorMsg() == null || response.getErrorMsg().isEmpty())) {
                log.info("✅ B2C account created successfully for: {}, Azure ID: {}",
                        user.getEmailId(), response.getId());

                // Save Azure user ID back (better than self-registration which doesn't do this)
                user.setAzureAdUserId(response.getId());
                authorizedUserRepository.save(user);

                return true;
            } else {
                log.error("❌ Azure B2C account creation failed for {}: {}",
                        user.getEmailId(), response != null ? response.getErrorMsg() : "null response");
                return false;
            }
        } catch (Exception e) {
            log.error("❌ Azure B2C account creation failed with exception for {}: {}",
                    user.getEmailId(), e.getMessage(), e);
            return false;
        }
    }

    private void sendRegistrationCredentialsEmail(String email, String investorName, String password) {
        GraphEmailService graphEmailService = graphEmailServiceProvider.getIfAvailable();
        if (graphEmailService != null) {
            try {
                boolean sent = graphEmailService.sendLoginDetailsEmail(
                        email, investorName, email, password, clientUrl);

                if (sent) {
                    log.info("📧 Registration credentials email sent to {}", email);
                } else {
                    log.warn("⚠️ Failed to send registration credentials email to {}", email);
                }
            } catch (Exception e) {
                log.error("❌ Failed to send registration credentials email to {}: {}", email, e.getMessage());
            }
        } else {
            log.warn("⚠️ Graph Email service not available. Login credentials: email={}, password={}", email, password);
        }
    }

    // ============================================
    // Helper Methods
    // ============================================

    private IntroducedRegistrationSession getSession(String uniqueCode) {
        return sessionRepository.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid registration code"));
    }

    private String generateUniqueCode() {
        String code = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + (1000 + RANDOM.nextInt(9000));
        while (sessionRepository.findByUniqueCode(code).isPresent()) {
            code = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                    + (1000 + RANDOM.nextInt(9000));
        }
        return code;
    }

    private String generateOtp() {
        return String.valueOf(1000 + RANDOM.nextInt(9000));
    }

    private String getServiceProviderTypeName(String type) {
        if (type == null)
            return "";
        return switch (type) {
            case "100000000" -> "Broker";
            case "100000001" -> "Bank";
            case "100000002" -> "Custodian";
            case "100000003" -> "Portfolio Manager";
            case "100000004" -> "Investment Advisor";
            default -> "Service Provider";
        };
    }

    /**
     * Parse nationality GUID to Integer ID from master_nationality table
     */
    private Integer parseNationality(String nationalityGuid) {
        if (nationalityGuid == null || nationalityGuid.isBlank()) {
            return null;
        }
        try {
            return nationalityRepository
                    .findBySsNationalityId(nationalityGuid)
                    .map(MasterNationality::getId)
                    .orElse(null);
        } catch (Exception e) {
            log.error("Failed to parse nationality GUID {}: {}", nationalityGuid, e.getMessage());
            return null;
        }
    }

    /**
     * Parse country of residence GUID to Integer ID from
     * master_country_of_residence table
     */
    private Integer parseCountryOfResidence(String countryGuid) {
        if (countryGuid == null || countryGuid.isBlank()) {
            return null;
        }
        try {
            return countryOfResidenceRepository
                    .findBySsCountryId(countryGuid)
                    .map(country -> country.getId().intValue())
                    .orElse(null);
        } catch (Exception e) {
            log.error("Failed to parse country of residence GUID {}: {}", countryGuid, e.getMessage());
            return null;
        }
    }

    /**
     * Save investor consents to investor_consents table for Consent Centre
     */
    private void saveInvestorConsents(String uniqueCode, IntroducedRegistrationSession session,
            Step4CompletionDto dto, Long investorId, Tenant tenant) {
        try {
            InvestorConsents consents = InvestorConsents.builder()
                    .investorUniqueId(uniqueCode)
                    .investorId(investorId)
                    .email(session.getEmail())
                    .userDob(session.getDateOfBirth())
                    .termsAccepted(dto.getTermsAccepted())
                    .privacyPolicyAccepted(dto.getPrivacyPolicyAccepted())
                    .whatsappConsent(dto.getAgreeForWhatsapp())
                    .marketingConsent(dto.getAgreeForMarketing())
                    .informationCorrectConsent(dto.getTermsAccepted())
                    .legalCapacityConsent(true) // Implied by registration completion
                    .consentDate(LocalDateTime.now())
                    .consentVersion("1.0")
                    .build();
            consents.setTenant(tenant);
            investorConsentsRepository.save(consents);
            log.info("✅ Investor consents saved to investor_consents table for uniqueCode: {}", uniqueCode);
        } catch (Exception e) {
            log.error("❌ Failed to save investor consents for {}: {}", uniqueCode, e.getMessage());
        }
    }
}
