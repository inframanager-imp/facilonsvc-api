package com.facilon.app.module.client.service;

import com.facilon.app.integration.storage.AzureBlobStorage;
import com.facilon.app.integration.storage.BlobStorageProperties;
import com.facilon.app.integration.storage.DocumentCipher;
import com.facilon.app.module.client.dto.JourneyKycGateDto;
import com.facilon.app.module.client.dto.KycDocumentRequirementDto;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.model.InvestorJourneyKycConsent;
import com.facilon.app.module.client.model.KycDocuments;
import com.facilon.app.module.client.repository.InvestorJourneyKycConsentRepository;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.module.client.repository.KycDocumentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Per-journey KYC-reuse consent gate (Phase 3).
 *
 * A journey may auto-populate from the investor's confirmed KYC documents only
 * after the investor consents for that specific journey. The gate also reports
 * whether KYC is complete so the UI can route to documents-center first.
 *
 * Plan: implemented_docs/kyc-blob-journey-consent-plan.md.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JourneyKycConsentService {

    static final String CONSENT_VERSION = "1.0";

    private final InvestorRepository investorRepository;
    private final InvestorJourneyKycConsentRepository consentRepository;
    private final KycDocumentsRepository kycDocumentsRepository;
    private final AzureBlobStorage blobStorage;
    private final DocumentCipher documentCipher;
    private final BlobStorageProperties blobProperties;
    private final InvestorDocumentService investorDocumentService;

    @Transactional(readOnly = true)
    public JourneyKycGateDto gate(String investorUniqueCode, String journeyId) {
        Investor investor = requireInvestor(investorUniqueCode);
        boolean kycComplete = investor.getKycVerifiedAt() != null;
        // consentGiven here means "already decided" (Agree OR Skip) - the prompt is
        // shown only when no decision row exists yet, so it is asked exactly once.
        boolean alreadyDecided = journeyId != null && !journeyId.isBlank()
                && consentRepository.existsByInvestorUniqueIdAndJourneyId(investorUniqueCode, journeyId);
        return JourneyKycGateDto.builder()
                .kycComplete(kycComplete)
                .consentGiven(alreadyDecided)
                .build();
    }

    /** "I Agree": records consent (requires KYC complete); caller then archives the docs. */
    @Transactional
    public void recordConsent(String investorUniqueCode, String journeyId, String ip, String userAgent) {
        Investor investor = requireInvestor(investorUniqueCode);
        if (investor.getKycVerifiedAt() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "KYC is not complete; cannot consent to reuse KYC documents for this journey");
        }
        persistDecision(investorUniqueCode, journeyId, true, ip, userAgent);
    }

    /** "Skip for now": records the declined decision so the prompt is not shown again. No archive. */
    @Transactional
    public void recordSkip(String investorUniqueCode, String journeyId, String ip, String userAgent) {
        persistDecision(investorUniqueCode, journeyId, false, ip, userAgent);
    }

    /** First decision wins: a row already present (Agree or Skip) is left untouched. */
    private void persistDecision(String investorUniqueCode, String journeyId,
                                 boolean agreed, String ip, String userAgent) {
        if (journeyId == null || journeyId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "journeyId is required");
        }
        if (consentRepository.existsByInvestorUniqueIdAndJourneyId(investorUniqueCode, journeyId)) {
            return; // idempotent - already decided
        }
        InvestorJourneyKycConsent decision = InvestorJourneyKycConsent.builder()
                .investorUniqueId(investorUniqueCode)
                .journeyId(journeyId)
                .consentGiven(agreed)
                .consentGivenAt(LocalDateTime.now())
                .ipAddress(truncate(ip, 64))
                .userAgent(truncate(userAgent, 512))
                .consentVersion(CONSENT_VERSION)
                .build();
        consentRepository.save(decision); // tenant set by TenantEntity @PrePersist
        log.info("Recorded per-journey KYC decision: investor={} journey={} agreed={}",
                investorUniqueCode, journeyId, agreed);
    }

    /**
     * Archive every confirmed, Blob-stored KYC document that is not yet in SharePoint:
     * download from Blob, decrypt, then hand the bytes to InvestorDocumentService's
     * archive function (same SharePoint + Dataverse process as /investor/documents).
     *
     * NOT transactional and called AFTER {@link #recordConsent} has committed, so
     * SharePoint/Dataverse I/O never holds a DB transaction and a failure here can
     * never roll back the consent. Each doc archives in its own REQUIRES_NEW
     * transaction and is isolated - one failure doesn't abort the rest.
     */
    public void archiveConfirmedKycDocuments(String uniqueCode) {
        if (!blobProperties.isEnabled() || !documentCipher.isConfigured()) {
            return; // nothing stored in Blob to copy
        }
        List<KycDocuments> docs =
                kycDocumentsRepository.findByInvestorUniqueIdAndDeletedAtIsNull(uniqueCode);

        // Resolve the Dataverse document id (documentMasterId) per kyc_documents row
        // once, so the SharePoint archive can run the full Dataverse sync. Best-effort.
        Map<Long, String> masterIdByDocId = resolveDataverseDocumentIds(uniqueCode);

        for (KycDocuments doc : docs) {
            if (doc.getConfirmedAt() == null) continue;                       // only confirmed
            String url = doc.getDocumentUrl();
            if (url == null || !url.startsWith("azureblob://")) continue;     // only Blob-stored
            if (doc.getSharepointUrl() != null && !doc.getSharepointUrl().isBlank()) continue; // already archived
            try {
                byte[] encrypted = blobStorage.retrieve(url);
                byte[] plain = documentCipher.decrypt(encrypted);
                String fileName = firstNonBlank(doc.getOriginalFilename(),
                        firstNonBlank(doc.getDocDescription(), doc.getDocumentType()));
                String masterId = firstNonBlank(masterIdByDocId.get(doc.getId()), doc.getDocumentMasterId());
                investorDocumentService.archiveConfirmedDocToSharePoint(
                        uniqueCode, doc.getId(), plain, fileName, doc.getDocumentType(), masterId);
            } catch (Exception e) {
                log.error("Failed to archive KYC doc {} to SharePoint on consent: {}",
                        doc.getId(), e.getMessage());
            }
        }
    }

    /** Map kyc_documents.id -> Dataverse document id, via the requirements lookup. Best-effort. */
    private Map<Long, String> resolveDataverseDocumentIds(String uniqueCode) {
        Map<Long, String> map = new HashMap<>();
        try {
            KycDocumentRequirementDto req = investorDocumentService.getKycRequirements(uniqueCode);
            if (req != null && req.getDocuments() != null) {
                for (KycDocumentRequirementDto.RequiredDocument rd : req.getDocuments()) {
                    if (rd.getLocalDocumentId() != null && rd.getDynamicsId() != null) {
                        map.put(rd.getLocalDocumentId(), rd.getDynamicsId());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Could not resolve Dataverse document ids for {}: {}", uniqueCode, e.getMessage());
        }
        return map;
    }

    private String firstNonBlank(String a, String b) {
        return (a != null && !a.isBlank()) ? a : b;
    }

    private Investor requireInvestor(String uniqueCode) {
        return investorRepository.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Investor not found"));
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }
}
