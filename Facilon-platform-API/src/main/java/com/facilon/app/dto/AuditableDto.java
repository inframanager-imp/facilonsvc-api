package com.facilon.app.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public abstract class AuditableDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private AuthorizedUserDto createdBy;
    private LocalDateTime createdDate;
    private AuthorizedUserDto modifiedBy;
    private LocalDateTime modifiedDate;

}