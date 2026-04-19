package com.facilon.app.module.client.controller;

import com.facilon.app.module.client.dto.onboarding.PmsInviteResponseDto;
import com.facilon.app.module.client.dto.onboarding.PmsInvestorRegistrationDto;
import com.facilon.app.module.client.dto.onboarding.PmsOtpRequestDto;
import com.facilon.app.module.client.dto.onboarding.PmsOtpResponseDto;
import com.facilon.app.module.client.dto.onboarding.PmsOtpVerifyDto;
import com.facilon.app.module.client.dto.onboarding.VerificationResponseDto;
import com.facilon.app.module.client.service.PmsInvestorService;
import com.facilon.app.module.client.service.PmsOtpService;
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
    private final PmsOtpService pmsOtpService;

    @PostMapping("/register")
    @Operation(summary = "Register PMS investor")
    public ResponseEntity<VerificationResponseDto> registerPmsInvestor(
            @Valid @RequestBody PmsInvestorRegistrationDto dto) {
        VerificationResponseDto response = pmsInvestorService.registerPmsInvestor(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Public invite-link entry — mirrors Laravel
     * {@code GET /introduce-investor-pms/{introduce_id}} (route web.php L200).
     *
     * <p>The broker / PM sends the investor an email containing a URL of the form
     * {@code https://&lt;host&gt;/investor/pms/invite/INV-1744}.  The frontend calls this
     * endpoint on page-load, receives the pre-populated assignment data + a fresh
     * {@code uniqueCode}, and carries the code through the remaining wizard steps
     * so the investor never has to select broker / product / plan / bank.
     */
    @GetMapping("/invite/{introduceId}")
    @Operation(summary = "Accept PMS invite link",
            description = "Laravel parity: fetches ss_investors by ss_name, seeds an "
                    + "intro_investor_temp row with the broker / product / plan / bank references, "
                    + "and returns the pre-populated data + a fresh uniqueCode for the wizard.")
    public ResponseEntity<PmsInviteResponseDto> acceptInvite(@PathVariable String introduceId) {
        log.info("PMS invite lookup for introduceId={}", introduceId);
        PmsInviteResponseDto response = pmsInvestorService.acceptPmsInvite(introduceId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-otp")
    @Operation(summary = "Send OTP",
            description = "Sends a 4-digit OTP to the supplied email and mobile. "
                    + "Rate-limited to 3 sends per 15-minute window per (email, mobile) pair. "
                    + "Laravel parity: InvestorController.introduce_investor_pms_register_step1_submit.")
    public ResponseEntity<PmsOtpResponseDto> sendOtp(@RequestBody PmsOtpRequestDto dto) {
        log.info("Sending PMS OTP to email={} mobile={}", dto.getEmail(), dto.getMobileNumber());

        if (dto.getEmail() == null || dto.getEmail().isBlank()
                || dto.getMobileNumber() == null || dto.getMobileNumber().isBlank()) {
            return ResponseEntity.badRequest().body(PmsOtpResponseDto.builder()
                    .success(false)
                    .code("invalid_request")
                    .message("Email and mobile number are required.")
                    .build());
        }

        PmsOtpService.OtpSendResult result = pmsOtpService.requestOtp(
                dto.getEmail(), dto.getMobileNumber(), dto.getFirstName());

        PmsOtpResponseDto response = PmsOtpResponseDto.builder()
                .success(result.success)
                .code(result.success ? "ok" : "rate_limited")
                .message(result.message)
                .emailSent(result.emailSent)
                .smsSent(result.smsSent)
                .expiresInMinutes(result.expiresInMinutes)
                .build();

        if (!result.success) {
            return ResponseEntity.status(429).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP",
            description = "Verifies the email + SMS OTP combination. "
                    + "Enforces brute-force protection: 5 failed attempts → 15-minute lock. "
                    + "Laravel parity: InvestorController.introduce_investor_pms_register_step2_verify_otp.")
    public ResponseEntity<PmsOtpResponseDto> verifyOtp(@RequestBody PmsOtpVerifyDto dto) {
        log.info("Verifying PMS OTP for email={} mobile={}", dto.getEmail(), dto.getMobileNumber());

        if (dto.getEmail() == null || dto.getMobileNumber() == null
                || dto.getEmailOtp() == null || dto.getSmsOtp() == null) {
            return ResponseEntity.badRequest().body(PmsOtpResponseDto.builder()
                    .success(false)
                    .code("invalid_request")
                    .message("Email, mobile, emailOtp and smsOtp are required.")
                    .build());
        }

        PmsOtpService.OtpVerifyResult result = pmsOtpService.verifyOtp(
                dto.getEmail(), dto.getMobileNumber(), dto.getEmailOtp(), dto.getSmsOtp());

        PmsOtpResponseDto response = PmsOtpResponseDto.builder()
                .success(result.success)
                .code(result.code)
                .message(result.message)
                .remainingAttempts(result.remainingAttempts)
                .build();

        if (!result.success) {
            // 423 Locked for lockout, 410 Gone for expired, 401 for mismatch / not_issued
            int status;
            switch (result.code) {
                case "locked":     status = 423; break;
                case "expired":    status = 410; break;
                case "not_issued": status = 404; break;
                default:           status = 401; break; // mismatch
            }
            return ResponseEntity.status(status).body(response);
        }
        return ResponseEntity.ok(response);
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
