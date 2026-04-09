package com.facilon.app.module.serviceagent.service;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.exception.BusinessException;
import com.facilon.app.exception.ResourceNotFoundException;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.module.serviceagent.dto.DelegationCreateDto;
import com.facilon.app.module.serviceagent.dto.DelegationDto;
import com.facilon.app.module.serviceagent.model.InvestorServiceAgentDelegation;
import com.facilon.app.module.serviceagent.model.ServiceAgent;
import com.facilon.app.module.serviceagent.repository.DelegationRepository;
import com.facilon.app.module.serviceagent.repository.ServiceAgentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DelegationService {

    private final DelegationRepository delegationRepository;
    private final ServiceAgentRepository serviceAgentRepository;
    private final InvestorRepository investorRepository;

    @Transactional
    public DelegationDto grantDelegation(Long authorizedUserId, DelegationCreateDto dto) {
        Long tenantId = TenantContextHolder.getContext().getTenantId();
        log.info("[DelegationService] Granting delegation for user {} to agent {}", authorizedUserId, dto.getServiceAgentCode());

        Investor investor = investorRepository.findByAuthorizedUserIdAndTenantId(authorizedUserId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Investor profile not found for this user"));

        ServiceAgent serviceAgent = serviceAgentRepository.findByAgentCodeAndTenantId(dto.getServiceAgentCode(), tenantId)
                .orElse(null);
        
        if (serviceAgent == null) {
            serviceAgent = serviceAgentRepository.findByEmailAndTenantId(dto.getServiceAgentCode(), tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Service Agent not found with code/email: " + dto.getServiceAgentCode()));
        }

        if (!Boolean.TRUE.equals(serviceAgent.getIsActive())) {
            throw new BusinessException("Service Agent is not active");
        }

        if (!Boolean.TRUE.equals(dto.getConsentGiven())) {
            throw new BusinessException("Consent must be given to grant delegation");
        }

        InvestorServiceAgentDelegation delegation = InvestorServiceAgentDelegation.builder()
                .investorId(investor.getId())
                .serviceAgentId(serviceAgent.getId())
                .scope(dto.getScope())
                .canViewProfile(dto.getCanViewProfile() != null ? dto.getCanViewProfile() : true)
                .canEditKyc(dto.getCanEditKyc() != null ? dto.getCanEditKyc() : false)
                .canUploadDocuments(dto.getCanUploadDocuments() != null ? dto.getCanUploadDocuments() : false)
                .canSubmitForms(dto.getCanSubmitForms() != null ? dto.getCanSubmitForms() : false)
                .validFrom(dto.getValidFrom() != null ? dto.getValidFrom() : LocalDate.now())
                .validTo(dto.getValidTo())
                .isActive(true)
                .status("ACTIVE")
                .consentVersion(dto.getConsentVersion() != null ? dto.getConsentVersion() : "1.0")
                .notes(dto.getNotes())
                .consentGivenAt(LocalDateTime.now())
                .consentIpAddress(dto.getConsentIpAddress())
                .build();

        delegation = delegationRepository.save(delegation);
        log.info("[DelegationService] Created delegation {} for investor {} → agent {}", 
                delegation.getId(), investor.getId(), serviceAgent.getId());

        return mapToDto(delegation, investor, serviceAgent);
    }

    @Transactional(readOnly = true)
    public List<DelegationDto> getMyDelegations(Long authorizedUserId) {
        Long tenantId = TenantContextHolder.getContext().getTenantId();
        
        Investor investor = investorRepository.findByAuthorizedUserIdAndTenantId(authorizedUserId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Investor profile not found"));

        List<InvestorServiceAgentDelegation> delegations =
                delegationRepository.findByInvestorIdAndTenantId(investor.getId(), tenantId);
        
        log.info("[DelegationService] Found {} total delegations for investor {}", delegations.size(), investor.getId());

        List<DelegationDto> filtered = delegations.stream()
                .filter(d -> {
                    boolean include = "PENDING".equals(d.getStatus()) || 
                                     "ACTIVE".equals(d.getStatus()) || 
                                     Boolean.TRUE.equals(d.getIsActive());
                    if (!include) {
                        log.debug("[DelegationService] Filtering out delegation {} with status={}, isActive={}", 
                                d.getId(), d.getStatus(), d.getIsActive());
                    }
                    return include;
                })
                .map(d -> {
                    ServiceAgent sa = serviceAgentRepository.findById(d.getServiceAgentId()).orElse(null);
                    return mapToDto(d, investor, sa);
                })
                .collect(Collectors.toList());
        
        log.info("[DelegationService] Returning {} filtered delegations (PENDING or ACTIVE only)", filtered.size());
        return filtered;
    }

    @Transactional
    public void revokeDelegation(Long authorizedUserId, Long delegationId) {
        Long tenantId = TenantContextHolder.getContext().getTenantId();
        
        Investor investor = investorRepository.findByAuthorizedUserIdAndTenantId(authorizedUserId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Investor profile not found"));

        InvestorServiceAgentDelegation delegation = delegationRepository.findById(delegationId)
                .orElseThrow(() -> new ResourceNotFoundException("Delegation not found"));

        if (!delegation.getInvestorId().equals(investor.getId())) {
            throw new BusinessException("You can only revoke your own delegations");
        }

        delegation.setIsActive(false);
        delegation.setStatus("REVOKED");
        delegation.setRevokedAt(LocalDateTime.now());
        delegation.setRevokedBy(authorizedUserId.toString());
        delegationRepository.save(delegation);

        log.info("[DelegationService] Revoked delegation {} by investor {}", delegationId, investor.getId());
    }

    @Transactional(readOnly = true)
    public List<DelegationDto> getMyInvestors(Long authorizedUserId) {
        Long tenantId = TenantContextHolder.getContext().getTenantId();
        
        ServiceAgent serviceAgent = serviceAgentRepository.findByAuthorizedUserIdAndTenantId(authorizedUserId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Service Agent profile not found for this user"));

        LocalDate today = LocalDate.now();
        List<InvestorServiceAgentDelegation> delegations = 
                delegationRepository.findActiveDelegationsForAgent(serviceAgent.getId(), today, tenantId);

        return delegations.stream()
                .map(d -> {
                    Investor inv = investorRepository.findById(d.getInvestorId()).orElse(null);
                    return mapToDto(d, inv, serviceAgent);
                })
                .collect(Collectors.toList());
    }

    private DelegationDto mapToDto(InvestorServiceAgentDelegation delegation, Investor investor, ServiceAgent serviceAgent) {
        return DelegationDto.builder()
                .id(delegation.getId())
                .investorId(delegation.getInvestorId())
                .investorName(investor != null ? buildInvestorName(investor) : null)
                .investorEmail(investor != null && investor.getAuthorizedUser() != null ? investor.getAuthorizedUser().getEmailId() : null)
                .investorUniqueCode(investor != null ? investor.getUniqueCode() : null)
                .serviceAgentId(delegation.getServiceAgentId())
                .serviceAgentName(serviceAgent != null ? serviceAgent.getFullName() : null)
                .serviceAgentEmail(serviceAgent != null ? serviceAgent.getEmail() : null)
                .serviceAgentCode(serviceAgent != null ? serviceAgent.getAgentCode() : null)
                .scope(delegation.getScope())
                .canViewProfile(delegation.getCanViewProfile())
                .canEditKyc(delegation.getCanEditKyc())
                .canUploadDocuments(delegation.getCanUploadDocuments())
                .canSubmitForms(delegation.getCanSubmitForms())
                .validFrom(delegation.getValidFrom())
                .validTo(delegation.getValidTo())
                .isActive(delegation.getIsActive())
                .status(delegation.getStatus())
                .assignedBySpId(delegation.getAssignedBySpId())
                .consentVersion(delegation.getConsentVersion())
                .notes(delegation.getNotes())
                .consentGivenAt(delegation.getConsentGivenAt())
                .consentIpAddress(delegation.getConsentIpAddress())
                .revokedAt(delegation.getRevokedAt())
                .revokedBy(delegation.getRevokedBy())
                .revocationReason(delegation.getRevocationReason())
                .createdAt(delegation.getCreatedAt())
                .modifiedAt(delegation.getModifiedAt())
                .build();
    }

    @Transactional
    public DelegationDto assignServiceAgentByServiceProvider(Long serviceProviderId, Long investorId, String serviceAgentCode, String scope, 
                                                              Boolean canViewProfile, Boolean canEditKyc, Boolean canUploadDocuments, 
                                                              Boolean canSubmitForms, LocalDate validFrom, LocalDate validTo, String notes) {
        Long tenantId = TenantContextHolder.getContext().getTenantId();
        log.info("[DelegationService] SP {} assigning SA {} to investor {}", serviceProviderId, serviceAgentCode, investorId);

        Investor investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found: " + investorId));

        ServiceAgent serviceAgent = serviceAgentRepository.findByAgentCodeAndTenantId(serviceAgentCode, tenantId)
                .orElse(null);
        
        if (serviceAgent == null) {
            serviceAgent = serviceAgentRepository.findByEmailAndTenantId(serviceAgentCode, tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Service Agent not found: " + serviceAgentCode));
        }

        if (!Boolean.TRUE.equals(serviceAgent.getIsActive())) {
            throw new BusinessException("Service Agent is not active");
        }

        List<InvestorServiceAgentDelegation> existingDelegations = 
                delegationRepository.findByInvestorIdAndTenantId(investorId, tenantId);
        
        for (InvestorServiceAgentDelegation existing : existingDelegations) {
            if (existing.getServiceAgentId().equals(serviceAgent.getId())) {
                if ("PENDING".equals(existing.getStatus())) {
                    log.warn("[DelegationService] PENDING delegation already exists: {} for investor {} → agent {}", 
                            existing.getId(), investorId, serviceAgent.getId());
                    throw new BusinessException("A pending assignment already exists for this service agent. Please wait for investor to accept/reject.");
                }
                
                if (Boolean.TRUE.equals(existing.getIsActive()) && "ACTIVE".equals(existing.getStatus())) {
                    LocalDate today = LocalDate.now();
                    boolean stillValid = (existing.getValidFrom() == null || !existing.getValidFrom().isAfter(today)) &&
                                        (existing.getValidTo() == null || !existing.getValidTo().isBefore(today));
                    if (stillValid) {
                        log.warn("[DelegationService] Active delegation already exists: {} for investor {} → agent {}", 
                                existing.getId(), investorId, serviceAgent.getId());
                        throw new BusinessException("An active delegation already exists for this service agent.");
                    }
                }
            }
        }

        InvestorServiceAgentDelegation delegation = InvestorServiceAgentDelegation.builder()
                .investorId(investorId)
                .serviceAgentId(serviceAgent.getId())
                .scope(scope)
                .canViewProfile(canViewProfile != null ? canViewProfile : true)
                .canEditKyc(canEditKyc != null ? canEditKyc : false)
                .canUploadDocuments(canUploadDocuments != null ? canUploadDocuments : false)
                .canSubmitForms(canSubmitForms != null ? canSubmitForms : false)
                .validFrom(validFrom != null ? validFrom : LocalDate.now())
                .validTo(validTo)
                .isActive(false)
                .status("PENDING")
                .assignedBySpId(serviceProviderId)
                .notes(notes)
                .build();

        delegation = delegationRepository.save(delegation);
        log.info("[DelegationService] Created PENDING delegation {} for investor {} → agent {} (assigned by SP {})", 
                delegation.getId(), investorId, serviceAgent.getId(), serviceProviderId);

        return mapToDto(delegation, investor, serviceAgent);
    }

    @Transactional(readOnly = true)
    public List<DelegationDto> getPendingDelegations(Long authorizedUserId) {
        Long tenantId = TenantContextHolder.getContext().getTenantId();
        
        Investor investor = investorRepository.findByAuthorizedUserIdAndTenantId(authorizedUserId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Investor profile not found"));

        List<InvestorServiceAgentDelegation> pending =
                delegationRepository.findPendingDelegationsForInvestor(investor.getId(), tenantId);

        return pending.stream()
                .map(d -> {
                    ServiceAgent sa = serviceAgentRepository.findById(d.getServiceAgentId()).orElse(null);
                    return mapToDto(d, investor, sa);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public DelegationDto acceptDelegation(Long authorizedUserId, Long delegationId, String consentIpAddress, String consentVersion,
                                          String scope, Boolean canViewProfile, Boolean canEditKyc, 
                                          Boolean canUploadDocuments, Boolean canSubmitForms) {
        Long tenantId = TenantContextHolder.getContext().getTenantId();
        
        Investor investor = investorRepository.findByAuthorizedUserIdAndTenantId(authorizedUserId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Investor profile not found"));

        InvestorServiceAgentDelegation delegation = delegationRepository.findById(delegationId)
                .orElseThrow(() -> new ResourceNotFoundException("Delegation not found"));

        if (!delegation.getInvestorId().equals(investor.getId())) {
            throw new BusinessException("You can only accept your own delegations");
        }

        if (!"PENDING".equals(delegation.getStatus())) {
            throw new BusinessException("Delegation is not in PENDING status");
        }

        delegation.setStatus("ACTIVE");
        delegation.setIsActive(true);
        
        if (scope != null) {
            delegation.setScope(scope);
        }
        if (canViewProfile != null) {
            delegation.setCanViewProfile(canViewProfile);
        }
        if (canEditKyc != null) {
            delegation.setCanEditKyc(canEditKyc);
        }
        if (canUploadDocuments != null) {
            delegation.setCanUploadDocuments(canUploadDocuments);
        }
        if (canSubmitForms != null) {
            delegation.setCanSubmitForms(canSubmitForms);
        }
        
        delegation.setConsentGivenAt(LocalDateTime.now());
        delegation.setConsentIpAddress(consentIpAddress);
        delegation.setConsentVersion(consentVersion != null ? consentVersion : "1.0");
        delegation = delegationRepository.save(delegation);

        log.info("[DelegationService] Investor {} accepted delegation {} with scope={}, view={}, edit={}, upload={}, submit={}",
                investor.getId(), delegationId, delegation.getScope(),
                delegation.getCanViewProfile(), delegation.getCanEditKyc(),
                delegation.getCanUploadDocuments(), delegation.getCanSubmitForms());

        ServiceAgent sa = serviceAgentRepository.findById(delegation.getServiceAgentId()).orElse(null);
        return mapToDto(delegation, investor, sa);
    }

    @Transactional
    public void rejectDelegation(Long authorizedUserId, Long delegationId, String reason) {
        Long tenantId = TenantContextHolder.getContext().getTenantId();
        
        Investor investor = investorRepository.findByAuthorizedUserIdAndTenantId(authorizedUserId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Investor profile not found"));

        InvestorServiceAgentDelegation delegation = delegationRepository.findById(delegationId)
                .orElseThrow(() -> new ResourceNotFoundException("Delegation not found"));

        if (!delegation.getInvestorId().equals(investor.getId())) {
            throw new BusinessException("You can only reject your own delegations");
        }

        if (!"PENDING".equals(delegation.getStatus())) {
            throw new BusinessException("Delegation is not in PENDING status");
        }

        delegation.setStatus("REJECTED");
        delegation.setIsActive(false);
        delegation.setRevocationReason(reason);
        delegation.setRevokedAt(LocalDateTime.now());
        delegation.setRevokedBy(authorizedUserId.toString());
        delegationRepository.save(delegation);

        log.info("[DelegationService] Investor {} rejected delegation {}", investor.getId(), delegationId);
    }

    @Transactional
    public DelegationDto updateDelegation(Long authorizedUserId, Long delegationId,
                                          String scope, Boolean canViewProfile, Boolean canEditKyc,
                                          Boolean canUploadDocuments, Boolean canSubmitForms) {
        Long tenantId = TenantContextHolder.getContext().getTenantId();
        
        Investor investor = investorRepository.findByAuthorizedUserIdAndTenantId(authorizedUserId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Investor profile not found"));

        InvestorServiceAgentDelegation delegation = delegationRepository.findById(delegationId)
                .orElseThrow(() -> new ResourceNotFoundException("Delegation not found"));

        if (!delegation.getInvestorId().equals(investor.getId())) {
            throw new BusinessException("You can only update your own delegations");
        }

        if (!"ACTIVE".equals(delegation.getStatus())) {
            throw new BusinessException("Can only update ACTIVE delegations");
        }

        if (scope != null) {
            delegation.setScope(scope);
        }
        if (canViewProfile != null) {
            delegation.setCanViewProfile(canViewProfile);
        }
        if (canEditKyc != null) {
            delegation.setCanEditKyc(canEditKyc);
        }
        if (canUploadDocuments != null) {
            delegation.setCanUploadDocuments(canUploadDocuments);
        }
        if (canSubmitForms != null) {
            delegation.setCanSubmitForms(canSubmitForms);
        }
        
        delegation = delegationRepository.save(delegation);

        log.info("[DelegationService] Investor {} updated delegation {} with scope={}, view={}, edit={}, upload={}, submit={}",
                investor.getId(), delegationId, delegation.getScope(),
                delegation.getCanViewProfile(), delegation.getCanEditKyc(),
                delegation.getCanUploadDocuments(), delegation.getCanSubmitForms());

        ServiceAgent sa = serviceAgentRepository.findById(delegation.getServiceAgentId()).orElse(null);
        return mapToDto(delegation, investor, sa);
    }

    private String buildInvestorName(Investor investor) {
        if (investor.getAuthorizedUser() == null) {
            return "Unknown";
        }
        String firstName = investor.getAuthorizedUser().getFirstName() != null ? investor.getAuthorizedUser().getFirstName() : "";
        String lastName = investor.getAuthorizedUser().getLastName() != null ? investor.getAuthorizedUser().getLastName() : "";
        return (firstName + " " + lastName).replaceAll("\\s+", " ").trim();
    }
}
