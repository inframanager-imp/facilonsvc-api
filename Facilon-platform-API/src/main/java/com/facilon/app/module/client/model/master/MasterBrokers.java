package com.facilon.app.module.client.model.master;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Master brokers list.
 * Global reference data.
 */
@Entity
@Table(name = "master_brokers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterBrokers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ss_name", columnDefinition = "TEXT")
    private String ssName;

    @Column(name = "ss_brokerid", columnDefinition = "TEXT")
    private String ssBrokerId;

    @Column(name = "ss_serviceprovidertype", columnDefinition = "TEXT")
    private String ssServiceProviderType;

    @Column(name = "_ss_nameofthefirm_value", columnDefinition = "TEXT")
    private String ssNameOfTheFirmValue;
}
