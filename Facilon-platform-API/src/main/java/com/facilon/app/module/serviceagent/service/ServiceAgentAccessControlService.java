package com.facilon.app.module.serviceagent.service;

import com.facilon.app.module.serviceagent.exception.DelegationExpiredException;
import com.facilon.app.module.serviceagent.exception.ScopeViolationException;
import com.facilon.app.module.serviceagent.model.InvestorServiceAgentDelegation;
import com.facilon.app.module.serviceagent.repository.InvestorServiceAgentDelegationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Set;

/**
 * Validates whether a Service Agent is permitted to perform a specific action on an investor.
 * Three-layer check: delegation active → validity period → scope → permission flag.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceAgentAccessControlService {

    private final InvestorServiceAgentDelegationRepository delegationRepository;

    private static final Set<String> CKYC_CATEGORIES     = Set.of("CKYC", "KYC", "PROFILE", "TAX", "DOCUMENT");
    private static final Set<String> ONBOARDING_CATEGORIES = Set.of("ONBOARDING", "BANK", "NOMINATION", "RISK", "PHYSICAL", "DOCUMENT");

    /**
     * Full validation: delegation active, validity period, scope, and specific permission.
     *
     * @param serviceAgentId   ID of the service agent
     * @param investorId       ID of the investor
     * @param actionCategory   Category of the action (e.g. "KYC", "ONBOARDING", "DOCUMENT")
     * @param requiredPermission Permission flag to check (e.g. "can_edit_kyc")
     */
    public void validateAction(Long serviceAgentId, Long investorId,
                               String actionCategory, String requiredPermission) {
        InvestorServiceAgentDelegation delegation = delegationRepository
                .findByServiceAgentIdAndInvestorIdAndIsActiveTrue(serviceAgentId, investorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "No active delegation found for investor " + investorId));

        checkValidityPeriod(delegation);
        validateScope(delegation.getScope(), actionCategory);
        checkPermission(delegation, requiredPermission);
    }

    /**
     * Light check: just confirms an active, in-period delegation exists. Used for read-only listing.
     */
    public InvestorServiceAgentDelegation getValidDelegation(Long serviceAgentId, Long investorId) {
        InvestorServiceAgentDelegation delegation = delegationRepository
                .findByServiceAgentIdAndInvestorIdAndIsActiveTrue(serviceAgentId, investorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "No active delegation found for investor " + investorId));
        checkValidityPeriod(delegation);
        return delegation;
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void checkValidityPeriod(InvestorServiceAgentDelegation delegation) {
        LocalDate today = LocalDate.now();
        if (delegation.getValidFrom() != null && today.isBefore(delegation.getValidFrom())) {
            throw new DelegationExpiredException("Delegation is not yet valid (starts " + delegation.getValidFrom() + ")");
        }
        if (delegation.getValidTo() != null && today.isAfter(delegation.getValidTo())) {
            throw new DelegationExpiredException("Delegation has expired (ended " + delegation.getValidTo() + ")");
        }
    }

    private void validateScope(String scope, String actionCategory) {
        if (scope == null) return;

        switch (scope) {
            case "VIEW_ONLY":
                // VIEW_ONLY allows reading in all categories; write operations are blocked by permission flags
                return;
            case "CKYC_ONLY":
                if (ONBOARDING_CATEGORIES.contains(actionCategory) && !CKYC_CATEGORIES.contains(actionCategory)) {
                    throw new ScopeViolationException(
                            "Scope CKYC_ONLY does not permit action in category: " + actionCategory);
                }
                break;
            case "ONBOARDING_ONLY":
                if (CKYC_CATEGORIES.contains(actionCategory) && !ONBOARDING_CATEGORIES.contains(actionCategory)) {
                    throw new ScopeViolationException(
                            "Scope ONBOARDING_ONLY does not permit action in category: " + actionCategory);
                }
                break;
            case "FULL_ONBOARDING":
                // No scope restriction
                break;
            default:
                log.warn("Unknown scope '{}' — denying by default", scope);
                throw new ScopeViolationException("Unknown delegation scope: " + scope);
        }
    }

    private void checkPermission(InvestorServiceAgentDelegation delegation, String requiredPermission) {
        boolean permitted = switch (requiredPermission) {
            case "can_view_profile"     -> Boolean.TRUE.equals(delegation.getCanViewProfile());
            case "can_edit_kyc"         -> Boolean.TRUE.equals(delegation.getCanEditKyc());
            case "can_upload_documents" -> Boolean.TRUE.equals(delegation.getCanUploadDocuments());
            case "can_submit_forms"     -> Boolean.TRUE.equals(delegation.getCanSubmitForms());
            default -> false;
        };
        if (!permitted) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Delegation does not grant permission: " + requiredPermission);
        }
    }
}
