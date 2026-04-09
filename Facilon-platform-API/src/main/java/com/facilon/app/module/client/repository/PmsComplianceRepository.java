package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.PmsCompliance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PmsComplianceRepository extends JpaRepository<PmsCompliance, Long> {
    Optional<PmsCompliance> findByInvestorId(Long investorId);
}
