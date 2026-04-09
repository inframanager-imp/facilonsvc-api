package com.facilon.app.service;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.model.Tenant;
import com.facilon.app.model.TenantB2CConfig;
import com.facilon.app.repository.TenantB2CConfigRepository;
import com.facilon.app.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for tenant-specific Azure B2C configuration.
 * Config is loaded from database only.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantB2CConfigService {

    private static final String STATE_PREFIX = "tid:";
    private static final long DEFAULT_TENANT_ID = 1L;

    private final TenantB2CConfigRepository b2cConfigRepository;
    private final TenantRepository tenantRepository;

    @Cacheable(value = "tenantB2CConfig", key = "#tenantId")
    public Optional<TenantB2CConfig> getConfigForTenant(Long tenantId) {
        return b2cConfigRepository.findByTenant_TenantId(tenantId);
    }

    /**
     * Get B2C config for the given tenant from DB only.
     */
    public TenantB2CConfig getConfigForTenantOrFallback(Long tenantId) {
        Optional<TenantB2CConfig> opt = getConfigForTenant(tenantId);
        if (opt.isPresent()) {
            TenantB2CConfig cfg = opt.get();
            if (Boolean.TRUE.equals(cfg.getEnabled()) && cfg.getClientSecret() != null
                    && !cfg.getClientSecret().isEmpty()
                    && !"PLACEHOLDER_UPDATE_VIA_ADMIN_API".equals(cfg.getClientSecret())) {
                return cfg;
            }
        }
        return null;
    }

    /**
     * Get config for current tenant from TenantContextHolder, or default tenant.
     */
    public TenantB2CConfig getConfigForCurrentTenantOrDefault() {
        Long tenantId = DEFAULT_TENANT_ID;
        var ctx = TenantContextHolder.getContext();
        if (ctx != null && ctx.getTenant() != null && ctx.getTenant().getTenantId() != null) {
            tenantId = ctx.getTenant().getTenantId();
        }
        return getConfigForTenantOrFallback(tenantId);
    }

    public boolean isB2CEnabled(Long tenantId) {
        TenantB2CConfig cfg = getConfigForTenantOrFallback(tenantId);
        return cfg != null && Boolean.TRUE.equals(cfg.getEnabled())
                && cfg.getClientSecret() != null && !cfg.getClientSecret().isEmpty();
    }

    public String buildLoginUrl(Long tenantId, String state) {
        TenantB2CConfig cfg = getConfigForTenantOrFallback(tenantId);
        if (cfg == null) return null;
        return buildLoginUrlInternal(cfg, state);
    }

    public String buildLogoutUrl(Long tenantId) {
        TenantB2CConfig cfg = getConfigForTenantOrFallback(tenantId);
        if (cfg == null) return null;
        return buildLogoutUrlInternal(cfg);
    }

    public String buildTokenEndpoint(TenantB2CConfig cfg) {
        boolean isCiam = isCiamMode(cfg);
        if (isCiam) {
            String authorityBase = String.format("https://%s.ciamlogin.com/%s", cfg.getB2cTenantName(), cfg.getB2cTenantId());
            if (cfg.getB2cTenantName().contains(".")) {
                authorityBase = String.format("https://%s/%s", cfg.getB2cTenantName(), cfg.getB2cTenantId());
            }
            return authorityBase + "/oauth2/v2.0/token";
        }
        return String.format("https://%s.b2clogin.com/%s.onmicrosoft.com/%s/oauth2/v2.0/token",
                cfg.getB2cTenantName(), cfg.getB2cTenantName(), cfg.getSignupSigninPolicy());
    }

    public Map<String, String> exchangeCodeForTokenParams(TenantB2CConfig cfg, String code) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("client_id", cfg.getClientId());
        params.put("client_secret", cfg.getClientSecret());
        params.put("code", code);
        params.put("redirect_uri", cfg.getRedirectUri());
        params.put("scope", cfg.getScope() != null ? cfg.getScope() : "openid profile email offline_access");
        return params;
    }

    public static String encodeState(Long tenantId) {
        return STATE_PREFIX + Base64.getEncoder().encodeToString(String.valueOf(tenantId).getBytes(StandardCharsets.UTF_8));
    }

    public static Long decodeState(String state) {
        if (state == null || !state.startsWith(STATE_PREFIX)) return DEFAULT_TENANT_ID;
        try {
            String encoded = state.substring(STATE_PREFIX.length());
            return Long.parseLong(new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8));
        } catch (Exception e) {
            return DEFAULT_TENANT_ID;
        }
    }

    @CacheEvict(value = "tenantB2CConfig", key = "#tenantId")
    @Transactional
    public TenantB2CConfig saveOrUpdate(Long tenantId, TenantB2CConfig config) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));
        Optional<TenantB2CConfig> existing = b2cConfigRepository.findByTenant_TenantId(tenantId);
        TenantB2CConfig toSave = existing.orElse(new TenantB2CConfig());
        toSave.setTenant(tenant);
        toSave.setEnabled(config.getEnabled());
        toSave.setB2cTenantName(config.getB2cTenantName());
        toSave.setB2cTenantId(config.getB2cTenantId());
        toSave.setClientId(config.getClientId());
        if (config.getClientSecret() != null && !config.getClientSecret().isEmpty()) {
            toSave.setClientSecret(config.getClientSecret());
        }
        toSave.setSignupSigninPolicy(config.getSignupSigninPolicy());
        toSave.setLogoutPolicy(config.getLogoutPolicy());
        toSave.setResetPasswordPolicy(config.getResetPasswordPolicy());
        toSave.setRedirectUri(config.getRedirectUri());
        toSave.setPostLogoutRedirectUri(config.getPostLogoutRedirectUri());
        toSave.setScope(config.getScope());
        return b2cConfigRepository.save(toSave);
    }

    private String buildLoginUrlInternal(TenantB2CConfig cfg, String state) {
        String actualState = state != null && !state.isEmpty() ? state : encodeState(cfg.getTenant().getTenantId());
        boolean isCiam = isCiamMode(cfg);

        if (isCiam) {
            String authorityBase = String.format("https://%s.ciamlogin.com/%s", cfg.getB2cTenantName(), cfg.getB2cTenantId());
            if (cfg.getB2cTenantName().contains(".")) {
                authorityBase = String.format("https://%s/%s", cfg.getB2cTenantName(), cfg.getB2cTenantId());
            }
            return UriComponentsBuilder.fromHttpUrl(authorityBase + "/oauth2/v2.0/authorize")
                    .queryParam("client_id", cfg.getClientId())
                    .queryParam("nonce", UUID.randomUUID())
                    .queryParam("redirect_uri", cfg.getRedirectUri())
                    .queryParam("scope", cfg.getScope() != null ? cfg.getScope() : "openid profile email offline_access")
                    .queryParam("response_type", "code")
                    .queryParam("prompt", "login")
                    .queryParam("state", actualState)
                    .build().toUriString();
        }

        String authorityBase = String.format("https://%s.b2clogin.com/%s.onmicrosoft.com", cfg.getB2cTenantName(), cfg.getB2cTenantName());
        return UriComponentsBuilder.fromHttpUrl(authorityBase + "/oauth2/v2.0/authorize")
                .queryParam("p", cfg.getSignupSigninPolicy())
                .queryParam("client_id", cfg.getClientId())
                .queryParam("nonce", UUID.randomUUID())
                .queryParam("redirect_uri", cfg.getRedirectUri())
                .queryParam("scope", cfg.getScope() != null ? cfg.getScope() : "openid profile email offline_access")
                .queryParam("response_type", "code")
                .queryParam("prompt", "login")
                .queryParam("state", actualState)
                .build().toUriString();
    }

    private String buildLogoutUrlInternal(TenantB2CConfig cfg) {
        boolean isCiam = isCiamMode(cfg);
        if (isCiam) {
            String authorityBase = String.format("https://%s.ciamlogin.com/%s", cfg.getB2cTenantName(), cfg.getB2cTenantId());
            if (cfg.getB2cTenantName().contains(".")) {
                authorityBase = String.format("https://%s/%s", cfg.getB2cTenantName(), cfg.getB2cTenantId());
            }
            return UriComponentsBuilder.fromHttpUrl(authorityBase + "/oauth2/v2.0/logout")
                    .queryParam("post_logout_redirect_uri", cfg.getPostLogoutRedirectUri())
                    .build().toUriString();
        }
        String authorityBase = String.format("https://%s.b2clogin.com/%s.onmicrosoft.com", cfg.getB2cTenantName(), cfg.getB2cTenantName());
        return UriComponentsBuilder.fromHttpUrl(authorityBase + "/" + cfg.getLogoutPolicy() + "/oauth2/v2.0/logout")
                .queryParam("post_logout_redirect_uri", cfg.getPostLogoutRedirectUri())
                .build().toUriString();
    }

    private boolean isCiamMode(TenantB2CConfig cfg) {
        return cfg.getB2cTenantName() != null && cfg.getB2cTenantName().contains("ciamlogin.com")
                || (cfg.getB2cTenantId() != null && !cfg.getB2cTenantId().isEmpty() && !cfg.getB2cTenantName().contains("b2clogin.com"));
    }
}
