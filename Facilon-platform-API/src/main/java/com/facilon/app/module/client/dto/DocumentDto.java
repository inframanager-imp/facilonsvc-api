package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for investor document metadata
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDto {
    private Long id;
    private String documentType;        // PASSPORT, PAN_CARD, ADDRESS_PROOF, PHOTO, OTHER
    private String fileName;
    private String originalFileName;
    private String fileExtension;
    private Long fileSize;              // in bytes
    private String sharePointItemId;    // SharePoint drive item ID for download/delete
    private String sharePointWebUrl;    // SharePoint web URL (optional)
    private String uploadedBy;          // investor unique code or username
    private LocalDateTime uploadedAt;
    private String status;              // UPLOADED, VERIFIED, REJECTED
    private String remarks;             // Admin remarks for rejection
    private String verifiedBy;          // Admin who verified/rejected
    private LocalDateTime verifiedAt;
}
