package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateLogsDto {

    private Long id;
    private String investorUniqueId;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}
