package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.InvestorResidentialStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestorResidentialStatusRepository extends JpaRepository<InvestorResidentialStatus, Long> {

    Optional<InvestorResidentialStatus> findByInvestorUniqueId(String investorUniqueId);
}
