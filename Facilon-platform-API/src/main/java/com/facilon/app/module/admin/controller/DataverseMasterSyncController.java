package com.facilon.app.module.admin.controller;

import com.facilon.app.integration.dynamics.DataverseMasterSyncService;
import com.facilon.app.integration.dynamics.PowerAppContactSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Admin-only REST controller that replicates Laravel's manual Dataverse
 * master-sync routes (web.php "databerse master tables" section).
 *
 * <p>All endpoints are prefixed {@code /api/admin/master-sync}.
 *
 * <p>Laravel equivalents:
 * <pre>
 *   GET /sync/all-masters        → /all-masters-dv (plus plans, schemes, pms)
 *   GET /sync/accounts           → /get-accounts-dv
 *   GET /sync/brokers            → /get-brokers-dv
 *   GET /sync/portfolio-managers → /get-pms-dv
 *   GET /sync/products           → /get-products-dv
 *   GET /sync/plans              → /get-plans-dv
 *   GET /sync/pms-plans          → /get-pms-plans-dv
 *   GET /sync/schemes            → /get-schemes-dv
 *   GET /sync/banks              → /get-banks
 *   GET /sync/broker-banks       → /get-broker-banks
 *   GET /sync/countries          → /get-countries-dv
 *   GET /sync/investor-types     → /get-nationality-dv (misleading Laravel name)
 *   GET /sync/service-provider-types → (no Laravel route; seeds hardcoded option-set)
 * </pre>
 */
@RestController
@RequestMapping("/api/admin/master-sync")
@RequiredArgsConstructor
@Slf4j
public class DataverseMasterSyncController {

    private final DataverseMasterSyncService syncService;
    private final ObjectProvider<PowerAppContactSyncService> powerAppContactSyncServiceProvider;

    /**
     * Sync ALL master tables in one call.
     * Equivalent to visiting all individual Laravel sync routes sequentially.
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> syncAll() {
        log.info("DataverseMasterSyncController: syncAll triggered");
        Map<String, Object> result = syncService.syncAllMasters();
        return ResponseEntity.ok(Map.of(
                "message", "Master sync completed",
                "results", result
        ));
    }

    /** Laravel: /get-accounts-dv → master_accounts */
    @GetMapping("/accounts")
    public ResponseEntity<Map<String, Object>> syncAccounts() {
        int count = syncService.syncAccounts();
        return ok("master_accounts", count);
    }

    /** Laravel: /get-brokers-dv → master_brokers */
    @GetMapping("/brokers")
    public ResponseEntity<Map<String, Object>> syncBrokers() {
        int count = syncService.syncBrokers();
        return ok("master_brokers", count);
    }

    /** Laravel: /get-pms-dv → master_portfolio_managers */
    @GetMapping("/portfolio-managers")
    public ResponseEntity<Map<String, Object>> syncPortfolioManagers() {
        int count = syncService.syncPortfolioManagers();
        return ok("master_portfolio_managers", count);
    }

    /** Laravel: /get-products-dv → master_products */
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> syncProducts() {
        int count = syncService.syncProducts();
        return ok("master_products", count);
    }

    /**
     * Laravel: /get-plans-dv → master_plans
     * Syncs BOTH ss_plans (broker) AND ss_portfoliomanagerplans (PMS) into the same table.
     */
    @GetMapping("/plans")
    public ResponseEntity<Map<String, Object>> syncPlans() {
        int count = syncService.syncPlans();
        return ok("master_plans", count);
    }

    /** Laravel: /get-pms-plans-dv → master_pms_plans */
    @GetMapping("/pms-plans")
    public ResponseEntity<Map<String, Object>> syncPmsPlans() {
        int count = syncService.syncPmsPlans();
        return ok("master_pms_plans", count);
    }

    /** Laravel: /get-schemes-dv → master_schemes */
    @GetMapping("/schemes")
    public ResponseEntity<Map<String, Object>> syncSchemes() {
        int count = syncService.syncSchemes();
        return ok("master_schemes", count);
    }

    /** Laravel: /get-banks → master_banks */
    @GetMapping("/banks")
    public ResponseEntity<Map<String, Object>> syncBanks() {
        int count = syncService.syncBanks();
        return ok("master_banks", count);
    }

