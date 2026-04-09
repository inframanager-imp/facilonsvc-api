package com.facilon.app.model;

import com.facilon.app.module.client.model.Investor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "AuthorizedUser")
@Table(name = "authorized_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizedUser extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authorized_user_id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Column(name = "email_id")
    private String emailId;

    @Column(name = "login_id", unique = true)
    private String loginId;

    @Column(name = "password")
    private String password;

    @Column(name = "failed_logins")
    private int failedLogins;

    @Column(name = "last_password_change")
    private LocalDateTime lastPasswordChange;

    @Column(name = "last_failed")
    private LocalDateTime lastFailed;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "must_change_password")
    private boolean mustChangePassword;

    @Column(name = "password_change_status")
    private Boolean passwordChangeStatus;

    // Azure AD / B2C integration - optional link to Microsoft identity
    @Column(name = "azure_ad_user_id")
    private String azureAdUserId;

    // Google / social IdP (e.g. when user signs in with Google via B2C)
    @Column(name = "google_user_id")
    private String googleUserId;

    // User Groups (many-to-many)
    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "user_group_mapping", joinColumns = @JoinColumn(name = "authorized_user_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    @Builder.Default
    private Set<UserGroup> userGroups = new HashSet<>();

    // Investor relationship (one-to-one, optional). Excluded to avoid cycle in
    // equals/hashCode/toString and JSON.
    @OneToOne(mappedBy = "authorizedUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    private Investor investor;

}
