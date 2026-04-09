package com.facilon.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuperAdminAuthorityDto {
    private Long id;
    
    @NotBlank(message = "Authority name is required")
    @Size(min = 2, max = 100, message = "Authority name must be between 2 and 100 characters")
    private String authorityName;
    
    @Size(max = 255, message = "Resource pattern must not exceed 255 characters")
    private String resourcePattern;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long tenantId;
    private String tenantName;
}
