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
public class Step1Dto {

    @NotNull(message = "Title is required")
    private Integer title; // Reference to master_title table
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    private String middleName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank @Email
    private String email; // For legal entity: must be corporate email (no gmail, yahoo, etc.)
    
    @NotBlank
    private String mobileNumber;
    
    private String whatsappNumber; // Optional - if different from mobile
    
    @NotNull
    private Integer countryCode;
    
    @NotNull
    private Integer gender;
    
    private LocalDate userDob;

    // For Legal Entity - Authorized Representative
    private String representativeCapacity; // Under what capacity representing the legal entity
}
