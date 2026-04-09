package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic DTO for master/reference lookups (marital status, title, visa type, banks, brokers, etc.).
 * Use id and name/display fields for dropdowns and APIs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterLookupDto {

    private Long myRowId;
    private Long id;
    private String name;
    private String code;
    private String externalId;
}
