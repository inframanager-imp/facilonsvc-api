package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.IntroducedRegistrationSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IntroducedRegistrationSessionRepository extends JpaRepository<IntroducedRegistrationSession, Long> {
    Optional<IntroducedRegistrationSession> findByUniqueCode(String uniqueCode);
    Optional<IntroducedRegistrationSession> findByDataverseInvestorId(String dataverseInvestorId);
    Optional<IntroducedRegistrationSession> findByEmail(String email);
}
