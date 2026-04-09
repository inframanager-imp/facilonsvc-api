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
public class PhysicalSubmissionDto {

    @NotBlank(message = "Physical submission method is required")
    private String physicalSubmission; // "inperson" or "courier"

    // Courier-specific fields (required if physicalSubmission = "courier")
    private String courierName;
    private String dispatchDate; // Format: "yyyy-MM-dd" or "yyyy-MM-ddTHH:mm"
    private String awbNumber;

    // Additional fields
    private String submissionLocation;
    private String receivedBy;
    private String notes;
    private Boolean documentsComplete;
}
