package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterProductsDto {

    private Long id;
    private String ssName;
    private String ssMarketsOfInterestValue;
    private String ssServicedBy;
    private String ssApplicableTo;
    private String ssProductDescription;
    private String ssRepatriationBenefits;
    private String ssInvestmentRouteValue;
    private String ssProductId;
}
