package com.azure.user_management.application.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Slf4j
public class SignInActivityResponseDto {
    private String errorMsg;
    private Map<String, Object> objectMap;
}
