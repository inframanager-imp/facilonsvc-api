package com.facilon.app.integration.usermgmt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String displayName;
    private String mobilePhone;
    private String clientId;
    private String clientSecret;
    private String tenantId;
    private String issuer; // e.g., "facilonservices.onmicrosoft.com"
    private String country;
}
