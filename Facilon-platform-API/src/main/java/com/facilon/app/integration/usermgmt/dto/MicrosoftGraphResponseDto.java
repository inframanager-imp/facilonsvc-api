package com.facilon.app.integration.usermgmt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MicrosoftGraphResponseDto {
    private String id;
    private String errorMsg;
    private String displayName;
    private String givenName;
    private String surname;
    private String mail;
    private String userPrincipalName;
    private List<String> businessPhones;
}
