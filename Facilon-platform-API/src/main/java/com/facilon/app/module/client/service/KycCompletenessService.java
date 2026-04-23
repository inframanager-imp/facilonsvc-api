package com.facilon.app.module.client.service;

import com.facilon.app.module.client.dto.KycRequirementSlotDto;
import com.facilon.app.module.client.dto.KycRequirementsResponseDto;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.model.KycDocuments;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.module.client.repository.KycDocumentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Flips investor.kyc_verified_at when every mandatory slot in the matrix
 * is both VALID and user-confirmed (confirmed_at IS NOT NULL).
 *
 * Re-runs on every confirm; also clears the flag if a later event
 * invalidates a slot (e.g. an expiry tick flips something to EXPIRED).
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KycCompletenessService {

    private final KycRequirementService requirementService;
    private final KycDocumentsRepository kycDocumentsRepository;
    private final InvestorRepository investorRepository;

    /**
     * Re-evaluate completeness for the investor and update kyc_verified_at.
     * @return true if the gate is currently satisfied, false otherwise.
     */
    public boolean reEvaluate(Investor investor) {
        KycRequirementsResponseDto matrix = requirementService.computeForInvestor(investor);

        boolean allMandatoryReady = true;
        for (KycRequirementSlotDto slot : matrix.getSlots()) {
            if (!"MANDATORY".equals(slot.getRequirement())) continue;
            if (!"VALID".equals(slot.getState())) {
                allMandatoryReady = false;
                break;
            }
            if (slot.getCurrentDocumentId() == null) {
                allMandatoryReady = false;
                break;
            }
            Optional<KycDocuments> doc = kycDocumentsRepository
                    .findByIdAndDeletedAtIsNull(slot.getCurrentDocumentId());
            if (doc.isEmpty() || doc.get().getConfirmedAt() == null) {
                allMandatoryReady = false;
                break;
            }
        }

        boolean changed = false;
        if (allMandatoryReady && investor.getKycVerifiedAt() == null) {
            investor.setKycVerifiedAt(LocalDateTime.now());
            investorRepository.save(investor);
            log.info("KYC verified gate TRIPPED for investor {}", investor.getUniqueCode());
            changed = true;
        } else if (!allMandatoryReady && investor.getKycVerifiedAt() != null) {
            investor.setKycVerifiedAt(null);
            investorRepository.save(investor);
            log.info("KYC verified gate CLEARED for investor {} (a slot is no longer valid+confirmed)",
                    investor.getUniqueCode());
            changed = true;
        }
        return allMandatoryReady;
    }
}
