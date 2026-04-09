package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for KYC document requirements
 * Represents the structure of required documents and their upload status
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycDocumentRequirementDto {
    
    private Integer totalRequired;
    private Integer uploaded;
    private Integer pending;
    private Integer approved;
    private Integer rejected;
    private Double completionPercentage;
    private String serviceProviderName;
    private List<RequiredDocument> documents;
    
    /**
     * Nested class representing individual required document
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequiredDocument {
        private String dynamicsId;
        private String description;
        private String documentTypeCode;
        private Integer documentType; // Numeric ss_documenttype from Dataverse (e.g., 100000012 for downloadable forms)
        private Boolean mandatory;
        private Boolean localRecordExists;
        private String localStatus;
        private String reason;
        private Long localDocumentId;
        private String fileName;
        private String documentUrl;
        private String documentMasterUrl; // SharePoint URL from ss_documentmasters.ss_documenturl (for downloadable templates)
        private String uploadedAt;
        private java.util.List<String> acceptedFormats;
        private String maxSize;
        private String inputId; // For frontend input field identification
        private String spanId; // For frontend span identification
        private String errorId; // For frontend error message identification
        private String fileInputName; // For frontend file input name
    }
}
