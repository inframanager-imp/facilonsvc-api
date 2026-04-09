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
        if(forceChangePasswordNextSignIn==null) this.forceChangePasswordNextSignIn=Boolean.FALSE;
        this.forceChangePasswordNextSignIn = forceChangePasswordNextSignIn;
    }
}
