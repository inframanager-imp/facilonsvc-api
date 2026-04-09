package com.facilon.app.integration.graphemail;

import com.facilon.app.model.TenantGraphEmailConfig;
import com.facilon.app.service.TenantGraphEmailConfigService;
import com.facilon.app.util.EmailTemplateLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * Microsoft Graph API Email Service
 * Sends emails using Microsoft 365 Graph API instead of SMTP.
 * Can be extracted as a microservice later.
 * 
 * Required Azure Permissions:
 * - Mail.Send (Application)
 * - Mail.ReadWrite (Application)
 * 
 * Configuration:
 * graph.email.enabled=true
 * graph.email.client-id=<azure-app-client-id>
 * graph.email.client-secret=<azure-app-client-secret>
 * graph.email.tenant-id=<azure-tenant-id>
 * graph.email.sender-email=noreply@yourdomain.com
 * graph.email.sender-name=Facilon Platform
 */
@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "graph.email.enabled", havingValue = "true")
public class GraphEmailService {

    private static final String GRAPH_BASE = "https://graph.microsoft.com/v1.0";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private final GraphEmailTokenProvider tokenProvider;
    private final EmailTemplateLoader emailTemplateLoader;
    private final TenantGraphEmailConfigService configService;

    /**
     * Send email using Microsoft Graph API
     * 
     * @param toEmail Recipient email address
     * @param subject Email subject
     * @param htmlContent HTML content of email
     * @param firstName Recipient first name (optional)
     * @param lastName Recipient last name (optional)
     * @return true if sent successfully
     */
    public boolean sendEmail(String toEmail, String subject, String htmlContent, String firstName, String lastName) {
        try {
            TenantGraphEmailConfig cfg = configService.getConfigForCurrentTenantOrDefault();
            String senderEmail = cfg != null && cfg.getSenderEmail() != null ? cfg.getSenderEmail() : "noreply@facilon.com";
            String token = tokenProvider.getGraphToken();
            String url = GRAPH_BASE + "/users/" + senderEmail + "/sendMail";

            Map<String, Object> emailPayload = buildEmailPayload(toEmail, subject, htmlContent, null, null, null);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(emailPayload, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email sent successfully via Graph API to: {}", toEmail);
                return true;
            } else {
                log.error("Failed to send email via Graph API. Status: {}", response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            log.error("Error sending email via Graph API to {}: {}", toEmail, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Send email with CC and BCC recipients
     */
    public boolean sendEmail(String toEmail, String subject, String htmlContent, 
                             List<String> ccEmails, List<String> bccEmails, 
                             String fromEmail, String replyToEmail) {
        try {
            TenantGraphEmailConfig cfg = configService.getConfigForCurrentTenantOrDefault();
            String defaultSender = cfg != null && cfg.getSenderEmail() != null ? cfg.getSenderEmail() : "noreply@facilon.com";
            String token = tokenProvider.getGraphToken();
            String senderEmail = (fromEmail != null && !fromEmail.isEmpty()) ? fromEmail : defaultSender;
            String url = GRAPH_BASE + "/users/" + senderEmail + "/sendMail";

            Map<String, Object> emailPayload = buildEmailPayload(toEmail, subject, htmlContent, ccEmails, bccEmails, replyToEmail);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(emailPayload, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email sent successfully via Graph API to: {} (with CC/BCC)", toEmail);
                return true;
            } else {
                log.error("Failed to send email via Graph API. Status: {}", response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            log.error("Error sending email via Graph API: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Send OTP email
     */
    public boolean sendOtpEmail(String toEmail, String firstName, String otp) {
        String subject = "Facilon – OTP Verification";
        String htmlContent = buildOtpEmailHtml(firstName, otp);
        return sendEmail(toEmail, subject, htmlContent, firstName, null);
    }

    /**
     * Send welcome email
     */
    public boolean sendWelcomeEmail(String toEmail, String firstName, String lastName) {
        String subject = "Welcome to Facilon";
        String htmlContent = buildWelcomeEmailHtml(firstName, lastName);
        return sendEmail(toEmail, subject, htmlContent, firstName, lastName);
    }

    /**
     * Send login details email with temporary password
     */
    public boolean sendLoginDetailsEmail(String toEmail, String investorName, String loginId, String temporaryPassword, String baseUrl) {
        String subject = "Facilon - Your Login Credentials";
        String htmlContent = buildLoginDetailsEmailHtml(investorName, loginId, temporaryPassword, baseUrl);
        return sendEmail(toEmail, subject, htmlContent, investorName, null);
    }

    /**
     * Send email with attachments (Graph API supports this)
     */
    public boolean sendEmailWithAttachments(String toEmail, String subject, String htmlContent, 
                                           List<MultipartFile> attachments) {
        try {
            TenantGraphEmailConfig cfg = configService.getConfigForCurrentTenantOrDefault();
            String senderEmail = cfg != null && cfg.getSenderEmail() != null ? cfg.getSenderEmail() : "noreply@facilon.com";
            String token = tokenProvider.getGraphToken();
            String url = GRAPH_BASE + "/users/" + senderEmail + "/sendMail";

            Map<String, Object> emailPayload = buildEmailPayload(toEmail, subject, htmlContent, null, null, null);
            
            // Add attachments
            if (attachments != null && !attachments.isEmpty()) {
                List<Map<String, Object>> attachmentsList = new ArrayList<>();
                for (MultipartFile file : attachments) {
                    Map<String, Object> attachment = new HashMap<>();
                    attachment.put("@odata.type", "#microsoft.graph.fileAttachment");
                    attachment.put("name", file.getOriginalFilename());
                    attachment.put("contentType", file.getContentType());
                    attachment.put("contentBytes", Base64.getEncoder().encodeToString(file.getBytes()));
                    attachmentsList.add(attachment);
                }
                ((Map<String, Object>) emailPayload.get("message")).put("attachments", attachmentsList);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(emailPayload, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email with attachments sent successfully via Graph API to: {}", toEmail);
                return true;
            } else {
                log.error("Failed to send email with attachments. Status: {}", response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            log.error("Error sending email with attachments: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Build email payload for Graph API
     */
    private Map<String, Object> buildEmailPayload(String toEmail, String subject, String htmlContent,
                                                   List<String> ccEmails, List<String> bccEmails, 
                                                   String replyToEmail) {
        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> message = new HashMap<>();

        // Subject
        message.put("subject", subject);

        // Body
        Map<String, Object> body = new HashMap<>();
        body.put("contentType", "HTML");
        body.put("content", htmlContent);
        message.put("body", body);

        // To recipients
        List<Map<String, Object>> toRecipients = new ArrayList<>();
        Map<String, Object> toRecipient = new HashMap<>();
        Map<String, Object> emailAddress = new HashMap<>();
        emailAddress.put("address", toEmail);
        toRecipient.put("emailAddress", emailAddress);
        toRecipients.add(toRecipient);
        message.put("toRecipients", toRecipients);

        // CC recipients
        if (ccEmails != null && !ccEmails.isEmpty()) {
            List<Map<String, Object>> ccRecipients = new ArrayList<>();
            for (String cc : ccEmails) {
                if (cc != null && !cc.trim().isEmpty()) {
                    Map<String, Object> ccRecipient = new HashMap<>();
                    Map<String, Object> ccEmailAddress = new HashMap<>();
                    ccEmailAddress.put("address", cc.trim());
                    ccRecipient.put("emailAddress", ccEmailAddress);
                    ccRecipients.add(ccRecipient);
                }
            }
            if (!ccRecipients.isEmpty()) {
                message.put("ccRecipients", ccRecipients);
            }
        }

        // BCC recipients
        if (bccEmails != null && !bccEmails.isEmpty()) {
            List<Map<String, Object>> bccRecipients = new ArrayList<>();
            for (String bcc : bccEmails) {
                if (bcc != null && !bcc.trim().isEmpty()) {
                    Map<String, Object> bccRecipient = new HashMap<>();
                    Map<String, Object> bccEmailAddress = new HashMap<>();
                    bccEmailAddress.put("address", bcc.trim());
                    bccRecipient.put("emailAddress", bccEmailAddress);
                    bccRecipients.add(bccRecipient);
                }
            }
            if (!bccRecipients.isEmpty()) {
                message.put("bccRecipients", bccRecipients);
            }
        }

        // Reply-to
        if (replyToEmail != null && !replyToEmail.isEmpty()) {
            List<Map<String, Object>> replyToList = new ArrayList<>();
            Map<String, Object> replyTo = new HashMap<>();
            Map<String, Object> replyToAddress = new HashMap<>();
            replyToAddress.put("address", replyToEmail);
            replyTo.put("emailAddress", replyToAddress);
            replyToList.add(replyTo);
            message.put("replyTo", replyToList);
        }

        TenantGraphEmailConfig cfg = configService.getConfigForCurrentTenantOrDefault();
        String senderEmail = cfg != null && cfg.getSenderEmail() != null ? cfg.getSenderEmail() : "noreply@facilon.com";
        String senderName = cfg != null && cfg.getSenderName() != null ? cfg.getSenderName() : "Facilon Platform";
        Map<String, Object> from = new HashMap<>();
        Map<String, Object> fromAddress = new HashMap<>();
        fromAddress.put("address", senderEmail);
        fromAddress.put("name", senderName);
        from.put("emailAddress", fromAddress);
        message.put("from", from);

        payload.put("message", message);
        payload.put("saveToSentItems", true);

        return payload;
    }

    /**
     * Build OTP email HTML
     */
    private String buildOtpEmailHtml(String firstName, String otp) {
        Map<String, String> variables = new HashMap<>();
        variables.put("recipientName", firstName != null ? firstName : "User");
        variables.put("otpCode", otp);
        variables.put("expiryMinutes", "15");
        return emailTemplateLoader.processTemplate("28-otp-verification.html", variables);
    }

    /**
     * Build welcome email HTML
     */
    private String buildWelcomeEmailHtml(String firstName, String lastName) {
        String fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
        Map<String, String> variables = new HashMap<>();
        variables.put("fullName", fullName.trim());
        return emailTemplateLoader.processTemplate("29-welcome-email.html", variables);
    }

    /**
     * Build login details email HTML
     */
    private String buildLoginDetailsEmailHtml(String investorName, String loginId, String temporaryPassword, String baseUrl) {
        Map<String, String> variables = new HashMap<>();
        variables.put("investorName", investorName != null ? investorName : "Investor");
        variables.put("loginId", loginId);
        variables.put("temporaryPassword", temporaryPassword);
        variables.put("baseUrl", baseUrl != null ? baseUrl : "http://localhost:3000");
        variables.put("supportEmail", "support@facilon.com");
        return emailTemplateLoader.processTemplate("01-login-details.html", variables);
    }
}
