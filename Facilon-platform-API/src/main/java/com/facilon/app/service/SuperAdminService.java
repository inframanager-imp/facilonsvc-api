package com.facilon.app.service;

import com.facilon.app.dto.*;
import com.facilon.app.model.*;
import com.facilon.app.repository.TenantRepository;
import com.facilon.app.repository.AuthorizedUserRepository;
import com.facilon.app.repository.RoleListRepository;
import com.facilon.app.repository.UserGroupRepository;
import com.facilon.app.repository.AuthorityListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SuperAdminService {
    
    private final TenantRepository tenantRepository;
    private final AuthorizedUserRepository authorizedUserRepository;
    private final RoleListRepository roleListRepository;
    private final UserGroupRepository userGroupRepository;
    private final AuthorityListRepository authorityListRepository;
    
    /**
     * Get Super Admin Dashboard Overview
     */
    @Transactional(readOnly = true)
    public SuperAdminDashboardDto getDashboardOverview() {
        log.info("Getting super admin dashboard overview");
        
        try {
            Long totalTenants = tenantRepository.countByIsActiveTrue();
            Long activeTenants = tenantRepository.countByIsActiveTrue();
            Long totalUsers = authorizedUserRepository.countByIsActiveTrue();
            Long activeUsers = authorizedUserRepository.countByIsActiveTrue();
            Long totalRoles = roleListRepository.countByIsActiveTrue();
            Long totalUserGroups = userGroupRepository.countByIsActiveTrue();
            
            List<SuperAdminTenantDto> recentTenants = getRecentTenants();
            List<SuperAdminUserDto> recentUsers = getRecentUsers();
            List<SuperAdminRoleDto> availableRoles = getAllRoles();
            
            return SuperAdminDashboardDto.builder()
                    .totalTenants(totalTenants)
                    .activeTenants(activeTenants)
                    .totalUsers(totalUsers)
                    .activeUsers(activeUsers)
                    .totalRoles(totalRoles)
                    .totalUserGroups(totalUserGroups)
                    .recentTenants(recentTenants)
                    .recentUsers(recentUsers)
                    .availableRoles(availableRoles)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error getting super admin dashboard: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get dashboard overview: " + e.getMessage());
        }
    }
    
    /**
     * Tenant Management
     */
    @Transactional(readOnly = true)
    public Page<SuperAdminTenantDto> getAllTenants(Pageable pageable) {
        log.info("Getting all active tenants with pagination");
        
        Page<Tenant> tenants = tenantRepository.findByIsActiveTrue(pageable);
        return tenants.map(this::convertToTenantDto);
    }
    
    @Transactional(readOnly = true)
    public SuperAdminTenantDto getTenantById(Long tenantId) {
        log.info("Getting tenant by ID: {}", tenantId);
        
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + tenantId));
        
        return convertToTenantDto(tenant);
    }
    
    public SuperAdminTenantDto createTenant(SuperAdminTenantDto tenantDto) {
        log.info("Creating new tenant: {}", tenantDto.getTenantName());
        
        try {
            Tenant tenant = Tenant.builder()
                    .tenantName(tenantDto.getTenantName())
                    .emailDomain(tenantDto.getEmailDomain())
                    .address(tenantDto.getAddress())
                    .state(tenantDto.getState())
                    .postalCode(tenantDto.getPostalCode())
                    .country(tenantDto.getCountry())
                    .isActive(tenantDto.getActive() != null ? tenantDto.getActive() : true)
                    .build();
            
            tenant = tenantRepository.save(tenant);
            log.info("Created tenant with ID: {}", tenant.getTenantId());
            
            return convertToTenantDto(tenant);
            
        } catch (Exception e) {
            log.error("Error creating tenant: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create tenant: " + e.getMessage());
        }
    }
    
    public SuperAdminTenantDto updateTenant(Long tenantId, SuperAdminTenantDto tenantDto) {
        log.info("Updating tenant: {}", tenantId);
        
        try {
            Tenant tenant = tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + tenantId));
            
            tenant.setTenantName(tenantDto.getTenantName());
            tenant.setEmailDomain(tenantDto.getEmailDomain());
            tenant.setAddress(tenantDto.getAddress());
            tenant.setState(tenantDto.getState());
            tenant.setPostalCode(tenantDto.getPostalCode());
            tenant.setCountry(tenantDto.getCountry());
            tenant.setIsActive(tenantDto.getActive());
            
            tenant = tenantRepository.save(tenant);
            log.info("Updated tenant with ID: {}", tenant.getTenantId());
            
            return convertToTenantDto(tenant);
            
        } catch (Exception e) {
            log.error("Error updating tenant: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update tenant: " + e.getMessage());
        }
    }
    
    public String deleteTenant(Long tenantId) {
        log.info("Soft deleting tenant: {}", tenantId);
        
        try {
            Tenant tenant = tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + tenantId));
            
            // Soft delete: mark as inactive instead of removing
            tenant.setIsActive(false);
            tenant.setModifiedAt(LocalDateTime.now());
            tenant.setModifiedBy("system");
            
            tenantRepository.save(tenant);
            log.info("Soft deleted tenant with ID: {}", tenantId);
            
            return "Tenant deactivated successfully";
            
        } catch (Exception e) {
            log.error("Error soft deleting tenant: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to deactivate tenant: " + e.getMessage());
        }
    }
    
    /**
     * User Management
     */
    @Transactional(readOnly = true)
    public Page<SuperAdminUserDto> getAllUsers(Pageable pageable) {
        log.info("Getting all active users with pagination");
        
        Page<AuthorizedUser> users = authorizedUserRepository.findByIsActiveTrue(pageable);
        return users.map(this::convertToUserDto);
    }
    
    @Transactional(readOnly = true)
    public SuperAdminUserDto getUserById(Long userId) {
        log.info("Getting user by ID: {}", userId);
        
        AuthorizedUser user = authorizedUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        return convertToUserDto(user);
    }
    
    public SuperAdminUserDto createUser(SuperAdminUserDto userDto) {
        log.info("Creating new user: {}", userDto.getEmailId());
        
        try {
            // Check if login ID already exists
            if (authorizedUserRepository.findByLoginId(userDto.getLoginId()).isPresent()) {
                throw new RuntimeException("Login ID '" + userDto.getLoginId() + "' already exists. Please choose a different login ID.");
            }
            
            // Check if email already exists
            if (authorizedUserRepository.findByEmailId(userDto.getEmailId()).isPresent()) {
                throw new RuntimeException("Email '" + userDto.getEmailId() + "' already exists. Please choose a different email.");
            }
            
            Tenant tenant = null;
            if (userDto.getTenantId() != null) {
                tenant = tenantRepository.findById(userDto.getTenantId())
                        .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + userDto.getTenantId()));
            }
            
            AuthorizedUser user = AuthorizedUser.builder()
                    .firstName(userDto.getFirstName())
                    .lastName(userDto.getLastName())
                    .emailId(userDto.getEmailId())
                    .mobilePhone(userDto.getMobilePhone())
                    .loginId(userDto.getLoginId())
                    .password("temp_password_123") // Temporary password
                    .isActive(userDto.isActive())
                    .lastLogin(LocalDateTime.now())
                    .build();
            
            if (tenant != null) {
                user.setTenant(tenant);
            }
            
            user = authorizedUserRepository.save(user);
            log.info("Created user with ID: {}", user.getId());
            
            // Assign user groups if provided
            if (userDto.getUserGroups() != null && !userDto.getUserGroups().isEmpty()) {
                log.info("Assigning user groups to user {}: {}", user.getId(), userDto.getUserGroups());
                assignUserGroupsToUser(user.getId(), userDto.getUserGroups());
            }
            
            return convertToUserDto(user);
            
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create user: " + e.getMessage());
        }
    }
    
    public SuperAdminUserDto updateUser(Long userId, SuperAdminUserDto userDto) {
        log.info("Updating user: {}", userId);
        
        try {
            AuthorizedUser user = authorizedUserRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            user.setEmailId(userDto.getEmailId());
            user.setMobilePhone(userDto.getMobilePhone());
            user.setLoginId(userDto.getLoginId());
            user.setActive(userDto.isActive());
            
            // Update tenant if provided
            if (userDto.getTenantId() != null) {
                Tenant tenant = tenantRepository.findById(userDto.getTenantId())
                        .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + userDto.getTenantId()));
                user.setTenant(tenant);
                log.info("Updated tenant for user {} to tenant {}", userId, userDto.getTenantId());
            } else {
                // If tenantId is null, remove the tenant association
                user.setTenant(null);
                log.info("Removed tenant association for user {}", userId);
            }
            
            user = authorizedUserRepository.save(user);
            log.info("Updated user with ID: {}", user.getId());
            
            // Update user groups if provided
            if (userDto.getUserGroups() != null) {
                log.info("Updating user groups for user {}: {}", user.getId(), userDto.getUserGroups());
                assignUserGroupsToUser(user.getId(), userDto.getUserGroups());
            }
            
            return convertToUserDto(user);
            
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update user: " + e.getMessage());
        }
    }
    
    public String deleteUser(Long userId) {
        log.info("Soft deleting user: {}", userId);
        
        try {
            AuthorizedUser user = authorizedUserRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
            // Soft delete: mark as inactive instead of removing
            user.setActive(false);
            user.setModifiedAt(LocalDateTime.now());
            user.setModifiedBy("system");
            
            authorizedUserRepository.save(user);
            log.info("Soft deleted user with ID: {}", userId);
            
            return "User deactivated successfully";
            
        } catch (Exception e) {
            log.error("Error soft deleting user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to deactivate user: " + e.getMessage());
        }
    }
    
    /**
     * Role Management
     */
    @Transactional(readOnly = true)
    public List<SuperAdminRoleDto> getAllRoles() {
        log.info("Getting all active roles");
        
        List<RoleList> roles = roleListRepository.findByIsActiveTrue();
        return roles.stream()
                .map(this::convertToRoleDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public SuperAdminRoleDto getRoleById(Long roleId) {
        log.info("Getting role by ID: {}", roleId);
        
        RoleList role = roleListRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));
        
        return convertToRoleDto(role);
    }
    
    public SuperAdminRoleDto createRole(SuperAdminRoleDto roleDto) {
        log.info("Creating new role: {}", roleDto.getLabel());
        
        try {
            Tenant tenant = null;
            if (roleDto.getTenantId() != null) {
                tenant = tenantRepository.findById(roleDto.getTenantId())
                        .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + roleDto.getTenantId()));
            }
            
            RoleList role = new RoleList(
                    roleDto.getParentListId(),
                    roleDto.getLabel(),
                    roleDto.getSequenceNo(),
                    roleDto.isActive()
            );
            
            if (tenant != null) {
                role.setTenant(tenant);
            }
            
            role = roleListRepository.save(role);
            log.info("Created role with ID: {}", role.getId());
            
            return convertToRoleDto(role);
            
        } catch (Exception e) {
            log.error("Error creating role: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create role: " + e.getMessage());
        }
    }
    
    public SuperAdminRoleDto updateRole(Long roleId, SuperAdminRoleDto roleDto) {
        log.info("Updating role: {}", roleId);
        
        try {
            RoleList role = roleListRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));
            
            role.setParentListId(roleDto.getParentListId());
            role.setLabel(roleDto.getLabel());
            role.setSequenceNo(roleDto.getSequenceNo());
            role.setIsActive(roleDto.isActive());
            
            // Update tenant if provided
            if (roleDto.getTenantId() != null) {
                Tenant tenant = tenantRepository.findById(roleDto.getTenantId())
                        .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + roleDto.getTenantId()));
                role.setTenant(tenant);
                log.info("Updated tenant for role {} to tenant {}", roleId, roleDto.getTenantId());
            }
            
            role = roleListRepository.save(role);
            log.info("Updated role with ID: {}", role.getId());
            
            return convertToRoleDto(role);
            
        } catch (Exception e) {
            log.error("Error updating role: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update role: " + e.getMessage());
        }
    }
    
    public String deleteRole(Long roleId) {
        log.info("Soft deleting role: {}", roleId);
        
        try {
            RoleList role = roleListRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));
            
            // Soft delete: mark as inactive instead of removing
            role.setIsActive(false);
            role.setModifiedAt(LocalDateTime.now());
            role.setModifiedBy("system");
            
            roleListRepository.save(role);
            log.info("Soft deleted role with ID: {}", roleId);
            
            return "Role deactivated successfully";
            
        } catch (Exception e) {
            log.error("Error soft deleting role: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to deactivate role: " + e.getMessage());
        }
    }
    
    /**
     * User Group Assignment (replaces direct role assignment)
     */
    public void assignUserGroupsToUser(Long userId, List<String> groupNames) {
        log.info("Assigning user groups to user: {}", userId);
        
        try {
            AuthorizedUser user = authorizedUserRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
            // Initialize userGroups if null
            if (user.getUserGroups() == null) {
                user.setUserGroups(new HashSet<>());
            }
            
            // Clear existing user groups
            user.getUserGroups().clear();
            
            // Add new user groups
            for (String groupName : groupNames) {
                UserGroup group = userGroupRepository.findByGroupName(groupName)
                        .orElseThrow(() -> new RuntimeException("User group not found: " + groupName));
                user.getUserGroups().add(group);
            }
            
            authorizedUserRepository.save(user);
            log.info("Assigned {} user groups to user {}", groupNames.size(), userId);
            
        } catch (Exception e) {
            log.error("Error assigning user groups to user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to assign user groups: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public UserRoleAssignmentDto getUserRoleAssignment(Long userId) {
        log.info("Getting user role assignment for user: {}", userId);
        
        AuthorizedUser user = authorizedUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        // Get roles from user groups
        List<String> currentRoles = new ArrayList<>();
        List<String> currentUserGroups = new ArrayList<>();
        
        if (user.getUserGroups() != null) {
            for (UserGroup group : user.getUserGroups()) {
                currentUserGroups.add(group.getGroupName());
                if (group.getRoles() != null) {
                    currentRoles.addAll(group.getRoles().stream()
                            .map(RoleList::getLabel)
                            .collect(Collectors.toList()));
                }
            }
        }
        
        // Get all available user groups
        List<String> availableUserGroups = userGroupRepository.findByIsActiveTrue().stream()
                .map(UserGroup::getGroupName)
                .collect(Collectors.toList());
        
        return UserRoleAssignmentDto.builder()
                .userId(user.getId())
                .userName(user.getFirstName() + " " + user.getLastName())
                .userEmail(user.getEmailId())
                .currentRoles(currentRoles)
                .availableRoles(availableUserGroups) // Now represents available user groups
                .build();
    }
    
    // Helper methods for conversion
    private SuperAdminTenantDto convertToTenantDto(Tenant tenant) {
        return SuperAdminTenantDto.builder()
                .tenantId(tenant.getTenantId())
                .tenantName(tenant.getTenantName())
                .emailDomain(tenant.getEmailDomain())
                .address(tenant.getAddress())
                .state(tenant.getState())
                .postalCode(tenant.getPostalCode())
                .country(tenant.getCountry())
                .active(tenant.getIsActive())
                .createdAt(tenant.getCreatedAt())
                .updatedAt(tenant.getModifiedAt())
                .totalUsers(authorizedUserRepository.countByTenantId(tenant.getTenantId()))
                .build();
    }
    
    private SuperAdminUserDto convertToUserDto(AuthorizedUser user) {
        List<String> userGroups = user.getUserGroups() != null ? 
                user.getUserGroups().stream()
                        .map(UserGroup::getGroupName)
                        .collect(Collectors.toList()) : 
                new ArrayList<>();
        
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
        
        return SuperAdminUserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .emailId(user.getEmailId())
                .mobilePhone(user.getMobilePhone())
                .loginId(user.getLoginId())
                .active(user.isActive())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getModifiedAt())
                .tenantId(user.getTenant() != null ? user.getTenant().getTenantId() : null)
                .tenantName(user.getTenant() != null ? user.getTenant().getTenantName() : null)
                .roles(roles)
                .userGroups(userGroups)
                .build();
    }
    
    private SuperAdminRoleDto convertToRoleDto(RoleList role) {
        List<String> authorities = role.getAuthorities() != null ? 
                role.getAuthorities().stream()
                        .map(AuthorityList::getAuthorityName) // Assuming AuthorityList has getAuthority method
                        .collect(Collectors.toList()) : 
                List.of();
        
        return SuperAdminRoleDto.builder()
                .id(role.getId())
                .label(role.getLabel())
                .parentListId(role.getParentListId())
                .sequenceNo(role.getSequenceNo())
                .active(role.getIsActive())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getModifiedAt())
                .tenantId(role.getTenant() != null ? role.getTenant().getTenantId() : null)
                .tenantName(role.getTenant() != null ? role.getTenant().getTenantName() : null)
                .authorities(authorities)
                .build();
    }
    
    private List<SuperAdminTenantDto> getRecentTenants() {
        // TODO: Implement proper recent tenants logic
        return List.of();
    }
    
    private List<SuperAdminUserDto> getRecentUsers() {
        // TODO: Implement proper recent users logic
        return List.of();
    }
    
    /**
     * Authority Management
     */
    @Transactional(readOnly = true)
    public List<SuperAdminAuthorityDto> getAllAuthorities() {
        log.info("Getting all authorities");
        
        try {
            List<AuthorityList> authorities = authorityListRepository.findAll();
            return authorities.stream()
                    .map(this::convertToAuthorityDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting authorities: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get authorities: " + e.getMessage());
        }
    }
    
    private SuperAdminAuthorityDto convertToAuthorityDto(AuthorityList authority) {
        return SuperAdminAuthorityDto.builder()
                .id(authority.getId())
                .authorityName(authority.getAuthorityName())
                .resourcePattern(authority.getResourcePattern())
                .description(authority.getDescription())
                .createdAt(authority.getCreatedAt())
                .updatedAt(authority.getModifiedAt())
                .tenantId(authority.getTenant() != null ? authority.getTenant().getTenantId() : null)
                .tenantName(authority.getTenant() != null ? authority.getTenant().getTenantName() : null)
                .build();
    }
    
    /**
     * User Group Management
     */
    @Transactional(readOnly = true)
    public List<SuperAdminUserGroupDto> getAllUserGroups() {
        log.info("Getting all active user groups");
        
        try {
            List<UserGroup> groups = userGroupRepository.findByIsActiveTrue();
            return groups.stream()
                    .map(this::convertToUserGroupDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting user groups: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get user groups: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public SuperAdminUserGroupDto getUserGroupById(Long groupId) {
        log.info("Getting user group by ID: {}", groupId);
        
        try {
            UserGroup group = userGroupRepository.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("User group not found with ID: " + groupId));
            
            return convertToUserGroupDto(group);
        } catch (Exception e) {
            log.error("Error getting user group: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get user group: " + e.getMessage());
        }
    }
    
    public SuperAdminUserGroupDto createUserGroup(SuperAdminUserGroupDto groupDto) {
        log.info("Creating user group: {}", groupDto.getGroupName());
        
        try {
            // Check if group name already exists
            if (userGroupRepository.findByGroupName(groupDto.getGroupName()).isPresent()) {
                throw new RuntimeException("User group with name '" + groupDto.getGroupName() + "' already exists");
            }
            
            UserGroup group = new UserGroup();
            group.setGroupName(groupDto.getGroupName());
            group.setDescription(groupDto.getDescription());
            group.setIsActive(true); // Set as active by default
            
            // Don't manually set audit fields - let TenantEntity handle them
            // The @PrePersist and @PreUpdate annotations in TenantEntity will handle these
            
            log.info("About to save user group with name: {}", group.getGroupName());
            UserGroup savedGroup = userGroupRepository.save(group);
            log.info("Successfully created user group with ID: {}", savedGroup.getId());
            
            return convertToUserGroupDto(savedGroup);
        } catch (Exception e) {
            log.error("Error creating user group: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create user group: " + e.getMessage());
        }
    }
    
    public SuperAdminUserGroupDto updateUserGroup(Long groupId, SuperAdminUserGroupDto groupDto) {
        log.info("Updating user group: {}", groupId);
        
        try {
            UserGroup group = userGroupRepository.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("User group not found with ID: " + groupId));
            
            group.setGroupName(groupDto.getGroupName());
            group.setDescription(groupDto.getDescription());
            
            // Don't manually set audit fields - let TenantEntity handle them
            // The @PreUpdate annotation in TenantEntity will handle modifiedAt and modifiedBy
            
            UserGroup updatedGroup = userGroupRepository.save(group);
            log.info("Updated user group with ID: {}", updatedGroup.getId());
            
            return convertToUserGroupDto(updatedGroup);
        } catch (Exception e) {
            log.error("Error updating user group: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update user group: " + e.getMessage());
        }
    }
    
    public String deleteUserGroup(Long groupId) {
        log.info("Soft deleting user group: {}", groupId);
        
        try {
            UserGroup group = userGroupRepository.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("User group not found with ID: " + groupId));
            
            // Soft delete: mark as inactive instead of removing
            group.setIsActive(false);
            
            // Don't manually set audit fields - let TenantEntity handle them
            // The @PreUpdate annotation in TenantEntity will handle modifiedAt and modifiedBy
            
            userGroupRepository.save(group);
            log.info("Soft deleted user group with ID: {}", groupId);
            
            return "User group deactivated successfully";
            
        } catch (Exception e) {
            log.error("Error soft deleting user group: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to deactivate user group: " + e.getMessage());
        }
    }
    
    public SuperAdminUserGroupDto assignRolesToGroup(Long groupId, List<Long> roleIds) {
        log.info("Assigning roles to group: {}", groupId);
        
        try {
            UserGroup group = userGroupRepository.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("User group not found with ID: " + groupId));
            
            List<RoleList> roles = roleListRepository.findAllById(roleIds);
            group.getRoles().clear();
            group.getRoles().addAll(roles);
            
            UserGroup updatedGroup = userGroupRepository.save(group);
            log.info("Assigned roles to group with ID: {}", updatedGroup.getId());
            
            return convertToUserGroupDto(updatedGroup);
        } catch (Exception e) {
            log.error("Error assigning roles to group: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to assign roles to group: " + e.getMessage());
        }
    }
    
    public String addUserToGroup(Long groupId, Long userId) {
        log.info("Adding user {} to group {}", userId, groupId);
        
        try {
            UserGroup group = userGroupRepository.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("User group not found with ID: " + groupId));
            
            AuthorizedUser user = authorizedUserRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
            if (!user.getUserGroups().contains(group)) {
                user.getUserGroups().add(group);
                authorizedUserRepository.save(user);
                log.info("Added user {} to group {}", userId, groupId);
                return "User added to group successfully";
            } else {
                return "User is already in the group";
            }
        } catch (Exception e) {
            log.error("Error adding user to group: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to add user to group: " + e.getMessage());
        }
    }
    
    public String removeUserFromGroup(Long groupId, Long userId) {
        log.info("Removing user {} from group {}", userId, groupId);
        
        try {
            UserGroup group = userGroupRepository.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("User group not found with ID: " + groupId));
            
            AuthorizedUser user = authorizedUserRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
            if (user.getUserGroups().contains(group)) {
                user.getUserGroups().remove(group);
                authorizedUserRepository.save(user);
                log.info("Removed user {} from group {}", userId, groupId);
                return "User removed from group successfully";
            } else {
                return "User is not in the group";
            }
        } catch (Exception e) {
            log.error("Error removing user from group: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to remove user from group: " + e.getMessage());
        }
    }
    
    private SuperAdminUserGroupDto convertToUserGroupDto(UserGroup group) {
        List<SuperAdminRoleDto> roles = group.getRoles() != null ? 
                group.getRoles().stream()
                        .map(this::convertToRoleDto)
                        .collect(Collectors.toList()) : 
                List.of();
        
        // Get users for this group
        List<AuthorizedUser> groupUsers = authorizedUserRepository.findByUserGroupsIdAndIsActiveTrue(group.getId());
        List<SuperAdminUserDto> users = groupUsers.stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
        
        return SuperAdminUserGroupDto.builder()
                .id(group.getId())
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getModifiedAt())
                .tenantId(group.getTenant() != null ? group.getTenant().getTenantId() : null)
                .tenantName(group.getTenant() != null ? group.getTenant().getTenantName() : null)
                .roles(roles)
                .users(users)
                .build();
    }
}
