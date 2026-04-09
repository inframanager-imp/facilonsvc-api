package com.facilon.app.integration.dynamics;

import com.facilon.app.module.client.model.master.*;
import com.facilon.app.module.client.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

/**
 * Replicates every Laravel {@code DataverseController} master-sync route into
 * a single Spring Boot service.
 *
 * <p>Laravel sync routes (web.php):
 * <pre>
 *   /get-accounts-dv            → fetch_accounts_from_dv        → master_accounts
 *   /get-brokers-dv             → fetch_brokers_from_dv          → master_brokers
 *   /get-pms-dv                 → fetch_portfolio_managers_from_dv → master_portfolio_managers
 *   /get-products-dv            → fetch_products_from_dv         → master_products
 *   /get-plans-dv               → fetch_plans_from_dv            → master_plans (ss_plans + ss_portfoliomanagerplans)
 *   /get-pms-plans-dv           → fetch_pms_plans_from_dv        → master_pms_plans
 *   /get-schemes-dv             → fetch_schemes_from_dv          → master_schemes
 *   /get-banks                  → fetch_banks_from_dv            → master_banks
 *   /get-broker-banks           → fetch_broker_banks_from_dv     → master_broker_banks
 *   /get-countries-dv           → fetch_countries_from_dv        → master_countries
 *   /get-nationality-dv         → fetch_nationality_from_dv      → master_investor_types
 *   (no route)                  → hardcoded seed                 → master_service_provider_type
 * </pre>
 *
 * <p>Each sync method: fetch ALL records from Dataverse, delete existing local rows,
 * insert fresh — same effective result as Laravel's upsert calls.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DataverseMasterSyncService {

    private final DynamicsTokenProvider tokenProvider;
    private final RestTemplate restTemplate = new RestTemplate();

    // ── Repositories ──────────────────────────────────────────────────────────
    private final MasterAccountsRepository masterAccountsRepository;
    private final MasterBrokersRepository masterBrokersRepository;
    private final MasterPortfolioManagersRepository masterPortfolioManagersRepository;
    private final MasterProductsRepository masterProductsRepository;
    private final MasterPlansRepository masterPlansRepository;
    private final MasterPmsPlansRepository masterPmsPlansRepository;
    private final MasterSchemesRepository masterSchemesRepository;
    private final MasterBanksRepository masterBanksRepository;
    private final MasterBrokerBanksRepository masterBrokerBanksRepository;
    private final MasterCountriesRepository masterCountriesRepository;
    private final MasterInvestorTypesRepository masterInvestorTypesRepository;
    private final MasterServiceProviderTypeRepository masterServiceProviderTypeRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // Public "sync all critical" entry point  (used by admin /sync-all endpoint)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Sync all master tables needed for the investor dashboard product-assignment
     * section.  Mirrors Laravel's {@code /all-masters-dv} + plan/scheme routes.
     */
    public Map<String, Object> syncAllMasters() {
        Map<String, Object> results = new LinkedHashMap<>();
        results.put("accounts",             runSync("accounts",             this::syncAccounts));
        results.put("brokers",              runSync("brokers",              this::syncBrokers));
        results.put("portfolioManagers",    runSync("portfolioManagers",    this::syncPortfolioManagers));
        results.put("products",             runSync("products",             this::syncProducts));
        results.put("plans",                runSync("plans",                this::syncPlans));
        results.put("pmsPlans",             runSync("pmsPlans",             this::syncPmsPlans));
        results.put("schemes",              runSync("schemes",              this::syncSchemes));
        results.put("banks",                runSync("banks",                this::syncBanks));
        results.put("brokerBanks",          runSync("brokerBanks",          this::syncBrokerBanks));
        results.put("countries",            runSync("countries",            this::syncCountries));
        results.put("investorTypes",        runSync("investorTypes",        this::syncInvestorTypes));
        results.put("serviceProviderTypes", runSync("serviceProviderTypes", this::seedServiceProviderTypes));
        return results;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Individual sync methods  (each = one Laravel DataverseController method)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Laravel: {@code fetch_accounts_from_dv}
     * URL: {@code GET /accounts}
     * Table: {@code master_accounts}
     */
    @Transactional
    public int syncAccounts() {
        List<JsonNode> rows = fetchAllPages("accounts", null);
        if (rows.isEmpty()) return 0;

        masterAccountsRepository.deleteAll();
        List<MasterAccounts> entities = rows.stream().map(r -> MasterAccounts.builder()
                .accountId(text(r, "accountid"))
                .name(text(r, "name"))
                .ssNseFoSebiRegNo(text(r, "ss_nsefosebiregnno"))
                .websiteUrl(text(r, "websiteurl"))
                .emailAddress1(text(r, "emailaddress1"))
                .primaryContactIdValue(text(r, "_primarycontactid_value"))
                .address1AddressId(text(r, "address1_addressid"))
                .ssNseSebiRegNo(text(r, "ss_nsesebiregnno"))
                .fax(text(r, "fax"))
                .ssCinNumber(text(r, "ss_cinnumber"))
                .telephone1(text(r, "telephone1"))
                .address2AddressId(text(r, "address2_addressid"))
                .ssPanNo(text(r, "ss_panno"))
                .ssBrokerValue(text(r, "_ss_broker_value"))
                .build()).toList();
        masterAccountsRepository.saveAll(entities);
        log.info("syncAccounts: saved {} records", entities.size());
        return entities.size();
    }

    /**
     * Laravel: {@code fetch_brokers_from_dv}
     * URL: {@code GET /ss_brokers}
     * Table: {@code master_brokers}
     */
    @Transactional
    public int syncBrokers() {
        List<JsonNode> rows = fetchAllPages("ss_brokers", null);
        if (rows.isEmpty()) return 0;

        masterBrokersRepository.deleteAll();
        List<MasterBrokers> entities = rows.stream().map(r -> MasterBrokers.builder()
                .ssBrokerId(text(r, "ss_brokerid"))
                .ssName(text(r, "ss_name"))
                .ssServiceProviderType(text(r, "ss_serviceprovidertype"))
                .ssNameOfTheFirmValue(text(r, "_ss_nameofthefirm_value"))
                .build()).toList();
        masterBrokersRepository.saveAll(entities);
        log.info("syncBrokers: saved {} records", entities.size());
        return entities.size();
    }

    /**
     * Laravel: {@code fetch_portfolio_managers_from_dv}
     * URL: {@code GET /ss_portfoliomanagers}
     * Table: {@code master_portfolio_managers}
     */
    @Transactional
    public int syncPortfolioManagers() {
        List<JsonNode> rows = fetchAllPages("ss_portfoliomanagers", null);
        if (rows.isEmpty()) return 0;

        masterPortfolioManagersRepository.deleteAll();
        List<MasterPortfolioManagers> entities = rows.stream().map(r -> MasterPortfolioManagers.builder()
                .ssPortfolioManagerId(text(r, "ss_portfoliomanagerid"))
                .ssName(text(r, "ss_name"))
                .ssServiceProviderType(text(r, "ss_serviceprovidertype"))
                .ssNameOfTheFirmValue(text(r, "_ss_nameofthefirm_value"))
                .build()).toList();
        masterPortfolioManagersRepository.saveAll(entities);
        log.info("syncPortfolioManagers: saved {} records", entities.size());
        return entities.size();
    }

    /**
     * Laravel: {@code fetch_products_from_dv}
     * URL: {@code GET /ss_products}
     * Table: {@code master_products}
     */
    @Transactional
    public int syncProducts() {
        List<JsonNode> rows = fetchAllPages("ss_products", null);
        if (rows.isEmpty()) return 0;

        masterProductsRepository.deleteAll();
        List<MasterProducts> entities = rows.stream().map(r -> MasterProducts.builder()
                .ssProductId(text(r, "ss_productid"))
                .ssName(text(r, "ss_name"))
                .ssMarketsOfInterestValue(text(r, "_ss_marketsofinterest_value"))
                .ssServicedBy(text(r, "ss_servicedby"))
                .ssApplicableTo(text(r, "ss_applicableto"))
                .ssProductDescription(text(r, "ss_productdescription"))
                .ssRepatriationBenefits(text(r, "ss_repatriationbenefits"))
                .ssInvestmentRouteValue(text(r, "_ss_investmentroute_value"))
                .build()).toList();
        masterProductsRepository.saveAll(entities);
        log.info("syncProducts: saved {} records", entities.size());
        return entities.size();
    }

    /**
     * Laravel: {@code fetch_plans_from_dv}
     * URLs: {@code GET /ss_plans} AND {@code GET /ss_portfoliomanagerplans} → merged into one table.
     * Table: {@code master_plans}
     *
     * <p>Both broker plans and PMS plans are stored in the same local table.
     * Laravel uses {@code ss_portfoliomanagerplanid} → {@code ss_planid} for PMS plans.
     */
    @Transactional
    public int syncPlans() {
        masterPlansRepository.deleteAll();
        int total = 0;

        // Pass A: broker plans (ss_plans)
        List<JsonNode> brokerPlans = fetchAllPages("ss_plans", null);
        List<MasterPlans> brokerEntities = brokerPlans.stream().map(r -> MasterPlans.builder()
                .ssPlanId(text(r, "ss_planid"))
                .ssName(text(r, "ss_name"))
                .ssInvestmentRouteValue(text(r, "_ss_investmentroute_value"))
                .ssPlanDescription(text(r, "ss_plandescription"))
                .versionNumber(text(r, "versionnumber"))
                .ssProductValue(text(r, "_ss_product_value"))
                .ssBrokerValue(text(r, "_ss_broker_value"))
                .build()).toList();
        masterPlansRepository.saveAll(brokerEntities);
        total += brokerEntities.size();
        log.info("syncPlans (ss_plans): saved {} records", brokerEntities.size());

        // Pass B: PMS plans (ss_portfoliomanagerplans) — stored in same table
        List<JsonNode> pmsPlans = fetchAllPages("ss_portfoliomanagerplans", null);
        List<MasterPlans> pmsEntities = pmsPlans.stream().map(r -> MasterPlans.builder()
                .ssPlanId(text(r, "ss_portfoliomanagerplanid"))   // DV PM plan id → ss_planid
                .ssName(text(r, "ss_name"))
                .ssInvestmentRouteValue(text(r, "_ss_investmentroute_value"))
                .ssPlanDescription(text(r, "ss_plandescription"))
                .versionNumber(text(r, "versionnumber"))
                .ssProductValue(text(r, "_ss_product_value"))
                .ssBrokerValue(text(r, "_ss_portfoliomanager_value")) // DV PM value → ss_broker_value col
                .build()).toList();
        masterPlansRepository.saveAll(pmsEntities);
        total += pmsEntities.size();
        log.info("syncPlans (ss_portfoliomanagerplans): saved {} records", pmsEntities.size());

        return total;
    }

    /**
     * Laravel: {@code fetch_pms_plans_from_dv}
     * URL: {@code GET /ss_portfoliomanagerplans}
     * Table: {@code master_pms_plans}
     */
    @Transactional
    public int syncPmsPlans() {
        List<JsonNode> rows = fetchAllPages("ss_portfoliomanagerplans", null);
        if (rows.isEmpty()) return 0;

        masterPmsPlansRepository.deleteAll();
        List<MasterPmsPlans> entities = rows.stream().map(r -> MasterPmsPlans.builder()
                .ssPlanId(text(r, "ss_portfoliomanagerplanid"))
                .ssName(text(r, "ss_name"))
                .ssInvestmentRouteValue(text(r, "_ss_investmentroute_value"))
                .ssPlanDescription(text(r, "ss_plandescription"))
                .versionNumber(text(r, "versionnumber"))
                .ssProductValue(text(r, "_ss_product_value"))
                .ssPmsValue(text(r, "_ss_portfoliomanager_value"))
                .ssPreferredBankValue(text(r, "_ss_preferredbank_value"))
                .ssSchemeValue(text(r, "_ss_scheme_value"))
                .build()).toList();
        masterPmsPlansRepository.saveAll(entities);
        log.info("syncPmsPlans: saved {} records", entities.size());
        return entities.size();
    }

    /**
     * Laravel: {@code fetch_schemes_from_dv}
     * URL: {@code GET /ss_schemeses}
     * Table: {@code master_schemes}
     */
    @Transactional
    public int syncSchemes() {
        List<JsonNode> rows = fetchAllPages("ss_schemeses", null);
        if (rows.isEmpty()) return 0;

        masterSchemesRepository.deleteAll();
        List<MasterSchemes> entities = rows.stream().map(r -> MasterSchemes.builder()
                .ssSchemesId(text(r, "ss_schemesid"))
                .ssName(text(r, "ss_name"))
                .ssPortfolioManagerValue(text(r, "_ss_portfoliomanager_value"))
                .ssSchemeDescription(text(r, "ss_schemedescription"))
                .build()).toList();
        masterSchemesRepository.saveAll(entities);
        log.info("syncSchemes: saved {} records", entities.size());
        return entities.size();
    }

    /**
     * Laravel: {@code fetch_banks_from_dv}
     * URL: {@code GET /ss_banks}
     * Table: {@code master_banks}
     */
    @Transactional
    public int syncBanks() {
        List<JsonNode> rows = fetchAllPages("ss_banks", null);
        if (rows.isEmpty()) return 0;

        masterBanksRepository.deleteAll();
        List<MasterBanks> entities = rows.stream().map(r -> MasterBanks.builder()
                .ssBankId(text(r, "ss_bankid"))
                .ssName(text(r, "ss_name"))
                .ssNameOfBank(text(r, "_ss_nameofbank_value"))
                .ssServiceProviderType(text(r, "ss_serviceprovidertype"))
                .build()).toList();
        masterBanksRepository.saveAll(entities);
        log.info("syncBanks: saved {} records", entities.size());
        return entities.size();
    }

    /**
     * Laravel: {@code fetch_broker_banks_from_dv}
     * URL: {@code GET /ss_brokerbanks}
     * Table: {@code master_broker_banks}
     */
    @Transactional
    public int syncBrokerBanks() {
        List<JsonNode> rows = fetchAllPages("ss_brokerbanks", null);
        if (rows.isEmpty()) return 0;

        masterBrokerBanksRepository.deleteAll();
        List<MasterBrokerBanks> entities = rows.stream().map(r -> MasterBrokerBanks.builder()
                .ssBrokerBankId(text(r, "ss_brokerbankid"))
                .ssName(text(r, "ss_name"))
                .ssBrokerValue(text(r, "_ss_broker_value"))
                .ssBankValue(text(r, "_ss_bank_value"))
                .build()).toList();
        masterBrokerBanksRepository.saveAll(entities);
        log.info("syncBrokerBanks: saved {} records", entities.size());
        return entities.size();
    }

    /**
     * Laravel: {@code fetch_countries_from_dv}
     * URL: {@code GET /ss_countries}
     * Table: {@code master_countries}
     */
    @Transactional
    public int syncCountries() {
        List<JsonNode> rows = fetchAllPages("ss_countries", null);
        if (rows.isEmpty()) return 0;

        masterCountriesRepository.deleteAll();
        final int[] countryIdx = {1};
        List<MasterCountries> entities = rows.stream().map(r -> MasterCountries.builder()
                .id(countryIdx[0]++)
                .ssName(text(r, "ss_name"))
                .ssCountry(text(r, "ss_country"))
                .ssCountryId(text(r, "ss_countryid"))
                .ssIsdCode(text(r, "ss_isdcode"))
                .build()).toList();
        masterCountriesRepository.saveAll(entities);
        log.info("syncCountries: saved {} records", entities.size());
        return entities.size();
    }

    /**
     * Laravel: {@code fetch_nationality_from_dv} (misleading name — actually investor types)
     * URL: {@code GET /ss_investortypes}
     * Table: {@code master_investor_types}
     */
    @Transactional
    public int syncInvestorTypes() {
        List<JsonNode> rows = fetchAllPages("ss_investortypes", null);
        if (rows.isEmpty()) return 0;

        masterInvestorTypesRepository.deleteAll();
        final int[] idx = {1};
        List<MasterInvestorTypes> entities = rows.stream().map(r -> MasterInvestorTypes.builder()
                .id(idx[0]++)
                .ssInvestorTypeId(text(r, "ss_investortypeid"))
                .ssName(text(r, "ss_name"))
                .ssApplicableTo(text(r, "ss_applicableto"))
                .build()).toList();
        masterInvestorTypesRepository.saveAll(entities);
        log.info("syncInvestorTypes: saved {} records", entities.size());
        return entities.size();
    }

    /**
     * {@code master_service_provider_type} has NO Dataverse sync in Laravel — it stores
     * Dynamics 365 option-set constants. We seed the four known values so that
     * the blade-equivalent lookup {@code WHERE ss_provider_id = '100000000'} always resolves.
     */
    @Transactional
    public int seedServiceProviderTypes() {
        masterServiceProviderTypeRepository.deleteAll();
        List<MasterServiceProviderType> seeds = List.of(
                MasterServiceProviderType.builder().id(1).ssProviderId("100000000").ssName("Broker").build(),
                MasterServiceProviderType.builder().id(2).ssProviderId("100000001").ssName("Bank").build(),
                MasterServiceProviderType.builder().id(3).ssProviderId("100000002").ssName("Insurance").build(),
                MasterServiceProviderType.builder().id(4).ssProviderId("100000003").ssName("Portfolio Manager").build()
        );
        masterServiceProviderTypeRepository.saveAll(seeds);
        log.info("seedServiceProviderTypes: seeded {} records", seeds.size());
        return seeds.size();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Dataverse fetch helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Fetches all records from a Dataverse entity set, handling OData {@code @odata.nextLink}
     * pagination (same as Laravel's cURL which returns all at once for small sets).
     *
     * @param entitySet e.g. "ss_brokers", "accounts"
     * @param select    optional comma-separated field list for {@code $select}; null = all
     */
    public List<JsonNode> fetchAllPages(String entitySet, String select) {
        String token = tokenProvider.getDynamicsToken();
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        if (token == null || baseUrl == null) {
            log.warn("fetchAllPages: no token/baseUrl for {}", entitySet);
            return Collections.emptyList();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");
        headers.set("Prefer", "odata.maxpagesize=5000");

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/" + entitySet);
        if (select != null && !select.isBlank()) {
            builder.queryParam("$select", select);
        }

        List<JsonNode> allRows = new ArrayList<>();
        String nextUrl = builder.build().encode().toUri().toString();

        while (nextUrl != null) {
            try {
                ResponseEntity<JsonNode> resp = restTemplate.exchange(
                        URI.create(nextUrl), HttpMethod.GET,
                        new HttpEntity<>(headers), JsonNode.class);
                JsonNode body = resp.getBody();
                if (body == null) break;

                JsonNode valueNode = body.get("value");
                if (valueNode != null && valueNode.isArray()) {
                    valueNode.forEach(allRows::add);
                }

                // OData pagination
                JsonNode next = body.get("@odata.nextLink");
                nextUrl = (next != null && !next.isNull()) ? next.asText() : null;

            } catch (Exception e) {
                log.warn("fetchAllPages error for {}: {}", entitySet, e.getMessage());
                break;
            }
        }
        log.info("fetchAllPages: {} → {} total rows", entitySet, allRows.size());
        return allRows;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────────────────────────────────

    private String text(JsonNode node, String field) {
        if (node == null || !node.has(field) || node.get(field).isNull()) return null;
        String v = node.get(field).asText("").trim();
        return v.isEmpty() ? null : v;
    }

    private Object runSync(String name, SyncTask task) {
        try {
            int count = task.run();
            return Map.of("status", "ok", "count", count);
        } catch (Exception e) {
            log.error("sync failed for {}: {}", name, e.getMessage(), e);
            return Map.of("status", "error", "message", e.getMessage());
        }
    }

    @FunctionalInterface
    private interface SyncTask {
        int run() throws Exception;
    }
}
