package com.facilon.app.module.client.model.master;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Master accounts list (broker accounts).
 * Global reference data.
 */
@Entity
@Table(name = "master_accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterAccounts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    @Column(name = "ss_nsefosebiregnno", columnDefinition = "TEXT")
    private String ssNseFoSebiRegNo;

    @Column(name = "websiteurl", columnDefinition = "TEXT")
    private String websiteUrl;

    @Column(name = "_ss_broker_value", columnDefinition = "TEXT")
    private String ssBrokerValue;

    @Column(name = "accountid", unique = true)
    private String accountId;

    @Column(name = "emailaddress1", columnDefinition = "TEXT")
    private String emailAddress1;

    @Column(name = "_primarycontactid_value", columnDefinition = "TEXT")
    private String primaryContactIdValue;

    @Column(name = "address1_addressid", columnDefinition = "TEXT")
    private String address1AddressId;

    @Column(name = "ss_nsesebiregnno", columnDefinition = "TEXT")
    private String ssNseSebiRegNo;

    @Column(name = "fax", columnDefinition = "TEXT")
    private String fax;

    @Column(name = "ss_cinnumber", columnDefinition = "TEXT")
    private String ssCinNumber;

    @Column(name = "telephone1", columnDefinition = "TEXT")
    private String telephone1;

    @Column(name = "address2_addressid", columnDefinition = "TEXT")
    private String address2AddressId;

    @Column(name = "ss_panno", columnDefinition = "TEXT")
    private String ssPanNo;
}
