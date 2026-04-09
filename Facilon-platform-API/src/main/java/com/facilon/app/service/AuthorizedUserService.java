package com.facilon.app.service;

import com.facilon.app.dto.AuthorizedUserDto;
import com.facilon.app.exception.UserAlreadyExistsException;
import com.facilon.app.exception.UserNotFoundException;
import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.model.RoleList;
import com.facilon.app.model.UserGroup;
import com.facilon.app.repository.AuthorizedUserRepository;
import com.facilon.app.repository.RoleListRepository;
import com.facilon.app.repository.UserGroupRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class AuthorizedUserService {

    private final AuthorizedUserRepository authorizedUserRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleListRepository roleListRepository;
    private final UserGroupRepository userGroupRepository;

    @Autowired
    public AuthorizedUserService(AuthorizedUserRepository authorizedUserRepository,
                                 ModelMapper modelMapper,
                                 BCryptPasswordEncoder passwordEncoder,
                                 RoleListRepository roleListRepository, UserGroupRepository userGroupRepository) {
        this.authorizedUserRepository = authorizedUserRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleListRepository = roleListRepository;
        this.userGroupRepository = userGroupRepository;
    }

    @Transactional
    public AuthorizedUserDto registerUser(AuthorizedUserDto userDto) {
        authorizedUserRepository.findByEmailId(userDto.getEmailId()).ifPresent(u -> {
            throw new UserAlreadyExistsException("User with email " + userDto.getEmailId() + " already exists");
        });

        RoleList roleList = roleListRepository.findByLabel("user")
                .orElseThrow(() -> new RuntimeException("Default user role not found"));

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        AuthorizedUser user = modelMapper.map(userDto, AuthorizedUser.class);
       // user.setRoleLists(Arrays.asList(roleList));

        user = authorizedUserRepository.save(user);

        return modelMapper.map(user, AuthorizedUserDto.class);
    }

    public boolean emailExists(String email) {
        return authorizedUserRepository.findByEmailId(email).isPresent();
    }

    public boolean loginIdExists(String loginId) {
        return authorizedUserRepository.findByLoginId(loginId).isPresent();
    }

    // New method to find user by ID
    public AuthorizedUser findUserById(Long userId) {
        return authorizedUserRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    // New method to save/update user entity
    public AuthorizedUser save(AuthorizedUser user) {
        return authorizedUserRepository.save(user);
    }

    // Add user to multiple groups
    @Transactional
    public AuthorizedUser addUserToGroups(Long userId, List<Long> groupIds) {
        AuthorizedUser user = authorizedUserRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<UserGroup> groups = userGroupRepository.findAllById(groupIds);
        user.getUserGroups().addAll(groups);
        return authorizedUserRepository.save(user);
    }

    // Remove user from group
    @Transactional
    public AuthorizedUser removeUserFromGroup(Long userId, Long groupId) {
        AuthorizedUser user = authorizedUserRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserGroup group = userGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        user.getUserGroups().remove(group);
        return authorizedUserRepository.save(user);
    }

    /**
     * Finds all users who have the FORWARDER role through their user groups
     * @return List of AuthorizedUser with FORWARDER role
     */
    public List<AuthorizedUser> findForwarders() {
        return authorizedUserRepository.findUsersWithForwarderRole();
    }

    /**
     * Checks if a user has the FORWARDER role through their user groups
     * @param userId The ID of the user to check
     * @return true if the user has the FORWARDER role, false otherwise
     */
    public boolean isForwarder(Long userId) {
        AuthorizedUser user = authorizedUserRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        return user.getUserGroups().stream()
            .flatMap(group -> group.getRoles().stream())
            .anyMatch(role -> "FORWARDER".equals(role.getLabel()));
    }

    /**
     * Adds the FORWARDER role to a user through a user group
     * @param userId The ID of the user
     * @param groupId The ID of the group that has the FORWARDER role
     * @return Updated AuthorizedUser
     */
    @Transactional
    public AuthorizedUser addForwarderRole(Long userId, Long groupId) {
        AuthorizedUser user = authorizedUserRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        UserGroup group = userGroupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("Group not found with ID: " + groupId));

        // Verify that the group has the FORWARDER role
        boolean hasForwarderRole = group.getRoles().stream()
            .anyMatch(role -> "FORWARDER".equals(role.getLabel()));
        
        if (!hasForwarderRole) {
            throw new RuntimeException("The specified group does not have the FORWARDER role");
        }

        user.getUserGroups().add(group);
        return authorizedUserRepository.save(user);
    }

}
