package com.facilon.app.service;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.model.Tenant;
import com.facilon.app.model.TenantDynamicsConfig;
import com.facilon.app.repository.TenantDynamicsConfigRepository;
import com.facilon.app.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for tenant-specific Dynamics 365 configuration.
 * Config is loaded from database (tenant_dynamics_config table).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantDynamicsConfigService {

    private static final long DEFAULT_TENANT_ID = 1L;

    private final TenantDynamicsConfigRepository dynamicsConfigRepository;
    private final TenantRepository tenantRepository;

    @Cacheable(value = "tenantDynamicsConfig", key = "#tenantId")
    public Optional<TenantDynamicsConfig> getConfigForTenant(Long tenantId) {
        return dynamicsConfigRepository.findByTenant_TenantId(tenantId);
    }

    /**
     * Get Dynamics config for the given tenant from DB only.
     * Returns null if not configured or disabled.
     */
    public TenantDynamicsConfig getConfigForTenantOrNull(Long tenantId) {
        log.debug("Fetching Dynamics config for tenant: {}", tenantId);
        Optional<TenantDynamicsConfig> opt = getConfigForTenant(tenantId);
        
        if (!opt.isPresent()) {
            log.warn("No Dynamics config found in database for tenant: {}", tenantId);
            return null;
        }
        
        TenantDynamicsConfig cfg = opt.get();
        log.debug("Found Dynamics config for tenant {}: enabled={}, clientId={}, resource={}", 
                tenantId, cfg.getEnabled(), cfg.getClientId() != null ? "***" : "null", cfg.getResource());
        
        if (!Boolean.TRUE.equals(cfg.getEnabled())) {
            log.warn("Dynamics config is DISABLED for tenant: {}", tenantId);
            return null;
        }
        
        if (cfg.getClientId() == null || cfg.getClientId().isEmpty()) {
            log.warn("Dynamics config missing client_id for tenant: {}", tenantId);
            return null;
        }
        
        if (cfg.getClientSecret() == null || cfg.getClientSecret().isEmpty()) {
            log.warn("Dynamics config missing client_secret for tenant: {}", tenantId);
            return null;
        }
        
        if (cfg.getTenantIdAzure() == null || cfg.getTenantIdAzure().isEmpty()) {
            log.warn("Dynamics config missing tenant_id_azure for tenant: {}", tenantId);
            return null;
        }
        
        log.info("✅ Valid Dynamics config loaded for tenant: {} (resource: {})", tenantId, cfg.getResource());
        return cfg;
    }

    /**
     * Get config for current tenant from TenantContextHolder, or default tenant.
     */
    public TenantDynamicsConfig getConfigForCurrentTenantOrDefault() {
        Long tenantId = DEFAULT_TENANT_ID;
        var ctx = TenantContextHolder.getContext();
        if (ctx != null && ctx.getTenant() != null && ctx.getTenant().getTenantId() != null) {
            tenantId = ctx.getTenant().getTenantId();
        }
        return getConfigForTenantOrNull(tenantId);
    }

    public boolean isDynamicsEnabled(Long tenantId) {
        TenantDynamicsConfig cfg = getConfigForTenantOrNull(tenantId);
        return cfg != null && Boolean.TRUE.equals(cfg.getEnabled());
    }

    @CacheEvict(value = "tenantDynamicsConfig", key = "#tenantId")
    @Transactional
    public TenantDynamicsConfig saveOrUpdate(Long tenantId, TenantDynamicsConfig config) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));
        
        Optional<TenantDynamicsConfig> existing = dynamicsConfigRepository.findByTenant_TenantId(tenantId);
        TenantDynamicsConfig toSave = existing.orElse(new TenantDynamicsConfig());
        
        toSave.setTenant(tenant);
        toSave.setEnabled(config.getEnabled());
        toSave.setTenantIdAzure(config.getTenantIdAzure());
        toSave.setClientId(config.getClientId());
        
        if (config.getClientSecret() != null && !config.getClientSecret().isEmpty()) {
            toSave.setClientSecret(config.getClientSecret());
        }
        
        toSave.setResource(config.getResource());
        
        return dynamicsConfigRepository.save(toSave);
    }
}
