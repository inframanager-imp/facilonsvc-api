package com.facilon.app.module.serviceagent.repository;

import com.facilon.app.module.serviceagent.model.InvestorServiceAgentDelegation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DelegationRepository extends JpaRepository<InvestorServiceAgentDelegation, Long> {
    
    @Query("SELECT d FROM InvestorServiceAgentDelegation d WHERE d.investorId = :investorId AND d.tenant.id = :tenantId")
    List<InvestorServiceAgentDelegation> findByInvestorIdAndTenantId(@Param("investorId") Long investorId, @Param("tenantId") Long tenantId);
    
    @Query("SELECT d FROM InvestorServiceAgentDelegation d WHERE d.serviceAgentId = :serviceAgentId AND d.tenant.id = :tenantId")
    List<InvestorServiceAgentDelegation> findByServiceAgentIdAndTenantId(@Param("serviceAgentId") Long serviceAgentId, @Param("tenantId") Long tenantId);
    
    @Query("SELECT d FROM InvestorServiceAgentDelegation d " +
           "WHERE d.investorId = :investorId " +
           "AND d.serviceAgentId = :serviceAgentId " +
           "AND d.isActive = true " +
           "AND d.tenant.id = :tenantId " +
           "AND (d.validFrom IS NULL OR d.validFrom <= :today) " +
           "AND (d.validTo IS NULL OR d.validTo >= :today)")
    Optional<InvestorServiceAgentDelegation> findActiveDelegation(
            @Param("investorId") Long investorId,
            @Param("serviceAgentId") Long serviceAgentId,
            @Param("today") LocalDate today,
            @Param("tenantId") Long tenantId
    );
    
    @Query("SELECT d FROM InvestorServiceAgentDelegation d " +
           "WHERE d.serviceAgentId = :serviceAgentId " +
           "AND d.isActive = true " +
           "AND d.status = 'ACTIVE' " +
           "AND d.tenant.id = :tenantId " +
           "AND (d.validFrom IS NULL OR d.validFrom <= :today) " +
           "AND (d.validTo IS NULL OR d.validTo >= :today)")
    List<InvestorServiceAgentDelegation> findActiveDelegationsForAgent(
            @Param("serviceAgentId") Long serviceAgentId,
            @Param("today") LocalDate today,
            @Param("tenantId") Long tenantId
    );
    
    @Query("SELECT d FROM InvestorServiceAgentDelegation d " +
           "WHERE d.investorId = :investorId " +
           "AND d.status = 'PENDING' " +
           "AND d.tenant.id = :tenantId")
    List<InvestorServiceAgentDelegation> findPendingDelegationsForInvestor(
            @Param("investorId") Long investorId,
            @Param("tenantId") Long tenantId
    );

    /**
     * Delegations whose {@code validTo} is before the supplied date AND are still
     * marked {@code ACTIVE}.  Drives the nightly expiry sweep that moves them
     * to status {@code EXPIRED} + {@code isActive = false}.
     */
    @Query("SELECT d FROM InvestorServiceAgentDelegation d " +
           "WHERE d.status = 'ACTIVE' " +
           "AND d.isActive = true " +
           "AND d.validTo IS NOT NULL " +
           "AND d.validTo < :today")
    List<InvestorServiceAgentDelegation> findExpiredActiveDelegations(@Param("today") LocalDate today);

    /**
     * All active delegations currently held by a given Service Agent across all
     * investors — used when an SA is deactivated to cascade-revoke the set.
     */
    @Query("SELECT d FROM InvestorServiceAgentDelegation d " +
           "WHERE d.serviceAgentId = :serviceAgentId " +
           "AND d.isActive = true " +
           "AND d.status IN ('ACTIVE','PENDING')")
    List<InvestorServiceAgentDelegation> findOpenDelegationsByServiceAgent(
            @Param("serviceAgentId") Long serviceAgentId);
}
