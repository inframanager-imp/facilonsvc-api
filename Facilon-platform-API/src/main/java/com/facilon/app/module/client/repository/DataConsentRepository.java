package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.DataConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataConsentRepository extends JpaRepository<DataConsent, Long> {

    List<DataConsent> findByInvestorUniqueId(String investorUniqueId);

    Optional<DataConsent> findByInvestorUniqueIdAndConsentType(String investorUniqueId, String consentType);

    List<DataConsent> findByInvestorUniqueIdAndConsentGivenTrue(String investorUniqueId);
}
