package com.facilon.app.module.client.model;

import com.facilon.app.model.TenantEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Investor bank account details.
 * Stores banking information for fund transfers.
 * Tenant-scoped.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "investor_bank_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorBankDetails extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Links to investor.unique_code
    @Column(name = "investor_unique_id")
    private String investorUniqueId;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_address", columnDefinition = "TEXT")
    private String bankAddress;

    // Encrypted in service layer
    @Column(name = "account_number", columnDefinition = "TEXT")
    private String accountNumber;

    @Column(name = "account_holder_name")
    private String accountHolderName;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name = "swift_code")
    private String swiftCode;

    @Column(name = "account_type")
    private String accountType; // Savings, Current

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "settlement_account_type")
    private String settlementAccountType;

    @Column(name = "bank_country")
    private Integer bankCountry;

    @Column(name = "rbi_approval")
    private String rbiApproval;

    @Column(name = "rbi_approval_order_number")
    private String rbiApprovalOrderNumber;

    @Column(name = "rbi_approval_date")
    private String rbiApprovalDate;

    @Column(name = "is_primary")
    @Builder.Default
    private Boolean isPrimary = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
