package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorSolutionBannersDto {

    private Long myRowId;
    private Integer id;
    private Integer solutionId;
    private String imageUpload;
    private String description;
    private Integer status;
}
