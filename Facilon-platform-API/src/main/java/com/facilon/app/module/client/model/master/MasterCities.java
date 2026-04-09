package com.facilon.app.module.client.model.master;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Master cities list.
 * Global reference data.
 */
@Entity
@Table(name = "master_cities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterCities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_row_id")
    private Long myRowId;

    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "ss_name", columnDefinition = "TEXT")
    private String ssName;

    @Column(name = "ss_cityid", columnDefinition = "TEXT")
    private String ssCityId;

    @Column(name = "importsequencenumber", columnDefinition = "TEXT")
    private String importSequenceNumber;

    @Column(name = "ss_city", columnDefinition = "TEXT")
    private String ssCity;

    @Column(name = "_ss_country_value", columnDefinition = "TEXT")
    private String ssCountryValue;

    @Column(name = "_ss_state_value", columnDefinition = "TEXT")
    private String ssStateValue;
}
