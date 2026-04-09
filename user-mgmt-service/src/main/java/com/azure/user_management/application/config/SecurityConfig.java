package com.azure.user_management.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers(
                                                                "/",
                                                                "/swagger-ui.html",
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs",
                                                                "/swagger-resources/**",
                                                                "/webjars/**",
                                                                "/api/v1/azure-ad/create-user-latest",
                                                                "/api/v1/azure-ad/change-password",
                                                                "/api/v1/azure-ad/log-password-failure-count")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.POST, "/mylogger/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/mylogger/**").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/public/**").permitAll()
                                                .requestMatchers(HttpMethod.POST,
                                                                "/api/v1/azure-ad/log-password-failure-count")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .headers(headers -> headers
                                                // Block all iframe embedding
                                                .frameOptions(frameOptions -> frameOptions.deny())
                                                // Add CSP for modern browsers
                                                .contentSecurityPolicy(
                                                                csp -> csp.policyDirectives("frame-ancestors 'none';")))
                                .addFilterBefore(new CustomJwtFilter(), UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
