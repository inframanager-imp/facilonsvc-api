package com.facilon.app.module.client.controller;

import com.facilon.app.annotations.CurrentTenant;
import com.facilon.app.module.client.dto.*;
import com.facilon.app.module.client.service.ClientService;
import com.facilon.app.module.client.service.InvestorInformationService;
import com.facilon.app.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Investor Information Controller - For collecting detailed investor
 * information
 */
@RestController
@RequestMapping("/api/clients/me/information")
@RequiredArgsConstructor
@Slf4j
@CurrentTenant
@Tag(name = "Investor Information", description = "APIs for collecting investor KYC information")
public class InvestorInformationController {

    private final InvestorInformationService informationService;
    private final ClientService clientService;

    @PostMapping("/personal")
    @Operation(summary = "Submit personal information", description = "Submit personal details for current investor")
    public ResponseEntity<Map<String, String>> submitPersonalInformation(
            @Valid @RequestBody PersonalInformationDto dto,
            Authentication authentication) {
        log.info("Submit personal information request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            informationService.submitPersonalInformation(uniqueCode, dto);
            return ResponseEntity.ok(Map.of("message", "Personal information submitted successfully"));
        } catch (Exception e) {
            log.error("Error submitting personal information: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/passport")
    @Operation(summary = "Submit passport information", description = "Submit passport details for current investor")
    public ResponseEntity<Map<String, String>> submitPassportInformation(
            @Valid @RequestBody PassportInformationDto dto,
            Authentication authentication) {
        log.info("Submit passport information request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            informationService.submitPassportInformation(uniqueCode, dto);
            return ResponseEntity.ok(Map.of("message", "Passport information submitted successfully"));
        } catch (Exception e) {
            log.error("Error submitting passport information: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/residential")
    @Operation(summary = "Submit residential status", description = "Submit residential address for current investor")
    public ResponseEntity<Map<String, String>> submitResidentialStatus(
            @Valid @RequestBody ResidentialStatusDto dto,
            Authentication authentication) {
        log.info("Submit residential status request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            informationService.submitResidentialStatus(uniqueCode, dto);
            return ResponseEntity.ok(Map.of("message", "Residential status submitted successfully"));
        } catch (Exception e) {
            log.error("Error submitting residential status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/tax")
    @Operation(summary = "Submit tax information", description = "Submit tax details for current investor")
    public ResponseEntity<Map<String, String>> submitTaxInformation(
            @Valid @RequestBody TaxInformationDto dto,
            Authentication authentication) {
        log.info("Submit tax information request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            informationService.submitTaxInformation(uniqueCode, dto);
            return ResponseEntity.ok(Map.of("message", "Tax information submitted successfully"));
        } catch (Exception e) {
            log.error("Error submitting tax information: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/bank")
    @Operation(summary = "Submit bank details", description = "Submit bank account information for current investor")
    public ResponseEntity<Map<String, String>> submitBankDetails(
            @Valid @RequestBody BankDetailsDto dto,
            Authentication authentication) {
        log.info("Submit bank details request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            informationService.submitBankDetails(uniqueCode, dto);
            return ResponseEntity.ok(Map.of("message", "Bank details submitted successfully"));
        } catch (Exception e) {
            log.error("Error submitting bank details: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/contact")
    @Operation(summary = "Submit contact details", description = "Submit contact preferences for current investor")
    public ResponseEntity<Map<String, String>> submitContactDetails(
            @Valid @RequestBody ContactDetailsDto dto,
            Authentication authentication) {
        log.info("Submit contact details request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            informationService.submitContactDetails(uniqueCode, dto);
            return ResponseEntity.ok(Map.of("message", "Contact details submitted successfully"));
        } catch (Exception e) {
            log.error("Error submitting contact details: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/nomination")
    @Operation(summary = "Submit nomination details", description = "Submit beneficiary nomination for current investor")
    public ResponseEntity<Map<String, String>> submitNominationDetails(
            @Valid @RequestBody NominationDetailsDto dto,
            Authentication authentication) {
        log.info("Submit nomination details request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            informationService.submitNominationDetails(uniqueCode, dto);
            return ResponseEntity.ok(Map.of("message", "Nomination details submitted successfully"));
        } catch (Exception e) {
            log.error("Error submitting nomination details: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/risk-profile")
    @Operation(summary = "Submit risk profile", description = "Submit investment risk assessment for current investor")
    public ResponseEntity<Map<String, String>> submitRiskProfile(
            @Valid @RequestBody RiskProfileDto dto,
            Authentication authentication) {
        log.info("Submit risk profile request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            informationService.submitRiskProfile(uniqueCode, dto);
            return ResponseEntity.ok(Map.of("message", "Risk profile submitted successfully"));
        } catch (Exception e) {
            log.error("Error submitting risk profile: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/final-submit")
    @Operation(summary = "Final information submission", description = "Complete information collection phase")
    public ResponseEntity<Map<String, String>> finalSubmit(Authentication authentication) {
        log.info("Final information submission request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            informationService.finalSubmit(uniqueCode);
            return ResponseEntity.ok(Map.of("message", "Information collection completed successfully"));
        } catch (Exception e) {
            log.error("Error in final submission: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private String getInvestorUniqueCode(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        InvestorDto investor = clientService.getMyClientProfile(userPrincipal.getId());
        return investor.getUniqueCode();
    }
}
