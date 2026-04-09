package com.facilon.app.module.client.service;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.model.Tenant;
import com.facilon.app.module.client.dto.onboarding.PmsInvestorRegistrationDto;
import com.facilon.app.module.client.dto.onboarding.VerificationResponseDto;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.model.InvestorPmsDetails;
import com.facilon.app.module.client.model.UserPersonalInformation;
import com.facilon.app.module.client.model.master.MasterPmsBanks;
import com.facilon.app.module.client.model.master.MasterPmsPlans;
import com.facilon.app.module.client.model.master.MasterPortfolioManagers;
import com.facilon.app.module.client.repository.InvestorPmsDetailsRepository;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.module.client.repository.MasterPmsBanksRepository;
import com.facilon.app.module.client.repository.MasterPmsPlansRepository;
import com.facilon.app.module.client.repository.MasterPortfolioManagersRepository;
import com.facilon.app.module.client.repository.UserPersonalInformationRepository;
import com.facilon.app.repository.AuthorizedUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * PMS (Portfolio Management Service) investor registration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PmsInvestorService {

    private static final Random RANDOM = new Random();
    /** registerAs value for PMS investors */
    private static final int REGISTER_AS_PMS = 3;

    private final AuthorizedUserRepository userRepository;
    private final InvestorRepository investorRepository;
    private final InvestorPmsDetailsRepository pmsDetailsRepository;
    private final UserPersonalInformationRepository personalInfoRepository;
    private final MasterPortfolioManagersRepository pmsManagerRepository;
    private final MasterPmsPlansRepository pmsPlanRepository;
    private final MasterPmsBanksRepository pmsBankRepository;
    private final PasswordEncoder passwordEncoder;

    public VerificationResponseDto registerPmsInvestor(PmsInvestorRegistrationDto dto) {
        validatePmsReferences(dto);

        if (userRepository.findByEmailId(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        String uniqueCode = generateUniqueCode();
        Tenant tenant = TenantContextHolder.getContext().getTenant();

        AuthorizedUser user = AuthorizedUser.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .emailId(dto.getEmail())
                .mobilePhone(dto.getMobilePhone())
                .password(passwordEncoder.encode(dto.getPassword()))
                .loginId(dto.getEmail())
                .isActive(true)
                .build();
        user.setTenant(tenant);
        user = userRepository.save(user);

        Investor investor = Investor.builder()
                .authorizedUser(user)
                .uniqueCode(uniqueCode)
                .registerAs(REGISTER_AS_PMS)
                .verifyStatus(2)
                .incorpCountry(0)
                .build();
        investor.setTenant(tenant);
        investor = investorRepository.save(investor);

        MasterPortfolioManagers pmsManager = pmsManagerRepository.findById(dto.getPmsManagerId())
                .orElseThrow(() -> new RuntimeException("PMS manager not found"));
        MasterPmsPlans pmsPlan = pmsPlanRepository.findById(dto.getPmsPlanId())
                .orElseThrow(() -> new RuntimeException("PMS plan not found"));
        MasterPmsBanks pmsBank = pmsBankRepository.findById(dto.getPmsBankId())
                .orElseThrow(() -> new RuntimeException("PMS bank not found"));

        InvestorPmsDetails pmsDetails = InvestorPmsDetails.builder()
                .investorUniqueCode(uniqueCode)
                .pmsManager(pmsManager)
                .pmsPlan(pmsPlan)
                .pmsBank(pmsBank)
                .accountNumber(dto.getAccountNumber())
                .agreementDate(dto.getAgreementDate())
                .comments(dto.getComments())
                .build();
        pmsDetails.setTenant(tenant);
        pmsDetailsRepository.save(pmsDetails);

        UserPersonalInformation personalInfo = UserPersonalInformation.builder()
                .investorUniqueId(uniqueCode)
                .investorId(String.valueOf(investor.getId()))
                .investorFirstName(dto.getFirstName())
                .investorLastName(dto.getLastName())
                .build();
        personalInfo.setTenant(tenant);
        personalInfoRepository.save(personalInfo);

        log.info("PMS investor registered: investorId={}, uniqueCode={}", investor.getId(), uniqueCode);
        return VerificationResponseDto.builder()
                .success(true)
                .investorId(investor.getId())
                .uniqueCode(uniqueCode)
                .message("PMS investor registered successfully")
                .build();
    }

    private void validatePmsReferences(PmsInvestorRegistrationDto dto) {
        if (!pmsManagerRepository.existsById(dto.getPmsManagerId())) {
            throw new RuntimeException("PMS manager not found: " + dto.getPmsManagerId());
        }
        if (!pmsPlanRepository.existsById(dto.getPmsPlanId())) {
            throw new RuntimeException("PMS plan not found: " + dto.getPmsPlanId());
        }
        if (!pmsBankRepository.existsById(dto.getPmsBankId())) {
            throw new RuntimeException("PMS bank not found: " + dto.getPmsBankId());
        }
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
