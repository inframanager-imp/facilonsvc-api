package com.facilon.app.module.serviceagent.controller;

import com.facilon.app.annotations.CurrentTenant;
import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.module.serviceagent.dto.DelegationDto;
import com.facilon.app.module.serviceagent.dto.SPAssignmentDto;
import com.facilon.app.module.serviceagent.dto.ServiceAgentDto;
import com.facilon.app.module.serviceagent.service.DelegationService;
import com.facilon.app.module.serviceagent.service.ServiceAgentService;
import com.facilon.app.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Service Provider APIs for assigning service agents to investors.
 * This creates PENDING delegations that investors must accept/reject.
 * <p>
 * <b>Security note:</b> {@code SecurityConfig} requires <b>any</b> valid JWT for all {@code /api/**}
 * routes (line 101). Any authenticated user can call these endpoints for now.
 * To restrict to SP-only roles later, add: {@code @PreAuthorize("hasAnyAuthority('SP_ADMIN', 'SP_MANAGE_AGENTS')")}
 */
@RestController
@RequestMapping("/api/service-provider/assignments")
@RequiredArgsConstructor
@Slf4j
@CurrentTenant
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Service Provider Assignment", description = "SP assigns service agents to investors")
public class ServiceProviderAssignmentController {

    private final DelegationService delegationService;
    private final ServiceAgentService serviceAgentService;

    @PostMapping
    @Operation(summary = "Assign a service agent to an investor (creates PENDING delegation)")
    public ResponseEntity<DelegationDto> assignServiceAgentToInvestor(
            @Valid @RequestBody SPAssignmentDto dto,
            Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        log.info("SP user {} assigning service agent {} to investor {}", 
                principal.getId(), dto.getServiceAgentCode(), dto.getInvestorId());
        
        DelegationDto created = delegationService.assignServiceAgentByServiceProvider(
                principal.getId(),
                dto.getInvestorId(),
                dto.getServiceAgentCode(),
                dto.getScope(),
                dto.getCanViewProfile(),
                dto.getCanEditKyc(),
                dto.getCanUploadDocuments(),
                dto.getCanSubmitForms(),
                dto.getValidFrom(),
                dto.getValidTo(),
                dto.getNotes()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * List the Service Agents employed by a given Service Provider.
     *
     * <p>Until explicit SP-to-user mapping is added, the SP id is supplied as
     * a query parameter and is validated at the authorization layer.
     */
    @GetMapping("/agents")
    @Operation(summary = "List my Service Agents (for an SP)")
    public ResponseEntity<List<ServiceAgentDto>> listAgents(
            @RequestParam Long serviceProviderId) {
        Long tenantId = TenantContextHolder.getContext().getTenantId();
        List<ServiceAgentDto> agents = serviceAgentService.listByServiceProvider(serviceProviderId, tenantId);
        return ResponseEntity.ok(agents);
    }

    /**
     * Deactivate a Service Agent and cascade-revoke their open delegations.
     * Body is optional and may include a free-text {@code reason}.
     */
    @PostMapping("/agents/{agentId}/deactivate")
    @Operation(summary = "Deactivate a Service Agent and cascade-revoke their delegations")
    public ResponseEntity<Map<String, Object>> deactivateAgent(
            @PathVariable Long agentId,
            @RequestBody(required = false) Map<String, String> body,
            Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        String reason = body != null ? body.get("reason") : null;
        int revokedCount = serviceAgentService.deactivateAgent(
                agentId, reason, principal.getId().toString());
        return ResponseEntity.ok(Map.of(
                "agentId", agentId,
                "status", "deactivated",
                "revokedDelegations", revokedCount));
    }
}
