package com.facilon.app.module.client.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSowDto {
    @NotNull
    private Long investorId;

    @NotNull
    private Long templateId;

    private Map<String, Object> sowData;
}
