package com.facilon.app.module.client.controller;

import com.facilon.app.module.client.dto.KycDocumentsDto;
import com.facilon.app.module.client.service.ClientKycService;
import com.facilon.app.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/clients/kyc")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Client KYC", description = "KYC document management")
public class ClientKycController {

    private final ClientKycService kycService;

    @PostMapping("/documents")
    @Operation(summary = "Upload KYC document")
    public ResponseEntity<KycDocumentsDto> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            @RequestParam(value = "description", required = false) String description,
            Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        KycDocumentsDto dto = kycService.uploadDocument(userId, file, documentType, description);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/documents")
    @Operation(summary = "List uploaded documents")
    public ResponseEntity<List<KycDocumentsDto>> getDocuments(Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        return ResponseEntity.ok(kycService.getDocuments(userId));
    }

    @DeleteMapping("/documents/{id}")
    @Operation(summary = "Delete document")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id, Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        kycService.deleteDocument(userId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/documents/{id}/download")
    @Operation(summary = "Download document")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id, Authentication auth) throws Exception {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        byte[] data = kycService.downloadDocument(userId, id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @GetMapping("/requirements")
    @Operation(summary = "Get required document types")
    public ResponseEntity<List<String>> getRequirements() {
        return ResponseEntity.ok(kycService.getRequirements());
    }
}
