package com.facilon.app.integration.sms;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * SMS integration client for sending OTP via SMS Country or compatible SMS API.
 * Aligned with Laravel Investor: POST JSON to SMS API with Text, Number, SenderId.
 */
@Component
@Slf4j
@ConditionalOnProperty(name = "integration.sms.enabled", havingValue = "true")
public class SmsServiceApiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;
    private final String authHeader;
    private final String senderId;

    public SmsServiceApiClient(
            @Value("${integration.sms.url:}") String baseUrl,
            @Value("${integration.sms.auth-header:}") String authHeader,
            @Value("${integration.sms.sender-id:FACILN}") String senderId) {
        this.baseUrl = baseUrl;
        this.authHeader = authHeader;
        this.senderId = senderId;
    }

    /**
     * Send SMS OTP to the given phone number.
     *
     * @param fullPhoneNumber E.164 style (e.g. 919876543210 for India)
     * @param otp             4-digit OTP
     */
    public void sendOtpSms(String fullPhoneNumber, String otp) {
        if (baseUrl == null || baseUrl.isEmpty()) {
            log.warn("SMS service URL not configured");
            return;
        }
        String url = baseUrl.replaceAll("/$", "");
        String text = "Dear User, Your OTP for Facilon Registration is " + otp
                + ". Do not share OTP with anyone. Team FACILON.";

        SmsRequestDto body = SmsRequestDto.builder()
                .text(text)
                .number(fullPhoneNumber)
                .senderId(senderId)
                .tool("API")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (authHeader != null && !authHeader.isEmpty()) {
            headers.set("Authorization", authHeader);
        }

        HttpEntity<SmsRequestDto> entity = new HttpEntity<>(body, headers);
        try {
            restTemplate.postForEntity(url, entity, String.class);
            log.info("SMS OTP sent to {}", fullPhoneNumber);
        } catch (Exception e) {
            log.error("Failed to send SMS via sms-service: {}", e.getMessage());
            throw new RuntimeException("SMS delivery failed: " + e.getMessage());
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SmsRequestDto {
        private String text;
        private String number;
        @JsonProperty("SenderId")
        private String senderId;
        private String tool;
    }
}
