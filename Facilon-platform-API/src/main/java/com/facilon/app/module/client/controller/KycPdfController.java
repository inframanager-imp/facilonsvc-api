package com.facilon.app.module.client.controller;

import com.facilon.app.module.client.dto.InvestorDto;
import com.facilon.app.module.client.dto.KycFormDataDto;
import com.facilon.app.module.client.service.ClientService;
import com.facilon.app.module.client.service.KycPdfService;
import com.facilon.app.module.client.service.KycPdfStampService;
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

/**
 * KYC PDF form-filler endpoints.
 *
 * Generates the filled 36-page KYC Account Opening Kit PDF from investor data
 * using the multi-fragment FreeMarker template system
 * ({@code templates/pdf/kyc-form/}) and the JSON section mapping
 * ({@code kyc-form-mapping.json}).
 */
@RestController
@RequestMapping("/api/investor/pdf")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "KYC PDF", description = "KYC Account Opening Kit PDF generation")
public class KycPdfController {

    private final KycPdfService kycPdfService;
    private final KycPdfStampService kycPdfStampService;
    private final ClientService clientService;

    /**
     * Download the filled KYC PDF for the authenticated investor.
     * Uses FreeMarker template (kyc-form-master.ftl) converted from Laravel pdf.blade
     * rendered to PDF via Flying Saucer — full parity with Laravel DomPDF output.
     */
    @GetMapping("/kyc-form")
    @Operation(summary = "Download KYC form PDF for current user")
    public ResponseEntity<byte[]> downloadKycPdf(Authentication auth) {
        String uniqueCode = resolveUniqueCode(auth);
        log.info("Generating KYC PDF (FreeMarker) for investor: {}", uniqueCode);

        byte[] pdf = kycPdfService.generateKycPdf(uniqueCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(pdf.length);
        headers.setContentDispositionFormData("inline", "KYC_Account_Opening_Kit.pdf");
        headers.set("Content-Transfer-Encoding", "binary");
        headers.set("Accept-Ranges", "bytes");

        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    /**
     * HTML preview of KYC form (FreeMarker template converted from Laravel pdf.blade).
     */
    @GetMapping("/kyc-form/preview")
    @Operation(summary = "Preview KYC form HTML for current user")
    public ResponseEntity<String> previewKycForm(Authentication auth) {
        String uniqueCode = resolveUniqueCode(auth);
        log.info("Generating KYC preview for investor: {}", uniqueCode);

        String html = kycPdfService.generateKycPreviewHtml(uniqueCode);
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }

    /**
     * Get the structured form data JSON for the authenticated investor.
     */
    @GetMapping("/kyc-form/data")
    @Operation(summary = "Get KYC form data JSON for current user")
    public ResponseEntity<KycFormDataDto> getKycFormData(Authentication auth) {
        String uniqueCode = resolveUniqueCode(auth);
        log.info("Loading KYC form data for investor: {}", uniqueCode);

        KycFormDataDto dto = kycPdfService.buildFormData(uniqueCode);
        return ResponseEntity.ok(dto);
    }

    private String resolveUniqueCode(Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        InvestorDto investor = clientService.getMyClientProfile(principal.getId());
        return investor.getUniqueCode();
    }
}
