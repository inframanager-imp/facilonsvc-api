package com.facilon.app.integration.graphemail;

import com.facilon.app.model.TenantGraphEmailConfig;
import com.facilon.app.service.TenantGraphEmailConfigService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides OAuth2 client_credentials tokens for Microsoft Graph API Email operations.
 * Uses tenant-specific config from database only.
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "graph.email.enabled", havingValue = "true")
public class GraphEmailTokenProvider {

    private static final String GRAPH_SCOPE = "https://graph.microsoft.com/.default";
    private static final long TOKEN_EXPIRY_BUFFER_SECONDS = 60;

    private final RestTemplate restTemplate = new RestTemplate();
    private final TenantGraphEmailConfigService configService;

    private final Map<Long, String> cachedTokenByTenant = new ConcurrentHashMap<>();
    private final Map<Long, Instant> tokenExpiresAtByTenant = new ConcurrentHashMap<>();

    /**
     * Get access token for Microsoft Graph API with Mail.Send permissions
     * Uses tenant-specific config from DB. Token is cached per tenant.
     */
    public synchronized String getGraphToken() {
        TenantGraphEmailConfig config = configService.getConfigForCurrentTenantOrDefault();
        if (config == null) {
            throw new RuntimeException("Graph Email configuration not found");
        }
        if (config.getTenantIdAzure() == null || config.getTenantIdAzure().isEmpty()
                || config.getClientId() == null || config.getClientId().isEmpty()
                || config.getClientSecret() == null || config.getClientSecret().isEmpty()) {
            throw new RuntimeException("Graph Email configuration is incomplete in DB");
        }
        Long tenantId = config.getTenant() != null ? config.getTenant().getTenantId() : 1L;

        Instant expiresAt = tokenExpiresAtByTenant.get(tenantId);
        String token = cachedTokenByTenant.get(tenantId);
        if (token != null && expiresAt != null &&
                Instant.now().isBefore(expiresAt.minusSeconds(TOKEN_EXPIRY_BUFFER_SECONDS))) {
            return token;
        }

        log.info("Requesting new Graph email access token for tenant {}", tenantId);
        String tokenUrl = "https://login.microsoftonline.com/" + config.getTenantIdAzure() + "/oauth2/v2.0/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", config.getClientId());
        body.add("client_secret", config.getClientSecret());
        body.add("scope", GRAPH_SCOPE);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, JsonNode.class);
            if (response.getBody() == null) throw new RuntimeException("Empty token response");
            JsonNode json = response.getBody();
            token = json.path("access_token").asText();
            int expiresIn = json.path("expires_in").asInt(3600);
            cachedTokenByTenant.put(tenantId, token);
            tokenExpiresAtByTenant.put(tenantId, Instant.now().plusSeconds(expiresIn));
            return token;
        } catch (Exception e) {
            log.error("Failed to obtain Graph email token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to authenticate with Microsoft Graph for email: " + e.getMessage(), e);
        }
    }

    public void invalidateToken() {
        log.info("Invalidating cached Graph email tokens");
        cachedTokenByTenant.clear();
        tokenExpiresAtByTenant.clear();
    }

    public boolean isTokenValid() {
        TenantGraphEmailConfig config = configService.getConfigForCurrentTenantOrDefault();
        if (config == null) return false;
        Long tenantId = config.getTenant() != null ? config.getTenant().getTenantId() : 1L;
        String token = cachedTokenByTenant.get(tenantId);
        Instant expiresAt = tokenExpiresAtByTenant.get(tenantId);
        return token != null && expiresAt != null &&
                Instant.now().isBefore(expiresAt.minusSeconds(TOKEN_EXPIRY_BUFFER_SECONDS));
    }
}
