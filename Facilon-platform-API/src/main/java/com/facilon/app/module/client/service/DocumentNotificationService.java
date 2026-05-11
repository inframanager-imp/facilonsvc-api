package com.facilon.app.module.client.service;

import com.facilon.app.module.client.dto.DocumentDto;
import com.facilon.app.service.EmailService;
import com.facilon.app.util.EmailTemplateLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for sending email notifications related to document status changes
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentNotificationService {

    private final EmailService emailService;
    private final EmailTemplateLoader templateLoader;
    
    @Value("${app.client_url:http://localhost:3000}")
    private String baseUrl;
    
    @Value("${app.support-email:support@facilon.com}")
    private String supportEmail;
    
    /**
     * Send notification when document is uploaded
     */
    public void sendDocumentUploadedNotification(String investorEmail, String investorName, DocumentDto document) {
        try {
            String subject = "Document Uploaded Successfully - Facilon Platform";
            
            Map<String, String> variables = new HashMap<>();
            variables.put("investorName", investorName);
            variables.put("documentType", getDocumentTypeLabel(document.getDocumentType()));
            variables.put("fileName", document.getOriginalFileName());
            variables.put("uploadDate", document.getUploadedAt() != null ? document.getUploadedAt().toString() : "");
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", "+91 22 1234 5678");
            
            String htmlBody = templateLoader.processTemplate("22-document-uploaded.html", variables);
            emailService.sendHtmlMessage(investorEmail, subject, htmlBody);
            
            log.info("Upload HTML notification sent to: {}", investorEmail);
        } catch (Exception e) {
            log.error("Failed to send upload notification", e);
        }
    }
    
    /**
     * Send notification when document is verified
     */
    public void sendDocumentVerifiedNotification(String investorEmail, String investorName, DocumentDto document) {
        try {
            String subject = "Document Verified ✓ - Facilon Platform";
            
            Map<String, String> variables = new HashMap<>();
            variables.put("investorName", investorName);
            variables.put("documentType", getDocumentTypeLabel(document.getDocumentType()));
            variables.put("fileName", document.getOriginalFileName());
            variables.put("verifiedDate", document.getVerifiedAt() != null ? document.getVerifiedAt().toString() : "");
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", "+91 22 1234 5678");
            
            String htmlBody = templateLoader.processTemplate("23-document-verified.html", variables);
            emailService.sendHtmlMessage(investorEmail, subject, htmlBody);
            
            log.info("Verification HTML notification sent to: {}", investorEmail);
        } catch (Exception e) {
            log.error("Failed to send verification notification", e);
        }
    }
    
    /**
     * Send notification when document is rejected
     */
    public void sendDocumentRejectedNotification(String investorEmail, String investorName, DocumentDto document) {
        try {
            String subject = "Document Requires Attention - Facilon Platform";
            
            Map<String, String> variables = new HashMap<>();
            variables.put("investorName", investorName);
            variables.put("documentType", getDocumentTypeLabel(document.getDocumentType()));
            variables.put("fileName", document.getOriginalFileName());
            variables.put("remarks", document.getRemarks() != null ? document.getRemarks() : "Please contact support for details");
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", "+91 22 1234 5678");
            
            String htmlBody = templateLoader.processTemplate("24-document-rejected.html", variables);
            emailService.sendHtmlMessage(investorEmail, subject, htmlBody);
            
            log.info("Rejection HTML notification sent to: {}", investorEmail);
        } catch (Exception e) {
            log.error("Failed to send rejection notification", e);
        }
    }
    
    /**
     * Send notification when all documents are verified
     */
    public void sendAllDocumentsVerifiedNotification(String investorEmail, String investorName) {
        try {
            String subject = "Account Verification Complete ✓ - Facilon Platform";
            
            Map<String, String> variables = new HashMap<>();
            variables.put("investorName", investorName);
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", "+91 22 1234 5678");
            
            String htmlBody = templateLoader.processTemplate("25-all-documents-verified.html", variables);
            emailService.sendHtmlMessage(investorEmail, subject, htmlBody);
            
            log.info("All documents verified HTML notification sent to: {}", investorEmail);
        } catch (Exception e) {
            log.error("Failed to send all verified notification", e);
        }
    }
    
    private String getDocumentTypeLabel(String type) {
        return switch (type) {
            case "PASSPORT" -> "Passport";
            case "PAN_CARD" -> "PAN Card";
            case "ADDRESS_PROOF" -> "Address Proof";
            case "PHOTO" -> "Passport Photo";
            case "BANK_STATEMENT" -> "Bank Statement";
            case "INCOME_PROOF" -> "Income Proof";
            default -> type;
        };
    }
}
