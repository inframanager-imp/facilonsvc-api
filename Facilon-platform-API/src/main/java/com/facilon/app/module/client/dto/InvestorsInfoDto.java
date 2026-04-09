package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorsInfoDto {

    private Long myRowId;
    private Integer id;
    private String title;
    private String imageUpload;
    private String description;
    private String slugUrl;
    private Integer status;
}
