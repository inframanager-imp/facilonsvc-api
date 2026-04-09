package com.facilon.app.service;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.model.Tenant;
import com.facilon.app.model.TenantSharePointConfig;
import com.facilon.app.repository.TenantSharePointConfigRepository;
import com.facilon.app.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for tenant-specific SharePoint configuration.
 * Config is loaded from database (tenant_sharepoint_config table).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantSharePointConfigService {

    private static final long DEFAULT_TENANT_ID = 1L;

    private final TenantSharePointConfigRepository sharePointConfigRepository;
    private final TenantRepository tenantRepository;

    @Cacheable(value = "tenantSharePointConfig", key = "#tenantId")
    public Optional<TenantSharePointConfig> getConfigForTenant(Long tenantId) {
        return sharePointConfigRepository.findByTenant_TenantId(tenantId);
    }

    /**
     * Get SharePoint config for the given tenant from DB only.
     * Returns null if not configured or disabled.
     */
    public TenantSharePointConfig getConfigForTenantOrNull(Long tenantId) {
        Optional<TenantSharePointConfig> opt = getConfigForTenant(tenantId);
        if (opt.isEmpty()) {
            log.warn("No SharePoint config record found in DB for tenant: {}", tenantId);
            return null;
        }
        
        TenantSharePointConfig cfg = opt.get();
        log.info("SharePoint config record found for tenant: {} | enabled={} | siteHostname={} | tenantIdAzure={} | clientId={} | clientSecret={}",
                tenantId,
                cfg.getEnabled(),
                cfg.getSiteHostname() != null ? cfg.getSiteHostname() : "NULL",
                cfg.getTenantIdAzure() != null ? cfg.getTenantIdAzure() : "NULL",
                cfg.getClientId() != null ? cfg.getClientId() : "NULL",
                cfg.getClientSecret() != null && !cfg.getClientSecret().isEmpty() ? "SET" : "NULL/EMPTY");
        
        // Validate all required fields
        if (!Boolean.TRUE.equals(cfg.getEnabled())) {
            log.warn("SharePoint config is DISABLED for tenant: {}", tenantId);
            return null;
        }
        if (cfg.getClientId() == null || cfg.getClientId().isEmpty()) {
            log.warn("SharePoint config missing CLIENT_ID for tenant: {}", tenantId);
            return null;
        }
        if (cfg.getClientSecret() == null || cfg.getClientSecret().isEmpty()) {
            log.warn("SharePoint config missing CLIENT_SECRET for tenant: {}", tenantId);
            return null;
        }
        if (cfg.getTenantIdAzure() == null || cfg.getTenantIdAzure().isEmpty()) {
            log.warn("SharePoint config missing TENANT_ID_AZURE for tenant: {}", tenantId);
            return null;
        }
        if (cfg.getSiteHostname() == null || cfg.getSiteHostname().isEmpty()) {
            log.warn("SharePoint config missing SITE_HOSTNAME for tenant: {}", tenantId);
            return null;
        }
        
        log.info("✅ SharePoint config is VALID and ENABLED for tenant: {}", tenantId);
        return cfg;
    }

    /**
     * Get config for current tenant from TenantContextHolder, or default tenant.
     */
    public TenantSharePointConfig getConfigForCurrentTenantOrDefault() {
        Long tenantId = DEFAULT_TENANT_ID;
        var ctx = TenantContextHolder.getContext();
        if (ctx != null && ctx.getTenant() != null && ctx.getTenant().getTenantId() != null) {
            tenantId = ctx.getTenant().getTenantId();
        }
        return getConfigForTenantOrNull(tenantId);
    }

    public boolean isSharePointEnabled(Long tenantId) {
        TenantSharePointConfig cfg = getConfigForTenantOrNull(tenantId);
        return cfg != null && Boolean.TRUE.equals(cfg.getEnabled());
    }

    @CacheEvict(value = "tenantSharePointConfig", key = "#tenantId")
    @Transactional
    public TenantSharePointConfig saveOrUpdate(Long tenantId, TenantSharePointConfig config) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));
        
        Optional<TenantSharePointConfig> existing = sharePointConfigRepository.findByTenant_TenantId(tenantId);
        TenantSharePointConfig toSave = existing.orElse(new TenantSharePointConfig());
        
        toSave.setTenant(tenant);
        toSave.setEnabled(config.getEnabled());
        toSave.setSiteHostname(config.getSiteHostname());
        toSave.setSitePath(config.getSitePath());
        toSave.setTenantIdAzure(config.getTenantIdAzure());
        toSave.setClientId(config.getClientId());
        
        if (config.getClientSecret() != null && !config.getClientSecret().isEmpty()) {
            toSave.setClientSecret(config.getClientSecret());
        }
        
        return sharePointConfigRepository.save(toSave);
    }
}
