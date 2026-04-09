package com.facilon.app.module.client.controller;

import com.facilon.app.annotations.CurrentTenant;
import com.facilon.app.module.client.dto.onboarding.*;
import com.facilon.app.module.client.service.PublicRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public Registration Controller for Investor Self-Registration Flow.
 * No authentication required - public endpoints for new investors.
 * Follows the 3-step process: Email/OTP → Individual/Legal Entity specific details.
 */
@RestController
@RequestMapping("/api/clients/onboarding/register")
@RequiredArgsConstructor
@Slf4j
@CurrentTenant
@Tag(name = "Public Registration", description = "Public investor self-registration APIs")
public class PublicRegistrationController {

    private final PublicRegistrationService registrationService;

    /**
     * Step 1a: Initialize registration with email.
     * Check if email already exists, send OTP if new.
     * Returns uniqueCode for subsequent steps.
     */
    @PostMapping("/email")
    @Operation(summary = "Step 1a: Submit email for registration", 
               description = "Submit email, check if already registered, and send OTP")
    public ResponseEntity<EmailRegistrationResponseDto> submitEmail(
            @Valid @RequestBody EmailRegistrationDto dto) {
        log.info("Email registration request: {}, registerAs={}", dto.getEmail(), dto.getRegisterAs());
        
        try {
            EmailRegistrationResponseDto response = registrationService.initiateRegistration(dto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Email registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(EmailRegistrationResponseDto.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error during email registration: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(EmailRegistrationResponseDto.builder()
                            .success(false)
                            .message("An error occurred. Please try again.")
                            .build());
        }
    }

    /**
     * Step 1b: Verify OTP and record consent.
     * After successful verification, user can proceed to Step 2 or Step 3.
     */
    @PostMapping("/verify-otp")
    @Operation(summary = "Step 1b: Verify OTP and record consent", 
               description = "Verify OTP sent to email and record user consent")
    public ResponseEntity<OtpVerificationResponseDto> verifyOtpAndConsent(
            @Valid @RequestBody OtpConsentVerificationDto dto) {
        log.info("OTP verification request for uniqueCode: {}", dto.getUniqueCode());
        
        try {
            OtpVerificationResponseDto response = registrationService.verifyOtpAndConsent(dto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("OTP verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(OtpVerificationResponseDto.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error during OTP verification: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(OtpVerificationResponseDto.builder()
                            .success(false)
                            .message("Verification failed. Please try again.")
                            .build());
        }
    }

    /**
     * Resend OTP to email.
     */
    @PostMapping("/resend-otp/{uniqueCode}")
    @Operation(summary = "Resend OTP", description = "Resend OTP to the registered email")
    public ResponseEntity<OtpResponseDto> resendOtp(
            @Parameter(description = "Unique registration code") @PathVariable String uniqueCode) {
        log.info("Resend OTP request for uniqueCode: {}", uniqueCode);
        
        try {
            OtpResponseDto response = registrationService.resendOtp(uniqueCode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error resending OTP: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(OtpResponseDto.builder()
                            .message(e.getMessage())
                            .emailSent(false)
                            .smsSent(false)
                            .expiresInMinutes(0)
                            .build());
        }
    }

    /**
     * Step 2: Complete Individual Investor registration.
     * Called after OTP verification for Individual (registerAs=1).
     */
    @PostMapping("/individual/{uniqueCode}")
    @Operation(summary = "Step 2: Complete Individual registration", 
               description = "Submit individual investor details after OTP verification")
    public ResponseEntity<RegistrationCompletionResponseDto> completeIndividualRegistration(
            @Parameter(description = "Unique registration code") @PathVariable String uniqueCode,
            @Valid @RequestBody IndividualRegistrationDto dto) {
        log.info("Individual registration completion for uniqueCode: {}", uniqueCode);
        
        try {
            RegistrationCompletionResponseDto response = registrationService.completeIndividualRegistration(uniqueCode, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("Individual registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(RegistrationCompletionResponseDto.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error completing individual registration: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RegistrationCompletionResponseDto.builder()
                            .success(false)
                            .message("Registration failed. Please try again.")
                            .build());
        }
    }

    /**
     * Step 3: Complete Legal Entity registration.
     * Called after OTP verification for Legal Entity (registerAs=2).
     */
    @PostMapping("/legal-entity/{uniqueCode}")
    @Operation(summary = "Step 3: Complete Legal Entity registration", 
               description = "Submit legal entity details after OTP verification")
    public ResponseEntity<RegistrationCompletionResponseDto> completeLegalEntityRegistration(
            @Parameter(description = "Unique registration code") @PathVariable String uniqueCode,
            @Valid @RequestBody LegalEntityRegistrationDto dto) {
        log.info("Legal entity registration completion for uniqueCode: {}", uniqueCode);
        
        try {
            RegistrationCompletionResponseDto response = registrationService.completeLegalEntityRegistration(uniqueCode, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("Legal entity registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(RegistrationCompletionResponseDto.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error completing legal entity registration: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RegistrationCompletionResponseDto.builder()
                            .success(false)
                            .message("Registration failed. Please try again.")
                            .build());
        }
    }
}
