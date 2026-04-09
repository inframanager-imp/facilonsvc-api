package com.facilon.app.module.client.controller;

import com.facilon.app.module.client.dto.ChangePasswordDto;
import com.facilon.app.module.client.dto.InvestorProfileDto;
import com.facilon.app.module.client.dto.ProfileUpdateDto;
import com.facilon.app.module.client.service.InvestorProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/investor/profile")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Investor Profile", description = "Investor profile management endpoints")
public class InvestorProfileController {

    private final InvestorProfileService profileService;

    @GetMapping("/{uniqueCode}")
    @Operation(summary = "Get investor profile")
    public ResponseEntity<InvestorProfileDto> getProfile(@PathVariable String uniqueCode) {
        log.info("Fetching profile for investor: {}", uniqueCode);
        InvestorProfileDto profile = profileService.getProfile(uniqueCode);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{uniqueCode}")
    @Operation(summary = "Update investor profile")
    public ResponseEntity<InvestorProfileDto> updateProfile(
            @PathVariable String uniqueCode,
            @Valid @RequestBody ProfileUpdateDto dto) {
        log.info("Updating profile for investor: {}", uniqueCode);
        InvestorProfileDto profile = profileService.updateProfile(uniqueCode, dto);
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change investor password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestParam String email,
            @Valid @RequestBody ChangePasswordDto dto) {
        log.info("Password change request for email: {}", email);
        profileService.changePassword(email, dto);
        return ResponseEntity.ok(Map.of(
                "message", "Password changed successfully",
                "status", "success"));
    }
}
