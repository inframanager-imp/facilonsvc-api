package com.facilon.app.module.client.controller;

import com.facilon.app.module.client.dto.KycRequirementsResponseDto;
import com.facilon.app.module.client.dto.KycSmartDocumentDto;
import com.facilon.app.module.client.service.KycRequirementService;
import com.facilon.app.module.client.service.KycSmartUploadService;
import com.facilon.app.module.client.service.KycSmartUploadService.BlockingDiscrepancyException;
import com.facilon.app.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Smart Upload REST surface (KYC_DOCUMENT_PLAN §3.4).
 * Mounted at /api/clients/me/kyc to avoid collision with the legacy
 * /api/clients/kyc controller (ClientKycController).
 */
@RestController
@RequestMapping("/api/clients/me/kyc")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Client KYC Smart Upload",
        description = "Type-aware KYC document upload with OCR + validation")
public class KycSmartUploadController {

    private final KycSmartUploadService uploadService;
    private final KycRequirementService requirementService;

    @GetMapping("/requirements")
    @Operation(summary = "Per-investor-type requirement matrix + per-slot state")
    public ResponseEntity<KycRequirementsResponseDto> requirements(Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        return ResponseEntity.ok(requirementService.computeForUser(userId));
    }

    @PostMapping("/documents")
    @Operation(summary = "Upload KYC document (synchronous OCR via ORCReader)")
    public ResponseEntity<KycSmartDocumentDto> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            @RequestParam(value = "addressProofType", required = false) String addressProofType,
            @RequestParam(value = "usesAadhaarForAddress", required = false) Boolean usesAadhaarForAddress,
            Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        try {
            KycSmartDocumentDto dto = uploadService.upload(userId, file, documentType,
                    addressProofType, usesAadhaarForAddress, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (BlockingDiscrepancyException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getDocument());
        }
    }

    @PostMapping("/documents/{id}/re-upload")
    @Operation(summary = "Re-upload supersedes a previous document in the version chain")
    public ResponseEntity<KycSmartDocumentDto> reUpload(
            @PathVariable("id") Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            @RequestParam(value = "addressProofType", required = false) String addressProofType,
            @RequestParam(value = "usesAadhaarForAddress", required = false) Boolean usesAadhaarForAddress,
            Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        try {
            KycSmartDocumentDto dto = uploadService.upload(userId, file, documentType,
                    addressProofType, usesAadhaarForAddress, id);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (BlockingDiscrepancyException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getDocument());
        }
    }

    @GetMapping("/documents")
    @Operation(summary = "List the investor's current (non-superseded) documents")
    public ResponseEntity<List<KycSmartDocumentDto>> list(Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        return ResponseEntity.ok(uploadService.list(userId));
    }

    @GetMapping("/documents/{id}")
    @Operation(summary = "Detail incl. OCR fields + discrepancies")
    public ResponseEntity<KycSmartDocumentDto> detail(@PathVariable Long id, Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        return ResponseEntity.ok(uploadService.detail(userId, id));
    }

    @GetMapping("/documents/{id}/history")
    @Operation(summary = "Full version chain for the document slot")
    public ResponseEntity<List<KycSmartDocumentDto>> history(@PathVariable Long id, Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        return ResponseEntity.ok(uploadService.history(userId, id));
    }

    @DeleteMapping("/documents/{id}")
    @Operation(summary = "Soft-delete (only if not yet approved)")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        uploadService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/documents/{id}/confirm")
    @Operation(summary = "Investor accepts the extracted fields; triggers profile auto-population")
    public ResponseEntity<KycSmartDocumentDto> confirm(@PathVariable Long id, Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        return ResponseEntity.ok(uploadService.confirm(userId, id));
    }

    @PostMapping("/documents/{id}/reject")
    @Operation(summary = "Investor rejects the extracted fields; soft-deletes the upload")
    public ResponseEntity<Void> reject(@PathVariable Long id, Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();
        uploadService.reject(userId, id);
        return ResponseEntity.noContent().build();
    }
}
