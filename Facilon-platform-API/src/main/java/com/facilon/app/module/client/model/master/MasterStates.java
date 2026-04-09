package com.facilon.app.module.client.model.master;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Master states/provinces list.
 * Global reference data.
 */
@Entity
@Table(name = "master_states")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterStates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_row_id")
    private Long myRowId;

    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "ss_name")
    private String ssName;

    @Column(name = "ss_state", columnDefinition = "TEXT")
    private String ssState;

    @Column(name = "ss_stateid", columnDefinition = "TEXT")
    private String ssStateId;

    @Column(name = "ss_country_value", columnDefinition = "TEXT")
    private String ssCountryValue;

    @Column(name = "importsequencenumber")
    private Integer importSequenceNumber;
}
