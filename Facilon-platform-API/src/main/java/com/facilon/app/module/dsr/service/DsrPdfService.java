package com.facilon.app.module.dsr.service;

import com.facilon.app.module.dsr.model.DsrCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Generates the DSR evidence PDFs for a case:
 * <ul>
 *   <li><b>01_Request</b> — the Request Submission record (all submitted details).</li>
 *   <li><b>02_Acknowledgement</b> — a copy of the acknowledgement issued to the requester.</li>
 * </ul>
 * Uses Flying Saucer ({@link ITextRenderer}); input must be well-formed XHTML.
 */
@Service
@Slf4j
public class DsrPdfService {

    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    public byte[] htmlToPdf(String xhtml) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(xhtml);
            renderer.layout();
            renderer.createPDF(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("DSR PDF render failed: {}", e.getMessage(), e);
            throw new RuntimeException("DSR PDF render failed", e);
        }
    }

    /** 01_Request — the original request as submitted. */
    public byte[] requestSummaryPdf(DsrCase c) {
        String rows =
                row("Case ID", c.getCaseId())
              + row("Submitted At", fmt(c.getCreatedAt()))
              + row("Right Exercised", name(c.getRequestType()))
              + row("Jurisdiction", name(c.getJurisdiction()))
              + row("Data Area", c.getDataArea())
              + row("Requester Name", c.getRequesterName())
              + row("Requester Email", c.getRequesterEmail())
              + row("Requester Phone", c.getRequesterPhone())
              + row("Requester Role", c.getRequesterRole())
              + row("SLA Deadline", fmt(c.getSlaDeadline()));

        String body =
                heading("Data Subject Rights — Request Submission")
              + subheading("Facilon Services Private Limited")
              + "<table style=\"width:100%; border-collapse:collapse; margin-top:12px;\">" + rows + "</table>"
              + "<h3 style=\"margin-top:18px;\">Request Description</h3>"
              + "<p>" + safe(c.getRequestDescription()).replace("\n", "<br/>") + "</p>"
              + "<h3 style=\"margin-top:18px;\">Declaration</h3>"
              + "<p style=\"font-size:11px; color:#555;\">The requester confirmed this request was submitted through "
              + "their Facilon Investor Account, and acknowledged that Facilon may verify identity, clarify the "
              + "request, or coordinate with relevant parties, and that certain requests may be subject to legal, "
              + "regulatory, contractual, security, audit, or retention requirements.</p>"
              + footer();

        return htmlToPdf(document("DSR Request " + safe(c.getCaseId()), body));
    }

    /** 02_Acknowledgement — copy of the acknowledgement issued to the requester. */
    public byte[] acknowledgementPdf(DsrCase c) {
        String body =
                heading("Acknowledgement of Data Rights Request")
              + subheading("Facilon Services Private Limited")
              + "<p style=\"margin-top:16px;\">Dear " + safe(c.getRequesterName()) + ",</p>"
              + "<p>We have received your data rights request. Your reference number is "
              + "<strong>" + safe(c.getCaseId()) + "</strong>.</p>"
              + "<table style=\"width:100%; border-collapse:collapse; margin:12px 0;\">"
              + row("Reference", c.getCaseId())
              + row("Right Exercised", name(c.getRequestType()))
              + row("Jurisdiction", name(c.getJurisdiction()))
              + row("Received On", fmt(c.getCreatedAt()))
              + row("Target Response By", fmt(c.getSlaDeadline()))
              + "</table>"
              + "<p>We may need to verify your identity or clarify the scope of your request before processing it. "
              + "Facilon will review your request in accordance with applicable data protection laws, contractual "
              + "obligations, and legal or regulatory retention requirements.</p>"
              + "<p>Please quote the above reference number in any future correspondence.</p>"
              + "<p style=\"margin-top:16px;\">Regards,<br/>Privacy Team<br/>Facilon Services Private Limited</p>"
              + footer();

        return htmlToPdf(document("DSR Acknowledgement " + safe(c.getCaseId()), body));
    }

    // ----- html helpers (well-formed XHTML) ------------------------------------

    private String document(String title, String body) {
        return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
             + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"
             + "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>" + safe(title) + "</title>"
             + "<style>body{font-family:Helvetica,Arial,sans-serif;font-size:12px;color:#222;}"
             + "td{padding:4px 8px;border-bottom:1px solid #eee;vertical-align:top;}"
             + "td.k{width:35%;color:#666;}h1{font-size:18px;margin:0;}h2{font-size:13px;color:#666;margin:2px 0 0;}"
             + "h3{font-size:13px;}</style></head><body>" + body + "</body></html>";
    }

    private String heading(String t) { return "<h1>" + safe(t) + "</h1>"; }

    private String subheading(String t) { return "<h2>" + safe(t) + "</h2>"; }

    private String row(String k, String v) {
        return "<tr><td class=\"k\">" + safe(k) + "</td><td>" + (v == null || v.isBlank() ? "—" : safe(v)) + "</td></tr>";
    }

    private String footer() {
        return "<p style=\"margin-top:24px;font-size:10px;color:#999;\">Generated by Facilon DSR Centre · "
             + fmt(LocalDateTime.now()) + " · This document is stored in the case evidence library.</p>";
    }

    private String name(Enum<?> e) { return e == null ? null : e.name(); }

    private String fmt(LocalDateTime v) { return v == null ? null : v.format(STAMP); }

    private String safe(String v) {
        if (v == null) {
            return "";
        }
        return v.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
