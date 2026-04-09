package com.facilon.app.module.client.service;

import com.facilon.app.module.client.dto.DsrCaseCreateRequestDto;
import com.facilon.app.module.client.dto.DsrCaseResponseDto;
import com.facilon.app.module.client.model.DsrCase;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.repository.DsrCaseRepository;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DsrService {

    private final DsrCaseRepository dsrCaseRepository;
    private final InvestorRepository investorRepository;
    private final EmailService emailService;

    @Value("${app.client_url:http://localhost:3000}")
    private String clientUrl;

    @Value("${app.dsr.privacy-email:privacy@facilonservices.com}")
    private String privacyEmail;

    @Value("${app.dsr.upload-dir:uploads/dsr}")
    private String dsrUploadDir;

    public DsrCaseResponseDto submitCase(
            String investorUniqueCode,
            DsrCaseCreateRequestDto dto,
            MultipartFile supportingFile) {

        Investor investor = investorRepository.findByUniqueCode(investorUniqueCode)
                .orElseThrow(() -> new NoSuchElementException("Investor not found"));

        DsrCase.RequestType requestType = parseRequestType(dto.getRequestType());
        DsrCase.Jurisdiction jurisdiction = parseJurisdiction(dto.getJurisdiction());

        String caseId = generateCaseId();
        LocalDateTime slaDeadline = calculateSlaDeadline(jurisdiction);
        String supportingFilePath = saveSupportingFile(caseId, supportingFile);

        DsrCase dsrCase = DsrCase.builder()
                .caseId(caseId)
                .investorUniqueCode(investorUniqueCode)
                .requestType(requestType)
                .jurisdiction(jurisdiction)
                .requestDescription(dto.getRequestDescription())
                .requesterName(dto.getRequesterName())
                .requesterEmail(dto.getRequesterEmail())
                .requesterPhone(dto.getRequesterPhone())
                .requesterRole(dto.getRequesterRole())
                .supportingFilePath(supportingFilePath)
                .status(DsrCase.CaseStatus.SUBMITTED)
                .slaDeadline(slaDeadline)
                .build();
        dsrCase.setTenant(investor.getTenant());

        DsrCase saved = dsrCaseRepository.save(dsrCase);
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
    public DsrCaseResponseDto getCaseForInvestor(String investorUniqueCode, String caseId) {
        DsrCase dsrCase = dsrCaseRepository.findByCaseId(caseId)
                .orElseThrow(() -> new NoSuchElementException("DSR case not found"));
        if (!investorUniqueCode.equals(dsrCase.getInvestorUniqueCode())) {
            throw new SecurityException("Forbidden");
        }
        return toResponse(dsrCase);
    }

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

    private String generateCaseId() {
        String year = String.valueOf(LocalDate.now().getYear());
        for (int i = 0; i < 10; i++) {
            String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase(Locale.ROOT);
            String caseId = "DSR-" + year + "-" + suffix;
            if (!dsrCaseRepository.existsByCaseId(caseId)) {
                return caseId;
            }
        }
        throw new IllegalStateException("Unable to generate unique DSR Case ID");
    }

    private LocalDateTime calculateSlaDeadline(DsrCase.Jurisdiction jurisdiction) {
        return switch (jurisdiction) {
            case HONG_KONG -> LocalDateTime.now().plusDays(40);
            case INDIA, CANADA, UK, UAE, SINGAPORE -> LocalDateTime.now().plusDays(30);
        };
    }

    private String saveSupportingFile(String caseId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalName = file.getOriginalFilename() == null ? "attachment" : file.getOriginalFilename();
        String ext = getExtension(originalName).toLowerCase(Locale.ROOT);
        if (!ext.equals("pdf") && !ext.equals("jpg") && !ext.equals("jpeg")) {
            throw new IllegalArgumentException("Only PDF/JPG/JPEG files are allowed");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Supporting file must be <= 5MB");
        }

        try {
            Path folder = Paths.get(dsrUploadDir, caseId);
            Files.createDirectories(folder);
            String safeName = "supporting-evidence." + ext;
            Path target = folder.resolve(safeName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return target.toString().replace("\\", "/");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store supporting file", e);
        }
    }

    private String getExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index < 0 || index == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(index + 1);
    }

    private void sendSubmissionNotifications(DsrCase dsrCase) {
        try {
            String requesterSubject = "Your DSR Request [" + dsrCase.getCaseId() + "] - Acknowledgment";
            String requesterBody = "<p>Dear " + safe(dsrCase.getRequesterName()) + ",</p>"
                    + "<p>Your Data Subject Rights request has been received.</p>"
                    + "<p><strong>Case ID:</strong> " + safe(dsrCase.getCaseId()) + "<br/>"
                    + "<strong>Right:</strong> " + safe(dsrCase.getRequestType().name()) + "<br/>"
                    + "<strong>Jurisdiction:</strong> " + safe(dsrCase.getJurisdiction().name()) + "<br/>"
                    + "<strong>Submitted At:</strong> " + formatDate(dsrCase.getCreatedAt()) + "<br/>"
                    + "<strong>SLA Deadline:</strong> " + formatDate(dsrCase.getSlaDeadline()) + "</p>"
                    + "<p>Our Privacy Ops team will follow up within 48 hours.</p>"
                    + "<p>You can track updates after login: <a href=\"" + clientUrl + "/investor/dsr-center\">DSR Center</a></p>";
            emailService.sendHtmlMessage(dsrCase.getRequesterEmail(), requesterSubject, requesterBody);
        } catch (Exception e) {
            log.error("Failed sending requester DSR acknowledgment for case {}", dsrCase.getCaseId(), e);
        }

        try {
            String opsSubject = "DSR Request - " + dsrCase.getJurisdiction().name() + " - "
                    + dsrCase.getRequestType().name() + " - " + dsrCase.getCaseId();
            String opsBody = "<p>New DSR request received.</p>"
                    + "<p><strong>Case ID:</strong> " + safe(dsrCase.getCaseId()) + "<br/>"
                    + "<strong>Investor Code:</strong> " + safe(dsrCase.getInvestorUniqueCode()) + "<br/>"
                    + "<strong>Requester:</strong> " + safe(dsrCase.getRequesterName()) + " (" + safe(dsrCase.getRequesterEmail()) + ")<br/>"
                    + "<strong>Jurisdiction:</strong> " + safe(dsrCase.getJurisdiction().name()) + "<br/>"
                    + "<strong>Right:</strong> " + safe(dsrCase.getRequestType().name()) + "<br/>"
                    + "<strong>SLA Deadline:</strong> " + formatDate(dsrCase.getSlaDeadline()) + "</p>"
                    + "<p><strong>Description:</strong><br/>" + safe(dsrCase.getRequestDescription()) + "</p>";
            emailService.sendHtmlMessage(privacyEmail, opsSubject, opsBody);
        } catch (Exception e) {
            log.error("Failed sending Privacy Ops DSR notification for case {}", dsrCase.getCaseId(), e);
        }
    }

    private String formatDate(LocalDateTime value) {
        if (value == null) {
            return "";
        }
        return value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private DsrCaseResponseDto toResponse(DsrCase dsrCase) {
        return DsrCaseResponseDto.builder()
                .caseId(dsrCase.getCaseId())
                .investorUniqueCode(dsrCase.getInvestorUniqueCode())
                .requestType(dsrCase.getRequestType() != null ? dsrCase.getRequestType().name() : null)
                .jurisdiction(dsrCase.getJurisdiction() != null ? dsrCase.getJurisdiction().name() : null)
                .requestDescription(dsrCase.getRequestDescription())
                .requesterName(dsrCase.getRequesterName())
                .requesterEmail(dsrCase.getRequesterEmail())
                .requesterPhone(dsrCase.getRequesterPhone())
                .requesterRole(dsrCase.getRequesterRole())
                .status(dsrCase.getStatus() != null ? dsrCase.getStatus().name() : null)
                .supportingFilePath(dsrCase.getSupportingFilePath())
                .submittedAt(formatDate(dsrCase.getCreatedAt()))
                .slaDeadline(formatDate(dsrCase.getSlaDeadline()))
                .resolvedAt(formatDate(dsrCase.getResolvedAt()))
                .resolutionNotes(dsrCase.getResolutionNotes())
                .build();
    }
}
