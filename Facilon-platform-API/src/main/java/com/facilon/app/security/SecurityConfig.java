package com.facilon.app.security;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.facilon.app.config.OnboardingTenantFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@ConfigurationProperties(prefix = "ignored")
public class SecurityConfig  {
    @Setter
    private List<String> httpUrl = new ArrayList<>();
    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }


    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private OnboardingTenantFilter onboardingTenantFilter;
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(HttpMethod.OPTIONS, "/**").and()

                .httpFirewall(allowUrlEncodedSlashHttpFirewall());
    }
    @Bean
    public JwtAuthenticationAndTenantFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationAndTenantFilter();
    }

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("========================================");
        log.info("SECURITY CONFIG - Initializing");
        log.info("Total ignored URLs from config: {}", httpUrl.size());
        for (String url : httpUrl) {
            log.info("  ✅ Public URL: {}", url);
        }
        log.info("========================================");

        // Enable CORS (using WebConfig settings)
        http.cors(cors -> cors.configure(http))
                .csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> {
            // Public URLs - B2C config for frontend MSAL (no auth required)
            auth.requestMatchers("/api/auth/b2c-config").permitAll();
            
            // Other public URLs from config
            for (String url : httpUrl) {
                auth.requestMatchers(url).permitAll();
            }

            // Protected endpoints - require authentication
            auth.requestMatchers("/api/**").authenticated();
        });

        // Exception handling
        http.exceptionHandling(exception ->
                exception.authenticationEntryPoint(unauthorizedHandler)
        );

        // Session management
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // Filters - add JWT first (so it has registered order), then onboarding before it
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(onboardingTenantFilter, JwtAuthenticationAndTenantFilter.class);

        return http.build();
    }
}
