package com.facilon.app.module.client.controller;

import com.facilon.app.module.client.dto.DocumentDto;
import com.facilon.app.module.client.dto.DocumentUploadResponseDto;
import com.facilon.app.module.client.service.ClientDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * REST API for investor document management
 */
@RestController
@RequestMapping("/api/clients/documents")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Client Documents", description = "Investor document upload, download, and management")
public class ClientDocumentController {

    private final ClientDocumentService documentService;
    
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload document(s) for investor", description = "Upload one or multiple documents (passport, PAN, etc.)")
    public ResponseEntity<List<DocumentUploadResponseDto>> uploadDocuments(
            @Parameter(description = "Document type (PASSPORT, PAN_CARD, ADDRESS_PROOF, PHOTO, OTHER)")
            @RequestParam String documentType,
            @Parameter(description = "File(s) to upload")
            @RequestParam("files") MultipartFile[] files,
            Authentication authentication
    ) {
        String investorCode = authentication.getName(); // username or unique code
        log.info("Upload request from investor: {} for type: {}, files: {}", investorCode, documentType, files.length);
        
        List<DocumentUploadResponseDto> responses = new ArrayList<>();
        
        for (MultipartFile file : files) {
            DocumentUploadResponseDto response = documentService.uploadDocument(investorCode, documentType, file);
            responses.add(response);
        }
        
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping
    @Operation(summary = "Get all documents for current investor")
    public ResponseEntity<List<DocumentDto>> getMyDocuments(Authentication authentication) {
        String investorCode = authentication.getName();
        List<DocumentDto> documents = documentService.getInvestorDocuments(investorCode);
        return ResponseEntity.ok(documents);
    }
    
    @GetMapping("/{documentId}")
    @Operation(summary = "Get document details by ID")
    public ResponseEntity<DocumentDto> getDocumentById(
            @PathVariable Long documentId,
            Authentication authentication
    ) {
        return documentService.getDocumentById(documentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{documentId}/download")
    @Operation(summary = "Download document file")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long documentId,
            Authentication authentication
    ) {
        try {
            DocumentDto document = documentService.getDocumentById(documentId)
                    .orElseThrow(() -> new RuntimeException("Document not found"));
            
            // Security check: ensure document belongs to authenticated user
            String investorCode = authentication.getName();
            if (!document.getUploadedBy().equals(investorCode)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            byte[] fileContent = documentService.downloadDocument(documentId);
            ByteArrayResource resource = new ByteArrayResource(fileContent);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getOriginalFileName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(fileContent.length)
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("Download failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{documentId}")
    @Operation(summary = "Delete document")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long documentId,
            Authentication authentication
    ) {
        String investorCode = authentication.getName();
        boolean deleted = documentService.deleteDocument(documentId, investorCode);
        
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{documentId}/verify")
    @Operation(summary = "Verify or reject document (Admin only)")
    public ResponseEntity<Void> verifyDocument(
            @PathVariable Long documentId,
            @RequestParam String status, // VERIFIED or REJECTED
            @RequestParam(required = false) String remarks,
            Authentication authentication
    ) {
        String adminUsername = authentication.getName();
        boolean success = documentService.verifyDocument(documentId, status, remarks, adminUsername);
        
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
