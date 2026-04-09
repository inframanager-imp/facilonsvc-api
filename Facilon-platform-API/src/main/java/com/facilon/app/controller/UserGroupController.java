package com.facilon.app.controller;

import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.model.UserGroup;
import com.facilon.app.service.UserGroupService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@SecurityRequirement(name = "Bearer Authentication")
public class UserGroupController {

    private final UserGroupService userGroupService;
    private static final Logger log = LoggerFactory.getLogger(UserGroupController.class);

    @Autowired
    public UserGroupController(UserGroupService userGroupService) {
        this.userGroupService = userGroupService;
    }

    // Create new group
    @PostMapping("/create")
    public ResponseEntity<UserGroup> createGroup(@RequestBody UserGroup groupRequest) {
        try {
            UserGroup createdGroup = userGroupService.createGroup(groupRequest);
            return ResponseEntity.ok(createdGroup);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Assign roles to group
    @PostMapping("/{groupId}/assign-roles")
    public ResponseEntity<UserGroup> assignRoles(@PathVariable Long groupId, @RequestBody List<Long> roleIds) {
        UserGroup updatedGroup = userGroupService.assignRolesToGroup(groupId, roleIds);
        return ResponseEntity.ok(updatedGroup);
    }

    // Add user to group
    @PostMapping("/{groupId}/add-user/{userId}")
    public ResponseEntity<String> addUserToGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        userGroupService.addUserToGroup(userId, groupId);
        return ResponseEntity.ok("User added to group successfully");
    }

    // Get group details
    @GetMapping("/{groupId}")
    public ResponseEntity<UserGroup> getGroup(@PathVariable Long groupId) {
        UserGroup group = userGroupService.getGroupDetails(groupId);
        return ResponseEntity.ok(group);
    }

    // List all groups
    @GetMapping("/all")
    public ResponseEntity<List<UserGroup>> getAllGroups() {
        List<UserGroup> groups = userGroupService.getAllGroups();
        return ResponseEntity.ok(groups);
    }

    // Delete group
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        try {
            userGroupService.deleteGroup(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting group: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update group
    @PutMapping("/{id}")
    public ResponseEntity<UserGroup> updateGroup(@PathVariable Long id, @RequestBody UserGroup groupRequest) {
        try {
            UserGroup updatedGroup = userGroupService.updateGroup(id, groupRequest);
            return ResponseEntity.ok(updatedGroup);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserGroup>> searchGroups(
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) String role) {
        List<UserGroup> groups = userGroupService.searchGroups(groupName, role);
        return ResponseEntity.ok(groups);
    }
}
