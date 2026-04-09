package com.facilon.app.module.serviceagent.repository;

import com.facilon.app.module.serviceagent.model.ServiceAgentAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceAgentAuditLogRepository extends JpaRepository<ServiceAgentAuditLog, Long> {

    Page<ServiceAgentAuditLog> findByServiceAgentIdOrderByCreatedAtDesc(Long serviceAgentId, Pageable pageable);

    Page<ServiceAgentAuditLog> findByInvestorIdOrderByCreatedAtDesc(Long investorId, Pageable pageable);

    Page<ServiceAgentAuditLog> findByServiceAgentIdAndInvestorIdOrderByCreatedAtDesc(
            Long serviceAgentId, Long investorId, Pageable pageable);
}
