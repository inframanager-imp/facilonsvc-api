package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IsdCodeValuesDto {

    private Long myRowId;
    private Integer id;
    private Integer codeValue;
    private String countryName;
    private String countryCode;
    private String nationality;
    private Integer status;
}
