package com.facilon.app.security;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.model.Tenant;
import com.facilon.app.repository.TenantRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationAndTenantFilter extends OncePerRequestFilter {

    @Value("${app.security.jwtSecret}")
    private String jwtSecret;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private TenantRepository tenantRepository;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationAndTenantFilter.class);

    private static final String[] SKIP_JWT_PATHS = {
            "/api/checkToken",
            "/api/auth/azure-b2c/**",
            "/api/azure-ad/create-user-latest",
            "/api/azure-ad/log-password-failure-count",
            "/api/clients/onboarding/**",
            "/api/clients/content/**",
            "/api/clients/introduced-investor/nextholder/docs/**",
            "/api/clients/pms-investor/nextholder/docs/**"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        AntPathMatcher matcher = new AntPathMatcher();
        for (String pattern : SKIP_JWT_PATHS) {
            if (matcher.match(pattern, path)) return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt, request)) {
                byte[] signingKey = jwtSecret.getBytes(StandardCharsets.UTF_8);

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(signingKey)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                // --- Extract roles and authorities from JWT ---
                List<String> roles = (List<String>) claims.get("roles");
                List<String> authoritiesFromJwt = (List<String>) claims.get("authorities");
                
                Collection<SimpleGrantedAuthority> authorities = new java.util.ArrayList<>();
                if (roles != null) {
                    roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .forEach(authorities::add);
                }
                if (authoritiesFromJwt != null) {
                    authoritiesFromJwt.stream()
                        .map(SimpleGrantedAuthority::new)
                        .forEach(authorities::add);
                }

                UserDetails userDetails = customUserDetailsService
                        .loadUserById(Long.parseLong(claims.getSubject()));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                // --- Extract tenant and set context ---
                Long tenantId = claims.get("tenantId", Long.class);
                if (tenantId != null) {
                    Tenant tenant = tenantRepository.findById(tenantId)
                            .orElseThrow(() -> new RuntimeException("Tenant not found: " + tenantId));
                    logger.debug("Set TenantContextHolder: {}", tenant);
                    TenantContextHolder.setTenant(tenant, true);
                } else {
                    logger.error("Tenant ID missing from JWT claims");
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tenant missing in JWT");
                    return;
                }
            }
        } catch (Exception ex) {
            logger.error("JWT authentication/tenant resolution failed", ex);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token");
            return;
        }

        try {
            filterChain.doFilter(request, response);
        } finally {

            TenantContextHolder.reset();
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
