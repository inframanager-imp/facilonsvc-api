package com.facilon.app.module.client.dto.onboarding;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Step 1 of introduced investor nextholder flow (docs/Investor).
 * Register as Self or Legal Entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntroNextholderInitDto {

    private String introduceId;
    @NotNull(message = "Register as (Self/Legal) is required")
    private Integer registerAs; // 1=Self, 2=Legal Entity
    private String legalEntityFullName;
    private Long countryOfIncorporation;
    private String legalEntityWebsite;
}
