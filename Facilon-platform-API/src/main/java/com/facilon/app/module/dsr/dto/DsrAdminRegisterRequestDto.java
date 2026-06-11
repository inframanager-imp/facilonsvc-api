package com.facilon.app.module.dsr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Registration payload for a new DSR Admin user. The DSR_ADMIN role is assigned
 * automatically - it is not part of the payload.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsrAdminRegisterRequestDto {

    private String firstName;
    private String lastName;
    private String emailId;
    private String mobilePhone;
    private String loginId;
    /** Initial password; the user must change it on first login. */
    private String password;
    private Boolean active;
}
