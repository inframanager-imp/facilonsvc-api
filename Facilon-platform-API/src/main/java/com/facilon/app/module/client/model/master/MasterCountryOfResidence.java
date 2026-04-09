package com.facilon.app.module.client.model.master;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Master country of residence list.
 * Global reference data.
 */
@Entity
@Table(name = "master_country_of_residence")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterCountryOfResidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ss_name", columnDefinition = "TEXT")
    private String ssName;

    @Column(name = "ss_countryid", columnDefinition = "TEXT")
    private String ssCountryId;

    @Column(name = "ss_isdcode", columnDefinition = "TEXT")
    private String ssIsdCode;
}
