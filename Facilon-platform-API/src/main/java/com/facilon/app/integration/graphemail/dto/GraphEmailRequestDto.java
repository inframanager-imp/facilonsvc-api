package com.facilon.app.integration.graphemail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for Graph API email requests
 * Can be used for REST API endpoints if this module is extracted as microservice
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphEmailRequestDto {

    private String toEmail;
    private String subject;
    private String htmlContent;
    private String plainTextContent;
    
    // Optional fields
    private List<String> ccEmails;
    private List<String> bccEmails;
    private String fromEmail;
    private String fromName;
    private String replyToEmail;
    
    // Metadata
    private String firstName;
    private String lastName;
    private String templateName;
    
    // Flags
    private boolean saveToSentItems;
    private boolean requestDeliveryReceipt;
    private boolean requestReadReceipt;
    private String importance; // Low, Normal, High
}
