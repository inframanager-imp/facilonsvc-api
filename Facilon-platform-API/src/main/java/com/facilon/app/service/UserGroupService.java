package com.facilon.app.service;

import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.model.RoleList;
import com.facilon.app.model.UserGroup;
import com.facilon.app.repository.AuthorizedUserRepository;
import com.facilon.app.repository.RoleListRepository;
import com.facilon.app.repository.UserGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional
public class UserGroupService {

    private final UserGroupRepository userGroupRepository;
    private final RoleListRepository roleListRepository;
    private final AuthorizedUserRepository authorizedUserRepository;

    @Autowired
    public UserGroupService(UserGroupRepository userGroupRepository,
                            RoleListRepository roleListRepository, AuthorizedUserRepository authorizedUserRepository) {
        this.userGroupRepository = userGroupRepository;
        this.roleListRepository = roleListRepository;
        this.authorizedUserRepository = authorizedUserRepository;
    }

    // Create new User Group
    public UserGroup createGroup(UserGroup groupRequest) {
        // Create new UserGroup
        UserGroup group = new UserGroup();
        group.setGroupName(groupRequest.getGroupName());
        group.setDescription(groupRequest.getDescription());

        // Handle roles
        if (groupRequest.getRoles() != null && !groupRequest.getRoles().isEmpty()) {
            Collection<RoleList> roles = new ArrayList<>();
            for (RoleList roleRequest : groupRequest.getRoles()) {
                // Fetch the actual role from database using the ID
                RoleList role = roleListRepository.findById(roleRequest.getId())
                    .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleRequest.getId()));
                roles.add(role);
            }
            group.setRoles(roles);
        }

        return userGroupRepository.save(group);
    }

    // Assign roles to Group
    @Transactional
    public UserGroup assignRolesToGroup(Long groupId, List<Long> roleIds) {
        UserGroup group = userGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        List<RoleList> roles = roleListRepository.findAllById(roleIds);
        group.setRoles(new HashSet<>(roles));

        return userGroupRepository.save(group);
    }

    // Add User to Group
    @Transactional
    public AuthorizedUser addUserToGroup(Long userId, Long groupId) {
        UserGroup group = userGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        AuthorizedUser user = authorizedUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getUserGroups().add(group);
        return authorizedUserRepository.save(user);
    }

    // Get Group details
    public UserGroup getGroupDetails(Long groupId) {
        return userGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
    }

    // List all Groups
    public List<UserGroup> getAllGroups() {
        return userGroupRepository.findAll();
    }

    // Delete User Group
    @Transactional
    public void deleteGroup(Long groupId) {
        UserGroup group = userGroupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));

        // First, remove this group from any authorized users that reference it
        List<AuthorizedUser> usersWithGroup = authorizedUserRepository.findByUserGroups(group);
        for (AuthorizedUser user : usersWithGroup) {
            user.getUserGroups().remove(group);
            authorizedUserRepository.save(user);
        }

        // Clear the roles association
        group.getRoles().clear();
        userGroupRepository.save(group);

        // Now we can safely delete the group
        userGroupRepository.delete(group);
    }

    // Update User Group
    public UserGroup updateGroup(Long id, UserGroup groupRequest) {
        UserGroup existingGroup = userGroupRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Group not found"));

        existingGroup.setGroupName(groupRequest.getGroupName());
        existingGroup.setDescription(groupRequest.getDescription());

        // Handle roles
        if (groupRequest.getRoles() != null) {
            Collection<RoleList> roles = new ArrayList<>();
            for (RoleList roleRequest : groupRequest.getRoles()) {
                RoleList role = roleListRepository.findById(roleRequest.getId())
                    .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleRequest.getId()));
                roles.add(role);
            }
            existingGroup.setRoles(roles);
        }

        return userGroupRepository.save(existingGroup);
    }

    // Add new method for filtered search
    public List<UserGroup> searchGroups(String groupName, String roleLabel) {
        if (groupName == null && roleLabel == null) {
            return userGroupRepository.findAll();
        }
        return userGroupRepository.findByFilters(groupName, roleLabel);
    }
}
