package com.facilon.app.module.dsr.service;

import com.facilon.app.module.dsr.model.DsrCase.CaseStatus;
import org.springframework.stereotype.Component;

/**
 * Single source of truth for mapping internal {@link CaseStatus} values to the
 * plain-language, investor-facing labels (BRD section 12). Investors never see the
 * raw internal status. Also decides which transitions notify the investor and which
 * are investor-visible on the timeline.
 */
@Component
public class DsrStatusMapper {

    public String toInvestorStatus(CaseStatus status) {
        if (status == null) {
            return "Submitted";
        }
        return switch (status) {
            case NEW -> "Submitted";
            case SUBMITTED -> "Submitted";
            case ACKNOWLEDGED -> "Received";
            case VERIFICATION_PENDING -> "Verification Required";
            case CLARIFICATION_PENDING -> "More Information Required";
            case UNDER_REVIEW, LEGAL_REVIEW -> "Under Review";
            case DATA_SEARCH_IN_PROGRESS -> "Locating Records";
            case ACTION_IN_PROGRESS -> "Processing Request";
            case RESPONSE_SENT -> "Response Available";
            case PARTIALLY_FULFILLED -> "Partially Completed";
            case REJECTED -> "Unable to Fulfil";
            case CLOSED -> "Closed";
            case REOPENED -> "Reopened";
        };
    }

    /** Investor-visible timeline title for an event landing on this status. */
    public String timelineTitle(CaseStatus status) {
        if (status == null) {
            return "Submitted";
        }
        return switch (status) {
            case NEW -> "Submitted";
            case SUBMITTED -> "Submitted";
            case ACKNOWLEDGED -> "Received by Facilon";
            case VERIFICATION_PENDING -> "Verification Required";
            case CLARIFICATION_PENDING -> "More Information Required";
            case UNDER_REVIEW, LEGAL_REVIEW -> "Under Review";
            case DATA_SEARCH_IN_PROGRESS -> "Locating Records";
            case ACTION_IN_PROGRESS -> "Processing Request";
            case RESPONSE_SENT -> "Response Available";
            case PARTIALLY_FULFILLED -> "Partially Completed";
            case REJECTED -> "Unable to Fulfil";
            case CLOSED -> "Closed";
            case REOPENED -> "Reopened";
        };
    }

    /** Whether the investor must act (drives the "Action Required" banner). */
    public boolean isActionRequired(CaseStatus status) {
        return status == CaseStatus.VERIFICATION_PENDING
                || status == CaseStatus.CLARIFICATION_PENDING;
    }

    /** Whether a transition to this status is shown on the investor timeline. */
    public boolean isInvestorVisible(CaseStatus status) {
        if (status == null) {
            return false;
        }
        return switch (status) {
            // Internal-only working states are hidden from the investor.
            case DATA_SEARCH_IN_PROGRESS, LEGAL_REVIEW, ACTION_IN_PROGRESS -> false;
            default -> true;
        };
    }

    /** Whether a transition to this status sends an email to the investor. */
    public boolean shouldNotifyInvestor(CaseStatus status) {
        if (status == null) {
            return false;
        }
        return switch (status) {
            case ACKNOWLEDGED,
                 VERIFICATION_PENDING,
                 CLARIFICATION_PENDING,
                 RESPONSE_SENT,
                 PARTIALLY_FULFILLED,
                 REJECTED,
                 CLOSED -> true;
            default -> false;
        };
    }
}
