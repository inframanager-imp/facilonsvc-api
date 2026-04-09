package com.facilon.app.controller.admin;

import com.facilon.app.model.TenantB2CConfig;
import com.facilon.app.service.TenantB2CConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Admin API for managing tenant-specific Azure B2C configuration.
 */
@RestController
@RequestMapping("/api/admin/tenants/{tenantId}/b2c-config")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin - Tenant B2C Config", description = "Manage Azure B2C configuration per tenant")
public class TenantB2CConfigAdminController {

    private final TenantB2CConfigService b2cConfigService;

    @GetMapping
    @PreAuthorize("hasAnyRole('PLATFORM_SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Get B2C config for tenant")
    public ResponseEntity<?> getConfig(@PathVariable Long tenantId) {
        return b2cConfigService.getConfigForTenant(tenantId)
                .map(cfg -> ResponseEntity.ok(toResponseDto(cfg)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('PLATFORM_SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Create or update B2C config for tenant")
    public ResponseEntity<?> upsertConfig(@PathVariable Long tenantId, @RequestBody Map<String, Object> body) {
        try {
            TenantB2CConfig cfg = TenantB2CConfig.builder()
                    .enabled(body.containsKey("enabled") ? Boolean.TRUE.equals(body.get("enabled")) : true)
                    .b2cTenantName((String) body.get("b2cTenantName"))
                    .b2cTenantId((String) body.getOrDefault("b2cTenantId", ""))
                    .clientId((String) body.get("clientId"))
                    .clientSecret((String) body.get("clientSecret"))
                    .signupSigninPolicy((String) body.get("signupSigninPolicy"))
                    .logoutPolicy((String) body.get("logoutPolicy"))
                    .resetPasswordPolicy((String) body.get("resetPasswordPolicy"))
                    .redirectUri((String) body.get("redirectUri"))
                    .postLogoutRedirectUri((String) body.get("postLogoutRedirectUri"))
                    .scope((String) body.getOrDefault("scope", "openid profile email offline_access"))
                    .build();
            TenantB2CConfig saved = b2cConfigService.saveOrUpdate(tenantId, cfg);
            return ResponseEntity.ok(toResponseDto(saved));
        } catch (Exception e) {
            log.error("Failed to save B2C config: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, Object> toResponseDto(TenantB2CConfig cfg) {
        String secret = cfg.getClientSecret();
        String masked = secret != null && secret.length() > 4
                ? "****" + secret.substring(secret.length() - 4) : "****";
        Map<String, Object> m = new HashMap<>();
        m.put("id", cfg.getId());
        m.put("tenantId", cfg.getTenant() != null ? cfg.getTenant().getTenantId() : null);
        m.put("enabled", cfg.getEnabled());
        m.put("b2cTenantName", cfg.getB2cTenantName());
        m.put("b2cTenantId", cfg.getB2cTenantId() != null ? cfg.getB2cTenantId() : "");
        m.put("clientId", cfg.getClientId());
        m.put("clientSecretMasked", masked);
        m.put("signupSigninPolicy", cfg.getSignupSigninPolicy());
        m.put("logoutPolicy", cfg.getLogoutPolicy());
        m.put("resetPasswordPolicy", cfg.getResetPasswordPolicy());
        m.put("redirectUri", cfg.getRedirectUri());
        m.put("postLogoutRedirectUri", cfg.getPostLogoutRedirectUri());
        m.put("scope", cfg.getScope() != null ? cfg.getScope() : "openid profile email offline_access");
        return m;
    }
}
