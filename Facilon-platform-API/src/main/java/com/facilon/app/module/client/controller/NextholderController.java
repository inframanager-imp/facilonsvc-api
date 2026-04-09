package com.facilon.app.module.client.controller;

import com.facilon.app.annotations.CurrentTenant;
import com.facilon.app.module.client.dto.*;
import com.facilon.app.module.client.service.ClientService;
import com.facilon.app.module.client.service.ConsentService;
import com.facilon.app.module.client.service.InvitationService;
import com.facilon.app.module.client.service.NextholderService;
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

import java.util.List;
import java.util.Map;

/**
 * Nextholder Controller - For managing investor nextholders (introduced
 * investors)
 */
@RestController
@RequestMapping("/api/clients/me/nextholders")
@RequiredArgsConstructor
@Slf4j
@CurrentTenant
@Tag(name = "Investor Nextholders", description = "APIs for managing investor nextholders")
public class NextholderController {

    private final NextholderService nextholderService;
    private final ConsentService consentService;
    private final InvitationService invitationService;
    private final ClientService clientService;

    @PostMapping
    @Operation(summary = "Create nextholder", description = "Add a new nextholder for the investor")
    public ResponseEntity<?> createNextholder(
            @Valid @RequestBody NextholderDto dto,
            Authentication authentication) {
        log.info("Create nextholder request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            NextholderDto created = nextholderService.createNextholder(uniqueCode, dto);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            log.error("Error creating nextholder: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Get all nextholders", description = "Get list of all nextholders for the investor")
    public ResponseEntity<List<NextholderDto>> getNextholders(Authentication authentication) {
        log.info("Get nextholders request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            List<NextholderDto> nextholders = nextholderService.getNextholders(uniqueCode);
            return ResponseEntity.ok(nextholders);
        } catch (Exception e) {
            log.error("Error fetching nextholders: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get nextholder", description = "Get details of a specific nextholder")
    public ResponseEntity<?> getNextholder(
            @PathVariable Long id,
            Authentication authentication) {
        log.info("Get nextholder request, id: {}", id);

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            NextholderDto nextholder = nextholderService.getNextholder(uniqueCode, id);
            return ResponseEntity.ok(nextholder);
        } catch (Exception e) {
            log.error("Error fetching nextholder: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update nextholder", description = "Update nextholder information")
    public ResponseEntity<?> updateNextholder(
            @PathVariable Long id,
            @Valid @RequestBody NextholderDto dto,
            Authentication authentication) {
        log.info("Update nextholder request, id: {}", id);

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            NextholderDto updated = nextholderService.updateNextholder(uniqueCode, id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Error updating nextholder: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete nextholder", description = "Remove a nextholder")
    public ResponseEntity<Map<String, String>> deleteNextholder(
            @PathVariable Long id,
            Authentication authentication) {
        log.info("Delete nextholder request, id: {}", id);

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            nextholderService.deleteNextholder(uniqueCode, id);
            return ResponseEntity.ok(Map.of("message", "Nextholder deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting nextholder: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/consent")
    @Operation(summary = "Record consent", description = "Record data consent for nextholder management")
    public ResponseEntity<?> recordConsent(
            @Valid @RequestBody DataConsentDto dto,
            HttpServletRequest request,
            Authentication authentication) {
        log.info("Record consent request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            String ipAddress = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");

            DataConsentDto recorded = consentService.recordConsent(uniqueCode, dto, ipAddress, userAgent);
            return ResponseEntity.ok(recorded);
        } catch (Exception e) {
            log.error("Error recording consent: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/consent")
    @Operation(summary = "Get consents", description = "Get all consent records")
    public ResponseEntity<List<DataConsentDto>> getConsents(Authentication authentication) {
        log.info("Get consents request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            List<DataConsentDto> consents = consentService.getConsents(uniqueCode);
            return ResponseEntity.ok(consents);
        } catch (Exception e) {
            log.error("Error fetching consents: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/invitations")
    @Operation(summary = "Send invitation", description = "Send email invitation to a nextholder")
    public ResponseEntity<?> sendInvitation(
            @Valid @RequestBody EmailInvitationDto dto,
            Authentication authentication) {
        log.info("Send invitation request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            EmailInvitationDto sent = invitationService.sendInvitation(uniqueCode, dto);
            return ResponseEntity.ok(sent);
        } catch (Exception e) {
            log.error("Error sending invitation: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/invitations")
    @Operation(summary = "Get invitations", description = "Get all email invitations")
    public ResponseEntity<List<EmailInvitationDto>> getInvitations(Authentication authentication) {
        log.info("Get invitations request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            List<EmailInvitationDto> invitations = invitationService.getInvitations(uniqueCode);
            return ResponseEntity.ok(invitations);
        } catch (Exception e) {
            log.error("Error fetching invitations: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/invitations/{id}/resend")
    @Operation(summary = "Resend invitation", description = "Resend an email invitation")
    public ResponseEntity<?> resendInvitation(
            @PathVariable Long id,
            Authentication authentication) {
        log.info("Resend invitation request, id: {}", id);

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            EmailInvitationDto resent = invitationService.resendInvitation(uniqueCode, id);
            return ResponseEntity.ok(resent);
        } catch (Exception e) {
            log.error("Error resending invitation: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private String getInvestorUniqueCode(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        InvestorDto investor = clientService.getMyClientProfile(userPrincipal.getId());
        return investor.getUniqueCode();
    }
}
