package com.facilon.app.module.dsr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** A registered DSR Admin (Privacy Ops) user as shown in the admin console. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsrAdminUserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String emailId;
    private String mobilePhone;
    private String loginId;
    private boolean active;
    private List<String> roles;
    private String lastLogin;
}
