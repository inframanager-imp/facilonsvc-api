package com.facilon.app.module.client.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequestDto {
    
    @NotBlank(message = "Requested fields are required")
    private String requestedFields;
    
    @NotBlank(message = "Reason is required")
    private String reason;
}
