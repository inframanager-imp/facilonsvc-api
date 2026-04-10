package com.facilon.app.module.client.service;

import com.facilon.app.integration.graphemail.GraphEmailService;
import com.facilon.app.integration.sms.SmsServiceApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OTP issuance and verification for the PMS investor registration flow.
 *
 * Mirrors Laravel {@code InvestorController.introduce_investor_pms_register_step1_submit}
 * and {@code introduce_investor_pms_register_step2_verify_otp}:
 * <ul>
 *   <li>Generates a 4-digit email OTP + 4-digit SMS OTP</li>
 *   <li>Sends the email OTP via {@link GraphEmailService#sendOtpEmail}</li>
 *   <li>Sends the SMS OTP via {@link SmsServiceApiClient#sendOtpSms}</li>
 *   <li>Max 3 OTP <em>sends</em> per key inside a 15-minute window</li>
 *   <li>Max 5 failed verification attempts per key then a 15-minute lock
 *       (Laravel uses {@code otp_attempts_{unique_code}} cache bucket)</li>
 *   <li>OTPs expire after {@link #OTP_TTL_MINUTES} minutes</li>
 * </ul>
 *
 * State is kept in-process ({@link ConcurrentHashMap}) — suitable for a single
 * instance. For multi-instance deployments, swap the backing map for Redis /
 * Caffeine distributed cache.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PmsOtpService {

    /** OTP validity window, in minutes. */
    public static final int OTP_TTL_MINUTES = 10;

    /** Maximum times an OTP can be (re)issued for the same key inside the window. */
    public static final int MAX_SEND_ATTEMPTS = 3;

    /** Maximum failed verification attempts before the key is locked. */
    public static final int MAX_VERIFY_ATTEMPTS = 5;

    /** Lock duration after too many failed verify attempts. */
    public static final int LOCK_MINUTES = 15;

    private static final SecureRandom RANDOM = new SecureRandom();

    private final Map<String, OtpEntry> store = new ConcurrentHashMap<>();

    @Autowired(required = false)
    private GraphEmailService graphEmailService;

    @Autowired(required = false)
    private SmsServiceApiClient smsServiceApiClient;

    /**
     * Request an OTP for the given email + mobile combination.
     *
     * @return a result describing what was sent (or why sending was rejected).
     */
    public OtpSendResult requestOtp(String email, String mobileNumber, String firstName) {
        String key = buildKey(email, mobileNumber);
        Instant now = Instant.now();

        OtpEntry entry = store.compute(key, (k, existing) -> {
            if (existing == null || existing.expiredOrLockReleased(now)) {
                return new OtpEntry();
            }
            return existing;
        });

        if (entry.isLocked(now)) {
            long remaining = Math.max(0, ChronoUnit.MINUTES.between(now, entry.lockUntil));
            log.warn("OTP request rejected — key is locked for {} more minutes", remaining);
            return OtpSendResult.locked(remaining);
        }

        if (entry.sendAttempts >= MAX_SEND_ATTEMPTS) {
            log.warn("OTP request rejected — max send attempts reached ({})", MAX_SEND_ATTEMPTS);
            return OtpSendResult.rateLimited(MAX_SEND_ATTEMPTS);
        }

        String emailOtp = generateOtp();
        String smsOtp = generateOtp();

        entry.emailOtp = emailOtp;
        entry.smsOtp = smsOtp;
        entry.issuedAt = now;
        entry.expiresAt = now.plus(OTP_TTL_MINUTES, ChronoUnit.MINUTES);
        entry.sendAttempts += 1;
        entry.verifyAttempts = 0;
        entry.lockUntil = null;

        boolean emailSent = tryDeliverEmail(email, firstName, emailOtp);
        boolean smsSent = tryDeliverSms(mobileNumber, smsOtp);

        log.info("PMS OTP issued for email={}, mobile={} (emailSent={}, smsSent={}, sendAttempt={}/{})",
                email, mobileNumber, emailSent, smsSent, entry.sendAttempts, MAX_SEND_ATTEMPTS);

        return OtpSendResult.success(emailSent, smsSent, OTP_TTL_MINUTES);
    }

    /**
     * Verify the email + SMS OTP for the given key.
     *
     * @return a result describing the outcome.
     */
    public OtpVerifyResult verifyOtp(String email, String mobileNumber, String emailOtp, String smsOtp) {
        String key = buildKey(email, mobileNumber);
        Instant now = Instant.now();
        OtpEntry entry = store.get(key);

        if (entry == null || entry.emailOtp == null || entry.smsOtp == null) {
            return OtpVerifyResult.notIssued();
        }

        if (entry.isLocked(now)) {
            long remaining = Math.max(0, ChronoUnit.MINUTES.between(now, entry.lockUntil));
            return OtpVerifyResult.locked(remaining);
        }

        if (now.isAfter(entry.expiresAt)) {
            store.remove(key);
            return OtpVerifyResult.expired();
        }

        boolean emailMatch = equalsConstantTime(emailOtp, entry.emailOtp);
        boolean smsMatch = equalsConstantTime(smsOtp, entry.smsOtp);

        if (emailMatch && smsMatch) {
            store.remove(key); // Single-use — invalidate after success.
            log.info("PMS OTP verified successfully for key={}", key);
            return OtpVerifyResult.success();
        }

        entry.verifyAttempts += 1;
        if (entry.verifyAttempts >= MAX_VERIFY_ATTEMPTS) {
            entry.lockUntil = now.plus(LOCK_MINUTES, ChronoUnit.MINUTES);
            log.warn("PMS OTP verification locked for {} minutes after {} failed attempts",
                    LOCK_MINUTES, entry.verifyAttempts);
            return OtpVerifyResult.locked(LOCK_MINUTES);
        }

        log.warn("PMS OTP mismatch ({} / {} verify attempts)", entry.verifyAttempts, MAX_VERIFY_ATTEMPTS);
        return OtpVerifyResult.mismatch(MAX_VERIFY_ATTEMPTS - entry.verifyAttempts);
    }

    // ------------------------------------------------------------------
    // helpers
    // ------------------------------------------------------------------

    private static String buildKey(String email, String mobileNumber) {
        return (email == null ? "" : email.trim().toLowerCase()) + "|" +
                (mobileNumber == null ? "" : mobileNumber.trim());
    }

    private static String generateOtp() {
        return String.format("%04d", RANDOM.nextInt(10_000));
    }

    private static boolean equalsConstantTime(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) return false;
        int diff = 0;
        for (int i = 0; i < a.length(); i++) {
            diff |= a.charAt(i) ^ b.charAt(i);
        }
        return diff == 0;
    }

    private boolean tryDeliverEmail(String email, String firstName, String otp) {
        if (graphEmailService == null) {
            log.warn("GraphEmailService not configured — email OTP NOT sent (value kept for dev/testing)");
            return false;
        }
        try {
            return graphEmailService.sendOtpEmail(email, firstName != null ? firstName : "User", otp);
        } catch (Exception e) {
            log.error("Failed to send PMS OTP email: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean tryDeliverSms(String mobileNumber, String otp) {
        if (smsServiceApiClient == null) {
            log.warn("SmsServiceApiClient not configured — SMS OTP NOT sent (value kept for dev/testing)");
            return false;
        }
        try {
            smsServiceApiClient.sendOtpSms(mobileNumber, otp);
            return true;
        } catch (Exception e) {
            log.error("Failed to send PMS OTP SMS: {}", e.getMessage(), e);
            return false;
        }
    }

    // ------------------------------------------------------------------
    // Inner state + result types
    // ------------------------------------------------------------------

    private static final class OtpEntry {
        String emailOtp;
        String smsOtp;
        Instant issuedAt;
        Instant expiresAt;
        int sendAttempts;
        int verifyAttempts;
        Instant lockUntil;

        boolean isLocked(Instant now) {
            return lockUntil != null && now.isBefore(lockUntil);
        }

        boolean expiredOrLockReleased(Instant now) {
            boolean expired = expiresAt != null && now.isAfter(expiresAt.plus(LOCK_MINUTES, ChronoUnit.MINUTES));
            boolean lockReleased = lockUntil != null && !now.isBefore(lockUntil);
            return expired || lockReleased;
        }
    }

    public static final class OtpSendResult {
        public final boolean success;
        public final boolean emailSent;
        public final boolean smsSent;
        public final int expiresInMinutes;
        public final String message;

        private OtpSendResult(boolean success, boolean emailSent, boolean smsSent, int expiresInMinutes, String message) {
            this.success = success;
            this.emailSent = emailSent;
            this.smsSent = smsSent;
            this.expiresInMinutes = expiresInMinutes;
            this.message = message;
        }

        static OtpSendResult success(boolean emailSent, boolean smsSent, int ttl) {
            return new OtpSendResult(true, emailSent, smsSent, ttl, "OTP sent");
        }

        static OtpSendResult rateLimited(int max) {
            return new OtpSendResult(false, false, false, 0,
                    "Maximum OTP send limit reached (" + max + "). Please wait before trying again.");
        }

        static OtpSendResult locked(long remainingMinutes) {
            return new OtpSendResult(false, false, false, 0,
                    "Too many failed attempts. Try again in " + remainingMinutes + " minutes.");
        }
    }

    public static final class OtpVerifyResult {
        public final boolean success;
        public final String code;     // "ok" | "mismatch" | "expired" | "locked" | "not_issued"
        public final String message;
        public final Integer remainingAttempts;

        private OtpVerifyResult(boolean success, String code, String message, Integer remainingAttempts) {
            this.success = success;
            this.code = code;
            this.message = message;
            this.remainingAttempts = remainingAttempts;
        }

        static OtpVerifyResult success() {
            return new OtpVerifyResult(true, "ok", "OTP verified", null);
        }

        static OtpVerifyResult mismatch(int remaining) {
            return new OtpVerifyResult(false, "mismatch",
                    "OTP not matched. " + remaining + " attempt(s) remaining.", remaining);
        }

        static OtpVerifyResult expired() {
            return new OtpVerifyResult(false, "expired", "OTP has expired. Please request a new one.", null);
        }

        static OtpVerifyResult locked(long remainingMinutes) {
            return new OtpVerifyResult(false, "locked",
                    "Too many failed attempts. Locked for " + remainingMinutes + " minutes.", 0);
        }

        static OtpVerifyResult notIssued() {
            return new OtpVerifyResult(false, "not_issued", "No OTP has been issued. Please request one first.", null);
        }
    }
}
