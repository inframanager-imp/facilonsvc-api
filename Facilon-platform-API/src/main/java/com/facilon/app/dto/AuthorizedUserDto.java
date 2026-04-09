package com.facilon.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizedUserDto extends  AuditableDto {
     private  Long Id;
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Mobile phone is required")
    @Pattern(regexp = "^[0-9]+$", message = "Mobile phone must be numeric")
    private String mobilePhone;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email ID is required")
    private String emailId;

    private String loginId;

    private Integer failedLogins;
    @NotBlank(message = "Password  is required")
    private String password;

    private LocalDateTime lastPasswordChange;

    private LocalDateTime lastFailed;

    private LocalDateTime lastLogin;

    private Boolean isActive;

    private List<RoleListDto> roleLists; // Add this line
    

    // Getters and Setters for all fields including the new userRole
}
