package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterPortfolioManagersDto {

    private Long id;
    private String ssName;
    private String ssPortfolioManagerId;
    private String ssServiceProviderType;
    private String ssNameOfTheFirmValue;

    /**
     * Resolved firm display name from {@code master_accounts.name} via the
     * {@code _ss_nameofthefirm_value → accountid} join.
     *
     * The raw {@link #ssName} column on {@code master_portfolio_managers} is
     * frequently a SEBI registration code (e.g. "FSP202510305335") or NULL,
     * so the UI prefers this resolved firm name when present.
     */
    private String firmName;
}
