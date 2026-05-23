package com.facilon.app.module.client.controller;

import com.facilon.app.annotations.CurrentTenant;
import com.facilon.app.module.client.dto.InvestorDto;
import com.facilon.app.module.client.dto.InvestorCreateDto;
import com.facilon.app.module.client.dto.InvestorUpdateDto;
import com.facilon.app.module.client.dto.InvestorProgressDto;
import com.facilon.app.module.client.dto.InvestorDashboardDto;
import com.facilon.app.module.client.dto.AccountDetailsDto;
import com.facilon.app.module.client.dto.JourneyListItemDto;
import com.facilon.app.module.client.service.ClientService;
import com.facilon.app.module.client.service.InvestorProgressService;
import com.facilon.app.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Client Controller - For clients (investors) to manage their own profile.
 * Tenant-scoped endpoints.
 */
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Slf4j
@CurrentTenant
@Tag(name = "Client Management", description = "Client/Investor self-service APIs")
public class ClientController {

    private final ClientService clientService;
    private final InvestorProgressService investorProgressService;

    /**
     * Get current user's client profile.
     * Accessible by any authenticated user who has a client/investor record.
     */
    @GetMapping("/me")
    @Operation(summary = "Get my client profile", description = "Get client/investor profile for current authenticated user")
    public ResponseEntity<InvestorDto> getMyProfile(Authentication authentication) {
        log.info("Get my client profile request");

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long currentUserId = userPrincipal.getId();

        try {
            InvestorDto client = clientService.getMyClientProfile(currentUserId);
            return ResponseEntity.ok(client);
        } catch (RuntimeException e) {
            log.error("Error getting client profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Update current user's client profile.
     */
    @PutMapping("/me")
    @Operation(summary = "Update my client profile", description = "Update client/investor profile for current authenticated user")
    public ResponseEntity<InvestorDto> updateMyProfile(
            @Valid @RequestBody InvestorUpdateDto dto,
            Authentication authentication) {
        log.info("Update my client profile request");

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long currentUserId = userPrincipal.getId();

        try {
            // First get client ID for current user
            InvestorDto currentClient = clientService.getMyClientProfile(currentUserId);

            // Update
            InvestorDto updated = clientService.updateClient(currentClient.getId(), dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Error updating client profile: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Create client profile (called after user registration to become a
     * client/investor).
     */
    @PostMapping
    @Operation(summary = "Create client profile", description = "Create client/investor profile for current user")
    public ResponseEntity<InvestorDto> createClientProfile(
            @Valid @RequestBody InvestorCreateDto dto,
            Authentication authentication) {
        log.info("Create client profile request");

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // Set current user as the authorized user
        dto.setAuthorizedUserId(userPrincipal.getId());

        try {
            InvestorDto client = clientService.createClient(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(client);
        } catch (RuntimeException e) {
            log.error("Error creating client profile: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get investor progress for current user.
     */
    @GetMapping("/me/progress")
    @Operation(summary = "Get my progress", description = "Get onboarding progress for current authenticated investor")
    public ResponseEntity<InvestorProgressDto> getMyProgress(Authentication authentication) {
        log.info("Get my progress request");

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long currentUserId = userPrincipal.getId();

        try {
            InvestorDto client = clientService.getMyClientProfile(currentUserId);
            InvestorProgressDto progress = investorProgressService.getInvestorProgress(client.getUniqueCode());
            return ResponseEntity.ok(progress);
        } catch (RuntimeException e) {
            log.error("Error getting progress: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Get dashboard data for current user.
     */
    @GetMapping("/me/dashboard")
    @Operation(summary = "Get my dashboard", description = "Get dashboard data for current authenticated investor")
    public ResponseEntity<InvestorDashboardDto> getMyDashboard(Authentication authentication) {
        log.info("Get my dashboard request");

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long currentUserId = userPrincipal.getId();

        try {
            InvestorDto client = clientService.getMyClientProfile(currentUserId);
            InvestorDashboardDto dashboard = investorProgressService.getInvestorDashboard(
                            client.getUniqueCode(), client.getEmailId());
            return ResponseEntity.ok(dashboard);
        } catch (RuntimeException e) {
            log.error("Error getting dashboard: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Get comprehensive account details for current user.
     * Includes bank information, verification status, document counts, and account opening status.
     * Aligned with Laravel account-details.blade.php
     */
    @GetMapping("/me/account-details")
    @Operation(summary = "Get my account details", description = "Get comprehensive account details including bank info, verification status, and progress")
    public ResponseEntity<AccountDetailsDto> getMyAccountDetails(Authentication authentication) {
        log.info("Get my account details request");

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long currentUserId = userPrincipal.getId();

        try {
            InvestorDto client = clientService.getMyClientProfile(currentUserId);
            AccountDetailsDto accountDetails = investorProgressService.getAccountDetails(client.getUniqueCode());
            return ResponseEntity.ok(accountDetails);
        } catch (RuntimeException e) {
            log.error("Error getting account details: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * List all onboarding journeys (assigned products) for the current investor.
     * Each item carries a journeyId used to route to that specific journey.
     */
    @GetMapping("/me/journeys")
    @Operation(summary = "Get my journeys", description = "List all onboarding journeys (assigned products) for the current investor")
    public ResponseEntity<java.util.List<JourneyListItemDto>> getMyJourneys(Authentication authentication) {
        log.info("Get my journeys request");

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long currentUserId = userPrincipal.getId();

        try {
            InvestorDto client = clientService.getMyClientProfile(currentUserId);
            return ResponseEntity.ok(investorProgressService.getInvestorJourneys(client.getUniqueCode()));
        } catch (RuntimeException e) {
            log.error("Error getting journeys: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
