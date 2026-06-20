package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorDashboardDto {
    private InvestorBasicInfo investor;
    private InvestorProgressDto progress;
    private List<ActivityItem> recentActivity;
    private List<String> nextSteps;
    private AccountSummary accountSummary; // Option 2: Enhanced dashboard with account details
    private AccountSnapshot accountSnapshot;
    private ActionsAlerts actionsAlerts;
    private ProductAssignment productAssignment;
    private List<ApplicationItem> applications;
    private List<ConsentItem> consentCenter;
    /**
     * Incomplete Facilon-status steps of the investor's onboarding journey, surfaced as the
     * "My Pending Action" card. Derived from the same 6-stage model as the journey stepper:
     * Information, KYC Docs, Onboarding, Verification, Physical, Account. Empty once onboarding
     * is complete.
     */
    private List<PendingAction> pendingActions;
    private DelegationInfo delegation;
    /**
     * True once the investor has agreed to their Statement of Work (SOW submitted/approved and
     * not revoked). The onboarding journey is gated on this — the journey page won't open until
     * it's true. Surfaced here so the frontend route guard can block entry.
     */
    private Boolean sowAgreed;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvestorBasicInfo {
        private String name;
        private String firstName;
        private String middleName;
        private String lastName;
        private String email;
        private String mobileNumber;
        private String nationality;
        private String countryOfResidence;
        private String investorType;
        private String uniqueCode;
        private Integer status;
        private String registerAs;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityItem {
        private String action;
        private String timestamp;
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountSummary {
        private Boolean accountOpeningStatus;
        private Boolean verificationDone;
        private Boolean physicalSubmissionDone;
        private Integer kycDocumentsUploaded;
        private Integer kycDocumentsRequired;
        private Integer onboardingDocumentsUploaded;
        private Integer onboardingDocumentsRequired;
        private BankInfo bankInfo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BankInfo {
        private String bankName;
        private String accountNumber; // Masked
        private String ifscCode;
        private Boolean hasBankDetails;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountSnapshot {
        private String investorId;
        private String primaryJurisdiction;
        private String eligibility;
        private String lastActivity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActionsAlerts {
        private String actionRequired;
        private String onboardingStatus;
        private String restrictions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductAssignment {
        private Boolean assigned;
        private String source;
        private String serviceProviderName;
        private String serviceProviderType;
        private String productName;
        private String productCode;
        private String planName;
        private String routeName;
        private String message;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplicationItem {
        private String code;
        private String name;
        private String status;
        private Boolean enabled;
        private String blockReason;
        private String actionRoute;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConsentItem {
        private String consent;
        private String scope;
        private String status; // "Active" | "Inactive"
        private Boolean actionRequired;
        private String action; // "ACTIVATE" | "UPDATE" | "REVOKE" | "NONE"
        private String key;    // stable id for the action flow: "sow" | "marketing" | "whatsapp" | "privacy" | "platformTerms"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendingAction {
        private String activity;   // step label, e.g. "KYC Docs"
        private String centre;     // grouping shown in the CENTRA column, e.g. "Compliance"
        private String status;     // "PENDING" (current/in-progress step) | "REQUIRED" (not started)
        private String stepKey;    // stable step id: information|documents|onboarding|verification|physical|account
        private String actionRoute; // frontend route to act on this step
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DelegationInfo {
        private String serviceAgent;
        private String scope;
        private String expiry;
        private String status;
        private String note;
    }
}
