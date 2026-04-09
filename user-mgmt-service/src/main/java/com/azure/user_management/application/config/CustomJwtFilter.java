package com.azure.user_management.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

public class CustomJwtFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final List<String> EXCLUDE_URLS = List.of(
            "/public",
            "/mylogger",
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-resources",
            "/api/user_message/get_message",
            "/api/v1/azure-ad/reset-password",
            "/api/v1/azure-ad/create-user-latest",
            "/api/v1/azure-ad/change-password",
            "/api/v1/azure-ad/log-password-failure-count");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        // Skip JWT validation for public endpoints
        if (EXCLUDE_URLS.stream().anyMatch(path::contains)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                Map<String, Object> claims = decodeJwtPayload(token);
                List<String> emails = (ArrayList<String>) claims.get("emails");
                // Extract email claim (adjust key if needed, like "preferred_username")
                String email = emails != null && !emails.isEmpty() ? emails.get(0) : null;
                // if (email == null) {
                // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Email not found in
                // token");
                // return;
                // }

                // Check token expiry
                Object expClaim = claims.get("exp");
                if (expClaim instanceof Integer) {
                    Instant expiry = Instant.ofEpochSecond(((Integer) expClaim).longValue());
                    if (expiry.isBefore(Instant.now())) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
                        return;
                    }
                }

                // Optional: check user email exists in DB
                // if (!userService.existsByEmail(email)) {
                // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                // return;
                // }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email,
                        null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT: " + e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private Map<String, Object> decodeJwtPayload(String token) throws IOException {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT format");
        }

        String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        return objectMapper.readValue(payload, Map.class);
    }
}
