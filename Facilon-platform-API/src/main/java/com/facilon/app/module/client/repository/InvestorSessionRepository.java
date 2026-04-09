package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.InvestorSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvestorSessionRepository extends JpaRepository<InvestorSession, Long> {

    Optional<InvestorSession> findBySessionId(String sessionId);

    List<InvestorSession> findByInvestorIdAndIsActiveTrue(Long investorId);

    List<InvestorSession> findByInvestorId(Long investorId);

    Optional<InvestorSession> findByJwtTokenHash(String jwtTokenHash);

    List<InvestorSession> findByIsActiveTrueAndLastActivityTimeBefore(LocalDateTime cutoffTime);

    long countByInvestorIdAndIsActiveTrue(Long investorId);
}
