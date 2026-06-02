package com.facilon.app.module.dsr.repository;

import com.facilon.app.module.dsr.model.DsrCaseSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DsrCaseSequenceRepository extends JpaRepository<DsrCaseSequence, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from DsrCaseSequence s where s.seqYear = :year")
    Optional<DsrCaseSequence> findForUpdate(@Param("year") Integer year);
}
