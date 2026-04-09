package com.facilon.app.module.serviceagent.service;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.module.serviceagent.dto.AuditLogDto;
import com.facilon.app.module.serviceagent.model.ServiceAgent;
import com.facilon.app.module.serviceagent.model.ServiceAgentAuditLog;
import com.facilon.app.module.serviceagent.repository.ServiceAgentAuditRepository;
import com.facilon.app.module.serviceagent.repository.ServiceAgentRepository;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.repository.InvestorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceAgentAuditService {

    private final ServiceAgentAuditRepository auditRepository;
    private final ServiceAgentRepository serviceAgentRepository;
    private final InvestorRepository investorRepository;

    @Transactional(readOnly = true)
    public Page<AuditLogDto> getLogsForServiceAgent(Long serviceAgentId, Pageable pageable) {
        Long tenantId = TenantContextHolder.getContext().getTenantId();
        Page<ServiceAgentAuditLog> logs = auditRepository.findByServiceAgentIdAndTenantId(serviceAgentId, tenantId, pageable);
        return logs.map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogDto> getLogsForInvestor(Long investorId, Pageable pageable) {
        Long tenantId = TenantContextHolder.getContext().getTenantId();
        Page<ServiceAgentAuditLog> logs = auditRepository.findByInvestorIdAndTenantId(investorId, tenantId, pageable);
        return logs.map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogDto> getLogsForServiceAgentAndInvestor(Long serviceAgentId, Long investorId, Pageable pageable) {
        Long tenantId = TenantContextHolder.getContext().getTenantId();
        Page<ServiceAgentAuditLog> logs = auditRepository.findByServiceAgentIdAndInvestorIdAndTenantId(
                serviceAgentId, investorId, tenantId, pageable);
        return logs.map(this::mapToDto);
    }

    @Transactional
    public void logAction(ServiceAgentAuditLog log) {
        auditRepository.save(log);
    }

    private AuditLogDto mapToDto(ServiceAgentAuditLog log) {
        ServiceAgent sa = serviceAgentRepository.findById(log.getServiceAgentId()).orElse(null);
        Investor inv = investorRepository.findById(log.getInvestorId()).orElse(null);

        return AuditLogDto.builder()
                .id(log.getId())
                .serviceAgentId(log.getServiceAgentId())
                .serviceAgentName(sa != null ? sa.getFullName() : null)
                .investorId(log.getInvestorId())
                .investorName(inv != null ? buildInvestorName(inv) : null)
                .delegationId(log.getDelegationId())
                .actionType(log.getActionType())
                .actionCategory(log.getActionCategory())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .endpoint(log.getEndpoint())
                .httpMethod(log.getHttpMethod())
                .success(log.getSuccess())
                .errorMessage(log.getErrorMessage())
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .requestPayload(log.getRequestPayload())
                .responseStatus(log.getResponseStatus())
                .fieldChanged(log.getFieldChanged())
                .oldValue(log.getOldValue())
                .newValue(log.getNewValue())
                .sessionId(log.getSessionId())
                .createdAt(log.getCreatedAt())
                .build();
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
