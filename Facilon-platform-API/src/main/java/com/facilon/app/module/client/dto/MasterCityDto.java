package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterCityDto {

    private Long myRowId;
    private Integer id;
    private String ssName;
    private String ssCity;
    private String ssCityId;
    private String ssCountryValue;
    private String ssStateValue;
}
