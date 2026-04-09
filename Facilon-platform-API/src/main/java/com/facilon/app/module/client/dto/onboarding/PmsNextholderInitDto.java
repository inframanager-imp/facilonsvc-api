package com.facilon.app.module.client.dto.onboarding;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Step 1 of PMS investor nextholder flow (docs/Investor).
 * Self vs Legal Entity + PMS context (manager, plan, bank).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsNextholderInitDto {

    /** 1=Self, 2=Legal Entity */
    private Integer registerAs;
    private String legalEntityFullName;
    private Long countryOfIncorporation;

    @NotNull
    private Long pmsManagerId;
    @NotNull
    private Long pmsPlanId;
    @NotNull
    private Long pmsBankId;
}
