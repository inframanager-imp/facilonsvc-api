package com.facilon.app.module.dsr.controller;

import com.facilon.app.module.dsr.dto.DsrAdminCaseDto;
import com.facilon.app.module.dsr.dto.DsrAdminUpdateRequestDto;
import com.facilon.app.module.dsr.dto.DsrCasePageDto;
import com.facilon.app.module.dsr.dto.DsrDashboardSummaryDto;
import com.facilon.app.module.dsr.dto.DsrEvidenceFileDto;
import com.facilon.app.module.dsr.dto.DsrFilePayload;
import com.facilon.app.module.dsr.model.DsrCase;
import com.facilon.app.module.dsr.service.AdminDsrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Admin Center / Privacy Ops console for DSR cases - the full resolution lifecycle.
 * No Service-Provider routing this phase.
 */
@RestController
@RequestMapping("/api/admin/dsr")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin DSR", description = "Privacy Ops / Admin DSR resolution console")
@PreAuthorize("hasAnyAuthority('ADMIN','PLATFORM_SUPER_ADMIN','DSR_ADMIN')")
public class AdminDsrController {

    private final AdminDsrService adminDsrService;

    @GetMapping
    @Operation(summary = "List DSR cases (queue) with optional filters")
    public ResponseEntity<List<DsrAdminCaseDto>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String requestType,
            @RequestParam(required = false) String jurisdiction,
            @RequestParam(required = false) Boolean overdueOnly,
            @RequestParam(required = false) String assignedTo,
            @RequestParam(required = false) String bucket) {
        return ResponseEntity.ok(
                adminDsrService.listCases(status, requestType, jurisdiction, overdueOnly, assignedTo, bucket));
    }

    @GetMapping("/paged")
    @Operation(summary = "List DSR cases with server-side pagination and optional filters")
    public ResponseEntity<DsrCasePageDto> listPaged(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String requestType,
            @RequestParam(required = false) String jurisdiction,
            @RequestParam(required = false) Boolean overdueOnly,
            @RequestParam(required = false) String assignedTo,
            @RequestParam(required = false) String bucket,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                adminDsrService.listCasesPaged(status, requestType, jurisdiction, overdueOnly, assignedTo, bucket, page, size));
    }

    @GetMapping("/dashboard")
    @Operation(summary = "DSR dashboard summary cards")
    public ResponseEntity<DsrDashboardSummaryDto> dashboard() {
        return ResponseEntity.ok(adminDsrService.dashboard());
    }

    @GetMapping("/{caseId}")
    @Operation(summary = "Get a DSR case (admin view + full timeline)")
    public ResponseEntity<DsrAdminCaseDto> get(@PathVariable String caseId) {
        try {
            return ResponseEntity.ok(adminDsrService.getCase(caseId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{caseId}")
    @Operation(summary = "Update a DSR case (status / assign / verification / decision / notes)")
    public ResponseEntity<DsrAdminCaseDto> update(@PathVariable String caseId,
                                                  @RequestBody DsrAdminUpdateRequestDto dto,
                                                  Authentication authentication) {
        try {
            String actor = authentication != null ? authentication.getName() : "ADMIN";
            return ResponseEntity.ok(adminDsrService.updateCase(caseId, dto, actor, "ADMIN"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping(value = "/{caseId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Attach a file to the case evidence folder (default 09_Final_Response)")
    public ResponseEntity<DsrAdminCaseDto> attachFile(@PathVariable String caseId,
                                                      @RequestParam(required = false) String subFolder,
                                                      @RequestPart("file") MultipartFile file,
                                                      Authentication authentication) {
        try {
            String actor = authentication != null ? authentication.getName() : "ADMIN";
            return ResponseEntity.ok(adminDsrService.attachFile(caseId, subFolder, file, actor, "ADMIN"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{caseId}/supporting-file")
    @Operation(summary = "Download the investor's supporting evidence (streamed)")
    public ResponseEntity<?> supportingFile(@PathVariable String caseId) {
        try {
            DsrFilePayload payload = adminDsrService.loadSupportingFile(caseId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + payload.filename() + "\"")
                    .contentType(MediaType.parseMediaType(payload.contentType()))
                    .body(payload.resource());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{caseId}/evidence")
    @Operation(summary = "List the case's local evidence library files")
    public ResponseEntity<List<DsrEvidenceFileDto>> listEvidence(@PathVariable String caseId) {
        try {
            return ResponseEntity.ok(adminDsrService.listEvidenceFiles(caseId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{caseId}/evidence/download")
    @Operation(summary = "Download a single evidence file by its relative path (streamed)")
    public ResponseEntity<?> downloadEvidence(@PathVariable String caseId,
                                              @RequestParam String path) {
        try {
            DsrFilePayload payload = adminDsrService.loadEvidenceFile(caseId, path);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + payload.filename() + "\"")
                    .contentType(MediaType.parseMediaType(payload.contentType()))
                    .body(payload.resource());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/reference/statuses")
    @Operation(summary = "List internal DSR statuses")
    public ResponseEntity<List<String>> statuses() {
        return ResponseEntity.ok(Arrays.stream(DsrCase.CaseStatus.values()).map(Enum::name).toList());
    }

    @GetMapping("/reference/request-types")
    @Operation(summary = "List DSR request types")
    public ResponseEntity<List<String>> requestTypes() {
        return ResponseEntity.ok(Arrays.stream(DsrCase.RequestType.values()).map(Enum::name).toList());
    }

    @GetMapping("/reference/jurisdictions")
    @Operation(summary = "List DSR jurisdictions")
    public ResponseEntity<List<String>> jurisdictions() {
        return ResponseEntity.ok(Arrays.stream(DsrCase.Jurisdiction.values()).map(Enum::name).toList());
    }
}
