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
public class ApprovalResponseDto {

    private Long id;
    private Long investorId;
    private String requestType;
    private String currentStatus;
    private String requestedStatus;
    private String reason;
    private String status; // "pending", "approved", "rejected"
    private String requestedBy;
    private LocalDateTime requestedAt;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String rejectionReason;
}
