package com.facilon.app.module.dsr.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Per-year counter backing the sequential DSR Case ID format {@code DSR-YYYY-000001}.
 * One row per year; incremented under a pessimistic write lock.
 */
@Entity
@Table(name = "dsr_case_sequence")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsrCaseSequence {

    @Id
    @Column(name = "seq_year")
    private Integer seqYear;

    @Column(name = "last_seq", nullable = false)
    private Long lastValue;
}
