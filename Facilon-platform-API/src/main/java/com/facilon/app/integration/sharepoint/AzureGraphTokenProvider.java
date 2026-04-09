package com.facilon.app.integration.sharepoint;

import com.facilon.app.model.TenantSharePointConfig;
import com.facilon.app.service.TenantSharePointConfigService;
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
 * Provides OAuth2 client_credentials tokens for Microsoft Graph API.
 * Configuration loaded from database (tenant_sharepoint_config table).
 * Used by SharePointService for document operations.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AzureGraphTokenProvider {

    private static final String GRAPH_SCOPE = "https://graph.microsoft.com/.default";
    private static final long TOKEN_EXPIRY_BUFFER_SECONDS = 60;

    private final RestTemplate restTemplate = new RestTemplate();
    private final TenantSharePointConfigService sharePointConfigService;

    private String cachedToken;
    private Instant tokenExpiresAt;
    private Long cachedForTenantId;

    public synchronized String getGraphToken() {
        TenantSharePointConfig config = sharePointConfigService.getConfigForCurrentTenantOrDefault();
        
        if (config == null) {
            log.warn("SharePoint configuration not found in database for current tenant");
            return null;
        }

        String tenantId = config.getTenantIdAzure();
        String clientId = config.getClientId();
        String clientSecret = config.getClientSecret();

        if (tenantId == null || tenantId.isEmpty() || clientId == null || clientId.isEmpty()) {
            log.warn("SharePoint credentials incomplete in database config");
            return null;
        }

        // Check if cached token is still valid for the same tenant
        Long currentTenantId = config.getTenant() != null ? config.getTenant().getTenantId() : null;
        if (cachedToken != null && tokenExpiresAt != null 
                && Instant.now().isBefore(tokenExpiresAt.minusSeconds(TOKEN_EXPIRY_BUFFER_SECONDS))
                && currentTenantId != null && currentTenantId.equals(cachedForTenantId)) {
            return cachedToken;
        }

        String tokenUrl = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("scope", GRAPH_SCOPE);

        try {
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<JsonNode> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, JsonNode.class);

            if (response.getBody() == null) {
                throw new RuntimeException("Failed to obtain Graph token: empty response");
            }

            JsonNode json = response.getBody();
            cachedToken = json.path("access_token").asText();
            int expiresIn = json.path("expires_in").asInt(3600);
            tokenExpiresAt = Instant.now().plusSeconds(expiresIn);
            cachedForTenantId = currentTenantId;

            log.info("Graph token obtained for tenant: {}, expires in {} seconds", currentTenantId, expiresIn);
            return cachedToken;
        } catch (Exception e) {
            log.error("Failed to get Graph token: {}", e.getMessage());
            return null;
        }
    }
}
