package com.facilon.app.module.client.model.master;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Master broker-bank associations.
 * Global reference data.
 */
@Entity
@Table(name = "master_broker_banks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterBrokerBanks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ss_name", columnDefinition = "TEXT")
    private String ssName;

    @Column(name = "ss_broker_value", columnDefinition = "TEXT")
    private String ssBrokerValue;

    @Column(name = "ss_bank_value", columnDefinition = "TEXT")
    private String ssBankValue;

    @Column(name = "ss_brokerbankid", columnDefinition = "TEXT")
    private String ssBrokerBankId;
}
