package com.facilon.app.module.dsr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Investor-facing case detail = the case + its investor-visible timeline. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsrCaseDetailDto {

    private DsrCaseResponseDto request;
    private List<DsrCaseEventDto> timeline;
}
