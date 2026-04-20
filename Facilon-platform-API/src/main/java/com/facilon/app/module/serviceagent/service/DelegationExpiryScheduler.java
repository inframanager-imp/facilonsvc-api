package com.facilon.app.module.serviceagent.service;

import com.facilon.app.module.serviceagent.model.InvestorServiceAgentDelegation;
import com.facilon.app.module.serviceagent.repository.DelegationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Nightly sweep that moves delegations past their {@code validTo} date from
 * {@code ACTIVE} to {@code EXPIRED}.
 *
 * <p>The request-time check in
 * {@link ServiceAgentAccessControlService#checkValidityPeriod(InvestorServiceAgentDelegation)}
 * already prevents expired agents from acting; this scheduler keeps the
 * {@code status} column coherent so admin / reporting views show a correct
 * count of truly active delegations without joining on validity dates.
 *
 * <h3>Configuration (application-*.yml)</h3>
 * <pre>
 * serviceagent:
 *   expiry:
 *     cron: "0 15 1 * * *"    # 01:15 every day (default)
 *     enabled: true
 * </pre>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DelegationExpiryScheduler {

    private final DelegationRepository delegationRepository;

    @Value("${serviceagent.expiry.enabled:true}")
    private boolean enabled;

    @Scheduled(cron = "${serviceagent.expiry.cron:0 15 1 * * *}")
    @Transactional
    public void sweepExpiredDelegations() {
        if (!enabled) {
            log.debug("[DelegationExpiry] Sweep skipped (enabled=false)");
            return;
        }
        LocalDate today = LocalDate.now();
        List<InvestorServiceAgentDelegation> expired = delegationRepository
                .findExpiredActiveDelegations(today);
        if (expired.isEmpty()) {
            log.info("[DelegationExpiry] No delegations to expire");
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        for (InvestorServiceAgentDelegation d : expired) {
            d.setStatus("EXPIRED");
            d.setIsActive(false);
            d.setRevokedAt(now);
            d.setRevokedBy("system:expiry-scheduler");
            if (d.getRevocationReason() == null) {
                d.setRevocationReason("Delegation validTo reached");
            }
        }
        delegationRepository.saveAll(expired);
        log.info("[DelegationExpiry] Expired {} delegations (validTo before {})",
                expired.size(), today);
    }
}
