package com.facilon.app.service;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.model.TenantGraphEmailConfig;
import com.facilon.app.repository.TenantGraphEmailConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantGraphEmailConfigService {

    private static final long DEFAULT_TENANT_ID = 1L;

    private final TenantGraphEmailConfigRepository repository;

    @Cacheable(value = "tenantGraphEmailConfig", key = "#tenantId")
    public Optional<TenantGraphEmailConfig> getConfigForTenant(Long tenantId) {
        return repository.findByTenant_TenantId(tenantId);
    }

    public TenantGraphEmailConfig getConfigForTenantOrFallback(Long tenantId) {
        Optional<TenantGraphEmailConfig> opt = getConfigForTenant(tenantId);
        if (opt.isPresent()) {
            TenantGraphEmailConfig cfg = opt.get();
            if (Boolean.TRUE.equals(cfg.getEnabled()) && cfg.getClientSecret() != null && !cfg.getClientSecret().isEmpty()) {
                return cfg;
            }
        }
        return null;
    }

    public TenantGraphEmailConfig getConfigForCurrentTenantOrDefault() {
        Long tenantId = DEFAULT_TENANT_ID;
        var ctx = TenantContextHolder.getContext();
        if (ctx != null && ctx.getTenant() != null && ctx.getTenant().getTenantId() != null) {
            tenantId = ctx.getTenant().getTenantId();
        }
        return getConfigForTenantOrFallback(tenantId);
    }

    public boolean isEnabled(Long tenantId) {
        TenantGraphEmailConfig cfg = getConfigForTenantOrFallback(tenantId);
        return cfg != null && Boolean.TRUE.equals(cfg.getEnabled())
                && cfg.getClientSecret() != null && !cfg.getClientSecret().isEmpty();
    }
}
