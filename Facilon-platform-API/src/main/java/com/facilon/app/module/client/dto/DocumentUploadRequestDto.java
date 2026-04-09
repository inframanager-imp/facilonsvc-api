package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUploadRequestDto {

    @NotBlank(message = "Document type is required")
    private String documentType; // "identity_proof", "address_proof", "pan_card", "cancelled_cheque", etc.

    @NotBlank(message = "Document category is required")
    private String documentCategory; // "kyc" or "onboarding"

    @NotBlank(message = "File name is required")
    private String fileName;

    @NotBlank(message = "File content type is required")
    private String contentType;

    @NotNull(message = "File size is required")
    private Long fileSize;

    private String description;

    // Base64 encoded file content or file path
    private String fileContent;
}
