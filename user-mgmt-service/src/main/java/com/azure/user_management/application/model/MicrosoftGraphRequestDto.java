package com.azure.user_management.application.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Slf4j
public class MicrosoftGraphRequestDto {
    private Boolean accountEnabled = Boolean.TRUE;
    private String displayName;
    private String mail;
    private String mobilePhone;
    private String country;
    private MicrosoftGraphPasswordProfileDto passwordProfile;
    private String passwordPolicies;
    private String mailNickname;
    private List<MicrosoftIdentitiesDto> identities;

    public List<MicrosoftIdentitiesDto> getIdentities() {
        List<MicrosoftIdentitiesDto> identities = new ArrayList<>();
        if (isNullOrEmpty(this.identities))
            return identities;
        for (MicrosoftIdentitiesDto elem : this.identities) {
            if (elem == null)
                continue;
            try {
                identities.add(elem.clone());
            } catch (CloneNotSupportedException e) {
                continue;
            }
        }
        return identities;
    }

    private boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public void setIdentities(List<MicrosoftIdentitiesDto> identities) {
        if (isNullOrEmpty(identities))
            return;
        this.identities = new ArrayList<>();
        for (MicrosoftIdentitiesDto elem : identities) {
            if (elem == null)
                continue;
            try {
                this.identities.add(elem.clone());
            } catch (CloneNotSupportedException e) {
                continue;
            }
        }
    }
}
