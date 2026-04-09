package com.facilon.app.module.client.dto.introduced;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto {
    private Boolean success;
    private String message;
    private String uniqueCode;
    private String nextStep;
    private Object data;
    private Boolean b2cAccountCreated;
    private Long investorId;
}
