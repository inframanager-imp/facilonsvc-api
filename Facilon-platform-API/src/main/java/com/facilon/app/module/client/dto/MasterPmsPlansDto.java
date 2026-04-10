package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterPmsPlansDto {

    private Long id;
    private String ssName;
    private String ssInvestmentRouteValue;
    private String ssPlanDescription;
    private String versionNumber;
    private String ssPlanId;
    private String ssProductValue;
    private String ssPmsValue;
    private String ssPreferredBankValue;
    private String ssSchemeValue;

    /**
     * Resolved bank display name from {@code master_accounts.name} via the
     * {@code _ss_preferredbank_value → accountid} join. Used to populate the
     * "PMS Bank" field in the wizard when {@code master_pms_banks} has no rows
     * synced from Dataverse yet.
     */
    private String preferredBankName;
}
