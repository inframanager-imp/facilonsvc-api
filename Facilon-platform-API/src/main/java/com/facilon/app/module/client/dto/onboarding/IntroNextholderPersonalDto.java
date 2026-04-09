package com.facilon.app.module.client.dto.onboarding;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Step 2 of introduced investor nextholder flow (docs/Investor).
 * Personal details + agree for OTP.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntroNextholderPersonalDto {

    @NotBlank
    private String firstName;
    private String middleName;
    @NotBlank
    private String lastName;
    private String gender; // Male, Female, Transgender
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String mobileNumber;
    private Integer countryCode; // ISD code id
    private String sameWhatsapp; // Yes, No
    private String diffMobWhatsapp; // if sameWhatsapp=No
    private Boolean agreeForOtp;
}
