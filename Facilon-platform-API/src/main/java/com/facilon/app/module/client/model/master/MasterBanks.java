package com.facilon.app.module.client.model.master;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Master banks list.
 * Global reference data.
 */
@Entity
@Table(name = "master_banks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterBanks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ss_name", columnDefinition = "TEXT")
    private String ssName;

    @Column(name = "ss_bankid", columnDefinition = "TEXT")
    private String ssBankId;

    @Column(name = "ss_nameofbank", columnDefinition = "TEXT")
    private String ssNameOfBank;

    @Column(name = "ss_serviceprovidertype", columnDefinition = "TEXT")
    private String ssServiceProviderType;
}
