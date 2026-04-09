package com.facilon.app.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
public abstract class TenantEntityDto extends AuditableDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private TenantDto tenant;

}
