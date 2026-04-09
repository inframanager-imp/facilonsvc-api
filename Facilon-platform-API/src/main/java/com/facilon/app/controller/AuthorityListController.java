package com.facilon.app.controller;

import com.facilon.app.annotations.CurrentTenant;
import com.facilon.app.dto.*;
import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.service.AdminService;
import com.facilon.app.service.AuthorityListService;
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
@RequestMapping("/api/authorities")
@SecurityRequirement(name = "Bearer Authentication")
public class AuthorityListController {

    @Autowired
    private AuthorityListService authorityListService;

    @GetMapping
    @Operation(
            summary = "Get all authorities",
            description = "Retrieve a list of all authorities in the system",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public List<AuthorityListDto> getAllAuthorities() {
        return authorityListService.getAllAuthorities();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get authority by ID",
            description = "Retrieve the details of an authority by its ID",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public Optional<AuthorityListDto> getAuthorityById(@PathVariable Long id) {
        return authorityListService.getAuthorityById(id);
    }

    @PostMapping
    @Operation(
            summary = "Create a new authority",
            description = "Create a new authority with the given details",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public AuthorityListDto createAuthority(@RequestBody AuthorityListDto authorityDto) {
        return authorityListService.saveAuthority(authorityDto);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete an authority",
            description = "Delete an authority by its ID",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public void deleteAuthority(@PathVariable Long id) {
        authorityListService.deleteAuthority(id);
    }

    @RestController
    @RequestMapping("/api/admin")
    @RequiredArgsConstructor
    @Slf4j
    @CurrentTenant
    @Tag(name = "Admin Management", description = "Admin Management APIs")
    public static class AdminController {

        private final AdminService adminService;


        /**
         * Create Admin User Endpoint
         * Creates admin user for existing tenant
         */
        @PostMapping("/tenants/{tenantId}/admin-user")
        @Operation(summary = "Create admin user", description = "Create admin user for existing tenant")
        public ResponseEntity<AuthorizedUser> createAdminUser(
                @Parameter(description = "Tenant ID") @PathVariable Long tenantId,
                @Valid @RequestBody AdminUserCreationDto adminUserDto) {
            log.info("Admin user creation request for tenant: {}", tenantId);

            try {
                AuthorizedUser adminUser = adminService.createAdminUser(tenantId, adminUserDto);
                return ResponseEntity.status(HttpStatus.CREATED).body(adminUser);
            } catch (Exception e) {
                log.error("Admin user creation failed: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        /**
         * Assign User Group to User Endpoint
         * Assigns user group to existing user (replaces direct role assignment)
         */
        @PostMapping("/users/{userId}/groups/{groupName}")
        @Operation(summary = "Assign user group to user", description = "Assign user group to existing user")
        public ResponseEntity<String> assignUserGroupToUser(
                @Parameter(description = "User ID") @PathVariable Long userId,
                @Parameter(description = "Group Name") @PathVariable String groupName) {
            log.info("User group assignment request for user: {} with group: {}", userId, groupName);

            try {
                adminService.assignUserGroupToUser(userId, groupName);
                return ResponseEntity.ok("User group assigned successfully");
            } catch (Exception e) {
                log.error("User group assignment failed: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User group assignment failed: " + e.getMessage());
            }
        }




        /**
         * User Invitation System
         */
        @PostMapping("/users/invite")
        @Operation(summary = "Invite users", description = "Invite new users to the platform")
        public ResponseEntity<String> inviteUsers(
                @Parameter(description = "Tenant ID") @RequestParam Long tenantId,
                @Valid @RequestBody List<UserInviteDto> userInvites) {
            log.info("User invitation request for tenant: {}", tenantId);

            try {
                adminService.inviteUsers(tenantId, userInvites);
                return ResponseEntity.ok("Users invited successfully");
            } catch (Exception e) {
                log.error("User invitation failed: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invitation failed: " + e.getMessage());
            }
        }




        /**
         * User Management - List Users
         */
        @GetMapping("/users")
        @Operation(summary = "List users", description = "Get all users for tenant")
        public ResponseEntity<Page<TenantUserDto>> getUsers(
                @Parameter(description = "Tenant ID") @RequestParam Long tenantId,
                Pageable pageable) {
            log.info("User list request for tenant: {}", tenantId);

            try {
                Page<TenantUserDto> users = adminService.getUsers(tenantId, pageable);
                return ResponseEntity.ok(users);
            } catch (Exception e) {
                log.error("Failed to get users: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        /**
         * User Management - Reset Password
         */
        @PostMapping("/users/{userId}/reset-password")
        @Operation(summary = "Reset user password", description = "Reset password for user")
        public ResponseEntity<String> resetUserPassword(
                @Parameter(description = "User ID") @PathVariable Long userId) {
            log.info("Password reset request for user: {}", userId);

            try {
                String result = adminService.resetUserPassword(userId);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                log.error("Password reset failed: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password reset failed: " + e.getMessage());
            }
        }

        /**
         * User Management - Deactivate User
         */
        @PostMapping("/users/{userId}/deactivate")
        @Operation(summary = "Deactivate user", description = "Deactivate user account")
        public ResponseEntity<String> deactivateUser(
                @Parameter(description = "User ID") @PathVariable Long userId) {
            log.info("User deactivation request for user: {}", userId);

            try {
                String result = adminService.deactivateUser(userId);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                log.error("User deactivation failed: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deactivation failed: " + e.getMessage());
            }
        }

        /**
         * Health Check Endpoint
         */
        @GetMapping("/health")
        @Operation(summary = "Health check", description = "Check admin service health")
        public ResponseEntity<String> healthCheck() {
            return ResponseEntity.ok("Admin service is healthy");
        }
    }
}
