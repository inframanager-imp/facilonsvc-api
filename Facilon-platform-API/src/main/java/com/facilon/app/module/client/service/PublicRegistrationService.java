package com.facilon.app.module.client.service;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.integration.graphemail.GraphEmailService;
import com.facilon.app.integration.usermgmt.UserMgmtApiClient;
import com.facilon.app.integration.usermgmt.dto.MicrosoftGraphResponseDto;
import com.facilon.app.integration.usermgmt.dto.SignUpDto;
import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.model.Tenant;
import com.facilon.app.model.TenantB2CConfig;
import com.facilon.app.service.TenantB2CConfigService;
import com.facilon.app.module.client.dto.onboarding.*;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.model.InvestorConsents;
import com.facilon.app.module.client.model.InvestorType;
import com.facilon.app.module.client.model.OtpVerification;
import com.facilon.app.module.client.model.RegistrationSession;
import com.facilon.app.module.client.model.UserPersonalInformation;
import com.facilon.app.module.client.repository.InvestorConsentsRepository;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.module.client.repository.OtpVerificationRepository;
import com.facilon.app.module.client.repository.RegistrationSessionRepository;
import com.facilon.app.module.client.repository.UserPersonalInformationRepository;
import com.facilon.app.repository.AuthorizedUserRepository;
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
import java.util.Optional;
import java.util.Random;

