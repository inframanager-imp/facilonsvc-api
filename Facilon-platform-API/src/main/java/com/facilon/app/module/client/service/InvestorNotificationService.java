package com.facilon.app.module.client.service;

import com.facilon.app.service.EmailService;
import com.facilon.app.util.EmailTemplateLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for sending all investor-related email notifications.
 * Implements 9 missing email templates from docs/Investor.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InvestorNotificationService {

    private final EmailService emailService;
    private final EmailTemplateLoader templateLoader;
    
    @Value("${app.client_url:http://localhost:3000}")
    private String baseUrl;
    
    @Value("${app.support-email:support@facilon.com}")
    private String supportEmail;
    
    @Value("${app.support-phone:+91-123-456-7890}")
    private String supportPhone;
    
    /**
     * 1. Send the first-login email with a setpassword link (FISP-style).
     * The template no longer renders a plaintext password.
     */
    public void sendLoginDetailsEmail(String email, String investorName, String loginId, String setPasswordUrl, String investorCode) {
        try {
            String subject = "Facilon – Set Your Password";

            Map<String, String> variables = new HashMap<>();
            variables.put("investorName", investorName);
            variables.put("loginId", loginId);
            variables.put("investorCode", investorCode);
            variables.put("setPasswordUrl", setPasswordUrl);
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", supportPhone);

            String htmlBody = templateLoader.processTemplate("01-login-details.html", variables);
            emailService.sendHtmlMessage(email, subject, htmlBody);

            log.info("Setpassword email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send setpassword email", e);
        }
    }
    
    /**
     * 2. Notify broker/introducer that new investor was introduced
     */
    public void sendIntroducedInvestorEmail(String brokerEmail, String brokerName, String investorName, String investorEmail, String investorMobile) {
        try {
            String subject = "New Investor Introduction - " + investorName;
            
            Map<String, String> variables = new HashMap<>();
            variables.put("brokerName", brokerName);
            variables.put("investorName", investorName);
            variables.put("investorEmail", investorEmail);
            variables.put("investorMobile", investorMobile);
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            
            String htmlBody = templateLoader.processTemplate("17-introduced-investor-email.html", variables);
            emailService.sendHtmlMessage(brokerEmail, subject, htmlBody);
            
            log.info("Introduced investor HTML notification sent to broker: {}", brokerEmail);
        } catch (Exception e) {
            log.error("Failed to send introduced investor email", e);
        }
    }
    
    /**
     * 3. Notify introducer that their introduced investor has completed registration
     */
    public void sendIntroducedInvestorRegisteredEmail(String brokerEmail, String brokerName, String investorName, String investorCode) {
        try {
            String subject = "Investor Registration Complete - " + investorName;
            
            Map<String, String> variables = new HashMap<>();
            variables.put("brokerName", brokerName);
            variables.put("investorName", investorName);
            variables.put("investorCode", investorCode);
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            
            String htmlBody = templateLoader.processTemplate("18-introduced-investor-registered.html", variables);
            emailService.sendHtmlMessage(brokerEmail, subject, htmlBody);
            
            log.info("Introduced investor registered HTML notification sent to broker: {}", brokerEmail);
        } catch (Exception e) {
            log.error("Failed to send introduced investor registered email", e);
        }
    }
    
    /**
     * 3b. Send the auto-introduction email to a freshly-created Dataverse investor.
     *
     * <p>Direct port of Laravel {@code InnerPageController::latest_investor_check}
     * (and {@code _pms}) — the per-minute cron that emails investors created
     * today in Dynamics with an encrypted registration link. Mirrors the
     * {@code intro-investor-mail.blade.php} body and the
     * {@code "{firm} - Introducing Facilon"} subject.
     *
     * @param email                investor email (Dataverse {@code ss_emailintroduceind})
     * @param investorName         investor first name (Dataverse {@code ss_firstnameintroduceind})
     * @param serviceProviderName  resolved firm name (master_accounts.name)
     * @param introLink            registration link with the encrypted {@code ss_name} code
     */
    public void sendInvestorIntroductionEmail(String email, String investorName, String serviceProviderName, String introLink) {
        String subject = (serviceProviderName != null ? serviceProviderName : "Facilon") + " - Introducing Facilon";

        Map<String, String> variables = new HashMap<>();
        variables.put("name", investorName != null ? investorName : "Investor");
        variables.put("serviceProviderName", serviceProviderName != null ? serviceProviderName : "your Service Provider");
        variables.put("introLink", introLink);

        String htmlBody = templateLoader.processTemplate("19-introduced-investor-intro.html", variables);
        emailService.sendHtmlMessage(email, subject, htmlBody);

        log.info("Investor introduction email sent to: {}", email);
    }

    /**
     * 4. Send assistance email for investors outside India (Self)
     */
    public void sendOutsideIndiaAssistanceSelfEmail(String email, String investorName) {
        try {
            String subject = "Assistance for Non-Resident Investor Registration";
            
            Map<String, String> variables = new HashMap<>();
            variables.put("investorName", investorName);
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", supportPhone);
            
            String htmlBody = templateLoader.processTemplate("09-outside-india-assistance.html", variables);
            emailService.sendHtmlMessage(email, subject, htmlBody);
            
            log.info("Outside India assistance (self) HTML email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send outside India assistance email", e);
        }
    }
    
    /**
     * 5. Send assistance email for legal entities outside India
     */
    public void sendOutsideIndiaAssistanceLegalEmail(String email, String legalEntityName) {
        try {
            String subject = "Assistance for Non-Resident Legal Entity Registration";
            
            Map<String, String> variables = new HashMap<>();
            variables.put("legalEntityName", legalEntityName);
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", supportPhone);
            
            String htmlBody = templateLoader.processTemplate("19-outside-india-legal-assistance.html", variables);
            emailService.sendHtmlMessage(email, subject, htmlBody);
            
            log.info("Outside India assistance (legal) HTML email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send outside India assistance legal email", e);
        }
    }
    
    /**
     * 6. Notify Indian resident without PAN
     */
    public void sendIndiaNoPanEmail(String email, String investorName) {
        try {
            String subject = "PAN Required for Registration - Facilon Platform";
            
            Map<String, String> variables = new HashMap<>();
            variables.put("investorName", investorName);
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", supportPhone);
            
            String htmlBody = templateLoader.processTemplate("02-india-no-pan.html", variables);
            emailService.sendHtmlMessage(email, subject, htmlBody);
            
            log.info("India no PAN HTML email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send India no PAN email", e);
        }
    }
    
    /**
     * 7. Notify person of Indian origin without OCI
     */
    public void sendIndianOriginNoOciEmail(String email, String investorName) {
        try {
            String subject = "OCI Card Required for Registration - Facilon Platform";
            
            Map<String, String> variables = new HashMap<>();
            variables.put("investorName", investorName);
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", supportPhone);
            
            String htmlBody = templateLoader.processTemplate("03-indian-origin-no-oci.html", variables);
            emailService.sendHtmlMessage(email, subject, htmlBody);
            
            log.info("Indian origin no OCI HTML email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send Indian origin no OCI email", e);
        }
    }
    
    /**
     * 8. Send final submission confirmation email
     */
    public void sendFinalSubmissionEmail(String email, String investorName, String investorCode) {
        try {
            String subject = "Profile Submitted Successfully - Facilon Platform";
            
            Map<String, String> variables = new HashMap<>();
            variables.put("investorName", investorName);
            variables.put("investorCode", investorCode);
            variables.put("timestamp", String.valueOf(System.currentTimeMillis()));
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", supportPhone);
            
            String htmlBody = templateLoader.processTemplate("20-final-submission.html", variables);
            emailService.sendHtmlMessage(email, subject, htmlBody);
            
            log.info("Final submission HTML email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send final submission email", e);
        }
    }
    
    /**
     * 9. Request investor to update information
     */
    public void sendUpdateRequestEmail(String email, String investorName, String adminName, String requestedFields, String reason) {
        try {
            String subject = "Update Request - Facilon Platform";
            
            Map<String, String> variables = new HashMap<>();
            variables.put("investorName", investorName);
            variables.put("adminName", adminName);
            variables.put("requestedFields", requestedFields);
            variables.put("reason", reason);
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", supportPhone);
            
            String htmlBody = templateLoader.processTemplate("21-update-request.html", variables);
            emailService.sendHtmlMessage(email, subject, htmlBody);
            
            log.info("Update request HTML email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send update request email", e);
        }
    }

    /**
     * 10. Sample 1 - Market other than India
     */
    public void sendMarketOtherThanIndiaEmail(String email, String investorName, String market) {
        try {
            String marketSuffix = (market != null && market.toLowerCase().contains("market")) ? " Interest" : " Market Interest";
            String subject = "Thank You for Registering - " + market + marketSuffix;
            
            Map<String, String> variables = new HashMap<>();
            variables.put("investorName", investorName);
            variables.put("market", market);
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", supportPhone);
            
            String htmlBody = templateLoader.processTemplate("05-market-other-than-india.html", variables);
            emailService.sendHtmlMessage(email, subject, htmlBody);
            
            log.info("Market other than India HTML email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send market other than India email", e);
        }
    }

    /**
     * 11. Sample 6 - PAN Yes, OCI Yes (or PAN Yes for resident), not introduced by SP
     */
    public void sendSuccessfulRegistrationSelfEmail(String email, String investorName, String uniqueCode, String loginUrl) {
        try {
            String subject = "Registration Successful - Welcome to Facilon";
            
            // Use HTML template
            Map<String, String> variables = new HashMap<>();
            variables.put("investorName", investorName);
            variables.put("uniqueCode", uniqueCode);
            variables.put("loginUrl", loginUrl);
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", supportPhone);
            
            String htmlBody = templateLoader.processTemplate("06-successful-registration-self.html", variables);
            emailService.sendHtmlMessage(email, subject, htmlBody);
            
            log.info("Successful registration (self) HTML email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send successful registration email", e);
        }
    }

    /**
     * 12. Sample 7 - PAN Yes, OCI Yes, introduced by SP - Direct to appointment selection
     */
    public void sendSuccessfulRegistrationIntroducedEmail(String email, String investorName, String serviceProviderName, String appointmentUrl) {
        try {
            String subject = "Registration Complete - Schedule Your Appointment";
            
            Map<String, String> variables = new HashMap<>();
            variables.put("investorName", investorName);
            variables.put("serviceProviderName", serviceProviderName);
            variables.put("appointmentUrl", appointmentUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", supportPhone);
            
            String htmlBody = templateLoader.processTemplate("07-successful-registration-introduced.html", variables);
            emailService.sendHtmlMessage(email, subject, htmlBody);
            
            log.info("Successful registration (introduced) HTML email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send successful registration introduced email", e);
        }
    }

    /**
     * 13. Sample 8 - PAN No, introduced by SP - Notify SP
     */
    public void sendNoPanIntroducedNotificationToSP(String spEmail, String spName, String investorName, String investorEmail) {
        try {
            String subject = "Action Required - Investor PAN Missing: " + investorName;
            
            Map<String, String> variables = new HashMap<>();
            variables.put("spName", spName);
            variables.put("investorName", investorName);
            variables.put("investorEmail", investorEmail);
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", supportPhone);
            
            String htmlBody = templateLoader.processTemplate("08-no-pan-sp-notification.html", variables);
            emailService.sendHtmlMessage(spEmail, subject, htmlBody);
            
            log.info("No PAN HTML notification to SP sent to: {}", spEmail);
        } catch (Exception e) {
            log.error("Failed to send no PAN notification to SP", e);
        }
    }

    /**
     * 14. Sample 9 - OCI No, introduced by SP - Notify SP
     */
    public void sendNoOciIntroducedNotificationToSP(String spEmail, String spName, String investorName, String investorEmail) {
        try {
            String subject = "Action Required - Investor OCI Missing: " + investorName;
            
            Map<String, String> variables = new HashMap<>();
            variables.put("spName", spName);
            variables.put("investorName", investorName);
            variables.put("investorEmail", investorEmail);
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", supportPhone);
            
            String htmlBody = templateLoader.processTemplate("15-no-oci-sp-notification.html", variables);
            emailService.sendHtmlMessage(spEmail, subject, htmlBody);
            
            log.info("No OCI HTML notification to SP sent to: {}", spEmail);
        } catch (Exception e) {
            log.error("Failed to send no OCI notification to SP", e);
        }
    }

    /**
     * 15. Sample 10 - PAN No and OCI No, introduced by SP - Notify SP
     */
    public void sendNoPanAndOciIntroducedNotificationToSP(String spEmail, String spName, String investorName, String investorEmail) {
        try {
            String subject = "Action Required - Investor PAN & OCI Missing: " + investorName;
            
            Map<String, String> variables = new HashMap<>();
            variables.put("spName", spName);
            variables.put("investorName", investorName);
            variables.put("investorEmail", investorEmail);
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", supportPhone);
            
            String htmlBody = templateLoader.processTemplate("16-no-pan-oci-sp-notification.html", variables);
            emailService.sendHtmlMessage(spEmail, subject, htmlBody);
            
            log.info("No PAN and OCI HTML notification to SP sent to: {}", spEmail);
        } catch (Exception e) {
            log.error("Failed to send no PAN and OCI notification to SP", e);
        }
    }

    /**
     * 16. Sample 2 - India market, no PAN (already implemented as sendIndiaNoPanEmail)
     */

    /**
     * 17. Sample 3 - Person of Indian Origin, no OCI (already implemented as sendIndianOriginNoOciEmail)
     */

    /**
     * 18. Sample 4 - Person of Indian Origin, no PAN and no OCI
     */
    public void sendIndianOriginNoPanAndOciEmail(String email, String investorName) {
        try {
            String subject = "PAN and OCI Required - Facilon Platform";
            
            Map<String, String> variables = new HashMap<>();
            variables.put("investorName", investorName);
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", supportPhone);
            
            String htmlBody = templateLoader.processTemplate("04-indian-origin-no-pan-and-oci.html", variables);
            emailService.sendHtmlMessage(email, subject, htmlBody);
            
            log.info("Indian origin no PAN and OCI HTML email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send Indian origin no PAN and OCI email", e);
        }
    }

    /**
     * 19. Sample 5 - Foreign Individual (already handled in sendOutsideIndiaAssistanceSelfEmail)
     */

    // ==================== LEGAL ENTITY EMAIL SCENARIOS ====================

    /**
     * 20. Sample A - Legal Entity interested in market other than India
     */
    public void sendLegalEntityMarketOtherThanIndiaEmail(String email, String entityName, String market) {
        try {
            String marketSuffix = (market != null && market.toLowerCase().contains("market")) ? " Interest" : " Market Interest";
            String subject = "Thank You for Registering - " + market + marketSuffix;
            
            Map<String, String> variables = new HashMap<>();
            variables.put("entityName", entityName);
            variables.put("market", market);
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", supportPhone);
            
            String htmlBody = templateLoader.processTemplate("10-legal-entity-market-other-than-india.html", variables);
            emailService.sendHtmlMessage(email, subject, htmlBody);
            
            log.info("Legal entity market other than India HTML email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send legal entity market other than India email", e);
        }
    }

    /**
     * 21. Sample B - Legal Entity interested in India market, no PAN
     */
    public void sendLegalEntityIndiaNoPanEmail(String email, String entityName, String representativeName) {
        try {
            String subject = "PAN Required for Legal Entity Registration - Facilon Platform";
            
            Map<String, String> variables = new HashMap<>();
            variables.put("entityName", entityName);
            variables.put("representativeName", representativeName);
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", supportPhone);
            
            String htmlBody = templateLoader.processTemplate("11-legal-entity-india-no-pan.html", variables);
            emailService.sendHtmlMessage(email, subject, htmlBody);
            
            log.info("Legal entity India no PAN HTML email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send legal entity India no PAN email", e);
        }
    }

    /**
     * 22. Sample C - Foreign Legal Entity
     */
    public void sendForeignLegalEntityEmail(String email, String entityName, String representativeName) {
        try {
            String subject = "Registration Successful - Foreign Legal Entity";
            
            Map<String, String> variables = new HashMap<>();
            variables.put("entityName", entityName);
            variables.put("representativeName", representativeName);
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", supportPhone);
            
            String htmlBody = templateLoader.processTemplate("12-foreign-legal-entity.html", variables);
            emailService.sendHtmlMessage(email, subject, htmlBody);
            
            log.info("Foreign legal entity HTML email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send foreign legal entity email", e);
        }
    }

    /**
     * 23. Sample D - Legal Entity with PAN, not introduced by SP
     */
    public void sendLegalEntitySuccessfulSelfRegistrationEmail(String email, String entityName, String representativeName, String uniqueCode, String loginUrl) {
        try {
            String subject = "Registration Successful - Welcome to Facilon";
            
            // Use HTML template
            Map<String, String> variables = new HashMap<>();
            variables.put("entityName", entityName);
            variables.put("representativeName", representativeName);
            variables.put("uniqueCode", uniqueCode);
            variables.put("loginUrl", loginUrl);
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", supportPhone);
            
            String htmlBody = templateLoader.processTemplate("13-legal-entity-successful-self.html", variables);
            emailService.sendHtmlMessage(email, subject, htmlBody);
            
            log.info("Legal entity successful self-registration HTML email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send legal entity successful self-registration email", e);
        }
    }

    /**
     * 24. Sample E - Legal Entity with PAN, introduced by SP
     */
    public void sendLegalEntitySuccessfulIntroducedEmail(String email, String entityName, String representativeName, String serviceProviderName, String appointmentUrl) {
        try {
            String subject = "Registration Complete - Schedule Your Appointment";
            
            Map<String, String> variables = new HashMap<>();
            variables.put("entityName", entityName);
            variables.put("representativeName", representativeName);
            variables.put("serviceProviderName", serviceProviderName);
            variables.put("appointmentUrl", appointmentUrl);
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", supportPhone);
            
            String htmlBody = templateLoader.processTemplate("14-legal-entity-successful-introduced.html", variables);
            emailService.sendHtmlMessage(email, subject, htmlBody);
            
            log.info("Legal entity successful introduced HTML email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send legal entity successful introduced email", e);
        }
    }

    /**
     * 25. Sample F - Legal Entity no PAN, introduced by SP - Notify SP
     */
    public void sendLegalEntityNoPanNotificationToSP(String spEmail, String spName, String entityName, String representativeName, String representativeEmail) {
        try {
            String subject = "Action Required - Legal Entity PAN Missing: " + entityName;
            
            Map<String, String> variables = new HashMap<>();
            variables.put("spName", spName);
            variables.put("entityName", entityName);
            variables.put("representativeName", representativeName);
            variables.put("representativeEmail", representativeEmail);
            variables.put("baseUrl", baseUrl);
            variables.put("supportEmail", supportEmail);
            variables.put("supportPhone", supportPhone);
            
            String htmlBody = templateLoader.processTemplate("27-legal-entity-no-pan-sp-notification.html", variables);
            emailService.sendHtmlMessage(spEmail, subject, htmlBody);
            
            log.info("Legal entity no PAN HTML notification to SP sent to: {}", spEmail);
        } catch (Exception e) {
            log.error("Failed to send legal entity no PAN notification to SP", e);
        }
    }
}
