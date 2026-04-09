package com.facilon.app.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.facilon.app.model.AuthorizedUser;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Data
public class UserPrincipal implements UserDetails {
    private static final long serialVersionUID = 671025128765898024L;
    private Long id;
    private String loginId;
    private String firstName;
    private String lastName;
    private String userName;
    private Long tenantId;

    private LocalDateTime lastLogin;

    @JsonIgnore
    private String email;

    @JsonIgnore
    private String password;

    @JsonIgnore
    boolean isAccountNonExpired = true;

    @JsonIgnore
    boolean isAccountNonLocked = true;

    @JsonIgnore
    boolean isCredentialsNonExpired = true;

    @JsonIgnore
    boolean isEnabled = true;

    private Collection<? extends GrantedAuthority> authorities;
    List<String> authorityNames;

    List<String> groupNames;
    List<String> rolesNames;

    public UserPrincipal(AuthorizedUser user, Collection<? extends GrantedAuthority> authorities, Integer expiryDays,
                         Integer maxFailureCount, List<String> authorityNames,List<String> groupNames,List<String> rolesNames) {
        log.info("**** MaxFailureCount ****" + maxFailureCount);
        this.id = user.getId();

        this.loginId = user.getLoginId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmailId();
        this.password = user.getPassword();
        this.lastLogin = user.getLastLogin();
        this.authorities = authorities;
        this.userName=user.getEmailId();
        this.authorityNames=authorityNames;
        this.groupNames=groupNames;
        this.rolesNames=rolesNames;
        this.tenantId=user.getTenant().getTenantId();
        //isEnabled = user.getLock();
        isAccountNonExpired = true;
        isAccountNonLocked = true;
    }


    public static UserPrincipal create(AuthorizedUser user, Integer expiryDays, Integer maxFailureCount,
                                       List<String> authorityNames,
                                       List<String> groupNames,List<String> rolesNames ) {
        log.debug("*********** In UserPrincipal create *********");
        List<GrantedAuthority> authorities = authorityNames.stream()
                .map(authority -> new SimpleGrantedAuthority(authority)).collect(Collectors.toList());
        log.debug("*********** Authorities loaded: {} *********", authorities);
        return new UserPrincipal(user, authorities, expiryDays, maxFailureCount,authorityNames,groupNames,rolesNames);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    public Long getId() {
        return this.id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getEmail() {
        return this.email;
    }

    public String getLoginId() {
        return this.loginId;
    }
}
