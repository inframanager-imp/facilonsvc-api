package com.facilon.app.module.client.model.content;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Investor type definitions.
 * Global reference data (not tenant-scoped).
 */
@Entity
@Table(name = "investor_type")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_row_id")
    private Long myRowId;

    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "type_name", columnDefinition = "TEXT")
    private String typeName;

    @Column(name = "cat_id")
    private Integer catId;
}
