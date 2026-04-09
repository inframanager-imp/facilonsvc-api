package com.facilon.app.module.client.controller;

import com.facilon.app.module.client.dto.introduced.*;
import com.facilon.app.module.client.service.IntroducedInvestorRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Introduced Investor Registration Flow
 * Matches Laravel routes for introduce-multiple-register flow
 */
@RestController
@RequestMapping("/api/investor/introduced")
@RequiredArgsConstructor
@Slf4j
public class IntroducedInvestorController {

    private final IntroducedInvestorRegistrationService registrationService;

    /**
     * Step 0: Initiate registration with plain Dataverse {@code ss_name} or
     * Laravel-encrypted token
     * (same as Laravel {@code introduce-investor1/{Crypt::encrypt(introduce_id)}}).
     * <p>
     * GET /api/investor/introduced/initiate?investorRef=&lt;encrypted-or-plain&gt;
     * or legacy {@code ?dataverseInvestorId=INV-1744}
     */
    @GetMapping("/initiate")
    public ResponseEntity<IntroducedInvestorDetailsDto> initiateRegistration(
            @RequestParam(required = false) String investorRef,
            @RequestParam(required = false) String dataverseInvestorId) {
        String resolved = registrationService.resolveInvestorReference(investorRef, dataverseInvestorId);
        log.info("📥 Initiate introduced investor registration (resolved ss_name): {}", resolved);
        return ResponseEntity.ok(registrationService.initiateRegistration(resolved));
    }

    /**
     * Step 1: Record data consent
     * POST /api/investor/introduced/consent
     */
    @PostMapping("/consent")
    public ResponseEntity<ApiResponseDto> recordConsent(
            @RequestParam String dataverseInvestorId) {
        log.info("📥 Record consent for: {}", dataverseInvestorId);
        return ResponseEntity.ok(registrationService.recordConsent(dataverseInvestorId));
    }

    /**
     * Step 2: Submit personal details and send OTP
     * POST /api/investor/introduced/step1
     */
    @PostMapping("/step1")
    public ResponseEntity<ApiResponseDto> submitStep1(
            @Valid @RequestBody Step1RequestDto dto) {
        log.info("📥 Step1 submission for uniqueCode: {}", dto.getUniqueCode());
        return ResponseEntity.ok(registrationService.submitStep1AndSendOtp(dto));
    }

    /**
     * Step 3: Verify OTP
     * POST /api/investor/introduced/verify-otp
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponseDto> verifyOtp(
            @Valid @RequestBody Step2OtpVerificationDto dto) {
        log.info("📥 OTP verification for uniqueCode: {}", dto.getUniqueCode());
        return ResponseEntity.ok(registrationService.verifyOtp(dto));
    }

    /**
     * Step 4: Complete registration (with B2C creation)
     * POST /api/investor/introduced/complete
     */
    @PostMapping("/complete")
    public ResponseEntity<ApiResponseDto> completeRegistration(
            @Valid @RequestBody Step4CompletionDto dto) {
        log.info("📥 Complete registration for uniqueCode: {}", dto.getUniqueCode());
        return ResponseEntity.ok(registrationService.completeRegistration(dto));
    }

    /**
     * Resend OTP
     * POST /api/investor/introduced/resend-otp
     */
    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponseDto> resendOtp(
            @RequestParam String uniqueCode) {
        log.info("📥 Resend OTP for uniqueCode: {}", uniqueCode);
        return ResponseEntity.ok(registrationService.resendOtp(uniqueCode));
    }

    /**
     * Get session prefill data for Step1 form
     * GET /api/investor/introduced/session/{uniqueCode}
     */
    @GetMapping("/session/{uniqueCode}")
    public ResponseEntity<IntroducedInvestorDetailsDto> getSessionPrefill(
            @PathVariable String uniqueCode) {
        log.info("📥 Get session prefill for uniqueCode: {}", uniqueCode);
        return ResponseEntity.ok(registrationService.getSessionPrefill(uniqueCode));
    }
}
