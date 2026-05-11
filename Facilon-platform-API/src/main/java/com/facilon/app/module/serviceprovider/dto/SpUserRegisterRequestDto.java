package com.facilon.app.module.serviceprovider.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Final user-form payload — mirrors validation in Laravel
 * `BrokerController::service_provider_user_register_store`.
 */
@Data
public class SpUserRegisterRequestDto {
    /** Laravel `Crypt::encrypt(email)` token. */
    @NotBlank
    private String serviceProviderEmail;

    @NotBlank
    private String serviceProviderNameHidden;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String designation;

    @NotBlank
    @Email
    private String officialEmail;

    @NotBlank
    @Size(min = 10, max = 15)
    private String officialPhone;

    @NotNull
    private Boolean consent;
}
