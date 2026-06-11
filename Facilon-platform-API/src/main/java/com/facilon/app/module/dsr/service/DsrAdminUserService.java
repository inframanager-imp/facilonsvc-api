package com.facilon.app.module.dsr.service;

import com.facilon.app.integration.graphemail.GraphEmailService;
import com.facilon.app.integration.usermgmt.UserMgmtApiClient;
import com.facilon.app.integration.usermgmt.dto.MicrosoftGraphResponseDto;
import com.facilon.app.integration.usermgmt.dto.SignUpDto;
import com.facilon.app.model.AuthorityList;
import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.model.RoleList;
import com.facilon.app.model.TenantB2CConfig;
import com.facilon.app.model.UserGroup;
import com.facilon.app.module.dsr.dto.DsrAdminRegisterRequestDto;
import com.facilon.app.module.dsr.dto.DsrAdminUserDto;
import com.facilon.app.repository.AuthorityListRepository;
import com.facilon.app.repository.AuthorizedUserRepository;
import com.facilon.app.repository.RoleListRepository;
import com.facilon.app.repository.UserGroupRepository;
import com.facilon.app.service.TenantB2CConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Registration and listing of DSR Admin (Privacy Ops) users.
 *
 * <p>Follows the platform's group -> role -> authority chain: a registered user is added
 * to the {@code DSR_Admins} group, which carries the {@code DSR_ADMIN} role and authority.
 * The chain is bootstrapped idempotently on first registration, so no manual DB seeding
 * is required.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DsrAdminUserService {

    public static final String AUTHORITY_DSR_ADMIN = "DSR_ADMIN";
    public static final String ROLE_DSR_ADMIN = "DSR_ADMIN";
    public static final String GROUP_DSR_ADMINS = "DSR_Admins";

    private final AuthorizedUserRepository authorizedUserRepository;
    private final UserGroupRepository userGroupRepository;
    private final RoleListRepository roleListRepository;
    private final AuthorityListRepository authorityListRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<UserMgmtApiClient> userMgmtClientProvider;
    private final ObjectProvider<GraphEmailService> graphEmailServiceProvider;
    private final TenantB2CConfigService tenantB2CConfigService;

    @Value("${app.client_url:http://localhost:3000}")
    private String clientUrl;

    public DsrAdminUserDto register(DsrAdminRegisterRequestDto dto) {
        validate(dto);

        if (authorizedUserRepository.findByLoginId(dto.getLoginId().trim()).isPresent()) {
            throw new IllegalArgumentException("Login ID '" + dto.getLoginId() + "' already exists");
        }
        if (authorizedUserRepository.findByEmailId(dto.getEmailId().trim()).isPresent()) {
            throw new IllegalArgumentException("Email '" + dto.getEmailId() + "' already exists");
        }

        UserGroup dsrAdminGroup = ensureDsrAdminGroup();

        AuthorizedUser user = AuthorizedUser.builder()
                .firstName(dto.getFirstName().trim())
                .lastName(dto.getLastName().trim())
                .emailId(dto.getEmailId().trim())
                .mobilePhone(dto.getMobilePhone() != null ? dto.getMobilePhone().trim() : null)
                .loginId(dto.getLoginId().trim())
                .password(passwordEncoder.encode(dto.getPassword()))
                .mustChangePassword(true)
                .isActive(dto.getActive() == null || dto.getActive())
                .build();
        user.getUserGroups().add(dsrAdminGroup);

        AuthorizedUser saved = authorizedUserRepository.save(user);
        log.info("Registered DSR Admin user {} ({}) in group {}",
                saved.getId(), saved.getLoginId(), GROUP_DSR_ADMINS);

        // Same flow as investor self-registration: create the Azure B2C account and email a
        // FISP-style set-password link. B2C failure must not fail the registration - the local
        // login path still works with the initial password.
        String azureUserId = createAzureUser(saved, dto.getPassword());
        if (azureUserId != null) {
            sendLoginDetailsEmail(saved.getEmailId(),
                    saved.getFirstName() + " " + saved.getLastName(),
                    saved.getLoginId(), buildSetPasswordUrl(azureUserId));
        } else {
            log.warn("Set-password email NOT sent for DSR admin {} because Azure user creation failed",
                    saved.getEmailId());
        }

        return toDto(saved);
    }

    /** Profile of the logged-in admin (for the dashboard welcome banner). */
    @Transactional(readOnly = true)
    public DsrAdminUserDto me(Long userId) {
        AuthorizedUser user = authorizedUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return toDto(user);
    }

    @Transactional(readOnly = true)
    public List<DsrAdminUserDto> list() {
        return authorizedUserRepository.findByUserGroups_GroupName(GROUP_DSR_ADMINS).stream()
                .map(this::toDto)
                .toList();
    }

    /** Deactivates (soft-deletes) a DSR Admin user. */
    public DsrAdminUserDto deactivate(Long userId) {
        AuthorizedUser user = authorizedUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        boolean isDsrAdmin = user.getUserGroups().stream()
                .anyMatch(g -> GROUP_DSR_ADMINS.equals(g.getGroupName()));
        if (!isDsrAdmin) {
            throw new IllegalArgumentException("User " + userId + " is not a DSR Admin");
        }
        user.setActive(false);
        return toDto(authorizedUserRepository.save(user));
    }

    // ----- bootstrap ------------------------------------------------------------

    /** Find-or-create the DSR_ADMIN authority, role, and group (in dependency order). */
    private UserGroup ensureDsrAdminGroup() {
        return userGroupRepository.findByGroupName(GROUP_DSR_ADMINS)
                .orElseGet(() -> {
                    RoleList role = ensureDsrAdminRole();
                    UserGroup group = new UserGroup();
                    group.setGroupName(GROUP_DSR_ADMINS);
                    group.setDescription("DSR Admin (Privacy Ops) console access");
                    group.setIsActive(true);
                    group.getRoles().add(role);
                    log.info("Bootstrapping user group {}", GROUP_DSR_ADMINS);
                    return userGroupRepository.save(group);
                });
    }

    private RoleList ensureDsrAdminRole() {
        return roleListRepository.findByLabel(ROLE_DSR_ADMIN)
                .orElseGet(() -> {
                    AuthorityList authority = ensureDsrAdminAuthority();
                    RoleList role = new RoleList(null, ROLE_DSR_ADMIN, 0, true);
                    role.setAuthorities(new java.util.ArrayList<>(List.of(authority)));
                    log.info("Bootstrapping role {}", ROLE_DSR_ADMIN);
                    return roleListRepository.save(role);
                });
    }

    private AuthorityList ensureDsrAdminAuthority() {
        return authorityListRepository.findByAuthorityName(AUTHORITY_DSR_ADMIN)
                .orElseGet(() -> {
                    log.info("Bootstrapping authority {}", AUTHORITY_DSR_ADMIN);
                    return authorityListRepository.save(
                            new AuthorityList(AUTHORITY_DSR_ADMIN, "DSR Admin console access"));
                });
    }

    // ----- Azure B2C account (aligned with PublicRegistrationService.createAzureUser) -----

    /**
     * Creates the Azure B2C user via user-mgmt-service, persists the returned object id onto
     * the {@code AuthorizedUser}, and returns the GUID (or {@code null} if creation failed).
     */
    private String createAzureUser(AuthorizedUser user, String password) {
        UserMgmtApiClient userMgmtClient = userMgmtClientProvider.getIfAvailable();
        if (userMgmtClient == null) {
            log.warn("User management service not available. DSR admin will not be created in Azure AD: {}",
                    user.getEmailId());
            return null;
        }
        try {
            TenantB2CConfig b2cConfig = tenantB2CConfigService.getConfigForCurrentTenantOrDefault();
            if (b2cConfig == null) {
                log.warn("Azure B2C configuration not found. DSR admin will not be created in Azure AD.");
                return null;
            }
            String tenantIdentifier;
            if (b2cConfig.getB2cTenantId() != null && !b2cConfig.getB2cTenantId().isEmpty()) {
                tenantIdentifier = b2cConfig.getB2cTenantId();
            } else {
                tenantIdentifier = b2cConfig.getB2cTenantName() + ".onmicrosoft.com";
            }

            SignUpDto signUpDto = SignUpDto.builder()
                    .email(user.getEmailId())
                    .password(password)
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .mobilePhone(user.getMobilePhone())
                    .clientId(b2cConfig.getClientId())
                    .clientSecret(b2cConfig.getClientSecret())
                    .tenantId(tenantIdentifier)
                    .build();

            MicrosoftGraphResponseDto response = userMgmtClient.createUserLatest(signUpDto);
            if (response != null
                    && response.getId() != null && !response.getId().isBlank()
                    && (response.getErrorMsg() == null || response.getErrorMsg().isEmpty())) {
                String azureUserId = response.getId();
                log.info("DSR admin created in Azure AD for {}, Azure ID: {}", user.getEmailId(), azureUserId);
                user.setAzureAdUserId(azureUserId);
                authorizedUserRepository.save(user);
                return azureUserId;
            }
            String reason = response == null
                    ? "null response"
                    : (response.getErrorMsg() != null && !response.getErrorMsg().isEmpty()
                            ? response.getErrorMsg() : "no Azure id in response");
            log.error("Azure AD user creation failed for DSR admin {}: {}", user.getEmailId(), reason);
            return null;
        } catch (Exception e) {
            log.error("Azure AD user creation failed with exception for DSR admin {}: {}",
                    user.getEmailId(), e.getMessage(), e);
            return null;
        }
    }

    private void sendLoginDetailsEmail(String email, String name, String loginId, String setPasswordUrl) {
        GraphEmailService graphEmailService = graphEmailServiceProvider.getIfAvailable();
        if (graphEmailService == null) {
            log.warn("Graph Email service not available. Setpassword URL would have been: {}", setPasswordUrl);
            return;
        }
        try {
            boolean sent = graphEmailService.sendLoginDetailsEmail(email, name, loginId, setPasswordUrl, clientUrl);
            log.info("Setpassword email {} for DSR admin {}", sent ? "sent" : "FAILED", email);
        } catch (Exception e) {
            log.error("Failed sending setpassword email to DSR admin {}: {}", email, e.getMessage(), e);
        }
    }

    private String buildSetPasswordUrl(String azureUserId) {
        String base = clientUrl != null && !clientUrl.isEmpty() ? clientUrl : "http://localhost:3000";
        String trimmed = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        return trimmed + "/investor/setpassword/" + azureUserId;
    }

    // ----- helpers --------------------------------------------------------------

    private void validate(DsrAdminRegisterRequestDto dto) {
        if (isBlank(dto.getFirstName()) || isBlank(dto.getLastName())) {
            throw new IllegalArgumentException("First name and last name are required");
        }
        if (isBlank(dto.getEmailId())) {
            throw new IllegalArgumentException("Email is required");
        }
        if (isBlank(dto.getLoginId())) {
            throw new IllegalArgumentException("Login ID is required");
        }
        if (isBlank(dto.getPassword()) || dto.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password is required (minimum 8 characters)");
        }
    }

    private boolean isBlank(String v) {
        return v == null || v.isBlank();
    }

    private DsrAdminUserDto toDto(AuthorizedUser user) {
        List<String> roles = user.getUserGroups().stream()
                .flatMap(g -> g.getRoles().stream())
                .map(RoleList::getLabel)
                .distinct()
                .toList();
        return DsrAdminUserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .emailId(user.getEmailId())
                .mobilePhone(user.getMobilePhone())
                .loginId(user.getLoginId())
                .active(user.isActive())
                .roles(roles)
                .lastLogin(user.getLastLogin() != null
                        ? user.getLastLogin().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .build();
    }
}
