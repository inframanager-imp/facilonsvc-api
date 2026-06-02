package com.facilon.app.module.dsr.controller;

import com.facilon.app.annotations.CurrentTenant;
import com.facilon.app.module.client.dto.InvestorDto;
import com.facilon.app.module.client.service.ClientService;
import com.facilon.app.module.dsr.dto.DsrCaseCreateRequestDto;
import com.facilon.app.module.dsr.dto.DsrCaseDetailDto;
import com.facilon.app.module.dsr.dto.DsrCaseResponseDto;
import com.facilon.app.module.dsr.model.DsrCase;
import com.facilon.app.module.dsr.service.DsrService;
import com.facilon.app.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * Investor-facing DSR endpoints. Path retained as {@code /api/clients/me/dsr} for
 * frontend compatibility while the implementation lives in the self-contained dsr module.
 */
@RestController
@RequestMapping("/api/clients/me/dsr")
@RequiredArgsConstructor
@Slf4j
@CurrentTenant
@Tag(name = "Client DSR", description = "Data Subject Rights APIs for investors")
public class DsrController {

    private final DsrService dsrService;
    private final ClientService clientService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Submit DSR request", description = "Create a DSR case with optional supporting evidence")
    public ResponseEntity<DsrCaseResponseDto> submitCase(
            @RequestParam String requestType,
            @RequestParam String jurisdiction,
            @RequestParam(required = false) String dataArea,
            @RequestParam String requestDescription,
            @RequestParam String requesterName,
            @RequestParam String requesterEmail,
            @RequestParam(required = false) String requesterPhone,
            @RequestParam(required = false) String requesterRole,
            @RequestPart(value = "supportingFile", required = false) MultipartFile supportingFile,
            Authentication authentication) {
        try {
            String uniqueCode = getCurrentInvestorUniqueCode(authentication);
            DsrCaseCreateRequestDto dto = DsrCaseCreateRequestDto.builder()
                    .requestType(requestType)
                    .jurisdiction(jurisdiction)
                    .dataArea(dataArea)
                    .requestDescription(requestDescription)
                    .requesterName(requesterName)
                    .requesterEmail(requesterEmail)
                    .requesterPhone(requesterPhone)
                    .requesterRole(requesterRole)
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(dsrService.submitCase(uniqueCode, dto, supportingFile));
        } catch (RuntimeException e) {
            log.error("Error submitting DSR case: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "Get my DSR cases", description = "List DSR cases for current investor")
    public ResponseEntity<List<DsrCaseResponseDto>> getMyCases(Authentication authentication) {
        try {
            String uniqueCode = getCurrentInvestorUniqueCode(authentication);
            return ResponseEntity.ok(dsrService.getCasesForInvestor(uniqueCode));
        } catch (RuntimeException e) {
            log.error("Error listing DSR cases: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{caseId}")
    @Operation(summary = "Get my DSR case by Case ID", description = "Case detail with investor-visible timeline")
    public ResponseEntity<DsrCaseDetailDto> getMyCase(@PathVariable String caseId, Authentication authentication) {
        try {
            String uniqueCode = getCurrentInvestorUniqueCode(authentication);
            return ResponseEntity.ok(dsrService.getCaseDetailForInvestor(uniqueCode, caseId));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            log.error("Error fetching DSR case {}: {}", caseId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/request-types")
    @Operation(summary = "List DSR request types")
    public ResponseEntity<List<String>> getRequestTypes() {
        return ResponseEntity.ok(Arrays.stream(DsrCase.RequestType.values()).map(Enum::name).toList());
    }

    @GetMapping("/jurisdictions")
    @Operation(summary = "List DSR jurisdictions")
    public ResponseEntity<List<String>> getJurisdictions() {
        return ResponseEntity.ok(Arrays.stream(DsrCase.Jurisdiction.values()).map(Enum::name).toList());
    }

    private String getCurrentInvestorUniqueCode(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        InvestorDto client = clientService.getMyClientProfile(userPrincipal.getId());
        return client.getUniqueCode();
    }
}
