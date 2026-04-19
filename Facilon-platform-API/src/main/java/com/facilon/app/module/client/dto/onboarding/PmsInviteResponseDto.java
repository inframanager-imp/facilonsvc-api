package com.facilon.app.module.client.dto.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for the public PMS invite-link entry endpoint.
 *
 * <p>Direct port of Laravel
 * {@code InvestorController::introduce_investor_register_pms_main_step_show}
 * (L3774–L3894): fetches a pre-existing {@code ss_investors} record from Dataverse
 * by its display name ({@code introduce_id} in the URL), creates a local
 * {@code intro_investor_temp} row with the fetched references, and returns a
 * fresh unique code plus the pre-populated data so the wizard can skip
 * broker / PM / plan / bank selection.
 *
 * <p>All lookup-value fields are Dataverse GUIDs — the frontend uses them either
 * directly (for display via the existing master-table joins) or keeps them for
 * the final {@code /register} payload.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsInviteResponseDto {

    /** Fresh local unique code for the subsequent wizard steps (mirrors Laravel's encrypted {@code unique_code}). */
    private String uniqueCode;

    /** The original {@code ss_investors.ss_name} that was in the invite URL (e.g. {@code "INV-1744"}). */
    private String introduceId;

    /** Dataverse {@code ss_investors.ss_investorid} GUID of the pre-existing record. */
    private String dvInvestorGuid;

    // ── Introduced-investor personal details (pre-populated by the broker/PM) ──────────
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String mobile;

    // ── Assignment references (GUIDs — looked up locally via the synced master tables) ──
    private String brokerGuid;
    private String brokerPreferredBankGuid;
    private String productGuid;
    private String brokeragePlanGuid;
    private String investorRouteGuid;
    private String investorTypeGuid;
    private String nationalityGuid;

    /** Dataverse {@code ss_serviceprovidertype} option-set value (100000000=Broker, 100000001=PMS, …). */
    private String serviceProviderType;

    private String ssIpRecords;
    private String ssApplicableToSlt;
}
