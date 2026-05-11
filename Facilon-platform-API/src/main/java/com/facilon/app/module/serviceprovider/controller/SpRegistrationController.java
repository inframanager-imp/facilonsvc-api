package com.facilon.app.module.serviceprovider.controller;

import com.facilon.app.annotations.CurrentTenant;
import com.facilon.app.module.serviceprovider.dto.*;
import com.facilon.app.module.serviceprovider.service.SpRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public Service Provider onboarding endpoints — no auth, mirrors Laravel
 * {@code BrokerController} routes reachable from the invite email URL.
 *
 * <p>Endpoint mapping:</p>
 * <pre>
 *   GET  /api/clients/sp/landing?status=ENC          → BrokerController::service_provider_registration
 *   GET  /api/clients/sp/user-consent?uniqueCode=ENC → BrokerController::services_provider_user
 *   POST /api/clients/sp/user-consent                → BrokerController::service_provider_step3_submit
 *   GET  /api/clients/sp/user-register?serviceProviderEmail=ENC
 *                                                    → BrokerController::services_provider_user_register
 *   POST /api/clients/sp/user-register               → BrokerController::service_provider_user_register_store
 * </pre>
 */
@RestController
@RequestMapping("/api/clients/sp")
@RequiredArgsConstructor
@Slf4j
@CurrentTenant
@Tag(name = "Service Provider Onboarding", description = "Public Service Provider invite + registration APIs")
public class SpRegistrationController {

    private final SpRegistrationService service;

    @GetMapping("/landing")
    @Operation(summary = "Step 1 landing — decrypt status and return SP greeting")
    public ResponseEntity<?> landing(@RequestParam("status") String status) {
        try {
            return ResponseEntity.ok(service.landing(status));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error(e.getMessage()));
        }
    }

    @GetMapping("/user-consent")
    @Operation(summary = "Step 2 — user-level Privacy & Consent display payload")
    public ResponseEntity<?> userConsentLanding(@RequestParam("uniqueCode") String uniqueCode) {
        try {
            return ResponseEntity.ok(service.userConsentLanding(uniqueCode));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error(e.getMessage()));
        }
    }

    @PostMapping("/user-consent")
    @Operation(summary = "Step 3 — record user consent, render PDF, send confirmation mail")
    public ResponseEntity<?> submitUserConsent(@Valid @RequestBody SpUserConsentRequestDto dto,
                                               HttpServletRequest req) {
        try {
            SpUserConsentResponseDto resp = service.submitUserConsent(
                    dto, clientIp(req), req.getHeader("User-Agent"));
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error(e.getMessage()));
        } catch (Exception e) {
            log.error("submitUserConsent failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error("Could not record consent."));
        }
    }

    @GetMapping("/user-register")
    @Operation(summary = "Step 4 — return prefill data for the final user form")
    public ResponseEntity<?> userRegisterPrefill(@RequestParam("serviceProviderEmail") String spEmail) {
        try {
            return ResponseEntity.ok(service.userRegisterPrefill(spEmail));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error(e.getMessage()));
        }
    }

    @PostMapping("/user-register")
    @Operation(summary = "Step 5 — insert service_provider_users + send welcome mail (B2B invite deferred)")
    public ResponseEntity<SpUserRegisterResponseDto> registerUser(
            @Valid @RequestBody SpUserRegisterRequestDto dto, HttpServletRequest req) {
        SpUserRegisterResponseDto resp = service.registerUser(
                dto, clientIp(req), req.getHeader("User-Agent"));
        if (!resp.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
        return ResponseEntity.ok(resp);
    }

    private static String clientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
        return req.getRemoteAddr();
    }

    private static java.util.Map<String, Object> error(String msg) {
        return java.util.Map.of("success", false, "message", msg != null ? msg : "Unknown error");
    }
}
