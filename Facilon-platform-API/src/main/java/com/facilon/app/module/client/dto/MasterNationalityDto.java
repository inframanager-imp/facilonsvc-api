package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterNationalityDto {

    private Long myRowId;
    private Integer id;
    private String ssName;
    private String ssNationality;
    private String ssNationalityId;
}
