package com.facilon.app.module.serviceagent.repository;

import com.facilon.app.module.serviceagent.model.ServiceAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceAgentRepository extends JpaRepository<ServiceAgent, Long> {

    /** All agents belonging to a given Service Provider (within the current tenant). */
    @Query("SELECT s FROM ServiceAgent s " +
           "WHERE s.serviceProviderId = :serviceProviderId " +
           "AND s.tenant.id = :tenantId " +
           "ORDER BY s.fullName")
    List<ServiceAgent> findByServiceProviderIdAndTenantId(
            @Param("serviceProviderId") Long serviceProviderId,
            @Param("tenantId") Long tenantId);
    
    Optional<ServiceAgent> findByAuthorizedUser_Id(Long authorizedUserId);
    
    boolean existsByAuthorizedUser_Id(Long authorizedUserId);
    
    Optional<ServiceAgent> findByAgentCode(String agentCode);
    
    @Query("SELECT s FROM ServiceAgent s WHERE s.authorizedUser.id = :authorizedUserId AND s.tenant.id = :tenantId")
    Optional<ServiceAgent> findByAuthorizedUserIdAndTenantId(@Param("authorizedUserId") Long authorizedUserId, @Param("tenantId") Long tenantId);
    
    @Query("SELECT s FROM ServiceAgent s WHERE s.agentCode = :agentCode AND s.tenant.id = :tenantId")
    Optional<ServiceAgent> findByAgentCodeAndTenantId(@Param("agentCode") String agentCode, @Param("tenantId") Long tenantId);
    
    @Query("SELECT s FROM ServiceAgent s WHERE s.email = :email AND s.tenant.id = :tenantId")
    Optional<ServiceAgent> findByEmailAndTenantId(@Param("email") String email, @Param("tenantId") Long tenantId);
}
