package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterSchemesDto {

    private Long id;
    private String ssName;
    private String ssBrokerValue;
    private String ssPortfolioManagerValue;
    private String ssInvestorTypeValue;
    private String ssBankValue;
    private String ssApplicableTo;
    private String ssSchemesId;
    private String ssCustodianValue;
    private String ssSchemeDescription;
    private String ssSchemeIdValue;
    private String ssServicedBy;
    private String ssProductValue;
}
