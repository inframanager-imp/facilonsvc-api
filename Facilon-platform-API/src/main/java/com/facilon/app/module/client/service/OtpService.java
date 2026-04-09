package com.facilon.app.module.client.service;

import com.facilon.app.module.client.model.OtpVerification;
import com.facilon.app.module.client.repository.OtpVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final OtpVerificationRepository otpRepository;
    private static final Random RANDOM = new Random();

    public String generateOtp() {
        return String.valueOf(1000 + RANDOM.nextInt(9000));
    }

    @Transactional
    public void storeOtp(String uniqueCode, String emailOtp, String smsOtp, int expiryMinutes) {
        OtpVerification o = otpRepository.findByUniqueCode(uniqueCode)
                .orElse(new OtpVerification());
        o.setUniqueCode(uniqueCode);
        o.setEmailOtp(emailOtp);
        o.setSmsOtp(smsOtp);
        o.setExpiresAt(LocalDateTime.now().plusMinutes(expiryMinutes));
        o.setVerified(false);
        o.setAttempts(0);
        otpRepository.save(o);
    }

    public boolean verifyOtp(String uniqueCode, String otp, String type) {
        Optional<OtpVerification> opt = otpRepository.findByUniqueCode(uniqueCode);
        if (opt.isEmpty()) return false;
        OtpVerification o = opt.get();
        if (o.getExpiresAt().isBefore(LocalDateTime.now())) return false;
        String expected = "email".equalsIgnoreCase(type) ? o.getEmailOtp() : o.getSmsOtp();
        return expected != null && expected.equals(otp);
    }

    @Transactional
    public void markAsVerified(String uniqueCode) {
        otpRepository.findByUniqueCode(uniqueCode).ifPresent(o -> {
            o.setVerified(true);
            otpRepository.save(o);
        });
    }
}
