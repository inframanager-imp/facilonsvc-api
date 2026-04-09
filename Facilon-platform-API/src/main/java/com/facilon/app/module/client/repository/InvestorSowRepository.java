package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.InvestorSow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestorSowRepository extends JpaRepository<InvestorSow, Long> {

    List<InvestorSow> findByInvestorId(Long investorId);

    List<InvestorSow> findByInvestorIdOrderByCreatedDateDesc(Long investorId);

    List<InvestorSow> findByStatus(String status);

    Optional<InvestorSow> findByInvestorIdAndStatus(Long investorId, String status);

    long countByInvestorIdAndStatus(Long investorId, String status);
}
