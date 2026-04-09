package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Broker's preferred-bank display info — resolved from
 * intro_investor_temp.broker_prefferedbank via master_broker_banks → master_banks → master_accounts.
 *
 * Mirrors the Laravel logic in information-update.blade.php lines 1653-1674.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreferredBankDto {
    /** true when service_provider_type == "100000000" (Broker). */
    private boolean isBroker;

    /** Bank display name from master_accounts.name, or null if the chain cannot be resolved. */
    private String bankName;
}
