package com.facilon.app.integration.dynamics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Mirrors Laravel's Dataverse master-sync behaviour:
 *
 * <p>Laravel approach: a developer manually visits a URL in the browser
 * (e.g. {@code /all-masters-dv}) whenever master data changes in Dynamics 365.
 * There is NO scheduled job for master tables in Laravel.
 *
 * <p>Spring Boot improvement: we do the same sync automatically on two occasions:
 * <ol>
 *   <li><b>Server startup</b> — runs once immediately after the application context
 *       is fully ready, so master tables are always populated on a fresh deploy.
 *   <li><b>Daily cron</b> — keeps tables in sync with Dataverse changes
 *       (new brokers, products, plans added by admins in Dynamics 365).
 * </ol>
 *
 * <h3>Configuration (application-dev.yml)</h3>
 * <pre>
 * dataverse:
 *   master-sync:
 *     run-on-startup: true          # sync once on server start
 *     cron: "0 0 2 * * *"          # daily at 2 AM
 *     cron-enabled: true            # set false to disable the daily cron
 * </pre>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataverseMasterSyncScheduler implements ApplicationRunner {

    private final DataverseMasterSyncService syncService;

    @Value("${dataverse.master-sync.run-on-startup:true}")
    private boolean runOnStartup;

    @Value("${dataverse.master-sync.cron-enabled:true}")
    private boolean cronEnabled;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ─────────────────────────────────────────────────────────────────────────
    // Startup sync  — runs once after Spring context is fully loaded
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Called by Spring after the application is fully started (all beans wired,
     * DB connections established, security context ready).
     * Runs in the main startup thread — any failure is logged but does NOT
     * prevent the server from starting.
     */
    @Override
    public void run(ApplicationArguments args) {
        if (!runOnStartup) {
            log.info("[MasterSync] Startup sync is disabled (dataverse.master-sync.run-on-startup=false)");
            return;
        }
        log.info("[MasterSync] ══════════════════════════════════════════════════");
        log.info("[MasterSync] Server startup — running initial master data sync");
        log.info("[MasterSync] ══════════════════════════════════════════════════");
        executeSync("STARTUP");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Daily cron  — keeps tables fresh after Dynamics 365 changes
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Scheduled cron job.  Default: every day at 02:00 AM.
     *
     * <p>The cron expression is read from
     * {@code dataverse.master-sync.cron} (application.yml).
     * The job is guarded by {@code cron-enabled} so it can be turned off
     * per environment without removing the annotation.
     *
     * <p>Uses {@code @Scheduled(cron = ...)} with a Spring EL expression so the
     * value comes from config. If the property is missing it defaults to 2 AM daily.
     */
    @Scheduled(cron = "${dataverse.master-sync.cron:0 0 2 * * *}")
    public void scheduledDailySync() {
        if (!cronEnabled) {
            log.debug("[MasterSync] Daily cron skipped (cron-enabled=false)");
            return;
        }
        log.info("[MasterSync] ══════════════════════════════════════════════════");
        log.info("[MasterSync] Daily cron triggered at {}", LocalDateTime.now().format(FMT));
        log.info("[MasterSync] ══════════════════════════════════════════════════");
        executeSync("CRON");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Shared execution logic
    // ─────────────────────────────────────────────────────────────────────────

    private void executeSync(String trigger) {
        long start = System.currentTimeMillis();
        try {
            Map<String, Object> results = syncService.syncAllMasters();
            long elapsed = System.currentTimeMillis() - start;

            log.info("[MasterSync][{}] Completed in {}ms. Results:", trigger, elapsed);
            results.forEach((table, result) ->
                    log.info("[MasterSync][{}]   {:30s} → {}", trigger, table, result));

        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("[MasterSync][{}] Failed after {}ms: {}", trigger, elapsed, e.getMessage(), e);
        }
    }
}
