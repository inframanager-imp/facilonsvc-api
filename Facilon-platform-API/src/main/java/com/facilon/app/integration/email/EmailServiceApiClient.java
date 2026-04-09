package com.facilon.app.integration.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@ConditionalOnProperty(name = "integration.email.enabled", havingValue = "true")
public class EmailServiceApiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;

    public EmailServiceApiClient(
            @Value("${integration.email.url:}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void sendEmail(String toEmail, String firstName, String lastName, String subject, String htmlContent) {
        if (baseUrl == null || baseUrl.isEmpty()) {
            log.warn("Email service URL not configured");
            return;
        }
        String url = baseUrl.replaceAll("/$", "") + "/api/v1/email/send";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("toEmail", toEmail);
        params.add("firstName", firstName != null ? firstName : "");
        params.add("lastName", lastName != null ? lastName : "");
        params.add("subject", subject);
        params.add("data", htmlContent);
        params.add("toCC", "");
        params.add("toBCC", "");
        params.add("hasAttachment", "false");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        try {
            restTemplate.postForEntity(url, entity, String.class);
            log.info("Email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email via email-service: {}", e.getMessage());
        }
    }

    public void sendOtpEmail(String toEmail, String firstName, String otp) {
        String subject = "Your OTP for Facilon Registration";
        String html = "<p>Hello " + (firstName != null ? firstName : "") + ",</p>" +
                "<p>Your OTP for registration is: <strong>" + otp + "</strong></p>" +
                "<p>This OTP is valid for 15 minutes.</p>" +
                "<p>If you did not request this, please ignore this email.</p>";
        sendEmail(toEmail, firstName, "", subject, html);
    }

    public void sendWelcomeEmail(String toEmail, String firstName, String lastName) {
        String subject = "Welcome to Facilon";
        String html = "<p>Hello " + firstName + " " + lastName + ",</p>" +
                "<p>Welcome to Facilon! Your registration is complete.</p>" +
                "<p>You can now log in with your email and password.</p>";
        sendEmail(toEmail, firstName, lastName, subject, html);
    }
}
