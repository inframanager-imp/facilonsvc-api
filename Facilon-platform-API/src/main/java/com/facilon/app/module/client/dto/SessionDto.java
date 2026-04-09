package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionDto {
    private String sessionId;
    private LocalDateTime loginTime;
    private LocalDateTime lastActivityTime;
    private String loginMethod;
    private String ipAddress;
    private String userAgent;
    private Boolean isCurrent;
    private Long expiresInSeconds;
}
