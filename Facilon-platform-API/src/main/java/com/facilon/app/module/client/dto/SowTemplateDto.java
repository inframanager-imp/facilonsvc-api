package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SowTemplateDto {
    private Long id;
    private String name;
    private String version;
    private String applicableFor;
    private Map<String, Object> content; // JSON structure
    private Boolean isActive;
}
