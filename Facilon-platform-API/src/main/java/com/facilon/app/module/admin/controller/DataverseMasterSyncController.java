package com.facilon.app.module.admin.controller;

import com.facilon.app.integration.dynamics.DataverseMasterSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private ResponseEntity<Map<String, Object>> ok(String table, int count) {
        return ResponseEntity.ok(Map.of(
                "table", table,
                "synced", count,
                "status", "ok"
        ));
    }
}
