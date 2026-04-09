package com.azure.user_management.application.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Slf4j
public class UserAuthenticationMobile {
    private String phoneNumber;
    private String phoneType;
}
