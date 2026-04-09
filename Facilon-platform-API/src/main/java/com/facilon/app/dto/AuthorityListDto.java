package com.facilon.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityListDto extends  AuditableDto {
    private Long id;
    private String authorityName;
    private String resourcePattern;
    private String description;

}
