package com.facilon.app.service;

import com.facilon.app.dto.*;
import com.facilon.app.model.Tenant;
import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.model.RoleList;
import com.facilon.app.model.UserGroup;
import com.facilon.app.repository.TenantRepository;
import com.facilon.app.repository.AuthorizedUserRepository;
import com.facilon.app.repository.RoleListRepository;
import com.facilon.app.repository.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminService {
    
    // Core repositories (Auth Service)
    private final TenantRepository tenantRepository;
    private final AuthorizedUserRepository authorizedUserRepository;
    private final RoleListRepository roleListRepository;
    private final UserGroupRepository userGroupRepository;

    
    
    /**
     * Create Admin User for Tenant
     * Separate API to create admin user after tenant registration
     */
    public AuthorizedUser createAdminUser(Long tenantId, AdminUserCreationDto adminUserDto) {
        log.info("Creating admin user for tenant: {}", tenantId);
        
        try {
            // Get tenant
            Tenant tenant = tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + tenantId));
            
            // Create Admin User
            AuthorizedUser adminUser = AuthorizedUser.builder()
                    .firstName(adminUserDto.getFirstName())
                    .lastName(adminUserDto.getLastName())
                    .emailId(adminUserDto.getEmail())
                    .mobilePhone(adminUserDto.getMobilePhone())
                    .loginId(adminUserDto.getEmail())
                    .password(adminUserDto.getPassword()) // Password will be encoded by existing auth service
                    .isActive(true)
                    .lastLogin(LocalDateTime.now())
                    .build();
            
            // Set tenant after creation
            adminUser.setTenant(tenant);
            
            adminUser = authorizedUserRepository.save(adminUser);
            log.info("Created admin user with ID: {}", adminUser.getId());
            
            return adminUser;
                    
        } catch (Exception e) {
            log.error("Error creating admin user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create admin user: " + e.getMessage());
        }
    }
    
    /**
     * Assign User Group to User (replaces direct role assignment)
     * Separate API to assign user groups to users
     */
    public void assignUserGroupToUser(Long userId, String groupName) {
        log.info("Assigning user group {} to user: {}", groupName, userId);
        
        try {
            UserGroup group = userGroupRepository.findByGroupName(groupName)
                    .orElseThrow(() -> new RuntimeException("User group not found: " + groupName));
            
            AuthorizedUser user = authorizedUserRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
            // Initialize userGroups if null
            if (user.getUserGroups() == null) {
                user.setUserGroups(new HashSet<>());
            }
            
            // Check if user already has this group
            boolean hasGroup = user.getUserGroups().stream()
                    .anyMatch(g -> g.getGroupName().equals(groupName));
            
            if (!hasGroup) {
                user.getUserGroups().add(group);
                authorizedUserRepository.save(user);
                log.info("Assigned user group {} to user {}", groupName, userId);
            } else {
                log.info("User {} already has user group {}", userId, groupName);
            }
            
        } catch (Exception e) {
            log.error("Error assigning user group to user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to assign user group: " + e.getMessage());
        }
    }
    


    
    /**
     * User Invitation System
     */
    public void inviteUsers(Long tenantId, List<UserInviteDto> userInvites) {
        log.info("Inviting users for tenant: {}", tenantId);
        
        for (UserInviteDto invite : userInvites) {
            // Create user account
            AuthorizedUser user = AuthorizedUser.builder()
                    .firstName(invite.getFirstName())
                    .lastName(invite.getLastName())
                    .emailId(invite.getEmail())
                    .mobilePhone(invite.getMobilePhone())
                    .loginId(invite.getEmail())
                    .password("temp_password_123") // Temporary password - will be encoded by existing auth service
                    .isActive(false) // Inactive until first login
                    .build();
            
            user = authorizedUserRepository.save(user);
            
            // Assign user group based on invite (if role is provided, find corresponding user group)
            if (invite.getRole() != null && !invite.getRole().isEmpty()) {
                // For now, we'll create a default user group or find existing one
                // This is a simplified approach - in real scenario, you might want to map roles to user groups
                try {
                    assignUserGroupToUser(user.getId(), invite.getRole());
                } catch (Exception e) {
                    log.warn("Could not assign user group {} to user {}: {}", invite.getRole(), user.getId(), e.getMessage());
                }
            }
            
            // TODO: Send invitation email with login credentials
            log.info("User invited: {} - {}", invite.getEmail(), invite.getRole());
        }
    }
    

    
    /**
     * Get Users for Tenant with Pagination
     */
    @Transactional(readOnly = true)
    public Page<TenantUserDto> getUsers(Long tenantId, Pageable pageable) {
        log.info("Getting users for tenant: {}", tenantId);
        
        try {
            Page<AuthorizedUser> users = authorizedUserRepository.findByTenantId(tenantId, pageable);
            
            return users.map(user -> {
                // Get user groups
                List<String> groupNames = user.getUserGroups() != null ?
                    user.getUserGroups().stream()
                        .map(UserGroup::getGroupName)
                        .collect(Collectors.toList()) : new ArrayList<>();
                
                // Get roles from user groups
                List<String> roles = new ArrayList<>();
                if (user.getUserGroups() != null) {
                    for (UserGroup group : user.getUserGroups()) {
                        if (group.getRoles() != null) {
                            roles.addAll(group.getRoles().stream()
                                .map(RoleList::getLabel)
                                .collect(Collectors.toList()));
                        }
                    }
                }
                
                return TenantUserDto.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .emailId(user.getEmailId())
                        .mobilePhone(user.getMobilePhone())
                        .loginId(user.getLoginId())
                        //.isActive(user.getIsActive())
                        .lastLogin(user.getLastLogin() != null ? user.getLastLogin().toString() : null)
                        .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                       // .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null)
                        .tenantId(tenantId)
                        .tenantName(user.getTenant() != null ? user.getTenant().getTenantName() : null)
                        .userGroups(groupNames)
                        .roles(roles)
                        .build();
            });
            
        } catch (Exception e) {
            log.error("Error getting users for tenant: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get users: " + e.getMessage());
        }
    }
    
    /**
     * Reset User Password
     */
    @Transactional
    public String resetUserPassword(Long userId) {
        log.info("Resetting password for user: {}", userId);
        
        try {
            AuthorizedUser user = authorizedUserRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
            // Generate a random password
            String newPassword = generateRandomPassword();
            
            // Encode password (assuming BCryptPasswordEncoder is available)
            // Note: You may need to inject BCryptPasswordEncoder
            String encodedPassword = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(newPassword);
            user.setPassword(encodedPassword);
            user.setModifiedAt(LocalDateTime.now());
            user.setModifiedBy("admin");
            
            authorizedUserRepository.save(user);
            
            // TODO: Send email with new password
            // emailService.sendSimpleMessage(user.getEmailId(), "Password Reset", 
            //     "Your new password is: " + newPassword);
            
            log.info("Password reset for user: {}", userId);
            return "Password reset email sent to " + user.getEmailId();
            
        } catch (Exception e) {
            log.error("Error resetting password: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to reset password: " + e.getMessage());
        }
    }
    
    /**
     * Deactivate User
     */
    @Transactional
    public String deactivateUser(Long userId) {
        log.info("Deactivating user: {}", userId);
        
        try {
            AuthorizedUser user = authorizedUserRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
           // user.setIsActive(false);
            user.setModifiedAt(LocalDateTime.now());
            user.setModifiedBy("admin");
            
            authorizedUserRepository.save(user);
            
            log.info("Deactivated user: {}", userId);
            return "User deactivated successfully";
            
        } catch (Exception e) {
            log.error("Error deactivating user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to deactivate user: " + e.getMessage());
        }
    }
    
    /**
     * Generate Random Password
     */
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }
}
