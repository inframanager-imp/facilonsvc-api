package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterPlansDto {

    private Long id;
    private String ssName;
    private String ssInvestmentRouteValue;
    private String ssPlanDescription;
    private String versionNumber;
    private String ssPlanId;
    private String ssProductValue;
    private String ssBrokerValue;
}
