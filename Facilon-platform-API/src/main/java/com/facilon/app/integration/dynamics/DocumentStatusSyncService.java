package com.facilon.app.integration.dynamics;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.model.Tenant;
import com.facilon.app.module.client.model.IntroInvestorTemp;
import com.facilon.app.module.client.model.KycDocuments;
import com.facilon.app.module.client.repository.IntroInvestorTempRepository;
import com.facilon.app.module.client.repository.KycDocumentsRepository;
import com.facilon.app.repository.TenantRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Syncs document approval/rejection status from Dataverse back to the local
 * {@code kyc_documents} table.
 *
 * <p>Direct mirror of Laravel {@code CheckInvestorDocumentStatus} Artisan command:
 * <ol>
 *   <li>For each investor with local {@code kyc_documents}, query Dataverse
 *       {@code ss_investordocumentses} filtered by today's modifications.</li>
 *   <li>Map {@code ss_documentstatus} codes to status strings
 *       (100000000=Submitted, 100000001=Approved, 100000002=Rejected, 100000003=Sent Back).</li>
 *   <li>Fetch rejection comments from {@code ss_comments}.</li>
 *   <li>Update local {@code kyc_documents.status} and {@code kyc_documents.reason}.</li>
 *   <li>Soft-delete (set {@code deleted_at}) non-Approved records so the investor can re-upload.</li>
 * </ol>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentStatusSyncService {

    private final KycDocumentsRepository kycDocumentsRepository;
    private final IntroInvestorTempRepository introInvestorTempRepository;
    private final TenantRepository tenantRepository;
    private final RestTemplate restTemplate;

    @Autowired(required = false)
    private DynamicsTokenProvider tokenProvider;

    @Autowired(required = false)
    private DynamicsCrmService dynamicsCrmService;

    /**
     * Main entry point: sync document statuses for all investors across all tenants.
     *
     * @return summary map with counts of processed/updated/skipped documents
     */
    @Transactional
    public Map<String, Object> syncAllInvestorDocumentStatuses() {
        int totalProcessed = 0;
        int totalUpdated = 0;
        int totalSkipped = 0;

        if (tokenProvider == null) {
            log.warn("[DocStatusSync] DynamicsTokenProvider not available, skipping sync");
            return Map.of("status", "skipped", "reason", "no token provider");
        }

        // Get all distinct investors who have documents
        List<String> investorUniqueIds = kycDocumentsRepository.findDistinctInvestorUniqueIdsWithNonDeletedDocs();
        log.info("[DocStatusSync] Found {} investors with documents to check", investorUniqueIds.size());

        // Group investors by their IntroInvestorTemp tenant for correct Dynamics credentials
        for (String uniqueCode : investorUniqueIds) {
            try {
                int[] result = processInvestor(uniqueCode);
                totalProcessed += result[0];
                totalUpdated += result[1];
                totalSkipped += result[2];
            } catch (Exception e) {
                log.error("[DocStatusSync] Error processing investor {}: {}", uniqueCode, e.getMessage());
                totalSkipped++;
            }
        }

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("investorsChecked", investorUniqueIds.size());
        summary.put("documentsProcessed", totalProcessed);
        summary.put("documentsUpdated", totalUpdated);
        summary.put("documentsSkipped", totalSkipped);
        return summary;
    }

    /**
     * Process a single investor: look up Dataverse GUIDs, query modified documents, sync status.
     *
     * @return int[3] = {processed, updated, skipped}
     */
    private int[] processInvestor(String uniqueCode) {
        int processed = 0, updated = 0, skipped = 0;

        // Look up IntroInvestorTemp (same fallback pattern as InvestorDocumentService)
        IntroInvestorTemp introInvestor = introInvestorTempRepository.findByUniqueCodeDb(uniqueCode).orElse(null);
        if (introInvestor == null) {
            log.debug("[DocStatusSync] No intro_investor_temp for {}, skipping", uniqueCode);
            return new int[]{0, 0, 1};
        }

        String ssInvestorGuid = introInvestor.getIntroInvestorId();
        String ssBrokerGuid = introInvestor.getSsBrokerValue();
        String ssInvestorTypeGuid = introInvestor.getSsInvestorTypeValue();
        String ssProductGuid = introInvestor.getSsProductValue();

        if (ssInvestorGuid == null || ssInvestorGuid.isBlank()) {
            log.debug("[DocStatusSync] No Dataverse investor GUID for {}, skipping", uniqueCode);
            return new int[]{0, 0, 1};
        }

        // Set tenant context for Dynamics API calls
        Tenant tenant = introInvestor.getTenant();
        if (tenant != null) {
            TenantContextHolder.setTenant(tenant);
        }

        try {
            String token = tokenProvider.getDynamicsToken();
            String baseUrl = tokenProvider.getDynamicsBaseUrl();
            if (token == null || baseUrl == null) {
                log.warn("[DocStatusSync] No Dynamics credentials for investor {}", uniqueCode);
                return new int[]{0, 0, 1};
            }

            // Build OData filter: mirrors Laravel CheckInvestorDocumentStatus lines 67-80
            String today = LocalDate.now(ZoneOffset.UTC).atStartOfDay().atOffset(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ISO_INSTANT);
            String tomorrow = LocalDate.now(ZoneOffset.UTC).plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ISO_INSTANT);

            StringBuilder filter = new StringBuilder("_ss_investor_value eq " + ssInvestorGuid);
            if (ssBrokerGuid != null && !ssBrokerGuid.isBlank()) {
                filter.append(" and _ss_broker_value eq ").append(ssBrokerGuid);
            }
            if (ssInvestorTypeGuid != null && !ssInvestorTypeGuid.isBlank()) {
                filter.append(" and _ss_investortype_value eq ").append(ssInvestorTypeGuid);
            }
            if (ssProductGuid != null && !ssProductGuid.isBlank()) {
                filter.append(" and _ss_product_value eq ").append(ssProductGuid);
            }
            filter.append(" and modifiedon ge ").append(today);
            filter.append(" and modifiedon lt ").append(tomorrow);

            String url = baseUrl + "/ss_investordocumentses?$filter=" + filter;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.set("OData-MaxVersion", "4.0");
            headers.set("OData-Version", "4.0");

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);

            JsonNode body = response.getBody();
            if (body == null || !body.has("value") || !body.get("value").isArray()) {
                return new int[]{0, 0, 0};
            }

            // Get local documents keyed by documentMasterId
            List<KycDocuments> localDocs = kycDocumentsRepository.findByInvestorUniqueIdAndDeletedAtIsNull(uniqueCode);
            Map<String, KycDocuments> localByMasterId = localDocs.stream()
                    .filter(d -> d.getDocumentMasterId() != null)
                    .collect(Collectors.toMap(KycDocuments::getDocumentMasterId, d -> d, (a, b) -> a));

            for (JsonNode node : body.get("value")) {
                processed++;

                String dynamicsId = node.path("ss_investordocumentsid").asText(null);
                int statusCode = node.path("ss_documentstatus").asInt(-1);

                if (dynamicsId == null || statusCode == -1) {
                    skipped++;
                    continue;
                }

                String dvStatus = DynamicsCrmService.DOCUMENT_STATUS_MAP.get(statusCode);
                if (dvStatus == null) {
                    skipped++;
                    continue;
                }

                // Match local document by document_master_id (mirrors Laravel)
                KycDocuments localDoc = localByMasterId.get(dynamicsId);
                if (localDoc == null) {
                    // Also try via repository (the document might have been matched differently)
                    localDoc = kycDocumentsRepository.findByDocumentMasterIdAndDeletedAtIsNull(dynamicsId).orElse(null);
                }

                if (localDoc == null) {
                    skipped++;
                    continue;
                }

                // Fetch comments (rejection reasons) — mirrors Laravel lines 98-114
                if (dynamicsCrmService != null) {
                    String comment = dynamicsCrmService.fetchDocumentComments(dynamicsId);
                    if (comment != null && !comment.isBlank()) {
                        localDoc.setReason(comment);
                    }
                }

                // Update status if changed — mirrors Laravel lines 131-145
                if (!dvStatus.equals(localDoc.getStatus())) {
                    log.info("[DocStatusSync] Investor {}: doc {} status '{}' -> '{}'",
                            uniqueCode, dynamicsId, localDoc.getStatus(), dvStatus);

                    localDoc.setStatus(dvStatus);

                    // Soft-delete if not Approved (allows re-upload)
                    if (!"Approved".equals(dvStatus)) {
                        localDoc.setDeletedAt(LocalDateTime.now());
                    }

                    updated++;
                }

                kycDocumentsRepository.save(localDoc);
            }

        } finally {
            TenantContextHolder.reset();
        }

        return new int[]{processed, updated, skipped};
    }
}
