package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.InvestorPmsDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestorPmsDetailsRepository extends JpaRepository<InvestorPmsDetails, Long> {

    Optional<InvestorPmsDetails> findByInvestorUniqueCode(String investorUniqueCode);
}
