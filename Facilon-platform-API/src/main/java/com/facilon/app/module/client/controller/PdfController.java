package com.facilon.app.module.client.controller;

import com.facilon.app.module.client.service.PdfGenerationService;
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

@RestController
@RequestMapping("/api/investor/pdf")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "PDF Generation", description = "Investor PDF generation endpoints")
public class PdfController {

    private final PdfGenerationService pdfGenerationService;

    /**
     * Generate and download investor information PDF for current user
     * Uses FreeMarker template + Flying Saucer - Laravel Blade + DomPDF equivalent
     * Aligned with Laravel: /user-pdf-view/{id}
     */
    @GetMapping("/view")
    @Operation(summary = "Generate investor PDF for current user")
    public ResponseEntity<byte[]> generateInvestorPdf(Authentication authentication) {
        try {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            Long userId = principal.getId();
            log.info("Generating PDF for user: {}", userId);

            // Use FreeMarker template + Flying Saucer (Laravel Blade + DomPDF equivalent)
            byte[] pdfBytes = pdfGenerationService.generateInvestorPdfFromTemplateByUserId(userId);
            
            if (pdfBytes == null || pdfBytes.length == 0) {
                log.error("Generated PDF is empty for user: {}", userId);
                return ResponseEntity.internalServerError().build();
            }
            
            log.info("PDF generated successfully. Size: {} bytes", pdfBytes.length);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentLength(pdfBytes.length);
            headers.setContentDispositionFormData("inline", "Account_Opening_Booklet.pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            headers.set("Content-Transfer-Encoding", "binary");
            headers.set("Accept-Ranges", "bytes");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Error generating PDF for authenticated user", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Generate investor information print preview HTML for current user
     * Uses FreeMarker template - Laravel Blade equivalent
     * Aligned with Laravel: /user-information-print-preview/{id}
     */
    @GetMapping("/print-preview")
    @Operation(summary = "Generate investor print preview for current user")
    public ResponseEntity<String> generatePrintPreview(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.getId();
        log.info("Generating print preview for user: {}", userId);

        // Use FreeMarker template (Laravel Blade equivalent)
        String html = pdfGenerationService.generatePrintPreviewHtmlFromTemplateByUserId(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);

        return ResponseEntity.ok()
                .headers(headers)
                .body(html);
    }

    /**
     * Generate and download investor information PDF by ID (admin use)
     * Aligned with Laravel: /user-pdf-view/{id}
     */
    @GetMapping("/view/{investorId}")
    @Operation(summary = "Generate investor PDF by ID")
    public ResponseEntity<byte[]> generateInvestorPdfById(@PathVariable Long investorId) {
        log.info("Generating PDF for investor ID: {}", investorId);

        byte[] pdfBytes = pdfGenerationService.generateInvestorPdf(investorId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "investor_" + investorId + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    /**
     * Generate investor information print preview HTML by ID (admin use)
     * Aligned with Laravel: /user-information-print-preview/{id}
     */
    @GetMapping("/print-preview/{investorId}")
    @Operation(summary = "Generate investor print preview by ID")
    public ResponseEntity<String> generatePrintPreviewById(@PathVariable Long investorId) {
        log.info("Generating print preview for investor ID: {}", investorId);

        String html = pdfGenerationService.generatePrintPreviewHtml(investorId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);

        return ResponseEntity.ok()
                .headers(headers)
                .body(html);
    }

}
