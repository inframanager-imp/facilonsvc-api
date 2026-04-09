package com.facilon.app.module.client.dto.onboarding;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsInvestorRegistrationDto {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String mobilePhone;

    @NotBlank
    private String password;

    @NotNull
    private Long pmsManagerId;

    @NotNull
    private Long pmsPlanId;

    @NotNull
    private Long pmsBankId;

    @NotBlank
    private String accountNumber;

    @NotNull
    private LocalDate agreementDate;

    private String comments;
}
