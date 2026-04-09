package com.facilon.app.controller;

import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.model.TenantB2CConfig;
import com.facilon.app.module.client.service.SessionManagementService;
import com.facilon.app.repository.AuthorizedUserRepository;
import com.facilon.app.security.CustomUserDetailsService;
import com.facilon.app.security.JwtAuthenticationResponse;
import com.facilon.app.security.JwtTokenProvider;
import com.facilon.app.service.TenantB2CConfigService;
import jakarta.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.facilon.app.service.TenantB2CConfigService.decodeState;

/**
 * Azure B2C OAuth2 Authentication Controller
 * Uses tenant-specific B2C config from database (with YAML fallback).
 */
@Slf4j
@RestController
@RequestMapping("/api/auth/azure-b2c")
@RequiredArgsConstructor
public class AzureB2CController {

    private static final long DEFAULT_TENANT_ID = 1L;

    private final AuthorizedUserRepository authorizedUserRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final SessionManagementService sessionManagementService;
    private final TenantB2CConfigService b2cConfigService;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/login-url")
    public ResponseEntity<Map<String, String>> getLoginUrl(
            @RequestParam(required = false) Long tenantId) {
        long tid = tenantId != null ? tenantId : DEFAULT_TENANT_ID;
        if (!b2cConfigService.isB2CEnabled(tid)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Azure B2C is not enabled"));
        }
        String loginUrl = b2cConfigService.buildLoginUrl(tid, null);
        if (loginUrl == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Azure B2C configuration not found"));
        }
        return ResponseEntity.ok(Map.of("loginUrl", loginUrl, "enabled", "true"));
    }

