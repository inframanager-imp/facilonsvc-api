package com.facilon.app.module.client.controller;

import com.facilon.app.module.client.dto.onboarding.*;
import com.facilon.app.module.client.service.PmsInvestorNextholderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public PMS investor nextholder flow per docs/Investor.
 * 4-step wizard: Self/Legal → Personal (with DOB) + OTP → Verify OTP → Consents + password.
 * PMS context (manager, plan, bank) from step 1 or URL query params.
 */
@RestController
@RequestMapping("/api/clients/pms-investor/nextholder/docs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "PMS Investor Nextholder (Public)", description = "4-step flow per docs/Investor - Self/Legal, Personal+DOB+OTP, Verify, Complete")
public class PmsInvestorNextholderController {

    private final PmsInvestorNextholderService nextholderService;

    @PostMapping("/step1")
    @Operation(summary = "Step 1: Self or Legal Entity + PMS context")
    public ResponseEntity<IntroInvestorResponseDto> step1(@Valid @RequestBody PmsNextholderInitDto dto) {
        IntroInvestorResponseDto response = nextholderService.processInit(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/step2/{code}")
    @Operation(summary = "Step 2: Personal details (incl. DOB) + OTP send")
    public ResponseEntity<OtpResponseDto> step2(
            @PathVariable String code,
            @Valid @RequestBody PmsNextholderPersonalDto dto) {
        OtpResponseDto response = nextholderService.processPersonal(code, dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/step3")
    @Operation(summary = "Step 3: Verify OTP")
    public ResponseEntity<IntroInvestorResponseDto> step3(@Valid @RequestBody IntroNextholderVerifyOtpDto dto) {
        IntroInvestorResponseDto response = nextholderService.processVerifyOtp(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/step4/{code}")
    @Operation(summary = "Step 4: Consents + password, complete registration")
    public ResponseEntity<IntroInvestorResponseDto> step4(
            @PathVariable String code,
            @Valid @RequestBody PmsNextholderCompleteDto dto) {
        IntroInvestorResponseDto response = nextholderService.processComplete(code, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/step2/{code}")
    @Operation(summary = "Get PMS intro data for step 2 (pre-fill)")
    public ResponseEntity<PmsStep2PrefillDto> getStep2Prefill(@PathVariable String code) {
        var temp = nextholderService.getByCode(code);
        if (temp == null) {
            return ResponseEntity.noContent().build();
        }
        PmsStep2PrefillDto dto = PmsStep2PrefillDto.builder()
                .firstName(temp.getIntroFirstName())
                .middleName(temp.getIntroMiddleName())
                .lastName(temp.getIntroLastName())
                .email(temp.getIntroEmail())
                .mobile(temp.getIntroMobile())
                .registerAs(temp.getInvestorRegisterAs())
                .build();
        return ResponseEntity.ok(dto);
    }
}
