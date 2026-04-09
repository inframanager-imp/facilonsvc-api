package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.InvestorConsents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestorConsentsRepository extends JpaRepository<InvestorConsents, Long> {

    Optional<InvestorConsents> findByInvestorUniqueId(String investorUniqueId);
}
