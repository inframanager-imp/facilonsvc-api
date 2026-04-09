package com.facilon.app.module.serviceagent.controller;

import com.facilon.app.annotations.CurrentTenant;
import com.facilon.app.exception.BusinessException;
import com.facilon.app.module.serviceagent.dto.AuditLogDto;
import com.facilon.app.module.serviceagent.dto.DelegationCreateDto;
import com.facilon.app.module.serviceagent.dto.DelegationDto;
import com.facilon.app.module.serviceagent.dto.InvestorConsentDto;
import com.facilon.app.module.serviceagent.service.DelegationService;
import com.facilon.app.module.serviceagent.service.ServiceAgentAuditService;
import com.facilon.app.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Investor-facing delegation management.
 * Investors grant, view, and revoke access to service agents.
 */
@RestController
@RequestMapping("/api/clients/me/delegations")
@RequiredArgsConstructor
@Slf4j
@CurrentTenant
@Tag(name = "Delegation Management", description = "Investor delegation management APIs")
public class DelegationController {

    private final DelegationService delegationService;
    private final ServiceAgentAuditService auditService;

    @GetMapping
    @Operation(summary = "List all my delegations (active and revoked)")
    public ResponseEntity<List<DelegationDto>> getMyDelegations(Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        return ResponseEntity.ok(delegationService.getMyDelegations(principal.getId()));
    }

    @PostMapping("/{delegationId}/revoke")
    @Operation(summary = "Revoke a delegation")
    public ResponseEntity<Void> revokeDelegation(
            @PathVariable Long delegationId,
            Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        log.info("Investor {} revoking delegation {}", principal.getId(), delegationId);
        delegationService.revokeDelegation(principal.getId(), delegationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{delegationId}")
    @Operation(summary = "Delete a delegation (alternative to revoke)")
    public ResponseEntity<Void> deleteDelegation(
            @PathVariable Long delegationId,
            Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        log.info("Investor {} deleting delegation {}", principal.getId(), delegationId);
        delegationService.revokeDelegation(principal.getId(), delegationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending delegations (assigned by SP, awaiting my consent)")
    public ResponseEntity<List<DelegationDto>> getPendingDelegations(Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        return ResponseEntity.ok(delegationService.getPendingDelegations(principal.getId()));
    }

    @PostMapping("/{delegationId}/accept")
    @Operation(summary = "Accept a pending delegation (investor can customize scope/permissions)")
    public ResponseEntity<DelegationDto> acceptDelegation(
            @PathVariable Long delegationId,
            @RequestBody InvestorConsentDto dto,
            Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        
        if (!Boolean.TRUE.equals(dto.getConsentGiven())) {
            throw new BusinessException("Consent must be given to accept delegation");
        }
        
        log.info("Investor {} accepting delegation {} with custom scope/permissions", principal.getId(), delegationId);
        DelegationDto accepted = delegationService.acceptDelegation(
                principal.getId(), 
                delegationId, 
                dto.getConsentIpAddress(), 
                dto.getConsentVersion(),
                dto.getScope(),
                dto.getCanViewProfile(),
                dto.getCanEditKyc(),
                dto.getCanUploadDocuments(),
                dto.getCanSubmitForms());
        return ResponseEntity.ok(accepted);
    }

    @PostMapping("/{delegationId}/reject")
    @Operation(summary = "Reject a pending delegation")
    public ResponseEntity<Void> rejectDelegation(
            @PathVariable Long delegationId,
            @RequestBody InvestorConsentDto dto,
            Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        log.info("Investor {} rejecting delegation {}", principal.getId(), delegationId);
        delegationService.rejectDelegation(principal.getId(), delegationId, dto.getRejectionReason());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{delegationId}")
    @Operation(summary = "Update an active delegation's permissions")
    public ResponseEntity<DelegationDto> updateDelegation(
            @PathVariable Long delegationId,
            @RequestBody InvestorConsentDto dto,
            Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        log.info("Investor {} updating delegation {} permissions", principal.getId(), delegationId);
        DelegationDto updated = delegationService.updateDelegation(
                principal.getId(),
                delegationId,
                dto.getScope(),
                dto.getCanViewProfile(),
                dto.getCanEditKyc(),
                dto.getCanUploadDocuments(),
                dto.getCanSubmitForms());
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/service-agent-activity")
    @Operation(summary = "View all service agent actions on my account (audit trail)")
    public ResponseEntity<Page<AuditLogDto>> getServiceAgentActivity(
            Authentication auth,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Page<AuditLogDto> activity = auditService.getLogsForInvestor(
                principal.getId(),
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(activity);
    }
}
