package com.facilon.app.module.client.model;

import com.facilon.app.model.TenantEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Temporary PMS investor data during public 4-step registration (docs/Investor).
 * Stores preliminary info before full registration. Tenant-scoped.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "intro_pms_investor_temp")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntroPmsInvestorTemp extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_code_db")
    private String uniqueCodeDb;

    @Column(name = "investor_register_as")  // Self=1, Legal Entity=2
    private String investorRegisterAs;
    @Column(name = "legal_entity_full_name")
    private String legalEntityFullName;
    @Column(name = "incorp_country")
    private String incorpCountry;

    @Column(name = "pms_manager_id")
    private Long pmsManagerId;
    @Column(name = "pms_plan_id")
    private Long pmsPlanId;
    @Column(name = "pms_bank_id")
    private Long pmsBankId;

    @Column(name = "intro_first_name")
    private String introFirstName;
    @Column(name = "intro_middle_name")
    private String introMiddleName;
    @Column(name = "intro_last_name")
    private String introLastName;
    @Column(name = "intro_dob")
    private LocalDate introDob;
    @Column(name = "intro_gender")
    private String introGender;
    @Column(name = "intro_email")
    private String introEmail;
    @Column(name = "intro_mobile")
    private String introMobile;
    @Column(name = "isd_code")
    private String isdCode;
    @Column(name = "diff_mob_whatsapp")
    private String diffMobWhatsapp;

    @Column(name = "status")
    private Integer status;
    @Column(name = "login_status")
    private Integer loginStatus;
    @Column(name = "password")
    private String password;

    @Column(name = "agree_for_whatsapp")
    private Boolean agreeForWhatsapp;
    @Column(name = "agree_for_marketing")
    private Boolean agreeForMarketing;
    @Column(name = "agree_privacy")
    private Boolean agreePrivacy;
    @Column(name = "agree_terms")
    private Boolean agreeTerms;
}
