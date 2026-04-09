package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterBrokersDto {

    private Long id;
    private String ssName;
    private String ssBrokerId;
    private String ssServiceProviderType;
    private String ssNameOfTheFirmValue;
}
