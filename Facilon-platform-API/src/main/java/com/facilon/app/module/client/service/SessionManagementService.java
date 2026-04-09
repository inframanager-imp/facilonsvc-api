package com.facilon.app.module.client.service;

import com.facilon.app.module.client.dto.SessionDto;
import com.facilon.app.module.client.dto.SessionStatusDto;
import com.facilon.app.module.client.model.InvestorSession;
import com.facilon.app.module.client.repository.InvestorSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing investor sessions
 * Works with both local JWT and Azure B2C authentication
 */
@Slf4j
@Service
public class SessionManagementService {

    @Value("${session.timeout.minutes:30}")
    private int sessionTimeoutMinutes;

    @Autowired
    private InvestorSessionRepository sessionRepository;

    /**
     * Create a new session on login
     */
    @Transactional
    public InvestorSession createSession(Long investorId, String jwtToken, String loginMethod,
            String ipAddress, String userAgent, Long tenantId) {
        InvestorSession session = new InvestorSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setInvestorId(investorId);
        session.setJwtTokenHash(hashToken(jwtToken));
        session.setLoginMethod(loginMethod);
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setLoginTime(LocalDateTime.now());
        session.setLastActivityTime(LocalDateTime.now());
        session.setIsActive(true);
        // session.setTenantId(tenantId); // Tenant is handled by TenantEntity /
        // TenantContextHolder

        log.info("Creating session for investor {} via {}", investorId, loginMethod);
        return sessionRepository.save(session);
    }

    /**
     * Update last activity time for a session
     */
    @Transactional
    public void updateActivity(String sessionId) {
        Optional<InvestorSession> sessionOpt = sessionRepository.findBySessionId(sessionId);
        if (sessionOpt.isPresent()) {
            InvestorSession session = sessionOpt.get();
            session.setLastActivityTime(LocalDateTime.now());
            sessionRepository.save(session);
        }
    }

    /**
     * Update activity by JWT token hash
     */
    @Transactional
    public void updateActivityByToken(String jwtToken) {
        String tokenHash = hashToken(jwtToken);
        Optional<InvestorSession> sessionOpt = sessionRepository.findByJwtTokenHash(tokenHash);
        if (sessionOpt.isPresent()) {
            InvestorSession session = sessionOpt.get();
            if (session.getIsActive()) {
                session.setLastActivityTime(LocalDateTime.now());
                sessionRepository.save(session);
            }
        }
    }

    /**
     * Logout - mark session as inactive
     */
    @Transactional
    public void logout(String sessionId, String reason) {
        Optional<InvestorSession> sessionOpt = sessionRepository.findBySessionId(sessionId);
        if (sessionOpt.isPresent()) {
            InvestorSession session = sessionOpt.get();
            session.setIsActive(false);
            session.setLogoutTime(LocalDateTime.now());
            session.setLogoutReason(reason);
            sessionRepository.save(session);
            log.info("Session {} logged out: {}", sessionId, reason);
        }
    }

    /**
     * Logout by JWT token
     */
    @Transactional
    public void logoutByToken(String jwtToken, String reason) {
        String tokenHash = hashToken(jwtToken);
        Optional<InvestorSession> sessionOpt = sessionRepository.findByJwtTokenHash(tokenHash);
        if (sessionOpt.isPresent()) {
            InvestorSession session = sessionOpt.get();
            session.setIsActive(false);
            session.setLogoutTime(LocalDateTime.now());
            session.setLogoutReason(reason);
            sessionRepository.save(session);
            log.info("Session for investor {} logged out: {}", session.getInvestorId(), reason);
        }
    }

    /**
     * Logout all sessions for an investor
     */
    @Transactional
    public int logoutAllSessions(Long investorId, String currentSessionId) {
        List<InvestorSession> sessions = sessionRepository.findByInvestorIdAndIsActiveTrue(investorId);
        int count = 0;
        for (InvestorSession session : sessions) {
            if (!session.getSessionId().equals(currentSessionId)) {
                session.setIsActive(false);
                session.setLogoutTime(LocalDateTime.now());
                session.setLogoutReason("forced");
                sessionRepository.save(session);
                count++;
            }
        }
        log.info("Logged out {} sessions for investor {}", count, investorId);
        return count;
    }

    /**
     * Get session status
     */
    public SessionStatusDto getSessionStatus(String sessionId) {
        Optional<InvestorSession> sessionOpt = sessionRepository.findBySessionId(sessionId);
        if (sessionOpt.isPresent()) {
            InvestorSession session = sessionOpt.get();
            long expiresIn = calculateExpiresIn(session.getLastActivityTime());

            return new SessionStatusDto(
                    session.getSessionId(),
                    session.getLoginTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    session.getLastActivityTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    session.getLoginMethod(),
                    expiresIn,
                    session.getIsActive());
        }
        return null;
    }

    /**
     * Get all active sessions for an investor
     */
    public List<SessionDto> getActiveSessions(Long investorId, String currentSessionId) {
        List<InvestorSession> sessions = sessionRepository.findByInvestorIdAndIsActiveTrue(investorId);
        return sessions.stream()
                .map(session -> {
                    long expiresIn = calculateExpiresIn(session.getLastActivityTime());
                    return new SessionDto(
                            session.getSessionId(),
                            session.getLoginTime(),
                            session.getLastActivityTime(),
                            session.getLoginMethod(),
                            session.getIpAddress(),
                            session.getUserAgent(),
                            session.getSessionId().equals(currentSessionId),
                            expiresIn);
                })
                .collect(Collectors.toList());
    }

    /**
     * Check if session is valid (not timed out)
     */
    public boolean isSessionValid(String sessionId) {
        Optional<InvestorSession> sessionOpt = sessionRepository.findBySessionId(sessionId);
        if (sessionOpt.isPresent()) {
            InvestorSession session = sessionOpt.get();
            if (!session.getIsActive()) {
                return false;
            }
            long minutesSinceActivity = Duration.between(session.getLastActivityTime(), LocalDateTime.now())
                    .toMinutes();
            return minutesSinceActivity < sessionTimeoutMinutes;
        }
        return false;
    }

    /**
     * Scheduled task to cleanup expired sessions
     * Runs every 10 minutes
     */
    @Scheduled(fixedRate = 600000) // 10 minutes
    @Transactional
    public void cleanupExpiredSessions() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(sessionTimeoutMinutes);
        List<InvestorSession> expiredSessions = sessionRepository
                .findByIsActiveTrueAndLastActivityTimeBefore(cutoffTime);

        for (InvestorSession session : expiredSessions) {
            session.setIsActive(false);
            session.setLogoutTime(LocalDateTime.now());
            session.setLogoutReason("timeout");
            sessionRepository.save(session);
        }

        if (!expiredSessions.isEmpty()) {
            log.info("Cleaned up {} expired sessions", expiredSessions.size());
        }
    }

    /**
     * Get session by JWT token
     */
    public Optional<InvestorSession> getSessionByToken(String jwtToken) {
        String tokenHash = hashToken(jwtToken);
        return sessionRepository.findByJwtTokenHash(tokenHash);
    }

    // Helper methods

    private long calculateExpiresIn(LocalDateTime lastActivity) {
        long minutesSinceActivity = Duration.between(lastActivity, LocalDateTime.now()).toMinutes();
        long remainingMinutes = sessionTimeoutMinutes - minutesSinceActivity;
        return Math.max(0, remainingMinutes * 60); // Convert to seconds
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.error("Error hashing token", e);
            return token; // Fallback (not recommended for production)
        }
    }
}
