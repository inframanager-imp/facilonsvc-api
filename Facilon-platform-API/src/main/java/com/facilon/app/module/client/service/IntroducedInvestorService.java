package com.facilon.app.module.client.service;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.integration.dynamics.DynamicsCrmService;
import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.model.Tenant;
import com.facilon.app.module.client.dto.onboarding.*;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.model.IntroInvestorTemp;
import com.facilon.app.module.client.model.UserPersonalInformation;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.module.client.repository.IntroInvestorTempRepository;
import com.facilon.app.module.client.repository.UserPersonalInformationRepository;
import com.facilon.app.module.client.repository.MasterBrokersRepository;
import com.facilon.app.module.client.model.master.MasterBrokers;
import com.facilon.app.repository.AuthorizedUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 4-step introduced investor (nextholder) flow.
 * Brokers/service providers introduce new investors.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class IntroducedInvestorService {

    private static final Random RANDOM = new Random();

    private final IntroInvestorTempRepository introTempRepository;
    private final InvestorRepository investorRepository;
    private final AuthorizedUserRepository userRepository;
    private final UserPersonalInformationRepository personalInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final InvestorNotificationService notificationService;
    private final MasterBrokersRepository brokersRepository;
    private final ObjectProvider<DynamicsCrmService> dynamicsCrmServiceProvider;

    public IntroInvestorResponseDto processStep1(IntroInvestorStep1Dto dto) {
        String uniqueCode = generateUniqueCode();

        Tenant tenant = TenantContextHolder.getContext().getTenant();

        IntroInvestorTemp temp = IntroInvestorTemp.builder()
                .uniqueCodeDb(uniqueCode)
                .introFirstName(dto.getFirstName())
                .introMiddleName(dto.getMiddleName())
                .introLastName(dto.getLastName())
                .introEmail(dto.getEmail())
                .introMobile(dto.getMobile())
                .introDvNationality(dto.getNationality() != null ? String.valueOf(dto.getNationality()) : null)
                .investorRegisterAs(dto.getRegisterAs() != null ? String.valueOf(dto.getRegisterAs()) : null)
                .ssBrokerValue(dto.getBrokerValue())
                .status(1)
                .loginStatus(0)
                .build();
        temp.setTenant(tenant);
        introTempRepository.save(temp);

        // Send notification to broker about new introduction
        if (dto.getBrokerValue() != null && dto.getEmail() != null) {
            try {
                sendBrokerIntroductionEmail(
                    dto.getBrokerValue(),
                    buildFullName(dto.getFirstName(), dto.getMiddleName(), dto.getLastName()),
                    dto.getEmail(),
                    dto.getMobile()
                );
            } catch (Exception e) {
                log.warn("Failed to send introduction notification to broker", e);
            }
        }

        log.info("Introduced investor step1 completed, code: {}", uniqueCode);
        return IntroInvestorResponseDto.builder()
                .uniqueCode(uniqueCode)
                .step(1)
                .message("Step 1 completed")
                .build();
    }

    public IntroInvestorResponseDto processStep2(String code, IntroInvestorStep2Dto dto) {
        IntroInvestorTemp temp = introTempRepository.findByUniqueCodeDb(code)
                .orElseThrow(() -> new RuntimeException("Introduced investor not found: " + code));

        if (dto.getProductValue() != null) temp.setSsProductValue(dto.getProductValue());
        if (dto.getBrokeragePlanValue() != null) temp.setSsBrokeragePlanValue(dto.getBrokeragePlanValue());
        if (dto.getInvestRouteValue() != null) temp.setInvestRouteValue(dto.getInvestRouteValue());
        temp.setTenant(TenantContextHolder.getContext().getTenant());
        introTempRepository.save(temp);

        log.info("Introduced investor step2 completed, code: {}", code);
        return IntroInvestorResponseDto.builder()
                .uniqueCode(code)
                .step(2)
                .message("Step 2 completed")
                .build();
    }

    public IntroInvestorResponseDto processStep3(String code, IntroInvestorStep3Dto dto) {
        IntroInvestorTemp temp = introTempRepository.findByUniqueCodeDb(code)
                .orElseThrow(() -> new RuntimeException("Introduced investor not found: " + code));

        if (dto.getBrokerPreferredBank() != null) temp.setBrokerPreferredBank(dto.getBrokerPreferredBank());
        if (dto.getSchemeName() != null) temp.setIntroSchemeName(dto.getSchemeName());
        temp.setTenant(TenantContextHolder.getContext().getTenant());
        introTempRepository.save(temp);

        log.info("Introduced investor step3 completed, code: {}", code);
        return IntroInvestorResponseDto.builder()
                .uniqueCode(code)
                .step(3)
                .message("Step 3 completed")
                .build();
    }

    public IntroInvestorResponseDto processStep4(String code, IntroInvestorStep4Dto dto) {
        IntroInvestorTemp temp = introTempRepository.findByUniqueCodeDb(code)
                .orElseThrow(() -> new RuntimeException("Introduced investor not found: " + code));

        Tenant tenant = TenantContextHolder.getContext().getTenant();

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
                .registerAs(temp.getInvestorRegisterAs() != null ? Integer.parseInt(temp.getInvestorRegisterAs()) : 1)
                .verifyStatus(2)
                .incorpCountry(0)
                // Carry the pre-existing Dataverse CRM IDs from intro_investor_temp so later
                // write-backs (bank, passport, personal-info PATCHes) target the right record.
                .dvContactId(temp.getSsContactId())
                .dvInvestorGuid(temp.getIntroInvestorId())
                .dvInvestorSsId(temp.getIntroDvInvestorSsId())
                .build();
        investor.setTenant(tenant);
        investor = investorRepository.save(investor);

        UserPersonalInformation personalInfo = UserPersonalInformation.builder()
                .investorUniqueId(code)
                .investorId(String.valueOf(investor.getId()))
                .investorFirstName(temp.getIntroFirstName())
                .investorMiddleName(temp.getIntroMiddleName())
                .investorLastName(temp.getIntroLastName())
                .investorGender(temp.getIntroGender())
                .build();
        personalInfo.setTenant(tenant);
        personalInfoRepository.save(personalInfo);

        temp.setStatus(2);
        temp.setLoginStatus(1);
        temp.setPassword(passwordEncoder.encode(dto.getPassword()));
        introTempRepository.save(temp);

        // Send registration complete notification to broker
        if (temp.getSsBrokerValue() != null) {
            try {
                String investorName = buildFullName(temp.getIntroFirstName(), temp.getIntroMiddleName(), temp.getIntroLastName());
                sendBrokerRegistrationCompleteEmail(
                    temp.getSsBrokerValue(),
                    investorName,
                    code
                );
            } catch (Exception e) {
                log.warn("Failed to send registration complete notification to broker", e);
            }
        }

        // Dataverse write-back (aligned to Laravel InvestorController::introduce_investor_register_step4_insert_data L2127)
        // Three PATCH calls: update contact name/email/mobile → re-assert investor link →
        // update ss_investors.ss_*introduceind fields.
        DynamicsCrmService crm = dynamicsCrmServiceProvider.getIfAvailable();
        if (crm != null && temp.getSsContactId() != null && temp.getIntroInvestorId() != null) {
            try {
                crm.completeIntroducedInvestor(
                        temp.getSsContactId(),
                        temp.getIntroInvestorId(),
                        temp.getIntroFirstName(),
                        temp.getIntroMiddleName(),
                        temp.getIntroLastName(),
                        temp.getIntroEmail(),
                        temp.getIntroMobile()
                );
            } catch (Exception e) {
                log.error("Dataverse introduced-investor write-back failed for {}: {}", code, e.getMessage(), e);
            }
        }

        log.info("Introduced investor step4 completed, investorId: {}, code: {}", investor.getId(), code);
        return IntroInvestorResponseDto.builder()
                .uniqueCode(code)
                .investorId(investor.getId().intValue())
                .step(4)
                .message("Registration complete")
                .build();
    }

    private String generateUniqueCode() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String code = datePrefix + (1000 + RANDOM.nextInt(9000));
        while (investorRepository.findByUniqueCode(code).isPresent()
                || introTempRepository.findByUniqueCodeDb(code).isPresent()) {
            code = datePrefix + (1000 + RANDOM.nextInt(9000));
        }
        return code;
    }
    
    /**
     * Helper: Build full name from components
     */
    private String buildFullName(String firstName, String middleName, String lastName) {
        StringBuilder name = new StringBuilder();
        if (firstName != null) {
            name.append(firstName);
        }
        if (middleName != null && !middleName.isBlank()) {
            if (name.length() > 0) name.append(" ");
            name.append(middleName);
        }
        if (lastName != null && !lastName.isBlank()) {
            if (name.length() > 0) name.append(" ");
            name.append(lastName);
        }
        return name.toString().trim();
    }
    
    /**
     * Send broker notification about new investor introduction
     */
    private void sendBrokerIntroductionEmail(String brokerId, String investorName, String investorEmail, String investorMobile) {
        try {
            // Get broker details
            BrokerContactInfo brokerInfo = getBrokerContactInfo(brokerId);
            if (brokerInfo == null || brokerInfo.email == null) {
                log.warn("No email found for broker: {}", brokerId);
                return;
            }
            
            // Send email
            notificationService.sendIntroducedInvestorEmail(
                brokerInfo.email,
                brokerInfo.name,
                investorName,
                investorEmail,
                investorMobile
            );
            
            log.info("Sent introduction notification to broker {} ({})", brokerInfo.name, brokerInfo.email);
        } catch (Exception e) {
            log.error("Failed to send broker introduction email for broker: {}", brokerId, e);
        }
    }
    
    /**
     * Send broker notification about investor registration completion
     */
    private void sendBrokerRegistrationCompleteEmail(String brokerId, String investorName, String investorCode) {
        try {
            // Get broker details
            BrokerContactInfo brokerInfo = getBrokerContactInfo(brokerId);
            if (brokerInfo == null || brokerInfo.email == null) {
                log.warn("No email found for broker: {}", brokerId);
                return;
            }
            
            // Send email
            notificationService.sendIntroducedInvestorRegisteredEmail(
                brokerInfo.email,
                brokerInfo.name,
                investorName,
                investorCode
            );
            
            log.info("Sent registration complete notification to broker {} ({})", brokerInfo.name, brokerInfo.email);
        } catch (Exception e) {
            log.error("Failed to send broker registration complete email for broker: {}", brokerId, e);
        }
    }
    
    /**
     * Get broker contact information
     * TODO: This is a simplified implementation. In production:
     * 1. Brokers should have user accounts with email addresses
     * 2. Or maintain a separate broker contacts table
     * 3. For now, we try to find broker in MasterBrokers and generate email from broker ID
     */
    private BrokerContactInfo getBrokerContactInfo(String brokerId) {
        if (brokerId == null || brokerId.isBlank()) {
            return null;
        }
        
        // Try to find broker in MasterBrokers table
        try {
            // If brokerId is numeric, try finding by ID
            if (brokerId.matches("\\d+")) {
                Long id = Long.parseLong(brokerId);
                return brokersRepository.findById(id)
                    .map(broker -> new BrokerContactInfo(
                        broker.getSsName() != null ? broker.getSsName() : "Broker",
                        generateBrokerEmail(broker.getSsBrokerId(), broker.getSsName())
                    ))
                    .orElse(null);
            }
            
            // Try finding by broker ID string
            return brokersRepository.findBySsBrokerId(brokerId)
                .map(broker -> new BrokerContactInfo(
                    broker.getSsName() != null ? broker.getSsName() : "Broker",
                    generateBrokerEmail(broker.getSsBrokerId(), broker.getSsName())
                ))
                .orElse(null);
                
        } catch (Exception e) {
            log.warn("Could not find broker details for: {}", brokerId, e);
        }
        
        // Fallback: generate from broker ID
        return new BrokerContactInfo(
            "Broker " + brokerId,
            generateBrokerEmail(brokerId, null)
        );
    }
    
    /**
     * Generate broker email (temporary solution until brokers have proper user accounts)
     * TODO: Replace with actual broker email lookup
     */
    private String generateBrokerEmail(String brokerId, String brokerName) {
        // In production, this should be replaced with actual email lookup
        // For now, generate a placeholder email
        if (brokerId == null) {
            return "broker@facilon.com";
        }
        
        // Clean broker ID for email
        String cleanId = brokerId.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        if (cleanId.isBlank()) {
            cleanId = "broker";
        }
        
        return cleanId + "@broker.facilon.com";
    }
    
    /**
     * Simple DTO for broker contact info
     */
    private static class BrokerContactInfo {
        final String name;
        final String email;
        
        BrokerContactInfo(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }
}
