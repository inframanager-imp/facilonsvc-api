package com.facilon.app.module.client.model.master;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Master investment plans.
 * Global reference data.
 */
@Entity
@Table(name = "master_plans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterPlans {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ss_name", columnDefinition = "TEXT")
    private String ssName;

    @Column(name = "_ss_investmentroute_value", columnDefinition = "TEXT")
    private String ssInvestmentRouteValue;

    @Column(name = "ss_plandescription", columnDefinition = "TEXT")
    private String ssPlanDescription;

    @Column(name = "versionnumber", columnDefinition = "TEXT")
    private String versionNumber;

    @Column(name = "ss_planid", columnDefinition = "TEXT")
    private String ssPlanId;

    @Column(name = "_ss_product_value", columnDefinition = "TEXT")
    private String ssProductValue;

    @Column(name = "_ss_broker_value", columnDefinition = "TEXT")
    private String ssBrokerValue;
}
