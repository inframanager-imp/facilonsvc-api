package com.facilon.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tenant_b2c_config")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantB2CConfig extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @Size(max = 256)
    @Column(name = "b2c_tenant_name", nullable = false)
    private String b2cTenantName;

    @Size(max = 256)
    @Column(name = "b2c_tenant_id")
    private String b2cTenantId;

    @Size(max = 256)
    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "client_secret", nullable = false, columnDefinition = "TEXT")
    private String clientSecret;

    @Size(max = 256)
    @Column(name = "signup_signin_policy", nullable = false)
    private String signupSigninPolicy;

    @Size(max = 256)
    @Column(name = "logout_policy", nullable = false)
    private String logoutPolicy;

    @Size(max = 256)
    @Column(name = "reset_password_policy")
    private String resetPasswordPolicy;

    @Size(max = 512)
    @Column(name = "redirect_uri", nullable = false)
    private String redirectUri;

    @Size(max = 512)
    @Column(name = "post_logout_redirect_uri", nullable = false)
    private String postLogoutRedirectUri;

    @Size(max = 512)
    @Column(name = "scope")
    @Builder.Default
    private String scope = "openid profile email offline_access";
}
