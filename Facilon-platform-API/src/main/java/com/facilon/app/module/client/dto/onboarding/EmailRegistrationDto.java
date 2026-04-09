package com.facilon.app.module.client.dto.onboarding;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Step 1a: Email registration DTO.
 * User provides email and indicates if registering as Individual or Legal Entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRegistrationDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotNull(message = "Register as is required")
    private Integer registerAs; // 1=Individual, 2=Legal Entity
}
