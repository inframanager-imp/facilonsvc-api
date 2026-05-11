package com.facilon.app.module.serviceprovider.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;

/**
 * Renders HTML → PDF for the SP-onboarding consent attachments. Mirrors Laravel's
 * {@code PDF::loadHTML($pdfHtml)->output()} (barryvdh/laravel-dompdf).
 *
 * <p>Uses Flying Saucer (already in pom.xml) with iText for the PDF backend.
 * Flying Saucer is XHTML-strict — keep the input HTML well-formed.</p>
 */
@Service
@Slf4j
public class PdfGeneratorService {

    /**
     * Render an XHTML string to a PDF byte array.
     *
     * @param xhtml well-formed XHTML (self-closing void tags, named entities only)
     * @return raw PDF bytes
     */
    public byte[] htmlToPdf(String xhtml) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(xhtml);
            renderer.layout();
            renderer.createPDF(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("PDF render failed: {}", e.getMessage(), e);
            throw new RuntimeException("PDF render failed", e);
        }
    }
}