    /** Laravel: /get-broker-banks → master_broker_banks */
    @GetMapping("/broker-banks")
    public ResponseEntity<Map<String, Object>> syncBrokerBanks() {
        int count = syncService.syncBrokerBanks();
        return ok("master_broker_banks", count);
    }

    /** Laravel: /get-countries-dv → master_countries */
    @GetMapping("/countries")
    public ResponseEntity<Map<String, Object>> syncCountries() {
        int count = syncService.syncCountries();
        return ok("master_countries", count);
    }

    /**
     * Laravel: /get-nationality-dv (misleading name) → ss_investortypes → master_investor_types
     */
    @GetMapping("/investor-types")
    public ResponseEntity<Map<String, Object>> syncInvestorTypes() {
        int count = syncService.syncInvestorTypes();
        return ok("master_investor_types", count);
    }

    /**
     * No Laravel equivalent — seeds master_service_provider_type with the
     * Dynamics 365 option-set constants (100000000=Broker, etc.)
     */
    @GetMapping("/service-provider-types")
    public ResponseEntity<Map<String, Object>> seedServiceProviderTypes() {
        int count = syncService.seedServiceProviderTypes();
        return ok("master_service_provider_type", count);
    }

    /** Laravel: /get-gensers-dv → master_gender */
    @GetMapping("/genders")
    public ResponseEntity<Map<String, Object>> syncGenders() {
        int count = syncService.syncGenders();
        return ok("master_gender", count);
    }

    /** Laravel: /get-maritials-status-dv → master_maritial_status */
    @GetMapping("/marital-status")
    public ResponseEntity<Map<String, Object>> syncMaritalStatus() {
        int count = syncService.syncMaritalStatus();
        return ok("master_maritial_status", count);
    }

    /** Laravel: /get-cities-dv → master_cities */
    @GetMapping("/cities")
    public ResponseEntity<Map<String, Object>> syncCities() {
        int count = syncService.syncCities();
        return ok("master_cities", count);
    }

    /** Laravel: /get-title-dv → master_title */
    @GetMapping("/titles")
    public ResponseEntity<Map<String, Object>> syncTitles() {
        int count = syncService.syncTitles();
        return ok("master_title", count);
    }

    /** Laravel: /get-type-of-visa → master_type_of_visa */
    @GetMapping("/visa-types")
    public ResponseEntity<Map<String, Object>> syncVisaTypes() {
        int count = syncService.syncVisaTypes();
        return ok("master_type_of_visa", count);
    }

    /** Laravel: /get-pms-banks → master_pms_banks */
    @GetMapping("/pms-banks")
    public ResponseEntity<Map<String, Object>> syncPmsBanks() {
        int count = syncService.syncPmsBanks();
        return ok("master_pms_banks", count);
    }

    /** Laravel: /country-residenceisd-code-dv → master_country_of_residence */
    @GetMapping("/country-of-residence")
    public ResponseEntity<Map<String, Object>> syncCountryOfResidence() {
        int count = syncService.syncCountryOfResidence();
        return ok("master_country_of_residence", count);
    }

    /**
     * Manual trigger for the PowerApps service-provider contact sync
     * (Laravel Artisan {@code php artisan powerapp:sync-contacts}).
     * Normally runs on the scheduler every 10 minutes; this endpoint lets an
     * admin kick it off on demand for testing or backfill.
     */
    @PostMapping("/powerapp-contacts")
    public ResponseEntity<Map<String, Object>> syncPowerAppContacts() {
        PowerAppContactSyncService svc = powerAppContactSyncServiceProvider.getIfAvailable();
        if (svc == null) {
            return ResponseEntity.status(503).body(Map.of(
                    "status", "unavailable",
                    "message", "PowerAppContactSyncService not configured"));
        }
        log.info("DataverseMasterSyncController: powerapp contact sync triggered manually");
        Map<String, Object> summary = svc.sync();
        return ResponseEntity.ok(Map.of(
                "message", "PowerApp contact sync completed",
                "summary", summary));
    }

    private ResponseEntity<Map<String, Object>> ok(String table, int count) {
        return ResponseEntity.ok(Map.of(
                "table", table,
                "synced", count,
                "status", "ok"
        ));
    }
}
