package com.facilon.app.integration.graphemail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Graph API email response
 * Can be used for REST API endpoints if this module is extracted as microservice
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphEmailResponseDto {

    private boolean success;
    private String message;
    private String messageId;
    private LocalDateTime sentAt;
    private String errorCode;
    private String errorDetails;
    
    public static GraphEmailResponseDto success(String messageId) {
        return GraphEmailResponseDto.builder()
                .success(true)
                .message("Email sent successfully via Microsoft Graph API")
                .messageId(messageId)
                .sentAt(LocalDateTime.now())
                .build();
    }
    
    public static GraphEmailResponseDto error(String errorCode, String errorDetails) {
        return GraphEmailResponseDto.builder()
                .success(false)
                .message("Failed to send email via Microsoft Graph API")
                .errorCode(errorCode)
                .errorDetails(errorDetails)
                .sentAt(LocalDateTime.now())
                .build();
    }
}
