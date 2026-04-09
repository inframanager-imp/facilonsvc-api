package com.facilon.app.module.client.service;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.module.client.dto.PmsComplianceDto;
import com.facilon.app.module.client.dto.PmsPortfolioPreferencesDto;
import com.facilon.app.module.client.dto.PmsRegistrationDto;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.model.PmsCompliance;
import com.facilon.app.module.client.model.PmsPortfolioPreferences;
import com.facilon.app.module.client.model.PmsRegistration;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.module.client.repository.PmsComplianceRepository;
import com.facilon.app.module.client.repository.PmsPortfolioPreferencesRepository;
import com.facilon.app.module.client.repository.PmsRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class PmsService {

    private final InvestorRepository investorRepository;
    private final PmsRegistrationRepository pmsRegistrationRepository;
    private final PmsComplianceRepository pmsComplianceRepository;
    private final PmsPortfolioPreferencesRepository pmsPortfolioRepository;

    private static final BigDecimal MINIMUM_INVESTMENT = new BigDecimal("5000000"); // 50 lakhs minimum

    @Transactional
    public PmsRegistrationDto submitRegistration(String uniqueCode, PmsRegistrationDto dto) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Submitting PMS registration (Investment Profile) for investor: {}", uniqueCode);

        // Validate minimum investment
        if (dto.getInvestmentAmount() != null && dto.getInvestmentAmount().compareTo(MINIMUM_INVESTMENT) < 0) {
            throw new RuntimeException("Minimum investment amount is ₹" + MINIMUM_INVESTMENT);
        }

        // Set registration metadata
        LocalDateTime now = LocalDateTime.now();
        dto.setSubmittedDate(now.format(DateTimeFormatter.ISO_DATE_TIME));
        dto.setRegistrationStatus("pending");

        PmsRegistration registration = pmsRegistrationRepository.findByInvestorId(investor.getId())
                .orElse(PmsRegistration.builder()
                        .investorId(investor.getId())
                        .build());

        registration.setInvestmentAmount(dto.getInvestmentAmount());
        registration.setPortfolioType(dto.getPortfolioType());
        registration.setRiskProfile(dto.getRiskProfile());
        registration.setInvestmentObjective(dto.getInvestmentObjective());
        registration.setInvestmentHorizon(dto.getInvestmentHorizon());
        registration.setPmsManagerId(parseId(dto.getPortfolioManagerId()));
        registration.setPmsPlanId(parseId(dto.getPmsPlanId()));
        registration.setPmsBankId(parseId(dto.getPmsBankId()));
        registration.setServicePreferences(dto.getServicePreferences());
        registration.setEligibilityConfirmed(dto.getEligibilityConfirmed());
        registration.setRegistrationStatus("pending");
        registration.setSubmittedDate(now);
        registration.setTenant(TenantContextHolder.getContext().getTenant());

        pmsRegistrationRepository.save(registration);

        log.info("PMS registration submitted successfully");
        return dto;
    }

    public PmsRegistrationDto getRegistration(String uniqueCode) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Fetching PMS registration for investor: {}", uniqueCode);

        PmsRegistration registration = pmsRegistrationRepository.findByInvestorId(investor.getId())
                .orElse(null);

        if (registration == null) {
            return PmsRegistrationDto.builder().build();
        }

        return PmsRegistrationDto.builder()
                .investmentAmount(registration.getInvestmentAmount())
                .portfolioType(registration.getPortfolioType())
                .riskProfile(registration.getRiskProfile())
                .investmentObjective(registration.getInvestmentObjective())
                .investmentHorizon(registration.getInvestmentHorizon())
                .portfolioManagerId(toStringId(registration.getPmsManagerId()))
                .pmsPlanId(toStringId(registration.getPmsPlanId()))
                .pmsBankId(toStringId(registration.getPmsBankId()))
                .servicePreferences(registration.getServicePreferences())
                .eligibilityConfirmed(registration.getEligibilityConfirmed())
                .registrationStatus(registration.getRegistrationStatus())
                .submittedDate(
                        registration.getSubmittedDate() != null ? registration.getSubmittedDate().toString() : null)
                .build();
    }

    @Transactional
    public PmsRegistrationDto updateRegistration(String uniqueCode, PmsRegistrationDto dto) {
        return submitRegistration(uniqueCode, dto);
    }

    @Transactional
    public PmsComplianceDto submitCompliance(String uniqueCode, PmsComplianceDto dto, String ipAddress,
            String userAgent) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Submitting PMS compliance for investor: {}", uniqueCode);

        // Validate all required acknowledgments
        if (!Boolean.TRUE.equals(dto.getRiskDisclosureAcknowledged()) ||
                !Boolean.TRUE.equals(dto.getFeeStructureAccepted()) ||
                !Boolean.TRUE.equals(dto.getTermsAccepted())) {
            throw new RuntimeException("All compliance acknowledgments are required");
        }

        // Set metadata
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ISO_DATE_TIME);
        dto.setRiskDisclosureDate(timestamp);
        dto.setFeeStructureDate(timestamp);
        dto.setTermsAcceptedDate(timestamp);
        dto.setIpAddress(ipAddress);
        dto.setUserAgent(userAgent);
        dto.setComplianceStatus("completed");

        PmsCompliance compliance = pmsComplianceRepository.findByInvestorId(investor.getId())
                .orElse(PmsCompliance.builder()
                        .investorId(investor.getId())
                        .build());

        compliance.setRiskDisclosureAcknowledged(dto.getRiskDisclosureAcknowledged());
        compliance.setFeeStructureAccepted(dto.getFeeStructureAccepted());
        compliance.setTermsAccepted(dto.getTermsAccepted());
        compliance.setRegulatoryDisclosureAcknowledged(dto.getRegulatoryDisclosureAcknowledged());
        compliance.setConflictOfInterestDisclosed(dto.getConflictOfInterestDisclosed());
        compliance.setPerformanceDisclosureAcknowledged(dto.getPerformanceDisclosureAcknowledged());
        compliance.setRiskDisclosureDate(now);
        compliance.setFeeStructureDate(now);
        compliance.setTermsAcceptedDate(now);
        compliance.setIpAddress(ipAddress);
        compliance.setUserAgent(userAgent);
        compliance.setDigitalSignature(dto.getDigitalSignature());
        compliance.setComplianceStatus("completed");
        compliance.setTenant(TenantContextHolder.getContext().getTenant());

        pmsComplianceRepository.save(compliance);

        log.info("PMS compliance submitted successfully");
        return dto;
    }

    public PmsComplianceDto getCompliance(String uniqueCode) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Fetching PMS compliance for investor: {}", uniqueCode);

        PmsCompliance compliance = pmsComplianceRepository.findByInvestorId(investor.getId())
                .orElse(null);

        if (compliance == null) {
            return PmsComplianceDto.builder().build();
        }

        return PmsComplianceDto.builder()
                .riskDisclosureAcknowledged(compliance.getRiskDisclosureAcknowledged())
                .feeStructureAccepted(compliance.getFeeStructureAccepted())
                .termsAccepted(compliance.getTermsAccepted())
                .regulatoryDisclosureAcknowledged(compliance.getRegulatoryDisclosureAcknowledged())
                .conflictOfInterestDisclosed(compliance.getConflictOfInterestDisclosed())
                .performanceDisclosureAcknowledged(compliance.getPerformanceDisclosureAcknowledged())
                .riskDisclosureDate(
                        compliance.getRiskDisclosureDate() != null ? compliance.getRiskDisclosureDate().toString()
                                : null)
                .feeStructureDate(
                        compliance.getFeeStructureDate() != null ? compliance.getFeeStructureDate().toString() : null)
                .termsAcceptedDate(
                        compliance.getTermsAcceptedDate() != null ? compliance.getTermsAcceptedDate().toString() : null)
                .ipAddress(compliance.getIpAddress())
                .userAgent(compliance.getUserAgent())
                .digitalSignature(compliance.getDigitalSignature())
                .complianceStatus(compliance.getComplianceStatus())
                .build();
    }

    @Transactional
    public PmsPortfolioPreferencesDto setPortfolioPreferences(String uniqueCode, PmsPortfolioPreferencesDto dto) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Setting portfolio preferences for investor: {}", uniqueCode);

        // Validate asset allocation sums to 100
        if (dto.getAssetAllocation() != null) {
            int total = dto.getAssetAllocation().values().stream().mapToInt(Integer::intValue).sum();
            if (total != 100) {
                throw new RuntimeException("Asset allocation must sum to 100%");
            }
        }

        PmsPortfolioPreferences preferences = pmsPortfolioRepository.findByInvestorId(investor.getId())
                .orElse(PmsPortfolioPreferences.builder()
                        .investorId(investor.getId())
                        .build());

        preferences.setAssetAllocation(dto.getAssetAllocation());
        preferences.setRebalancingFrequency(dto.getRebalancingFrequency());
        preferences.setAutoRebalancing(dto.getAutoRebalancing());
        preferences.setPreferredSectors(dto.getPreferredSectors()); // Assuming DTO has String or we convert
        preferences.setExcludedSectors(dto.getExcludedSectors());
        preferences.setEsgPreference(dto.getEsgPreference());
        preferences.setDividendPreference(dto.getDividendPreference());
        preferences.setMaxSingleStockExposure(dto.getMaxSingleStockExposure());
        preferences.setInternationalExposure(dto.getInternationalExposure());
        preferences.setLiquidityPreference(dto.getLiquidityPreference());
        preferences.setTaxOptimization("enabled".equalsIgnoreCase(dto.getTaxOptimization()));
        preferences.setTenant(TenantContextHolder.getContext().getTenant());

        pmsPortfolioRepository.save(preferences);

        log.info("Portfolio preferences set successfully");
        return dto;
    }

    public PmsPortfolioPreferencesDto getPortfolioPreferences(String uniqueCode) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Fetching portfolio preferences for investor: {}", uniqueCode);

        PmsPortfolioPreferences preferences = pmsPortfolioRepository.findByInvestorId(investor.getId())
                .orElse(null);

        if (preferences == null) {
            return PmsPortfolioPreferencesDto.builder().build();
        }

        return PmsPortfolioPreferencesDto.builder()
                .assetAllocation(preferences.getAssetAllocation())
                .rebalancingFrequency(preferences.getRebalancingFrequency())
                .autoRebalancing(preferences.getAutoRebalancing())
                .preferredSectors(preferences.getPreferredSectors())
                .excludedSectors(preferences.getExcludedSectors())
                .esgPreference(preferences.getEsgPreference())
                .dividendPreference(preferences.getDividendPreference())
                .maxSingleStockExposure(preferences.getMaxSingleStockExposure())
                .internationalExposure(preferences.getInternationalExposure())
                .liquidityPreference(preferences.getLiquidityPreference())
                .taxOptimization(Boolean.TRUE.equals(preferences.getTaxOptimization()) ? "enabled" : "disabled")
                .build();
    }

    private Investor getInvestorByUniqueCode(String uniqueCode) {
        return investorRepository.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new RuntimeException("Investor not found with unique code: " + uniqueCode));
    }

    private Long parseId(String id) {
        if (id == null || id.isEmpty())
            return null;
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String toStringId(Long id) {
        return id != null ? id.toString() : null;
    }
}
