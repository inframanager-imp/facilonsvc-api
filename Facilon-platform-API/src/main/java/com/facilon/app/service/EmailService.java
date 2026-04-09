package com.facilon.app.service;

import com.facilon.app.integration.graphemail.GraphEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private ObjectProvider<GraphEmailService> graphEmailServiceProvider;

    private GraphEmailService graphEmailService() {
        GraphEmailService service = graphEmailServiceProvider.getIfAvailable();
        if (service == null) {
            throw new IllegalStateException("Graph email service is not enabled. Set graph.email.enabled=true");
        }
        return service;
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        String htmlBody = text == null
                ? ""
                : text.replace("&", "&amp;")
                      .replace("<", "&lt;")
                      .replace(">", "&gt;")
                      .replace("\n", "<br/>");
        sendHtmlMessage(to, subject, htmlBody);
    }

    /**
     * Send HTML email message
     * 
     * @param to Recipient email address
     * @param subject Email subject
     * @param htmlBody HTML content of the email
     */
    public void sendHtmlMessage(String to, String subject, String htmlBody) {
        try {
            boolean sent = graphEmailService().sendEmail(to, subject, htmlBody, null, null);
            if (!sent) {
                throw new RuntimeException("Graph API returned unsuccessful response");
            }
            log.info("HTML email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send HTML email to: {}", to, e);
            throw new RuntimeException("Email sending failed", e);
        }
    }
}
