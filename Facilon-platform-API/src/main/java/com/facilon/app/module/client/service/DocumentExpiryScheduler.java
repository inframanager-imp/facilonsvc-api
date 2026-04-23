package com.facilon.app.module.client.service;

import com.facilon.app.module.client.model.KycDocuments;
import com.facilon.app.module.client.model.KycValidationStatus;
import com.facilon.app.module.client.repository.KycDocumentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Nightly job that flips expired docs, emits notifications, and feeds the
 * Smart Upload "expires soon" banner.
 * Plan: KYC_DOCUMENT_PLAN §3.7.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentExpiryScheduler {

    private final KycDocumentsRepository kycRepository;
    private final KycNotificationService notificationService;

    @Value("${kyc.expiry.warn-window-days:30}")
    private int warnWindowDays;

    @Value("${kyc.expiry.notify-interval-days:7}")
    private int notifyIntervalDays;

    @Value("${kyc.expiry.cron-enabled:true}")
    private boolean enabled;

    @Scheduled(cron = "${kyc.expiry.cron:0 0 2 * * *}")
    @Transactional
    public void run() {
        if (!enabled) return;
        LocalDate today = LocalDate.now();
        LocalDate cutoff = today.plusDays(warnWindowDays);

        List<KycDocuments> candidates = kycRepository.findExpiringOnOrBefore(cutoff);
        int expired = 0, expiringSoon = 0, skipped = 0;
        LocalDateTime now = LocalDateTime.now();

        for (KycDocuments doc : candidates) {
            boolean isExpired = doc.getExpiryDate().isBefore(today);
            if (isExpired) {
                doc.setValidationStatus(KycValidationStatus.EXPIRED.name());
                if (shouldNotify(doc, now)) {
                    notificationService.notifyExpired(doc);
                    doc.setExpiryNotifiedAt(now);
                    expired++;
                } else {
                    skipped++;
                }
            } else {
                // expires within warn window
                if (shouldNotify(doc, now)) {
                    notificationService.notifyExpiringSoon(doc);
                    doc.setExpiryNotifiedAt(now);
                    expiringSoon++;
                } else {
                    skipped++;
                }
            }
            kycRepository.save(doc);
        }
        log.info("KYC expiry scan: expired={}, expiringSoon={}, throttled={}",
                expired, expiringSoon, skipped);
    }

    private boolean shouldNotify(KycDocuments doc, LocalDateTime now) {
        if (doc.getExpiryNotifiedAt() == null) return true;
        return doc.getExpiryNotifiedAt().plusDays(notifyIntervalDays).isBefore(now);
    }
}
