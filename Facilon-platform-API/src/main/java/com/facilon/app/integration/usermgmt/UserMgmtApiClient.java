package com.facilon.app.integration.usermgmt;

import com.facilon.app.integration.usermgmt.dto.MicrosoftGraphResponseDto;
import com.facilon.app.integration.usermgmt.dto.PasswordChangeDto;
import com.facilon.app.integration.usermgmt.dto.SignUpDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@ConditionalOnProperty(name = "integration.user-mgmt.enabled", havingValue = "true")
public class UserMgmtApiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;

    public UserMgmtApiClient(
            @org.springframework.beans.factory.annotation.Value("${integration.user-mgmt.url:}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Create user in Azure AD via user-mgmt-service /create-user-latest
     */
    public MicrosoftGraphResponseDto createUserLatest(SignUpDto signUpDto) {
        if (baseUrl == null || baseUrl.isEmpty()) {
            log.warn("User-mgmt service URL not configured");
            return MicrosoftGraphResponseDto.builder().errorMsg("User-mgmt service URL not configured").build();
        }
        String url = baseUrl.replaceAll("/$", "") + "/api/v1/azure-ad/create-user-latest";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SignUpDto> entity = new HttpEntity<>(signUpDto, headers);
        try {
            ResponseEntity<MicrosoftGraphResponseDto> response = restTemplate.postForEntity(url, entity,
                    MicrosoftGraphResponseDto.class);
            log.info("User created in Azure AD via user-mgmt-service for: {}", signUpDto.getEmail());
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to create user via user-mgmt-service: {}", e.getMessage());
            return MicrosoftGraphResponseDto.builder()
                    .errorMsg("User creation failed: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Change user password in Azure AD via user-mgmt-service /change-password
     */
    public MicrosoftGraphResponseDto changePassword(PasswordChangeDto passwordChangeDto) {
        if (baseUrl == null || baseUrl.isEmpty()) {
            log.warn("User-mgmt service URL not configured");
            return MicrosoftGraphResponseDto.builder().errorMsg("User-mgmt service URL not configured").build();
        }
        String url = baseUrl.replaceAll("/$", "") + "/api/v1/azure-ad/change-password";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PasswordChangeDto> entity = new HttpEntity<>(passwordChangeDto, headers);
        try {
            ResponseEntity<MicrosoftGraphResponseDto> response = restTemplate.postForEntity(url, entity,
                    MicrosoftGraphResponseDto.class);
            log.info("Password changed in Azure AD via user-mgmt-service for userId: {}",
                    passwordChangeDto.getUserId());
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to change password via user-mgmt-service: {}", e.getMessage());
            return MicrosoftGraphResponseDto.builder()
                    .errorMsg("Password change failed: " + e.getMessage())
                    .build();
        }
    }
}
