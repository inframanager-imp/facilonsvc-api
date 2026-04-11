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
 * Scheduled job that syncs document approval/rejection status from Dataverse
 * back to the local {@code kyc_documents} table.
 *
 * <p>Direct mirror of Laravel's {@code CheckInvestorDocumentStatus} Artisan command
 * which runs on the Laravel scheduler.
 *
 * <h3>Configuration (application-dev.yml)</h3>
 * <pre>
 * dataverse:
 *   document-status-sync:
 *     cron: "0 *&#47;15 * * * *"    # every 15 minutes (default)
 *     cron-enabled: true
 * </pre>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentStatusSyncScheduler {

    private final DocumentStatusSyncService syncService;

    @Value("${dataverse.document-status-sync.cron-enabled:true}")
    private boolean cronEnabled;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Scheduled(cron = "${dataverse.document-status-sync.cron:0 */15 * * * *}")
    public void scheduledSync() {
        if (!cronEnabled) {
            log.debug("[DocStatusSync] Cron skipped (cron-enabled=false)");
            return;
        }
        log.info("[DocStatusSync] ══════════════════════════════════════════════════");
        log.info("[DocStatusSync] Scheduled sync triggered at {}", LocalDateTime.now().format(FMT));
        log.info("[DocStatusSync] ══════════════════════════════════════════════════");

        long start = System.currentTimeMillis();
        try {
            Map<String, Object> results = syncService.syncAllInvestorDocumentStatuses();
            long elapsed = System.currentTimeMillis() - start;

            log.info("[DocStatusSync] Completed in {}ms. Results:", elapsed);
            results.forEach((key, value) ->
                    log.info("[DocStatusSync]   {} → {}", key, value));
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("[DocStatusSync] Failed after {}ms: {}", elapsed, e.getMessage(), e);
        }
    }
}
