package com.facilon.app.module.dsr.service;

import com.facilon.app.module.dsr.model.DsrCaseSequence;
import com.facilon.app.module.dsr.repository.DsrCaseSequenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Generates sequential DSR Case IDs in the form {@code DSR-YYYY-000001}.
 *
 * <p>Backed by a per-year counter row that is incremented under a pessimistic write lock,
 * inside its own {@code REQUIRES_NEW} transaction so the reservation commits independently
 * of the surrounding case-creation transaction.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DsrCaseIdGenerator {

    private final DsrCaseSequenceRepository sequenceRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String nextCaseId() {
        int year = LocalDate.now().getYear();

        DsrCaseSequence seq = sequenceRepository.findForUpdate(year).orElse(null);
        if (seq == null) {
            // First case of the year. saveAndFlush surfaces a race as a constraint
            // violation, which the caller-level retry (unique case_id) would catch;
            // in practice the pessimistic lock serialises subsequent callers.
            seq = DsrCaseSequence.builder().seqYear(year).lastValue(0L).build();
        }
        long next = seq.getLastValue() + 1;
        seq.setLastValue(next);
        sequenceRepository.saveAndFlush(seq);

        return String.format("DSR-%d-%06d", year, next);
    }
}
