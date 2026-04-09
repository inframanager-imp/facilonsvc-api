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
public class DocumentVerificationDto {

    /**
     * Status: Approved, Rejected
     */
    @NotBlank
    private String status;

    /**
     * Rejection reason (required when status=Rejected)
     */
    private String reason;
}
