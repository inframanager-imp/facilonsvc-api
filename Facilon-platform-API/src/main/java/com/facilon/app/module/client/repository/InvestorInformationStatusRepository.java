package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.InvestorInformationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestorInformationStatusRepository extends JpaRepository<InvestorInformationStatus, Long> {
    Optional<InvestorInformationStatus> findByInvestorEmail(String investorEmail);
}
