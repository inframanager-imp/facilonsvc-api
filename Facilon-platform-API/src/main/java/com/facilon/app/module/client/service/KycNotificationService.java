package com.facilon.app.module.client.service;

import com.facilon.app.integration.graphemail.GraphEmailService;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.model.KycDocuments;
import com.facilon.app.module.client.repository.InvestorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Emits "expires soon" / "expired - re-upload required" emails for KYC docs.
 * Reuses the existing GraphEmailService plumbing.
 * Plan: KYC_DOCUMENT_PLAN §3.7.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KycNotificationService {

    private final GraphEmailService emailService;
    private final InvestorRepository investorRepository;

    public void notifyExpired(KycDocuments doc) {
        resolveRecipient(doc).ifPresent(investor -> {
            var user = investor.getAuthorizedUser();
            if (user == null || user.getEmailId() == null) return;
            String subject = "Your " + readable(doc.getDocumentType()) + " has expired";
            String html = buildHtml(user.getFirstName(), doc, true);
            boolean ok = emailService.sendEmail(user.getEmailId(), subject, html,
                    user.getFirstName(), user.getLastName());
            if (!ok) log.warn("Failed to send KYC expired email for doc {}", doc.getId());
        });
    }

    public void notifyExpiringSoon(KycDocuments doc) {
        resolveRecipient(doc).ifPresent(investor -> {
            var user = investor.getAuthorizedUser();
            if (user == null || user.getEmailId() == null) return;
            String subject = "Your " + readable(doc.getDocumentType()) + " expires soon";
            String html = buildHtml(user.getFirstName(), doc, false);
            boolean ok = emailService.sendEmail(user.getEmailId(), subject, html,
                    user.getFirstName(), user.getLastName());
            if (!ok) log.warn("Failed to send KYC expires-soon email for doc {}", doc.getId());
        });
    }

    private Optional<Investor> resolveRecipient(KycDocuments doc) {
        if (doc.getInvestorUniqueId() == null) return Optional.empty();
        return investorRepository.findByUniqueCode(doc.getInvestorUniqueId());
    }

    private String readable(String type) {
        if (type == null) return "KYC document";
        return switch (type) {
            case "PAN_CARD" -> "PAN card";
            case "PASSPORT" -> "passport";
            case "AADHAR_CARD" -> "Aadhaar";
            case "OCI_CARD" -> "OCI card";
            case "ADDRESS_PROOF" -> "address proof";
            default -> type;
        };
    }

    private String buildHtml(String firstName, KycDocuments doc, boolean expired) {
        String greeting = firstName != null
                ? "Hello " + firstName + ","
                : "Hello,";
        String status = expired
                ? "has expired. Please re-upload a current version to keep your KYC compliant."
                : "will expire on " + doc.getExpiryDate() + ". Please re-upload a current version at your convenience.";
        return "<p>" + greeting + "</p>"
                + "<p>Your " + readable(doc.getDocumentType()) + " " + status + "</p>"
                + "<p>You can upload the new document from the Smart Upload tab in your investor dashboard.</p>"
                + "<p>Regards,<br/>Facilon team</p>";
    }
}
