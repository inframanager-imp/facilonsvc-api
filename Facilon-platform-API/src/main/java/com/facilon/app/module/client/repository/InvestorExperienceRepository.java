package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.InvestorExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestorExperienceRepository extends JpaRepository<InvestorExperience, Long> {

    Optional<InvestorExperience> findByInvestorUniqueId(String investorUniqueId);
}
