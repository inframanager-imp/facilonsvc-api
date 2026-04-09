package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response after uploading document
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUploadResponseDto {
    private Long documentId;
    private String fileName;
    private String documentType;
    private String status;
    private String message;
    
    public static DocumentUploadResponseDto success(Long id, String fileName, String type) {
        return new DocumentUploadResponseDto(id, fileName, type, "SUCCESS", "Document uploaded successfully");
    }
    
    public static DocumentUploadResponseDto error(String fileName, String message) {
        return new DocumentUploadResponseDto(null, fileName, null, "ERROR", message);
    }
}
