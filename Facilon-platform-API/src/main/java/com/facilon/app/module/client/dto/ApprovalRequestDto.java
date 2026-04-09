package com.facilon.app.module.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRequestDto {

    @NotNull(message = "Investor ID is required")
    private Long investorId;

    @NotBlank(message = "Request type is required")
    private String requestType; // "status_change", "document_approval", "profile_update"

    private String currentStatus;

    private String requestedStatus;

    private String reason;

    private String requestedBy;
}
