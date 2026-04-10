package com.facilon.app.module.client.service;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.integration.graphemail.GraphEmailService;
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
import org.springframework.beans.factory.annotation.Autowired;
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

    /** Optional — wired only when Graph email credentials are present. */
    @Autowired(required = false)
    private GraphEmailService graphEmailService;

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

        // Laravel only sets incorpCountry for Legal Entity users (it stores the
        // country of incorporation, NOT the residence country). For Self users
        // we leave it null and use Investor.countryOfResidence instead.
        boolean isLegalEntity = "Legal Entity".equalsIgnoreCase(dto.getRegisterAs());
        Integer incorpCountryValue = isLegalEntity && dto.getLegalCountryId() != null
                ? dto.getLegalCountryId().intValue()
                : null;

        Investor investor = Investor.builder()
                .authorizedUser(user)
                .uniqueCode(uniqueCode)
                .registerAs(REGISTER_AS_PMS) // 3 = PMS investor type (Self vs Legal stored on entityName)
                .verifyStatus(2)
                .incorpCountry(incorpCountryValue)
                .build();
        // Laravel parity — persist nationality, residence type and consent flags on Investor.
        applyLaravelParityFields(investor, dto);
        investor.setTenant(tenant);
        investor = investorRepository.save(investor);

        // PMS link is optional — Laravel inherits manager/plan/bank from the
        // introducer's Dataverse record and never asks the user to pick them.
        // Skip the InvestorPmsDetails row when no manager id is supplied.
        if (dto.getPmsManagerId() != null && dto.getPmsManagerId() > 0
                && dto.getPmsPlanId() != null && dto.getPmsPlanId() > 0) {

            MasterPortfolioManagers pmsManager = pmsManagerRepository.findById(dto.getPmsManagerId())
                    .orElseThrow(() -> new RuntimeException("PMS manager not found: " + dto.getPmsManagerId()));
            MasterPmsPlans pmsPlan = pmsPlanRepository.findById(dto.getPmsPlanId())
                    .orElseThrow(() -> new RuntimeException("PMS plan not found: " + dto.getPmsPlanId()));

            // Bank is optional — master_pms_banks may be empty in environments
            // where Dataverse sync has not yet populated it.
            MasterPmsBanks pmsBank = null;
            if (dto.getPmsBankId() != null && dto.getPmsBankId() > 0) {
                pmsBank = pmsBankRepository.findById(dto.getPmsBankId())
                        .orElseThrow(() -> new RuntimeException("PMS bank not found: " + dto.getPmsBankId()));
            }

            InvestorPmsDetails pmsDetails = InvestorPmsDetails.builder()
                    .investorUniqueCode(uniqueCode)
                    .pmsManager(pmsManager)
                    .pmsPlan(pmsPlan)
                    .pmsBank(pmsBank)
                    .accountNumber(dto.getAccountNumber())
                    .agreementDate(dto.getAgreementDate() != null ? dto.getAgreementDate() : LocalDate.now())
                    .comments(dto.getComments())
                    .build();
            pmsDetails.setTenant(tenant);
            pmsDetailsRepository.save(pmsDetails);
            log.info("Linked InvestorPmsDetails to PMS manager {} / plan {} / bank {}",
                    dto.getPmsManagerId(), dto.getPmsPlanId(), dto.getPmsBankId());
        } else {
            log.info("PMS link skipped — no manager/plan supplied (introducer pre-fetch not implemented)");
        }

        UserPersonalInformation personalInfo = UserPersonalInformation.builder()
                .investorUniqueId(uniqueCode)
                .investorId(String.valueOf(investor.getId()))
                .investorFirstName(dto.getFirstName())
                .investorMiddleName(dto.getMiddleName())
                .investorLastName(dto.getLastName())
                .userDob(dto.getUserDob())
                .investorGender(dto.getGender())
                .gender(dto.getGender())
                .build();
        personalInfo.setTenant(tenant);
        personalInfoRepository.save(personalInfo);

        // Send welcome / credentials email (non-fatal on failure).
        tryDeliverWelcomeEmail(dto);

        log.info("PMS investor registered: investorId={}, uniqueCode={}", investor.getId(), uniqueCode);
        return VerificationResponseDto.builder()
                .success(true)
                .investorId(investor.getId())
                .uniqueCode(uniqueCode)
                .message("PMS investor registered successfully")
                .build();
    }

    /**
     * Copy Laravel parity fields from the DTO into the {@link Investor} entity.
     * Only touches columns that Laravel's {@code introduce_investor_pms_register_step4_insert_data}
     * persists on the {@code investors} / {@code users} tables.
     */
    private void applyLaravelParityFields(Investor investor, PmsInvestorRegistrationDto dto) {
        if (dto.getNationalityId() != null) {
            investor.setNationality(dto.getNationalityId().intValue());
        }
        if (dto.getLegalCountryId() != null) {
            investor.setCountryOfResidence(dto.getLegalCountryId().intValue());
        }
        // "Yes" / "No" radios captured verbatim (Laravel stores them as strings too)
        if (dto.getPancard() != null) {
            investor.setPancardStatus(dto.getPancard());
        }
        if (dto.getOciCard() != null) {
            investor.setOciCardStatus(dto.getOciCard());
        }
        if (dto.getIndianOrigin() != null) {
            investor.setIndianOrigin(dto.getIndianOrigin());
        }
        // Consent / marketing flags — Integer 1/0 columns
        investor.setConfirmation(Boolean.TRUE.equals(dto.getConfirmation()) ? 1 : 0);
        investor.setTermsRead(Boolean.TRUE.equals(dto.getAgreeTerms()) ? 1 : 0);
        investor.setPrivacyPolicyAccepted(Boolean.TRUE.equals(dto.getAgreePrivacy()) ? 1 : 0);
        investor.setWhatsappConsent(Boolean.TRUE.equals(dto.getAgreeToWhatsapp()) ? 1 : 0);

        // Legal entity name (only when register_as == Legal Entity)
        if ("Legal Entity".equalsIgnoreCase(dto.getRegisterAs()) && dto.getLegalEntityFullName() != null) {
            investor.setEntityName(dto.getLegalEntityFullName());
        }
    }

    /**
     * Send a best-effort welcome / credentials email. Swallows all exceptions —
     * registration must succeed even if the Graph mailer is unavailable.
     */
    private void tryDeliverWelcomeEmail(PmsInvestorRegistrationDto dto) {
        if (graphEmailService == null) {
            log.warn("GraphEmailService not configured — skipping PMS registration email");
            return;
        }
        try {
            String firstName = dto.getFirstName() != null ? dto.getFirstName() : "Investor";
            String lastName = dto.getLastName() != null ? dto.getLastName() : "";
            graphEmailService.sendWelcomeEmail(dto.getEmail(), firstName, lastName);
            log.info("PMS registration welcome email sent to {}", dto.getEmail());
        } catch (Exception e) {
            log.warn("Failed to send PMS registration email to {}: {}", dto.getEmail(), e.getMessage());
        }
    }

    private void validatePmsReferences(PmsInvestorRegistrationDto dto) {
        // All three references are optional — see PmsInvestorRegistrationDto.
        // We only verify a row when a positive id is actually supplied.
        if (dto.getPmsManagerId() != null && dto.getPmsManagerId() > 0
                && !pmsManagerRepository.existsById(dto.getPmsManagerId())) {
            throw new RuntimeException("PMS manager not found: " + dto.getPmsManagerId());
        }
        if (dto.getPmsPlanId() != null && dto.getPmsPlanId() > 0
                && !pmsPlanRepository.existsById(dto.getPmsPlanId())) {
            throw new RuntimeException("PMS plan not found: " + dto.getPmsPlanId());
        }
        if (dto.getPmsBankId() != null && dto.getPmsBankId() > 0
                && !pmsBankRepository.existsById(dto.getPmsBankId())) {
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
