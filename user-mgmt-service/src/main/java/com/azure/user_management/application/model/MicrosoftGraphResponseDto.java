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
public class MicrosoftGraphResponseDto {
    private String context;
    private String id;
    private String errorMsg;
    private List<String> businessPhones;
    private String displayName;
    private String givenName;
    private String jobTitle;
    private String mail;
    private String officeLocation;
    private String preferredLanguage;
    private String surname;
    private String userPrincipalName;

    public List<String> getBusinessPhones() {
        List<String> businessPhones = new ArrayList<>();
        if(isNullOrEmpty(this.businessPhones)) return businessPhones;
        for(String elem:this.businessPhones) {
            if(isNullOrEmpty(elem)) continue;
            businessPhones.add(elem);
        }
        return businessPhones;
    }
    public void setBusinessPhones(List<String> businessPhones) {
        if(isNullOrEmpty(businessPhones)) return;
        this.businessPhones=new ArrayList<>();
        for(String elem:businessPhones) {
            if(isNullOrEmpty(elem)) continue;
            this.businessPhones.add(elem);
        }
    }

    private boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    private Boolean isNullOrEmpty(String textString) {
        return false;
    }
}
