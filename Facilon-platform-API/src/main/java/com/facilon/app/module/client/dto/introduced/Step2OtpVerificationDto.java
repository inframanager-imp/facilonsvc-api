package com.facilon.app.module.client.dto.introduced;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Step2OtpVerificationDto {
    @NotBlank(message = "Unique code is required")
    private String uniqueCode;
    
    @NotBlank(message = "Email OTP is required")
    @Pattern(regexp = "^[0-9]{4}$", message = "Email OTP must be 4 digits")
    private String emailOtp;
}
