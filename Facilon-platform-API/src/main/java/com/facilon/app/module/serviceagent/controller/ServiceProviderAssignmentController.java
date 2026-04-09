package com.facilon.app.module.serviceagent.controller;

import com.facilon.app.annotations.CurrentTenant;
import com.facilon.app.module.serviceagent.dto.DelegationDto;
import com.facilon.app.module.serviceagent.dto.SPAssignmentDto;
import com.facilon.app.module.serviceagent.service.DelegationService;
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
}
