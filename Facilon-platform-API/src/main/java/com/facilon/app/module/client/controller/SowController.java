package com.facilon.app.module.client.controller;

import com.facilon.app.module.client.dto.*;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.module.client.service.SowService;
import com.facilon.app.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Statement of Work (SOW) management
 */
@Slf4j
@RestController
@RequestMapping("/api/investor/sow")
public class SowController {

    @Autowired
    private SowService sowService;

    @Autowired
    private InvestorRepository investorRepository;

    /** Resolve the current authenticated investor's id. */
    private Long currentInvestorId(Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        return investorRepository.findByAuthorizedUser_Id(userId)
                .map(Investor::getId)
                .orElseThrow(() -> new RuntimeException("Investor not found for current user"));
    }

    /** Whether the current investor has agreed to their SOW (drives the journey gate). */
    @GetMapping("/me/agreed")
    public ResponseEntity<?> myAgreed(Authentication auth) {
        try {
            boolean agreed = sowService.hasAgreedSow(currentInvestorId(auth));
            return ResponseEntity.ok(Map.of("agreed", agreed));
        } catch (Exception e) {
            log.error("Error checking SOW agreement", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** Record the current investor's agreement to the SOW. */
    @PostMapping("/me/agree")
    public ResponseEntity<?> myAgree(Authentication auth) {
        try {
            InvestorSowDto sow = sowService.recordAgreement(currentInvestorId(auth));
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Statement of Work agreed");
            response.put("agreed", true);
            response.put("sow", sow);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error recording SOW agreement", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** Revoke the current investor's SOW agreement (re-closes the journey gate). */
    @PostMapping("/me/revoke")
    public ResponseEntity<?> myRevoke(Authentication auth) {
        try {
            InvestorSowDto sow = sowService.revokeAgreement(currentInvestorId(auth));
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Statement of Work revoked");
            response.put("agreed", false);
            response.put("sow", sow);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error revoking SOW agreement", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get active SOW template for investor type
     */
    @GetMapping("/template")
    public ResponseEntity<?> getTemplate(@RequestParam(required = false, defaultValue = "All") String applicableFor) {
        try {
            SowTemplateDto template = sowService.getActiveTemplate(applicableFor);
            if (template == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "No active template found"));
            }
            return ResponseEntity.ok(template);
        } catch (Exception e) {
            log.error("Error getting template", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Create new SOW from template
     */
    @PostMapping("/create")
    public ResponseEntity<?> createSow(@Valid @RequestBody CreateSowDto dto) {
        try {
            InvestorSowDto sow = sowService.createSow(dto);
            return ResponseEntity.ok(sow);
        } catch (Exception e) {
            log.error("Error creating SOW", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update SOW (draft only)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSow(@PathVariable Long id, @Valid @RequestBody UpdateSowDto dto) {
        try {
            InvestorSowDto sow = sowService.updateSow(id, dto);
            return ResponseEntity.ok(sow);
        } catch (Exception e) {
            log.error("Error updating SOW", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Submit SOW for approval
     */
    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitSow(@PathVariable Long id) {
        try {
            InvestorSowDto sow = sowService.submitSow(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "SOW submitted for approval");
            response.put("sow", sow);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error submitting SOW", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get SOW by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getSow(@PathVariable Long id) {
        try {
            InvestorSowDto sow = sowService.getSow(id);
            if (sow == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "SOW not found"));
            }
            return ResponseEntity.ok(sow);
        } catch (Exception e) {
            log.error("Error getting SOW", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * List all SOWs for investor
     */
    @GetMapping("/list")
    public ResponseEntity<?> listSows(@RequestParam Long investorId) {
        try {
            List<InvestorSowDto> sows = sowService.listInvestorSows(investorId);
            return ResponseEntity.ok(sows);
        } catch (Exception e) {
            log.error("Error listing SOWs", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Approve SOW (admin)
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveSow(@PathVariable Long id, @RequestParam Long approvedBy) {
        try {
            InvestorSowDto sow = sowService.approveSow(id, approvedBy);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "SOW approved successfully");
            response.put("sow", sow);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error approving SOW", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Reject SOW (admin)
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectSow(@PathVariable Long id, @RequestParam String reason) {
        try {
            InvestorSowDto sow = sowService.rejectSow(id, reason);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "SOW rejected");
            response.put("sow", sow);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error rejecting SOW", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get SOW data for PDF generation
     */
    @GetMapping("/{id}/pdf-data")
    public ResponseEntity<?> getSowPdfData(@PathVariable Long id) {
        try {
            Map<String, Object> data = sowService.getSowDataForPdf(id);
            if (data == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "SOW not found"));
            }
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            log.error("Error getting SOW PDF data", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
