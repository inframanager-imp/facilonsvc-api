package com.facilon.app.module.client.model.master;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * ISD code values for international dialing.
 * Global reference data.
 */
@Entity
@Table(name = "isd_code_values")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IsdCodeValues {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_row_id")
    private Long myRowId;

    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "code_value")
    private Integer codeValue;

    @Column(name = "country_name")
    private String countryName;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "active_date")
    private LocalDate activeDate;

    @Column(name = "inactive_date")
    private LocalDate inactiveDate;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private Integer status = 1;
}
