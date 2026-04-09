package com.facilon.app.module.client.service;

import com.facilon.app.exception.ResourceNotFoundException;
import com.facilon.app.module.client.dto.DataConsentDto;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.model.InvestorConsents;
import com.facilon.app.module.client.repository.InvestorConsentsRepository;
import com.facilon.app.module.client.repository.InvestorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsentService {

    private final InvestorRepository investorRepository;
    private final InvestorConsentsRepository consentRepository;

    @Transactional
    public DataConsentDto recordConsent(String uniqueCode, DataConsentDto dto, String ipAddress, String userAgent) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Recording consent for investor: {}, type: {}", uniqueCode, dto.getConsentType());

        InvestorConsents consent = consentRepository.findByInvestorUniqueId(uniqueCode)
                .orElse(InvestorConsents.builder()
                        .investorUniqueId(uniqueCode)
                        .investorId(investor.getId())
                        .email(investor.getAuthorizedUser().getEmailId())
                        .build());

        // Update specific consent based on type
        updateConsentField(consent, dto.getConsentType(), dto.getConsentGiven());

        // Update metadata
        consent.setConsentDate(LocalDateTime.now());
        consent.setIpAddress(ipAddress);
        consent.setUserAgent(userAgent);

        // Convert version if needed or store
        if (dto.getConsentDocumentUrl() != null) {
            consent.setConsentVersion(dto.getConsentDocumentUrl()); // Using doc url as version proxy if needed
        }

        consentRepository.save(consent);
        log.info("Consent recorded successfully");

        // Return updated DTO
        dto.setConsentDate(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        dto.setIpAddress(ipAddress);
        dto.setUserAgent(userAgent);

        return dto;
    }

    @Transactional(readOnly = true)
    public List<DataConsentDto> getConsents(String uniqueCode) {
        // Ensure investor exists
        getInvestorByUniqueCode(uniqueCode);

        log.info("Fetching consents for investor: {}", uniqueCode);

        Optional<InvestorConsents> consentOpt = consentRepository.findByInvestorUniqueId(uniqueCode);
        List<DataConsentDto> result = new ArrayList<>();

        if (consentOpt.isPresent()) {
            InvestorConsents consent = consentOpt.get();
            result.add(createDtoFromConsent(consent, "information_correct", consent.getInformationCorrectConsent()));
            result.add(createDtoFromConsent(consent, "privacy_policy", consent.getPrivacyPolicyAccepted()));
            result.add(createDtoFromConsent(consent, "marketing", consent.getNotificationConsent()));
            result.add(createDtoFromConsent(consent, "whatsapp", consent.getWhatsappConsent()));
        }

        return result;
    }

    // Helper to map type string to entity field update
    private void updateConsentField(InvestorConsents consent, String type, Boolean value) {
        if (type == null)
            return;

        switch (type.toLowerCase()) {
            case "information_correct":
            case "confirmation":
                consent.setInformationCorrectConsent(value);
                break;
            case "privacy_policy":
            case "terms_conditions":
                consent.setPrivacyPolicyAccepted(value);
                break;
            case "marketing":
            case "notification":
                consent.setNotificationConsent(value);
                break;
            case "whatsapp":
                consent.setWhatsappConsent(value);
                break;
            default:
                log.warn("Unknown consent type: {}", type);
        }
    }

    private DataConsentDto createDtoFromConsent(InvestorConsents consent, String type, Boolean value) {
        return DataConsentDto.builder()
                .consentType(type)
                .consentGiven(Boolean.TRUE.equals(value))
                .consentDate(consent.getConsentDate() != null ? consent.getConsentDate().toString() : null)
                .ipAddress(consent.getIpAddress())
                .userAgent(consent.getUserAgent())
                .build();
    }

    private Investor getInvestorByUniqueCode(String uniqueCode) {
        return investorRepository.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found with unique code: " + uniqueCode));
    }
}
