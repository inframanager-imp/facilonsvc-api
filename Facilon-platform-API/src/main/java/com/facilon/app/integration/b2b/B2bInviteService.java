package com.facilon.app.integration.b2b;

import com.facilon.app.model.TenantDynamicsConfig;
import com.facilon.app.service.TenantDynamicsConfigService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mirrors Laravel BrokerController::service_provider_user_register_store §B2B section
 * (file path d:/sam/facilon/docs/Investor/app/Http/Controllers/BrokerController.php:954-986).
 *
 * <p>Mints a workforce-tenant Graph token using {@code tenant_dynamics_config} creds (same
 * app registration Laravel reuses via {@code AZURE_B2B_DATAVERSE_CLIENT_ID}) and POSTs to
 * {@code https://graph.microsoft.com/v1.0/invitations}. The app reg needs the
 * {@code User.Invite.All} application permission granted with admin consent.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class B2bInviteService {

    private static final String GRAPH_SCOPE = "https://graph.microsoft.com/.default";
    private static final String GRAPH_INVITATIONS_URL = "https://graph.microsoft.com/v1.0/invitations";
    private static final long TOKEN_EXPIRY_BUFFER_SECONDS = 60;

    private final TenantDynamicsConfigService dynamicsConfigService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${b2b.invite.redirect-url:https://fportalonboard.powerappsportals.com}")
    private String defaultRedirectUrl;

    private final Map<Long, String> cachedTokenByTenant = new ConcurrentHashMap<>();
    private final Map<Long, Instant> tokenExpiresAtByTenant = new ConcurrentHashMap<>();

    public record InvitationResult(boolean success, String invitedUserId, String errorMessage) {
        public static InvitationResult ok(String invitedUserId) {
            return new InvitationResult(true, invitedUserId, null);
        }

        public static InvitationResult fail(String error) {
            return new InvitationResult(false, null, error);
        }
    }

    public InvitationResult sendInvitation(String email, String displayName) {
        return sendInvitation(email, displayName, defaultRedirectUrl);
    }

    public InvitationResult sendInvitation(String email, String displayName, String redirectUrl) {
        if (email == null || email.isBlank()) {
            return InvitationResult.fail("invitedUserEmailAddress is required");
        }

        TenantDynamicsConfig cfg = dynamicsConfigService.getConfigForCurrentTenantOrDefault();
        if (cfg == null
                || cfg.getClientId() == null || cfg.getClientId().isEmpty()
                || cfg.getClientSecret() == null || cfg.getClientSecret().isEmpty()
                || cfg.getTenantIdAzure() == null || cfg.getTenantIdAzure().isEmpty()) {
            log.warn("B2B invite skipped: tenant_dynamics_config incomplete (need clientId, clientSecret, tenantIdAzure)");
            return InvitationResult.fail("Workforce tenant credentials not configured");
        }

        String token;
        try {
            token = getGraphToken(cfg);
        } catch (Exception e) {
            log.error("B2B invite token fetch failed: {}", e.getMessage(), e);
            return InvitationResult.fail("Failed to authenticate with Microsoft Graph: " + e.getMessage());
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("invitedUserEmailAddress", email);
        payload.put("inviteRedirectUrl", (redirectUrl == null || redirectUrl.isBlank()) ? defaultRedirectUrl : redirectUrl);
        payload.put("sendInvitationMessage", true);
        if (displayName != null && !displayName.isBlank()) {
            payload.put("invitedUserDisplayName", displayName);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    GRAPH_INVITATIONS_URL, HttpMethod.POST,
                    new HttpEntity<>(payload, headers), JsonNode.class);

            JsonNode body = response.getBody();
            String invitedUserId = body != null && body.hasNonNull("invitedUser")
                    ? body.path("invitedUser").path("id").asText(null)
                    : null;
            log.info("B2B invite sent to {} (invitedUserId={})", email, invitedUserId);
            return InvitationResult.ok(invitedUserId);
        } catch (HttpStatusCodeException e) {
            String body = e.getResponseBodyAsString();
            log.error("B2B invite Graph call failed ({}): {}", e.getStatusCode(), body);
            return InvitationResult.fail("Graph " + e.getStatusCode() + ": " + body);
        } catch (Exception e) {
            log.error("B2B invite Graph call exception: {}", e.getMessage(), e);
            return InvitationResult.fail(e.getMessage());
        }
    }

    private synchronized String getGraphToken(TenantDynamicsConfig cfg) {
        Long tenantId = cfg.getTenant() != null ? cfg.getTenant().getTenantId() : 1L;
        Instant expiresAt = tokenExpiresAtByTenant.get(tenantId);
        String cached = cachedTokenByTenant.get(tenantId);
        if (cached != null && expiresAt != null
                && Instant.now().isBefore(expiresAt.minusSeconds(TOKEN_EXPIRY_BUFFER_SECONDS))) {
            return cached;
        }

        String tokenUrl = "https://login.microsoftonline.com/" + cfg.getTenantIdAzure() + "/oauth2/v2.0/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", cfg.getClientId());
        body.add("client_secret", cfg.getClientSecret());
        body.add("scope", GRAPH_SCOPE);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                tokenUrl, HttpMethod.POST, new HttpEntity<>(body, headers), JsonNode.class);
        JsonNode json = response.getBody();
        if (json == null || !json.hasNonNull("access_token")) {
            throw new RuntimeException("Empty token response");
        }
        String token = json.path("access_token").asText();
        int expiresIn = json.path("expires_in").asInt(3600);
        cachedTokenByTenant.put(tenantId, token);
        tokenExpiresAtByTenant.put(tenantId, Instant.now().plusSeconds(expiresIn));
        return token;
    }
}
