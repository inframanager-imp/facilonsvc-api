package com.facilon.app.module.dsr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/** Admin Center dashboard summary cards. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsrDashboardSummaryDto {

    private long total;
    private long open;
    /** Open + past SLA deadline (triage bucket 1). */
    private long overdue;
    /** Open, untouched, created today (triage bucket 2). */
    private long newToday;
    /** Open, untouched, created before today (triage bucket 3). */
    private long notWorked;
    /** Open, on time, being worked (triage bucket 4). overdue + newToday + notWorked + withinTat = open. */
    private long withinTat;
    private long awaitingVerification;
    private long closedThisMonth;
    /** Count keyed by request type code. */
    private Map<String, Long> byRequestType;
    /** Count keyed by internal status. */
    private Map<String, Long> byStatus;
}
