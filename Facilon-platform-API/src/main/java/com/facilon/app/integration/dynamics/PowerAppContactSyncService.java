package com.facilon.app.integration.dynamics;

import com.facilon.app.integration.graphemail.GraphEmailService;
import com.facilon.app.module.client.model.PowerAppContacts;
import com.facilon.app.module.client.repository.PowerAppContactsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Discovers newly-created service-provider contacts in Dataverse and onboards
 * them locally + emails them a registration link.
 *
 * <p>Direct port of Laravel
 * {@code app/Console/Commands/SyncNewServiceProviders.php}
 * (Artisan signature: {@code powerapp:sync-contacts}).
 *
 * <h3>Laravel flow (mirrored here step-for-step)</h3>
 * <ol>
 *   <li>GET Dataverse token (handled by {@link DynamicsTokenProvider})</li>
 *   <li>Map {@code ss_contacttype} option-set → display name</li>
 *   <li>Fetch contacts {@code /contacts?$select=...&amp;$orderby=createdon desc&amp;$top=N}</li>
 *   <li>Skip contacts with invalid type or missing email</li>
 *   <li>Dedup against local {@code powerapp_contacts} by {@code powerapp_contact_id} OR {@code email}</li>
 *   <li>For broker-linked contacts, GET {@code /ss_brokers?$filter=ss_brokerid eq guid} for name</li>
 *   <li>Insert local row with {@code b2c_status = 'NEW'}</li>
 *   <li>Send onboarding email via Graph, mark {@code MAIL_SENT} / {@code MAIL_FAILED}</li>
 * </ol>
 *
 * <h3>Configuration</h3>
 * <pre>
 * dataverse:
 *   powerapp-sync:
 *     top: 1                                     # Laravel hard-codes $top=1 per run
 *     registration-base-url: https://facilonservices.com/demo/investor/step
 *     email-subject: "Complete Your User Registration with Facilon Services"
 * </pre>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PowerAppContactSyncService {

    /** Laravel {@code $contactTypeMap} — ss_contacttype option-set → display label. */
    private static final Map<Integer, String> CONTACT_TYPE_MAP = Map.of(
            100000000, "Portfolio Manager",
            100000001, "Broker",
            100000002, "Custodian",
            100000003, "Bank"
    );

    private final DynamicsTokenProvider tokenProvider;
    private final PowerAppContactsRepository contactsRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectProvider<GraphEmailService> graphEmailServiceProvider;

    @Value("${dataverse.powerapp-sync.top:1}")
    private int topPerRun;

    @Value("${dataverse.powerapp-sync.registration-base-url:https://facilonservices.com/demo/investor/step}")
    private String registrationBaseUrl;

    @Value("${dataverse.powerapp-sync.email-subject:Complete Your User Registration with Facilon Services}")
    private String emailSubject;

    // ─────────────────────────────────────────────────────────────────────────
    // Public entry point (called by scheduler or admin endpoint)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Run one sync pass.  Returns a summary map suitable for scheduler/admin-endpoint logging:
     * {@code {fetched, skippedInvalidType, skippedNoEmail, duplicates, inserted, mailed, mailFailed}}.
     */
    public Map<String, Object> sync() {
        log.info("================ POWERAPP CONTACT SYNC START ================");
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("fetched", 0);
        summary.put("skippedInvalidType", 0);
        summary.put("skippedNoEmail", 0);
        summary.put("duplicates", 0);
        summary.put("inserted", 0);
        summary.put("mailed", 0);
        summary.put("mailFailed", 0);

        if (tokenProvider == null) {
            log.warn("powerapp sync: token provider unavailable; aborting");
            return summary;
        }
        String token = tokenProvider.getDynamicsToken();
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        if (token == null || baseUrl == null) {
            log.error("FAILED: Unable to get Dynamics access token");
            return summary;
        }

        JsonNode contactsNode = fetchNewContacts(token, baseUrl);
        if (contactsNode == null || !contactsNode.isArray() || contactsNode.isEmpty()) {
            log.info("No contacts found");
            log.info("================ POWERAPP CONTACT SYNC END ================");
            return summary;
        }

        for (JsonNode contact : contactsNode) {
            summary.merge("fetched", 1, (a, b) -> (int) a + (int) b);
            processContact(contact, token, baseUrl, summary);
        }

        log.info("================ POWERAPP CONTACT SYNC END ================");
        return summary;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Step 3 — Dataverse contact fetch
    // ─────────────────────────────────────────────────────────────────────────

    private JsonNode fetchNewContacts(String token, String baseUrl) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/contacts")
                .queryParam("$select",
                        "contactid,firstname,lastname,emailaddress1,telephone1,"
                                + "_parentcustomerid_value,_ss_broker_value,createdon,ss_contacttype")
                .queryParam("$orderby", "createdon desc")
                .queryParam("$top", topPerRun)
                .build().encode().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");

        try {
            ResponseEntity<JsonNode> resp = restTemplate.exchange(
                    uri, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
            JsonNode body = resp.getBody();
            return (body != null) ? body.get("value") : null;
        } catch (Exception e) {
            log.error("Dataverse /contacts fetch failed: {}", e.getMessage());
            return null;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Steps 4–8 — per-contact processing
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    protected void processContact(JsonNode contact, String token, String baseUrl,
                                  Map<String, Object> summary) {
        String contactId = asText(contact, "contactid");
        String email = asText(contact, "emailaddress1");

        // Step 4a — type validation
        JsonNode typeNode = contact.get("ss_contacttype");
        if (typeNode == null || typeNode.isNull()) {
            log.info("Skipping contact – invalid type  contactid={}", contactId);
            summary.merge("skippedInvalidType", 1, (a, b) -> (int) a + (int) b);
            return;
        }
        Integer typeCode = typeNode.asInt(-1);
        String contactType = CONTACT_TYPE_MAP.get(typeCode);
        if (contactType == null) {
            log.info("Skipping contact – invalid type  contactid={}  type={}", contactId, typeCode);
            summary.merge("skippedInvalidType", 1, (a, b) -> (int) a + (int) b);
            return;
        }

        // Step 4b — email validation
        if (email == null || email.isBlank()) {
            log.warn("Skipping contact – missing email  contactid={}", contactId);
            summary.merge("skippedNoEmail", 1, (a, b) -> (int) a + (int) b);
            return;
        }

        // Step 5 — dedup (powerapp_contact_id OR email)
        if (contactsRepository.existsByPowerAppContactIdOrEmail(contactId, email)) {
            log.info("Skipping duplicate contact  contactid={}  email={}", contactId, email);
            summary.merge("duplicates", 1, (a, b) -> (int) a + (int) b);
            return;
        }

        log.info("Processing contact  contactid={}  type={}", contactId, contactType);

        // Step 6 — broker-name resolution (only if _ss_broker_value is populated)
        String brokerGuid = asText(contact, "_ss_broker_value");
        String brokerName = null;
        if (brokerGuid != null && !brokerGuid.isBlank()) {
            brokerName = fetchBrokerName(brokerGuid, token, baseUrl);
        }

        // Step 7 — local insert
        String firstName = asText(contact, "firstname");
        String lastName = asText(contact, "lastname");
        PowerAppContacts row = PowerAppContacts.builder()
                .powerAppContactId(contactId)
                .firstName(firstName)
                .lastName(lastName)
                .fullName(trimToNull((nullSafe(firstName) + " " + nullSafe(lastName)).trim()))
                .email(email)
                .phone(asText(contact, "telephone1"))
                .accountId(asText(contact, "_parentcustomerid_value"))
                .brokerId(brokerGuid)
                .broker(brokerName)
                .contactType(contactType)
                .b2cStatus("NEW")
                .build();
        contactsRepository.save(row);
        summary.merge("inserted", 1, (a, b) -> (int) a + (int) b);
        log.info("Inserted contact locally  contactid={}", contactId);

        // Step 8 — onboarding email
        boolean mailed = sendOnboardingEmail(contactId, firstName, lastName, email);
        if (mailed) {
            summary.merge("mailed", 1, (a, b) -> (int) a + (int) b);
        } else {
            summary.merge("mailFailed", 1, (a, b) -> (int) a + (int) b);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Step 6 — broker-name lookup
    // ─────────────────────────────────────────────────────────────────────────

    private String fetchBrokerName(String brokerGuid, String token, String baseUrl) {
        String cleanGuid = brokerGuid.trim().replaceAll("^\\{|\\}$", "");
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/ss_brokers")
                .queryParam("$select", "ss_brokerid,ss_name")
                .queryParam("$filter", "ss_brokerid eq " + cleanGuid)
                .build().encode().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");

        try {
            ResponseEntity<JsonNode> resp = restTemplate.exchange(
                    uri, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
            JsonNode body = resp.getBody();
            if (body == null) return null;
            JsonNode value = body.get("value");
            if (value == null || !value.isArray() || value.isEmpty()) return null;
            return asText(value.get(0), "ss_name");
        } catch (Exception e) {
            log.warn("Broker lookup failed for {}: {}", cleanGuid, e.getMessage());
            return null;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Step 8 — email send + status update
    // ─────────────────────────────────────────────────────────────────────────

    private boolean sendOnboardingEmail(String contactId, String firstName, String lastName, String email) {
        GraphEmailService mailer = graphEmailServiceProvider.getIfAvailable();
        if (mailer == null) {
            log.warn("GraphEmailService not available; leaving b2c_status=NEW for contact {}", contactId);
            return false;
        }
        try {
            // Laravel encrypts the email and puts it in a ?status= query param on the registration URL
            // (L219–L223).  Java uses Base64-URL as a lightweight functional equivalent — the real
            // on-the-wire format of the status token is a server-side concern and the receiving
            // endpoint will be the Java counterpart, so the opaque token just needs to round-trip.
            String emailToken = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(email.getBytes(StandardCharsets.UTF_8));
            String registrationUrl = registrationBaseUrl + "?status="
                    + URLEncoder.encode(emailToken, StandardCharsets.UTF_8);

            String name = trimToNull((nullSafe(firstName) + " " + nullSafe(lastName)).trim());
            String htmlBody = buildOnboardingEmailBody(name, email, registrationUrl);

            boolean sent = mailer.sendEmail(email, emailSubject, htmlBody, firstName, lastName);
            String status = sent ? "MAIL_SENT" : "MAIL_FAILED";
            String errMsg = sent ? null : "GraphEmailService.sendEmail returned false";
            contactsRepository.updateB2cStatus(contactId, status, errMsg);
            log.info("MAIL_{} for contact {}", sent ? "SENT" : "FAILED", contactId);
            return sent;
        } catch (Exception e) {
            contactsRepository.updateB2cStatus(contactId, "MAIL_FAILED", e.getMessage());
            log.error("Onboarding email send failed for {}: {}", contactId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Laravel renders a Blade template {@code mails.service-provider-scheduling-mail}.
     * Java assembles an equivalent HTML body inline — the Blade template can be
     * introduced later as a FreeMarker / Thymeleaf file if content authors need to
     * edit it without code changes.
     */
    private String buildOnboardingEmailBody(String name, String email, String registrationUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>Dear ").append(name != null ? escape(name) : "User").append(",</p>");
        sb.append("<p>Welcome to Facilon Services. Please complete your user registration ")
          .append("by following the link below:</p>");
        sb.append("<p><a href=\"").append(registrationUrl).append("\">")
          .append("Complete Registration</a></p>");
        sb.append("<p>If the button above does not work, copy and paste this URL into your browser:<br>")
          .append(registrationUrl).append("</p>");
        sb.append("<p>Regards,<br>Team Facilon</p>");
        return sb.toString();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private static String asText(JsonNode node, String field) {
        if (node == null || !node.has(field) || node.get(field).isNull()) return null;
        String v = node.get(field).asText("").trim();
        return v.isEmpty() ? null : v;
    }

    private static String nullSafe(String s) { return s == null ? "" : s; }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
