package com.facilon.app.module.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsrCaseCreateRequestDto {

    private String requestType;
    private String jurisdiction;
    private String requestDescription;
    private String requesterName;
    private String requesterEmail;
    private String requesterPhone;
    private String requesterRole;
}
