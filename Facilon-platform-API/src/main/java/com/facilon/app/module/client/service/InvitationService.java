package com.facilon.app.module.client.service;

import com.facilon.app.exception.ResourceNotFoundException;
import com.facilon.app.exception.BusinessException;
import com.facilon.app.module.client.dto.EmailInvitationDto;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.model.InvestorInvitation;
import com.facilon.app.module.client.repository.InvestorInvitationRepository;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvitationService {

    private final InvestorRepository investorRepository;
    private final InvestorInvitationRepository invitationRepository;
    private final EmailService emailService;

    private static final int INVITATION_EXPIRY_DAYS = 7;
    private static final int MAX_RESEND_COUNT = 3;

    @Transactional
    public EmailInvitationDto sendInvitation(String uniqueCode, EmailInvitationDto dto) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Sending invitation for investor: {}, to: {}", uniqueCode, dto.getRecipientEmail());

        // Check if invitation already exists and is pending/sent
        invitationRepository.findByRecipientEmailAndInvitationType(dto.getRecipientEmail(), dto.getInvitationType())
                .ifPresent(existing -> {
                    if (!"expired".equals(existing.getStatus()) && !"accepted".equals(existing.getStatus())) {
                        throw new BusinessException("Active invitation already exists for this email");
                    }
                });

        // Generate invitation token
        String token = UUID.randomUUID().toString();

        // Set dates
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusDays(INVITATION_EXPIRY_DAYS);

        InvestorInvitation invitation = InvestorInvitation.builder()
                .senderId(investor.getId())
                .recipientEmail(dto.getRecipientEmail())
                .recipientName(dto.getRecipientName())
                .invitationType(dto.getInvitationType())
                .invitationToken(token)
                .status("sent")
                .sentDate(now)
                .expiryDate(expiry)
                .message(dto.getMessage())
                .resendCount(0)
                .build();

        InvestorInvitation saved = invitationRepository.save(invitation);

        // Send email via EmailService
        sendInvitationEmail(saved);

        log.info("Invitation sent successfully with id: {}", saved.getId());
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<EmailInvitationDto> getInvitations(String uniqueCode) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Fetching invitations for investor: {}", uniqueCode);

        List<InvestorInvitation> invitations = invitationRepository.findBySenderId(investor.getId());
        return invitations.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public EmailInvitationDto resendInvitation(String uniqueCode, Long invitationId) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Resending invitation {} for investor: {}", invitationId, uniqueCode);

        InvestorInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        if (!invitation.getSenderId().equals(investor.getId())) {
            throw new ResourceNotFoundException("Invitation not found for this investor");
        }

        // Check if not expired
        if (invitation.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Invitation has expired");
        }

        if (invitation.getResendCount() >= MAX_RESEND_COUNT) {
            throw new BusinessException("Maximum resend limit reached");
        }

        // Update resend count and date
        invitation.setResendCount(invitation.getResendCount() + 1);
        invitation.setLastResendDate(LocalDateTime.now());
        invitation.setStatus("sent"); // Reset status if it was opened

        invitationRepository.save(invitation);

        // Resend email
        sendInvitationEmail(invitation);

        log.info("Invitation resent successfully");
        return mapToDto(invitation);
    }

    @Transactional
    public void markInvitationOpened(String token) {
        log.info("Marking invitation as opened: {}", token);

        invitationRepository.findByInvitationToken(token).ifPresent(invitation -> {
            if (invitation.getOpenedDate() == null) {
                invitation.setOpenedDate(LocalDateTime.now());
                if (!"accepted".equals(invitation.getStatus())) {
                    invitation.setStatus("opened");
                }
                invitationRepository.save(invitation);
            }
        });
    }

    @Transactional
    public void markInvitationAccepted(String token) {
        log.info("Marking invitation as accepted: {}", token);

        InvestorInvitation invitation = invitationRepository.findByInvitationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        invitation.setAcceptedDate(LocalDateTime.now());
        invitation.setStatus("accepted");
        invitationRepository.save(invitation);
    }

    private void sendInvitationEmail(InvestorInvitation invitation) {
        String subject = "You have been invited to Facilon";
        // Simple HTML body for now - in production use template engine
        String body = String.format(
                "<html><body>" +
                        "<h2>Hello %s,</h2>" +
                        "<p>You have been invited by %s to join Facilon.</p>" +
                        "<p>%s</p>" +
                        "<p><a href='https://platform.facilon.com/invite/%s'>Click here to accept invitation</a></p>" +
                        "</body></html>",
                invitation.getRecipientName(),
                "an investor", // Ideally query sender name
                invitation.getMessage() != null ? invitation.getMessage() : "",
                invitation.getInvitationToken());

        try {
            emailService.sendHtmlMessage(invitation.getRecipientEmail(), subject, body);
        } catch (Exception e) {
            log.error("Error sending invitation email", e);
            // Don't rollback transaction, just log error
        }
    }

    private Investor getInvestorByUniqueCode(String uniqueCode) {
        return investorRepository.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found with unique code: " + uniqueCode));
    }

    private EmailInvitationDto mapToDto(InvestorInvitation entity) {
        return EmailInvitationDto.builder()
                .id(entity.getId())
                .recipientEmail(entity.getRecipientEmail())
                .recipientName(entity.getRecipientName())
                .invitationType(entity.getInvitationType())
                .invitationToken(entity.getInvitationToken())
                .status(entity.getStatus())
                .sentDate(entity.getSentDate() != null ? entity.getSentDate().format(DateTimeFormatter.ISO_DATE_TIME)
                        : null)
                .expiryDate(
                        entity.getExpiryDate() != null ? entity.getExpiryDate().format(DateTimeFormatter.ISO_DATE_TIME)
                                : null)
                .message(entity.getMessage())
                .resendCount(entity.getResendCount())
                .lastResendDate(entity.getLastResendDate() != null
                        ? entity.getLastResendDate().format(DateTimeFormatter.ISO_DATE_TIME)
                        : null)
                .openedDate(
                        entity.getOpenedDate() != null ? entity.getOpenedDate().format(DateTimeFormatter.ISO_DATE_TIME)
                                : null)
                .acceptedDate(entity.getAcceptedDate() != null
                        ? entity.getAcceptedDate().format(DateTimeFormatter.ISO_DATE_TIME)
                        : null)
                .build();
    }
}
