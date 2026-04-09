package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBankDetailsDto {
    private String settlementAccountType;
    private String beneficiaryName;

    private String bankName;
    private String bankAccountNumber;
    private String accountNumber;
    private String accountHolderName;
    private String accountType;
    private String bankIfscCode;
    private String ifscCode;
    private String swiftCode;
    private String branchName;
    private String bankBranchAddress;
    private String branchAddress;
    private Integer bankCountry;
    private Boolean isPrimaryAccount;
    private Boolean isPrimary;

    private String rbiApproval;
    private String rbiApprovalOrderNumber;
    private String rbiApprovalDate;

    // Laravel parity: structured bank branch address
    private String bankDetailsCity;
    private String bankDetailsState;
    private String bankDetailsCountry;
    private String bankDetailsZipCode;
    private String bankDetailsMicr;
}
