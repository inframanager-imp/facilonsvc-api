package com.facilon.app.integration.dynamics;

import com.facilon.app.config.TenantContextHolder;
import com.facilon.app.module.client.model.IntroduceInvestorEmails;
import com.facilon.app.module.client.model.master.MasterAccounts;
import com.facilon.app.module.client.repository.IntroduceInvestorEmailsRepository;
import com.facilon.app.module.client.repository.MasterAccountsRepository;
import com.facilon.app.module.client.service.InvestorNotificationService;
import com.facilon.app.util.LaravelEncryptionUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Emails newly-created Dataverse investors an introduction / registration link.
 *
 * <p>Direct port of Laravel {@code InnerPageController::latest_investor_check}
 * <b>and</b> {@code latest_investor_check_pms} (Investor/app/Http/Controllers/
 * InnerPageController.php). Laravel scheduled the broker variant
 * {@code ->everyMinute()} in {@code app/Console/Kernel.php}; both variants poll
 * the same {@code ss_investors} set and dedup on the same
 * {@code introduce_investor_emails} table — only the firm-name resolution hop
 * differs (broker vs portfolio manager). This service folds both into a single
 * pass: per investor it resolves the firm via {@code _ss_broker_value} when
 * present, otherwise {@code _ss_nameofportfoliomanager_value}.
 *
 * <h3>Laravel flow (mirrored step-for-step)</h3>
 * <ol>
 *   <li>GET Dataverse token (handled by {@link DynamicsTokenProvider})</li>
 *   <li>Fetch {@code /ss_investors?$filter=createdon ge {today} and createdon lt {tomorrow}}</li>
 *   <li>Per investor: resolve firm name via {@code /ss_brokers} or
 *       {@code /ss_portfoliomanagers} → {@code _ss_nameofthefirm_value} →
 *       local {@code master_accounts.name}</li>
 *   <li>Dedup against local {@code introduce_investor_emails} by {@code email_id}</li>
 *   <li>New email → insert row ({@code auto_mail_sent_count=1}) + send Graph mail
 *       with an encrypted {@code ss_name} registration link</li>
 *   <li>Existing email → increment {@code auto_mail_sent_count}</li>
 * </ol>
 *
 * <p><b>Tenancy:</b> the caller ({@link LatestInvestorCheckScheduler}) sets the
 * tenant context before invoking {@link #run()} so the Dynamics token, the
 * {@code master_accounts} lookup, the dedup table and the Graph mail config all
 * resolve for that tenant — reproducing Laravel's single-tenant behaviour once
 * per active tenant.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LatestInvestorCheckService {

    private final DynamicsTokenProvider tokenProvider;
    private final IntroduceInvestorEmailsRepository emailsRepository;
    private final MasterAccountsRepository masterAccountsRepository;
    private final InvestorNotificationService notificationService;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Base URL for the investor registration link. Laravel builds
     * {@code config('app.url') . '/introduce-investor1/' . Crypt::encrypt($code)}.
     * Configurable so dev/uat/prod can point at the correct front-end host.
     */
    @Value("${dataverse.latest-investor-check.intro-link-base-url:https://demo.facilonservices.com/demo/investor/introduce-investor1}")
    private String introLinkBaseUrl;

    /**
     * Run one pass for the CURRENT tenant context. Returns a summary map suitable
     * for scheduler/admin-endpoint logging.
     */
    @Transactional
    public Map<String, Object> run() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("fetched", 0);
        summary.put("skippedNoEmail", 0);
        summary.put("inserted", 0);
        summary.put("mailed", 0);
        summary.put("mailFailed", 0);
        summary.put("incremented", 0);
        summary.put("errors", 0);

        Long tenantId = currentTenantId();
        if (tenantId == null) {
            log.warn("[LatestInvestorCheck] No tenant in context; skipping run");
            return summary;
        }

        String token = tokenProvider.getDynamicsToken();
        String baseUrl = tokenProvider.getDynamicsBaseUrl();
        if (token == null || baseUrl == null) {
            log.warn("[LatestInvestorCheck] Dynamics token/baseUrl unavailable; skipping tenant {}", tenantId);
            return summary;
        }

        // Laravel: createdon ge {today} and createdon lt {tomorrow} (Y-m-d, bare)
        String today = LocalDate.now().toString();          // yyyy-MM-dd
        String tomorrow = LocalDate.now().plusDays(1).toString();

        JsonNode investors = fetchTodaysInvestors(token, baseUrl, today, tomorrow);
        if (investors == null || !investors.isArray() || investors.isEmpty()) {
            log.info("[LatestInvestorCheck] No investors created between {} and {}", today, tomorrow);
            return summary;
        }

        for (JsonNode investor : investors) {
            summary.merge("fetched", 1, (a, b) -> (int) a + (int) b);
            try {
                processInvestor(investor, token, baseUrl, tenantId, summary);
            } catch (Exception e) {
                summary.merge("errors", 1, (a, b) -> (int) a + (int) b);
                log.error("[LatestInvestorCheck] Failed processing investor {}: {}",
                        asText(investor, "ss_name"), e.getMessage(), e);
            }
        }
        return summary;
    }

    // ── Step 1 — ss_investors created today ──────────────────────────────────

    private JsonNode fetchTodaysInvestors(String token, String baseUrl, String today, String tomorrow) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/ss_investors")
                .queryParam("$select",
                        "ss_name,ss_emailintroduceind,ss_firstnameintroduceind,"
                                + "_ss_broker_value,_ss_nameofportfoliomanager_value,createdon")
                .queryParam("$filter",
                        "createdon ge " + today + " and createdon lt " + tomorrow)
                .build().encode().toUri();

        try {
            ResponseEntity<JsonNode> resp = restTemplate.exchange(
                    uri, HttpMethod.GET, new HttpEntity<>(odataHeaders(token)), JsonNode.class);
            JsonNode body = resp.getBody();
            return (body != null) ? body.get("value") : null;
        } catch (Exception e) {
            log.error("[LatestInvestorCheck] /ss_investors fetch failed: {}", e.getMessage());
            return null;
        }
    }

    // ── Steps 2–5 — per-investor processing ──────────────────────────────────

    private void processInvestor(JsonNode investor, String token, String baseUrl, Long tenantId, Map<String, Object> summary) {
        String email = asText(investor, "ss_emailintroduceind");
        String code = asText(investor, "ss_name");
        String name = asText(investor, "ss_firstnameintroduceind");
        String brokerValue = asText(investor, "_ss_broker_value");
        String pmsValue = asText(investor, "_ss_nameofportfoliomanager_value");

        // Laravel guards `if ($email)` — everything else is skipped without one.
        if (email == null || email.isBlank()) {
            summary.merge("skippedNoEmail", 1, (a, b) -> (int) a + (int) b);
            return;
        }

        // Resolve the firm/service-provider name (broker path first, else PMS path).
        String serviceProviderName = resolveServiceProviderName(brokerValue, pmsValue, token, baseUrl);

        if (emailsRepository.existsByEmailIdAndTenant_TenantId(email, tenantId)) {
            // Existing — bump auto_mail_sent_count (Laravel parity).
            Optional<IntroduceInvestorEmails> existing = emailsRepository.findByEmailIdAndTenant_TenantId(email, tenantId);
            existing.ifPresent(row -> {
                int current = row.getAutoMailSentCount() != null ? row.getAutoMailSentCount() : 0;
                row.setAutoMailSentCount(current + 1);
                emailsRepository.save(row);
            });
            summary.merge("incremented", 1, (a, b) -> (int) a + (int) b);
            return;
        }

        // New — insert dedup row first (Laravel inserts before sending, so a mail
        // failure does not cause a re-send on the next minute).
        IntroduceInvestorEmails row = IntroduceInvestorEmails.builder()
                .emailId(email)
                .name(name != null ? name : "")
                .code(code != null ? code : "")
                .autoEmail(1)
                .autoMailSentCount(1)
                .manualEmail(2)
                .createdAt(LocalDateTime.now())
                .build();
        emailsRepository.save(row);
        summary.merge("inserted", 1, (a, b) -> (int) a + (int) b);

        // Send the introduction email with the encrypted registration link.
        try {
            String introLink = buildIntroLink(code);
            notificationService.sendInvestorIntroductionEmail(email, name, serviceProviderName, introLink);
            summary.merge("mailed", 1, (a, b) -> (int) a + (int) b);
        } catch (Exception e) {
            summary.merge("mailFailed", 1, (a, b) -> (int) a + (int) b);
            log.error("[LatestInvestorCheck] Failed to send intro mail to {}: {}", email, e.getMessage());
        }
    }

    /**
     * Resolve the firm name shown in the email subject/body.
     *
     * <p>Laravel broker path: {@code GET /ss_brokers?$filter=ss_brokerid eq {guid}}
     * → {@code _ss_nameofthefirm_value} → {@code master_accounts.name}.
     * PMS path is identical against {@code /ss_portfoliomanagers} keyed by
     * {@code ss_portfoliomanagerid}.
     */
    private String resolveServiceProviderName(String brokerValue, String pmsValue, String token, String baseUrl) {
        String firmAccountId = null;
        if (brokerValue != null && !brokerValue.isBlank()) {
            firmAccountId = fetchFirmAccountId("ss_brokers", "ss_brokerid", brokerValue, false, token, baseUrl);
        } else if (pmsValue != null && !pmsValue.isBlank()) {
            // Laravel quotes the PMS guid: ss_portfoliomanagerid eq '{guid}'
            firmAccountId = fetchFirmAccountId("ss_portfoliomanagers", "ss_portfoliomanagerid", pmsValue, true, token, baseUrl);
        }
        if (firmAccountId == null || firmAccountId.isBlank()) {
            return null;
        }
        return masterAccountsRepository.findByAccountId(firmAccountId)
                .map(MasterAccounts::getName)
                .orElse(null);
    }

    private String fetchFirmAccountId(String entitySet, String idField, String guid, boolean quote,
                                      String token, String baseUrl) {
        String cleanGuid = guid.trim().replaceAll("^\\{|\\}$", "");
        String filterValue = quote ? "'" + cleanGuid + "'" : cleanGuid;
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/" + entitySet)
                .queryParam("$select", "_ss_nameofthefirm_value")
                .queryParam("$filter", idField + " eq " + filterValue)
                .build().encode().toUri();

        try {
            ResponseEntity<JsonNode> resp = restTemplate.exchange(
                    uri, HttpMethod.GET, new HttpEntity<>(odataHeaders(token)), JsonNode.class);
            JsonNode body = resp.getBody();
            if (body == null) return null;
            JsonNode value = body.get("value");
            if (value == null || !value.isArray() || value.isEmpty()) return null;
            return asText(value.get(0), "_ss_nameofthefirm_value");
        } catch (Exception e) {
            log.warn("[LatestInvestorCheck] {} lookup failed for {}: {}", entitySet, cleanGuid, e.getMessage());
            return null;
        }
    }

    /**
     * Laravel: {@code config('app.url') . '/introduce-investor1/' . Crypt::encrypt($code)}.
     * {@link LaravelEncryptionUtil} produces a wire-compatible ciphertext; the code
     * is URL-encoded so it survives as a single path segment.
     */
    private String buildIntroLink(String code) throws Exception {
        String encrypted = LaravelEncryptionUtil.encrypt(code != null ? code : "");
        String encoded = URLEncoder.encode(encrypted, StandardCharsets.UTF_8);
        return introLinkBaseUrl.replaceAll("/+$", "") + "/" + encoded;
    }

    private Long currentTenantId() {
        var context = TenantContextHolder.getContext();
        if (context == null || context.getTenant() == null) {
            return null;
        }
        return context.getTenant().getTenantId();
    }

    private HttpHeaders odataHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("OData-MaxVersion", "4.0");
        headers.set("OData-Version", "4.0");
        return headers;
    }

    private static String asText(JsonNode node, String field) {
        if (node == null) return null;
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? null : v.asText();
    }
}
