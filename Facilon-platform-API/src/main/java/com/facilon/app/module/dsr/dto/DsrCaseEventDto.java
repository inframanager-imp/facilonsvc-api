package com.facilon.app.module.dsr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsrCaseEventDto {

    private String eventCode;
    private String title;
    private String note;
    private String actor;
    private String actorRole;
    private String fromStatus;
    private String toStatus;
    private boolean investorVisible;
    private String createdAt;
}
