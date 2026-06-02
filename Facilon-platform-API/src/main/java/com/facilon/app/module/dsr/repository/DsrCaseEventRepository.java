package com.facilon.app.module.dsr.repository;

import com.facilon.app.module.dsr.model.DsrCaseEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DsrCaseEventRepository extends JpaRepository<DsrCaseEvent, Long> {

    List<DsrCaseEvent> findByDsrCaseIdOrderByCreatedAtAsc(Long dsrCaseId);

    List<DsrCaseEvent> findByDsrCaseIdAndInvestorVisibleTrueOrderByCreatedAtAsc(Long dsrCaseId);
}
