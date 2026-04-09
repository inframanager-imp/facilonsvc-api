package com.facilon.app.module.client.model.master;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Master investor types classification.
 * Global reference data.
 */
@Entity
@Table(name = "master_investor_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterInvestorTypes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_row_id")
    private Long myRowId;

    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "ss_name", columnDefinition = "TEXT")
    private String ssName;

    @Column(name = "ss_investortypeid", columnDefinition = "TEXT")
    private String ssInvestorTypeId;

    @Column(name = "ss_applicableto", columnDefinition = "TEXT")
    private String ssApplicableTo;
}
