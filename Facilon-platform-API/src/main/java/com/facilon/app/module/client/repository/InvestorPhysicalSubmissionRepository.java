package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.InvestorPhysicalSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestorPhysicalSubmissionRepository extends JpaRepository<InvestorPhysicalSubmission, Long> {

    Optional<InvestorPhysicalSubmission> findByInvestorUniqueId(String investorUniqueId);
}
