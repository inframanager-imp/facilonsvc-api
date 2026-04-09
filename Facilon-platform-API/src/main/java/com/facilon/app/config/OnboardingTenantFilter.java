package com.facilon.app.config;

import com.facilon.app.model.Tenant;
import com.facilon.app.repository.TenantRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Sets default tenant context for public onboarding endpoints when no JWT is present.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@RequiredArgsConstructor
@Slf4j
public class OnboardingTenantFilter extends OncePerRequestFilter {

    private final TenantRepository tenantRepository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Value("${investor.onboarding.default-tenant-id:1}")
    private Long defaultTenantId;

    private static final String[] ONBOARDING_PATHS = {
            "/api/clients/onboarding/**",
            "/api/clients/content/**",
            "/api/clients/introduced-investor/nextholder/docs/**",
            "/api/clients/pms-investor/nextholder/docs/**",
            "/api/investor/introduced/**"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        for (String pattern : ONBOARDING_PATHS) {
            if (pathMatcher.match(pattern, path)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            var ctx = TenantContextHolder.getContext();
            if (ctx == null || ctx.getTenant() == null) {
                Optional<Tenant> tenantOpt = tenantRepository.findById(defaultTenantId);
                if (tenantOpt.isPresent()) {
                    TenantContextHolder.setTenant(tenantOpt.get(), true);
                    log.debug("Set default tenant {} for public onboarding path", defaultTenantId);
                } else {
                    log.warn("Default tenant {} not found for onboarding", defaultTenantId);
                }
            }
        } catch (Exception e) {
            log.warn("Could not set onboarding tenant: {}", e.getMessage());
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContextHolder.reset();
        }
    }
}
