package com.facilon.app.config;

import com.facilon.app.integration.dynamics.DynamicsCrmService;
import com.facilon.app.integration.dynamics.DynamicsTokenProvider;
import com.facilon.app.service.TenantDynamicsConfigService;
import com.facilon.app.service.TenantSharePointConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Logs Dataverse integration status on application startup.
 */
@Component
@Slf4j
public class DataverseIntegrationLogger {

    @Autowired(required = false)
    private TenantDynamicsConfigService dynamicsConfigService;

    @Autowired(required = false)
    private TenantSharePointConfigService sharePointConfigService;

    @Autowired(required = false)
    private DynamicsTokenProvider dynamicsTokenProvider;

    @Autowired(required = false)
    private DynamicsCrmService dynamicsCrmService;

    @EventListener(ApplicationReadyEvent.class)
    public void logDataverseIntegrationStatus() {
        log.info("========================================");
        log.info("📊 DATAVERSE INTEGRATION STATUS");
        log.info("========================================");
        
        if (dynamicsConfigService != null) {
            log.info("✅ TenantDynamicsConfigService bean LOADED");
        } else {
            log.error("❌ TenantDynamicsConfigService bean NOT LOADED");
        }
        
        if (sharePointConfigService != null) {
            log.info("✅ TenantSharePointConfigService bean LOADED");
        } else {
            log.error("❌ TenantSharePointConfigService bean NOT LOADED");
        }
        
        if (dynamicsTokenProvider != null) {
            log.info("✅ DynamicsTokenProvider bean LOADED");
        } else {
            log.error("❌ DynamicsTokenProvider bean NOT LOADED");
        }
        
        if (dynamicsCrmService != null) {
            log.info("✅ DynamicsCrmService bean LOADED");
        } else {
            log.error("❌ DynamicsCrmService bean NOT LOADED");
        }
        
        // Test configuration fetch
        if (dynamicsConfigService != null) {
            try {
                var config = dynamicsConfigService.getConfigForTenantOrNull(1L);
                if (config != null) {
                    log.info("✅ Dynamics config found for tenant 1: enabled={}, resource={}", 
                            config.getEnabled(), config.getResource());
                } else {
                    log.warn("⚠️ No Dynamics config found in database for tenant 1");
                    log.warn("   Run SQL: INSERT INTO tenant_dynamics_config ...");
                }
            } catch (Exception e) {
                log.error("❌ Error fetching Dynamics config: {} - {}", e.getClass().getSimpleName(), e.getMessage());
                log.error("   Table may not exist. Check migrations: V4__create_tenant_dynamics_config.sql");
            }
        }
        
        log.info("========================================");
    }
}
