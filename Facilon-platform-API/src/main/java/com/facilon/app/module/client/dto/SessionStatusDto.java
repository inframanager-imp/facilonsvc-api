package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionStatusDto {
    private String sessionId;
    private String loginTime;
    private String lastActivityTime;
    private String loginMethod;
    private Long expiresInSeconds;
    private Boolean isActive;
}
