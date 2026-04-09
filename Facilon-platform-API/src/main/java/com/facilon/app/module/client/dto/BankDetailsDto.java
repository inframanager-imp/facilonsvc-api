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
public class BankDetailsDto {

    @NotBlank(message = "Settlement account type is required")
    private String settlementAccountType; // "yes" or "no"

    // Conditional fields for Settlement Account
    private String beneficiaryName;

    @NotBlank(message = "Bank name is required")
    private String bankName;

    @NotBlank(message = "Account number is required")
    private String bankAccountNumber;

    // "savings", "current", "nre", "nro" - might be optional if settlement is NO
    private String accountType;

    @NotBlank(message = "IFSC code is required")
    private String bankIfscCode;

    @NotBlank(message = "Branch name is required")
    private String branchName;

    private String bankBranchAddress; // bank_branch_address in Laravel

    @NotNull(message = "Bank country is required")
    private Integer bankCountry;

    private String cancelledChequeDocumentId;

    private Boolean isPrimaryAccount;

    // RBI Approval Details (Conditional)
    private String rbiApproval; // "yes" or "no"
    private String rbiApprovalOrderNumber;
    private String rbiApprovalDate;
    private String femaApprovalDocument;
}
