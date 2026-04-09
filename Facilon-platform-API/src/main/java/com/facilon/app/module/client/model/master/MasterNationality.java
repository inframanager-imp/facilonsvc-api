package com.facilon.app.module.client.model.master;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Master nationality list.
 * Global reference data.
 */
@Entity
@Table(name = "master_nationality")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterNationality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_row_id")
    private Long myRowId;

    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "ss_name")
    private String ssName;

    @Column(name = "ss_nationality", columnDefinition = "TEXT")
    private String ssNationality;

    @Column(name = "ss_nationalityid")
    private String ssNationalityId;
}
