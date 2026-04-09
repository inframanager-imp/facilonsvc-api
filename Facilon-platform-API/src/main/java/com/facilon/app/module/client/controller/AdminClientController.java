package com.facilon.app.module.client.controller;

import com.facilon.app.annotations.CurrentTenant;
import com.facilon.app.module.client.dto.*;
import com.facilon.app.module.client.dto.onboarding.BatchRegistrationDto;
import com.facilon.app.module.client.dto.onboarding.BatchRegistrationResponseDto;
import com.facilon.app.module.client.service.BatchRegistrationService;
import com.facilon.app.module.client.service.ClientKycService;
import com.facilon.app.module.client.service.ClientProfileService;
import com.facilon.app.module.client.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin Client Controller - For tenant admins to manage clients/investors in their tenant.
 * Requires ADMIN role.
 * Tenant-scoped endpoints.
 */
@RestController
@RequestMapping("/api/admin/clients")
@RequiredArgsConstructor
@Slf4j
@CurrentTenant
@PreAuthorize("hasAnyRole('ADMIN', 'PLATFORM_SUPER_ADMIN')")
@Tag(name = "Admin - Client Management", description = "Admin APIs for managing clients/investors")
public class AdminClientController {

    private final ClientService clientService;
    private final BatchRegistrationService batchRegistrationService;
    private final ClientProfileService profileService;
    private final ClientKycService kycService;
    private final com.facilon.app.module.client.service.InvestorNotificationService notificationService;

