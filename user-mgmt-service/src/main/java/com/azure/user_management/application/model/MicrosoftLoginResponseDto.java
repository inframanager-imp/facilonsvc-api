package com.azure.user_management.application.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Slf4j
public class MicrosoftLoginResponseDto {
    @JsonProperty("token_type")
    private String token_type;

    @JsonProperty("expires_in")
    private String expires_in;

    @JsonProperty("ext_expires_in")
    private String ext_expires_in;

    @JsonProperty("access_token")
    private String access_token;
}
