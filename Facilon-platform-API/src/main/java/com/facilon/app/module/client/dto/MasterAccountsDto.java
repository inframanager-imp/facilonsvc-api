package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterAccountsDto {

    private Long id;
    private String name;
    private String ssNseFoSebiRegNo;
    private String websiteUrl;
    private String ssBrokerValue;
    private String accountId;
    private String emailAddress1;
    private String primaryContactIdValue;
    private String address1AddressId;
    private String ssNseSebiRegNo;
    private String fax;
    private String ssCinNumber;
    private String telephone1;
    private String address2AddressId;
    private String ssPanNo;
}
