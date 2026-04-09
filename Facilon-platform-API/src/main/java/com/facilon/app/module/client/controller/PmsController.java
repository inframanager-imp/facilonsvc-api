package com.facilon.app.module.client.controller;

import com.facilon.app.annotations.CurrentTenant;
import com.facilon.app.module.client.dto.*;
import com.facilon.app.module.client.service.ClientService;
import com.facilon.app.module.client.service.PmsService;
import com.facilon.app.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * PMS Controller - For managing PMS investor registration and preferences
 */
@RestController
@RequestMapping("/api/clients/me/pms")
@RequiredArgsConstructor
@Slf4j
@CurrentTenant
@Tag(name = "PMS Investor", description = "APIs for PMS investor registration and management")
public class PmsController {

    private final PmsService pmsService;
    private final ClientService clientService;

    @PostMapping("/registration")
    @Operation(summary = "Submit PMS registration", description = "Submit PMS registration application")
    public ResponseEntity<?> submitRegistration(
            @Valid @RequestBody PmsRegistrationDto dto,
            Authentication authentication) {
        log.info("Submit PMS registration request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            PmsRegistrationDto submitted = pmsService.submitRegistration(uniqueCode, dto);
            return ResponseEntity.ok(submitted);
        } catch (Exception e) {
            log.error("Error submitting PMS registration: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/registration")
    @Operation(summary = "Get PMS registration", description = "Get PMS registration status")
    public ResponseEntity<?> getRegistration(Authentication authentication) {
        log.info("Get PMS registration request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            PmsRegistrationDto registration = pmsService.getRegistration(uniqueCode);
            return ResponseEntity.ok(registration);
        } catch (Exception e) {
            log.error("Error fetching PMS registration: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/registration")
    @Operation(summary = "Update PMS registration", description = "Update PMS registration details")
    public ResponseEntity<?> updateRegistration(
            @Valid @RequestBody PmsRegistrationDto dto,
            Authentication authentication) {
        log.info("Update PMS registration request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            PmsRegistrationDto updated = pmsService.updateRegistration(uniqueCode, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Error updating PMS registration: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/compliance")
    @Operation(summary = "Submit compliance forms", description = "Submit PMS compliance and disclosures")
    public ResponseEntity<?> submitCompliance(
            @Valid @RequestBody PmsComplianceDto dto,
            HttpServletRequest request,
            Authentication authentication) {
        log.info("Submit PMS compliance request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            String ipAddress = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");

            PmsComplianceDto submitted = pmsService.submitCompliance(uniqueCode, dto, ipAddress, userAgent);
            return ResponseEntity.ok(submitted);
        } catch (Exception e) {
            log.error("Error submitting PMS compliance: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/compliance")
    @Operation(summary = "Get compliance status", description = "Get PMS compliance status")
    public ResponseEntity<?> getCompliance(Authentication authentication) {
        log.info("Get PMS compliance request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            PmsComplianceDto compliance = pmsService.getCompliance(uniqueCode);
            return ResponseEntity.ok(compliance);
        } catch (Exception e) {
            log.error("Error fetching PMS compliance: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/portfolio-preferences")
    @Operation(summary = "Set portfolio preferences", description = "Set portfolio allocation and preferences")
    public ResponseEntity<?> setPortfolioPreferences(
            @Valid @RequestBody PmsPortfolioPreferencesDto dto,
            Authentication authentication) {
        log.info("Set portfolio preferences request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            PmsPortfolioPreferencesDto set = pmsService.setPortfolioPreferences(uniqueCode, dto);
            return ResponseEntity.ok(set);
        } catch (Exception e) {
            log.error("Error setting portfolio preferences: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/portfolio-preferences")
    @Operation(summary = "Get portfolio preferences", description = "Get portfolio allocation and preferences")
    public ResponseEntity<?> getPortfolioPreferences(Authentication authentication) {
        log.info("Get portfolio preferences request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            PmsPortfolioPreferencesDto preferences = pmsService.getPortfolioPreferences(uniqueCode);
            return ResponseEntity.ok(preferences);
        } catch (Exception e) {
            log.error("Error fetching portfolio preferences: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private String getInvestorUniqueCode(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        InvestorDto investor = clientService.getMyClientProfile(userPrincipal.getId());
        return investor.getUniqueCode();
    }
}
