package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.InvestorRiskProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestorRiskProfileRepository extends JpaRepository<InvestorRiskProfile, Long> {

    Optional<InvestorRiskProfile> findByInvestorUniqueId(String investorUniqueId);
}
