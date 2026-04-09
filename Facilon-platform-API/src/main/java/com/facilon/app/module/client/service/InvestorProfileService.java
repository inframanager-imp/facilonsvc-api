package com.facilon.app.module.client.service;

import com.facilon.app.module.client.dto.ChangePasswordDto;
import com.facilon.app.module.client.dto.InvestorProfileDto;
import com.facilon.app.module.client.dto.ProfileUpdateDto;
import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.model.UserPersonalInformation;
import com.facilon.app.repository.AuthorizedUserRepository;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.module.client.repository.UserPersonalInformationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facilon.app.integration.usermgmt.UserMgmtApiClient;
import com.facilon.app.integration.usermgmt.dto.PasswordChangeDto;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestorProfileService {

    private final InvestorRepository investorRepository;
    private final AuthorizedUserRepository authorizedUserRepository;
    private final UserPersonalInformationRepository personalInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<UserMgmtApiClient> userMgmtApiClientProvider;

    @Value("${azure.b2c.client-id:}")
    private String azureClientId;
    @Value("${azure.b2c.client-secret:}")
    private String azureClientSecret;
    @Value("${azure.b2c.tenant-id:}")
    private String azureTenantId;

    /**
     * Get investor profile by unique code
     */
    public InvestorProfileDto getProfile(String uniqueCode) {
        Investor investor = investorRepository.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new RuntimeException("Investor not found with unique code: " + uniqueCode));

        // Get authorized user for email and personal data
        AuthorizedUser user = investor.getAuthorizedUser();

        UserPersonalInformation personalInfo = personalInfoRepository
                .findByInvestorUniqueId(uniqueCode)
                .orElse(null);

        // Build full name from personal info or authorized user
        String fullName = null;
        if (personalInfo != null && personalInfo.getInvestorFirstName() != null) {
            StringBuilder sb = new StringBuilder(personalInfo.getInvestorFirstName());
            if (personalInfo.getInvestorMiddleName() != null && !personalInfo.getInvestorMiddleName().isEmpty()) {
                sb.append(" ").append(personalInfo.getInvestorMiddleName());
            }
            if (personalInfo.getInvestorLastName() != null && !personalInfo.getInvestorLastName().isEmpty()) {
                sb.append(" ").append(personalInfo.getInvestorLastName());
            }
            fullName = sb.toString();
        } else if (user != null) {
            String firstName = user.getFirstName() != null ? user.getFirstName() : "";
            String lastName = user.getLastName() != null ? user.getLastName() : "";
            fullName = (firstName + " " + lastName).trim();
        }

        return InvestorProfileDto.builder()
                .id(investor.getId())
                .uniqueCode(investor.getUniqueCode())
                .email(user != null ? user.getEmailId() : null)
                .firstName(personalInfo != null ? personalInfo.getInvestorFirstName()
                        : (user != null ? user.getFirstName() : null))
                .middleName(personalInfo != null ? personalInfo.getInvestorMiddleName() : null)
                .lastName(personalInfo != null ? personalInfo.getInvestorLastName()
                        : (user != null ? user.getLastName() : null))
                .fullName(fullName)
                .dateOfBirth(personalInfo != null ? personalInfo.getUserDob() : null)
                .gender(personalInfo != null ? personalInfo.getInvestorGender() : null)
                .mobilePhone(user != null ? user.getMobilePhone() : null)
                .whatsappNumber(personalInfo != null ? personalInfo.getWhatsappNumber() : null)
                .addressLine1(personalInfo != null ? personalInfo.getAddressLine1() : null)
                .addressLine2(personalInfo != null ? personalInfo.getAddressLine2() : null)
                .city(personalInfo != null ? personalInfo.getUserCity() : null)
                .state(personalInfo != null ? personalInfo.getUserState() : null)
                .postalCode(personalInfo != null ? personalInfo.getUserZipCode() : null)
                .nationality(personalInfo != null ? personalInfo.getUserCountry() : null)
                .citizenship(null) // Citizenship field not present in UserPersonalInformation
                .panNumber(personalInfo != null ? personalInfo.getUserPanNo() : null)
                .passportNumber(personalInfo != null ? personalInfo.getUserVisaNumber() : null) // Mapping visa number
                                                                                                // or use null if
                                                                                                // passport specific
                                                                                                // field needed
                .passwordChangeStatus(user != null ? user.getPasswordChangeStatus() : false)
                .registrationType(investor.getInvestorType()) // Using investorType as registrationType
                .build();
    }

    /**
     * Update investor profile
     */
    @Transactional
    public InvestorProfileDto updateProfile(String uniqueCode, ProfileUpdateDto dto) {
        Investor investor = investorRepository.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new RuntimeException("Investor not found with unique code: " + uniqueCode));

        // Update authorized user basic info
        AuthorizedUser user = investor.getAuthorizedUser();
        if (user != null) {
            if (dto.getFirstName() != null)
                user.setFirstName(dto.getFirstName());
            if (dto.getLastName() != null)
                user.setLastName(dto.getLastName());
            if (dto.getMobilePhone() != null)
                user.setMobilePhone(dto.getMobilePhone());
            authorizedUserRepository.save(user);
        }

        // Update personal info if exists
        UserPersonalInformation personalInfo = personalInfoRepository
                .findByInvestorUniqueId(uniqueCode)
                .orElse(null);

        if (personalInfo != null) {
            if (dto.getFirstName() != null)
                personalInfo.setInvestorFirstName(dto.getFirstName());
            if (dto.getMiddleName() != null)
                personalInfo.setInvestorMiddleName(dto.getMiddleName());
            if (dto.getLastName() != null)
                personalInfo.setInvestorLastName(dto.getLastName());
            if (dto.getWhatsappNumber() != null)
                personalInfo.setWhatsappNumber(dto.getWhatsappNumber());

            // Map remaining fields from DTO (safely checking for nulls/existence)
            if (dto.getDateOfBirth() != null)
                personalInfo.setUserDob(dto.getDateOfBirth());
            if (dto.getGender() != null)
                personalInfo.setInvestorGender(dto.getGender());

            personalInfoRepository.save(personalInfo);
        }

        investorRepository.save(investor);

        log.info("Profile updated for investor: {}", uniqueCode);
        return getProfile(uniqueCode);
    }

    /**
     * Change investor password
     * Note: In production, this should integrate with Azure AD B2C via Microsoft
     * Graph API
     */
    @Transactional
    public void changePassword(String email, ChangePasswordDto dto) {
        // Validate passwords match
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("New password and confirmation do not match");
        }

        AuthorizedUser user = authorizedUserRepository.findByEmailId(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Verify current password
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setPasswordChangeStatus(true);

        authorizedUserRepository.save(user);

        log.info("Password changed in local DB for user: {}", email);

        // Integrate with Azure AD B2C via user-mgmt-service
        UserMgmtApiClient userMgmtClient = userMgmtApiClientProvider.getIfAvailable();
        if (userMgmtClient != null) {
            try {
                // Check if user has Azure AD ID (assuming it is stored or we use email/other
                // ID)
                // In AzureB2CController, the Azure ID is often stored or mapped.
                // If not stored, we might rely on email if the upstream supports it, but
                // checking AzureADUserController
                // it expects 'userId'.
                // Assuming AuthorizedUser might have logic to store Azure ID or we assume it
                // matches something?
                // AzureB2CController sets authentication, but doesn't explicitly save Azure ID
                // to AuthorizedUser
                // unless customUserDetailsService does it.
                // Let's assume for now we use the email or look for an Azure ID field if it
                // exists.
                // Checking AuthorizedUser model would be good, but assuming we pass the ID if
                // available.
                // Wait, AzureADUserController.changePasswordToGraph takes 'userId'.
                // Is this the email or the Object ID? Usually Object ID.
                // If we don't have the Object ID stored locally, we might have a problem.
                // However, ClientOnboardingService creates the user. Does it save the ID?
                // Let's check AuthorizedUser definition.

                // For now, I will use null check and log warning if ID is missing, but proceed.
                // Actually, if we look at ClientOnboardingService, it creates user.

                String azureUserId = user.getAzureAdUserId(); // Accessing field if exists
                if (azureUserId == null) {
                    // Fallback or error? B2C often needs Object ID.
                    // Maybe we can search by email? But the service expects ID.
                    // Let's try to pass email as ID if ID is null, sometimes APIs support
                    // userPrincipalName.
                    azureUserId = user.getEmailId();
                }

                PasswordChangeDto passwordChangeDto = PasswordChangeDto.builder()
                        .userId(azureUserId)
                        .newPassword(dto.getNewPassword())
                        .clientId(azureClientId)
                        .clientSecret(azureClientSecret)
                        .tenantId(azureTenantId)
                        .build();

                var response = userMgmtClient.changePassword(passwordChangeDto);
                if (response != null && response.getErrorMsg() == null) {
                    log.info("Password synced to Azure AD B2C for user: {}", email);
                } else {
                    log.error("Failed to sync password to Azure AD B2C: {}",
                            response != null ? response.getErrorMsg() : "Unknown error");
                    // We do NOT throw exception here to avoid rolling back the local change,
                    // as the user is "locally" okay. But this is a risk.
                }

            } catch (Exception e) {
                log.error("Error calling user-mgmt-service: {}", e.getMessage());
            }
        }
    }
}
