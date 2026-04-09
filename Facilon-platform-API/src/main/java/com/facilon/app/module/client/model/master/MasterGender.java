package com.facilon.app.module.client.model.master;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Master gender list.
 * Global reference data.
 */
@Entity
@Table(name = "master_gender")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterGender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_row_id")
    private Long myRowId;

    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "ss_name", columnDefinition = "TEXT")
    private String ssName;

    @Column(name = "ss_genderid", columnDefinition = "TEXT")
    private String ssGenderId;
}
