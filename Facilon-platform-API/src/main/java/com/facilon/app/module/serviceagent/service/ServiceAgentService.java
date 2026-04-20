package com.facilon.app.module.serviceagent.service;

import com.facilon.app.exception.ResourceNotFoundException;
import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.module.serviceagent.dto.ServiceAgentDto;
import com.facilon.app.module.serviceagent.dto.ServiceAgentProfileUpdateDto;
import com.facilon.app.module.serviceagent.model.InvestorServiceAgentDelegation;
import com.facilon.app.module.serviceagent.model.ServiceAgent;
import com.facilon.app.module.serviceagent.repository.DelegationRepository;
import com.facilon.app.module.serviceagent.repository.ServiceAgentRepository;
import com.facilon.app.repository.AuthorizedUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ServiceAgentService {

    private final ServiceAgentRepository serviceAgentRepository;
    private final AuthorizedUserRepository authorizedUserRepository;
    private final DelegationRepository delegationRepository;
    private final ObjectProvider<InvestorRepository> investorRepositoryProvider;
    private final ObjectProvider<DelegationNotificationService> notifierProvider;

    /** Find the ServiceAgent record for the currently-authenticated user. */
    @Transactional(readOnly = true)
    public ServiceAgent getByAuthorizedUserId(Long authorizedUserId) {
        return serviceAgentRepository.findByAuthorizedUser_Id(authorizedUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service agent profile not found for user " + authorizedUserId));
    }

    @Transactional(readOnly = true)
    public ServiceAgent getById(Long serviceAgentId) {
        return serviceAgentRepository.findById(serviceAgentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service agent not found: " + serviceAgentId));
    }

    @Transactional(readOnly = true)
    public ServiceAgent getByAgentCode(String agentCode) {
        return serviceAgentRepository.findByAgentCode(agentCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service agent not found with code: " + agentCode));
    }

    /** Get my profile as a DTO. */
    @Transactional(readOnly = true)
    public ServiceAgentDto getMyProfile(Long authorizedUserId) {
        return toDto(getByAuthorizedUserId(authorizedUserId));
    }

    /**
     * Auto-provision a ServiceAgent profile when a user with SERVICE_AGENT role
     * logs in for the first time and has no profile record yet.
     */
    public ServiceAgent ensureProfileExists(Long authorizedUserId) {
        if (serviceAgentRepository.existsByAuthorizedUser_Id(authorizedUserId)) {
            return serviceAgentRepository.findByAuthorizedUser_Id(authorizedUserId).get();
        }

        AuthorizedUser user = authorizedUserRepository.findById(authorizedUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + authorizedUserId));

        String agentCode = "SA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String fullName = (user.getFirstName() != null ? user.getFirstName() : "") +
                          (user.getLastName()  != null ? " " + user.getLastName() : "");

        ServiceAgent agent = ServiceAgent.builder()
                .authorizedUser(user)
                .agentCode(agentCode)
                .fullName(fullName.trim())
                .email(user.getEmailId())
                .mobile(user.getMobilePhone())
                .isActive(true)
                .onboardingStatus("APPROVED")
                .build();

        return serviceAgentRepository.save(agent);
    }

    /**
     * Update the authenticated SA's own profile.  Only fields supplied on the
     * payload (non-null) are changed; administrative fields (agent code,
     * service provider id, active flag, etc.) are never touched.
     */
    public ServiceAgentDto updateMyProfile(Long authorizedUserId, ServiceAgentProfileUpdateDto dto) {
        ServiceAgent agent = getByAuthorizedUserId(authorizedUserId);
        if (dto.getFullName() != null)            agent.setFullName(dto.getFullName());
        if (dto.getMobile() != null)              agent.setMobile(dto.getMobile());
        if (dto.getAssignedRegion() != null)      agent.setAssignedRegion(dto.getAssignedRegion());
        if (dto.getAssignedSegment() != null)     agent.setAssignedSegment(dto.getAssignedSegment());
        if (dto.getPhotoUrl() != null)            agent.setPhotoUrl(dto.getPhotoUrl());
        if (dto.getPanNumber() != null)           agent.setPanNumber(dto.getPanNumber());
        if (dto.getAddressProofUrl() != null)     agent.setAddressProofUrl(dto.getAddressProofUrl());
        if (dto.getRegistrationNumber() != null)  agent.setRegistrationNumber(dto.getRegistrationNumber());
        if (dto.getAgentType() != null)           agent.setAgentType(dto.getAgentType());
        agent = serviceAgentRepository.save(agent);
        log.info("[ServiceAgentService] Updated SA profile for user {}", authorizedUserId);
        return toDto(agent);
    }

    /**
     * List all Service Agents employed by a given Service Provider.
     */
    @Transactional(readOnly = true)
    public List<ServiceAgentDto> listByServiceProvider(Long serviceProviderId, Long tenantId) {
        return serviceAgentRepository.findByServiceProviderIdAndTenantId(serviceProviderId, tenantId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Deactivate a Service Agent and cascade-revoke their open delegations.
     *
     * <p>All ACTIVE and PENDING delegations currently held by the agent are
     * flipped to {@code REVOKED} / {@code isActive = false} with a
     * system-generated reason, and the corresponding investors are notified
     * so they know access has been withdrawn.
     *
     * <p>The agent account itself is <em>not deleted</em> — we keep it around
     * for audit-log referential integrity and so the agent could be
     * reactivated later by an admin.
     *
     * @return number of delegations revoked as part of the cascade.
     */
    public int deactivateAgent(Long agentId, String reason, String actorUserId) {
        ServiceAgent agent = getById(agentId);
        agent.setIsActive(false);
        serviceAgentRepository.save(agent);

        List<InvestorServiceAgentDelegation> open =
                delegationRepository.findOpenDelegationsByServiceAgent(agentId);
        LocalDateTime now = LocalDateTime.now();
        String effectiveReason = (reason == null || reason.isBlank())
                ? "Service Agent was deactivated"
                : reason;

        InvestorRepository investorRepo = investorRepositoryProvider.getIfAvailable();
        DelegationNotificationService notifier = notifierProvider.getIfAvailable();

        for (InvestorServiceAgentDelegation d : open) {
            d.setStatus("REVOKED");
            d.setIsActive(false);
            d.setRevocationReason(effectiveReason);
            d.setRevokedAt(now);
            d.setRevokedBy(actorUserId != null ? actorUserId : "system:sa-deactivate");
        }
        delegationRepository.saveAll(open);

        if (notifier != null && investorRepo != null) {
            for (InvestorServiceAgentDelegation d : open) {
                Investor investor = investorRepo.findById(d.getInvestorId()).orElse(null);
                if (investor != null) {
                    notifier.notifyRevoked(d, investor, agent, effectiveReason);
                }
            }
        }

        log.info("[ServiceAgentService] Deactivated agent {} — cascade-revoked {} delegations",
                agentId, open.size());
        return open.size();
    }

    public ServiceAgentDto toDto(ServiceAgent agent) {
        return ServiceAgentDto.builder()
                .id(agent.getId())
                .authorizedUserId(agent.getAuthorizedUser().getId())
                .agentCode(agent.getAgentCode())
                .agentType(agent.getAgentType())
                .fullName(agent.getFullName())
                .email(agent.getEmail())
                .mobile(agent.getMobile())
                .assignedRegion(agent.getAssignedRegion())
                .assignedSegment(agent.getAssignedSegment())
                .isActive(agent.getIsActive())
                .onboardingStatus(agent.getOnboardingStatus())
                .build();
    }
}
