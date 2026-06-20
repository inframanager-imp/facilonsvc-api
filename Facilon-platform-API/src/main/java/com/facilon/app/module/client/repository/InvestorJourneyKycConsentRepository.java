package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.InvestorJourneyKycConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestorJourneyKycConsentRepository
        extends JpaRepository<InvestorJourneyKycConsent, Long> {

    boolean existsByInvestorUniqueIdAndJourneyId(String investorUniqueId, String journeyId);

    Optional<InvestorJourneyKycConsent> findByInvestorUniqueIdAndJourneyId(
            String investorUniqueId, String journeyId);
}
