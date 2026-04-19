package com.facilon.app.integration.dynamics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Cron-driven wrapper around {@link PowerAppContactSyncService}.
 *
 * <p>Direct mirror of Laravel's {@code powerapp:sync-contacts} Artisan command
 * which runs on the Laravel scheduler.  Default cadence is every 10 minutes —
 * low enough latency for service-provider onboarding while avoiding Dataverse
 * throttling.
 *
 * <h3>Configuration (application-*.yml)</h3>
 * <pre>
 * dataverse:
 *   powerapp-sync:
 *     cron: "0 *&#47;10 * * * *"    # every 10 minutes (default)
 *     cron-enabled: true
 * </pre>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PowerAppContactSyncScheduler {

    private final PowerAppContactSyncService syncService;

    @Value("${dataverse.powerapp-sync.cron-enabled:true}")
    private boolean cronEnabled;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Scheduled(cron = "${dataverse.powerapp-sync.cron:0 */10 * * * *}")
    public void scheduledSync() {
        if (!cronEnabled) {
            log.debug("[PowerAppSync] Cron skipped (cron-enabled=false)");
            return;
        }
        log.info("[PowerAppSync] ══════════════════════════════════════════════════");
        log.info("[PowerAppSync] Scheduled sync triggered at {}", LocalDateTime.now().format(FMT));
        log.info("[PowerAppSync] ══════════════════════════════════════════════════");

        long start = System.currentTimeMillis();
        try {
            Map<String, Object> summary = syncService.sync();
            long elapsed = System.currentTimeMillis() - start;
            log.info("[PowerAppSync] Completed in {}ms.  Summary:", elapsed);
            summary.forEach((k, v) -> log.info("[PowerAppSync]   {} → {}", k, v));
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("[PowerAppSync] Failed after {}ms: {}", elapsed, e.getMessage(), e);
        }
    }
}
