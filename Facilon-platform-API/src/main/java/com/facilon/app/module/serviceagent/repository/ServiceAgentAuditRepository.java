package com.facilon.app.module.serviceagent.repository;

import com.facilon.app.module.serviceagent.model.ServiceAgentAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceAgentAuditRepository extends JpaRepository<ServiceAgentAuditLog, Long> {
    
    @Query("SELECT a FROM ServiceAgentAuditLog a WHERE a.serviceAgentId = :serviceAgentId AND a.tenant.id = :tenantId")
    Page<ServiceAgentAuditLog> findByServiceAgentIdAndTenantId(@Param("serviceAgentId") Long serviceAgentId, @Param("tenantId") Long tenantId, Pageable pageable);
    
    @Query("SELECT a FROM ServiceAgentAuditLog a WHERE a.investorId = :investorId AND a.tenant.id = :tenantId")
    Page<ServiceAgentAuditLog> findByInvestorIdAndTenantId(@Param("investorId") Long investorId, @Param("tenantId") Long tenantId, Pageable pageable);
    
    @Query("SELECT a FROM ServiceAgentAuditLog a WHERE a.serviceAgentId = :serviceAgentId AND a.investorId = :investorId AND a.tenant.id = :tenantId")
    Page<ServiceAgentAuditLog> findByServiceAgentIdAndInvestorIdAndTenantId(
            @Param("serviceAgentId") Long serviceAgentId, @Param("investorId") Long investorId, @Param("tenantId") Long tenantId, Pageable pageable);
}
