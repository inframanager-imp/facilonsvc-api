package com.facilon.app.module.admin.controller;

import com.facilon.app.module.client.dto.DocumentDto;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin controller for document verification
 */
@RestController
@RequestMapping("/api/admin/documents")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Documents", description = "Admin document verification and management")
 
public class AdminDocumentController {

    private final ClientDocumentService documentService;
    
    @GetMapping
    @Operation(summary = "Get all documents with optional filters")
    public ResponseEntity<List<DocumentDto>> getAllDocuments(
            @Parameter(description = "Filter by status (UPLOADED, VERIFIED, REJECTED)")
            @RequestParam(required = false) String status,
            @Parameter(description = "Filter by document type")
            @RequestParam(required = false) String documentType,
            @Parameter(description = "Filter by investor code")
            @RequestParam(required = false) String investorCode
    ) {
        log.info("Admin fetching documents: status={}, type={}, investor={}", status, documentType, investorCode);
        
        // Get all documents from all investors
        List<DocumentDto> allDocuments = documentService.getAllDocuments();
        
        // Apply filters
        List<DocumentDto> filtered = allDocuments.stream()
                .filter(doc -> status == null || status.equals(doc.getStatus()))
                .filter(doc -> documentType == null || documentType.equals(doc.getDocumentType()))
                .filter(doc -> investorCode == null || investorCode.equals(doc.getUploadedBy()))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(filtered);
    }
    
    @GetMapping("/pending")
    @Operation(summary = "Get all pending documents for verification")
    public ResponseEntity<List<DocumentDto>> getPendingDocuments() {
        List<DocumentDto> pending = documentService.getAllDocuments().stream()
                .filter(doc -> "UPLOADED".equals(doc.getStatus()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(pending);
    }
    
    @GetMapping("/{documentId}")
    @Operation(summary = "Get document details by ID")
    public ResponseEntity<DocumentDto> getDocumentById(@PathVariable Long documentId) {
        return documentService.getDocumentById(documentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{documentId}/download")
    @Operation(summary = "Download document for review")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        try {
            DocumentDto document = documentService.getDocumentById(documentId)
                    .orElseThrow(() -> new RuntimeException("Document not found"));
            
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
    
    @PutMapping("/{documentId}/verify")
    @Operation(summary = "Verify document")
    public ResponseEntity<Void> verifyDocument(
            @PathVariable Long documentId,
            @RequestParam(required = false) String remarks,
            Authentication authentication
    ) {
        String adminUsername = authentication.getName();
        boolean success = documentService.verifyDocument(documentId, "VERIFIED", remarks, adminUsername);
        
        if (success) {
            log.info("Document {} verified by {}", documentId, adminUsername);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{documentId}/reject")
    @Operation(summary = "Reject document with reason")
    public ResponseEntity<Void> rejectDocument(
            @PathVariable Long documentId,
            @RequestParam String remarks,
            Authentication authentication
    ) {
        String adminUsername = authentication.getName();
        boolean success = documentService.verifyDocument(documentId, "REJECTED", remarks, adminUsername);
        
        if (success) {
            log.info("Document {} rejected by {}", documentId, adminUsername);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get document verification statistics")
    public ResponseEntity<DocumentStats> getStats() {
        List<DocumentDto> allDocs = documentService.getAllDocuments();
        
        DocumentStats stats = new DocumentStats();
        stats.setTotal(allDocs.size());
        stats.setPending(allDocs.stream().filter(d -> "UPLOADED".equals(d.getStatus())).count());
        stats.setVerified(allDocs.stream().filter(d -> "VERIFIED".equals(d.getStatus())).count());
        stats.setRejected(allDocs.stream().filter(d -> "REJECTED".equals(d.getStatus())).count());
        
        return ResponseEntity.ok(stats);
    }
    
    // Stats DTO
    public static class DocumentStats {
        private int total;
        private long pending;
        private long verified;
        private long rejected;
        
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
        public long getPending() { return pending; }
        public void setPending(long pending) { this.pending = pending; }
        public long getVerified() { return verified; }
        public void setVerified(long verified) { this.verified = verified; }
        public long getRejected() { return rejected; }
        public void setRejected(long rejected) { this.rejected = rejected; }
    }
}
