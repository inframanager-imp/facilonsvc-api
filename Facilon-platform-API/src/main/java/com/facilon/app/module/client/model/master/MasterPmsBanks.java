package com.facilon.app.module.client.model.master;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Master PMS-bank associations.
 * Global reference data.
 */
@Entity
@Table(name = "master_pms_banks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterPmsBanks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ss_name")
    private String ssName;

    @Column(name = "ss_pmsbankid")
    private String ssPmsBankId;

    @Column(name = "ss_portfoliomanager_value")
    private String ssPortfolioManagerValue;

    @Column(name = "ss_bank_value")
    private String ssBankValue;
}
