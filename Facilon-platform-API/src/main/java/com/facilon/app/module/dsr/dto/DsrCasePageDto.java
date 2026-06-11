package com.facilon.app.module.dsr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** A page of admin DSR cases plus pagination metadata for server-side paging. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsrCasePageDto {

    private List<DsrAdminCaseDto> content;
    /** Zero-based page index. */
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
