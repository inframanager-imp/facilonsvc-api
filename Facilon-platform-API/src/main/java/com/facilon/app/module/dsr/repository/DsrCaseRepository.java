package com.facilon.app.module.dsr.repository;

import com.facilon.app.module.dsr.model.DsrCase;
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

    /** Admin queue - tenant-scoped automatically via the Hibernate tenant filter. */
    List<DsrCase> findAllByOrderByCreatedAtDesc();

    boolean existsByCaseId(String caseId);
}
