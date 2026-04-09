package com.facilon.app.controller;

import com.facilon.app.model.TenantB2CConfig;
import com.facilon.app.service.TenantB2CConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Public API for auth-related config consumed by the frontend (e.g. MSAL B2C config).
 * No authentication required.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth Config", description = "Public auth configuration for frontend")
public class AuthConfigController {

    private static final long DEFAULT_TENANT_ID = 1L;

    private final TenantB2CConfigService b2cConfigService;

    /**
     * Returns public B2C configuration for the default tenant so the frontend can configure MSAL.
     * Does not include clientSecret. Callable without authentication.
     */
    @GetMapping("/b2c-config")
    @Operation(summary = "Get public B2C config for frontend MSAL")
    public ResponseEntity<Map<String, Object>> getB2cConfig() {
        TenantB2CConfig cfg = b2cConfigService.getConfigForTenantOrFallback(DEFAULT_TENANT_ID);
        if (cfg == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> body = new HashMap<>();
        body.put("enabled", cfg.getEnabled());
        body.put("b2cTenantName", cfg.getB2cTenantName());
        body.put("b2cTenantId", cfg.getB2cTenantId() != null ? cfg.getB2cTenantId() : "");
        body.put("clientId", cfg.getClientId());
        body.put("signupSigninPolicy", cfg.getSignupSigninPolicy());
        body.put("logoutPolicy", cfg.getLogoutPolicy());
        body.put("resetPasswordPolicy", cfg.getResetPasswordPolicy());
        body.put("redirectUri", cfg.getRedirectUri());
        body.put("postLogoutRedirectUri", cfg.getPostLogoutRedirectUri());
        body.put("scope", cfg.getScope() != null ? cfg.getScope() : "openid profile email offline_access");
        return ResponseEntity.ok(body);
    }
}