/**
 * Public Registration Service for Investor Self-Registration.
 * Handles the new 3-step registration flow:
 * 1. Email submission & OTP verification with consent
 * 2. Individual details (if registerAs=1)
 * 3. Legal Entity details (if registerAs=2)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PublicRegistrationService {

    private static final Random RANDOM = new Random();
    private static final int OTP_EXPIRY_MINUTES = 15;
    private static final int SESSION_EXPIRY_HOURS = 24;

    private final RegistrationSessionRepository sessionRepository;
    private final OtpVerificationRepository otpRepository;
    private final AuthorizedUserRepository userRepository;
    private final InvestorRepository investorRepository;
    private final UserPersonalInformationRepository personalInfoRepository;
    private final InvestorConsentsRepository investorConsentsRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<GraphEmailService> graphEmailServiceProvider;
    private final ObjectProvider<UserMgmtApiClient> userMgmtClientProvider;
    private final TenantB2CConfigService tenantB2CConfigService;

    @Value("${investor.onboarding.default-password:TempPass123!}")
    private String defaultTempPassword;
    
    @Value("${app.client_url:http://localhost:3000}")
    private String clientUrl;

    /**
     * Step 1a: Initiate registration with email.
     */
    public EmailRegistrationResponseDto initiateRegistration(EmailRegistrationDto dto) {
        Tenant tenant = TenantContextHolder.getContext().getTenant();
        String email = dto.getEmail().trim().toLowerCase();

        // Check if email already registered
        Optional<AuthorizedUser> existingUser = userRepository.findByEmailId(email);
        if (existingUser.isPresent()) {
            log.info("Email already registered: {}", email);
            return EmailRegistrationResponseDto.builder()
                    .success(false)
                    .message("You are already signed up. Please use Sign In to access your account")
                    .emailAlreadyExists(true)
                    .otpSent(false)
                    .build();
        }

        // Check for incomplete registration session
        Optional<RegistrationSession> existingSession = 
            sessionRepository.findByEmailAndRegistrationCompletedFalse(email);
        
        String uniqueCode;
        RegistrationSession session;

        if (existingSession.isPresent()) {
            session = existingSession.get();
            uniqueCode = session.getUniqueCode();
            session.setRegisterAs(dto.getRegisterAs());
            session.setExpiresAt(LocalDateTime.now().plusHours(SESSION_EXPIRY_HOURS));
            session.setCurrentStep(1);
            log.info("Resuming registration session for email: {}, uniqueCode: {}", email, uniqueCode);
        } else {
            uniqueCode = generateUniqueCode();
            session = RegistrationSession.builder()
                    .uniqueCode(uniqueCode)
                    .email(email)
                    .registerAs(dto.getRegisterAs())
                    .currentStep(1)
                    .otpVerified(false)
                    .consentGiven(false)
                    .registrationCompleted(false)
                    .expiresAt(LocalDateTime.now().plusHours(SESSION_EXPIRY_HOURS))
                    .build();
            session.setTenant(tenant);
            log.info("Created new registration session for email: {}, uniqueCode: {}", email, uniqueCode);
        }

        session = sessionRepository.save(session);

        // Generate and store OTP
        String emailOtp = generateOtp();
        storeOtp(uniqueCode, emailOtp);

        // Send OTP email via Graph Email Service
        GraphEmailService graphEmailService = graphEmailServiceProvider.getIfAvailable();
        boolean otpSent = false;
        if (graphEmailService != null) {
            try {
                otpSent = graphEmailService.sendOtpEmail(email, "", emailOtp);
                if (otpSent) {
                    log.info("OTP sent to email: {}", email);
                } else {
                    log.warn("Failed to send OTP email to {}", email);
                }
            } catch (Exception e) {
                log.error("Failed to send OTP email to {}: {}", email, e.getMessage());
            }
        } else {
            log.warn("Graph Email service not available, cannot send OTP. Check graph.email.enabled=true and database config.");
        }

        return EmailRegistrationResponseDto.builder()
                .success(true)
                .message("An email with a One-Time Password has been sent to your email address provided above. (Check your spam or junk folder for the email in case you are unable to locate the email)")
                .uniqueCode(uniqueCode)
                .emailAlreadyExists(false)
                .otpSent(otpSent)
                .expiresInMinutes(OTP_EXPIRY_MINUTES)
                .build();
    }

    /**
     * Step 1b: Verify OTP and record consent.
     */
    public OtpVerificationResponseDto verifyOtpAndConsent(OtpConsentVerificationDto dto) {
        String uniqueCode = dto.getUniqueCode();
        
        // Find registration session
        RegistrationSession session = sessionRepository.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid registration code"));

        // Check if session expired
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Registration session has expired. Please start again.");
        }

        // Verify OTP
        Optional<OtpVerification> otpOpt = otpRepository.findByUniqueCode(uniqueCode);
        if (otpOpt.isEmpty()) {
            throw new IllegalArgumentException("OTP not found. Please request a new OTP.");
        }

        OtpVerification otpVerification = otpOpt.get();
        if (otpVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP has expired. Please request a new OTP.");
        }

        if (!otpVerification.getEmailOtp().equals(dto.getEmailOtp())) {
            throw new IllegalArgumentException("Invalid OTP. Please check and try again.");
        }

        // Mark OTP as verified
        otpVerification.setVerified(true);
        otpRepository.save(otpVerification);

        // Record consent
        session.setOtpVerified(true);
        session.setConsentGiven(dto.getConsentGiven());
        session.setCurrentStep(2);
        session = sessionRepository.save(session);

        log.info("OTP verified and consent recorded for uniqueCode: {}", uniqueCode);

        return OtpVerificationResponseDto.builder()
                .success(true)
                .message("Email verified successfully! Please proceed to complete your registration.")
                .uniqueCode(uniqueCode)
                .registerAs(session.getRegisterAs())
                .build();
    }

    /**
     * Resend OTP.
     */
    public OtpResponseDto resendOtp(String uniqueCode) {
        RegistrationSession session = sessionRepository.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid registration code"));

        if (session.getOtpVerified()) {
            throw new IllegalArgumentException("OTP already verified");
        }

        // Generate new OTP
        String emailOtp = generateOtp();
        storeOtp(uniqueCode, emailOtp);

        // Send OTP email via Graph Email Service
        GraphEmailService graphEmailService = graphEmailServiceProvider.getIfAvailable();
        boolean sent = false;
        if (graphEmailService != null) {
            try {
                sent = graphEmailService.sendOtpEmail(session.getEmail(), "", emailOtp);
                if (sent) {
                    log.info("OTP resent to email: {}", session.getEmail());
                } else {
                    log.warn("Failed to resend OTP to {}", session.getEmail());
                }
            } catch (Exception e) {
                log.error("Failed to resend OTP: {}", e.getMessage());
            }
        }

        return OtpResponseDto.builder()
                .message("OTP has been resent to your email")
                .emailSent(sent)
                .smsSent(false)
                .expiresInMinutes(OTP_EXPIRY_MINUTES)
                .build();
    }

    /**
     * Step 2: Complete Individual Investor registration.
     */
    public RegistrationCompletionResponseDto completeIndividualRegistration(
            String uniqueCode, IndividualRegistrationDto dto) {
        
        // Find and validate session
        RegistrationSession session = sessionRepository.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid registration code"));

        if (!session.getOtpVerified()) {
            throw new IllegalArgumentException("Please verify your email first");
        }

        if (session.getRegisterAs() != 1) {
            throw new IllegalArgumentException("This endpoint is for Individual registration only");
        }

        if (session.getRegistrationCompleted()) {
            throw new IllegalArgumentException("Registration already completed");
        }

        Tenant tenant = TenantContextHolder.getContext().getTenant();

        // Check if not interested in Indian market
        if (!dto.getInterestedInIndianMarket()) {
            session.setRegistrationCompleted(true);
            session.setCurrentStep(3);
            sessionRepository.save(session);
            
            log.info("Individual not interested in Indian market, registration session closed");
            return RegistrationCompletionResponseDto.builder()
                    .success(true)
                    .message("Thank you for your interest in Facilon. We will notify you when we add other markets.")
                    .uniqueCode(uniqueCode)
                    .requiresPanCard(false)
                    .build();
        }

        // Check if PAN card is required but not available
        if (!dto.getHasPanCard()) {
            session.setRegistrationCompleted(true);
            session.setCurrentStep(3);
            sessionRepository.save(session);

            log.info("Individual doesn't have PAN card, registration completed with notification");
            return RegistrationCompletionResponseDto.builder()
                    .success(true)
                    .message("It is important for you to have a PAN card before appointing any service provider. We will contact you by email and provide you the process for applying for PAN card")
                    .uniqueCode(uniqueCode)
                    .email(session.getEmail())
                    .requiresPanCard(true)
                    .build();
        }

        // Create authorized user
        String password = defaultTempPassword;
        String fullName = dto.getFirstName() + 
                         (dto.getMiddleName() != null && !dto.getMiddleName().isBlank() ? " " + dto.getMiddleName() : "") + 
                         " " + dto.getLastName();
        AuthorizedUser user = AuthorizedUser.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .emailId(session.getEmail())
                .password(passwordEncoder.encode(password))
                .loginId(session.getEmail())
                .isActive(true)
                .mustChangePassword(true)
                .lastPasswordChange(LocalDateTime.now())
                .passwordChangeStatus(false)
                .failedLogins(0)
                .build();
        user.setTenant(tenant);
        user = userRepository.save(user);

        // Determine investor type based on nationality and residency
        String investorType = determineInvestorType(dto);

        // Create investor record
        Investor investor = Investor.builder()
                .authorizedUser(user)
                .uniqueCode(uniqueCode)
                .registerAs(1) // Individual
                .market(1) // Indian Securities Market
                .nationality(dto.getNationality())
                .countryOfResidence(dto.getCountryOfResidence())
                .residenceType(dto.getResidencyType())
                .pancardStatus(dto.getHasPanCard() ? "yes" : "no")
                .indianOrigin(dto.getIsPersonOfIndianOrigin() != null ? 
                    (dto.getIsPersonOfIndianOrigin() ? "yes" : "no") : null)
                .ociCardStatus(dto.getHasOciCard() != null ? 
                    (dto.getHasOciCard() ? "yes" : "no") : null)
                .termsRead(dto.getTermsAccepted() ? 1 : 0)
                .confirmation(dto.getTermsAccepted() ? 1 : 0)
                .investorType(investorType)
                .verifyStatus(2) // Pending verification
                .build();
        investor.setTenant(tenant);
        investor = investorRepository.save(investor);

        // Create personal information record
        UserPersonalInformation personalInfo = UserPersonalInformation.builder()
                .investorUniqueId(uniqueCode)
                .investorId(String.valueOf(investor.getId()))
                .title(dto.getTitle())
                .investorFirstName(dto.getFirstName())
                .investorMiddleName(dto.getMiddleName())
                .investorLastName(dto.getLastName())
                .userDob(dto.getDateOfBirth())
                .gender(dto.getGender())
                .investorGender(dto.getGender())
                .build();
        personalInfo.setTenant(tenant);
        personalInfoRepository.save(personalInfo);

        // Save investor consents for Consent Centre
        saveInvestorConsents(uniqueCode, session, dto.getTermsAccepted(), dto.getDateOfBirth(), tenant);

        // Create user in Azure B2C
        boolean azureUserCreated = createAzureUser(user, password);

        // Send login details email with temporary password ONLY if Azure user created successfully
        if (azureUserCreated) {
            sendLoginDetailsEmail(session.getEmail(), fullName, user.getLoginId(), password);
        } else {
            log.warn("Login details email NOT sent for {} because Azure user creation failed", session.getEmail());
        }

        // Mark session as completed
        session.setRegistrationCompleted(true);
        session.setCurrentStep(3);
        sessionRepository.save(session);

        log.info("Individual registration completed successfully for uniqueCode: {}", uniqueCode);

        return RegistrationCompletionResponseDto.builder()
                .success(true)
                .message("Registration completed successfully! Login credentials have been sent to your email.")
                .uniqueCode(uniqueCode)
                .investorId(investor.getId())
                .email(session.getEmail())
                .requiresPanCard(false)
                .build();
    }

    /**
     * Step 3: Complete Legal Entity registration.
     */
    public RegistrationCompletionResponseDto completeLegalEntityRegistration(
            String uniqueCode, LegalEntityRegistrationDto dto) {
        
        // Find and validate session
        RegistrationSession session = sessionRepository.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid registration code"));

        if (!session.getOtpVerified()) {
            throw new IllegalArgumentException("Please verify your email first");
        }

        if (session.getRegisterAs() != 2) {
            throw new IllegalArgumentException("This endpoint is for Legal Entity registration only");
        }

        if (session.getRegistrationCompleted()) {
            throw new IllegalArgumentException("Registration already completed");
        }

        Tenant tenant = TenantContextHolder.getContext().getTenant();

        // Check if India and no PAN card
        final int INDIA_COUNTRY_ID = 1; // Adjust based on your master_country table
        if (dto.getCountryOfIncorporation().equals(INDIA_COUNTRY_ID) && 
            (dto.getHasPanCard() == null || !dto.getHasPanCard())) {
            
            throw new IllegalArgumentException("PAN card is required for entities incorporated in India");
        }

        // Create authorized user (representative)
        // Split representative name into first and last name
        String repName = dto.getEntityRepresentativeName().trim();
        String[] nameParts = repName.split("\\s+");
        String repFirstName = nameParts[0];
        String repLastName = nameParts.length > 1 ? nameParts[nameParts.length - 1] : "";
        
        String password = defaultTempPassword;
        AuthorizedUser user = AuthorizedUser.builder()
                .firstName(repFirstName)
                .lastName(repLastName)
                .emailId(session.getEmail())
                .password(passwordEncoder.encode(password))
                .loginId(session.getEmail())
                .isActive(true)
                .mustChangePassword(true)
                .lastPasswordChange(LocalDateTime.now())
                .passwordChangeStatus(false)
                .failedLogins(0)
                .build();
        user.setTenant(tenant);
        user = userRepository.save(user);

        // Create investor record
        Investor investor = Investor.builder()
                .authorizedUser(user)
                .uniqueCode(uniqueCode)
                .registerAs(2) // Legal Entity
                .market(1) // Indian Securities Market (as per PDF)
                .entityName(dto.getEntityName())
                .incorpCountry(dto.getCountryOfIncorporation())
                .pancardStatus(dto.getHasPanCard() != null && dto.getHasPanCard() ? "yes" : "no")
                .entityNameRepresentative(dto.getEntityRepresentativeName())
                .companyCapacity(dto.getRepresentativeCapacity())
                .securityRegulated(dto.getIsSecuritiesRegulated() != null && dto.getIsSecuritiesRegulated() ? "yes" : "no")
                .termsRead(dto.getTermsAccepted() ? 1 : 0)
                .confirmation(dto.getTermsAccepted() ? 1 : 0)
                .investorType("LEGAL_ENTITY")
                .verifyStatus(2) // Pending verification
                .build();
        investor.setTenant(tenant);
        investor = investorRepository.save(investor);

        // Create personal information for representative
        UserPersonalInformation personalInfo = UserPersonalInformation.builder()
                .investorUniqueId(uniqueCode)
                .investorId(String.valueOf(investor.getId()))
                .investorFirstName(repFirstName)
                .investorLastName(repLastName)
                .build();
        personalInfo.setTenant(tenant);
        personalInfoRepository.save(personalInfo);

        // Save investor consents for Consent Centre
        saveInvestorConsents(uniqueCode, session, dto.getTermsAccepted(), null, tenant);

        // Create user in Azure B2C
        boolean azureUserCreated = createAzureUser(user, password);

        // Send login details email with temporary password ONLY if Azure user created successfully
        if (azureUserCreated) {
            sendLoginDetailsEmail(session.getEmail(), dto.getEntityRepresentativeName(), user.getLoginId(), password);
        } else {
            log.warn("Login details email NOT sent for {} because Azure user creation failed", session.getEmail());
        }

        // Mark session as completed
        session.setRegistrationCompleted(true);
        session.setCurrentStep(3);
        sessionRepository.save(session);

        log.info("Legal entity registration completed successfully for uniqueCode: {}", uniqueCode);

        return RegistrationCompletionResponseDto.builder()
                .success(true)
                .message("Registration completed successfully! Login credentials have been sent to your email.")
                .uniqueCode(uniqueCode)
                .investorId(investor.getId())
                .email(session.getEmail())
                .requiresPanCard(false)
                .build();
    }

    private String generateUniqueCode() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String code = datePrefix + (1000 + RANDOM.nextInt(9000));
        while (investorRepository.findByUniqueCode(code).isPresent() || 
               sessionRepository.findByUniqueCode(code).isPresent()) {
            code = datePrefix + (1000 + RANDOM.nextInt(9000));
        }
        return code;
    }

    private String generateOtp() {
        return String.valueOf(1000 + RANDOM.nextInt(9000));
    }

    private void storeOtp(String uniqueCode, String emailOtp) {
        OtpVerification otp = otpRepository.findByUniqueCode(uniqueCode)
                .orElse(new OtpVerification());
        otp.setUniqueCode(uniqueCode);
        otp.setEmailOtp(emailOtp);
        otp.setSmsOtp("1111"); // Placeholder for SMS OTP
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        otp.setVerified(false);
        otp.setAttempts(0);
        otpRepository.save(otp);
    }

    private String determineInvestorType(IndividualRegistrationDto dto) {
        boolean isIndianNationality = isIndiaId(dto.getNationality());
        boolean isIndiaResidence = isIndiaId(dto.getCountryOfResidence());
        boolean hasPan = Boolean.TRUE.equals(dto.getHasPanCard());
        boolean hasOci = Boolean.TRUE.equals(dto.getHasOciCard());
        Boolean isPio = isIndianNationality ? null : dto.getIsPersonOfIndianOrigin();

        try {
            return InvestorType.determineType(
                    true, isIndianNationality, false, isIndiaResidence,
                    hasPan, isPio, hasOci).name();
        } catch (Exception e) {
            log.warn("determineInvestorType failed: {}", e.getMessage());
            return "FOREIGN_NATIONAL";
        }
    }

    private static boolean isIndiaId(Integer id) {
        return id != null && (id == 1 || id == 240);
    }


    private boolean createAzureUser(AuthorizedUser user, String password) {
        UserMgmtApiClient userMgmtClient = userMgmtClientProvider.getIfAvailable();
        if (userMgmtClient == null) {
            log.warn("User management service not available. User will not be created in Azure AD: {}", user.getEmailId());
            return false;
        }

        try {
            // Get Azure B2C credentials from database
            TenantB2CConfig b2cConfig = tenantB2CConfigService.getConfigForCurrentTenantOrDefault();
            if (b2cConfig == null) {
                log.warn("Azure B2C configuration not found in database for current tenant. User will not be created in Azure AD.");
                return false;
            }
            
            // Determine correct tenant identifier for Azure AD
            String tenantIdentifier;
            if (b2cConfig.getB2cTenantId() != null && !b2cConfig.getB2cTenantId().isEmpty()) {
                // Use GUID if available (for CIAM or when tenant ID is stored)
                tenantIdentifier = b2cConfig.getB2cTenantId();
            } else {
                // For B2C Classic, append .onmicrosoft.com to tenant name
                tenantIdentifier = b2cConfig.getB2cTenantName() + ".onmicrosoft.com";
            }
            
            log.info("Creating Azure user with tenant identifier: {}", tenantIdentifier);
            
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

            // Treat as success only if an Azure object id came back. An empty errorMsg alone is
            // not enough — user-mgmt-service has returned 201 with an empty body when Graph
            // rejected the call.
            if (response != null
                    && response.getId() != null && !response.getId().isBlank()
                    && (response.getErrorMsg() == null || response.getErrorMsg().isEmpty())) {
                log.info("User created successfully in Azure AD for email: {}, Azure ID: {}", user.getEmailId(), response.getId());
                return true;
            } else {
                String reason = response == null
                        ? "null response"
                        : (response.getErrorMsg() != null && !response.getErrorMsg().isEmpty()
                                ? response.getErrorMsg()
                                : "no Azure id in response");
                log.error("Azure AD user creation failed for {}: {}", user.getEmailId(), reason);
                return false;
            }
        } catch (Exception e) {
            log.error("Azure AD user creation failed with exception for {}: {}", user.getEmailId(), e.getMessage(), e);
            return false;
        }
    }

    private void saveInvestorConsents(String uniqueCode, RegistrationSession session, boolean termsAccepted, LocalDate dateOfBirth, Tenant tenant) {
        try {
            InvestorConsents consents = InvestorConsents.builder()
                    .investorUniqueId(uniqueCode)
                    .email(session.getEmail())
                    .userDob(dateOfBirth)
                    .termsAccepted(termsAccepted)
                    .privacyPolicyAccepted(session.getConsentGiven()) // Map initial consent to privacy policy
                    .informationCorrectConsent(termsAccepted)
                    .legalCapacityConsent(true) // Implied by registration completion
                    .consentDate(LocalDateTime.now())
                    .consentVersion("1.0")
                    .build();
            consents.setTenant(tenant);
            investorConsentsRepository.save(consents);
            log.info("Investor consents saved for uniqueCode: {}", uniqueCode);
        } catch (Exception e) {
            log.error("Failed to save investor consents for {}: {}", uniqueCode, e.getMessage());
        }
    }

    private void sendLoginDetailsEmail(String email, String investorName, String loginId, String password) {
        GraphEmailService graphEmailService = graphEmailServiceProvider.getIfAvailable();
        if (graphEmailService != null) {
            try {
                boolean sent = graphEmailService.sendLoginDetailsEmail(email, investorName, loginId, password, clientUrl);
                if (sent) {
                    log.info("Login details email sent to {}", email);
                } else {
                    log.warn("Failed to send login details email to {}", email);
                }
            } catch (Exception e) {
                log.warn("Failed to send login details email to {}: {}", email, e.getMessage());
            }
        } else {
            log.warn("Graph Email service not available. Login credentials: email={}, password={}", email, password);
        }
    }
}
