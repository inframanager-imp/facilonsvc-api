package com.facilon.app.module.client.service;

import com.facilon.app.module.client.dto.*;
import com.facilon.app.module.client.model.InvestorSow;
import com.facilon.app.module.client.model.SowTemplate;
import com.facilon.app.module.client.repository.InvestorSowRepository;
import com.facilon.app.module.client.repository.SowTemplateRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing Statement of Work (SOW) documents
 */
@Slf4j
@Service
public class SowService {

    @Autowired
    private SowTemplateRepository templateRepository;

    @Autowired
    private InvestorSowRepository sowRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Get active template for investor type
     */
    public SowTemplateDto getActiveTemplate(String applicableFor) {
        Optional<SowTemplate> template = templateRepository
                .findTopByApplicableForAndIsActiveTrueOrderByCreatedDateDesc(applicableFor);

        if (template.isEmpty()) {
            // Try to get "All" template
            template = templateRepository
                    .findTopByApplicableForAndIsActiveTrueOrderByCreatedDateDesc("All");
        }

        return template.map(this::convertToTemplateDto).orElse(null);
    }

    /**
     * Create new SOW from template
     */
    @Transactional
    public InvestorSowDto createSow(CreateSowDto dto) {
        InvestorSow sow = new InvestorSow();
        sow.setInvestorId(dto.getInvestorId());
        sow.setTemplateId(dto.getTemplateId());
        sow.setSowData(convertToJson(dto.getSowData()));
        sow.setStatus("draft");
        sow.setCreatedDate(LocalDateTime.now());

        InvestorSow saved = sowRepository.save(sow);
        log.info("Created SOW for investor {}", dto.getInvestorId());

        return convertToSowDto(saved);
    }

    /**
     * Update SOW (draft only)
     */
    @Transactional
    public InvestorSowDto updateSow(Long sowId, UpdateSowDto dto) {
        Optional<InvestorSow> sowOpt = sowRepository.findById(sowId);
        if (sowOpt.isEmpty()) {
            throw new RuntimeException("SOW not found");
        }

        InvestorSow sow = sowOpt.get();
        if (!"draft".equals(sow.getStatus())) {
            throw new RuntimeException("Can only update draft SOWs");
        }

        sow.setSowData(convertToJson(dto.getSowData()));
        if (dto.getDigitalSignature() != null) {
            sow.setDigitalSignature(dto.getDigitalSignature());
            sow.setSignedDate(LocalDateTime.now());
        }
        sow.setUpdatedDate(LocalDateTime.now());

        InvestorSow updated = sowRepository.save(sow);
        log.info("Updated SOW {}", sowId);

        return convertToSowDto(updated);
    }

    /**
     * Submit SOW for approval
     */
    @Transactional
    public InvestorSowDto submitSow(Long sowId) {
        Optional<InvestorSow> sowOpt = sowRepository.findById(sowId);
        if (sowOpt.isEmpty()) {
            throw new RuntimeException("SOW not found");
        }

        InvestorSow sow = sowOpt.get();
        if (!"draft".equals(sow.getStatus())) {
            throw new RuntimeException("SOW already submitted");
        }

        if (sow.getDigitalSignature() == null) {
            throw new RuntimeException("Digital signature required");
        }

        sow.setStatus("submitted");
        sow.setSubmittedDate(LocalDateTime.now());

        InvestorSow submitted = sowRepository.save(sow);
        log.info("Submitted SOW {} for approval", sowId);

        return convertToSowDto(submitted);
    }

    /**
     * Approve SOW (admin)
     */
    @Transactional
    public InvestorSowDto approveSow(Long sowId, Long approvedBy) {
        Optional<InvestorSow> sowOpt = sowRepository.findById(sowId);
        if (sowOpt.isEmpty()) {
            throw new RuntimeException("SOW not found");
        }

        InvestorSow sow = sowOpt.get();
        if (!"submitted".equals(sow.getStatus())) {
            throw new RuntimeException("SOW must be submitted for approval");
        }

        sow.setStatus("approved");
        sow.setApprovedDate(LocalDateTime.now());
        sow.setApprovedBy(approvedBy);

        InvestorSow approved = sowRepository.save(sow);
        log.info("Approved SOW {} by user {}", sowId, approvedBy);

        return convertToSowDto(approved);
    }

    /**
     * Reject SOW (admin)
     */
    @Transactional
    public InvestorSowDto rejectSow(Long sowId, String reason) {
        Optional<InvestorSow> sowOpt = sowRepository.findById(sowId);
        if (sowOpt.isEmpty()) {
            throw new RuntimeException("SOW not found");
        }

        InvestorSow sow = sowOpt.get();
        if (!"submitted".equals(sow.getStatus())) {
            throw new RuntimeException("SOW must be submitted to reject");
        }

        sow.setStatus("rejected");
        sow.setRejectionReason(reason);

        InvestorSow rejected = sowRepository.save(sow);
        log.info("Rejected SOW {}: {}", sowId, reason);

        return convertToSowDto(rejected);
    }

    /**
     * Get SOW by ID
     */
    public InvestorSowDto getSow(Long sowId) {
        return sowRepository.findById(sowId)
                .map(this::convertToSowDto)
                .orElse(null);
    }

    /**
     * List all SOWs for investor
     */
    public List<InvestorSowDto> listInvestorSows(Long investorId) {
        return sowRepository.findByInvestorIdOrderByCreatedDateDesc(investorId)
                .stream()
                .map(this::convertToSowDto)
                .collect(Collectors.toList());
    }

