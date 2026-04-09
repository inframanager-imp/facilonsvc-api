package com.facilon.app.integration.dynamics;

import com.facilon.app.model.TenantDynamicsConfig;
import com.facilon.app.service.TenantDynamicsConfigService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

/**
 * Provides OAuth tokens for Dynamics 365 API access.
 * Configuration is loaded from database (tenant_dynamics_config table).
 * Similar to Laravel's getDynamicsToken() in InnerPageController.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DynamicsTokenProvider {

    private final RestTemplate restTemplate = new RestTemplate();
    private final TenantDynamicsConfigService dynamicsConfigService;

    private String cachedToken;
    private Instant tokenExpiry;
    private Long cachedForTenantId;

    /**
     * Get Dynamics 365 access token (cached for 50 minutes).
     * Configuration loaded from database (tenant_dynamics_config table).
     * Aligns with Laravel: getDynamicsToken()
     */
    public synchronized String getDynamicsToken() {
        log.debug("🔐 Acquiring Dynamics token...");
        TenantDynamicsConfig config = dynamicsConfigService.getConfigForCurrentTenantOrDefault();
        
        if (config == null) {
            log.error("❌ Dynamics configuration not found in database for current tenant");
            return null;
        }

        Long currentTenantId = config.getTenant() != null ? config.getTenant().getTenantId() : null;
        log.debug("Dynamics config found for tenant: {}", currentTenantId);
        
        // Check if cached token is still valid for the same tenant
        if (cachedToken != null && tokenExpiry != null && Instant.now().isBefore(tokenExpiry)
                && currentTenantId != null && currentTenantId.equals(cachedForTenantId)) {
            log.debug("Using cached Dynamics token for tenant: {}", currentTenantId);
            return cachedToken;
        }

        String tenantId = config.getTenantIdAzure();
        String clientId = config.getClientId();
        String clientSecret = config.getClientSecret();
        String resource = config.getResource();

        log.debug("Dynamics credentials: tenantId={}, clientId={}, resource={}", 
                tenantId != null ? "***" : "NULL", 
                clientId != null ? "***" : "NULL", 
                resource);

        if (tenantId == null || tenantId.isEmpty() || clientId == null || clientId.isEmpty()) {
            log.error("❌ Dynamics credentials incomplete in database config");
            return null;
        }

        String tokenUrl = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token";
        log.debug("Requesting Dynamics token from: {}", tokenUrl);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("scope", resource + "/.default");
        params.add("grant_type", "client_credentials");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(tokenUrl, request, JsonNode.class);
            if (response.getBody() != null) {
                cachedToken = response.getBody().path("access_token").asText();
                int expiresIn = response.getBody().path("expires_in").asInt(3600);
                tokenExpiry = Instant.now().plusSeconds(expiresIn - 600); // Refresh 10 min early
                cachedForTenantId = currentTenantId;
                log.info("✅ Dynamics token refreshed successfully for tenant: {} (expires in {} seconds)", currentTenantId, expiresIn);
                return cachedToken;
            }
        } catch (Exception e) {
            log.error("❌ Failed to get Dynamics token: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            if (e.getMessage() != null && e.getMessage().contains("40")) {
                log.error("HTTP error response - check credentials and permissions");
            }
        }

        log.error("❌ Dynamics token acquisition failed, returning null");
        return null;
    }

    public String getDynamicsBaseUrl() {
        TenantDynamicsConfig config = dynamicsConfigService.getConfigForCurrentTenantOrDefault();
        if (config == null || config.getResource() == null) {
            log.warn("Dynamics resource URL not configured in database");
            return null;
        }
        return config.getResource() + "/api/data/v9.2";
    }
}
