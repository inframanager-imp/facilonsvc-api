package com.facilon.app.module.client.model.master;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Master PMS (Portfolio Management Service) plans.
 * Global reference data.
 */
@Entity
@Table(name = "master_pms_plans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterPmsPlans {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ss_name")
    private String ssName;

    @Column(name = "_ss_investmentroute_value")
    private String ssInvestmentRouteValue;

    @Column(name = "ss_plandescription", columnDefinition = "TEXT")
    private String ssPlanDescription;

    @Column(name = "versionnumber")
    private String versionNumber;

    @Column(name = "ss_planid")
    private String ssPlanId;

    @Column(name = "_ss_product_value")
    private String ssProductValue;

    @Column(name = "_ss_pms_value")
    private String ssPmsValue;

    @Column(name = "_ss_preferredbank_value")
    private String ssPreferredBankValue;

    @Column(name = "_ss_scheme_value", columnDefinition = "TEXT")
    private String ssSchemeValue;
}
