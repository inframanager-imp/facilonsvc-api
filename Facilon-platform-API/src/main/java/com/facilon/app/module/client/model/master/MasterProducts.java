package com.facilon.app.module.client.model.master;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Master investment products.
 * Global reference data.
 */
@Entity
@Table(name = "master_products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterProducts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ss_name", columnDefinition = "TEXT")
    private String ssName;

    @Column(name = "_ss_marketsofinterest_value", columnDefinition = "TEXT")
    private String ssMarketsOfInterestValue;

    @Column(name = "ss_servicedby", columnDefinition = "TEXT")
    private String ssServicedBy;

    @Column(name = "ss_applicableto", columnDefinition = "TEXT")
    private String ssApplicableTo;

    @Column(name = "ss_productdescription", columnDefinition = "TEXT")
    private String ssProductDescription;

    @Column(name = "ss_repatriationbenefits", columnDefinition = "TEXT")
    private String ssRepatriationBenefits;

    @Column(name = "_ss_investmentroute_value", columnDefinition = "TEXT")
    private String ssInvestmentRouteValue;

    @Column(name = "ss_productid", columnDefinition = "TEXT")
    private String ssProductId;
}
