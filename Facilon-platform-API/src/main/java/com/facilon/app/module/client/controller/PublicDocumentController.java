package com.facilon.app.module.client.controller;

import com.facilon.app.annotations.CurrentTenant;
import com.facilon.app.module.client.dto.DocumentResponseDto;
import com.facilon.app.module.client.dto.KycDocumentRequirementDto;
import com.facilon.app.module.client.service.InvestorDocumentService;
import com.facilon.app.util.LaravelEncryptionUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Public Document Submission Controller
 * Handles document submission via encrypted token (no authentication required)
 * Similar to Laravel: /investor/document-submission/{encryptedToken}
 */
@RestController
@RequestMapping("/api/clients/documents/submission")
@RequiredArgsConstructor
@Slf4j
@CurrentTenant
@Tag(name = "Public Document Submission", description = "Public APIs for document submission via encrypted token")
public class PublicDocumentController {

    private final InvestorDocumentService documentService;

    @GetMapping("/{encryptedToken}")
    @Operation(summary = "Get document requirements via encrypted token", 
               description = "Decrypt token to get investor info and return document requirements from Dataverse")
    public ResponseEntity<?> getDocumentSubmissionData(@PathVariable String encryptedToken) {
        log.info("Document submission request with encrypted token");

        try {
            // Step 1: Decrypt the Laravel encrypted token
            String decryptedData = LaravelEncryptionUtil.decrypt(encryptedToken);
            log.info("Decrypted token data: {}", decryptedData);

            // Step 2: Parse decrypted data (assuming it contains uniqueCode)
            // Format could be: "202603141648" or JSON like {"uniqueCode":"202603141648"}
            String uniqueCode = extractUniqueCode(decryptedData);

            // Step 3: Fetch document requirements from Dataverse
            KycDocumentRequirementDto requirements = documentService.getKycRequirementsFromDataverse(uniqueCode);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "uniqueCode", uniqueCode,
                    "requirements", requirements
            ));

        } catch (SecurityException e) {
            log.error("Token verification failed: {}", e.getMessage());
            return ResponseEntity.status(403).body(Map.of(
                    "success", false,
                    "error", "Invalid or tampered token"
            ));
        } catch (IllegalArgumentException e) {
            log.error("Invalid token format: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error processing document submission token: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "error", "Failed to process token"
            ));
        }
    }

    @PostMapping(value = "/{encryptedToken}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload document via encrypted token",
               description = "Upload KYC document using encrypted token (no authentication required)")
    public ResponseEntity<?> uploadDocumentViaToken(
            @PathVariable String encryptedToken,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType) {
        log.info("Document upload via token, type: {}", documentType);

        try {
            // Step 1: Decrypt token to get uniqueCode
            String decryptedData = LaravelEncryptionUtil.decrypt(encryptedToken);
            String uniqueCode = extractUniqueCode(decryptedData);

            // Step 2: Upload document and sync with Dataverse
            DocumentResponseDto response = documentService.uploadKycDocumentAndSyncDataverse(uniqueCode, file, documentType);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Document uploaded successfully",
                    "document", response
            ));

        } catch (SecurityException e) {
            log.error("Token verification failed: {}", e.getMessage());
            return ResponseEntity.status(403).body(Map.of(
                    "success", false,
                    "error", "Invalid or tampered token"
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Upload validation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error uploading document via token: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage() != null ? e.getMessage() : "Upload failed"
            ));
        }
    }

    /**
     * Extract uniqueCode from decrypted token data.
     * Handles both simple string and JSON format.
     */
    private String extractUniqueCode(String decryptedData) {
        if (decryptedData == null || decryptedData.isEmpty()) {
            throw new IllegalArgumentException("Decrypted token is empty");
        }

        // If it's JSON, parse it
        if (decryptedData.startsWith("{")) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode json = mapper.readTree(decryptedData);
                
                // Try different possible field names
                if (json.has("uniqueCode")) {
                    return json.get("uniqueCode").asText();
                } else if (json.has("unique_code")) {
                    return json.get("unique_code").asText();
                } else if (json.has("investor_unique_id")) {
                    return json.get("investor_unique_id").asText();
                } else if (json.has("contactId")) {
                    // If only contactId is provided, we may need to query DB to get uniqueCode
                    return json.get("contactId").asText();
                }
            } catch (Exception e) {
                log.warn("Failed to parse JSON token, treating as plain string: {}", e.getMessage());
            }
        }

        // If it's a simple string, return as-is
        return decryptedData.trim();
    }
}
