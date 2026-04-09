package com.facilon.app.module.client.model.master;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Master investment schemes.
 * Global reference data.
 */
@Entity
@Table(name = "master_schemes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterSchemes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ss_name", columnDefinition = "TEXT")
    private String ssName;

    @Column(name = "ss_broker_value", columnDefinition = "TEXT")
    private String ssBrokerValue;

    @Column(name = "_ss_portfoliomanager_value", columnDefinition = "TEXT")
    private String ssPortfolioManagerValue;

    @Column(name = "_ss_investortype_value", columnDefinition = "TEXT")
    private String ssInvestorTypeValue;

    @Column(name = "_ss_bank_value", columnDefinition = "TEXT")
    private String ssBankValue;

    @Column(name = "ss_applicableto", columnDefinition = "TEXT")
    private String ssApplicableTo;

    @Column(name = "ss_schemesid", columnDefinition = "TEXT")
    private String ssSchemesId;

    @Column(name = "_ss_custodian_value", columnDefinition = "TEXT")
    private String ssCustodianValue;

    @Column(name = "ss_schemedescription", columnDefinition = "TEXT")
    private String ssSchemeDescription;

    @Column(name = "_ss_schemeid_value", columnDefinition = "TEXT")
    private String ssSchemeIdValue;

    @Column(name = "ss_servicedby", columnDefinition = "TEXT")
    private String ssServicedBy;

    @Column(name = "_ss_product_value", columnDefinition = "TEXT")
    private String ssProductValue;
}
