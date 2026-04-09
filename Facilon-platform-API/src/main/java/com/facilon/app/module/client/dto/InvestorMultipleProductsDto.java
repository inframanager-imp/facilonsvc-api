package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorMultipleProductsDto {

    private Long id;
    private String ssInvestorId;
    private String investorUniqueId;
    private Integer journeyStatus;
}
