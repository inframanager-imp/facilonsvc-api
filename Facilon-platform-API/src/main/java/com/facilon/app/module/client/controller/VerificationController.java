package com.facilon.app.module.client.controller;

import com.facilon.app.annotations.CurrentTenant;
import com.facilon.app.module.client.dto.*;
import com.facilon.app.module.client.service.ClientService;
import com.facilon.app.module.client.service.VerificationService;
import com.facilon.app.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Verification Controller - For managing investor verification process
 */
@RestController
@RequestMapping("/api/clients/me/verification")
@RequiredArgsConstructor
@Slf4j
@CurrentTenant
@Tag(name = "Investor Verification", description = "APIs for managing investor verification process")
public class VerificationController {

    private final VerificationService verificationService;
    private final ClientService clientService;

    @PostMapping("/physical-submission")
    @Operation(summary = "Submit physical documents", description = "Record physical document submission")
    public ResponseEntity<Map<String, String>> submitPhysicalDocuments(
            @Valid @RequestBody PhysicalSubmissionDto dto,
            Authentication authentication) {
        log.info("Physical submission request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            verificationService.submitPhysicalDocuments(uniqueCode, dto);
            return ResponseEntity.ok(Map.of("message", "Physical submission recorded successfully"));
        } catch (Exception e) {
            log.error("Error recording physical submission: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/appointment")
    @Operation(summary = "Schedule verification appointment", description = "Schedule an appointment for verification")
    public ResponseEntity<Map<String, String>> scheduleAppointment(
            @Valid @RequestBody VerificationAppointmentDto dto,
            Authentication authentication) {
        log.info("Schedule appointment request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            verificationService.scheduleVerificationAppointment(uniqueCode, dto);
            return ResponseEntity.ok(Map.of("message", "Appointment scheduled successfully"));
        } catch (Exception e) {
            log.error("Error scheduling appointment: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/appointment/{appointmentId}/status")
    @Operation(summary = "Update appointment status", description = "Update the status of a verification appointment")
    public ResponseEntity<Map<String, String>> updateAppointmentStatus(
            @PathVariable Long appointmentId,
            @RequestParam String status,
            Authentication authentication) {
        log.info("Update appointment status request, appointmentId: {}, status: {}", appointmentId, status);

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            verificationService.updateAppointmentStatus(uniqueCode, appointmentId, status);
            return ResponseEntity.ok(Map.of("message", "Appointment status updated successfully"));
        } catch (Exception e) {
            log.error("Error updating appointment status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/status")
    @Operation(summary = "Get verification status", description = "Get current verification status")
    public ResponseEntity<VerificationStatusDto> getVerificationStatus(Authentication authentication) {
        log.info("Get verification status request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            VerificationStatusDto status = verificationService.getVerificationStatus(uniqueCode);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Error fetching verification status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/document-checklist")
    @Operation(summary = "Download document checklist", description = "Get HTML checklist of documents to be submitted physically")
    public ResponseEntity<String> getDocumentChecklist(Authentication authentication) {
        log.info("Document checklist request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            String html = verificationService.generateDocumentChecklistHtml(uniqueCode);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .header("Content-Disposition", "inline; filename=document-checklist.html")
                    .body(html);
        } catch (Exception e) {
            log.error("Error generating document checklist: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_HTML)
                    .body("<html><body><h1>Error</h1><p>" + e.getMessage() + "</p></body></html>");
        }
    }

    private String getInvestorUniqueCode(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        InvestorDto investor = clientService.getMyClientProfile(userPrincipal.getId());
        return investor.getUniqueCode();
    }
}
