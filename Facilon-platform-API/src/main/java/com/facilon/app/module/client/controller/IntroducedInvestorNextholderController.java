package com.facilon.app.module.client.controller;

import com.facilon.app.module.client.dto.onboarding.IntroInvestorResponseDto;
import com.facilon.app.module.client.dto.onboarding.IntroInvestorStep2PrefillDto;
import com.facilon.app.module.client.dto.onboarding.IntroNextholderCompleteDto;
import com.facilon.app.module.client.dto.onboarding.IntroNextholderInitDto;
import com.facilon.app.module.client.dto.onboarding.IntroNextholderPersonalDto;
import com.facilon.app.module.client.dto.onboarding.IntroNextholderVerifyOtpDto;
import com.facilon.app.module.client.dto.onboarding.OtpResponseDto;
import com.facilon.app.module.client.service.IntroducedInvestorNextholderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public introduced investor nextholder flow per docs/Investor.
 * 4-step wizard: Self/Legal → Personal+OTP → Verify OTP → Password+Terms.
 */
@RestController
@RequestMapping("/api/clients/introduced-investor/nextholder/docs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Introduced Investor Nextholder (Public)", description = "4-step flow per docs/Investor - Self/Legal, Personal+OTP, Verify, Complete")
public class IntroducedInvestorNextholderController {

    private final IntroducedInvestorNextholderService nextholderService;

    @PostMapping("/step1")
    @Operation(summary = "Step 1: Self or Legal Entity")
    public ResponseEntity<IntroInvestorResponseDto> step1(@Valid @RequestBody IntroNextholderInitDto dto) {
        IntroInvestorResponseDto response = nextholderService.processInit(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/step2/{code}")
    @Operation(summary = "Step 2: Personal details + OTP send")
    public ResponseEntity<OtpResponseDto> step2(
            @PathVariable String code,
            @Valid @RequestBody IntroNextholderPersonalDto dto) {
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
    @Operation(summary = "Step 4: Password + terms, complete registration")
    public ResponseEntity<IntroInvestorResponseDto> step4(
            @PathVariable String code,
            @Valid @RequestBody IntroNextholderCompleteDto dto) {
        IntroInvestorResponseDto response = nextholderService.processComplete(code, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/step2/{code}")
    @Operation(summary = "Get intro data for step 2 (pre-fill from invitation)")
    public ResponseEntity<IntroInvestorStep2PrefillDto> getStep2Prefill(@PathVariable String code) {
        var temp = nextholderService.getByCode(code);
        if (temp == null) {
            return ResponseEntity.noContent().build();
        }
        IntroInvestorStep2PrefillDto dto = IntroInvestorStep2PrefillDto.builder()
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
