package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.IntroduceInvestorEmails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Tracks investor introduction/invitation emails for the
 * {@code latest_investor_check} cron (Laravel parity).
 *
 * <p>Dedup is scoped <b>explicitly</b> by tenant id rather than relying on the
 * Hibernate {@code tenantFilter} — that filter is only enabled by
 * {@code MultiTenantAspect} for methods annotated {@code @CurrentTenant}, which
 * does not cover repository calls made from a cron thread. Passing the active
 * tenant id keeps the dedup correct per tenant, mirroring Laravel's
 * single-tenant {@code introduce_investor_emails} table.
 */
@Repository
public interface IntroduceInvestorEmailsRepository extends JpaRepository<IntroduceInvestorEmails, Long> {

    boolean existsByEmailIdAndTenant_TenantId(String emailId, Long tenantId);

    Optional<IntroduceInvestorEmails> findByEmailIdAndTenant_TenantId(String emailId, Long tenantId);
}
