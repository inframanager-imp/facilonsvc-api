package com.facilon.app.module.client.service;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.integration.email.EmailServiceApiClient;
import com.facilon.app.integration.sms.SmsServiceApiClient;
import com.facilon.app.integration.usermgmt.UserMgmtApiClient;
import com.facilon.app.integration.usermgmt.dto.SignUpDto;
import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.model.Tenant;
import com.facilon.app.module.client.dto.onboarding.*;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.model.InvestorPmsDetails;
import com.facilon.app.module.client.model.IntroPmsInvestorTemp;
import com.facilon.app.module.client.model.UserPersonalInformation;
import com.facilon.app.module.client.model.master.MasterPmsBanks;
import com.facilon.app.module.client.model.master.MasterPmsPlans;
import com.facilon.app.module.client.model.master.MasterPortfolioManagers;
import com.facilon.app.module.client.repository.*;
import com.facilon.app.repository.AuthorizedUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * PMS investor nextholder flow per docs/Investor (4-step public flow).
 * Step 1: Self vs Legal Entity + PMS context
 * Step 2: Personal details (incl. DOB) + OTP send
 * Step 3: OTP verify
 * Step 4: Consents + password → create user, investor, InvestorPmsDetails
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PmsInvestorNextholderService {

    private static final Random RANDOM = new Random();
    private static final int REGISTER_AS_PMS = 3;

    private final IntroPmsInvestorTempRepository pmsTempRepository;
    private final InvestorRepository investorRepository;
    private final InvestorPmsDetailsRepository pmsDetailsRepository;
    private final MasterPortfolioManagersRepository pmsManagerRepository;
    private final MasterPmsPlansRepository pmsPlanRepository;
    private final MasterPmsBanksRepository pmsBankRepository;
    private final AuthorizedUserRepository userRepository;
    private final UserPersonalInformationRepository personalInfoRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<UserMgmtApiClient> userMgmtApiClientProvider;
    private final ObjectProvider<EmailServiceApiClient> emailServiceApiClientProvider;
    private final ObjectProvider<SmsServiceApiClient> smsServiceApiClientProvider;

    @Value("${investor.onboarding.india-country-code:240}")
    private Integer indiaCountryCode;
    @Value("${investor.onboarding.india-isd-code:91}")
    private String indiaIsdCode;
    @Value("${azure.b2c.client-id:}")
    private String azureClientId;
    @Value("${azure.b2c.client-secret:}")
    private String azureClientSecret;
    @Value("${azure.b2c.tenant-id:}")
    private String azureTenantId;

    public IntroInvestorResponseDto processInit(PmsNextholderInitDto dto) {
        validatePmsReferences(dto.getPmsManagerId(), dto.getPmsPlanId(), dto.getPmsBankId());

        String uniqueCode = generateUniqueCode();
        Tenant tenant = TenantContextHolder.getContext().getTenant();

        IntroPmsInvestorTemp temp = IntroPmsInvestorTemp.builder()
                .uniqueCodeDb(uniqueCode)
                .investorRegisterAs(dto.getRegisterAs() != null ? String.valueOf(dto.getRegisterAs()) : "1")
                .legalEntityFullName(dto.getLegalEntityFullName())
                .incorpCountry(dto.getCountryOfIncorporation() != null ? String.valueOf(dto.getCountryOfIncorporation())
                        : null)
                .pmsManagerId(dto.getPmsManagerId())
                .pmsPlanId(dto.getPmsPlanId())
                .pmsBankId(dto.getPmsBankId())
                .status(1)
                .loginStatus(0)
                .build();
        temp.setTenant(tenant);
        pmsTempRepository.save(temp);

        log.info("PMS investor nextholder init completed, code: {}", uniqueCode);
        return IntroInvestorResponseDto.builder()
                .uniqueCode(uniqueCode)
                .step(1)
                .message("Step 1 completed")
                .build();
    }

    public OtpResponseDto processPersonal(String code, PmsNextholderPersonalDto dto) {
        IntroPmsInvestorTemp temp = pmsTempRepository.findByUniqueCodeDb(code)
                .orElseThrow(() -> new RuntimeException("PMS investor not found: " + code));

        if (userRepository.findByEmailId(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        temp.setIntroFirstName(dto.getFirstName());
        temp.setIntroMiddleName(dto.getMiddleName());
        temp.setIntroLastName(dto.getLastName());
        temp.setIntroDob(dto.getUserDob());
        temp.setIntroGender(dto.getGender());
        temp.setIntroEmail(dto.getEmail());
        temp.setIntroMobile(dto.getMobileNumber());
        temp.setIsdCode(dto.getCountryCode() != null ? String.valueOf(dto.getCountryCode()) : null);
        temp.setDiffMobWhatsapp("No".equalsIgnoreCase(dto.getSameWhatsapp()) ? dto.getDiffMobWhatsapp() : null);
        temp.setTenant(TenantContextHolder.getContext().getTenant());
        pmsTempRepository.save(temp);

        String emailOtp = otpService.generateOtp();
        boolean isIndia = dto.getCountryCode() != null && dto.getCountryCode().equals(indiaCountryCode);
        String smsOtp = isIndia ? otpService.generateOtp() : "1111";
        otpService.storeOtp(code, emailOtp, smsOtp, 10);

        EmailServiceApiClient emailClient = emailServiceApiClientProvider.getIfAvailable();
        boolean emailSent = false;
        if (emailClient != null) {
            try {
                emailClient.sendOtpEmail(dto.getEmail(), dto.getFirstName(), emailOtp);
                emailSent = true;
                log.info("OTP email sent to {} for PMS code: {}", dto.getEmail(), code);
            } catch (Exception e) {
                log.warn("Failed to send OTP email: {}", e.getMessage());
            }
        }

        boolean smsSent = false;
        if (isIndia) {
            SmsServiceApiClient smsClient = smsServiceApiClientProvider.getIfAvailable();
            if (smsClient != null) {
                try {
                    String fullNumber = (indiaIsdCode != null ? indiaIsdCode : "91")
                            + dto.getMobileNumber().replaceAll("\\D", "");
                    smsClient.sendOtpSms(fullNumber, smsOtp);
                    smsSent = true;
                    log.info("SMS OTP sent for PMS India (code: {})", code);
                } catch (Exception e) {
                    log.warn("Failed to send SMS OTP: {}", e.getMessage());
                }
            }
        }

        return OtpResponseDto.builder()
                .message("OTP sent successfully")
                .emailSent(emailSent)
                .smsSent(smsSent)
                .expiresInMinutes(10)
                .build();
    }

    public IntroInvestorResponseDto processVerifyOtp(IntroNextholderVerifyOtpDto dto) {
        String code = dto.getUniqueCode();
        pmsTempRepository.findByUniqueCodeDb(code)
                .orElseThrow(() -> new RuntimeException("PMS investor not found: " + code));

        if (!otpService.verifyOtp(code, dto.getEmailOtp(), "email")) {
            throw new RuntimeException("Invalid email OTP");
        }
        if (!otpService.verifyOtp(code, dto.getSmsOtp(), "sms")) {
            throw new RuntimeException("Invalid SMS OTP");
        }
        otpService.markAsVerified(code);

        log.info("PMS investor OTP verified, code: {}", code);
        return IntroInvestorResponseDto.builder()
                .uniqueCode(code)
                .step(3)
                .message("OTP verified")
                .build();
    }

    public IntroInvestorResponseDto processComplete(String code, PmsNextholderCompleteDto dto) {
        IntroPmsInvestorTemp temp = pmsTempRepository.findByUniqueCodeDb(code)
                .orElseThrow(() -> new RuntimeException("PMS investor not found: " + code));

        Tenant tenant = TenantContextHolder.getContext().getTenant();

        MasterPortfolioManagers pmsManager = pmsManagerRepository.findById(temp.getPmsManagerId())
                .orElseThrow(() -> new RuntimeException("PMS manager not found"));
        MasterPmsPlans pmsPlan = pmsPlanRepository.findById(temp.getPmsPlanId())
                .orElseThrow(() -> new RuntimeException("PMS plan not found"));
        MasterPmsBanks pmsBank = pmsBankRepository.findById(temp.getPmsBankId())
                .orElseThrow(() -> new RuntimeException("PMS bank not found"));

        AuthorizedUser user = AuthorizedUser.builder()
                .firstName(temp.getIntroFirstName())
                .lastName(temp.getIntroLastName())
                .emailId(temp.getIntroEmail())
                .mobilePhone(temp.getIntroMobile())
                .password(passwordEncoder.encode(dto.getPassword()))
                .loginId(temp.getIntroEmail())
                .isActive(true)
                .build();
        user.setTenant(tenant);
        user = userRepository.save(user);

        Investor investor = Investor.builder()
                .authorizedUser(user)
                .uniqueCode(code)
                .registerAs(REGISTER_AS_PMS)
                .verifyStatus(2)
                .incorpCountry(0)
                .build();
        investor.setTenant(tenant);
        investor = investorRepository.save(investor);

        InvestorPmsDetails pmsDetails = InvestorPmsDetails.builder()
                .investorUniqueCode(code)
                .pmsManager(pmsManager)
                .pmsPlan(pmsPlan)
                .pmsBank(pmsBank)
                .build();
        pmsDetails.setTenant(tenant);
        pmsDetailsRepository.save(pmsDetails);

        UserPersonalInformation personalInfo = UserPersonalInformation.builder()
                .investorUniqueId(code)
                .investorId(String.valueOf(investor.getId()))
                .investorFirstName(temp.getIntroFirstName())
                .investorMiddleName(temp.getIntroMiddleName())
                .investorLastName(temp.getIntroLastName())
                .investorGender(temp.getIntroGender())
                .userDob(temp.getIntroDob())
                .build();
        personalInfo.setTenant(tenant);
        personalInfoRepository.save(personalInfo);

        temp.setStatus(2);
        temp.setLoginStatus(1);
        temp.setPassword(passwordEncoder.encode(dto.getPassword()));
        temp.setAgreeForWhatsapp(dto.getAgreeToWhatsapp());
        temp.setAgreeForMarketing(dto.getAgreeToMarketing());
        temp.setAgreePrivacy(dto.getAgreePrivacy());
        temp.setAgreeTerms(dto.getAgreeTerms());
        pmsTempRepository.save(temp);

        UserMgmtApiClient userMgmtClient = userMgmtApiClientProvider.getIfAvailable();
        if (userMgmtClient != null) {
            try {
                SignUpDto signUpDto = SignUpDto.builder()
                        .email(user.getEmailId())
                        .password(dto.getPassword())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .mobilePhone(user.getMobilePhone())
                        .clientId(azureClientId)
                        .clientSecret(azureClientSecret)
                        .tenantId(azureTenantId)
                        .build();
                userMgmtClient.createUserLatest(signUpDto);
                log.info("User created in Azure AD for PMS investor {}", investor.getId());
            } catch (Exception e) {
                log.warn("Azure AD user creation failed: {}", e.getMessage());
            }
        }

        EmailServiceApiClient emailClient = emailServiceApiClientProvider.getIfAvailable();
        if (emailClient != null) {
            try {
                emailClient.sendWelcomeEmail(user.getEmailId(), user.getFirstName(),
                        user.getLastName() != null ? user.getLastName() : "");
            } catch (Exception e) {
                log.warn("Failed to send welcome email: {}", e.getMessage());
            }
        }

        log.info("PMS investor nextholder complete, investorId: {}, code: {}", investor.getId(), code);
        return IntroInvestorResponseDto.builder()
                .uniqueCode(code)
                .investorId(investor.getId().intValue())
                .step(4)
                .message("Registration complete")
                .build();
    }

    public IntroPmsInvestorTemp getByCode(String code) {
        return pmsTempRepository.findByUniqueCodeDb(code).orElse(null);
    }

    private void validatePmsReferences(Long pmsManagerId, Long pmsPlanId, Long pmsBankId) {
        if (!pmsManagerRepository.existsById(pmsManagerId)) {
            throw new RuntimeException("PMS manager not found: " + pmsManagerId);
        }
        if (!pmsPlanRepository.existsById(pmsPlanId)) {
            throw new RuntimeException("PMS plan not found: " + pmsPlanId);
        }
        if (!pmsBankRepository.existsById(pmsBankId)) {
            throw new RuntimeException("PMS bank not found: " + pmsBankId);
        }
    }

    private String generateUniqueCode() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String code = datePrefix + (1000 + RANDOM.nextInt(9000));
        while (investorRepository.findByUniqueCode(code).isPresent()
                || pmsTempRepository.findByUniqueCodeDb(code).isPresent()) {
            code = datePrefix + (1000 + RANDOM.nextInt(9000));
        }
        return code;
    }
}
