package com.facilon.app.module.client.model.master;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Master visa types list.
 * Global reference data.
 */
@Entity
@Table(name = "master_type_of_visa")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterTypeOfVisa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_row_id")
    private Long myRowId;

    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "ss_name", columnDefinition = "TEXT", nullable = false)
    private String ssName;

    @Column(name = "statuscode", columnDefinition = "TEXT", nullable = false)
    private String statusCode;

    @Column(name = "ss_visatypeid", columnDefinition = "TEXT", nullable = false)
    private String ssVisaTypeId;
}
