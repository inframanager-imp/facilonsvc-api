package com.facilon.app.integration.dynamics;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.model.Tenant;
import com.facilon.app.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Cron-driven wrapper around {@link LatestInvestorCheckService}.
 *
 * <p>Direct mirror of Laravel's {@code latest_investor_check} closure scheduled
 * {@code ->everyMinute()} in {@code Investor/app/Console/Kernel.php}. Laravel ran
 * single-tenant against one Dynamics org; this app is multi-tenant, so the cron
 * fans out over every active tenant, setting the tenant context for each so the
 * Dynamics token, {@code master_accounts} lookup, {@code introduce_investor_emails}
 * dedup and Graph mail config all resolve per tenant.
 *
 * <p>Spring's default scheduler is single-threaded, so a slow run skips the next
 * firing rather than overlapping (matches Laravel's {@code ->withoutOverlapping()}).
 *
 * <h3>Configuration (application-*.yml)</h3>
 * <pre>
 * dataverse:
 *   latest-investor-check:
 *     cron: "0 * * * * *"       # every minute (matches Laravel ->everyMinute())
 *     cron-enabled: true
 *     intro-link-base-url: https://demo.facilonservices.com/demo/investor/introduce-investor1
 * </pre>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LatestInvestorCheckScheduler {

    private final LatestInvestorCheckService service;
    private final TenantRepository tenantRepository;

    @Value("${dataverse.latest-investor-check.cron-enabled:true}")
    private boolean cronEnabled;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Scheduled(cron = "${dataverse.latest-investor-check.cron:0 * * * * *}")
    public void scheduledCheck() {
        if (!cronEnabled) {
            log.debug("[LatestInvestorCheck] Cron skipped (cron-enabled=false)");
            return;
        }

        List<Tenant> tenants = tenantRepository.findByIsActiveTrue();
        if (tenants.isEmpty()) {
            log.debug("[LatestInvestorCheck] No active tenants; nothing to do");
            return;
        }

        log.info("[LatestInvestorCheck] Triggered at {} for {} active tenant(s)",
                LocalDateTime.now().format(FMT), tenants.size());

        long start = System.currentTimeMillis();
        for (Tenant tenant : tenants) {
            runForTenant(tenant);
        }
        log.info("[LatestInvestorCheck] All tenants done in {}ms", System.currentTimeMillis() - start);
    }

    private void runForTenant(Tenant tenant) {
        try {
            TenantContextHolder.setTenant(tenant);
            Map<String, Object> summary = service.run();
            log.info("[LatestInvestorCheck][tenant={}] {}", tenant.getTenantId(), summary);
        } catch (Exception e) {
            log.error("[LatestInvestorCheck][tenant={}] Failed: {}",
                    tenant.getTenantId(), e.getMessage(), e);
        } finally {
            TenantContextHolder.reset();
        }
    }
}
