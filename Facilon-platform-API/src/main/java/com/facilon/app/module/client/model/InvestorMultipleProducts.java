package com.facilon.app.module.client.model;

import com.facilon.app.model.TenantEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Tracks investor subscriptions to multiple products.
 * Used for product journey and status tracking.
 * Tenant-scoped.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "investor_multiple_products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorMultipleProducts extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ss_investorid", columnDefinition = "TEXT")
    private String ssInvestorId;

    // Links to investor.unique_code
    @Column(name = "investor_unique_id", columnDefinition = "TEXT")
    private String investorUniqueId;

    // Journey status: 1=complete, 2=in-progress, 3=abandoned, etc.
    @Column(name = "journeyStatus")
    @Builder.Default
    private Integer journeyStatus = 2;
}
