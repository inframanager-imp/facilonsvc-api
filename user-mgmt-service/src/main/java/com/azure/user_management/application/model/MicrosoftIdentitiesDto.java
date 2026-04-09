package com.azure.user_management.application.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Slf4j
public class MicrosoftIdentitiesDto implements Cloneable {
    private String signInType="emailAddress";
    private String issuer;
    private String issuerAssignedId;

    @Override
    protected MicrosoftIdentitiesDto clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return (MicrosoftIdentitiesDto) super.clone();
    }
}
