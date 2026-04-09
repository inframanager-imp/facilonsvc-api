package com.facilon.app.module.serviceagent.controller;

import com.facilon.app.annotations.CurrentTenant;
import com.facilon.app.module.client.dto.*;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.service.*;
import com.facilon.app.module.serviceagent.model.ServiceAgent;
import com.facilon.app.module.serviceagent.model.ServiceAgentAuditLog;
import com.facilon.app.module.serviceagent.service.ServiceAgentAccessControlService;
import com.facilon.app.module.serviceagent.service.ServiceAgentAuditService;
import com.facilon.app.module.serviceagent.service.ServiceAgentService;
import com.facilon.app.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

/**
 * Thin proxy controller: validates delegation + logs the action, then delegates
 * to existing investor service methods. No duplication of investor business logic.
 * All endpoints are under /api/service-agents/me/investors/{investorId}/...
 * so they are completely separate from existing investor routes.
 */
@RestController
@RequestMapping("/api/service-agents/me/investors/{investorId}")
@RequiredArgsConstructor
@Slf4j
@CurrentTenant
@Tag(name = "Service Agent – Investor Actions", description = "Service Agent acting on behalf of investor")
@PreAuthorize("hasAuthority('SA_VIEW_INVESTORS')")
public class ServiceAgentInvestorController {

    private final ServiceAgentService serviceAgentService;
    private final ServiceAgentAccessControlService accessControl;
    private final ServiceAgentAuditService auditService;

    // Existing investor services — reused, not duplicated
    private final ClientService clientService;
    private final ClientProfileService clientProfileService;
    private final ClientDocumentService clientDocumentService;
    private final ClientKycService clientKycService;
    private final InvestorProgressService investorProgressService;
    private final InvestorDocumentService investorDocumentService;
    private final VerificationService verificationService;

