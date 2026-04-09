package com.facilon.app.module.client.model;

import com.facilon.app.model.TenantEntity;
import com.facilon.app.module.client.model.master.MasterPmsBanks;
import com.facilon.app.module.client.model.master.MasterPmsPlans;
import com.facilon.app.module.client.model.master.MasterPortfolioManagers;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * PMS-specific investor details.
 * Links investor to portfolio manager, plan, and bank.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "investor_pms_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorPmsDetails extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "investor_unique_code")
    private String investorUniqueCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pms_manager_id")
    private MasterPortfolioManagers pmsManager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pms_plan_id")
    private MasterPmsPlans pmsPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pms_bank_id")
    private MasterPmsBanks pmsBank;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "agreement_date")
    private LocalDate agreementDate;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;
}
