package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.PmsRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PmsRegistrationRepository extends JpaRepository<PmsRegistration, Long> {
    Optional<PmsRegistration> findByInvestorId(Long investorId);
}
