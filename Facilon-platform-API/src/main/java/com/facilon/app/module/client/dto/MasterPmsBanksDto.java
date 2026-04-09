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
}
