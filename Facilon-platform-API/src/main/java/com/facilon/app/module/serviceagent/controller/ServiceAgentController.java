package com.facilon.app.module.serviceagent.controller;

import com.facilon.app.annotations.CurrentTenant;
import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.exception.ResourceNotFoundException;
import com.facilon.app.module.serviceagent.dto.AuditLogDto;
import com.facilon.app.module.serviceagent.dto.ServiceAgentDto;
import com.facilon.app.module.serviceagent.dto.ServiceAgentInvestorDto;
import com.facilon.app.module.serviceagent.model.ServiceAgent;
import com.facilon.app.module.serviceagent.repository.ServiceAgentRepository;
import com.facilon.app.module.serviceagent.service.DelegationService;
import com.facilon.app.module.serviceagent.service.ServiceAgentAuditService;
import com.facilon.app.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/service-agents/me")
@RequiredArgsConstructor
@Slf4j
@CurrentTenant
@Tag(name = "Service Agent", description = "Service Agent APIs")
@PreAuthorize("hasAuthority('SA_VIEW_INVESTORS')")
public class ServiceAgentController {

    private final ServiceAgentRepository serviceAgentRepository;
    private final DelegationService delegationService;
    private final ServiceAgentAuditService auditService;

    @GetMapping("/profile")
    @Operation(summary = "Get my Service Agent profile")
    public ResponseEntity<ServiceAgentDto> getMyProfile(Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Long tenantId = TenantContextHolder.getContext().getTenantId();
        
        ServiceAgent sa = serviceAgentRepository.findByAuthorizedUserIdAndTenantId(principal.getId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Service Agent profile not found"));

        ServiceAgentDto dto = ServiceAgentDto.builder()
                .id(sa.getId())
                .authorizedUserId(sa.getAuthorizedUser() != null ? sa.getAuthorizedUser().getId() : null)
                .agentCode(sa.getAgentCode())
                .agentType(sa.getAgentType())
                .fullName(sa.getFullName())
                .email(sa.getEmail())
                .mobile(sa.getMobile())
                .assignedRegion(sa.getAssignedRegion())
                .assignedSegment(sa.getAssignedSegment())
                .isActive(sa.getIsActive())
                .onboardingStatus(sa.getOnboardingStatus())
                .serviceProviderId(sa.getServiceProviderId())
                .photoUrl(sa.getPhotoUrl())
                .panNumber(sa.getPanNumber())
                .addressProofUrl(sa.getAddressProofUrl())
                .registrationNumber(sa.getRegistrationNumber())
                .onboardedAt(sa.getOnboardedAt())
                .onboardedBy(sa.getOnboardedBy())
                .build();

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/investors")
    @Operation(summary = "Get all investors with active delegations to me")
    public ResponseEntity<List<ServiceAgentInvestorDto>> getMyInvestors(Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        log.info("[ServiceAgentController] getMyInvestors for user {}", principal.getId());
        
        List<ServiceAgentInvestorDto> investors = delegationService.getMyInvestors(principal.getId())
                .stream()
                .map(d -> ServiceAgentInvestorDto.builder()
                        .investorId(d.getInvestorId())
                        .investorName(d.getInvestorName())
                        .investorEmail(d.getInvestorEmail())
                        .investorUniqueCode(d.getInvestorUniqueCode())
                        .delegationId(d.getId())
                        .delegationScope(d.getScope())
                        .scope(d.getScope())
                        .canViewProfile(d.getCanViewProfile())
                        .canEditKyc(d.getCanEditKyc())
                        .canUploadDocuments(d.getCanUploadDocuments())
                        .canSubmitForms(d.getCanSubmitForms())
                        .validFrom(d.getValidFrom())
                        .validTo(d.getValidTo())
                        .delegationActive(d.getIsActive())
                        .isActive(d.getIsActive())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(investors);
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasAuthority('SA_VIEW_AUDIT')")
    @Operation(summary = "Get my audit logs")
    public ResponseEntity<Page<AuditLogDto>> getMyAuditLogs(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Long tenantId = TenantContextHolder.getContext().getTenantId();
        
        ServiceAgent sa = serviceAgentRepository.findByAuthorizedUserIdAndTenantId(principal.getId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Service Agent profile not found"));

        Page<AuditLogDto> logs = auditService.getLogsForServiceAgent(
                sa.getId(), 
                PageRequest.of(page, size, Sort.by("createdAt").descending()));

        return ResponseEntity.ok(logs);
    }

    @GetMapping("/audit-logs/investor/{investorId}")
    @PreAuthorize("hasAuthority('SA_VIEW_AUDIT')")
    @Operation(summary = "Get audit logs for a specific investor")
    public ResponseEntity<Page<AuditLogDto>> getAuditLogsForInvestor(
            @PathVariable Long investorId,
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Long tenantId = TenantContextHolder.getContext().getTenantId();
        
        ServiceAgent sa = serviceAgentRepository.findByAuthorizedUserIdAndTenantId(principal.getId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Service Agent profile not found"));

        Page<AuditLogDto> logs = auditService.getLogsForServiceAgentAndInvestor(
                sa.getId(),
                investorId,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));

        return ResponseEntity.ok(logs);
    }
}
