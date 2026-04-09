package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterBanksDto {

    private Long id;
    private String ssName;
    private String ssBankId;
    private String ssNameOfBank;
    private String ssServiceProviderType;
}
