package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycDocumentDiscrepancyDto {
    private Long id;
    private String canonicalSource;
    private String fieldName;
    private String expectedValue;
    private String observedValue;
    private String severity;
}
