package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponseDto {

    private Long id;
    private String documentType;
    private String documentCategory;
    private String fileName;
    private String documentUrl; // Document URL (alias for sharePointUrl for compatibility)
    private String contentType;
    private Long fileSize;
    private String status; // "pending", "approved", "rejected"
    private String sharePointUrl;
    private String sharePointDocumentId;
    private String uploadedAt;
    private String description;
    private String rejectionReason;
}
