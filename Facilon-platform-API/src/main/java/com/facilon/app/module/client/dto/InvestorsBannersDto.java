package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorsBannersDto {

    private Long myRowId;
    private Integer id;
    private Integer sectionId;
    private String title;
    private String imageUpload;
    private String backgroundImage;
    private String description;
    private Integer status;
}