    @PostMapping("/msal-callback")
    public ResponseEntity<?> handleMsalCallback(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        long tid = DEFAULT_TENANT_ID;
        if (requestBody.containsKey("tenantId")) {
            try {
                tid = Long.parseLong(requestBody.get("tenantId"));
            } catch (NumberFormatException ignored) {}
        }
        if (!b2cConfigService.isB2CEnabled(tid)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Azure B2C is not enabled"));
        }

        String idToken = requestBody.get("idToken");
        if (idToken == null || idToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "ID token is required"));
        }

        try {
            Map<String, Object> userClaims = parseJwt(idToken);
            String email = extractEmail(userClaims);
            if (email == null) {
                log.error("Could not extract email from MSAL token. Available claims: {}", userClaims.keySet());
                return ResponseEntity.badRequest().body(Map.of("error", "Email not found in token"));
            }

            String externalUserId = (String) userClaims.get("sub");
            boolean isGoogleIdp = isGoogleIdentityProvider(userClaims);
            log.info("Azure B2C MSAL login successful for user: {} (IdP: {})", email, isGoogleIdp ? "Google" : "Azure/Local");

            Optional<AuthorizedUser> userOptional = authorizedUserRepository.findByEmailId(email);
            AuthorizedUser user;
            if (userOptional.isPresent()) {
                user = userOptional.get();
                if (isGoogleIdp) {
                    if (user.getGoogleUserId() == null || user.getGoogleUserId().isEmpty()) {
                        user.setGoogleUserId(externalUserId);
                        authorizedUserRepository.save(user);
                    }
                } else {
                    if (user.getAzureAdUserId() == null || user.getAzureAdUserId().isEmpty()) {
                        user.setAzureAdUserId(externalUserId);
                        authorizedUserRepository.save(user);
                    }
                }
            } else {
                log.warn("User {} not found in local database after Azure B2C MSAL login", email);
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "User not found in system. Please contact administrator.",
                        "azureUserId", externalUserId));
            }

            var userDetails = customUserDetailsService.loadUserEmail(user.getEmailId());
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            String jwt = jwtTokenProvider.generateToken(authentication);

            try {
                sessionManagementService.createSession(
                        user.getId(), jwt, "azure-b2c-msal",
                        request.getRemoteAddr(), request.getHeader("User-Agent"),
                        user.getTenant().getTenantId());
            } catch (Exception e) {
                log.warn("Failed to create session tracking: {}", e.getMessage());
            }

            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
        } catch (Exception e) {
            log.error("Error handling Azure B2C MSAL callback: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Authentication failed: " + e.getMessage()));
        }
    }

    @PostMapping("/callback")
    public ResponseEntity<?> handleCallback(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        String code = requestBody.get("code");
        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Authorization code is required"));
        }

        String state = requestBody.get("state");
        long tenantId = decodeState(state);
        if (!b2cConfigService.isB2CEnabled(tenantId)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Azure B2C is not enabled for this tenant"));
        }

        TenantB2CConfig config = b2cConfigService.getConfigForTenantOrFallback(tenantId);
        if (config == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Azure B2C configuration not found"));
        }

        try {
            Map<String, Object> tokenResponse = exchangeCodeForToken(config, code);
            if (tokenResponse == null || tokenResponse.containsKey("error")) {
                log.error("Failed to exchange code for token: {}", tokenResponse);
                return ResponseEntity.badRequest().body(Map.of("error", "Failed to authenticate with Azure B2C"));
            }

            String idToken = (String) tokenResponse.get("id_token");
            Map<String, Object> userClaims = parseJwt(idToken);
            String email = extractEmail(userClaims);
            if (email == null) {
                log.error("Could not extract email from token. Available claims: {}", userClaims.keySet());
            }

            String externalUserId = (String) userClaims.get("sub");
            boolean isGoogleIdp = isGoogleIdentityProvider(userClaims);
            log.info("Azure B2C login successful for user: {} (IdP: {})", email, isGoogleIdp ? "Google" : "Azure/Local");

            Optional<AuthorizedUser> userOptional = authorizedUserRepository.findByEmailId(email);
            AuthorizedUser user;
            if (userOptional.isPresent()) {
                user = userOptional.get();
                if (isGoogleIdp) {
                    if (user.getGoogleUserId() == null || user.getGoogleUserId().isEmpty()) {
                        user.setGoogleUserId(externalUserId);
                        authorizedUserRepository.save(user);
                    }
                } else {
                    if (user.getAzureAdUserId() == null || user.getAzureAdUserId().isEmpty()) {
                        user.setAzureAdUserId(externalUserId);
                        authorizedUserRepository.save(user);
                    }
                }
                if (!user.getTenant().getTenantId().equals(tenantId)) {
                    log.warn("User {} tenant {} does not match state tenant {}", email, user.getTenant().getTenantId(), tenantId);
                }
            } else {
                log.warn("User {} not found in local database after Azure B2C login", email);
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "User not found in system. Please contact administrator.",
                        "azureUserId", externalUserId));
            }

            var userDetails = customUserDetailsService.loadUserEmail(user.getEmailId());
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            String jwt = jwtTokenProvider.generateToken(authentication);

            try {
                sessionManagementService.createSession(
                        user.getId(), jwt, "azure-b2c",
                        request.getRemoteAddr(), request.getHeader("User-Agent"),
                        user.getTenant().getTenantId());
            } catch (Exception e) {
                log.warn("Failed to create session tracking: {}", e.getMessage());
            }

            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
        } catch (Exception e) {
            log.error("Error handling Azure B2C callback: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Authentication failed: " + e.getMessage()));
        }
    }

    @GetMapping("/logout-url")
    public ResponseEntity<Map<String, String>> getLogoutUrl(
            @RequestParam(required = false) Long tenantId) {
        long tid = tenantId != null ? tenantId : DEFAULT_TENANT_ID;
        if (!b2cConfigService.isB2CEnabled(tid)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Azure B2C is not enabled"));
        }
        String logoutUrl = b2cConfigService.buildLogoutUrl(tid);
        if (logoutUrl == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Azure B2C configuration not found"));
        }
        return ResponseEntity.ok(Map.of("logoutUrl", logoutUrl, "enabled", "true"));
    }

    private Map<String, Object> exchangeCodeForToken(TenantB2CConfig config, String code) {
        String tokenEndpoint = b2cConfigService.buildTokenEndpoint(config);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        b2cConfigService.exchangeCodeForTokenParams(config, code).forEach(params::add);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenEndpoint, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
            log.error("Token exchange failed with status: {}", response.getStatusCode());
            return null;
        } catch (Exception e) {
            log.error("Error exchanging code for token: {}", e.getMessage(), e);
            return null;
        }
    }

    private static String extractEmail(Map<String, Object> userClaims) {
        Object emailsObj = userClaims.get("emails");
        if (emailsObj instanceof List<?> list && !list.isEmpty() && list.get(0) != null) {
            return list.get(0).toString();
        }
        if (emailsObj instanceof String s) return s;
        Object email = userClaims.get("email");
        return email != null ? email.toString() : null;
    }

    /**
     * Returns true if the B2C token indicates the user signed in with Google (or another social IdP we treat as Google).
     * B2C often sets "identityProvider" or "idp" (e.g. "Google-OAuth2", "google.com").
     */
    private static boolean isGoogleIdentityProvider(Map<String, Object> userClaims) {
        for (String key : new String[] { "identityProvider", "idp" }) {
            Object val = userClaims.get(key);
            if (val != null) {
                String s = val.toString().toLowerCase();
                if (s.contains("google")) return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJwt(String idToken) {
        try {
            String[] parts = idToken.split("\\.");
            if (parts.length < 2) throw new IllegalArgumentException("Invalid JWT format");
            byte[] decodedBytes = Base64.getUrlDecoder().decode(parts[1]);
            return new ObjectMapper().readValue(new String(decodedBytes), Map.class);
        } catch (Exception e) {
            log.error("Error parsing JWT: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
}