    // ── Dashboard ─────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    @Operation(summary = "View investor dashboard on behalf of investor")
    public ResponseEntity<InvestorDashboardDto> getDashboard(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "PROFILE", "can_view_profile");
        InvestorDto client = clientService.getClientById(investorId);
        InvestorDashboardDto dashboard = investorProgressService.getInvestorDashboard(
                client.getUniqueCode(), client.getEmailId());
        log(agent, investorId, "VIEW_DASHBOARD", "PROFILE", "Investor", investorId.toString(), request);
        return ResponseEntity.ok(dashboard);
    }

    // ── Profile ───────────────────────────────────────────────────────────────

    @GetMapping("/profile")
    @Operation(summary = "View investor profile")
    public ResponseEntity<InvestorDto> getProfile(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "PROFILE", "can_view_profile");
        InvestorDto profile = clientService.getClientById(investorId);
        log(agent, investorId, "VIEW_PROFILE", "PROFILE", "Investor", investorId.toString(), request);
        return ResponseEntity.ok(profile);
    }

    // ── Personal Information ───────────────────────────────────────────────────

    @GetMapping("/personal-info")
    @Operation(summary = "View investor personal information")
    public ResponseEntity<UserPersonalInformationDto> getPersonalInfo(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "KYC", "can_view_profile");
        Long userId = resolveUserId(investorId);
        UserPersonalInformationDto info = clientProfileService.getPersonalInfo(userId).orElse(null);
        log(agent, investorId, "VIEW_PERSONAL_INFO", "KYC", "PersonalInfo", investorId.toString(), request);
        return ResponseEntity.ok(info);
    }

    @PutMapping("/personal-info")
    @PreAuthorize("hasAuthority('SA_EDIT_INVESTOR_KYC')")
    @Operation(summary = "Edit investor personal information")
    public ResponseEntity<UserPersonalInformationDto> updatePersonalInfo(
            @PathVariable Long investorId,
            @RequestBody UserPersonalInformationDto dto,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "KYC", "can_edit_kyc");
        Long userId = resolveUserId(investorId);
        UserPersonalInformationDto updated = clientProfileService.updatePersonalInfo(userId, dto);
        log(agent, investorId, "EDIT_PERSONAL_INFO", "KYC", "PersonalInfo", investorId.toString(), request);
        return ResponseEntity.ok(updated);
    }

    // ── Contact Details ────────────────────────────────────────────────────────

    @GetMapping("/contact-details")
    @Operation(summary = "View investor contact details")
    public ResponseEntity<UserContactDetailsDto> getContactDetails(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "KYC", "can_view_profile");
        Long userId = resolveUserId(investorId);
        UserContactDetailsDto info = clientProfileService.getContactDetails(userId).orElse(null);
        log(agent, investorId, "VIEW_CONTACT_DETAILS", "KYC", "ContactDetails", investorId.toString(), request);
        return ResponseEntity.ok(info);
    }

    @PutMapping("/contact-details")
    @PreAuthorize("hasAuthority('SA_EDIT_INVESTOR_KYC')")
    @Operation(summary = "Edit investor contact details")
    public ResponseEntity<UserContactDetailsDto> updateContactDetails(
            @PathVariable Long investorId,
            @RequestBody UserContactDetailsDto dto,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "KYC", "can_edit_kyc");
        Long userId = resolveUserId(investorId);
        UserContactDetailsDto updated = clientProfileService.updateContactDetails(userId, dto);
        log(agent, investorId, "EDIT_CONTACT_DETAILS", "KYC", "ContactDetails", investorId.toString(), request);
        return ResponseEntity.ok(updated);
    }

    // ── Passport ──────────────────────────────────────────────────────────────

    @GetMapping("/passport")
    @Operation(summary = "View investor passport details")
    public ResponseEntity<UserPassportDetailsDto> getPassport(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "KYC", "can_view_profile");
        Long userId = resolveUserId(investorId);
        UserPassportDetailsDto info = clientProfileService.getPassport(userId).orElse(null);
        log(agent, investorId, "VIEW_PASSPORT", "KYC", "Passport", investorId.toString(), request);
        return ResponseEntity.ok(info);
    }

    @PutMapping("/passport")
    @PreAuthorize("hasAuthority('SA_EDIT_INVESTOR_KYC')")
    @Operation(summary = "Edit investor passport details")
    public ResponseEntity<UserPassportDetailsDto> updatePassport(
            @PathVariable Long investorId,
            @RequestBody UserPassportDetailsDto dto,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "KYC", "can_edit_kyc");
        Long userId = resolveUserId(investorId);
        UserPassportDetailsDto updated = clientProfileService.updatePassport(userId, dto);
        log(agent, investorId, "EDIT_PASSPORT", "KYC", "Passport", investorId.toString(), request);
        return ResponseEntity.ok(updated);
    }

    // ── Investment experience ───────────────────────────────────────────────────

    @GetMapping("/experience")
    @Operation(summary = "View investor investment experience")
    public ResponseEntity<InvestorExperienceDto> getExperience(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "KYC", "can_view_profile");
        Long userId = resolveUserId(investorId);
        return clientProfileService.getExperience(userId)
                .map(dto -> {
                    log(agent, investorId, "VIEW_EXPERIENCE", "KYC", "Experience", investorId.toString(), request);
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> {
                    log(agent, investorId, "VIEW_EXPERIENCE", "KYC", "Experience", investorId.toString(), request);
                    return ResponseEntity.noContent().build();
                });
    }

    @PutMapping("/experience")
    @PreAuthorize("hasAuthority('SA_EDIT_INVESTOR_KYC')")
    @Operation(summary = "Update investor investment experience")
    public ResponseEntity<InvestorExperienceDto> updateExperience(
            @PathVariable Long investorId,
            @Valid @RequestBody InvestorExperienceDto dto,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "KYC", "can_edit_kyc");
        Long userId = resolveUserId(investorId);
        InvestorExperienceDto updated = clientProfileService.updateExperience(userId, dto);
        log(agent, investorId, "EDIT_EXPERIENCE", "KYC", "Experience", investorId.toString(), request);
        return ResponseEntity.ok(updated);
    }

    // ── Consents ───────────────────────────────────────────────────────────────

    @GetMapping("/consents")
    @Operation(summary = "View investor consents")
    public ResponseEntity<InvestorConsentsDto> getConsents(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "KYC", "can_view_profile");
        Long userId = resolveUserId(investorId);
        return clientProfileService.getConsents(userId)
                .map(dto -> {
                    log(agent, investorId, "VIEW_CONSENTS", "KYC", "Consents", investorId.toString(), request);
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> {
                    log(agent, investorId, "VIEW_CONSENTS", "KYC", "Consents", investorId.toString(), request);
                    return ResponseEntity.noContent().build();
                });
    }

    @PostMapping("/consents")
    @PreAuthorize("hasAuthority('SA_EDIT_INVESTOR_KYC')")
    @Operation(summary = "Record or update investor consents on behalf of investor")
    public ResponseEntity<InvestorConsentsDto> recordConsent(
            @PathVariable Long investorId,
            @Valid @RequestBody InvestorConsentsDto dto,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "KYC", "can_edit_kyc");
        Long userId = resolveUserId(investorId);
        InvestorConsentsDto updated = clientProfileService.recordConsent(userId, dto);
        log(agent, investorId, "RECORD_CONSENTS", "KYC", "Consents", investorId.toString(), request);
        return ResponseEntity.ok(updated);
    }

    // ── Residential status ─────────────────────────────────────────────────────

    @GetMapping("/residential")
    @Operation(summary = "View investor residential status")
    public ResponseEntity<UserResidentialStatusDto> getResidentialStatus(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "KYC", "can_view_profile");
        Long userId = resolveUserId(investorId);
        return clientProfileService.getResidentialStatus(userId)
                .map(dto -> {
                    log(agent, investorId, "VIEW_RESIDENTIAL", "KYC", "Residential", investorId.toString(), request);
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> {
                    log(agent, investorId, "VIEW_RESIDENTIAL", "KYC", "Residential", investorId.toString(), request);
                    return ResponseEntity.noContent().build();
                });
    }

    @PutMapping("/residential")
    @PreAuthorize("hasAuthority('SA_EDIT_INVESTOR_KYC')")
    @Operation(summary = "Update investor residential status")
    public ResponseEntity<UserResidentialStatusDto> updateResidentialStatus(
            @PathVariable Long investorId,
            @Valid @RequestBody UserResidentialStatusDto dto,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "KYC", "can_edit_kyc");
        Long userId = resolveUserId(investorId);
        UserResidentialStatusDto updated = clientProfileService.updateResidentialStatus(userId, dto);
        log(agent, investorId, "EDIT_RESIDENTIAL", "KYC", "Residential", investorId.toString(), request);
        return ResponseEntity.ok(updated);
    }

    // ── Tax Information ────────────────────────────────────────────────────────

    @GetMapping("/tax-info")
    @Operation(summary = "View investor tax information")
    public ResponseEntity<UserTaxInfoDto> getTaxInfo(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "KYC", "can_view_profile");
        Long userId = resolveUserId(investorId);
        UserTaxInfoDto info = clientProfileService.getTaxInfo(userId).orElse(null);
        log(agent, investorId, "VIEW_TAX_INFO", "KYC", "TaxInfo", investorId.toString(), request);
        return ResponseEntity.ok(info);
    }

    @PutMapping("/tax-info")
    @PreAuthorize("hasAuthority('SA_EDIT_INVESTOR_KYC')")
    @Operation(summary = "Edit investor tax information")
    public ResponseEntity<UserTaxInfoDto> updateTaxInfo(
            @PathVariable Long investorId,
            @RequestBody UserTaxInfoDto dto,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "KYC", "can_edit_kyc");
        Long userId = resolveUserId(investorId);
        UserTaxInfoDto updated = clientProfileService.updateTaxInfo(userId, dto);
        log(agent, investorId, "EDIT_TAX_INFO", "KYC", "TaxInfo", investorId.toString(), request);
        return ResponseEntity.ok(updated);
    }

    // ── Bank Details ──────────────────────────────────────────────────────────

    @GetMapping("/bank-details")
    @Operation(summary = "View investor bank details")
    public ResponseEntity<UserBankDetailsDto> getBankDetails(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "ONBOARDING", "can_view_profile");
        Long userId = resolveUserId(investorId);
        UserBankDetailsDto info = clientProfileService.getBankDetails(userId).orElse(null);
        log(agent, investorId, "VIEW_BANK_DETAILS", "ONBOARDING", "BankDetails", investorId.toString(), request);
        return ResponseEntity.ok(info);
    }

    @PutMapping("/bank-details")
    @PreAuthorize("hasAuthority('SA_EDIT_INVESTOR_KYC')")
    @Operation(summary = "Update investor bank details")
    public ResponseEntity<UserBankDetailsDto> updateBankDetails(
            @PathVariable Long investorId,
            @RequestBody UserBankDetailsDto dto,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "ONBOARDING", "can_edit_kyc");
        Long userId = resolveUserId(investorId);
        UserBankDetailsDto updated = clientProfileService.updateBankDetails(userId, dto);
        log(agent, investorId, "EDIT_BANK_DETAILS", "ONBOARDING", "BankDetails", investorId.toString(), request);
        return ResponseEntity.ok(updated);
    }

    // ── Nomination ────────────────────────────────────────────────────────────

    @GetMapping("/nomination")
    @Operation(summary = "View investor nomination details")
    public ResponseEntity<UserNominationDto> getNomination(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "ONBOARDING", "can_view_profile");
        Long userId = resolveUserId(investorId);
        UserNominationDto info = clientProfileService.getNomination(userId).orElse(null);
        log(agent, investorId, "VIEW_NOMINATION", "ONBOARDING", "Nomination", investorId.toString(), request);
        return ResponseEntity.ok(info);
    }

    @PutMapping("/nomination")
    @PreAuthorize("hasAuthority('SA_EDIT_INVESTOR_KYC')")
    @Operation(summary = "Update investor nomination details")
    public ResponseEntity<UserNominationDto> updateNomination(
            @PathVariable Long investorId,
            @RequestBody UserNominationDto dto,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "ONBOARDING", "can_edit_kyc");
        Long userId = resolveUserId(investorId);
        UserNominationDto updated = clientProfileService.updateNomination(userId, dto);
        log(agent, investorId, "EDIT_NOMINATION", "ONBOARDING", "Nomination", investorId.toString(), request);
        return ResponseEntity.ok(updated);
    }

    // ── Risk Profile ───────────────────────────────────────────────────────────

    @GetMapping("/risk-profile")
    @Operation(summary = "View investor risk profile")
    public ResponseEntity<UserRiskProfileDto> getRiskProfile(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "ONBOARDING", "can_view_profile");
        Long userId = resolveUserId(investorId);
        UserRiskProfileDto info = clientProfileService.getRiskProfile(userId).orElse(null);
        log(agent, investorId, "VIEW_RISK_PROFILE", "ONBOARDING", "RiskProfile", investorId.toString(), request);
        return ResponseEntity.ok(info);
    }

    @PutMapping("/risk-profile")
    @PreAuthorize("hasAuthority('SA_EDIT_INVESTOR_KYC')")
    @Operation(summary = "Edit investor risk profile")
    public ResponseEntity<UserRiskProfileDto> updateRiskProfile(
            @PathVariable Long investorId,
            @RequestBody UserRiskProfileDto dto,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "ONBOARDING", "can_edit_kyc");
        Long userId = resolveUserId(investorId);
        UserRiskProfileDto updated = clientProfileService.updateRiskProfile(userId, dto);
        log(agent, investorId, "EDIT_RISK_PROFILE", "ONBOARDING", "RiskProfile", investorId.toString(), request);
        return ResponseEntity.ok(updated);
    }

    // ── Onboarding ────────────────────────────────────────────────────────────

    @GetMapping({"/onboarding", "/onboarding-status"})
    @Operation(summary = "View investor onboarding status")
    public ResponseEntity<OnboardingStatusDto> getOnboarding(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "ONBOARDING", "can_view_profile");
        Long userId = resolveUserId(investorId);
        OnboardingStatusDto status = clientProfileService.getOnboardingStatus(userId);
        log(agent, investorId, "VIEW_ONBOARDING", "ONBOARDING", "Onboarding", investorId.toString(), request);
        return ResponseEntity.ok(status);
    }

    @PostMapping({"/onboarding/submit", "/final-submit"})
    @PreAuthorize("hasAuthority('SA_SUBMIT_FORMS')")
    @Operation(summary = "Submit investor onboarding on behalf of investor")
    public ResponseEntity<OnboardingStatusDto> submitOnboarding(
            @PathVariable Long investorId,
            @RequestBody InvestorConsentsDto consentsDto,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "ONBOARDING", "can_submit_forms");
        Long userId = resolveUserId(investorId);
        OnboardingStatusDto result = clientProfileService.finalSubmit(userId, consentsDto);
        log(agent, investorId, "SUBMIT_ONBOARDING", "ONBOARDING", "Onboarding", investorId.toString(), request);
        return ResponseEntity.ok(result);
    }

    // ── KYC Documents ─────────────────────────────────────────────────────────

    @GetMapping("/documents/kyc/requirements")
    @Operation(summary = "Get KYC document requirements for investor")
    public ResponseEntity<KycDocumentRequirementDto> getKycRequirements(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "KYC", "can_view_profile");
        String uniqueCode = resolveUniqueCode(investorId);
        KycDocumentRequirementDto requirements = investorDocumentService.getKycRequirements(uniqueCode);
        log(agent, investorId, "VIEW_KYC_REQUIREMENTS", "KYC", "KycRequirements", investorId.toString(), request);
        return ResponseEntity.ok(requirements);
    }

    @GetMapping("/documents/onboarding/requirements")
    @Operation(summary = "Get onboarding document requirements for investor")
    public ResponseEntity<KycDocumentRequirementDto> getOnboardingRequirements(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "ONBOARDING", "can_view_profile");
        String uniqueCode = resolveUniqueCode(investorId);
        KycDocumentRequirementDto requirements = investorDocumentService.getOnboardingRequirements(uniqueCode);
        log(agent, investorId, "VIEW_ONBOARDING_REQUIREMENTS", "ONBOARDING", "OnboardingRequirements", investorId.toString(), request);
        return ResponseEntity.ok(requirements);
    }

    @PostMapping(value = "/documents/kyc", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('SA_UPLOAD_DOCUMENTS')")
    @Operation(summary = "Upload KYC document on behalf of investor")
    public ResponseEntity<?> uploadKycDocument(
            @PathVariable Long investorId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "KYC", "can_upload_documents");
        String uniqueCode = resolveUniqueCode(investorId);
        
        try {
            DocumentResponseDto doc = investorDocumentService.uploadKycDocument(uniqueCode, file, documentType);
            log(agent, investorId, "UPLOAD_KYC_DOCUMENT", "KYC", "KycDocument", documentType, request);
            return ResponseEntity.ok(doc);
        } catch (IllegalArgumentException e) {
            log.warn("KYC upload validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error uploading KYC document: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Upload failed"));
        }
    }

    @GetMapping("/kyc/documents")
    @Operation(summary = "List investor KYC documents")
    public ResponseEntity<List<KycDocumentsDto>> getKycDocuments(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "KYC", "can_view_profile");
        List<KycDocumentsDto> docs = clientKycService.getDocumentsByClientId(investorId);
        log(agent, investorId, "VIEW_KYC_DOCUMENTS", "KYC", "KycDocument", investorId.toString(), request);
        return ResponseEntity.ok(docs);
    }

    // ── General Documents ─────────────────────────────────────────────────────

    @GetMapping("/documents")
    @Operation(summary = "List all investor documents")
    public ResponseEntity<List<DocumentResponseDto>> listDocuments(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "DOCUMENT", "can_view_profile");
        String uniqueCode = resolveUniqueCode(investorId);
        
        try {
            List<DocumentResponseDto> docs = investorDocumentService.getInvestorDocuments(uniqueCode);
            log(agent, investorId, "LIST_DOCUMENTS", "DOCUMENT", "Document", investorId.toString(), request);
            return ResponseEntity.ok(docs);
        } catch (Exception e) {
            log.error("Error fetching documents: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/documents/kyc")
    @Operation(summary = "List investor KYC documents only")
    public ResponseEntity<List<DocumentResponseDto>> listKycDocuments(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "KYC", "can_view_profile");
        String uniqueCode = resolveUniqueCode(investorId);
        
        try {
            List<DocumentResponseDto> docs = investorDocumentService.getKycDocuments(uniqueCode);
            log(agent, investorId, "LIST_KYC_DOCUMENTS", "KYC", "KycDocument", investorId.toString(), request);
            return ResponseEntity.ok(docs);
        } catch (Exception e) {
            log.error("Error fetching KYC documents: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/documents/onboarding/list")
    @Operation(summary = "List investor onboarding documents")
    public ResponseEntity<List<DocumentResponseDto>> listOnboardingDocuments(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "ONBOARDING", "can_view_profile");
        String uniqueCode = resolveUniqueCode(investorId);
        
        try {
            List<DocumentResponseDto> docs = investorDocumentService.getOnboardingDocuments(uniqueCode);
            log(agent, investorId, "LIST_ONBOARDING_DOCUMENTS", "ONBOARDING", "OnboardingDocument", investorId.toString(), request);
            return ResponseEntity.ok(docs);
        } catch (Exception e) {
            log.error("Error fetching onboarding documents: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/documents/onboarding/forms/download")
    @Operation(summary = "Download pre-filled onboarding forms for investor")
    public ResponseEntity<Map<String, Object>> downloadPreFilledForms(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "ONBOARDING", "can_view_profile");
        log(agent, investorId, "DOWNLOAD_PREFILLED_FORMS", "ONBOARDING", "OnboardingForms", investorId.toString(), request);
        
        try {
            // Return available form templates (matching InvestorDocumentController pattern)
            Map<String, Object> forms = Map.of(
                    "message", "Pre-filled forms generation in progress",
                    "availableForms", List.of(
                            Map.of("name", "Account Opening Form", "url", "/forms/account-opening.pdf"),
                            Map.of("name", "Risk Disclosure Form", "url", "/forms/risk-disclosure.pdf"),
                            Map.of("name", "Trading Agreement", "url", "/forms/trading-agreement.pdf")
                    )
            );
            return ResponseEntity.ok(forms);
        } catch (Exception e) {
            log.error("Error downloading pre-filled forms: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/documents/{documentId}/status")
    @Operation(summary = "Get document status")
    public ResponseEntity<DocumentResponseDto> getDocumentStatus(
            @PathVariable Long investorId,
            @PathVariable Long documentId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "DOCUMENT", "can_view_profile");
        String uniqueCode = resolveUniqueCode(investorId);
        
        try {
            DocumentResponseDto document = investorDocumentService.getDocumentStatus(uniqueCode, documentId);
            log(agent, investorId, "VIEW_DOCUMENT_STATUS", "DOCUMENT", "Document", documentId.toString(), request);
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            log.error("Error fetching document status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/documents/{documentId}")
    @PreAuthorize("hasAuthority('SA_UPLOAD_DOCUMENTS')")
    @Operation(summary = "Delete investor document")
    public ResponseEntity<Map<String, String>> deleteDocument(
            @PathVariable Long investorId,
            @PathVariable Long documentId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "DOCUMENT", "can_upload_documents");
        String uniqueCode = resolveUniqueCode(investorId);
        
        try {
            investorDocumentService.deleteDocument(uniqueCode, documentId);
            log(agent, investorId, "DELETE_DOCUMENT", "DOCUMENT", "Document", documentId.toString(), request);
            return ResponseEntity.ok(Map.of("message", "Document deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting document: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/documents")
    @PreAuthorize("hasAuthority('SA_UPLOAD_DOCUMENTS')")
    @Operation(summary = "Upload a document on behalf of investor")
    public ResponseEntity<DocumentUploadResponseDto> uploadDocument(
            @PathVariable Long investorId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "DOCUMENT", "can_upload_documents");
        String uniqueCode = resolveUniqueCode(investorId);
        DocumentUploadResponseDto doc = clientDocumentService.uploadDocument(uniqueCode, documentType, file);
        log(agent, investorId, "UPLOAD_DOCUMENT", "DOCUMENT", "Document", documentType, request);
        return ResponseEntity.ok(doc);
    }

    @PostMapping(value = "/documents/onboarding", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('SA_UPLOAD_DOCUMENTS')")
    @Operation(summary = "Upload onboarding document on behalf of investor")
    public ResponseEntity<DocumentResponseDto> uploadOnboardingDocument(
            @PathVariable Long investorId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            @RequestParam(value = "investorDocumentId", required = false) String investorDocumentId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "ONBOARDING", "can_upload_documents");
        String uniqueCode = resolveUniqueCode(investorId);
        
        try {
            DocumentResponseDto doc = investorDocumentService.uploadOnboardingDocument(uniqueCode, file, documentType, investorDocumentId);
            log(agent, investorId, "UPLOAD_ONBOARDING_DOCUMENT", "ONBOARDING", "OnboardingDocument", documentType, request);
            return ResponseEntity.ok(doc);
        } catch (Exception e) {
            log.error("Error uploading onboarding document: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    // ── Onboarding Progress ───────────────────────────────────────────────────

    @GetMapping("/progress")
    @Operation(summary = "View investor onboarding progress")
    public ResponseEntity<InvestorProgressDto> getProgress(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "PROFILE", "can_view_profile");
        String uniqueCode = resolveUniqueCode(investorId);
        InvestorProgressDto progress = investorProgressService.getInvestorProgress(uniqueCode);
        log(agent, investorId, "VIEW_PROGRESS", "PROFILE", "Progress", investorId.toString(), request);
        return ResponseEntity.ok(progress);
    }

    // ── Physical Submission ───────────────────────────────────────────────────

    @GetMapping("/physical-submission")
    @Operation(summary = "View investor physical submission status")
    public ResponseEntity<OnboardingStatusDto> getPhysicalSubmission(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "ONBOARDING", "can_view_profile");
        Long userId = resolveUserId(investorId);
        OnboardingStatusDto status = clientProfileService.getOnboardingStatus(userId);
        log(agent, investorId, "VIEW_PHYSICAL_SUBMISSION", "ONBOARDING", "PhysicalSubmission", investorId.toString(), request);
        return ResponseEntity.ok(status);
    }

    // ── Verification (In-Person) ──────────────────────────────────────────────

    @GetMapping("/verification/status")
    @Operation(summary = "View investor verification status")
    public ResponseEntity<VerificationStatusDto> getVerificationStatus(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "PROFILE", "can_view_profile");
        String uniqueCode = resolveUniqueCode(investorId);
        VerificationStatusDto status = verificationService.getVerificationStatus(uniqueCode);
        log(agent, investorId, "VIEW_VERIFICATION_STATUS", "PROFILE", "Verification", investorId.toString(), request);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/verification/appointment")
    @Operation(summary = "Schedule verification appointment for investor")
    public ResponseEntity<Void> scheduleVerification(
            @PathVariable Long investorId,
            @RequestBody @Valid VerificationAppointmentDto appointmentDto,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "ONBOARDING", "can_submit_forms");
        String uniqueCode = resolveUniqueCode(investorId);
        
        try {
            verificationService.scheduleVerificationAppointment(uniqueCode, appointmentDto);
            log(agent, investorId, "SCHEDULE_VERIFICATION", "ONBOARDING", "VerificationAppointment", 
                appointmentDto.getAppointmentDate() + " " + appointmentDto.getAppointmentTime(), request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error scheduling appointment: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/verification/appointment/{appointmentId}/status")
    @Operation(summary = "Update verification appointment status")
    public ResponseEntity<Void> updateAppointmentStatus(
            @PathVariable Long investorId,
            @PathVariable Long appointmentId,
            @RequestParam("status") String status,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "ONBOARDING", "can_submit_forms");
        String uniqueCode = resolveUniqueCode(investorId);
        
        try {
            verificationService.updateAppointmentStatus(uniqueCode, appointmentId, status);
            log(agent, investorId, "UPDATE_APPOINTMENT_STATUS", "ONBOARDING", "VerificationAppointment", 
                appointmentId.toString(), request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error updating appointment status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/verification/physical-submission")
    @Operation(summary = "Submit physical documents verification")
    public ResponseEntity<Void> submitPhysicalDocuments(
            @PathVariable Long investorId,
            @RequestBody PhysicalSubmissionDto submissionDto,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "ONBOARDING", "can_submit_forms");
        String uniqueCode = resolveUniqueCode(investorId);
        
        try {
            verificationService.submitPhysicalDocuments(uniqueCode, submissionDto);
            log(agent, investorId, "SUBMIT_PHYSICAL_DOCS", "ONBOARDING", "PhysicalSubmission", 
                submissionDto.getPhysicalSubmission(), request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error submitting physical documents: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    // ── Account Details ───────────────────────────────────────────────────────

    @GetMapping("/account-details")
    @Operation(summary = "View investor account details")
    public ResponseEntity<AccountDetailsDto> getAccountDetails(
            @PathVariable Long investorId,
            Authentication auth, HttpServletRequest request) {
        ServiceAgent agent = resolveAgent(auth);
        accessControl.validateAction(agent.getId(), investorId, "PROFILE", "can_view_profile");
        String uniqueCode = resolveUniqueCode(investorId);
        
        try {
            AccountDetailsDto accountDetails = investorProgressService.getAccountDetails(uniqueCode);
            log(agent, investorId, "VIEW_ACCOUNT_DETAILS", "PROFILE", "AccountDetails", investorId.toString(), request);
            return ResponseEntity.ok(accountDetails);
        } catch (Exception e) {
            log.error("Error fetching account details: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private String resolveUniqueCode(Long investorId) {
        return clientService.getClientById(investorId).getUniqueCode();
    }

    private Long resolveUserId(Long investorId) {
        Investor investor = clientProfileService.getInvestorForAdmin(investorId);
        return investor.getAuthorizedUser().getId();
    }

    private ServiceAgent resolveAgent(Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        return serviceAgentService.ensureProfileExists(principal.getId());
    }

    private void log(ServiceAgent agent, Long investorId,
                     String actionType, String actionCategory,
                     String entityType, String entityId,
                     HttpServletRequest request) {
        auditService.logAction(ServiceAgentAuditLog.builder()
                .serviceAgentId(agent.getId())
                .investorId(investorId)
                .actionType(actionType)
                .actionCategory(actionCategory)
                .entityType(entityType)
                .entityId(entityId)
                .endpoint(request.getRequestURI())
                .httpMethod(request.getMethod())
                .success(true)
                .ipAddress(getClientIp(request))
                .userAgent(request.getHeader("User-Agent"))
                .build());
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null && !forwarded.isBlank())
                ? forwarded.split(",")[0].trim()
                : request.getRemoteAddr();
    }
}
