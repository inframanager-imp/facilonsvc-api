package com.facilon.app.module.client.model.master;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Master portfolio managers (PMS) list.
 * Global reference data.
 */
@Entity
@Table(name = "master_portfolio_managers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterPortfolioManagers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ss_name")
    private String ssName;

    @Column(name = "ss_portfoliomanagerid")
    private String ssPortfolioManagerId;

    @Column(name = "ss_serviceprovidertype")
    private String ssServiceProviderType;

    @Column(name = "_ss_nameofthefirm_value")
    private String ssNameOfTheFirmValue;
}
