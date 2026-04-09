package com.facilon.app.integration.usermgmt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeDto {
    private String userId;
    private String newPassword;
    private String clientId;
    private String clientSecret;
    private String tenantId;
    private String newMobile;
}