    /**
     * Batch register multiple investors.
     */
    @PostMapping("/register-batch")
    @Operation(summary = "Batch register investors", description = "Register multiple investors at once (Broker/Admin)")
    public ResponseEntity<BatchRegistrationResponseDto> registerBatch(
            @Valid @RequestBody BatchRegistrationDto dto) {
        log.info("Batch registration request: {} investors, brokerCode={}", dto.getInvestors().size(), dto.getBrokerCode());
        BatchRegistrationResponseDto response = batchRegistrationService.registerBatch(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * List all clients/investors in current tenant.
     */
    @GetMapping
    @Operation(summary = "List all clients", description = "Get all clients/investors in current tenant with pagination")
    public ResponseEntity<Page<InvestorDto>> listClients(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        log.info("List clients request");
        
        Page<InvestorDto> clients = clientService.listClients(pageable);
        return ResponseEntity.ok(clients);
    }

    /**
     * Search clients/investors in current tenant.
     */
    @GetMapping("/search")
    @Operation(summary = "Search clients", description = "Search clients/investors by name, email, or unique code")
    public ResponseEntity<Page<InvestorDto>> searchClients(
            @Parameter(description = "Search term") @RequestParam String searchTerm,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        log.info("Search clients: {}", searchTerm);
        
        Page<InvestorDto> clients = clientService.searchClients(searchTerm, pageable);
        return ResponseEntity.ok(clients);
    }

    /**
     * Get client by ID.
     */
    @GetMapping("/{clientId}")
    @Operation(summary = "Get client by ID", description = "Get client/investor details by ID")
    public ResponseEntity<InvestorDto> getClient(
            @Parameter(description = "Client ID") @PathVariable Long clientId) {
        log.info("Get client request for ID: {}", clientId);
        
        try {
            InvestorDto client = clientService.getClientById(clientId);
            return ResponseEntity.ok(client);
        } catch (RuntimeException e) {
            log.error("Error getting client: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Create client for a user.
     */
    @PostMapping
    @Operation(summary = "Create client", description = "Create client/investor profile for a user")
    public ResponseEntity<InvestorDto> createClient(
            @Valid @RequestBody InvestorCreateDto dto) {
        log.info("Admin create client request for user ID: {}", dto.getAuthorizedUserId());
        
        try {
            InvestorDto client = clientService.createClient(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(client);
        } catch (RuntimeException e) {
            log.error("Error creating client: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Update client information.
     */
    @PutMapping("/{clientId}")
    @Operation(summary = "Update client", description = "Update client/investor information")
    public ResponseEntity<InvestorDto> updateClient(
            @Parameter(description = "Client ID") @PathVariable Long clientId,
            @Valid @RequestBody InvestorUpdateDto dto) {
        log.info("Update client request for ID: {}", clientId);
        
        try {
            InvestorDto client = clientService.updateClient(clientId, dto);
            return ResponseEntity.ok(client);
        } catch (RuntimeException e) {
            log.error("Error updating client: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Update client verification status.
     */
    @PutMapping("/{clientId}/verify")
    @Operation(summary = "Update verification status", description = "Update client/investor verification status (verify/reject)")
    public ResponseEntity<InvestorDto> updateVerificationStatus(
            @Parameter(description = "Client ID") @PathVariable Long clientId,
            @Valid @RequestBody InvestorVerificationDto dto) {
        log.info("Update client {} verification status to: {}", clientId, dto.getVerifyStatus());
        
        try {
            InvestorDto client = clientService.updateVerificationStatus(clientId, dto);
            return ResponseEntity.ok(client);
        } catch (RuntimeException e) {
            log.error("Error updating verification status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get client by user ID.
     */
    @GetMapping("/by-user/{userId}")
    @Operation(summary = "Get client by user ID", description = "Get client/investor by authorized user ID")
    public ResponseEntity<InvestorDto> getClientByUserId(
            @Parameter(description = "Authorized User ID") @PathVariable Long userId) {
        log.info("Get client by user ID: {}", userId);
        
        try {
            InvestorDto client = clientService.getClientByUserId(userId);
            return ResponseEntity.ok(client);
        } catch (RuntimeException e) {
            log.error("Error getting client by user ID: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Get full investor profile (personal info, passport, experience, consents).
     */
    @GetMapping("/{clientId}/profile")
    @Operation(summary = "Get client full profile", description = "Get investor profile details for admin view")
    public ResponseEntity<InvestorFullProfileDto> getClientProfile(
            @Parameter(description = "Client ID") @PathVariable Long clientId) {
        log.info("Get client profile for ID: {}", clientId);
        try {
            InvestorFullProfileDto profile = profileService.getFullProfileForAdmin(clientId);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            log.error("Error getting client profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * List KYC documents for a client.
     */
    @GetMapping("/{clientId}/documents")
    @Operation(summary = "List client KYC documents", description = "Get all KYC documents for an investor")
    public ResponseEntity<List<KycDocumentsDto>> getClientDocuments(
            @Parameter(description = "Client ID") @PathVariable Long clientId) {
        log.info("Get KYC documents for client ID: {}", clientId);
        try {
            return ResponseEntity.ok(kycService.getDocumentsByClientId(clientId));
        } catch (RuntimeException e) {
            log.error("Error getting client documents: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Verify a KYC document.
     */
    @PutMapping("/documents/{documentId}/verify")
    @Operation(summary = "Verify KYC document", description = "Approve a KYC document")
    public ResponseEntity<KycDocumentsDto> verifyDocument(
            @Parameter(description = "Document ID") @PathVariable Long documentId) {
        log.info("Admin verify document ID: {}", documentId);
        try {
            return ResponseEntity.ok(kycService.verifyDocument(documentId));
        } catch (RuntimeException e) {
            log.error("Error verifying document: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Reject a KYC document.
     */
    @PutMapping("/documents/{documentId}/reject")
    @Operation(summary = "Reject KYC document", description = "Reject a KYC document with reason")
    public ResponseEntity<KycDocumentsDto> rejectDocument(
            @Parameter(description = "Document ID") @PathVariable Long documentId,
            @RequestBody(required = false) DocumentVerificationDto dto) {
        log.info("Admin reject document ID: {}", documentId);
        try {
            String reason = dto != null && dto.getReason() != null ? dto.getReason() : "Rejected by admin";
            return ResponseEntity.ok(kycService.rejectDocument(documentId, reason));
        } catch (RuntimeException e) {
            log.error("Error rejecting document: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Download a KYC document (admin).
     */
    @GetMapping("/documents/{documentId}/download")
    @Operation(summary = "Download KYC document", description = "Download a KYC document file")
    public ResponseEntity<byte[]> downloadDocument(
            @Parameter(description = "Document ID") @PathVariable Long documentId) {
        log.info("Admin download document ID: {}", documentId);
        try {
            byte[] data = kycService.downloadDocumentByAdmin(documentId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(data);
        } catch (Exception e) {
            log.error("Error downloading document: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Send update request email to investor.
     */
    @PostMapping("/{clientId}/request-update")
    @Operation(summary = "Request information update from investor", 
               description = "Send email to investor requesting them to update specific information or documents")
    public ResponseEntity<Void> requestUpdate(
            @Parameter(description = "Client ID") @PathVariable Long clientId,
            @Valid @RequestBody UpdateRequestDto dto,
            @Parameter(hidden = true) org.springframework.security.core.Authentication authentication) {
        log.info("Admin {} requesting update from client {}", authentication.getName(), clientId);
        try {
            var investor = profileService.getInvestorForAdmin(clientId);
            String investorName = investor.getAuthorizedUser().getFirstName();
            if (investor.getAuthorizedUser().getLastName() != null) {
                investorName += " " + investor.getAuthorizedUser().getLastName();
            }
            String adminName = authentication.getName();
            
            notificationService.sendUpdateRequestEmail(
                investor.getAuthorizedUser().getEmailId(),
                investorName,
                adminName,
                dto.getRequestedFields(),
                dto.getReason()
            );
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error sending update request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