    /**
     * Whether the investor has agreed to a Statement of Work — i.e. has an InvestorSow in an
     * agreed state ({@code submitted} or {@code approved}). A {@code draft}, {@code rejected},
     * {@code revoked} or absent SOW counts as NOT agreed. Drives the onboarding-journey gate:
     * the journey only opens once the investor has agreed, and a later revoke closes it again.
     */
    public boolean hasAgreedSow(Long investorId) {
        if (investorId == null) {
            return false;
        }
        return sowRepository.countByInvestorIdAndStatus(investorId, "submitted") > 0
                || sowRepository.countByInvestorIdAndStatus(investorId, "approved") > 0;
    }

    /**
     * Record the investor's agreement to their Statement of Work. Lightweight "I Agree" — creates
     * (or reactivates) an InvestorSow in the {@code submitted} state, which is what the journey
     * gate checks. Idempotent: if an agreed SOW already exists it's returned unchanged.
     */
    @Transactional
    public InvestorSowDto recordAgreement(Long investorId) {
        if (investorId == null) {
            throw new RuntimeException("Investor not resolved");
        }
        // Already agreed → no-op.
        Optional<InvestorSow> agreed = sowRepository.findByInvestorIdOrderByCreatedDateDesc(investorId)
                .stream()
                .filter(s -> "submitted".equals(s.getStatus()) || "approved".equals(s.getStatus()))
                .findFirst();
        if (agreed.isPresent()) {
            return convertToSowDto(agreed.get());
        }

        // Reuse a revoked/rejected/draft row if present, else create fresh.
        InvestorSow sow = sowRepository.findByInvestorIdOrderByCreatedDateDesc(investorId)
                .stream().findFirst().orElseGet(InvestorSow::new);
        sow.setInvestorId(investorId);
        if (sow.getTemplateId() == null) {
            templateRepository.findTopByApplicableForAndIsActiveTrueOrderByCreatedDateDesc("All")
                    .ifPresent(t -> sow.setTemplateId(t.getId()));
        }
        if (sow.getSowData() == null) {
            sow.setSowData("{\"agreed\":true}");
        }
        sow.setStatus("submitted");
        sow.setSubmittedDate(LocalDateTime.now());
        sow.setSignedDate(LocalDateTime.now());
        sow.setRejectionReason(null);
        if (sow.getDigitalSignature() == null) {
            sow.setDigitalSignature("AGREED_VIA_CONSENT_CENTRE");
        }
        if (sow.getCreatedDate() == null) {
            sow.setCreatedDate(LocalDateTime.now());
        }
        InvestorSow saved = sowRepository.save(sow);
        log.info("Recorded SOW agreement for investor {}", investorId);
        return convertToSowDto(saved);
    }

    /**
     * Revoke the investor's SOW agreement — sets any agreed ({@code submitted}/{@code approved})
     * SOW to {@code revoked}, which re-closes the onboarding-journey gate.
     */
    @Transactional
    public InvestorSowDto revokeAgreement(Long investorId) {
        if (investorId == null) {
            throw new RuntimeException("Investor not resolved");
        }
        InvestorSow agreed = sowRepository.findByInvestorIdOrderByCreatedDateDesc(investorId)
                .stream()
                .filter(s -> "submitted".equals(s.getStatus()) || "approved".equals(s.getStatus()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No agreed SOW to revoke"));
        agreed.setStatus("revoked");
        agreed.setUpdatedDate(LocalDateTime.now());
        InvestorSow saved = sowRepository.save(agreed);
        log.info("Revoked SOW agreement for investor {}", investorId);
        return convertToSowDto(saved);
    }

    /**
     * Get SOW data for PDF generation
     */
    public Map<String, Object> getSowDataForPdf(Long sowId) {
        Optional<InvestorSow> sowOpt = sowRepository.findById(sowId);
        if (sowOpt.isEmpty()) {
            return null;
        }

        InvestorSow sow = sowOpt.get();
        Map<String, Object> data = convertFromJson(sow.getSowData());

        // Add metadata
        data.put("sowId", sow.getId());
        data.put("status", sow.getStatus());
        data.put("submittedDate", sow.getSubmittedDate());
        data.put("approvedDate", sow.getApprovedDate());
        data.put("digitalSignature", sow.getDigitalSignature());

        return data;
    }

    // Helper methods

    private SowTemplateDto convertToTemplateDto(SowTemplate template) {
        SowTemplateDto dto = new SowTemplateDto();
        dto.setId(template.getId());
        dto.setName(template.getName());
        dto.setVersion(template.getVersion());
        dto.setApplicableFor(template.getApplicableFor());
        dto.setContent(convertFromJson(template.getContent()));
        dto.setIsActive(template.getIsActive());
        return dto;
    }

    private InvestorSowDto convertToSowDto(InvestorSow sow) {
        InvestorSowDto dto = new InvestorSowDto();
        dto.setId(sow.getId());
        dto.setInvestorId(sow.getInvestorId());
        dto.setTemplateId(sow.getTemplateId());
        dto.setSowData(convertFromJson(sow.getSowData()));
        dto.setStatus(sow.getStatus());
        dto.setSubmittedDate(sow.getSubmittedDate());
        dto.setApprovedDate(sow.getApprovedDate());
        dto.setApprovedBy(sow.getApprovedBy());
        dto.setRejectionReason(sow.getRejectionReason());
        dto.setDigitalSignature(sow.getDigitalSignature());
        dto.setSignedDate(sow.getSignedDate());
        dto.setCreatedDate(sow.getCreatedDate());
        return dto;
    }

    private String convertToJson(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            log.error("Error converting to JSON", e);
            return "{}";
        }
    }

    private Map<String, Object> convertFromJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            log.error("Error parsing JSON", e);
            return Map.of();
        }
    }
}
