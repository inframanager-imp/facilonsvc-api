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
public class SuperAdminDashboardDto {
    private Long totalTenants;
    private Long activeTenants;
    private Long totalUsers;
    private Long activeUsers;
    private Long totalRoles;
    private Long totalUserGroups;
    private List<SuperAdminTenantDto> recentTenants;
    private List<SuperAdminUserDto> recentUsers;
    private List<SuperAdminRoleDto> availableRoles;
}
