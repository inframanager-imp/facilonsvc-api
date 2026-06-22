package com.facilon.app.module.client.service;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.integration.dynamics.DynamicsCrmService;
import com.facilon.app.integration.laravel.LaravelCryptPayloadDecryptor;
import com.facilon.app.integration.graphemail.GraphEmailService;
import com.facilon.app.module.serviceprovider.service.PdfGeneratorService;
import com.facilon.app.util.EmailTemplateLoader;
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
    private static final String CONSENT_VERSION = "1.0"; // Privacy Notice version (matches Laravel consentVersion)
    private static final String TOU_CONSENT_VERSION = "V01"; // SR TOU consent version (matches Laravel default)

    private final DynamicsCrmService dynamicsCrmService;
    private final LaravelCryptPayloadDecryptor laravelCryptPayloadDecryptor;
    private final IntroInvestorTempRepository introInvestorTempRepository;
    private final AuthorizedUserRepository authorizedUserRepository;
    private final InvestorRepository investorRepository;
    private final OtpVerificationRepository otpVerificationRepository;
    private final IntroducedRegistrationSessionRepository sessionRepository;
    private final MasterNationalityRepository nationalityRepository;
    private final MasterCountryOfResidenceRepository countryOfResidenceRepository;
    private final IsdCodeValuesRepository isdCodeValuesRepository;
    private final EmailTemplateLoader emailTemplateLoader;
    private final InvestorTouConsentRepository investorTouConsentRepository;
    private final PdfGeneratorService pdfGeneratorService;
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
        String ssBrokeragePlanValue = (String) dataverseInvestor.get("_ss_brokerageplan_value");
        String ssSchemeValue = (String) dataverseInvestor.get("_ss_scheme_value");
        String ssCountryOfResidenceValue = (String) dataverseInvestor.get("_ss_countryofresidence_value");
        // ISD/country-dialing code GUID (Laravel: isd_code ← _ss_countryofresidenceisdcode_value).
        String ssCountryOfResidenceIsdCode = (String) dataverseInvestor.get("_ss_countryofresidenceisdcode_value");

        // Diagnostic: confirm what Dataverse returned for mobile + ISD, and how the dial code resolves.
        log.info("📞 Dataverse intro fields for {}: mobile(ss_mobilephoneintroduceind)='{}', isdGuid='{}', resolvedDialCode='{}', email='{}', firstName='{}'",
                dataverseInvestorId,
                dataverseInvestor.get("ss_mobilephoneintroduceind"),
                ssCountryOfResidenceIsdCode,
                resolveDialCode(ssCountryOfResidenceIsdCode),
                introEmail, introFirstName);

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
        introInvestor.setIsdCode(ssCountryOfResidenceIsdCode);
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
                .isdCode(ssCountryOfResidenceIsdCode)
                .countryCode(resolveDialCode(ssCountryOfResidenceIsdCode))
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

        // Send "Confirmation of Privacy Consent – Facilon" email (mirrors Laravel
        // introduce_investor_register_step1_insert_data → consent-confirmation-mail).
        sendConsentConfirmationEmail(introInvestor);

        return ApiResponseDto.builder()
                .success(true)
                .message("Consent recorded successfully")
                .uniqueCode(uniqueCode)
                .nextStep("step1")
                .build();
    }

    /**
     * Sends the "Confirmation of Privacy Consent – Facilon" email — identical subject and
     * content to Laravel's {@code investor.consent-confirmation-mail} blade.
     * Non-fatal: consent is already persisted, so an email failure must not break the flow.
     */
    private void sendConsentConfirmationEmail(IntroInvestorTemp introInvestor) {
        try {
            GraphEmailService graphEmailService = graphEmailServiceProvider.getIfAvailable();
            if (graphEmailService == null) {
                log.warn("[consent-email] GraphEmailService unavailable; skipping consent confirmation email");
                return;
            }
            String email = introInvestor.getIntroEmail();
            if (email == null || email.isBlank()) {
                log.warn("[consent-email] No intro email; skipping consent confirmation email");
                return;
            }
            String firstName = introInvestor.getIntroFirstName() != null ? introInvestor.getIntroFirstName() : "";
            String lastName = introInvestor.getIntroLastName() != null ? introInvestor.getIntroLastName() : "";
            String name = (firstName + " " + lastName).trim();
            String datetime = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
            String privacyUrl = (clientUrl != null && !clientUrl.isEmpty() ? clientUrl : "http://localhost:3000")
                    + "/privacy-policy";

            String html = emailTemplateLoader.processTemplate("30-consent-confirmation.html", Map.of(
                    "name", name,
                    "version", CONSENT_VERSION,
                    "datetime", datetime,
                    "privacyUrl", privacyUrl));

            boolean sent = graphEmailService.sendEmail(
                    email, "Confirmation of Privacy Consent – Facilon", html, firstName, lastName);
            log.info("[consent-email] Confirmation of Privacy Consent email sent={} to {}", sent, email);
        } catch (Exception e) {
            log.error("[consent-email] Failed to send consent confirmation email: {}", e.getMessage(), e);
        }
    }

    /**
     * Persists the SR TOU consent and emails the executed Terms of Use PDF.
     * Mirrors Laravel: insert into {@code investor_tou_consents} + render {@code sr_tou} PDF +
     * send {@code tou-consent-confirmation-mail} with subject "SR TOU Executed Successfully".
     * Non-fatal: registration is already complete, so a failure here must not break the flow.
     */
    private void sendSrTouConsentAndEmail(IntroducedRegistrationSession session, Tenant tenant) {
        try {
            String firstName = session.getFirstName() != null ? session.getFirstName() : "";
            String lastName = session.getLastName() != null ? session.getLastName() : "";

            // 1. Persist ToU consent (audit record)
            InvestorTouConsent touConsent = InvestorTouConsent.builder()
                    .consentVersion(TOU_CONSENT_VERSION)
                    .consentedAt(LocalDateTime.now())
                    .email(session.getEmail())
                    .firstName(firstName)
                    .middleName(session.getMiddleName())
                    .lastName(lastName)
                    .mobileNumber(session.getMobileNumber())
                    .uniqueCode(session.getUniqueCode())
                    .ipAddress(currentRequestValue(true))
                    .userAgent(currentRequestValue(false))
                    .build();
            touConsent.setTenant(tenant);
            investorTouConsentRepository.save(touConsent);

            // 2. Email the executed SR TOU PDF
            GraphEmailService graphEmailService = graphEmailServiceProvider.getIfAvailable();
            if (graphEmailService == null) {
                log.warn("[sr-tou] GraphEmailService unavailable; consent saved but email skipped");
                return;
            }
            String email = session.getEmail();
            if (email == null || email.isBlank()) {
                log.warn("[sr-tou] No email on session; consent saved but email skipped");
                return;
            }

            String fullName = (firstName + " " + lastName).trim();
            String consentedAt = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Kolkata"))
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")) + " IST (UTC +05:30)";

            String emailHtml = emailTemplateLoader.processTemplate("31-tou-consent-confirmation.html", Map.of(
                    "full_name", fullName,
                    "consented_at", consentedAt,
                    "consent_version", TOU_CONSENT_VERSION));

            byte[] pdfBytes = pdfGeneratorService.htmlToPdf(loadSrTouHtml());

            boolean sent = graphEmailService.sendEmailWithPdfBytes(
                    email, "SR TOU Executed Successfully", emailHtml, "SR-TOU.pdf", pdfBytes);
            log.info("[sr-tou] SR TOU Executed email sent={} to {}", sent, email);
        } catch (Exception e) {
            log.error("[sr-tou] Failed to record/send SR TOU consent: {}", e.getMessage(), e);
        }
    }

    /** Loads the static SR TOU document (well-formed XHTML) from the classpath for PDF rendering. */
    private String loadSrTouHtml() throws java.io.IOException {
        org.springframework.core.io.ClassPathResource resource =
                new org.springframework.core.io.ClassPathResource("templates/pdf/sr-tou.html");
        try (java.io.InputStream in = resource.getInputStream()) {
            return new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    /** Best-effort capture of the current request's IP ({@code ip=true}) or User-Agent ({@code ip=false}). */
    private String currentRequestValue(boolean ip) {
        try {
            org.springframework.web.context.request.ServletRequestAttributes attrs =
                    (org.springframework.web.context.request.ServletRequestAttributes)
                            org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            if (attrs == null) return null;
            return ip ? attrs.getRequest().getRemoteAddr() : attrs.getRequest().getHeader("User-Agent");
        } catch (Exception e) {
            return null;
        }
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
                .isdCode(introInvestor != null ? introInvestor.getIsdCode() : null)
                .countryCode(introInvestor != null ? resolveDialCode(introInvestor.getIsdCode()) : null)
                .build();
    }

    /**
     * Resolve a Dataverse country-of-residence ISD GUID to a dialing code (e.g. "+65").
     *
     * <p>Chain: {@code _ss_countryofresidenceisdcode_value} GUID →
     * {@code master_country_of_residence.ss_countryid} → country name →
     * {@code isd_code_values.country_name} → {@code "+" + code_value}.
     *
     * <p>We resolve the dial number via {@code isd_code_values} (not
     * {@code master_country_of_residence.ss_isdcode}, which the master sync fills with a
     * GUID rather than the dial digits), so the returned value matches the dropdown options
     * the frontend builds from the same {@code isd_code_values} list.
     */
    private String resolveDialCode(String isdGuid) {
        if (isdGuid == null || isdGuid.isBlank()) {
            return null;
        }
        return countryOfResidenceRepository.findBySsCountryId(isdGuid.trim())
                .map(MasterCountryOfResidence::getSsName)
                .filter(name -> name != null && !name.isBlank())
                .flatMap(name -> isdCodeValuesRepository.findFirstByCountryNameIgnoreCase(name.trim()))
                .map(isd -> isd.getCodeValue())
                .filter(code -> code != null)
                .map(code -> "+" + code)
                .orElse(null);
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
        Integer nationalityId = parseNationality(dto.getNationality());
        Integer residenceId = parseCountryOfResidence(dto.getCountryOfResidence());
        Integer registerAs = "Self".equals(dto.getSelfOrLegalEntity()) ? 1 : 2;
        String investorType = determineInvestorTypeName(
                registerAs, nationalityId, residenceId,
                dto.getPanCardStatus(), dto.getIndianOrigin(), dto.getOciCardStatus());
        if (investorType == null) {
            // Rule-based derivation bailed (nationality/residence GUID not in the local master
            // tables). Fall back to the Dataverse-assigned investor-type GUID the broker/PM set
            // in CRM, so investor_type is still captured on the row.
            investorType = canonicalInvestorTypeFromGuid(introInvestor.getSsInvestorTypeValue());
            if (investorType != null) {
                log.info("[introducedRegistration] investor_type derived from Dataverse type GUID {} → {}",
                        introInvestor.getSsInvestorTypeValue(), investorType);
            } else {
                log.warn("[introducedRegistration] Could not capture investor_type for uniqueCode={} "
                        + "(nationality GUID {} unresolved, type GUID {} unresolved)",
                        dto.getUniqueCode(), dto.getNationality(), introInvestor.getSsInvestorTypeValue());
            }
        }

        Investor investor = Investor.builder()
                .authorizedUser(user)
                .uniqueCode(dto.getUniqueCode())
                .registerAs(registerAs)
                .market(1)
                .nationality(nationalityId)
                .pancardStatus(dto.getPanCardStatus())
                .indianOrigin(dto.getIndianOrigin())
                .ociCardStatus(dto.getOciCardStatus())
                .termsRead(dto.getTermsAccepted() ? 1 : 0)
                .confirmation(dto.getTermsAccepted() ? 1 : 0)
                .verifyStatus(2)
                .dvInvestorSsId(session.getDataverseInvestorGuid())
                .dvContactId(contactId)
                .countryOfResidence(residenceId)
                .whatsappConsent(dto.getAgreeForWhatsapp() != null && dto.getAgreeForWhatsapp() ? 1 : 0)
                .entityName(dto.getEntityName())
                .entityNameRepresentative(
                        dto.getEntityName() != null ? session.getFirstName() + " " + session.getLastName() : null)
                .companyCapacity(dto.getRepresentativeCapacity())
                .securityRegulated(dto.getSecurityRegulated())
                .investorType(investorType)
                .build();
        investor.setTenant(tenant);
        investor = investorRepository.save(investor);

        // Save investor consents to investor_consents table
        saveInvestorConsents(dto.getUniqueCode(), session, dto, investor.getId(), tenant);

        // Update intro_investor_temp with unique_code
        introInvestor.setUniqueCodeDb(dto.getUniqueCode());
        introInvestorTempRepository.save(introInvestor);

        // **CRITICAL: Create B2C Account**
        String azureUserId = createB2CAccount(user, password,
                session.getFirstName() + " " + session.getLastName());

        // FISP-style: email the setpassword link rather than the temp password itself.
        boolean b2cAccountCreated = azureUserId != null;
        if (b2cAccountCreated) {
            sendRegistrationCredentialsEmail(session.getEmail(),
                    session.getFirstName() + " " + session.getLastName(),
                    buildSetPasswordUrl(azureUserId));
        } else {
            log.warn("⚠️ Setpassword email NOT sent because B2C account creation failed");
        }

        // Record SR TOU consent + email the executed Terms of Use PDF
        // (mirrors Laravel introduce_investor_register_step4_insert_data → investor_tou_consents + sr_tou PDF).
        sendSrTouConsentAndEmail(session, tenant);

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
     * Create B2C account - aligned with PublicRegistrationService.createAzureUser().
     * Returns the Azure object id on success, or {@code null} on failure. Callers use the
     * returned GUID to build the FISP-style setpassword email link.
     */
    private String createB2CAccount(AuthorizedUser user, String password, String displayName) {
        UserMgmtApiClient userMgmtClient = userMgmtClientProvider.getIfAvailable();
        if (userMgmtClient == null) {
            log.error("❌ User management service not available. B2C account NOT created for: {}", user.getEmailId());
            return null;
        }

        try {
            TenantB2CConfig b2cConfig = tenantB2CConfigService.getConfigForCurrentTenantOrDefault();
            if (b2cConfig == null) {
                log.error("❌ Azure B2C configuration not found. B2C account NOT created.");
                return null;
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

            if (response != null
                    && response.getId() != null && !response.getId().isBlank()
                    && (response.getErrorMsg() == null || response.getErrorMsg().isEmpty())) {
                String azureUserId = response.getId();
                log.info("✅ B2C account created successfully for: {}, Azure ID: {}",
                        user.getEmailId(), azureUserId);

                user.setAzureAdUserId(azureUserId);
                authorizedUserRepository.save(user);

                return azureUserId;
            } else {
                String reason = response == null
                        ? "null response"
                        : (response.getErrorMsg() != null && !response.getErrorMsg().isEmpty()
                                ? response.getErrorMsg()
                                : "no Azure id in response");
                log.error("❌ Azure B2C account creation failed for {}: {}", user.getEmailId(), reason);
                return null;
            }
        } catch (Exception e) {
            log.error("❌ Azure B2C account creation failed with exception for {}: {}",
                    user.getEmailId(), e.getMessage(), e);
            return null;
        }
    }

    private void sendRegistrationCredentialsEmail(String email, String investorName, String setPasswordUrl) {
        GraphEmailService graphEmailService = graphEmailServiceProvider.getIfAvailable();
        if (graphEmailService != null) {
            try {
                boolean sent = graphEmailService.sendLoginDetailsEmail(
                        email, investorName, email, setPasswordUrl, clientUrl);

                if (sent) {
                    log.info("📧 Setpassword email sent to {}", email);
                } else {
                    log.warn("⚠️ Failed to send setpassword email to {}", email);
                }
            } catch (Exception e) {
                log.error("❌ Failed to send setpassword email to {}: {}", email, e.getMessage());
            }
        } else {
            log.warn("⚠️ Graph Email service not available. Setpassword URL would have been: {}", setPasswordUrl);
        }
    }

    private String buildSetPasswordUrl(String azureUserId) {
        String base = clientUrl != null && !clientUrl.isEmpty() ? clientUrl : "http://localhost:3000";
        String trimmed = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        return trimmed + "/investor/setpassword/" + azureUserId;
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
     * Determine the investor_type enum string from the step-4 form inputs so
     * the introduced-investor row is populated at registration completion
     * (matching what the regular self-register flow does). Laravel's
     * introduced flow prepared but never wrote this value; the later
     * ClientOnboardingService.determineInvestorType(...) call re-derives the
     * same value from the same columns, so setting it here is safe and
     * consistent.
     *
     * Returns null when we cannot confidently pick a type (e.g. nationality
     * not resolved) — keeping behaviour as before in that edge case so the
     * onboarding step can fill it in later.
     */
    private String determineInvestorTypeName(Integer registerAs, Integer nationalityId,
                                             Integer countryOfResidenceId,
                                             String pancardStatus, String indianOrigin,
                                             String ociCardStatus) {
        if (registerAs == null) return null;
        boolean isIndividual = registerAs == 1;
        boolean isIndiaNationality = isIndiaNationalityRef(nationalityId);
        boolean isIndiaResidence = isIndiaResidenceRef(countryOfResidenceId);
        boolean isIndiaIncorporation = !isIndividual && isIndiaResidence;
        boolean hasPan = "yes".equalsIgnoreCase(pancardStatus);
        boolean hasOci = "yes".equalsIgnoreCase(ociCardStatus);
        Boolean isPio = indianOrigin == null ? null : "yes".equalsIgnoreCase(indianOrigin);
        if (isIndividual && nationalityId == null) return null;

        try {
            return com.facilon.app.module.client.model.InvestorType.determineType(
                    isIndividual, isIndiaNationality, isIndiaIncorporation,
                    isIndiaResidence, hasPan, isPio, hasOci).name();
        } catch (Exception e) {
            log.warn("determineInvestorTypeName failed: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Resolves the Dataverse investor-type GUID (intro_investor_temp.ss_investortype_value) to a
     * canonical {@link com.facilon.app.module.client.model.InvestorType} enum constant, via
     * master_investor_types.ss_name matched against the enum's name/display name. Returns null when
     * the GUID can't be resolved or doesn't map to a known type (so we store the enum or nothing —
     * never a non-enum string).
     */
    private String canonicalInvestorTypeFromGuid(String typeGuid) {
        if (typeGuid == null || typeGuid.isBlank() || dynamicsCrmService == null) {
            return null;
        }
        String ssName = dynamicsCrmService.fetchInvestorTypeName(typeGuid.trim());
        if (ssName == null || ssName.isBlank()) {
            return null;
        }
        for (com.facilon.app.module.client.model.InvestorType t
                : com.facilon.app.module.client.model.InvestorType.values()) {
            if (t.name().equalsIgnoreCase(ssName.trim()) || t.getDisplayName().equalsIgnoreCase(ssName.trim())) {
                return t.name();
            }
        }
        return null;
    }

    /**
     * India matches the legacy IDs used by ClientOnboardingService.isIndiaNationality —
     * 1 (older seed) and 240 (current seed). Centralised here so the
     * introduced-flow type inference uses the same rule as the onboarding
     * re-evaluation downstream.
     */
    /**
     * Whether the given nationality reference is India.
     *
     * <p>In this flow {@code parseNationality} resolves a Dataverse GUID to
     * {@code master_nationality.id} (the business {@code id} column, e.g. 245 for India),
     * so we resolve that row and test a STABLE attribute (ISO {@code IN} / name {@code Indian}).
     * The old {@code id == 1 || 240} hardcode was wrong (India's id is 245, never 1/240) and
     * unstable across master re-syncs.
     */
    private boolean isIndiaNationalityRef(Integer nationalityId) {
        if (nationalityId == null) {
            return false;
        }
        return nationalityRepository.findByIdColumn(nationalityId)
                .map(n -> isIndiaText(n.getSsNationality()) || isIndiaText(n.getSsName()))
                .orElse(false);
    }

    /**
     * Whether the given country-of-residence reference is India.
     *
     * <p>{@code parseCountryOfResidence} resolves a Dataverse GUID to
     * {@code master_country_of_residence.id} (the JPA primary key), so we look that row up
     * and test its country name.
     */
    private boolean isIndiaResidenceRef(Integer countryOfResidenceId) {
        if (countryOfResidenceId == null) {
            return false;
        }
        return countryOfResidenceRepository.findById(countryOfResidenceId.longValue())
                .map(c -> isIndiaText(c.getSsName()))
                .orElse(false);
    }

    /** True when the text is India's ISO code ("IN") or India/Indian by name. */
    private static boolean isIndiaText(String value) {
        if (value == null) {
            return false;
        }
        String v = value.trim();
        return v.equalsIgnoreCase("IN") || v.equalsIgnoreCase("India") || v.equalsIgnoreCase("Indian");
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
