package com.azure.user_management.application.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Slf4j
public class PasswordChangeDto {
    private String userId;
    private String newPassword;
    private String clientId;
    private String clientSecret;
    private String tenantId;
    private String newMobile;
}
