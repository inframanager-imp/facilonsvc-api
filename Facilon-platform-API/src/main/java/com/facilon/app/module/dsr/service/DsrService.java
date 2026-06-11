package com.facilon.app.module.dsr.service;

import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.module.dsr.dto.DsrCaseDetailDto;
import com.facilon.app.module.dsr.dto.DsrCaseCreateRequestDto;
import com.facilon.app.module.dsr.dto.DsrCaseResponseDto;
import com.facilon.app.module.dsr.model.DsrCase;
import com.facilon.app.module.dsr.repository.DsrCaseRepository;
import com.facilon.app.service.EmailService;
import com.facilon.app.util.EmailTemplateLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Investor-facing DSR operations: submit a request, list my requests, view a request
 * with its investor-visible timeline. Privacy Ops / Admin operations live in
 * {@link AdminDsrService}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DsrService {

    private final DsrCaseRepository dsrCaseRepository;
    private final InvestorRepository investorRepository;
    private final EmailService emailService;
    private final DsrCaseIdGenerator caseIdGenerator;
    private final DsrEvidenceService evidenceService;
    private final DsrPdfService pdfService;
    private final DsrStatusMapper statusMapper;
    private final DsrEventService eventService;
    private final EmailTemplateLoader emailTemplateLoader;

    @Value("${app.client_url:http://localhost:3000}")
    private String clientUrl;

    @Value("${app.dsr.privacy-email:privacy@facilonservices.com}")
    private String privacyEmail;

    public DsrCaseResponseDto submitCase(
            String investorUniqueCode,
            DsrCaseCreateRequestDto dto,
            MultipartFile[] supportingFiles) {

        Investor investor = investorRepository.findByUniqueCode(investorUniqueCode)
                .orElseThrow(() -> new NoSuchElementException("Investor not found"));

        DsrCase.RequestType requestType = parseRequestType(dto.getRequestType());
        DsrCase.Jurisdiction jurisdiction = parseJurisdiction(dto.getJurisdiction());

        String caseId = caseIdGenerator.nextCaseId();
        LocalDateTime slaDeadline = calculateSlaDeadline(jurisdiction);
        String evidenceFolder = evidenceService.createCaseFolder(caseId);
        String supportingFilePath = saveSupportingFiles(caseId, supportingFiles);

        DsrCase dsrCase = DsrCase.builder()
                .caseId(caseId)
                .investorUniqueCode(investorUniqueCode)
                .requestType(requestType)
                .jurisdiction(jurisdiction)
                .dataArea(dto.getDataArea())
                .spRelationFlag(DsrCase.SpRelationFlag.NO_SP)
                .requestDescription(dto.getRequestDescription())
                .requesterName(dto.getRequesterName())
                .requesterEmail(dto.getRequesterEmail())
                .requesterPhone(dto.getRequesterPhone())
                .requesterRole(dto.getRequesterRole())
                .supportingFilePath(supportingFilePath)
                .evidenceFolderPath(evidenceFolder)
                .status(DsrCase.CaseStatus.SUBMITTED)
                .verificationStatus(DsrCase.VerificationStatus.PENDING)
                .decision(DsrCase.Decision.PENDING)
                .slaDeadline(slaDeadline)
                .build();
        dsrCase.setTenant(investor.getTenant());

        DsrCase saved = dsrCaseRepository.save(dsrCase);

        eventService.record(saved, DsrCase.CaseStatus.SUBMITTED.name(),
                null, DsrCase.CaseStatus.SUBMITTED,
                "Request submitted by investor", saved.getRequesterName(), "INVESTOR", true);

        generateEvidencePdfs(saved);
        sendSubmissionNotifications(saved);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<DsrCaseResponseDto> getCasesForInvestor(String investorUniqueCode) {
        return dsrCaseRepository.findByInvestorUniqueCodeOrderByCreatedAtDesc(investorUniqueCode)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DsrCaseDetailDto getCaseDetailForInvestor(String investorUniqueCode, String caseId) {
        DsrCase dsrCase = dsrCaseRepository.findByCaseId(caseId)
                .orElseThrow(() -> new NoSuchElementException("DSR case not found"));
        if (!investorUniqueCode.equals(dsrCase.getInvestorUniqueCode())) {
            throw new SecurityException("Forbidden");
        }
        return DsrCaseDetailDto.builder()
                .request(toResponse(dsrCase))
                .timeline(eventService.investorTimeline(dsrCase.getId()))
                .build();
    }

    // ----- helpers -------------------------------------------------------------

    private DsrCase.RequestType parseRequestType(String value) {
        try {
            return DsrCase.RequestType.valueOf(normalizeEnum(value));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid request type: " + value);
        }
    }

    private DsrCase.Jurisdiction parseJurisdiction(String value) {
        try {
            return DsrCase.Jurisdiction.valueOf(normalizeEnum(value));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid jurisdiction: " + value);
        }
    }

    private String normalizeEnum(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Required enum field is empty");
        }
        return value.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
    }

    private LocalDateTime calculateSlaDeadline(DsrCase.Jurisdiction jurisdiction) {
        return switch (jurisdiction) {
            case HONG_KONG -> LocalDateTime.now().plusDays(40);
            case INDIA, CANADA, UK, UAE, SINGAPORE -> LocalDateTime.now().plusDays(30);
        };
    }

    private static final int MAX_SUPPORTING_FILES = 10;

    /**
     * Stores all uploaded supporting documents into {@code 01_Request} and returns the path of
     * the first one (kept on {@code supportingFilePath} for the legacy single-download button;
     * every file is also visible in the admin Evidence Files panel).
     */
    private String saveSupportingFiles(String caseId, MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return null;
        }
        if (files.length > MAX_SUPPORTING_FILES) {
            throw new IllegalArgumentException("A maximum of " + MAX_SUPPORTING_FILES + " supporting files is allowed");
        }
        String firstPath = null;
        int index = 0;
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            String originalName = file.getOriginalFilename() == null ? "attachment" : file.getOriginalFilename();
            String ext = evidenceService.getExtension(originalName);
            if (!ext.equals("pdf") && !ext.equals("jpg") && !ext.equals("jpeg")) {
                throw new IllegalArgumentException("Only PDF/JPG/JPEG files are allowed");
            }
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("Each supporting file must be <= 5MB");
            }
            index++;
            String storedName = "supporting-evidence-" + index + "-" + originalName;
            String path = evidenceService.storeFile(caseId, DsrEvidenceService.SUB_REQUEST, storedName, file);
            if (firstPath == null) {
                firstPath = path;
            }
        }
        return firstPath;
    }

    /**
     * Generates and stores the case evidence PDFs: the Request Submission record in
     * {@code 01_Request} and a copy of the acknowledgement in {@code 02_Acknowledgement}.
     * Best-effort — a PDF failure must not fail the submission.
     */
    private void generateEvidencePdfs(DsrCase dsrCase) {
        try {
            LocalDateTime base = dsrCase.getCreatedAt() != null ? dsrCase.getCreatedAt() : LocalDateTime.now();
            String date = base.toLocalDate().toString();
            String caseId = dsrCase.getCaseId();

            evidenceService.storeBytes(caseId, DsrEvidenceService.SUB_REQUEST,
                    caseId + "_Request_Submission_" + date + ".pdf",
                    pdfService.requestSummaryPdf(dsrCase));

            evidenceService.storeBytes(caseId, DsrEvidenceService.SUB_ACKNOWLEDGEMENT,
                    caseId + "_Acknowledgement_" + date + ".pdf",
                    pdfService.acknowledgementPdf(dsrCase));
        } catch (Exception e) {
            log.error("Failed generating DSR evidence PDFs for case {}", dsrCase.getCaseId(), e);
        }
    }

    private static final String TEMPLATE_DSR_ACKNOWLEDGMENT = "32-dsr-acknowledgment.html";
    private static final String TEMPLATE_DSR_PRIVACY_OPS = "33-dsr-privacy-ops-notification.html";

    /** Template variables shared by both DSR submission emails. Values are HTML-escaped here
     *  because {@link EmailTemplateLoader} does plain replacement without escaping. */
    private Map<String, String> dsrTemplateVariables(DsrCase dsrCase) {
        return Map.of(
                "caseId", safe(dsrCase.getCaseId()),
                "investorCode", safe(dsrCase.getInvestorUniqueCode()),
                "requesterName", safe(dsrCase.getRequesterName()),
                "requesterEmail", safe(dsrCase.getRequesterEmail()),
                "requestType", safe(dsrCase.getRequestType().name()),
                "jurisdiction", safe(dsrCase.getJurisdiction().name()),
                "submittedAt", formatDate(dsrCase.getCreatedAt() != null ? dsrCase.getCreatedAt() : LocalDateTime.now()),
                "slaDeadline", formatDate(dsrCase.getSlaDeadline()),
                "requestDescription", safe(dsrCase.getRequestDescription()),
                "baseUrl", clientUrl
        );
    }

    private void sendSubmissionNotifications(DsrCase dsrCase) {
        try {
            String requesterSubject = "Your DSR Request [" + dsrCase.getCaseId() + "] - Acknowledgment";
            String requesterBody = emailTemplateLoader.processTemplate(
                    TEMPLATE_DSR_ACKNOWLEDGMENT, dsrTemplateVariables(dsrCase));
            emailService.sendHtmlMessage(dsrCase.getRequesterEmail(), requesterSubject, requesterBody);
        } catch (Exception e) {
            log.error("Failed sending requester DSR acknowledgment for case {}", dsrCase.getCaseId(), e);
        }

        // TODO(UAT): Privacy Ops notification to privacyEmail (privacy@facilonservices.com) is
        // disabled for now. Re-enable this block during UAT testing so new DSR requests notify
        // the Privacy Ops inbox. Uses template 33-dsr-privacy-ops-notification.html (includes
        // the DSR description).
        /*
        try {
            String opsSubject = "DSR Request - " + dsrCase.getJurisdiction().name() + " - "
                    + dsrCase.getRequestType().name() + " - " + dsrCase.getCaseId();
            String opsBody = emailTemplateLoader.processTemplate(
                    TEMPLATE_DSR_PRIVACY_OPS, dsrTemplateVariables(dsrCase));
            emailService.sendHtmlMessage(privacyEmail, opsSubject, opsBody);
        } catch (Exception e) {
            log.error("Failed sending Privacy Ops DSR notification for case {}", dsrCase.getCaseId(), e);
        }
        */
    }

    private DsrCaseResponseDto toResponse(DsrCase dsrCase) {
        return DsrCaseResponseDto.builder()
                .caseId(dsrCase.getCaseId())
                .investorUniqueCode(dsrCase.getInvestorUniqueCode())
                .requestType(dsrCase.getRequestType() != null ? dsrCase.getRequestType().name() : null)
                .jurisdiction(dsrCase.getJurisdiction() != null ? dsrCase.getJurisdiction().name() : null)
                .dataArea(dsrCase.getDataArea())
                .requestDescription(dsrCase.getRequestDescription())
                .requesterName(dsrCase.getRequesterName())
                .requesterEmail(dsrCase.getRequesterEmail())
                .requesterPhone(dsrCase.getRequesterPhone())
                .requesterRole(dsrCase.getRequesterRole())
                .investorStatus(statusMapper.toInvestorStatus(dsrCase.getStatus()))
                .actionRequired(statusMapper.isActionRequired(dsrCase.getStatus()))
                .supportingFilePath(dsrCase.getSupportingFilePath())
                .submittedAt(formatDate(dsrCase.getCreatedAt()))
                .slaDeadline(formatDate(dsrCase.getSlaDeadline()))
                .resolvedAt(formatDate(dsrCase.getResolvedAt()))
                .resolutionNotes(dsrCase.getResolutionNotes())
                .finalOutcome(dsrCase.getFinalOutcome())
                .build();
    }

    private String formatDate(LocalDateTime value) {
        return value == null ? null : value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
