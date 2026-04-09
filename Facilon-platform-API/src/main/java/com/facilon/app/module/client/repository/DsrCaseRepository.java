package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.DsrCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DsrCaseRepository extends JpaRepository<DsrCase, Long> {

    Optional<DsrCase> findByCaseId(String caseId);

    List<DsrCase> findByInvestorUniqueCodeOrderByCreatedAtDesc(String investorUniqueCode);

    List<DsrCase> findByStatusAndSlaDeadlineBefore(DsrCase.CaseStatus status, LocalDateTime deadline);

    boolean existsByCaseId(String caseId);
}
