package com.facilon.app.module.client.service;

import com.facilon.app.module.client.dto.KycRequirementSlotDto;
import com.facilon.app.module.client.dto.KycRequirementsResponseDto;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.model.KycDocuments;
import com.facilon.app.module.client.model.KycValidationStatus;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.module.client.repository.KycDocumentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Computes the per-investor required-document list.
 * Plan: KYC_DOCUMENT_PLAN.md §2 (matrix) and §3.3.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class KycRequirementService {

    static final String DOC_PAN = "PAN_CARD";
    static final String DOC_PASSPORT = "PASSPORT";
    static final String DOC_AADHAAR = "AADHAR_CARD";
    static final String DOC_OCI = "OCI_CARD";
    static final String DOC_ADDRESS = "ADDRESS_PROOF";

    static final String REQ_MANDATORY = "MANDATORY";
    static final String REQ_CONDITIONAL = "CONDITIONAL";
    static final String REQ_OPTIONAL = "OPTIONAL";

    private final InvestorRepository investorRepository;
    private final KycDocumentsRepository kycDocumentsRepository;

    @Value("${kyc.expiry.warn-window-days:30}")
    private int warnWindowDays;

    public KycRequirementsResponseDto computeForUser(Long userId) {
        Investor investor = investorRepository.findByAuthorizedUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Investor not found for user " + userId));
        return computeForInvestor(investor);
    }

    public KycRequirementsResponseDto computeForInvestor(Investor investor) {
        String type = investor.getInvestorType() == null ? "RESIDENT_INDIVIDUAL" : investor.getInvestorType();

        List<KycDocuments> currentDocs =
                kycDocumentsRepository.findCurrentByInvestorUniqueId(investor.getUniqueCode());
        Map<String, KycDocuments> byType = new HashMap<>();
        for (KycDocuments d : currentDocs) {
            if (d.getDocumentType() != null) byType.put(d.getDocumentType(), d);
        }

        boolean usesAadhaarEsign = guessAadhaarEsign(byType);
        boolean usesAadhaarForAddress = guessAadhaarForAddress(byType);

        List<KycRequirementSlotDto> slots = new ArrayList<>();

        switch (normalizeType(type)) {
            case "NRI":
                slots.add(slot(DOC_PAN, REQ_MANDATORY, "PAN card", null, byType));
                slots.add(slot(DOC_PASSPORT, REQ_MANDATORY, "Passport (bio page)", null, byType));
                slots.add(slot(DOC_AADHAAR,
                        usesAadhaarEsign ? REQ_MANDATORY : REQ_CONDITIONAL,
                        "Aadhaar",
                        usesAadhaarEsign ? "Required for Aadhaar e-sign" : "Optional unless using Aadhaar e-sign",
                        byType));
                slots.add(slot(DOC_ADDRESS, REQ_MANDATORY, "Address proof", null, byType));
                break;
            case "OCI":
                slots.add(slot(DOC_PAN, REQ_MANDATORY, "PAN card", null, byType));
                slots.add(slot(DOC_PASSPORT, REQ_MANDATORY, "Passport (bio page)", null, byType));
                slots.add(slot(DOC_OCI, REQ_MANDATORY, "OCI card", null, byType));
                slots.add(slot(DOC_ADDRESS, REQ_MANDATORY, "Address proof", null, byType));
                break;
            case "RESIDENT_INDIVIDUAL":
            default:
                slots.add(slot(DOC_PAN, REQ_MANDATORY, "PAN card", null, byType));
                slots.add(slot(DOC_AADHAAR, REQ_MANDATORY, "Aadhaar", null, byType));
                slots.add(slot(DOC_ADDRESS, REQ_MANDATORY, "Address proof", null, byType));
                break;
        }

        int mandatoryTotal = 0, mandatoryComplete = 0, expired = 0;
        for (KycRequirementSlotDto s : slots) {
            if (REQ_MANDATORY.equals(s.getRequirement())) {
                mandatoryTotal++;
                if ("VALID".equals(s.getState())) mandatoryComplete++;
            }
            if ("EXPIRED".equals(s.getState())) expired++;
        }

        return KycRequirementsResponseDto.builder()
                .investorType(type)
                .usesAadhaarEsign(usesAadhaarEsign)
                .usesAadhaarForAddress(usesAadhaarForAddress)
                .slots(slots)
                .mandatoryTotal(mandatoryTotal)
                .mandatoryComplete(mandatoryComplete)
                .expiredCount(expired)
                .kycVerifiedAt(investor.getKycVerifiedAt())
                .build();
    }

    private KycRequirementSlotDto slot(String docType, String requirement, String label,
                                       String note, Map<String, KycDocuments> byType) {
        KycDocuments existing = byType.get(docType);
        if (existing == null) {
            return KycRequirementSlotDto.builder()
                    .documentType(docType)
                    .requirement(requirement)
                    .state(KycValidationStatus.NOT_UPLOADED.name())
                    .label(label)
                    .note(note)
                    .build();
        }
        String state = deriveState(existing);
        Integer daysUntilExpiry = null;
        if (existing.getExpiryDate() != null) {
            daysUntilExpiry = (int) ChronoUnit.DAYS.between(LocalDate.now(), existing.getExpiryDate());
        }
        return KycRequirementSlotDto.builder()
                .documentType(docType)
                .requirement(requirement)
                .state(state)
                .currentDocumentId(existing.getId())
                .expiryDate(existing.getExpiryDate())
                .daysUntilExpiry(daysUntilExpiry)
                .label(label)
                .note(note)
                .build();
    }

    private String deriveState(KycDocuments d) {
        if (d.getExpiryDate() != null) {
            LocalDate today = LocalDate.now();
            if (d.getExpiryDate().isBefore(today)) return KycValidationStatus.EXPIRED.name();
            if (!d.getExpiryDate().isAfter(today.plusDays(warnWindowDays))) {
                return KycValidationStatus.EXPIRES_SOON.name();
            }
        }
        String vs = d.getValidationStatus();
        // VALID-but-not-yet-confirmed shows as PENDING_REVIEW so the card prompts
        // the investor to click "Looks right" in the review modal.
        if (KycValidationStatus.VALID.name().equals(vs) && d.getConfirmedAt() == null) {
            return KycValidationStatus.PENDING_REVIEW.name();
        }
        if (vs != null && !vs.isBlank()) return vs;
        if ("Approved".equalsIgnoreCase(d.getStatus())) return KycValidationStatus.VALID.name();
        return KycValidationStatus.PENDING_REVIEW.name();
    }

    private boolean guessAadhaarEsign(Map<String, KycDocuments> byType) {
        KycDocuments aadhaar = byType.get(DOC_AADHAAR);
        return aadhaar != null; // any Aadhaar upload implies intent; flag refined in upload UI
    }

    private boolean guessAadhaarForAddress(Map<String, KycDocuments> byType) {
        KycDocuments addr = byType.get(DOC_ADDRESS);
        if (addr == null) return false;
        return Boolean.TRUE.equals(addr.getUsesAadhaarForAddress())
                || "AADHAAR".equalsIgnoreCase(addr.getAddressProofType());
    }

    private String normalizeType(String t) {
        if (t == null) return "RESIDENT_INDIVIDUAL";
        String n = t.trim().toUpperCase(Locale.ROOT);
        if (n.contains("NRI")) return "NRI";
        if (n.contains("OCI")) return "OCI";
        return "RESIDENT_INDIVIDUAL";
    }
}
