package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycDocumentFieldDto {
    private String fieldName;
    private String fieldValue;
    private BigDecimal confidence;
    private Integer page;
}
