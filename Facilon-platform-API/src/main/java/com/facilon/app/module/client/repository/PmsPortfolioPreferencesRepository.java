package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.PmsPortfolioPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PmsPortfolioPreferencesRepository extends JpaRepository<PmsPortfolioPreferences, Long> {
    Optional<PmsPortfolioPreferences> findByInvestorId(Long investorId);
}
