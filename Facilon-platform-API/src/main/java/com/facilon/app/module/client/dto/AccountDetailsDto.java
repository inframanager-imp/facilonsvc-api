package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Account Details DTO - Comprehensive investor account information
 * Aligned with Laravel account-details.blade.php
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDetailsDto {
    // Basic Info
    private String uniqueCode;
    private String fullName;
    private String email;
    private String mobile;
    private String registrationType; // Self, Introduced, PMS
    private String investorCategory;
    private String investorCategoryCode;
    private LocalDate registrationDate;
    private LocalDate lastUpdated;
    
    // Account Opening Status
    private Boolean accountOpeningStatus; // ss_account_opening: true = Completed, false/null = Pending
    
    // Progress Tracking
    private ProgressInfo progress;
    
    // Bank and Account Details
    private BankAccountInfo bankAccount;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgressInfo {
        // Personal Information sections status
        private Boolean personalInfoComplete;
        private Boolean passportComplete;
        private Boolean residentialComplete;
        private Boolean taxInfoComplete;
        private Boolean bankDetailsComplete;
        private Boolean contactDetailsComplete;
        private Boolean nominationComplete;
        private Boolean riskProfileComplete;
        
        // Document counts
        private Integer kycDocumentsUploaded;
        private Integer kycDocumentsRequired;
        private Integer onboardingDocumentsUploaded;
        private Integer onboardingDocumentsRequired;
        
        // Verification status
        private Boolean verificationDone; // ss_verification_done
        private String verificationDoneBy; // ss_verification_done_by
        private String verificationDateTime; // ss_verificationdateandtime
        
        // Physical submission
        private Boolean physicalSubmissionDone;
        private String physicalSubmissionMethod; // inperson or courier
        private String courierName;
        private String dispatchDate;
        private String awbNumber;
        
        // Overall completion percentage
        private Integer completionPercentage;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BankAccountInfo {
        // Standard bank details (from Dataverse ss_productsvisitorses)
        private String bankName; // ss_nameofbank
        private String branchAddress; // ss_bankaddress
        private String accountNumber; // ss_bankaccountnumber (may be encrypted/masked)
        private String swiftCode; // ss_swiftcode
        private String ifscCode; // ss_ifsccode
        
        // Specialized account numbers (from Dataverse, only one will be populated based on service/product)
        private String safeKeepingAccountNo; // ss_safekeepingcustodyaccountno
        private String tradingAccountNo; // ss_tradingaccountno
        private String pmsAccountFolioNo; // ss_pmsaccountfoliono
        
        // Depository account numbers (from Dataverse ss_productsvisitorses) - NEW FIELDS
        private String nsdlDpId; // ss_depositorynsdldpid
        private String nsdlAccountNo; // ss_depositorynsdlaccountno
        private String cdslAccountNo; // ss_depositorycsdlaccountno
        
        private String accountType; // Savings, Current
        private Boolean isPrimary;
    }
}
