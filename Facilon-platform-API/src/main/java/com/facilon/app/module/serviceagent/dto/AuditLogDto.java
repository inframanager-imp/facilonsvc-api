package com.facilon.app.module.serviceagent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDto {
    private Long id;
    private Long serviceAgentId;
    private String serviceAgentName;
    private Long investorId;
    private String investorName;
    private Long delegationId;
    private String actionType;
    private String actionCategory;
    private String entityType;
    private String entityId;
    private String endpoint;
    private String httpMethod;
    private Boolean success;
    private String errorMessage;
    private String ipAddress;
    private String userAgent;
    private String requestPayload;
    private Integer responseStatus;
    private String fieldChanged;
    private String oldValue;
    private String newValue;
    private String sessionId;
    private LocalDateTime createdAt;
}
