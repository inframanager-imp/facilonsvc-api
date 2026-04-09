package com.facilon.app.module.client.controller;

import com.facilon.app.module.client.dto.onboarding.PmsInvestorRegistrationDto;
import com.facilon.app.module.client.dto.onboarding.PmsOtpRequestDto;
import com.facilon.app.module.client.dto.onboarding.PmsOtpVerifyDto;
import com.facilon.app.module.client.dto.onboarding.VerificationResponseDto;
import com.facilon.app.module.client.service.PmsInvestorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.facilon.app.module.client.dto.PmsComplianceDto;
import com.facilon.app.module.client.dto.PmsPortfolioPreferencesDto;
import com.facilon.app.module.client.dto.PmsRegistrationDto;
import com.facilon.app.module.client.service.PmsService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/clients/pms-investor")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "PMS Investor", description = "Portfolio Management Service investor registration")
public class PmsInvestorController {

    private final PmsInvestorService pmsInvestorService;
    private final PmsService pmsService;

    @PostMapping("/register")
    @Operation(summary = "Register PMS investor")
    public ResponseEntity<VerificationResponseDto> registerPmsInvestor(
            @Valid @RequestBody PmsInvestorRegistrationDto dto) {
        VerificationResponseDto response = pmsInvestorService.registerPmsInvestor(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-otp")
    @Operation(summary = "Send OTP")
    public ResponseEntity<String> sendOtp(@RequestBody PmsOtpRequestDto dto) {
        log.info("Sending OTP to email: {} and mobile: {}", dto.getEmail(), dto.getMobileNumber());
        // Mock OTP send
        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP")
    public ResponseEntity<String> verifyOtp(@RequestBody PmsOtpVerifyDto dto) {
        log.info("Verifying OTP for email: {} and mobile: {}", dto.getEmail(), dto.getMobileNumber());
        // Mock OTP verification - consider any 4 digit code valid for now or specific
        // mock
        return ResponseEntity.ok("OTP verified successfully");
    }

    @GetMapping("/registration/{uniqueCode}")
    @Operation(summary = "Get PMS registration details")
    public ResponseEntity<PmsRegistrationDto> getRegistration(@PathVariable String uniqueCode) {
        return ResponseEntity.ok(pmsService.getRegistration(uniqueCode));
    }

    @PutMapping("/registration/{uniqueCode}")
    @Operation(summary = "Update PMS registration details")
    public ResponseEntity<PmsRegistrationDto> updateRegistration(
            @PathVariable String uniqueCode,
            @Valid @RequestBody PmsRegistrationDto dto) {
        return ResponseEntity.ok(pmsService.updateRegistration(uniqueCode, dto));
    }

    @PostMapping("/compliance/{uniqueCode}")
    @Operation(summary = "Submit PMS compliance")
    public ResponseEntity<PmsComplianceDto> submitCompliance(
            @PathVariable String uniqueCode,
            @Valid @RequestBody PmsComplianceDto dto,
            HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        return ResponseEntity.ok(pmsService.submitCompliance(uniqueCode, dto, ipAddress, userAgent));
    }

    @GetMapping("/compliance/{uniqueCode}")
    @Operation(summary = "Get PMS compliance details")
    public ResponseEntity<PmsComplianceDto> getCompliance(@PathVariable String uniqueCode) {
        return ResponseEntity.ok(pmsService.getCompliance(uniqueCode));
    }

    @PostMapping("/portfolio-preferences/{uniqueCode}")
    @Operation(summary = "Set PMS portfolio preferences")
    public ResponseEntity<PmsPortfolioPreferencesDto> setPortfolioPreferences(
            @PathVariable String uniqueCode,
            @Valid @RequestBody PmsPortfolioPreferencesDto dto) {
        return ResponseEntity.ok(pmsService.setPortfolioPreferences(uniqueCode, dto));
    }

    @GetMapping("/portfolio-preferences/{uniqueCode}")
    @Operation(summary = "Get PMS portfolio preferences")
    public ResponseEntity<PmsPortfolioPreferencesDto> getPortfolioPreferences(@PathVariable String uniqueCode) {
        return ResponseEntity.ok(pmsService.getPortfolioPreferences(uniqueCode));
    }
}
