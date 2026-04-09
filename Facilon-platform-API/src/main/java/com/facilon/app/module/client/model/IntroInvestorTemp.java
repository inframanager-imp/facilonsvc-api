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
 * Temporary investor introduction data during onboarding.
 * Stores preliminary information before full registration.
 * Tenant-scoped.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "intro_investor_temp")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntroInvestorTemp extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "intro_first_name", columnDefinition = "TEXT")
    private String introFirstName;

    @Column(name = "intro_middle_name", columnDefinition = "TEXT")
    private String introMiddleName;

    @Column(name = "intro_last_name", columnDefinition = "TEXT")
    private String introLastName;

    @Column(name = "intro_gender", columnDefinition = "TEXT")
    private String introGender;

    @Column(name = "intro_email", columnDefinition = "TEXT")
    private String introEmail;

    @Column(name = "intro_mobile", columnDefinition = "TEXT")
    private String introMobile;

    @Column(name = "intro_dv_investor_ss_id", columnDefinition = "TEXT")
    private String introDvInvestorSsId;

    @Column(name = "intro_dv_nationality", columnDefinition = "TEXT")
    private String introDvNationality;

    @Column(name = "intro_investorid", columnDefinition = "TEXT")
    private String introInvestorId;

    @Column(name = "ss_broker_value", columnDefinition = "TEXT")
    private String ssBrokerValue;

    @Column(name = "broker_prefferedbank", columnDefinition = "TEXT")
    private String brokerPreferredBank;

    @Column(name = "invest_route_value", columnDefinition = "TEXT")
    private String investRouteValue;

    @Column(name = "service_provider_type", columnDefinition = "TEXT")
    private String serviceProviderType;

    @Column(name = "ss_iprecords", columnDefinition = "TEXT")
    private String ssIpRecords;

    @Column(name = "ss_applicabletoslt", columnDefinition = "TEXT")
    private String ssApplicableToSlt;

    @Column(name = "ss_investortype_value", columnDefinition = "TEXT")
    private String ssInvestorTypeValue;

    @Column(name = "ss_product_value", columnDefinition = "TEXT")
    private String ssProductValue;

    @Column(name = "_ss_brokerageplan_value", columnDefinition = "TEXT")
    private String ssBrokeragePlanValue;

    @Column(name = "investor_register_as", columnDefinition = "TEXT")
    private String investorRegisterAs;

    @Column(name = "legal_entity_full_name", columnDefinition = "TEXT")
    private String legalEntityFullName;

    @Column(name = "incorp_country", columnDefinition = "TEXT")
    private String incorpCountry;

    @Column(name = "isd_code", columnDefinition = "TEXT")
    private String isdCode;

    @Column(name = "email_otp", columnDefinition = "TEXT")
    private String emailOtp;

    @Column(name = "mobile_otp", columnDefinition = "TEXT")
    private String mobileOtp;

    @Column(name = "unique_code_db", columnDefinition = "TEXT")
    private String uniqueCodeDb;

    @Column(name = "status")
    private Integer status;

    @Column(name = "diff_mob_whatsapp", columnDefinition = "TEXT")
    private String diffMobWhatsapp;

    @Column(name = "ss_contactid", columnDefinition = "TEXT")
    private String ssContactId;

    @Column(name = "agree_for_whatsapp")
    private Integer agreeForWhatsapp;

    @Column(name = "password", columnDefinition = "TEXT")
    private String password;

    @Column(name = "abandon_status", columnDefinition = "TEXT")
    private String abandonStatus;

    @Column(name = "abandon_reason", columnDefinition = "TEXT")
    private String abandonReason;

    @Column(name = "abandon_date")
    private LocalDate abandonDate;

    @Column(name = "login_status")
    @Builder.Default
    private Integer loginStatus = 0;

    @Column(name = "intro_scheme_name", columnDefinition = "TEXT")
    private String introSchemeName;

    @Column(name = "intro_country_of_residence", columnDefinition = "TEXT")
    private String introCountryOfResidence;

    @Column(name = "sp_type")
    @Builder.Default
    private Integer spType = 1;

    @Column(name = "ss_account_opening")
    @Builder.Default
    private Boolean ssAccountOpening = false;
}
