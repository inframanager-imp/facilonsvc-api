package com.facilon.app.module.client.controller;

import com.facilon.app.annotations.CurrentTenant;
import com.facilon.app.module.client.dto.DocumentResponseDto;
import com.facilon.app.module.client.dto.InvestorDto;
import com.facilon.app.module.client.dto.KycDocumentRequirementDto;
import com.facilon.app.module.client.service.ClientService;
import com.facilon.app.module.client.service.InvestorDocumentService;
import com.facilon.app.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Investor Document Controller - For managing investor documents
 */
@RestController
@RequestMapping("/api/clients/me/documents")
@RequiredArgsConstructor
@Slf4j
@CurrentTenant
@Tag(name = "Investor Documents", description = "APIs for managing investor documents")
public class InvestorDocumentController {

    private final InvestorDocumentService documentService;
    private final ClientService clientService;

    @PostMapping(value = "/kyc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload KYC document", description = "Upload a KYC document for current investor. Allowed: PDF, JPG, JPEG, PNG. Max 10 MB.")
    public ResponseEntity<?> uploadKycDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            @RequestParam(value = "documentMasterId", required = false) String documentMasterId,
            Authentication authentication) {
        log.info("Upload KYC document request, type: {}, dynamicsId: {}", documentType, documentMasterId);

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            DocumentResponseDto response = documentService.uploadKycDocument(uniqueCode, file, documentType, documentMasterId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("KYC upload validation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error uploading KYC document: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Upload failed"));
        }
    }

    @PostMapping(value = "/onboarding", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload onboarding document", description = "Upload an onboarding document for current investor. "
            + "When Dataverse is enabled, pass investorDocumentId (ss_investordocumentsid from requirements).")
    public ResponseEntity<DocumentResponseDto> uploadOnboardingDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            @RequestParam(value = "investorDocumentId", required = false) String investorDocumentId,
            Authentication authentication) {
        log.info("Upload onboarding document request, type: {}, investorDocumentId: {}", documentType, investorDocumentId);

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            DocumentResponseDto response = documentService.uploadOnboardingDocument(uniqueCode, file, documentType, investorDocumentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error uploading onboarding document: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{documentId}/download")
    @Operation(summary = "Download / preview a KYC document",
            description = "Streams the document bytes, resolving the SharePoint copy or decrypting the encrypted Azure Blob copy.")
    public ResponseEntity<byte[]> downloadKycDocument(@PathVariable Long documentId, Authentication authentication) {
        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            InvestorDocumentService.DownloadResult result = documentService.downloadKycDocument(uniqueCode, documentId);
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + result.fileName() + "\"")
                    .contentType(MediaType.parseMediaType(result.contentType()))
                    .body(result.content());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error downloading document {}: {}", documentId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    @Operation(summary = "Get all documents", description = "Get all documents for current investor")
    public ResponseEntity<List<DocumentResponseDto>> getAllDocuments(Authentication authentication) {
        log.info("Get all documents request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            List<DocumentResponseDto> documents = documentService.getInvestorDocuments(uniqueCode);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            log.error("Error fetching documents: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/kyc")
    @Operation(summary = "Get KYC documents", description = "Get KYC documents (uploadType=1) for current investor")
    public ResponseEntity<List<DocumentResponseDto>> getKycDocuments(Authentication authentication) {
        log.info("Get KYC documents request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            List<DocumentResponseDto> documents = documentService.getKycDocuments(uniqueCode);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            log.error("Error fetching KYC documents: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/kyc/requirements")
    @Operation(summary = "Get KYC requirements", description = "Get KYC requirements with upload status for current investor")
    public ResponseEntity<KycDocumentRequirementDto> getKycRequirements(Authentication authentication) {
        log.info("Get KYC requirements request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            KycDocumentRequirementDto requirements = documentService.getKycRequirements(uniqueCode);
            return ResponseEntity.ok(requirements);
        } catch (Exception e) {
            log.error("Error fetching KYC requirements: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/onboarding/list")
    @Operation(summary = "Get onboarding documents", description = "Get onboarding documents (uploadType=2) for current investor")
    public ResponseEntity<List<DocumentResponseDto>> getOnboardingDocuments(Authentication authentication) {
        log.info("Get onboarding documents request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            List<DocumentResponseDto> documents = documentService.getOnboardingDocuments(uniqueCode);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            log.error("Error fetching onboarding documents: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/onboarding/requirements")
    @Operation(summary = "Get onboarding requirements", description = "Get onboarding document requirements with upload status from Dataverse")
    public ResponseEntity<KycDocumentRequirementDto> getOnboardingRequirements(Authentication authentication) {
        log.info("Get onboarding requirements request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            KycDocumentRequirementDto requirements = documentService.getOnboardingRequirements(uniqueCode);
            return ResponseEntity.ok(requirements);
        } catch (Exception e) {
            log.error("Error fetching onboarding requirements: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/onboarding/forms/download")
    @Operation(summary = "Download pre-filled forms", description = "Download pre-filled onboarding forms for current investor")
    public ResponseEntity<Map<String, Object>> downloadPreFilledForms(Authentication authentication) {
        log.info("Download pre-filled forms request");

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            
            // TODO: Generate and return pre-filled PDF forms
            // For now, return available form templates
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

    @GetMapping("/{documentId}/status")
    @Operation(summary = "Get document status", description = "Get status of a specific document")
    public ResponseEntity<DocumentResponseDto> getDocumentStatus(
            @PathVariable Long documentId,
            Authentication authentication) {
        log.info("Get document status request, documentId: {}", documentId);

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            DocumentResponseDto document = documentService.getDocumentStatus(uniqueCode, documentId);
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            log.error("Error fetching document status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{documentId}")
    @Operation(summary = "Delete document", description = "Delete a document")
    public ResponseEntity<Map<String, String>> deleteDocument(
            @PathVariable Long documentId,
            Authentication authentication) {
        log.info("Delete document request, documentId: {}", documentId);

        try {
            String uniqueCode = getInvestorUniqueCode(authentication);
            documentService.deleteDocument(uniqueCode, documentId);
            return ResponseEntity.ok(Map.of("message", "Document deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting document: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/sharepoint/download")
    @Operation(summary = "Download SharePoint document", description = "Download a document from SharePoint by URL (for onboarding templates)")
    public ResponseEntity<byte[]> downloadSharePointDocument(
            @RequestParam("url") String url,
            Authentication authentication) {
        log.info("Download SharePoint document request, URL: {}", url);

        try {
            // Verify user is authenticated (already done by Spring Security, but good practice to validate)
            getInvestorUniqueCode(authentication);

            byte[] fileContent = documentService.downloadSharePointDocument(url);

            // Extract filename from URL or use default
            String filename = extractFilenameFromUrl(url);

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileContent);
        } catch (IllegalArgumentException e) {
            log.warn("SharePoint download validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error downloading SharePoint document: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    private String extractFilenameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "document.pdf";
        }
        // Extract last segment from URL
        String[] parts = url.split("/");
        String lastPart = parts[parts.length - 1];
        // Decode URL-encoded characters
        try {
            return java.net.URLDecoder.decode(lastPart, "UTF-8") + ".pdf";
        } catch (Exception e) {
            return lastPart + ".pdf";
        }
    }

    private String getInvestorUniqueCode(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        InvestorDto investor = clientService.getMyClientProfile(userPrincipal.getId());
        return investor.getUniqueCode();
    }
}
