package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsPortfolioPreferencesDto {

    private Long id;

    private Map<String, Integer> assetAllocation; // e.g., {"equity": 60, "debt": 30, "gold": 10}

    private String rebalancingFrequency; // "monthly", "quarterly", "annually", "as_needed"

    private Boolean autoRebalancing;

    private String preferredSectors; // Comma-separated list

    private String excludedSectors; // Comma-separated list

    private Boolean esgPreference; // Environmental, Social, Governance investing

    private String dividendPreference; // "reinvest", "payout"

    private Integer maxSingleStockExposure; // Percentage

    private Boolean internationalExposure;

    private String liquidityPreference; // "high", "medium", "low"

    private String taxOptimization; // "enabled", "disabled"
}
