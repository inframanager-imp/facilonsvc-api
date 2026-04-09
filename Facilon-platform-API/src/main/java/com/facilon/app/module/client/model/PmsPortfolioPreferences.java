package com.facilon.app.module.client.model;

import com.facilon.app.model.TenantEntity;
import com.facilon.app.module.client.converter.JsonMapConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "investor_pms_portfolio_preferences")
public class PmsPortfolioPreferences extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "investor_id")
    private Long investorId;

    @Convert(converter = JsonMapConverter.class)
    @Column(name = "asset_allocation", columnDefinition = "json")
    private Map<String, Integer> assetAllocation;

    @Column(name = "rebalancing_frequency")
    private String rebalancingFrequency;

    @Column(name = "auto_rebalancing")
    private Boolean autoRebalancing;

    @Column(name = "preferred_sectors")
    private String preferredSectors; // Comma-separated or JSON

    @Column(name = "excluded_sectors")
    private String excludedSectors; // Comma-separated or JSON

    @Column(name = "esg_preference")
    private Boolean esgPreference;

    @Column(name = "dividend_preference")
    private String dividendPreference;

    @Column(name = "max_single_stock_exposure")
    private Integer maxSingleStockExposure;

    @Column(name = "international_exposure")
    private Boolean internationalExposure;

    @Column(name = "liquidity_preference")
    private String liquidityPreference;

    @Column(name = "tax_optimization")
    private Boolean taxOptimization;
}
