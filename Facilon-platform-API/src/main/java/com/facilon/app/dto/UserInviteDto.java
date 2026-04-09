package com.facilon.app.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInviteDto {
    private String firstName;
    private String lastName;
    private String email;
    private String mobilePhone;
    private String role;
}
