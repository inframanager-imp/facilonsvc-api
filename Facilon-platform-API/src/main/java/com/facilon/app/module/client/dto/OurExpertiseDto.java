package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OurExpertiseDto {

    private Long myRowId;
    private Integer id;
    private String title;
    private String description;
    private String imageUpload;
    private Integer status;
}
