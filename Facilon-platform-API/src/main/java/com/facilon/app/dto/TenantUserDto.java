package com.facilon.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantUserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String emailId;
    private String mobilePhone;
    private String loginId;
    private Boolean isActive;
    private String lastLogin;
    private String createdAt;
    private String updatedAt;
    private Long tenantId;
    private String tenantName;
    private List<String> userGroups;
    private List<String> roles;
}
