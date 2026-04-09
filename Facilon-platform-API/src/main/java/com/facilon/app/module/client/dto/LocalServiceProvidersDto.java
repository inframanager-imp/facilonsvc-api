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
public class LocalServiceProvidersDto {

    private Long id;
    private String contactId;
    private String inviteUrl;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
