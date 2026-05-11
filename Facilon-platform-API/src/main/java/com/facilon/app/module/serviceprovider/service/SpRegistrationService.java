package com.facilon.app.module.serviceprovider.service;

import com.facilon.app.integration.graphemail.GraphEmailService;
import com.facilon.app.module.client.model.PowerAppContacts;
import com.facilon.app.module.client.repository.PowerAppContactsRepository;
import com.facilon.app.module.serviceprovider.dto.*;
import com.facilon.app.module.serviceprovider.model.ServiceProviderUser;
import com.facilon.app.module.serviceprovider.model.SpConsentUser;
import com.facilon.app.module.serviceprovider.repository.ServiceProviderUserRepository;
import com.facilon.app.module.serviceprovider.repository.SpConsentUserRepository;
import com.facilon.app.util.EmailTemplateLoader;
import com.facilon.app.util.LaravelEncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Direct port of the active Service Provider onboarding journey from Laravel
 * {@code BrokerController} — only the endpoints the inbound email URL actually reaches:
 *
 * <ol>
 *   <li>{@code service_provider_registration} (landing) → {@link #landing(String)}</li>
 *   <li>{@code services_provider_user} (user-consent display) → {@link #userConsentLanding(String)}</li>
 *   <li>{@code service_provider_step3_submit} (consent insert + PDF + mail)
 *       → {@link #submitUserConsent(SpUserConsentRequestDto, String)}</li>
 *   <li>{@code services_provider_user_register} (final form prefill)
 *       → {@link #userRegisterPrefill(String)}</li>
 *   <li>{@code service_provider_user_register_store} (insert user + welcome mail)
 *       → {@link #registerUser(SpUserRegisterRequestDto, String, String)}</li>
 * </ol>
 *
 * <p>Step 8b (Graph {@code /invitations} B2B invite) is intentionally deferred.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SpRegistrationService {

    /** Same hardcoded value Laravel uses in `services_provider_user.blade.php` and `sp-consent.blade.php`. */
    private static final String CONSENT_VERSION = "1.0";
    private static final String DEFAULT_PROVIDER_GREETING = "Partner";

    private static final String SP_INVITE_TEMPLATE = "sp-invite.html";
    private static final String SP_CONSENT_MAIL_TEMPLATE = "sp-user-consent-mail.html";
    private static final String SP_CONSENT_PDF_TEMPLATE = "sp-user-consent-pdf.html";
    private static final String SP_WELCOME_TEMPLATE = "sp-user-welcome.html";

    private static final String SP_CONSENT_MAIL_SUBJECT =
            "Service Provider User - Privacy & Consent Confirmation";
    private static final String SP_WELCOME_MAIL_SUBJECT =
            "Service Provider User - Registration Confirmation";

    private final PowerAppContactsRepository powerAppContactsRepository;
    private final SpConsentUserRepository spConsentUserRepository;
    private final ServiceProviderUserRepository serviceProviderUserRepository;
    private final EmailTemplateLoader emailTemplateLoader;
    private final PdfGeneratorService pdfGeneratorService;
    /** Optional so the API still boots when graph.email.enabled=false in some envs. */
    private final ObjectProvider<GraphEmailService> graphEmailServiceProvider;

    // ─── Step 1 — landing ───────────────────────────────────────────────────────

    public SpLandingDto landing(String status) {
        String email = decryptOrThrow(status);
        PowerAppContacts contact = lookupContact(email);
        String fullName = contact != null && contact.getFullName() != null
                ? contact.getFullName()
                : DEFAULT_PROVIDER_GREETING;
        return SpLandingDto.builder()
                .fullName(fullName)
                .email(email)
                .status(status)
                .build();
    }

    // ─── Step 2 — user consent landing ──────────────────────────────────────────

    public SpUserConsentLandingDto userConsentLanding(String uniqueCodes) {
        String email = decryptOrThrow(uniqueCodes);
        PowerAppContacts contact = lookupContact(email);
        String name = contact != null && contact.getFullName() != null ? contact.getFullName() : "";
        return SpUserConsentLandingDto.builder()
                .uniqueCode(uniqueCodes)
                .nameOfClient(name)
                .consentVersion(CONSENT_VERSION)
                .effectiveDate(LocalDate.now().toString())
                .build();
    }

    // ─── Step 3 — submit user consent (insert + PDF + mail) ─────────────────────

    @Transactional
    public SpUserConsentResponseDto submitUserConsent(SpUserConsentRequestDto dto,
                                                     String ipAddress,
                                                     String userAgent) {
        String email = decryptOrThrow(dto.getUniqueCode());
        PowerAppContacts contact = lookupContact(email);
        String providerName = contact != null && contact.getFullName() != null
                ? contact.getFullName()
                : "Service Provider";

        SpConsentUser row = SpConsentUser.builder()
                .uniqueCode(safeUniqueCode(contact))
                .consentVersion(dto.getConsentVersion())
                .effectiveDate(LocalDate.parse(dto.getEffectiveDate()))
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .consentGiven(true)
                .build();
        spConsentUserRepository.save(row);

        boolean mailed = false;
        try {
            byte[] pdfBytes = renderUserConsentPdf(providerName);
            String html = renderConsentMailHtml(providerName, dto.getConsentVersion());
            mailed = sendMailWithPdf(email, SP_CONSENT_MAIL_SUBJECT, html,
                    "Service Provider User Privacy & Consent Notice.pdf", pdfBytes);
        } catch (Exception e) {
            log.error("SP user-consent mail failed for {}: {}", email, e.getMessage(), e);
        }

        // Laravel redirects to ['service_provider_email' => $encryptedCode] —
        // the value is the same encrypted email Laravel already had in $unique_code,
        // so we just echo it back unchanged.
        return SpUserConsentResponseDto.builder()
                .serviceProviderEmail(dto.getUniqueCode())
                .message(mailed
                        ? "Submitted and consent email sent successfully."
                        : "Submitted; consent email could not be delivered.")
                .mailSent(mailed)
                .build();
    }

    // ─── Step 4 — final form prefill ────────────────────────────────────────────

    public SpUserPrefillDto userRegisterPrefill(String serviceProviderEmail) {
        String email = decryptOrThrow(serviceProviderEmail);
        PowerAppContacts contact = lookupContact(email);
        return SpUserPrefillDto.builder()
                .serviceProviderEmail(serviceProviderEmail)
                .fullName(opt(contact != null ? contact.getFullName() : null, "Client"))
                .complianceEmail(opt(contact != null ? contact.getEmail() : null, "NA"))
                .compliancePhoneNo(opt(contact != null ? contact.getPhone() : null, "NA"))
                .complianceName(opt(contact != null ? contact.getFirstName() : null, ""))
                .complianceLastName(opt(contact != null ? contact.getLastName() : null, ""))
                .build();
    }

    // ─── Step 5 — final form submit ─────────────────────────────────────────────

    @Transactional
    public SpUserRegisterResponseDto registerUser(SpUserRegisterRequestDto dto,
                                                  String ipAddress,
                                                  String userAgent) {
        String decryptedEmail;
        try {
            decryptedEmail = LaravelEncryptionUtil.decrypt(dto.getServiceProviderEmail());
        } catch (Exception e) {
            log.warn("SP register: invalid token {}", dto.getServiceProviderEmail());
            return SpUserRegisterResponseDto.builder()
                    .success(false)
                    .message("Invalid service provider link.")
                    .build();
        }

        if (serviceProviderUserRepository.existsByOfficialEmail(dto.getOfficialEmail())) {
            return SpUserRegisterResponseDto.builder()
                    .success(false)
                    .message("This official email is already registered.")
                    .build();
        }

        ServiceProviderUser saved = serviceProviderUserRepository.save(
                ServiceProviderUser.builder()
                        .serviceProviderName(dto.getServiceProviderNameHidden())
                        .firstName(dto.getFirstName())
                        .lastName(dto.getLastName())
                        .designation(dto.getDesignation())
                        .officialEmail(dto.getOfficialEmail())
                        .officialPhone(dto.getOfficialPhone())
                        .consent(Boolean.TRUE.equals(dto.getConsent()))
                        .build());

        boolean welcomeMailed = false;
        try {
            String html = renderWelcomeMailHtml(dto.getFirstName() + " " + dto.getLastName());
            welcomeMailed = sendPlainMail(dto.getOfficialEmail(), SP_WELCOME_MAIL_SUBJECT, html);
        } catch (Exception e) {
            log.error("SP welcome mail failed for {}: {}", dto.getOfficialEmail(), e.getMessage(), e);
        }

        // TODO: Slice C — call Microsoft Graph POST /v1.0/invitations using the
        // workforce-tenant credentials in tenant_graph_email_config. Deferred per stakeholder
        // direction (B2B vs B2C decision still open — see SP_REGISTRATION_PORT_PLAN.md §5).
        log.info("SP user {} registered (id={}); B2B /invitations call deferred", saved.getOfficialEmail(), saved.getId());

        return SpUserRegisterResponseDto.builder()
                .success(true)
                .message("Registration submitted successfully.")
                .welcomeMailSent(welcomeMailed)
                .b2bInviteSent(false)
                .build();
    }

    // ─── Helpers ────────────────────────────────────────────────────────────────

    private String decryptOrThrow(String token) {
        try {
            return LaravelEncryptionUtil.decrypt(token);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid or expired link", e);
        }
    }

    private PowerAppContacts lookupContact(String email) {
        return powerAppContactsRepository
                .findFirstByEmailIgnoreCaseOrderByIdDesc(email)
                .orElse(null);
    }

    private byte[] renderUserConsentPdf(String providerName) {
        Map<String, String> vars = new HashMap<>();
        vars.put("name_of_client", providerName != null ? providerName : "");
        vars.put("consent_version", CONSENT_VERSION);
        vars.put("effective_date_display", LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)));
        vars.put("privacy_policy_url", privacyPolicyUrl());
        String xhtml = emailTemplateLoader.processTemplate(SP_CONSENT_PDF_TEMPLATE, vars);
        return pdfGeneratorService.htmlToPdf(xhtml);
    }

    private String renderConsentMailHtml(String providerName, String consentVersion) {
        // Laravel composes timestamp like "08 Apr 2026, 14:35 IST (09:05 UTC)"
        ZoneId ist = ZoneId.of("Asia/Kolkata");
        ZoneId utc = ZoneId.of("UTC");
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH);
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.atZone(ZoneId.systemDefault()).withZoneSameInstant(ist).format(dateFmt)
                + " IST (" + now.atZone(ZoneId.systemDefault()).withZoneSameInstant(utc).format(timeFmt) + " UTC)";

        Map<String, String> vars = new HashMap<>();
        vars.put("service_provider_name", providerName != null ? providerName : "");
        vars.put("service_provider_legal_name", providerName != null ? providerName : "");
        vars.put("version", consentVersion != null ? consentVersion : CONSENT_VERSION);
        vars.put("timestamp", timestamp);
        vars.put("privacy_policy_url", privacyPolicyUrl());
        return emailTemplateLoader.processTemplate(SP_CONSENT_MAIL_TEMPLATE, vars);
    }

    private String renderWelcomeMailHtml(String name) {
        Map<String, String> vars = new HashMap<>();
        vars.put("name", name != null ? name.trim() : "");
        return emailTemplateLoader.processTemplate(SP_WELCOME_TEMPLATE, vars);
    }

    private boolean sendPlainMail(String to, String subject, String html) {
        GraphEmailService mailer = graphEmailServiceProvider.getIfAvailable();
        if (mailer == null) {
            log.warn("GraphEmailService unavailable; skipping mail to {}", to);
            return false;
        }
        return mailer.sendEmail(to, subject, html, null, null);
    }

    private boolean sendMailWithPdf(String to, String subject, String html,
                                    String filename, byte[] pdfBytes) {
        GraphEmailService mailer = graphEmailServiceProvider.getIfAvailable();
        if (mailer == null) {
            log.warn("GraphEmailService unavailable; skipping mail to {}", to);
            return false;
        }
        return mailer.sendEmailWithPdfBytes(to, subject, html, filename, pdfBytes);
    }

    private String safeUniqueCode(PowerAppContacts contact) {
        // Laravel uses $provider->unique_code ?? '' — PowerAppContacts has no unique_code column;
        // fall back to powerapp_contact_id which is the closest equivalent.
        if (contact == null) return "";
        return Optional.ofNullable(contact.getPowerAppContactId()).orElse("");
    }

    private String privacyPolicyUrl() {
        // Laravel emits route('privacy_policy_show'); React app owns this route now.
        return "/privacy-policy";
    }

    private String opt(String value, String fallback) {
        return (value != null && !value.isBlank()) ? value : fallback;
    }
}
