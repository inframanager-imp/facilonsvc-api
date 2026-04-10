package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterPmsBanksDto {

    private Long id;
    private String ssName;
    private String ssPmsBankId;
    private String ssPortfolioManagerValue;
    private String ssBankValue;

    /**
     * Resolved bank display name from {@code master_accounts.name} via the
     * {@code ss_bank_value → accountid} join. Falls back to {@link #ssName}
     * when no master_accounts row is found.
     */
    private String bankName;
}
