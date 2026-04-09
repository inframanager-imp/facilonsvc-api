package com.azure.user_management.application.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Slf4j
public class SignUpDto {
    private String userGroupUrlName;
    private String email;
    private Long userId;
    private String password;
    private Boolean isVerify;
    private String firstName;
    private String lastName;
    private int firmID;
    private String clientId;
    private String clientSecret;
    private String tenantId;
    private String clientUrlName;
    private String subscriptionUrlName;
    private String subscriptionId;
    private Long userGroupId;
    private String mobilePhone;
    private String country;
}
