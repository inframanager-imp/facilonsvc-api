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
public class UserRoleAssignmentDto {
    private Long userId;
    private String userName;
    private String userEmail;
    private List<String> currentRoles;
    private List<String> availableRoles;
    private List<String> selectedRoles;
}
