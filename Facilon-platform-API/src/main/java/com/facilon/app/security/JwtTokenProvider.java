package com.facilon.app.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.security.jwtSecret}")
    private String jwtSecret;

    @Value("${app.security.jwtTokenExpirationInMills}")
    private int jwtExpirationInMs;

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        byte[] signingKey = jwtSecret.getBytes(StandardCharsets.UTF_8);
        final List<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String token = Jwts.builder().setSubject(userPrincipal.getId().toString()).setIssuedAt(new Date())
                .claim("tenantId", userPrincipal.getTenantId()).claim("roles", userPrincipal.rolesNames)
                .setExpiration(expiryDate)
                .claim("authorities", authorities)
                .claim("groupName", userPrincipal.groupNames)
                .claim("given_name", userPrincipal.getFirstName())
                .claim("family_name", userPrincipal.getLastName())
                .claim("name", userPrincipal.getFirstName() + " " + userPrincipal.getLastName())
                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS256)
                .compact();

        return token;
    }

    public Long getUserIdFromJWT(String token) {
        byte[] signingKey = jwtSecret.getBytes(StandardCharsets.UTF_8);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build().parseClaimsJws(token).getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken, HttpServletRequest httpServletRequest) {
        try {
            byte[] signingKey = jwtSecret.getBytes(StandardCharsets.UTF_8);
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(authToken).getBody();
            return true;
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
            httpServletRequest.setAttribute("expired", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        } catch (InvalidTokenRequestException ex) {
            logger.error(ex.getMessage());
        }
        return false;
    }

}
