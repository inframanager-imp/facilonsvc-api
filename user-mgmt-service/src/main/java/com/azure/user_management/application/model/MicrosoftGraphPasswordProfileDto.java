package com.azure.user_management.application.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Slf4j
public class MicrosoftGraphPasswordProfileDto {
    private String password;
    private Boolean forceChangePasswordNextSignIn;

    public void setForceChangePasswordNextSignIn(Boolean forceChangePasswordNextSignIn) {
        this.forceChangePasswordNextSignIn = forceChangePasswordNextSignIn == null ? Boolean.FALSE : forceChangePasswordNextSignIn;
    }
}
