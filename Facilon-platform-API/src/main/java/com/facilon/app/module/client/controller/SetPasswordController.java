package com.facilon.app.module.client.controller;

import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.model.TenantB2CConfig;
import com.facilon.app.module.client.dto.onboarding.SetPasswordDetailsDto;
import com.facilon.app.repository.AuthorizedUserRepository;
import com.facilon.app.service.TenantB2CConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Public endpoint backing the FISP-style {@code /investor/setpassword/:azureUserId} React page.
 *
 * <p>Given the Azure AD object id from the first-login email link, returns the B2C config and
 * sign-in history the React page needs to drive MSAL to the right user flow:</p>
 * <ul>
 *   <li>If {@code hasLoggedInBefore=false} → the page uses {@code resetPasswordPolicy} so the user
 *   picks a new password through B2C's reset flow.</li>
 *   <li>If {@code hasLoggedInBefore=true} → the page uses {@code signupSigninPolicy}, behaving like
 *   a normal sign-in.</li>
 * </ul>
 *
 * <p>Mirrors FISP {@code firmservice.getUserProfileResetPasswordByAzureuserId}.</p>
 */
@RestController
@RequestMapping("/api/clients/onboarding/setpassword")
@RequiredArgsConstructor
@Slf4j
public class SetPasswordController {

    private final AuthorizedUserRepository authorizedUserRepository;
    private final TenantB2CConfigService b2cConfigService;

    @GetMapping("/details/{azureUserId}")
    public ResponseEntity<?> getDetails(@PathVariable("azureUserId") String azureUserId) {
        if (azureUserId == null || azureUserId.isBlank()) {
            return ResponseEntity.badRequest().body(error("azureUserId is required"));
        }

        Optional<AuthorizedUser> userOpt = authorizedUserRepository.findByAzureAdUserId(azureUserId);
        if (userOpt.isEmpty()) {
            log.warn("Setpassword details requested for unknown azureUserId={}", azureUserId);
            // Generic 404 — don't reveal whether the GUID exists or not.
            return ResponseEntity.status(404).body(error("Invalid or expired link"));
        }

        AuthorizedUser user = userOpt.get();
        Long tenantId = user.getTenant() != null ? user.getTenant().getTenantId() : null;
        if (tenantId == null) {
            log.error("AuthorizedUser id={} has no tenant; cannot resolve B2C config", user.getId());
            return ResponseEntity.status(500).body(error("Tenant not resolved for user"));
        }

        TenantB2CConfig cfg = b2cConfigService.getConfigForTenantOrFallback(tenantId);
        if (cfg == null) {
            log.error("No tenant_b2c_config row for tenant {}", tenantId);
            return ResponseEntity.status(500).body(error("B2C configuration not available"));
        }

        SetPasswordDetailsDto dto = SetPasswordDetailsDto.builder()
                .azureUserId(azureUserId)
                .b2cTenantName(cfg.getB2cTenantName())
                .clientId(cfg.getClientId())
                .signupSigninPolicy(cfg.getSignupSigninPolicy())
                .resetPasswordPolicy(cfg.getResetPasswordPolicy())
                .redirectUri(cfg.getRedirectUri())
                .scope(cfg.getScope())
                .hasLoggedInBefore(user.getLastLogin() != null)
                .displayName(buildDisplayName(user))
                .build();
        return ResponseEntity.ok(dto);
    }

    private static String buildDisplayName(AuthorizedUser user) {
        String first = user.getFirstName() != null ? user.getFirstName() : "";
        String last = user.getLastName() != null ? user.getLastName() : "";
        String full = (first + " " + last).trim();
        return full.isEmpty() ? user.getEmailId() : full;
    }

    private static java.util.Map<String, Object> error(String msg) {
        return java.util.Map.of("success", false, "message", msg);
    }
}
