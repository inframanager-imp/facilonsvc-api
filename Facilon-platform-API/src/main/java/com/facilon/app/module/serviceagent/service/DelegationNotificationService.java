package com.facilon.app.module.serviceagent.service;

import com.facilon.app.integration.graphemail.GraphEmailService;
import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.serviceagent.model.InvestorServiceAgentDelegation;
import com.facilon.app.module.serviceagent.model.ServiceAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

/**
 * Notifies the Service Agent and / or investor when a delegation changes state.
 *
 * <p>All methods are <b>fire-and-forget</b> — they swallow any exception from
 * {@link GraphEmailService} so a mail outage can never break the underlying
 * delegation operation.  The Laravel system did not emit any delegation emails
 * (because it had no Service Agent feature), so there is no parity constraint
 * on the exact content of these messages.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DelegationNotificationService {

    /** Optional — wired only when Graph credentials are present. */
    private final ObjectProvider<GraphEmailService> graphEmailServiceProvider;

    public void notifyAccepted(InvestorServiceAgentDelegation delegation,
                               Investor investor,
                               ServiceAgent agent) {
        sendToAgent(agent,
                "Delegation accepted — you can now act on behalf of " + investorDisplayName(investor),
                acceptedBodyForAgent(investor, delegation));
        sendToInvestor(investor,
                "You have accepted " + agentDisplayName(agent) + " as your Service Agent",
                acceptedBodyForInvestor(agent, delegation));
    }

    public void notifyRejected(InvestorServiceAgentDelegation delegation,
                               Investor investor,
                               ServiceAgent agent,
                               String reason) {
        sendToAgent(agent,
                "Delegation rejected by " + investorDisplayName(investor),
                rejectedBodyForAgent(investor, reason));
        sendToInvestor(investor,
                "You have declined " + agentDisplayName(agent) + " as your Service Agent",
                rejectedBodyForInvestor(agent, reason));
    }

    public void notifyRevoked(InvestorServiceAgentDelegation delegation,
                              Investor investor,
                              ServiceAgent agent,
                              String reason) {
        sendToAgent(agent,
                "Delegation revoked by " + investorDisplayName(investor),
                revokedBodyForAgent(investor, reason));
        sendToInvestor(investor,
                "Your delegation to " + agentDisplayName(agent) + " has been revoked",
                revokedBodyForInvestor(agent, reason));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Mail-send helpers
    // ─────────────────────────────────────────────────────────────────────────

    private void sendToAgent(ServiceAgent agent, String subject, String html) {
        if (agent == null || agent.getEmail() == null || agent.getEmail().isBlank()) return;
        send(agent.getEmail(), subject, html, agent.getFullName(), null);
    }

    private void sendToInvestor(Investor investor, String subject, String html) {
        String email = investorEmail(investor);
        if (email == null) return;
        AuthorizedUser user = investor.getAuthorizedUser();
        String firstName = user != null ? user.getFirstName() : null;
        String lastName = user != null ? user.getLastName() : null;
        send(email, subject, html, firstName, lastName);
    }

    private void send(String toEmail, String subject, String html, String firstName, String lastName) {
        GraphEmailService mailer = graphEmailServiceProvider.getIfAvailable();
        if (mailer == null) {
            log.debug("GraphEmailService not configured; skipping delegation email to {}", toEmail);
            return;
        }
        try {
            mailer.sendEmail(toEmail, subject, html, firstName, lastName);
        } catch (Exception e) {
            log.warn("Delegation email to {} failed: {}", toEmail, e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Body builders — kept minimal and inline; can be lifted to FreeMarker if
    // content authors need to edit copy without a code change.
    // ─────────────────────────────────────────────────────────────────────────

    private String acceptedBodyForAgent(Investor investor, InvestorServiceAgentDelegation d) {
        return "<p>Dear Agent,</p>"
                + "<p>" + escape(investorDisplayName(investor))
                + " has accepted your delegation request. "
                + "You can now act on their behalf with scope <b>"
                + escape(nullSafe(d.getScope())) + "</b>.</p>"
                + permissionsList(d)
                + "<p>Regards,<br>Team Facilon</p>";
    }

    private String acceptedBodyForInvestor(ServiceAgent agent, InvestorServiceAgentDelegation d) {
        return "<p>Dear Investor,</p>"
                + "<p>You have authorised <b>" + escape(agentDisplayName(agent))
                + "</b> to act as your Service Agent "
                + (d.getValidTo() != null
                    ? "until " + d.getValidTo() + "."
                    : "without an end date.")
                + "</p>"
                + permissionsList(d)
                + "<p>You can revoke this access at any time from your Facilon dashboard.</p>"
                + "<p>Regards,<br>Team Facilon</p>";
    }

    private String rejectedBodyForAgent(Investor investor, String reason) {
        return "<p>Dear Agent,</p>"
                + "<p>" + escape(investorDisplayName(investor))
                + " has declined your delegation request.</p>"
                + (reason != null && !reason.isBlank()
                    ? "<p><i>Reason:</i> " + escape(reason) + "</p>"
                    : "")
                + "<p>Regards,<br>Team Facilon</p>";
    }

    private String rejectedBodyForInvestor(ServiceAgent agent, String reason) {
        return "<p>Dear Investor,</p>"
                + "<p>You declined <b>" + escape(agentDisplayName(agent))
                + "</b> as your Service Agent.</p>"
                + (reason != null && !reason.isBlank()
                    ? "<p><i>Reason recorded:</i> " + escape(reason) + "</p>"
                    : "")
                + "<p>Regards,<br>Team Facilon</p>";
    }

    private String revokedBodyForAgent(Investor investor, String reason) {
        return "<p>Dear Agent,</p>"
                + "<p>" + escape(investorDisplayName(investor))
                + " has revoked your delegation access, effective immediately.</p>"
                + (reason != null && !reason.isBlank()
                    ? "<p><i>Reason:</i> " + escape(reason) + "</p>"
                    : "")
                + "<p>Regards,<br>Team Facilon</p>";
    }

    private String revokedBodyForInvestor(ServiceAgent agent, String reason) {
        return "<p>Dear Investor,</p>"
                + "<p>The delegation granted to <b>" + escape(agentDisplayName(agent))
                + "</b> has been revoked. "
                + "They can no longer view or act on your account.</p>"
                + (reason != null && !reason.isBlank()
                    ? "<p><i>Reason recorded:</i> " + escape(reason) + "</p>"
                    : "")
                + "<p>Regards,<br>Team Facilon</p>";
    }

    private String permissionsList(InvestorServiceAgentDelegation d) {
        StringBuilder sb = new StringBuilder("<p><b>Permissions granted:</b></p><ul>");
        if (Boolean.TRUE.equals(d.getCanViewProfile()))      sb.append("<li>View profile</li>");
        if (Boolean.TRUE.equals(d.getCanEditKyc()))          sb.append("<li>Edit KYC</li>");
        if (Boolean.TRUE.equals(d.getCanUploadDocuments()))  sb.append("<li>Upload documents</li>");
        if (Boolean.TRUE.equals(d.getCanSubmitForms()))      sb.append("<li>Submit forms</li>");
        sb.append("</ul>");
        return sb.toString();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Identity helpers
    // ─────────────────────────────────────────────────────────────────────────

    private String investorDisplayName(Investor investor) {
        if (investor == null) return "the investor";
        AuthorizedUser user = investor.getAuthorizedUser();
        if (user == null) return "the investor";
        String first = nullSafe(user.getFirstName());
        String last = nullSafe(user.getLastName());
        String name = (first + " " + last).trim();
        return name.isEmpty() ? "the investor" : name;
    }

    private String agentDisplayName(ServiceAgent agent) {
        if (agent == null) return "the Service Agent";
        if (agent.getFullName() != null && !agent.getFullName().isBlank()) return agent.getFullName();
        if (agent.getEmail() != null) return agent.getEmail();
        return "the Service Agent";
    }

    private String investorEmail(Investor investor) {
        if (investor == null || investor.getAuthorizedUser() == null) return null;
        String email = investor.getAuthorizedUser().getEmailId();
        return (email == null || email.isBlank()) ? null : email;
    }

    private static String nullSafe(String s) { return s == null ? "" : s; }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
