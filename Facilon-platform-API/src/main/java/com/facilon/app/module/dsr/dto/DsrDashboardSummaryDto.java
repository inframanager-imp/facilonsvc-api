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
    private long overdue;
    private long awaitingVerification;
    private long closedThisMonth;
    /** Count keyed by request type code. */
    private Map<String, Long> byRequestType;
    /** Count keyed by internal status. */
    private Map<String, Long> byStatus;
}
