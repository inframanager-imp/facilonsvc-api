package com.facilon.app.module.serviceagent.service;

import com.facilon.app.exception.ResourceNotFoundException;
import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.module.serviceagent.dto.ServiceAgentDto;
import com.facilon.app.module.serviceagent.model.ServiceAgent;
import com.facilon.app.module.serviceagent.repository.ServiceAgentRepository;
import com.facilon.app.repository.AuthorizedUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ServiceAgentService {

    private final ServiceAgentRepository serviceAgentRepository;
    private final AuthorizedUserRepository authorizedUserRepository;

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
