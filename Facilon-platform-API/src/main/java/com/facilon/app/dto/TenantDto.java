package com.facilon.app.dto;

import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@EqualsAndHashCode
public class TenantDto {

    private Long tenantId;

    @Size(max = 256)

    private String tenantName;

    @Size(max = 256)

    private String emailDomain;

    @Size(max = 256)
    private String address;
    @Size(max = 256)
    private String state;

    @Size(max = 256)

    private String postalCode;

    @Size(max = 256)

    private String country;

    private Boolean isActive = true;
}
