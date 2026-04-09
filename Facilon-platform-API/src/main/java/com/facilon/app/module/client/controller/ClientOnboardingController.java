package com.facilon.app.module.client.controller;

import com.facilon.app.module.client.dto.onboarding.*;
import com.facilon.app.module.client.service.ClientOnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients/onboarding")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Client Onboarding", description = "Investor self-registration (public)")
public class ClientOnboardingController {

    private final ClientOnboardingService onboardingService;

    @PostMapping("/register/main-step")
    @Operation(summary = "Step 1: Basic info", description = "Returns unique code for next steps")
    public ResponseEntity<String> registerMainStep(@Valid @RequestBody MainStepDto dto) {
        String uniqueCode = onboardingService.registerMainStep(dto);
        return ResponseEntity.ok(uniqueCode);
    }

    @PostMapping("/register/step1/{code}")
    @Operation(summary = "Step 2: Personal details + OTP")
    public ResponseEntity<OtpResponseDto> registerStep1(
            @PathVariable String code,
            @Valid @RequestBody Step1Dto dto) {
        OtpResponseDto response = onboardingService.registerStep1(code, dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/step2/verify-otp")
    @Operation(summary = "Step 3: Verify OTP")
    public ResponseEntity<VerificationResponseDto> verifyOtp(@Valid @RequestBody OtpVerificationDto dto) {
        VerificationResponseDto response = onboardingService.verifyOtp(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/step3/{code}")
    @Operation(summary = "Step 4: Registration details (nationality, PAN/OCI, resident status) and categorization")
    public ResponseEntity<VerificationResponseDto> registerStep3(
            @PathVariable String code,
            @Valid @RequestBody Step3RegistrationDetailsDto dto,
            HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        VerificationResponseDto response = onboardingService.registerStep3(code, dto, ipAddress, userAgent);
        return ResponseEntity.ok(response);
    }

    /**
     * Get client IP address from request, handling proxies
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For can contain multiple IPs, take the first one
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        return request.getRemoteAddr();
    }
}
