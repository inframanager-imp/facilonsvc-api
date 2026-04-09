package com.facilon.app.module.client.controller;

import com.facilon.app.module.client.dto.*;
import com.facilon.app.module.client.service.ClientProfileService;
import com.facilon.app.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/clients/profile")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Client Profile", description = "Investor profile - personal info, passport, experience, consents")
public class ClientProfileController {

    private final ClientProfileService profileService;

    @GetMapping("/onboarding-status")
    @Operation(summary = "Get onboarding progress status")
    public ResponseEntity<OnboardingStatusDto> getOnboardingStatus(Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        OnboardingStatusDto status = profileService.getOnboardingStatus(userId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/personal-info")
    @Operation(summary = "Get personal information")
    public ResponseEntity<UserPersonalInformationDto> getPersonalInfo(Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        Optional<UserPersonalInformationDto> info = profileService.getPersonalInfo(userId);
        return info.map(ResponseEntity::ok).orElse(ResponseEntity.noContent().build());
    }

    @PutMapping("/personal-info")
    @Operation(summary = "Update personal information")
    public ResponseEntity<UserPersonalInformationDto> updatePersonalInfo(
            @Valid @RequestBody UserPersonalInformationDto dto,
            Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        UserPersonalInformationDto updated = profileService.updatePersonalInfo(userId, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/passport")
    @Operation(summary = "Get passport details")
    public ResponseEntity<UserPassportDetailsDto> getPassport(Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        Optional<UserPassportDetailsDto> passport = profileService.getPassport(userId);
        return passport.map(ResponseEntity::ok).orElse(ResponseEntity.noContent().build());
    }

    @PutMapping("/passport")
    @Operation(summary = "Update passport details")
    public ResponseEntity<UserPassportDetailsDto> updatePassport(
            @Valid @RequestBody UserPassportDetailsDto dto,
            Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        UserPassportDetailsDto updated = profileService.updatePassport(userId, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/experience")
    @Operation(summary = "Get investor experience")
    public ResponseEntity<InvestorExperienceDto> getExperience(Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        return profileService.getExperience(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PutMapping("/experience")
    @Operation(summary = "Update investor experience")
    public ResponseEntity<InvestorExperienceDto> updateExperience(
            @Valid @RequestBody InvestorExperienceDto dto,
            Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        InvestorExperienceDto updated = profileService.updateExperience(userId, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/consents")
    @Operation(summary = "Get consents")
    public ResponseEntity<InvestorConsentsDto> getConsents(Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        return profileService.getConsents(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/consents")
    @Operation(summary = "Record consent")
    public ResponseEntity<InvestorConsentsDto> recordConsent(
            @Valid @RequestBody InvestorConsentsDto dto,
            Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        InvestorConsentsDto updated = profileService.recordConsent(userId, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/residential")
    @Operation(summary = "Get residential status")
    public ResponseEntity<UserResidentialStatusDto> getResidentialStatus(Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        return profileService.getResidentialStatus(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PutMapping("/residential")
    @Operation(summary = "Update residential status")
    public ResponseEntity<UserResidentialStatusDto> updateResidentialStatus(
            @Valid @RequestBody UserResidentialStatusDto dto,
            Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        UserResidentialStatusDto updated = profileService.updateResidentialStatus(userId, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/tax-info")
    @Operation(summary = "Get tax information")
    public ResponseEntity<UserTaxInfoDto> getTaxInfo(Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        return profileService.getTaxInfo(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PutMapping("/tax-info")
    @Operation(summary = "Update tax information")
    public ResponseEntity<UserTaxInfoDto> updateTaxInfo(
            @Valid @RequestBody UserTaxInfoDto dto,
            Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        UserTaxInfoDto updated = profileService.updateTaxInfo(userId, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/bank-details")
    @Operation(summary = "Get bank details")
    public ResponseEntity<UserBankDetailsDto> getBankDetails(Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        return profileService.getBankDetails(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PutMapping("/bank-details")
    @Operation(summary = "Update bank details")
    public ResponseEntity<UserBankDetailsDto> updateBankDetails(
            @Valid @RequestBody UserBankDetailsDto dto,
            Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        UserBankDetailsDto updated = profileService.updateBankDetails(userId, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/contact-details")
    @Operation(summary = "Get contact details")
    public ResponseEntity<UserContactDetailsDto> getContactDetails(Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        return profileService.getContactDetails(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PutMapping("/contact-details")
    @Operation(summary = "Update contact details")
    public ResponseEntity<UserContactDetailsDto> updateContactDetails(
            @Valid @RequestBody UserContactDetailsDto dto,
            Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        UserContactDetailsDto updated = profileService.updateContactDetails(userId, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/nomination")
    @Operation(summary = "Get nomination details")
    public ResponseEntity<UserNominationDto> getNomination(Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        return profileService.getNomination(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PutMapping("/nomination")
    @Operation(summary = "Update nomination details")
    public ResponseEntity<UserNominationDto> updateNomination(
            @Valid @RequestBody UserNominationDto dto,
            Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        UserNominationDto updated = profileService.updateNomination(userId, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/risk-profile")
    @Operation(summary = "Get risk profile")
    public ResponseEntity<UserRiskProfileDto> getRiskProfile(Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        return profileService.getRiskProfile(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PutMapping("/risk-profile")
    @Operation(summary = "Update risk profile")
    public ResponseEntity<UserRiskProfileDto> updateRiskProfile(
            @Valid @RequestBody UserRiskProfileDto dto,
            Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        UserRiskProfileDto updated = profileService.updateRiskProfile(userId, dto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/final-submit")
    @Operation(summary = "Final submission of all profile information")
    public ResponseEntity<OnboardingStatusDto> finalSubmit(@RequestBody InvestorConsentsDto consents, Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        OnboardingStatusDto status = profileService.finalSubmit(userId, consents);
        return ResponseEntity.ok(status);
    }
}
