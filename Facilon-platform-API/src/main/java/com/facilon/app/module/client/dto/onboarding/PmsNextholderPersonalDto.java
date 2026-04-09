package com.facilon.app.module.client.dto.onboarding;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Step 2 of PMS investor nextholder flow (docs/Investor).
 * Personal details including DOB + agree for OTP.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsNextholderPersonalDto {

    @NotBlank
    private String firstName;
    private String middleName;
    @NotBlank
    private String lastName;
    @NotNull
    private LocalDate userDob;
    private String gender;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String mobileNumber;
    private Integer countryCode;
    private String sameWhatsapp;
    private String diffMobWhatsapp;
    private Boolean agreeForOtp;
}
