package com.facilon.app.module.client.service;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.integration.email.EmailServiceApiClient;
import com.facilon.app.integration.usermgmt.UserMgmtApiClient;
import com.facilon.app.integration.usermgmt.dto.SignUpDto;
import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.model.Tenant;
import com.facilon.app.module.client.dto.onboarding.*;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.model.UserPersonalInformation;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.repository.AuthorizedUserRepository;
import com.facilon.app.module.client.repository.UserPersonalInformationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Batch registration of multiple investors by Broker/Admin.
 * Creates AuthorizedUser, Investor, UserPersonalInformation for each.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BatchRegistrationService {

    private static final Random RANDOM = new Random();

    private final AuthorizedUserRepository userRepository;
    private final InvestorRepository investorRepository;
    private final UserPersonalInformationRepository personalInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<UserMgmtApiClient> userMgmtApiClientProvider;
    private final ObjectProvider<EmailServiceApiClient> emailServiceApiClientProvider;

    @Value("${investor.onboarding.batch-default-password:TempPass123!}")
    private String defaultTempPassword;

    @Value("${azure.b2c.client-id:}")
    private String azureClientId;
    @Value("${azure.b2c.client-secret:}")
    private String azureClientSecret;
    @Value("${azure.b2c.tenant-id:}")
    private String azureTenantId;

    public BatchRegistrationResponseDto registerBatch(BatchRegistrationDto dto) {
        Tenant tenant = TenantContextHolder.getContext().getTenant();
        List<BatchRegistrationResponseDto.BatchSuccessItemDto> successes = new ArrayList<>();
        List<BatchRegistrationResponseDto.BatchFailureItemDto> failures = new ArrayList<>();

        for (BatchInvestorDto inv : dto.getInvestors()) {
            try {
                var result = registerOne(tenant, inv);
                successes.add(BatchRegistrationResponseDto.BatchSuccessItemDto.builder()
                        .email(inv.getEmail())
                        .uniqueCode(result.uniqueCode)
                        .investorId(result.investorId)
                        .build());

                EmailServiceApiClient emailClient = emailServiceApiClientProvider.getIfAvailable();
                if (emailClient != null) {
                    try {
                        emailClient.sendWelcomeEmail(
                                inv.getEmail(),
                                inv.getFirstName(),
                                inv.getLastName() != null ? inv.getLastName() : "");
                        log.info("Welcome email sent to {} for batch registration", inv.getEmail());
                    } catch (Exception e) {
                        log.warn("Failed to send welcome email to {}: {}", inv.getEmail(), e.getMessage());
                    }
                }
            } catch (Exception e) {
                log.warn("Batch registration failed for {}: {}", inv.getEmail(), e.getMessage());
                failures.add(BatchRegistrationResponseDto.BatchFailureItemDto.builder()
                        .email(inv.getEmail())
                        .reason(e.getMessage())
                        .build());
            }
        }

        return BatchRegistrationResponseDto.builder()
                .totalCount(dto.getInvestors().size())
                .successCount(successes.size())
                .failureCount(failures.size())
                .successes(successes)
                .failures(failures)
                .build();
    }

    private record RegisterResult(String uniqueCode, Long investorId) {
    }

    private RegisterResult registerOne(Tenant tenant, BatchInvestorDto inv) {
        if (userRepository.findByEmailId(inv.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered: " + inv.getEmail());
        }

        String uniqueCode = generateUniqueCode();
        String password = defaultTempPassword;

        AuthorizedUser user = AuthorizedUser.builder()
                .firstName(inv.getFirstName())
                .lastName(inv.getLastName())
                .emailId(inv.getEmail())
                .mobilePhone(inv.getMobilePhone())
                .password(passwordEncoder.encode(password))
                .loginId(inv.getEmail())
                .isActive(true)
                .build();
        user.setTenant(tenant);
        user = userRepository.save(user);

        Investor investor = Investor.builder()
                .authorizedUser(user)
                .uniqueCode(uniqueCode)
                .registerAs(inv.getRegisterAs() != null ? inv.getRegisterAs() : 1)
                .nationality(inv.getNationality())
                .market(inv.getMarket())
                .verifyStatus(2)
                .build();
        investor.setTenant(tenant);
        investor = investorRepository.save(investor);

        UserPersonalInformation personalInfo = UserPersonalInformation.builder()
                .investorUniqueId(uniqueCode)
                .investorId(String.valueOf(investor.getId()))
                .investorFirstName(inv.getFirstName())
                .investorMiddleName(inv.getMiddleName())
                .investorLastName(inv.getLastName())
                .build();
        personalInfo.setTenant(tenant);
        personalInfoRepository.save(personalInfo);

        UserMgmtApiClient userMgmtClient = userMgmtApiClientProvider.getIfAvailable();
        if (userMgmtClient != null) {
            try {
                SignUpDto signUpDto = SignUpDto.builder()
                        .email(user.getEmailId())
                        .password(password)
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .mobilePhone(user.getMobilePhone())
                        .clientId(azureClientId)
                        .clientSecret(azureClientSecret)
                        .tenantId(azureTenantId)
                        .build();
                userMgmtClient.createUserLatest(signUpDto);
                log.info("User created in Azure AD for batch investor {}", investor.getId());
            } catch (Exception e) {
                log.warn("Azure AD user creation failed for {}: {}", inv.getEmail(), e.getMessage());
            }
        }

        return new RegisterResult(uniqueCode, investor.getId());
    }

    private String generateUniqueCode() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String code = datePrefix + (1000 + RANDOM.nextInt(9000));
        while (investorRepository.findByUniqueCode(code).isPresent()) {
            code = datePrefix + (1000 + RANDOM.nextInt(9000));
        }
        return code;
    }
}
