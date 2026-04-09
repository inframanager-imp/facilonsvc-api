package com.facilon.app.controller;

import com.facilon.app.dto.*;
import com.facilon.app.service.SuperAdminService;
import com.facilon.app.service.RoleListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
@SecurityRequirement(name = "Bearer Authentication")
public class RoleListController {

    @Autowired
    private RoleListService roleListService;

    @GetMapping
    @Operation(summary = "Get all roles")
    public List<RoleListDto> getAllRoles() {
        return roleListService.getAllRoles();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID")
    public Optional<RoleListDto> getRoleById(@PathVariable Long id) {
        return roleListService.getRoleById(id);
    }

    @PostMapping

    @Operation(summary = "Create a new role")
    public RoleListDto createRole(@RequestBody RoleListDto roleDto) {
        return roleListService.saveRole(roleDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a role")
    public void deleteRole(@PathVariable Long id) {
        roleListService.deleteRole(id);
    }

    @RestController
    @RequestMapping("/api/super-admin")
    @RequiredArgsConstructor
    @Slf4j
    @Tag(name = "Super Admin Management", description = "Super Admin APIs for managing tenants, users, and roles")
    public static class SuperAdminController {

        private final SuperAdminService superAdminService;

        /**
         * Super Admin Dashboard Overview
         */
        @GetMapping("/dashboard")
        @Operation(summary = "Get super admin dashboard", description = "Get overview statistics and recent activities")
        public ResponseEntity<SuperAdminDashboardDto> getDashboard() {
            log.info("Super admin dashboard request");

            try {
                SuperAdminDashboardDto dashboard = superAdminService.getDashboardOverview();
                return ResponseEntity.ok(dashboard);
            } catch (Exception e) {
                log.error("Failed to get super admin dashboard: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        /**
         * Tenant Management Endpoints
         */
        @GetMapping("/tenants")
        @Operation(summary = "Get all tenants", description = "Get paginated list of all tenants")
        public ResponseEntity<Page<SuperAdminTenantDto>> getAllTenants(Pageable pageable) {
            log.info("Getting all tenants");

            try {
                Page<SuperAdminTenantDto> tenants = superAdminService.getAllTenants(pageable);
                return ResponseEntity.ok(tenants);
            } catch (Exception e) {
                log.error("Failed to get tenants: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        @GetMapping("/tenants/{tenantId}")
        @Operation(summary = "Get tenant by ID", description = "Get specific tenant details")
        public ResponseEntity<SuperAdminTenantDto> getTenantById(
                @Parameter(description = "Tenant ID") @PathVariable Long tenantId) {
            log.info("Getting tenant by ID: {}", tenantId);

            try {
                SuperAdminTenantDto tenant = superAdminService.getTenantById(tenantId);
                return ResponseEntity.ok(tenant);
            } catch (Exception e) {
                log.error("Failed to get tenant: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }

        @PostMapping("/tenants")
        @Operation(summary = "Create tenant", description = "Create new tenant")
        public ResponseEntity<SuperAdminTenantDto> createTenant(
                @Valid @RequestBody SuperAdminTenantDto tenantDto) {
            log.info("Creating tenant: {}", tenantDto.getTenantName());

            try {
                SuperAdminTenantDto createdTenant = superAdminService.createTenant(tenantDto);
                return ResponseEntity.status(HttpStatus.CREATED).body(createdTenant);
            } catch (Exception e) {
                log.error("Failed to create tenant: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        @PutMapping("/tenants/{tenantId}")
        @Operation(summary = "Update tenant", description = "Update existing tenant")
        public ResponseEntity<SuperAdminTenantDto> updateTenant(
                @Parameter(description = "Tenant ID") @PathVariable Long tenantId,
                @Valid @RequestBody SuperAdminTenantDto tenantDto) {
            log.info("Updating tenant: {}", tenantId);

            try {
                SuperAdminTenantDto updatedTenant = superAdminService.updateTenant(tenantId, tenantDto);
                return ResponseEntity.ok(updatedTenant);
            } catch (Exception e) {
                log.error("Failed to update tenant: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        @DeleteMapping("/tenants/{tenantId}")
        @Operation(summary = "Delete tenant", description = "Soft delete tenant")
        public ResponseEntity<String> deleteTenant(
                @Parameter(description = "Tenant ID") @PathVariable Long tenantId) {
            log.info("Deactivating tenant: {}", tenantId);

            try {
                String result = superAdminService.deleteTenant(tenantId);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                log.error("Failed to deactivate tenant: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to deactivate tenant: " + e.getMessage());
            }
        }

        /**
         * User Management Endpoints
         */
        @GetMapping("/users")
        @Operation(summary = "Get all users", description = "Get paginated list of all users")
        public ResponseEntity<Page<SuperAdminUserDto>> getAllUsers(Pageable pageable) {
            log.info("Getting all users");

            try {
                Page<SuperAdminUserDto> users = superAdminService.getAllUsers(pageable);
                return ResponseEntity.ok(users);
            } catch (Exception e) {
                log.error("Failed to get users: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        @GetMapping("/users/{userId}")
        @Operation(summary = "Get user by ID", description = "Get specific user details")
        public ResponseEntity<SuperAdminUserDto> getUserById(
                @Parameter(description = "User ID") @PathVariable Long userId) {
            log.info("Getting user by ID: {}", userId);

            try {
                SuperAdminUserDto user = superAdminService.getUserById(userId);
                return ResponseEntity.ok(user);
            } catch (Exception e) {
                log.error("Failed to get user: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }

        @PostMapping("/users")
        @Operation(summary = "Create user", description = "Create new user")
        public ResponseEntity<SuperAdminUserDto> createUser(
                @Valid @RequestBody SuperAdminUserDto userDto) {
            log.info("Creating user: {}", userDto.getEmailId());

            try {
                SuperAdminUserDto createdUser = superAdminService.createUser(userDto);
                return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
            } catch (Exception e) {
                log.error("Failed to create user: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        @PutMapping("/users/{userId}")
        @Operation(summary = "Update user", description = "Update existing user")
        public ResponseEntity<SuperAdminUserDto> updateUser(
                @Parameter(description = "User ID") @PathVariable Long userId,
                @Valid @RequestBody SuperAdminUserDto userDto) {
            log.info("Updating user: {}", userId);

            try {
                SuperAdminUserDto updatedUser = superAdminService.updateUser(userId, userDto);
                return ResponseEntity.ok(updatedUser);
            } catch (Exception e) {
                log.error("Failed to update user: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        @DeleteMapping("/users/{userId}")
        @Operation(summary = "Delete user", description = "Delete user")
        public ResponseEntity<String> deleteUser(
                @Parameter(description = "User ID") @PathVariable Long userId) {
            log.info("Deleting user: {}", userId);

            try {
                String result = superAdminService.deleteUser(userId);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                log.error("Failed to delete user: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete user: " + e.getMessage());
            }
        }

        /**
         * Role Management Endpoints
         */
        @GetMapping("/roles")
        @Operation(summary = "Get all roles", description = "Get list of all roles")
        public ResponseEntity<List<SuperAdminRoleDto>> getAllRoles() {
            log.info("Getting all roles");

            try {
                List<SuperAdminRoleDto> roles = superAdminService.getAllRoles();
                return ResponseEntity.ok(roles);
            } catch (Exception e) {
                log.error("Failed to get roles: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        @GetMapping("/roles/{roleId}")
        @Operation(summary = "Get role by ID", description = "Get specific role details")
        public ResponseEntity<SuperAdminRoleDto> getRoleById(
                @Parameter(description = "Role ID") @PathVariable Long roleId) {
            log.info("Getting role by ID: {}", roleId);

            try {
                SuperAdminRoleDto role = superAdminService.getRoleById(roleId);
                return ResponseEntity.ok(role);
            } catch (Exception e) {
                log.error("Failed to get role: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }

        @PostMapping("/roles")
        @Operation(summary = "Create role", description = "Create new role")
        public ResponseEntity<SuperAdminRoleDto> createRole(
                @Valid @RequestBody SuperAdminRoleDto roleDto) {
            log.info("Creating role: {}", roleDto.getLabel());

            try {
                SuperAdminRoleDto createdRole = superAdminService.createRole(roleDto);
                return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
            } catch (Exception e) {
                log.error("Failed to create role: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        @PutMapping("/roles/{roleId}")
        @Operation(summary = "Update role", description = "Update existing role")
        public ResponseEntity<SuperAdminRoleDto> updateRole(
                @Parameter(description = "Role ID") @PathVariable Long roleId,
                @Valid @RequestBody SuperAdminRoleDto roleDto) {
            log.info("Updating role: {}", roleId);

            try {
                SuperAdminRoleDto updatedRole = superAdminService.updateRole(roleId, roleDto);
                return ResponseEntity.ok(updatedRole);
            } catch (Exception e) {
                log.error("Failed to update role: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        @DeleteMapping("/roles/{roleId}")
        @Operation(summary = "Delete role", description = "Delete role")
        public ResponseEntity<String> deleteRole(
                @Parameter(description = "Role ID") @PathVariable Long roleId) {
            log.info("Deleting role: {}", roleId);

            try {
                String result = superAdminService.deleteRole(roleId);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                log.error("Failed to delete role: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete role: " + e.getMessage());
            }
        }

        /**
         * User Group Assignment Endpoints (replaces direct role assignment)
         */
        @GetMapping("/users/{userId}/groups")
        @Operation(summary = "Get user group assignment", description = "Get user's current and available user groups")
        public ResponseEntity<UserRoleAssignmentDto> getUserGroupAssignment(
                @Parameter(description = "User ID") @PathVariable Long userId) {
            log.info("Getting user group assignment for user: {}", userId);

            try {
                UserRoleAssignmentDto assignment = superAdminService.getUserRoleAssignment(userId);
                return ResponseEntity.ok(assignment);
            } catch (Exception e) {
                log.error("Failed to get user group assignment: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }

        @PostMapping("/users/{userId}/groups")
        @Operation(summary = "Assign user groups to user", description = "Assign user groups to user")
        public ResponseEntity<String> assignUserGroupsToUser(
                @Parameter(description = "User ID") @PathVariable Long userId,
                @RequestBody List<String> groupNames) {
            log.info("Assigning user groups to user: {}", userId);

            try {
                superAdminService.assignUserGroupsToUser(userId, groupNames);
                return ResponseEntity.ok("User groups assigned successfully");
            } catch (Exception e) {
                log.error("Failed to assign user groups: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to assign user groups: " + e.getMessage());
            }
        }

        /**
         * User Group Management Endpoints
         */
        @GetMapping("/groups/all")
        @Operation(summary = "Get all user groups", description = "Get list of all user groups")
        public ResponseEntity<List<SuperAdminUserGroupDto>> getAllUserGroups() {
            log.info("Getting all user groups");

            try {
                List<SuperAdminUserGroupDto> groups = superAdminService.getAllUserGroups();
                return ResponseEntity.ok(groups);
            } catch (Exception e) {
                log.error("Failed to get user groups: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        @GetMapping("/groups/{groupId}")
        @Operation(summary = "Get user group by ID", description = "Get specific user group details")
        public ResponseEntity<SuperAdminUserGroupDto> getUserGroupById(
                @Parameter(description = "Group ID") @PathVariable Long groupId) {
            log.info("Getting user group by ID: {}", groupId);

            try {
                SuperAdminUserGroupDto group = superAdminService.getUserGroupById(groupId);
                return ResponseEntity.ok(group);
            } catch (Exception e) {
                log.error("Failed to get user group: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }

        @PostMapping("/groups/create")
        @Operation(summary = "Create user group", description = "Create new user group")
        public ResponseEntity<SuperAdminUserGroupDto> createUserGroup(
                @Valid @RequestBody SuperAdminUserGroupDto groupDto) {
            log.info("Creating user group: {}", groupDto.getGroupName());

            try {
                SuperAdminUserGroupDto createdGroup = superAdminService.createUserGroup(groupDto);
                return ResponseEntity.status(HttpStatus.CREATED).body(createdGroup);
            } catch (Exception e) {
                log.error("Failed to create user group: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        @PutMapping("/groups/{groupId}")
        @Operation(summary = "Update user group", description = "Update existing user group")
        public ResponseEntity<SuperAdminUserGroupDto> updateUserGroup(
                @Parameter(description = "Group ID") @PathVariable Long groupId,
                @Valid @RequestBody SuperAdminUserGroupDto groupDto) {
            log.info("Updating user group: {}", groupId);

            try {
                SuperAdminUserGroupDto updatedGroup = superAdminService.updateUserGroup(groupId, groupDto);
                return ResponseEntity.ok(updatedGroup);
            } catch (Exception e) {
                log.error("Failed to update user group: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        @DeleteMapping("/groups/{groupId}")
        @Operation(summary = "Delete user group", description = "Delete user group")
        public ResponseEntity<String> deleteUserGroup(
                @Parameter(description = "Group ID") @PathVariable Long groupId) {
            log.info("Deleting user group: {}", groupId);

            try {
                String result = superAdminService.deleteUserGroup(groupId);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                log.error("Failed to delete user group: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user group: " + e.getMessage());
            }
        }

        @PostMapping("/groups/{groupId}/assign-roles")
        @Operation(summary = "Assign roles to group", description = "Assign roles to user group")
        public ResponseEntity<SuperAdminUserGroupDto> assignRolesToGroup(
                @Parameter(description = "Group ID") @PathVariable Long groupId,
                @RequestBody List<Long> roleIds) {
            log.info("Assigning roles to group: {}", groupId);

            try {
                SuperAdminUserGroupDto updatedGroup = superAdminService.assignRolesToGroup(groupId, roleIds);
                return ResponseEntity.ok(updatedGroup);
            } catch (Exception e) {
                log.error("Failed to assign roles to group: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        @PostMapping("/groups/{groupId}/add-user/{userId}")
        @Operation(summary = "Add user to group", description = "Add user to user group")
        public ResponseEntity<String> addUserToGroup(
                @Parameter(description = "Group ID") @PathVariable Long groupId,
                @Parameter(description = "User ID") @PathVariable Long userId) {
            log.info("Adding user {} to group {}", userId, groupId);

            try {
                String result = superAdminService.addUserToGroup(groupId, userId);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                log.error("Failed to add user to group: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to add user to group: " + e.getMessage());
            }
        }

        @PostMapping("/groups/{groupId}/remove-user/{userId}")
        @Operation(summary = "Remove user from group", description = "Remove user from user group")
        public ResponseEntity<String> removeUserFromGroup(
                @Parameter(description = "Group ID") @PathVariable Long groupId,
                @Parameter(description = "User ID") @PathVariable Long userId) {
            log.info("Removing user {} from group {}", userId, groupId);

            try {
                String result = superAdminService.removeUserFromGroup(groupId, userId);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                log.error("Failed to remove user from group: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to remove user from group: " + e.getMessage());
            }
        }

        @GetMapping("/groups/search")
        @Operation(summary = "Search user groups", description = "Search user groups by name or role")
        public ResponseEntity<List<SuperAdminUserGroupDto>> searchUserGroups(
                @Parameter(description = "Group name") @RequestParam(required = false) String groupName,
                @Parameter(description = "Role") @RequestParam(required = false) String role) {
            log.info("Searching user groups with name: {}, role: {}", groupName, role);

            try {
                // For now, return all groups - can be enhanced later with proper search logic
                List<SuperAdminUserGroupDto> groups = superAdminService.getAllUserGroups();
                return ResponseEntity.ok(groups);
            } catch (Exception e) {
                log.error("Failed to search user groups: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        /**
         * Authority Management Endpoints
         */
        @GetMapping("/authorities")
        @Operation(summary = "Get all authorities", description = "Get list of all authorities")
        public ResponseEntity<List<SuperAdminAuthorityDto>> getAllAuthorities() {
            log.info("Getting all authorities");

            try {
                List<SuperAdminAuthorityDto> authorities = superAdminService.getAllAuthorities();
                return ResponseEntity.ok(authorities);
            } catch (Exception e) {
                log.error("Failed to get authorities: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        /**
         * Health Check Endpoint
         */
        @GetMapping("/health")
        @Operation(summary = "Health check", description = "Check super admin service health")
        public ResponseEntity<String> healthCheck() {
            return ResponseEntity.ok("Super Admin service is healthy");
        }

    }
}
