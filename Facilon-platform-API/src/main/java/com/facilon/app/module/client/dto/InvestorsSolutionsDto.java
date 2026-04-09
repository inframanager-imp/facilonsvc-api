package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorsSolutionsDto {

    private Long myRowId;
    private Integer id;
    private Integer typeId;
    private String title;
    private String imageUpload;
    private String slugUrl;
    private String shortDesc;
    private String longDesc;
    private Integer status;
}
