package com.facilon.app.integration.dynamics;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.facilon.app.module.client.model.master.MasterCountryOfResidence;
import com.facilon.app.module.client.model.master.MasterInvestorTypes;
import com.facilon.app.module.client.model.master.MasterNationality;
import com.facilon.app.module.client.repository.MasterCountryOfResidenceRepository;
import com.facilon.app.module.client.repository.MasterInvestorTypesRepository;
import com.facilon.app.module.client.repository.MasterNationalityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Dynamics 365 CRM service for investor document operations.
 * Aligns with Laravel's document_submission_show and kyc_document_submit logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicsCrmService {

    private static final Logger log = LoggerFactory.getLogger(DynamicsCrmService.class);
    
    private final RestTemplate restTemplate;
    private final MasterNationalityRepository nationalityRepository;
    private final MasterCountryOfResidenceRepository countryOfResidenceRepository;
    private final MasterInvestorTypesRepository investorTypesRepository;
    
    @Autowired(required = false)
    private DynamicsTokenProvider tokenProvider;

    // ─────────────────────────────────────────────────────────────────────────
    // Investor product-assignment lookup by email  (mirrors Laravel
    //   DataverseController::insert_introduce_investor_details_temp
    //   and investor-details.blade.php Section 3)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Laravel equivalent:
     * <pre>
     *   GET ss_investors?$filter=ss_name eq '{introduce_id}'
     * </pre>
     * We filter by {@code ss_emailintroduceind} (the introduced investor's email)
     * instead, because in the Spring platform the trigger is the investor's login email,
     * not a separate introduce_id URL parameter.
     *
     * <p>Returns the broker GUID, product GUID, plan GUID, and service-provider type
     * that would normally be stored in {@code intro_investor_temp} after the Laravel
     * introduction flow.  The Spring platform can use this to populate the dashboard
     * even when the {@code intro_investor_temp} row is missing.
     */
    public Optional<DataverseInvestorAssignmentDto> fetchInvestorAssignmentByEmail(String email) {
        if (tokenProvider == null || email == null || email.isBlank()) {
            return Optional.empty();
        }
        String token = tokenProvider.getDynamicsToken();
        if (token == null) {
            log.warn("fetchInvestorAssignmentByEmail: no Dynamics token");
            return Optional.empty();
        }
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        if (baseUrl == null) return Optional.empty();

        // OData: escape single quotes in email (e.g. O'Brien → O''Brien)
        String safeEmail = email.trim().replace("'", "''");
        // Explicit $select so lookup columns (_ss_*_value) are returned consistently across environments
        String select = "ss_investorid,ss_emailintroduceind,_ss_broker_value,ss_serviceprovidertype,"
                + "_ss_product_value,_ss_brokerageplan_value,_ss_investorroute_value,_ss_brokerpreferredbank_value,"
                + "_ss_investortype_value,ss_name,_ss_nationality_value";
        URI uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/ss_investors")
                .queryParam("$select", select)
                .queryParam("$filter", "ss_emailintroduceind eq '" + safeEmail + "'")
                .queryParam("$top", "1")
                .build().encode().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");

        try {
            ResponseEntity<JsonNode> resp = restTemplate.exchange(
                    uri, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
            JsonNode body = resp.getBody();
            if (body == null || !body.has("value") || body.get("value").isEmpty()) {
                log.info("fetchInvestorAssignmentByEmail: no ss_investors record for {}", email);
                return Optional.empty();
            }
            JsonNode row = body.get("value").get(0);
            DataverseInvestorAssignmentDto dto = DataverseInvestorAssignmentDto.builder()
                    .ssInvestorId(text(row, "ss_investorid"))
                    .introEmail(text(row, "ss_emailintroduceind"))
                    .ssBrokerValue(firstNonBlank(
                            text(row, "_ss_broker_value"),
                            parseLookupGuidFromBind(text(row, "_ss_broker_value@odata.bind"))))
                    .serviceProviderType(text(row, "ss_serviceprovidertype"))
                    .ssProductValue(firstNonBlank(
                            text(row, "_ss_product_value"),
                            parseLookupGuidFromBind(text(row, "_ss_product_value@odata.bind"))))
                    .ssBrokeragePlanValue(firstNonBlank(
                            text(row, "_ss_brokerageplan_value"),
                            parseLookupGuidFromBind(text(row, "_ss_brokerageplan_value@odata.bind"))))
                    .investRouteValue(firstNonBlank(
                            text(row, "_ss_investorroute_value"),
                            parseLookupGuidFromBind(text(row, "_ss_investorroute_value@odata.bind"))))
                    .brokerPreferredBank(firstNonBlank(
                            text(row, "_ss_brokerpreferredbank_value"),
                            parseLookupGuidFromBind(text(row, "_ss_brokerpreferredbank_value@odata.bind"))))
                    .ssInvestorTypeValue(firstNonBlank(
                            text(row, "_ss_investortype_value"),
                            parseLookupGuidFromBind(text(row, "_ss_investortype_value@odata.bind"))))
                    .introDvInvestorSsId(text(row, "ss_name"))
                    .introDvNationality(firstNonBlank(
                            text(row, "_ss_nationality_value"),
                            parseLookupGuidFromBind(text(row, "_ss_nationality_value@odata.bind"))))
                    .build();
            log.info("fetchInvestorAssignmentByEmail: found broker={} product={} for {}",
                    dto.getSsBrokerValue(), dto.getSsProductValue(), email);
            return Optional.of(dto);
        } catch (Exception e) {
            log.warn("fetchInvestorAssignmentByEmail failed for {}: {}", email, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Exact replication of Laravel blade (investor-details-home.blade.php lines 217–275):
     *
     * <pre>
     * Step 1:
     *   service_provider_type == '100000000' (Broker)?
     *     GET ss_brokers?$filter=ss_brokerid eq '{brokerGuid}'
     *   else (PMS):
     *     GET ss_portfoliomanagers?$filter=ss_portfoliomanagerid eq '{brokerGuid}'
     *   → extract _ss_nameofthefirm_value  (= account GUID, e.g. "abc-123-...")
     *
     * Step 2:
     *   SELECT name FROM master_accounts WHERE accountid = '{firmAccountGuid}'
     *   (Laravel reads local DB; we call Dataverse GET accounts(guid) as live fallback)
     *   → returns display name, e.g. "Facilon Matrix Broker"
     * </pre>
     *
     * <p><b>URL encoding note:</b> OData single-entity path {@code accounts(guid)} uses
     * parentheses which are valid URL path characters (RFC 3986 sub-delimiters) but are
     * encoded by {@code UriComponentsBuilder.encode()} as {@code %28/%29}, breaking the
     * request. We use {@code URI.create()} directly for the accounts endpoint.
     */
    public Optional<String> fetchServiceProviderName(String providerGuid, String serviceProviderType) {
        if (tokenProvider == null || providerGuid == null || providerGuid.isBlank()) {
            return Optional.empty();
        }
        String token = tokenProvider.getDynamicsToken();
        if (token == null) return Optional.empty();
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        if (baseUrl == null) return Optional.empty();

        boolean isBroker = "100000000".equals(
                serviceProviderType != null ? serviceProviderType.trim() : "");
        String entitySet = isBroker ? "ss_brokers" : "ss_portfoliomanagers";
        String idField   = isBroker ? "ss_brokerid" : "ss_portfoliomanagerid";
        String guid = providerGuid.trim().replaceAll("^\\{|\\}$", "");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");

        // ── Step 1: GET ss_brokers or ss_portfoliomanagers by broker GUID ─────
        // Blade: GET ss_brokers?$filter=ss_brokerid eq '{guid}'
        URI step1Uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/" + entitySet)
                .queryParam("$filter", idField + " eq '" + guid + "'")
                .queryParam("$select", idField + ",_ss_nameofthefirm_value,ss_name")
                .build().encode().toUri();

        String firmAccountGuid = null;
        String brokerSsName    = null;

        try {
            ResponseEntity<JsonNode> resp = restTemplate.exchange(
                    step1Uri, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
            JsonNode body = resp.getBody();
            if (body != null && body.has("value") && !body.get("value").isEmpty()) {
                JsonNode row = body.get("value").get(0);
                firmAccountGuid = text(row, "_ss_nameofthefirm_value"); // account GUID
                brokerSsName    = text(row, "ss_name");                 // e.g. "FSP202510163839"
                log.debug("[fetchServiceProviderName] {} broker={} firmAccountGuid={} ssName={}",
                        entitySet, guid, firmAccountGuid, brokerSsName);
            }
        } catch (Exception e) {
            log.warn("[fetchServiceProviderName] step1 failed for {} {}: {}", entitySet, guid, e.getMessage());
        }

        if (firmAccountGuid == null || firmAccountGuid.isBlank()) {
            // _ss_nameofthefirm_value not in broker record → can't resolve firm name
            log.warn("[fetchServiceProviderName] no _ss_nameofthefirm_value for broker {}. " +
                     "Falling back to broker ss_name={}", guid, brokerSsName);
            return Optional.ofNullable(brokerSsName);
        }

        // ── Step 2: Resolve account name from firmAccountGuid ─────────────────
        // Blade: SELECT name FROM master_accounts WHERE accountid = '{firmAccountGuid}'
        // We call Dataverse GET accounts(guid) directly so it works even when
        // master_accounts table is not yet synced.
        //
        // IMPORTANT: OData single-entity URL uses parentheses: accounts(guid)
        // Do NOT use UriComponentsBuilder.encode() — it encodes ( → %28, ) → %29.
        String cleanFirmGuid = firmAccountGuid.trim().replaceAll("^\\{|\\}$", "");
        String accountUrl = baseUrl + "/accounts(" + cleanFirmGuid + ")?$select=name,accountid";

        try {
            ResponseEntity<JsonNode> acctResp = restTemplate.exchange(
                    URI.create(accountUrl), HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
            JsonNode acctBody = acctResp.getBody();
            if (acctBody != null) {
                String name = text(acctBody, "name");
                if (name != null && !name.isBlank()) {
                    log.info("[fetchServiceProviderName] resolved: broker={} → firmGuid={} → name={}",
                            guid, cleanFirmGuid, name);
                    return Optional.of(name);  // ← "Facilon Matrix Broker"
                }
            }
        } catch (Exception e) {
            log.warn("[fetchServiceProviderName] step2 accounts({}) failed: {}", cleanFirmGuid, e.getMessage());
        }

        // Final fallback: return firmAccountGuid so caller can try master_accounts lookup
        log.warn("[fetchServiceProviderName] accounts({}) call failed. Returning firmGuid as fallback.", cleanFirmGuid);
        return Optional.of(firmAccountGuid);
    }

    /**
     * Laravel {@code DataverseController::fetch_products_from_dv}:
     * <pre>
     *   GET ss_products   →  ss_productid, ss_name
     * </pre>
     * Here we filter by the specific product GUID so we get exactly one row.
     */
    public Optional<String> fetchProductName(String productGuid) {
        if (tokenProvider == null || productGuid == null || productGuid.isBlank()) {
            return Optional.empty();
        }
        String token = tokenProvider.getDynamicsToken();
        if (token == null) return Optional.empty();
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        if (baseUrl == null) return Optional.empty();

        String guid = productGuid.trim().replaceAll("^\\{|\\}$", "");
        URI uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/ss_products")
                .queryParam("$filter", "ss_productid eq '" + guid + "'")
                .queryParam("$select", "ss_productid,ss_name")
                .build().encode().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");

        try {
            ResponseEntity<JsonNode> resp = restTemplate.exchange(
                    uri, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
            JsonNode body = resp.getBody();
            if (body == null || !body.has("value") || body.get("value").isEmpty()) {
                return Optional.empty();
            }
            String name = text(body.get("value").get(0), "ss_name");
            return name != null && !name.isBlank() ? Optional.of(name) : Optional.empty();
        } catch (Exception e) {
            log.warn("fetchProductName failed for {}: {}", guid, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Laravel {@code DataverseController::fetch_plans_from_dv} (ss_plans)
     * and {@code fetch_plans_from_dv} second call (ss_portfoliomanagerplans).
     * We try ss_plans first, then ss_portfoliomanagerplans (PMS plans).
     */
    public Optional<String> fetchPlanName(String planGuid) {
        if (tokenProvider == null || planGuid == null || planGuid.isBlank()) {
            return Optional.empty();
        }
        String token = tokenProvider.getDynamicsToken();
        if (token == null) return Optional.empty();
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        if (baseUrl == null) return Optional.empty();

        String guid = planGuid.trim().replaceAll("^\\{|\\}$", "");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");

        // Try ss_plans first (broker plans)
        for (String[] entityInfo : new String[][]{
                {"ss_plans", "ss_planid"},
                {"ss_portfoliomanagerplans", "ss_portfoliomanagerplanid"}}) {
            String entitySet = entityInfo[0];
            String idField   = entityInfo[1];
            URI uri = UriComponentsBuilder
                    .fromHttpUrl(baseUrl + "/" + entitySet)
                    .queryParam("$filter", idField + " eq '" + guid + "'")
                    .queryParam("$select", idField + ",ss_name")
                    .build().encode().toUri();
            try {
                ResponseEntity<JsonNode> resp = restTemplate.exchange(
                        uri, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
                JsonNode body = resp.getBody();
                if (body != null && body.has("value") && !body.get("value").isEmpty()) {
                    String name = text(body.get("value").get(0), "ss_name");
                    if (name != null && !name.isBlank()) {
                        return Optional.of(name);
                    }
                }
            } catch (Exception e) {
                log.debug("fetchPlanName tried {} for {}: {}", entitySet, guid, e.getMessage());
            }
        }
        return Optional.empty();
    }

    // ── private helper ─────────────────────────────────────────────────────────
    private static String text(JsonNode node, String field) {
        if (node == null || !node.has(field) || node.get(field).isNull()) return null;
        String v = node.get(field).asText("");
        return v.isBlank() ? null : v;
    }

    /** Prefer first non-blank string (Dataverse may expose lookup as raw GUID or @odata.bind URL). */
    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) {
            return a;
        }
        if (b != null && !b.isBlank()) {
            return b;
        }
        return null;
    }

    /**
     * Parses GUID from OData bind string, e.g. {@code /ss_brokers(67018359-762d-ef11-8e50-6045bdaad4ff)}.
     */
    private static String parseLookupGuidFromBind(String bindValue) {
        if (bindValue == null || bindValue.isBlank()) {
            return null;
        }
        int open = bindValue.lastIndexOf('(');
        int close = bindValue.lastIndexOf(')');
        if (open >= 0 && close > open) {
            String inner = bindValue.substring(open + 1, close).trim();
            return inner.isBlank() ? null : inner;
        }
        return null;
    }

    /**
     * Discover the actual schema of ss_investordocumentses entity.
     * This queries the metadata to see what fields actually exist.
     */
    public void discoverInvestorDocumentsSchema() {
        if (tokenProvider == null) {
            log.error("❌ Token provider is NULL");
            return;
        }
        
        String token = tokenProvider.getDynamicsToken();
        if (token == null) {
            log.error("❌ Token is NULL");
            return;
        }
        
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        if (baseUrl == null) {
            log.error("❌ Base URL is NULL");
            return;
        }
        
        // Query metadata for the entity
        String metadataUrl = baseUrl + "/$metadata#EntityDefinitions";
        log.info("🔍 Discovering schema: {}", metadataUrl);
        
        // Also try getting one record without filters to see what fields come back
        String sampleUrl = baseUrl + "/ss_investordocumentses?$top=1";
        log.info("🔍 Getting sample record: {}", sampleUrl);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");
        headers.set("Prefer", "odata.include-annotations=*");
        
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    sampleUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    JsonNode.class
            );
            
            log.info("✅ Sample response status: {}", response.getStatusCode());
            if (response.getBody() != null) {
                log.info("📄 Sample record structure:");
                log.info(response.getBody().toPrettyString());
            }
        } catch (Exception e) {
            log.error("❌ Failed to get sample: {} - {}", e.getClass().getSimpleName(), e.getMessage());
        }
    }
    
    /**
     * Fetch KYC document requirements from {@code ss_investordocumentses}.
     *
     * <p>Exact replication of Laravel {@code InnerPageController::document_submission_show} (lines 1201–1210):
     * <pre>
     *   $filter = "_ss_investor_value eq {intro_investorid} " .            ← ss_investorid GUID (NOT ss_name!)
     *       "and _ss_broker_value eq {ss_broker_value} " .
     *       "and _ss_investortype_value eq {ss_investortype_value} " .
     *       "and _ss_product_value eq {ss_product_value} " .
     *       "and (ss_documentcategory eq 100000005 or ss_documentcategory eq 100000000)";
     *   GET ss_investordocumentses?$filter={filter}
     * </pre>
     *
     * @param ssInvestorGuid Dataverse {@code ss_investorid} GUID from {@code intro_investor_temp.intro_investorid}
     * @param ssBrokerGuid   Broker/PMS GUID from {@code intro_investor_temp.ss_broker_value}
     * @param ssInvestorTypeGuid Investor type GUID from {@code intro_investor_temp.ss_investortype_value}
     * @param ssProductGuid  Product GUID from {@code intro_investor_temp.ss_product_value}
     */
    public List<Map<String, Object>> getInvestorDocumentRequirements(
            String ssInvestorGuid, String ssBrokerGuid, String ssInvestorTypeGuid, String ssProductGuid) {
        log.info("📡 getInvestorDocumentRequirements: investorGuid={}, broker={}, type={}, product={}", 
                ssInvestorGuid, ssBrokerGuid, ssInvestorTypeGuid, ssProductGuid);
        
        if (tokenProvider == null) {
            log.error("❌ Dynamics token provider is NULL - bean not loaded");
            return new ArrayList<>();
        }
        
        String token = tokenProvider.getDynamicsToken();
        if (token == null) {
            log.error("❌ Dynamics token is NULL - check config and credentials");
            return new ArrayList<>();
        }

        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        if (baseUrl == null) {
            log.error("❌ Dynamics base URL is NULL");
            return new ArrayList<>();
        }
        
        // Laravel document_submission_show (line 1201–1206): ALL lookup fields are raw GUIDs without quotes
        // OData lookup filters (_field_value) compare against GUID strings
        StringBuilder odataFilter = new StringBuilder("_ss_investor_value eq " + ssInvestorGuid);
        if (ssBrokerGuid != null && !ssBrokerGuid.isBlank()) {
            odataFilter.append(" and _ss_broker_value eq ").append(ssBrokerGuid);
        }
        if (ssInvestorTypeGuid != null && !ssInvestorTypeGuid.isBlank()) {
            odataFilter.append(" and _ss_investortype_value eq ").append(ssInvestorTypeGuid);
        }
        if (ssProductGuid != null && !ssProductGuid.isBlank()) {
            odataFilter.append(" and _ss_product_value eq ").append(ssProductGuid);
        }
        odataFilter.append(" and (ss_documentcategory eq 100000005 or ss_documentcategory eq 100000000)");

        String filter = "ss_investordocumentses?$filter=" + odataFilter;

        String url = baseUrl + "/" + filter;
        log.info("🌐 Calling Dataverse API: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");
        headers.set("Prefer", "odata.include-annotations=*");

        try {
            log.debug("Sending GET request to Dataverse...");
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    JsonNode.class
            );

            log.info("✅ Dataverse API response: status={}", response.getStatusCode());
            
            if (response.getBody() != null && response.getBody().has("value")) {
                JsonNode value = response.getBody().get("value");
                log.info("📄 Dataverse returned {} document records", value.size());
                
                List<Map<String, Object>> documents = new ArrayList<>();
                Map<String, Boolean> documentMasterCache = new HashMap<>();
                for (JsonNode doc : value) {
                    Map<String, Object> docMap = new HashMap<>();
                    
                    // Use actual Dataverse field names from schema discovery
                    String investorDocId = doc.path("ss_investordocumentsid").asText();
                    String documentMasterId = doc.path("_ss_documentmaster_value").asText();
                    String description = doc.path("ss_description").asText(); // Readable name!
                    String recordName = doc.path("ss_name").asText(); // Autonumber
                    boolean verificationDone = doc.path("ss_verification_done").asBoolean(false);
                    int documentCategory = doc.path("ss_documentcategory").asInt(-1);

                    // Match Laravel document_submission_show behavior:
                    // - keep all 100000005 (KYC) records
                    // - for 100000000, keep only if document master has ss_onboardingdocument = false
                    if (documentCategory == 100000000) {
                        if (documentMasterId == null || documentMasterId.isBlank()) {
                            continue;
                        }
                        boolean include = documentMasterCache.computeIfAbsent(
                                documentMasterId,
                                masterId -> isKycDocumentMaster(token, baseUrl, masterId)
                        );
                        if (!include) {
                            continue;
                        }
                    } else if (documentCategory != 100000005) {
                        continue;
                    }
                    
                    // Fallback if description is empty: use record name
                    String displayName = (description != null && !description.trim().isEmpty()) 
                            ? description 
                            : recordName;
                    
                    docMap.put("ss_investordocumentsid", investorDocId);
                    docMap.put("ss_documentmasterid", documentMasterId);
                    docMap.put("ss_name", displayName); // Use ss_description or fallback to ss_name
                    docMap.put("ss_verification_done", verificationDone);
                    documents.add(docMap);
                    
                    log.info("  📄 Document: '{}' (id: {}, record: {}, masterid: {})", 
                            displayName, investorDocId, recordName, documentMasterId);
                }
                log.info("✅ Successfully parsed {} documents from Dataverse", documents.size());
                return documents;
            } else {
                log.warn("⚠️ Dataverse response has no 'value' field or is empty");
            }
        } catch (Exception e) {
            log.error("❌ Failed to fetch document requirements from Dynamics: {} - {}", 
                    e.getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        }

        log.warn("⚠️ Returning empty list from Dataverse call");
        return new ArrayList<>();
    }

    /**
     * Fetch onboarding document requirements from {@code ss_investordocumentses}.
     * Same as {@link #getInvestorDocumentRequirements} but filters for onboarding category (100000001).
     */
    public List<Map<String, Object>> getInvestorOnboardingDocuments(
            String ssInvestorGuid, String ssBrokerGuid, String ssInvestorTypeGuid, String ssProductGuid) {
        log.info("📡 getInvestorOnboardingDocuments: investorGuid={}, broker={}, type={}, product={}", 
                ssInvestorGuid, ssBrokerGuid, ssInvestorTypeGuid, ssProductGuid);
        
        if (tokenProvider == null) {
            log.warn("Dynamics token provider not available");
            return new ArrayList<>();
        }

        String token = tokenProvider.getDynamicsToken();
        if (token == null) {
            log.warn("Dynamics token not available");
            return new ArrayList<>();
        }

        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        
        // Laravel onboarding_documents_show filter (same pattern as KYC):
        //   $filter = "_ss_investor_value eq {intro_investorid} and _ss_broker_value eq {broker} ..."
        StringBuilder odataFilter = new StringBuilder("_ss_investor_value eq " + ssInvestorGuid);
        if (ssBrokerGuid != null && !ssBrokerGuid.isBlank()) {
            odataFilter.append(" and _ss_broker_value eq ").append(ssBrokerGuid);
        }
        if (ssInvestorTypeGuid != null && !ssInvestorTypeGuid.isBlank()) {
            odataFilter.append(" and _ss_investortype_value eq ").append(ssInvestorTypeGuid);
        }
        if (ssProductGuid != null && !ssProductGuid.isBlank()) {
            odataFilter.append(" and _ss_product_value eq ").append(ssProductGuid);
        }
        odataFilter.append(" and (ss_documentcategory eq 100000001 or ss_documentcategory eq 100000000)");

        String filter = "ss_investordocumentses?$filter=" + odataFilter;
        
        String url = baseUrl + "/" + filter;
        log.info("🔗 Dataverse Onboarding Query URL: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    JsonNode.class
            );

            log.info("✅ Dataverse response status: {}", response.getStatusCode());

            JsonNode body = response.getBody();
            if (body != null && body.has("value") && body.get("value").isArray()) {
                List<Map<String, Object>> documents = new ArrayList<>();
                Map<String, String> documentMasterUrlCache = new HashMap<>(); // Cache master URLs
                JsonNode value = body.get("value");

                log.info("📦 Processing {} onboarding documents from Dataverse", value.size());

                for (JsonNode node : value) {
                    Map<String, Object> docMap = new HashMap<>();
                    
                    String investorDocId = node.has("ss_investordocumentsid") ? node.get("ss_investordocumentsid").asText() : "";
                    String recordName = node.has("ss_name") ? node.get("ss_name").asText() : "";
                    String description = node.has("ss_description") ? node.get("ss_description").asText() : "";
                    String documentMasterId = node.has("_ss_documentmaster_value") ? node.get("_ss_documentmaster_value").asText() : "";
                    Integer documentType = node.has("ss_documenttype") ? node.get("ss_documenttype").asInt() : null;
                    int documentCategory = node.path("ss_documentcategory").asInt(-1);
                    
                    String displayName = (description != null && !description.trim().isEmpty()) ? description : recordName;

                    // Match Laravel behavior:
                    // - keep all 100000001 (onboarding) records
                    // - for 100000000, keep only if document master has ss_onboardingdocument = true
                    // - fetch and cache ss_documenturl from document master for download links
                    String documentMasterUrl = null;
                    if (documentCategory == 100000000) {
                        if (documentMasterId == null || documentMasterId.isBlank()) {
                            continue;
                        }
                        // Fetch document master URL (Laravel lines 1553-1566)
                        documentMasterUrl = documentMasterUrlCache.computeIfAbsent(
                                documentMasterId,
                                masterId -> fetchDocumentMasterUrl(token, baseUrl, masterId, true) // true = onboarding
                        );
                        if (documentMasterUrl == null) {
                            // Document master not found or ss_onboardingdocument != true
                            continue;
                        }
                    } else if (documentCategory != 100000001) {
                        continue;
                    }
                    
                    docMap.put("ss_investordocumentsid", investorDocId);
                    docMap.put("ss_name", recordName);
                    docMap.put("ss_description", displayName);
                    docMap.put("_ss_documentmaster_value", documentMasterId);
                    
                    if (documentType != null) {
                        docMap.put("ss_documenttype", documentType);
                    }
                    
                    // Add document master URL for download link (Laravel: $data12['value'][$key]['ss_doc_master_url'])
                    if (documentMasterUrl != null) {
                        docMap.put("ss_doc_master_url", documentMasterUrl);
                    }
                    
                    documents.add(docMap);
                    
                    log.info("  📄 Onboarding Document: '{}' (id: {}, type: {}, masterid: {}, masterUrl: {})", 
                            displayName, investorDocId, documentType, documentMasterId, documentMasterUrl != null ? "YES" : "NO");
                }
                log.info("✅ Successfully parsed {} onboarding documents from Dataverse", documents.size());
                return documents;
            } else {
                log.warn("⚠️ Dataverse response has no 'value' field or is empty");
            }
        } catch (Exception e) {
            log.error("❌ Failed to fetch onboarding requirements from Dynamics: {} - {}", 
                    e.getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        }

        log.warn("⚠️ Returning empty list from Dataverse onboarding call");
        return new ArrayList<>();
    }

    private boolean isKycDocumentMaster(String token, String baseUrl, String masterId) {
        String url = String.format(
                "%s/ss_documentmasters?$filter=ss_documentmasterid eq '%s' and ss_onboardingdocument eq false",
                baseUrl,
                masterId
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    JsonNode.class
            );
            JsonNode body = response.getBody();
            return body != null && body.has("value") && body.get("value").isArray() && body.get("value").size() > 0;
        } catch (Exception e) {
            log.warn("Failed to verify ss_documentmaster KYC flag for {}: {}", masterId, e.getMessage());
            return false;
        }
    }

    private boolean isOnboardingDocumentMaster(String token, String baseUrl, String masterId) {
        String url = String.format(
                "%s/ss_documentmasters?$filter=ss_documentmasterid eq '%s' and ss_onboardingdocument eq true",
                baseUrl,
                masterId
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    JsonNode.class
            );
            JsonNode body = response.getBody();
            return body != null && body.has("value") && body.get("value").isArray() && body.get("value").size() > 0;
        } catch (Exception e) {
            log.warn("Failed to verify ss_documentmaster onboarding flag for {}: {}", masterId, e.getMessage());
            return false;
        }
    }

    /**
     * Fetch document master URL for downloadable templates.
     * Laravel equivalent (InnerPageController.php lines 1553-1566):
     * Queries ss_documentmasters for ss_documenturl where ss_onboardingdocument = true
     * 
     * @param token Dataverse access token
     * @param baseUrl Dataverse base URL
     * @param masterId Document master ID (_ss_documentmaster_value)
     * @param isOnboarding If true, filter for ss_onboardingdocument = true; if false, filter for false
     * @return Document URL (ss_documenturl) or null if not found/invalid
     */
    private String fetchDocumentMasterUrl(String token, String baseUrl, String masterId, boolean isOnboarding) {
        if (masterId == null || masterId.isBlank()) {
            return null;
        }

        String url = String.format(
                "%s/ss_documentmasters?$filter=ss_documentmasterid eq '%s' and ss_onboardingdocument eq %s&$select=ss_documenturl",
                baseUrl,
                masterId,
                isOnboarding ? "true" : "false"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    JsonNode.class
            );
            
            JsonNode body = response.getBody();
            if (body != null && body.has("value") && body.get("value").isArray()) {
                JsonNode valueArray = body.get("value");
                if (valueArray.size() > 0) {
                    JsonNode firstDoc = valueArray.get(0);
                    if (firstDoc.has("ss_documenturl")) {
                        String documentUrl = firstDoc.get("ss_documenturl").asText();
                        if (documentUrl != null && !documentUrl.trim().isEmpty()) {
                            log.debug("✅ Fetched document master URL for {}: {}", masterId, documentUrl);
                            return documentUrl;
                        }
                    }
                }
            }
            log.debug("⚠️ No document master URL found for {}", masterId);
            return null;
        } catch (Exception e) {
            log.warn("❌ Failed to fetch document master URL for {}: {}", masterId, e.getMessage());
            return null;
        }
    }

    /**
     * Fetch ss_documentmasters (document master list) for dropdown options.
     * Aligns with Laravel:
     * $allMasterDocuments = $httpHelper->callOrgAPI('ss_documentmasters?...');
     */
    public List<Map<String, Object>> getDocumentMasters() {
        if (tokenProvider == null) {
            log.warn("Dynamics token provider not available");
            return new ArrayList<>();
        }

        String token = tokenProvider.getDynamicsToken();
        if (token == null) {
            log.warn("Dynamics token not available");
            return new ArrayList<>();
        }

        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        String url = baseUrl + "/ss_documentmasters?$select=ss_documentmasterid,ss_name";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    JsonNode.class
            );

            if (response.getBody() != null && response.getBody().has("value")) {
                JsonNode value = response.getBody().get("value");
                List<Map<String, Object>> masters = new ArrayList<>();
                for (JsonNode master : value) {
                    Map<String, Object> masterMap = new HashMap<>();
                    masterMap.put("ss_documentmasterid", master.path("ss_documentmasterid").asText());
                    masterMap.put("ss_name", master.path("ss_name").asText());
                    masters.add(masterMap);
                }
                return masters;
            }
        } catch (Exception e) {
            log.error("Failed to fetch document masters from Dynamics: {}", e.getMessage());
        }

        return new ArrayList<>();
    }

    /**
     * Update (PATCH) an ss_investordocuments record in Dynamics after upload.
     * Aligns with Laravel:
     * $patchData = ['ss_filelocation' => $webUrlEncoded, 'ss_verification_done' => true, ...];
     * $httpHelper->callOrgAPI('ss_investordocumentses(...)', 'PATCH', json_encode($patchData));
     */
    public void updateInvestorDocumentRecord(String investorDocumentsId, Map<String, Object> updates) {
        if (tokenProvider == null) {
            log.warn("Dynamics token provider not available");
            return;
        }

        String token = tokenProvider.getDynamicsToken();
        if (token == null) {
            log.warn("Dynamics token not available");
            return;
        }

        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        String url = baseUrl + "/ss_investordocumentses(" + investorDocumentsId + ")";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");

        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(updates, headers);
            restTemplate.exchange(url, HttpMethod.PATCH, request, Void.class);
            log.info("Updated Dynamics record: {}", investorDocumentsId);
        } catch (Exception e) {
            log.error("Failed to update Dynamics record: {}", e.getMessage());
        }
    }

    /**
     * Update user_personal_information verification status in Dynamics contact.
     * Aligns with Laravel: updating ss_verification_done in user_personal_information after KYC submit.
     */
    public void updateContactVerificationStatus(String contactId, boolean verified) {
        if (tokenProvider == null) {
            log.warn("Dynamics token provider not available");
            return;
        }

        String token = tokenProvider.getDynamicsToken();
        if (token == null) {
            log.warn("Dynamics token not available");
            return;
        }

        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        String url = baseUrl + "/contacts(" + contactId + ")";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");

        Map<String, Object> updates = new HashMap<>();
        updates.put("ss_verification_done", verified);

        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(updates, headers);
            restTemplate.exchange(url, HttpMethod.PATCH, request, Void.class);
            log.info("Updated contact verification status: {}", contactId);
        } catch (Exception e) {
            log.error("Failed to update contact verification: {}", e.getMessage());
        }
    }

    /**
     * Laravel {@code investor-details.blade.php} Section 3 (My Selection): GET {@code ss_brokers} or
     * {@code ss_portfoliomanagers} by id, then read {@code _ss_nameofthefirm_value} (account GUID for
     * {@code master_accounts.accountid}).
     *
     * @param providerEntityId   {@code ss_broker_value} from {@code intro_investor_temp} (broker or PM GUID)
     * @param serviceProviderType Laravel uses {@code 100000000} for broker; other values use portfolio managers
     */
    public Optional<String> fetchFirmAccountLookupGuid(String providerEntityId, String serviceProviderType) {
        if (tokenProvider == null || providerEntityId == null || providerEntityId.isBlank()) {
            return Optional.empty();
        }
        String token = tokenProvider.getDynamicsToken();
        if (token == null) {
            log.debug("Skipping firm lookup: no Dynamics token");
            return Optional.empty();
        }
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        if (baseUrl == null) {
            return Optional.empty();
        }

        String guid = providerEntityId.trim().replaceAll("^\\{|\\}$", "");
        boolean isBroker = "100000000".equals(serviceProviderType != null ? serviceProviderType.trim() : null);
        String entitySet = isBroker ? "ss_brokers" : "ss_portfoliomanagers";
        String idField = isBroker ? "ss_brokerid" : "ss_portfoliomanagerid";
        String filter = " " + idField + " eq '" + guid + "'";

        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/" + entitySet)
                .queryParam("$filter", filter)
                .build()
                .encode()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    JsonNode.class
            );
            JsonNode body = response.getBody();
            if (body == null || !body.has("value") || !body.get("value").isArray()
                    || body.get("value").isEmpty()) {
                return Optional.empty();
            }
            JsonNode row = body.get("value").get(0);
            if (row == null || !row.has("_ss_nameofthefirm_value")) {
                return Optional.empty();
            }
            String lookup = row.get("_ss_nameofthefirm_value").asText("");
            if (lookup == null || lookup.isBlank()) {
                return Optional.empty();
            }
            log.info("Dataverse {} firm lookup for {}: {}", entitySet, guid, lookup);
            return Optional.of(lookup.trim());
        } catch (Exception e) {
            log.warn("fetchFirmAccountLookupGuid failed for {} / {}: {}", entitySet, guid, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Update investor document URL and status in Dataverse after upload (aligns with Laravel).
     * PATCH ss_investordocumentses({documentMasterId}) with ss_documenturl + ss_documentstatus.
     */
    public void updateInvestorDocumentUrl(String documentMasterId, String documentUrl) {
        log.info("Updating Dataverse ss_investordocumentses({}) with documentUrl", documentMasterId);
        
        if (tokenProvider == null) {
            log.warn("tokenProvider is null, skipping Dataverse update");
            return;
        }
        
        String accessToken = tokenProvider.getDynamicsToken();
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        
        if (accessToken == null || baseUrl == null) {
            log.warn("Dynamics config not available, skipping document URL update");
            return;
        }
        
        // Direct URI construction to avoid encoding issues
        URI uri = URI.create(baseUrl + "/ss_investordocumentses(" + documentMasterId + ")");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");
        
        Map<String, Object> patchData = new java.util.HashMap<>();
        patchData.put("ss_documenturl", documentUrl);
        patchData.put("ss_documentstatus", 100000000); // Submitted (Laravel uses this value)
        
        try {
            restTemplate.exchange(
                    uri,
                    HttpMethod.PATCH,
                    new HttpEntity<>(patchData, headers),
                    Void.class
            );
            log.info("✅ Successfully updated ss_investordocumentses({}) in Dataverse", documentMasterId);
        } catch (Exception e) {
            log.error("❌ Failed to update Dataverse document record: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update Dataverse document URL: " + e.getMessage());
        }
    }

    /**
     * PATCH {@code ss_investors({investorGuid})} to mark personal details as filled.
     * Mirrors Laravel {@code InnerPageController.investor_details_final_submit} lines 4853-4861:
     *
     * <pre>
     *   PATCH /ss_investors({investorId})
     *   {
     *     "ss_investorpersonaldetailsfilled": true
     *   }
     * </pre>
     *
     * Non-fatal — logs and swallows errors so the rest of the final-submit flow can complete.
     */
    public void updateInvestorPersonalDetailsFilled(String investorGuid) {
        if (investorGuid == null || investorGuid.isBlank()) {
            log.warn("updateInvestorPersonalDetailsFilled: investorGuid is null/blank, skipping");
            return;
        }
        log.info("Updating Dataverse ss_investors({}) — ss_investorpersonaldetailsfilled=true", investorGuid);

        if (tokenProvider == null) {
            log.warn("tokenProvider is null, skipping ss_investors update");
            return;
        }

        String accessToken = tokenProvider.getDynamicsToken();
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        if (accessToken == null || baseUrl == null) {
            log.warn("Dynamics config not available, skipping ss_investors update");
            return;
        }

        URI uri = URI.create(baseUrl + "/ss_investors(" + investorGuid + ")");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");

        Map<String, Object> patchData = new java.util.HashMap<>();
        patchData.put("ss_investorpersonaldetailsfilled", true);

        try {
            restTemplate.exchange(
                    uri,
                    HttpMethod.PATCH,
                    new HttpEntity<>(patchData, headers),
                    Void.class
            );
            log.info("✅ ss_investors({}).ss_investorpersonaldetailsfilled set to true", investorGuid);
        } catch (Exception e) {
            // Non-fatal: downstream workflow may be slightly out of sync, but local
            // finalSubmit succeeded and can be retried.
            log.error("❌ Failed to PATCH ss_investors({}): {}", investorGuid, e.getMessage());
        }
    }

    /**
     * Fetch document master ID for "Investor Information" document type.
     */
    public String fetchDocumentMasterIdForInvestorInfo(String brokerGuid) {
        log.info("Fetching document master ID for Investor Information (broker: {})", brokerGuid);
        
        if (tokenProvider == null) return null;
        String accessToken = tokenProvider.getDynamicsToken();
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        if (accessToken == null || baseUrl == null) return null;
        
        String filter = String.format("_ss_broker_value eq '%s' and ss_documentcategory eq 100000005 and ss_documenttype eq 100000011", brokerGuid);
        
        URI uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/ss_documentmasters")
                .queryParam("$filter", filter)
                .queryParam("$select", "ss_documentmasterid")
                .build().encode().toUri();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");
        
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
            JsonNode body = response.getBody();
            if (body != null && body.has("value") && !body.get("value").isEmpty()) {
                String masterId = body.get("value").get(0).path("ss_documentmasterid").asText(null);
                log.info("Found document master ID: {}", masterId);
                return masterId;
            }
        } catch (Exception e) {
            log.error("Failed to fetch document master ID: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Load a single {@code ss_investordocumentses} row by primary key.
     * Laravel onboarding form keys files by {@code ss_investordocumentsid}.
     */
    public Map<String, String> fetchInvestorDocumentByInvestorDocumentsId(String ssInvestorDocumentsId) {
        if (tokenProvider == null || ssInvestorDocumentsId == null || ssInvestorDocumentsId.isBlank()) {
            return null;
        }
        String accessToken = tokenProvider.getDynamicsToken();
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        if (accessToken == null || baseUrl == null) {
            return null;
        }
        String id = ssInvestorDocumentsId.trim();
        URI uri = URI.create(baseUrl + "/ss_investordocumentses(" + id + ")?$select=ss_name,ss_description,ss_investordocumentsid");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    uri, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
            JsonNode body = response.getBody();
            if (body == null) {
                return null;
            }
            Map<String, String> result = new HashMap<>();
            result.put("ss_investordocumentsid", body.path("ss_investordocumentsid").asText(id));
            result.put("ss_name", body.path("ss_name").asText(null));
            result.put("ss_description", body.path("ss_description").asText(null));
            log.info("Fetched ss_investordocumentses row: {}", id);
            return result;
        } catch (Exception e) {
            log.error("fetchInvestorDocumentByInvestorDocumentsId failed for {}: {}", id, e.getMessage());
            return null;
        }
    }

    /**
     * Fetch investor document record ID.
     */
    public Map<String, String> fetchInvestorDocumentRecord(String investorGuid, String brokerGuid, String documentMasterId) {
        log.info("Fetching investor document record for investor {}", investorGuid);
        
        if (tokenProvider == null) return null;
        String accessToken = tokenProvider.getDynamicsToken();
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        if (accessToken == null || baseUrl == null) return null;
        
        String filter = String.format("_ss_investor_value eq '%s' and _ss_broker_value eq '%s' and _ss_documentmaster_value eq '%s'", 
                                      investorGuid, brokerGuid, documentMasterId);
        
        URI uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/ss_investordocumentses")
                .queryParam("$filter", filter)
                .queryParam("$select", "ss_investordocumentsid,ss_name")
                .build().encode().toUri();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");
        
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
            JsonNode body = response.getBody();
            if (body != null && body.has("value") && !body.get("value").isEmpty()) {
                JsonNode record = body.get("value").get(0);
                Map<String, String> result = new java.util.HashMap<>();
                result.put("ss_investordocumentsid", record.path("ss_investordocumentsid").asText());
                result.put("ss_name", record.path("ss_name").asText());
                log.info("Found investor document record: {}", result.get("ss_investordocumentsid"));
                return result;
            }
        } catch (Exception e) {
            log.error("Failed to fetch investor document record: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Create SharePoint Document Location record in Dataverse.
     */
    public void createSharePointDocumentLocation(String investorDocumentId, String folderName, 
                                                  String parentLocationId, String siteCollectionId) {
        log.info("Creating SharePoint Document Location for document {}", investorDocumentId);
        
        if (tokenProvider == null) return;
        String accessToken = tokenProvider.getDynamicsToken();
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        if (accessToken == null || baseUrl == null) return;
        
        URI uri = URI.create(baseUrl + "/sharepointdocumentlocations");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");
        headers.set("Prefer", "return=representation");
        
        Map<String, Object> locationData = new java.util.HashMap<>();
        locationData.put("absoluteurl", null);
        locationData.put("name", "Documents on Default Site");
        locationData.put("locationtype", 0);
        locationData.put("regardingobjectid_ss_investordocuments@odata.bind", 
                        "/ss_investordocumentses(" + investorDocumentId + ")");
        locationData.put("parentsiteorlocation_sharepointdocumentlocation@odata.bind", 
                        "/sharepointdocumentlocations(" + parentLocationId + ")");
        locationData.put("statecode", 0);
        locationData.put("sitecollectionid", siteCollectionId);
        locationData.put("relativeurl", folderName);
        locationData.put("servicetype", 0);
        locationData.put("statuscode", 1);
        
        try {
            restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(locationData, headers), Void.class);
            log.info("Successfully created SharePoint document location");
        } catch (Exception e) {
            log.error("Failed to create SharePoint document location: {}", e.getMessage(), e);
        }
    }

    /**
     * Fetch investor data by Dataverse ss_investorid GUID (aligns with Laravel updateVerificationStatus).
     */
    public Map<String, Object> fetchInvestorByGuid(String investorId) {
        log.info("Fetching investor from Dataverse by GUID: {}", investorId);
        
        if (tokenProvider == null) {
            log.warn("tokenProvider is null, cannot fetch investor");
            return null;
        }
        
        String accessToken = tokenProvider.getDynamicsToken();
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        
        if (accessToken == null || baseUrl == null) {
            log.warn("Dynamics config not available");
            return null;
        }
        
        // Direct URI: GET ss_investors({guid})
        URI uri = URI.create(baseUrl + "/ss_investors(" + investorId + ")");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");
        
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    JsonNode.class
            );
            
            JsonNode body = response.getBody();
            if (body == null) {
                log.warn("Empty response from Dataverse for investorId: {}", investorId);
                return null;
            }
            
            // Convert JsonNode to Map
            Map<String, Object> result = new java.util.HashMap<>();
            body.fields().forEachRemaining(entry -> {
                if (entry.getValue().isBoolean()) {
                    result.put(entry.getKey(), entry.getValue().asBoolean());
                } else if (entry.getValue().isNumber()) {
                    result.put(entry.getKey(), entry.getValue().asInt());
                } else if (entry.getValue().isTextual()) {
                    result.put(entry.getKey(), entry.getValue().asText());
                } else {
                    result.put(entry.getKey(), entry.getValue().toString());
                }
            });
            
            log.info("✅ Fetched investor data from Dataverse: investorId={}, ss_verificationdone={}", 
                    investorId, result.get("ss_verificationdone"));
            
            return result;
        } catch (Exception e) {
            log.error("❌ Failed to fetch investor from Dataverse: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Fetch investor product verification details from Dataverse ss_investorproducts table.
     * Aligns with Laravel InnerPageController.php lines 1658-1675.
     * 
     * @param investorGuid Dataverse ss_investors GUID
     * @param brokerId Broker GUID
     * @param productId Product GUID
     * @return Map containing ss_verificationdone, ss_verificationdoneby, ss_verificationdateandtime
     */
    public Map<String, Object> fetchInvestorProduct(String investorGuid, String brokerId, String productId) {
        log.info("📡 Fetching investor product verification from Dataverse: investor={}, broker={}, product={}", 
                investorGuid, brokerId, productId);
        
        if (tokenProvider == null) {
            log.warn("tokenProvider is null, cannot fetch investor product");
            return null;
        }
        
        String accessToken = tokenProvider.getDynamicsToken();
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        
        if (accessToken == null || baseUrl == null) {
            log.warn("Dynamics config not available");
            return null;
        }
        
        String filter = String.format(
                "_ss_investor_value eq %s and _ss_broker_value eq %s and _ss_product_value eq %s",
                investorGuid, brokerId, productId);
        String select = "ss_verificationdone,ss_verificationdoneby,ss_verificationdateandtime";

        URI uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/ss_investorproducts")
                .queryParam("$filter", filter)
                .queryParam("$select", select)
                .build()
                .encode()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    JsonNode.class
            );
            
            JsonNode body = response.getBody();
            if (body == null || !body.has("value")) {
                log.warn("Empty or invalid response from Dataverse for investor product");
                return null;
            }
            
            JsonNode valueArray = body.get("value");
            if (!valueArray.isArray() || valueArray.size() == 0) {
                log.warn("No investor product found in Dataverse");
                return null;
            }
            
            // Get first product (should be only one)
            JsonNode productNode = valueArray.get(0);
            Map<String, Object> result = new java.util.HashMap<>();
            
            // Extract verification fields (Dataverse may use Edm.Boolean or option-set integers)
            JsonNode doneNode = productNode.get("ss_verificationdone");
            if (doneNode != null && !doneNode.isNull()) {
                if (doneNode.isBoolean()) {
                    result.put("ss_verificationdone", doneNode.asBoolean());
                } else if (doneNode.isNumber()) {
                    result.put("ss_verificationdone", doneNode.asInt() != 0);
                } else {
                    String t = doneNode.asText("");
                    if ("true".equalsIgnoreCase(t) || "1".equals(t)) {
                        result.put("ss_verificationdone", true);
                    } else if ("false".equalsIgnoreCase(t) || "0".equals(t)) {
                        result.put("ss_verificationdone", false);
                    }
                }
            }
            JsonNode byNode = productNode.get("ss_verificationdoneby");
            if (byNode != null && !byNode.isNull() && byNode.isTextual()) {
                result.put("ss_verificationdoneby", byNode.asText());
            }
            JsonNode dtNode = productNode.get("ss_verificationdateandtime");
            if (dtNode != null && !dtNode.isNull()) {
                result.put("ss_verificationdateandtime", dtNode.asText());
            }
            
            log.info("✅ Fetched investor product verification: done={}, by={}, date={}", 
                    result.get("ss_verificationdone"), 
                    result.get("ss_verificationdoneby"),
                    result.get("ss_verificationdateandtime"));
            
            return result;
        } catch (Exception e) {
            log.error("❌ Failed to fetch investor product from Dataverse: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Fetch account details from Dataverse ss_investorproducts entity
     * Matches Laravel InnerPageController::account_details_show() logic (lines 2073-2106)
     * 
     * @param investorGuid Investor GUID (actual GUID, not display name like INV-1744)
     * @param brokerGuid Broker GUID from intro_investor_temp.ss_broker_value
     * @param productGuid Product GUID from intro_investor_temp.ss_product_value
     * @return Map containing account details (bank, trading, depository account numbers)
     */
    public Map<String, String> fetchAccountDetailsFromDataverse(String investorGuid, String brokerGuid, String productGuid) {
        if (tokenProvider == null) {
            log.warn("fetchAccountDetailsFromDataverse: tokenProvider not configured");
            return null;
        }
        
        String token = tokenProvider.getDynamicsToken();
        if (token == null) {
            log.warn("fetchAccountDetailsFromDataverse: no Dynamics token available");
            return null;
        }
        
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        if (baseUrl == null) {
            log.warn("fetchAccountDetailsFromDataverse: no Dynamics base URL");
            return null;
        }
        
        // Validate that investorGuid is an actual GUID, not a display name like "INV-1744"
        if (!isValidGuid(investorGuid)) {
            log.error("❌ Invalid investor GUID format: {}. Expected GUID, got display name.", investorGuid);
            return getDefaultAccountDetails();
        }
        
        try {
            // Build OData filter: _ss_investor_value eq {investorGuid} and _ss_broker_value eq {brokerGuid} and _ss_product_value eq {productGuid}
            String filter = String.format("_ss_investor_value eq %s and _ss_broker_value eq %s and _ss_product_value eq %s",
                    investorGuid, brokerGuid, productGuid);
            
            // CORRECT entity name: ss_investorproducts (NOT ss_productsvisitorses)
            URI uri = UriComponentsBuilder
                    .fromHttpUrl(baseUrl + "/ss_investorproducts")
                    .queryParam("$filter", filter)
                    .queryParam("$top", "1")
                    .build().encode().toUri();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.set("OData-MaxVersion", "4.0");
            headers.set("OData-Version", "4.0");
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            
            log.info("🔗 Fetching account details from Dataverse: {}", uri);
            
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    uri, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
            
            JsonNode body = response.getBody();
            if (body == null || !body.has("value")) {
                log.warn("❌ No response body or value array from Dataverse");
                return getDefaultAccountDetails();
            }
            
            JsonNode valueArray = body.get("value");
            if (!valueArray.isArray() || valueArray.size() == 0) {
                log.warn("⚠️ No account details found in Dataverse for investor {} / broker {} / product {}", 
                        investorGuid, brokerGuid, productGuid);
                return getDefaultAccountDetails();
            }
            
            // Extract account details from first record
            JsonNode data = valueArray.get(0);
            Map<String, String> accountDetails = new HashMap<>();
            
            // Standard bank details
            accountDetails.put("ss_nameofbank", getTextOrDefault(data, "ss_nameofbank", "-"));
            accountDetails.put("ss_bankaddress", getTextOrDefault(data, "ss_bankaddress", "-"));
            accountDetails.put("ss_bankaccountnumber", getTextOrDefault(data, "ss_bankaccountnumber", "-"));
            accountDetails.put("ss_swiftcode", getTextOrDefault(data, "ss_swiftcode", "-"));
            accountDetails.put("ss_ifsccode", getTextOrDefault(data, "ss_ifsccode", "-"));
            
            // Specialized accounts
            accountDetails.put("ss_safekeepingcustodyaccountno", getTextOrDefault(data, "ss_safekeepingcustodyaccountno", "-"));
            accountDetails.put("ss_tradingaccountno", getTextOrDefault(data, "ss_tradingaccountno", "-"));
            accountDetails.put("ss_pmsaccountfoliono", getTextOrDefault(data, "ss_pmsaccountfoliono", "-"));
            
            // Depository accounts (NEW - critical for Indian market)
            accountDetails.put("ss_depositorynsdldpid", getTextOrDefault(data, "ss_depositorynsdldpid", "-"));
            accountDetails.put("ss_depositorynsdlaccountno", getTextOrDefault(data, "ss_depositorynsdlaccountno", "-"));
            accountDetails.put("ss_depositorycsdlaccountno", getTextOrDefault(data, "ss_depositorycsdlaccountno", "-"));
            
            // Account opening status (boolean in Dataverse)
            JsonNode accountOpeningNode = data.get("ss_accountopening");
            if (accountOpeningNode != null && !accountOpeningNode.isNull()) {
                if (accountOpeningNode.isBoolean()) {
                    accountDetails.put("ss_accountopening", accountOpeningNode.asBoolean() ? "1" : "2");
                } else if (accountOpeningNode.isTextual()) {
                    String val = accountOpeningNode.asText().toLowerCase();
                    accountDetails.put("ss_accountopening", "true".equals(val) ? "1" : "2");
                } else {
                    accountDetails.put("ss_accountopening", "0");
                }
            } else {
                accountDetails.put("ss_accountopening", "0");
            }
            
            log.info("✅ Fetched account details from Dataverse: bank={}, nsdl={}, cdsl={}, accountOpening={}", 
                    accountDetails.get("ss_nameofbank"),
                    accountDetails.get("ss_depositorynsdldpid"),
                    accountDetails.get("ss_depositorycsdlaccountno"),
                    accountDetails.get("ss_accountopening"));
            
            return accountDetails;
            
        } catch (Exception e) {
            log.error("❌ Failed to fetch account details from Dataverse: {}", e.getMessage(), e);
            return getDefaultAccountDetails();
        }
    }
    
    /**
     * Get default account details (all fields set to "-")
     * Matches Laravel's $defaults array (lines 2082-2096)
     */
    private Map<String, String> getDefaultAccountDetails() {
        Map<String, String> defaults = new HashMap<>();
        defaults.put("ss_nameofbank", "-");
        defaults.put("ss_bankaddress", "-");
        defaults.put("ss_bankaccountnumber", "-");
        defaults.put("ss_swiftcode", "-");
        defaults.put("ss_ifsccode", "-");
        defaults.put("ss_safekeepingcustodyaccountno", "-");
        defaults.put("ss_tradingaccountno", "-");
        defaults.put("ss_pmsaccountfoliono", "-");
        defaults.put("ss_depositorynsdldpid", "-");
        defaults.put("ss_depositorynsdlaccountno", "-");
        defaults.put("ss_depositorycsdlaccountno", "-");
        defaults.put("ss_accountopening", "0");
        return defaults;
    }
    
    /**
     * Helper to extract text value from JsonNode or return default
     */
    private String getTextOrDefault(JsonNode node, String fieldName, String defaultValue) {
        JsonNode field = node.get(fieldName);
        if (field != null && !field.isNull() && field.isTextual()) {
            String value = field.asText().trim();
            return value.isEmpty() ? defaultValue : value;
        }
        return defaultValue;
    }
    
    /**
     * Validate if a string is a valid GUID format
     * Prevents using display names like "INV-1744" as GUIDs
     */
    private boolean isValidGuid(String guid) {
        if (guid == null || guid.isBlank()) {
            return false;
        }
        // GUID format: 8-4-4-4-12 (e.g., 9bf54b0b-731c-f111-9730-7c1e523b1a05)
        String guidPattern = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        return guid.matches(guidPattern);
    }
    
    // ============================================
    // Introduced Investor Registration Methods
    // ============================================
    
    /**
     * Fetch investor details from Dataverse ss_investors entity by display name
     * Matches Laravel: lines 3805-3826
     */
    public Map<String, Object> fetchInvestorFromDataverse(String investorDisplayName) {
        if (tokenProvider == null) {
            log.error("TokenProvider is null");
            return Map.of();
        }
        String accessToken = tokenProvider.getDynamicsToken();
        if (accessToken == null) {
            log.error("Failed to obtain Dataverse access token");
            return Map.of();
        }
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        if (baseUrl == null) {
            log.error("Dynamics base URL is null");
            return Map.of();
        }

        String filter = String.format("ss_name eq '%s'", investorDisplayName);
        URI uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/ss_investors")
                .queryParam("$filter", filter)
                .queryParam("$top", "1")
                .build().encode().toUri();

        log.info("🔗 Fetching investor from Dataverse: {}", uri);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> values = (List<Map<String, Object>>) body.get("value");
                if (values != null && !values.isEmpty()) {
                    log.info("✅ Investor found in Dataverse: {}", investorDisplayName);
                    return values.get(0);
                }
            }
            log.warn("⚠️ Investor not found in Dataverse: {}", investorDisplayName);
            return Map.of();
        } catch (Exception e) {
            log.error("❌ Failed to fetch investor from Dataverse: {}", e.getMessage(), e);
            return Map.of();
        }
    }
    
    /**
     * Fetch contact ID from Dataverse by investor GUID
     */
    public String fetchContactIdFromDataverse(String investorGuid) {
        if (tokenProvider == null) return null;
        String accessToken = tokenProvider.getDynamicsToken();
        if (accessToken == null) return null;
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        if (baseUrl == null) return null;

        String filter = String.format("_ss_lookuptoinvestor_value eq %s", investorGuid);
        URI uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/contacts")
                .queryParam("$filter", filter)
                .queryParam("$top", "1")
                .build().encode().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> values = (List<Map<String, Object>>) body.get("value");
                if (values != null && !values.isEmpty()) {
                    return (String) values.get(0).get("contactid");
                }
            }
        } catch (Exception e) {
            log.error("Failed to fetch contact ID: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Fetch broker/portfolio manager name from accounts entity
     */
    public String fetchBrokerName(String brokerGuid) {
        if (tokenProvider == null) return null;
        String accessToken = tokenProvider.getDynamicsToken();
        if (accessToken == null) return null;
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        if (baseUrl == null) return null;

        String filter = String.format("_ss_portfoliomanager_value eq %s", brokerGuid);
        URI uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/accounts")
                .queryParam("$filter", filter)
                .queryParam("$top", "1")
                .build().encode().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> values = (List<Map<String, Object>>) body.get("value");
                if (values != null && !values.isEmpty()) {
                    return (String) values.get(0).get("name");
                }
            }
        } catch (Exception e) {
            log.error("Failed to fetch broker name: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Fetch scheme name from ss_schemeses entity
     * Laravel: ss_schemeses({guid}) -> ss_name
     */
    public String fetchSchemeName(String schemeGuid) {
        if (tokenProvider == null) return null;
        String accessToken = tokenProvider.getDynamicsToken();
        if (accessToken == null) return null;
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        if (baseUrl == null) return null;

        URI uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/ss_schemeses(" + schemeGuid + ")")
                .build().encode().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return (String) response.getBody().get("ss_name");
            }
        } catch (Exception e) {
            log.error("Failed to fetch scheme name: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Nationality lookup GUID from {@code ss_investors} row (raw {@code _ss_nationality_value} or {@code @odata.bind}).
     */
    public String extractNationalityLookupGuid(Map<String, Object> investorRow) {
        if (investorRow == null || investorRow.isEmpty()) {
            return null;
        }
        String raw = mapString(investorRow, "_ss_nationality_value");
        String bind = parseLookupGuidFromBind(mapString(investorRow, "_ss_nationality_value@odata.bind"));
        return firstNonBlank(raw, bind);
    }

    private static String mapString(Map<String, Object> row, String key) {
        Object o = row.get(key);
        if (o == null) {
            return null;
        }
        if (o instanceof String s) {
            return s.isBlank() ? null : s;
        }
        return String.valueOf(o);
    }

    private static List<String> guidLookupCandidates(String guid) {
        if (guid == null || guid.isBlank()) {
            return List.of();
        }
        String t = guid.trim();
        String inner = t.replaceAll("^\\{|\\}$", "").trim();
        Set<String> out = new LinkedHashSet<>();
        out.add(t);
        out.add(inner);
        out.add(inner.toLowerCase());
        out.add(inner.toUpperCase());
        out.add("{" + inner + "}");
        out.add("{" + inner.toUpperCase() + "}");
        return new ArrayList<>(out);
    }

    /**
     * Fetch nationality display name: local {@code master_nationality} (ss_nationalityid = Dataverse GUID),
     * then live Dataverse {@code ss_countries} / {@code ss_nationalities} (dev seeds often use ISO codes in
     * {@code ss_nationalityid}, which never match CRM GUIDs — same as Laravel after DV sync).
     */
    public String fetchNationalityName(String nationalityGuid) {
        if (nationalityGuid == null || nationalityGuid.isBlank()) {
            return null;
        }
        try {
            for (String candidate : guidLookupCandidates(nationalityGuid)) {
                Optional<String> local = nationalityRepository.findBySsNationalityId(candidate)
                        .map(MasterNationality::getSsName);
                if (local.isPresent()) {
                    return local.get();
                }
            }
            for (String candidate : guidLookupCandidates(nationalityGuid)) {
                String fromDv = tryFetchEntitySsName(candidate, "ss_countries");
                if (fromDv != null) {
                    return fromDv;
                }
                fromDv = tryFetchEntitySsName(candidate, "ss_nationalities");
                if (fromDv != null) {
                    return fromDv;
                }
            }
            log.warn("Nationality not resolved for GUID {} — ensure master_nationality.ss_nationalityid stores Dataverse IDs or DV entity is ss_countries/ss_nationalities", nationalityGuid);
            return null;
        } catch (Exception e) {
            log.error("Failed to fetch nationality name for GUID {}: {}", nationalityGuid, e.getMessage());
            return null;
        }
    }

    /**
     * GET {entitySet}({id})?$select=ss_name — id field is the logical primary key for OData key segment.
     */
    private String tryFetchEntitySsName(String recordId, String entitySet) {
        if (tokenProvider == null || recordId == null || recordId.isBlank()) {
            return null;
        }
        String accessToken = tokenProvider.getDynamicsToken();
        if (accessToken == null) {
            return null;
        }
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        if (baseUrl == null) {
            return null;
        }
        String guid = recordId.trim().replaceAll("^\\{|\\}$", "");
        URI uri = URI.create(baseUrl + "/" + entitySet + "(" + guid + ")?$select=ss_name");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Object name = response.getBody().get("ss_name");
                return name != null ? String.valueOf(name) : null;
            }
        } catch (Exception e) {
            log.debug("tryFetchEntitySsName {} for {}: {}", entitySet, guid, e.getMessage());
        }
        return null;
    }
    
    /**
     * Fetch country of residence name from master_country_of_residence table (local database)
     * Laravel: DB::table('master_country_of_residence')->where('ss_countryid', '=', $guid)->first()
     */
    public String fetchCountryOfResidenceName(String countryGuid) {
        try {
            return countryOfResidenceRepository.findBySsCountryId(countryGuid)
                    .map(MasterCountryOfResidence::getSsName)
                    .orElse(null);
        } catch (Exception e) {
            log.error("Failed to fetch country of residence name for GUID {}: {}", countryGuid, e.getMessage());
            return null;
        }
    }
    
    /**
     * Fetch investor type name from master_investor_types table (local database)
     * Laravel: DB::table('master_investor_types')->where('ss_investortypeid', '=', $guid)->first()
     */
    public String fetchInvestorTypeName(String investorTypeGuid) {
        try {
            return investorTypesRepository.findBySsInvestorTypeId(investorTypeGuid)
                    .map(MasterInvestorTypes::getSsName)
                    .orElse(null);
        } catch (Exception e) {
            log.error("Failed to fetch investor type name for GUID {}: {}", investorTypeGuid, e.getMessage());
            return null;
        }
    }
}
