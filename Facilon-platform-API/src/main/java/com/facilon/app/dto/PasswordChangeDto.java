package com.facilon.app.dto;

import lombok.Data;

@Data
public class PasswordChangeDto {
    private String oldPassword;
    private String newPassword;

    // Getters and setters
}
