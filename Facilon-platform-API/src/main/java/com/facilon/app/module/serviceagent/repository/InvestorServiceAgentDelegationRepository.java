package com.facilon.app.module.serviceagent.repository;

import com.facilon.app.module.serviceagent.model.InvestorServiceAgentDelegation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestorServiceAgentDelegationRepository extends JpaRepository<InvestorServiceAgentDelegation, Long> {

    /** Active delegation for a specific (agent, investor) pair. */
    Optional<InvestorServiceAgentDelegation> findByServiceAgentIdAndInvestorIdAndIsActiveTrue(
            Long serviceAgentId, Long investorId);

    /** All active delegations for a service agent (used to build "My Investors" list). */
    List<InvestorServiceAgentDelegation> findByServiceAgentIdAndIsActiveTrue(Long serviceAgentId);

    /** All delegations (active + inactive) granted to a particular investor. */
    List<InvestorServiceAgentDelegation> findByInvestorId(Long investorId);

    /** Active delegations granted by a particular investor. */
    List<InvestorServiceAgentDelegation> findByInvestorIdAndIsActiveTrue(Long investorId);

    /** Check if an active delegation already exists between this investor and agent. */
    boolean existsByInvestorIdAndServiceAgentIdAndIsActiveTrue(Long investorId, Long serviceAgentId);

    /**
     * Active delegation for an investor where the linked ServiceAgent's authorized user matches.
     * Uses scalar IDs on delegation + subquery on ServiceAgent (no investor/serviceAgent relations on delegation entity).
     */
    @Query("SELECT d FROM InvestorServiceAgentDelegation d " +
           "WHERE d.investorId = :investorId " +
           "AND d.serviceAgentId IN (SELECT sa.id FROM ServiceAgent sa WHERE sa.authorizedUser.id = :agentUserId) " +
           "AND d.isActive = true")
    Optional<InvestorServiceAgentDelegation> findActiveByInvestorIdAndAgentUserId(
            @Param("investorId") Long investorId,
            @Param("agentUserId") Long agentUserId);
}
