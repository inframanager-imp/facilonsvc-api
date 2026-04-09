package com.facilon.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuperAdminRoleDto {
    private Long id;
    
    @NotBlank(message = "Role label is required")
    @Size(min = 2, max = 50, message = "Role label must be between 2 and 50 characters")
    private String label;
    
    private Long parentListId;
    private int sequenceNo;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long tenantId;
    private String tenantName;
    private List<String> authorities;
}
