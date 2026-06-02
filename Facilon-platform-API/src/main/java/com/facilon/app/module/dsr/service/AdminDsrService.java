package com.facilon.app.module.dsr.service;

import com.facilon.app.module.dsr.dto.DsrAdminCaseDto;
import com.facilon.app.module.dsr.dto.DsrAdminUpdateRequestDto;
import com.facilon.app.module.dsr.dto.DsrDashboardSummaryDto;
import com.facilon.app.module.dsr.dto.DsrEvidenceFileDto;
import com.facilon.app.module.dsr.dto.DsrFilePayload;
import com.facilon.app.module.dsr.model.DsrCase;
import com.facilon.app.module.dsr.repository.DsrCaseRepository;
import com.facilon.app.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Privacy Ops / Admin DSR operations: queue, dashboard, case detail, and the full
 * resolution lifecycle (assign, verify, change status, decide, attach final response,
 * close). Every mutation logs a timeline event and, where relevant, emails the investor.
 *
 * <p>No Service-Provider routing this phase - the admin handles each case directly.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminDsrService {

    private final DsrCaseRepository dsrCaseRepository;
    private final DsrEventService eventService;
    private final DsrEvidenceService evidenceService;
    private final DsrStatusMapper statusMapper;
    private final EmailService emailService;

    // ----- read ----------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<DsrAdminCaseDto> listCases(String status, String requestType, String jurisdiction,
                                           Boolean overdueOnly, String assignedTo) {
        LocalDateTime now = LocalDateTime.now();
        return dsrCaseRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(c -> status == null || status.isBlank()
                        || (c.getStatus() != null && c.getStatus().name().equalsIgnoreCase(status)))
                .filter(c -> requestType == null || requestType.isBlank()
                        || (c.getRequestType() != null && c.getRequestType().name().equalsIgnoreCase(requestType)))
                .filter(c -> jurisdiction == null || jurisdiction.isBlank()
                        || (c.getJurisdiction() != null && c.getJurisdiction().name().equalsIgnoreCase(jurisdiction)))
                .filter(c -> assignedTo == null || assignedTo.isBlank()
                        || assignedTo.equalsIgnoreCase(c.getAssignedTo()))
                .filter(c -> !Boolean.TRUE.equals(overdueOnly) || isOverdue(c, now))
                .map(c -> toAdminDto(c, false))
                .toList();
    }

    @Transactional(readOnly = true)
    public DsrAdminCaseDto getCase(String caseId) {
        return toAdminDto(require(caseId), true);
    }

    @Transactional(readOnly = true)
    public DsrDashboardSummaryDto dashboard() {
        List<DsrCase> all = dsrCaseRepository.findAllByOrderByCreatedAtDesc();
        LocalDateTime now = LocalDateTime.now();
        YearMonth thisMonth = YearMonth.now();

        long open = all.stream().filter(this::isOpen).count();
        long overdue = all.stream().filter(c -> isOverdue(c, now)).count();
        long awaitingVerification = all.stream()
                .filter(c -> c.getStatus() == DsrCase.CaseStatus.VERIFICATION_PENDING).count();
        long closedThisMonth = all.stream()
                .filter(c -> c.getStatus() == DsrCase.CaseStatus.CLOSED)
                .filter(c -> c.getResolvedAt() != null
                        && YearMonth.from(c.getResolvedAt()).equals(thisMonth))
                .count();

        Map<String, Long> byType = all.stream()
                .filter(c -> c.getRequestType() != null)
                .collect(Collectors.groupingBy(c -> c.getRequestType().name(),
                        LinkedHashMap::new, Collectors.counting()));
        Map<String, Long> byStatus = all.stream()
                .filter(c -> c.getStatus() != null)
                .collect(Collectors.groupingBy(c -> c.getStatus().name(),
                        LinkedHashMap::new, Collectors.counting()));

        return DsrDashboardSummaryDto.builder()
                .total(all.size())
                .open(open)
                .overdue(overdue)
                .awaitingVerification(awaitingVerification)
                .closedThisMonth(closedThisMonth)
                .byRequestType(byType)
                .byStatus(byStatus)
                .build();
    }

    // ----- mutate --------------------------------------------------------------

    /** Applies any non-null fields of the payload, logs a timeline event, and may email the investor. */
    public DsrAdminCaseDto updateCase(String caseId, DsrAdminUpdateRequestDto dto,
                                      String actor, String actorRole) {
        DsrCase c = require(caseId);
        DsrCase.CaseStatus fromStatus = c.getStatus();

        if (dto.getAssignedTo() != null) {
            c.setAssignedTo(dto.getAssignedTo());
        }
        if (dto.getVerificationStatus() != null && !dto.getVerificationStatus().isBlank()) {
            c.setVerificationStatus(parse(DsrCase.VerificationStatus.class, dto.getVerificationStatus()));
        }
        if (dto.getVerificationMethod() != null) {
            c.setVerificationMethod(dto.getVerificationMethod());
        }
        if (dto.getDecision() != null && !dto.getDecision().isBlank()) {
            c.setDecision(parse(DsrCase.Decision.class, dto.getDecision()));
        }
        if (dto.getFinalOutcome() != null) {
            c.setFinalOutcome(dto.getFinalOutcome());
        }
        if (dto.getResolutionNotes() != null) {
            c.setResolutionNotes(dto.getResolutionNotes());
        }

        DsrCase.CaseStatus toStatus = fromStatus;
        boolean statusChanged = false;
        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            toStatus = parse(DsrCase.CaseStatus.class, dto.getStatus());
            statusChanged = toStatus != fromStatus;

            if (toStatus == DsrCase.CaseStatus.CLOSED
                    && (c.getDecision() == null || c.getDecision() == DsrCase.Decision.PENDING)) {
                throw new IllegalArgumentException("A decision must be recorded before closing the case");
            }

            c.setStatus(toStatus);
            if (isTerminal(toStatus) && c.getResolvedAt() == null) {
                c.setResolvedAt(LocalDateTime.now());
            }
            if (toStatus == DsrCase.CaseStatus.REOPENED) {
                c.setResolvedAt(null);
            }
        }

        DsrCase saved = dsrCaseRepository.save(c);

        boolean internalOnly = Boolean.TRUE.equals(dto.getInternalOnly());
        if (statusChanged) {
            boolean visible = !internalOnly && statusMapper.isInvestorVisible(toStatus);
            eventService.record(saved, toStatus.name(), fromStatus, toStatus,
                    dto.getNote(), actor, actorRole, visible);
            if (statusMapper.shouldNotifyInvestor(toStatus)) {
                sendTransitionEmail(saved, toStatus);
            }
        } else if (dto.getNote() != null && !dto.getNote().isBlank()) {
            eventService.record(saved, "NOTE", fromStatus, toStatus,
                    dto.getNote(), actor, actorRole, !internalOnly);
        }

        return toAdminDto(saved, true);
    }

    /** Stores an admin upload into the case evidence folder (default 09_Final_Response). */
    public DsrAdminCaseDto attachFile(String caseId, String subFolder, MultipartFile file,
                                      String actor, String actorRole) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file provided");
        }
        DsrCase c = require(caseId);
        String folder = (subFolder == null || subFolder.isBlank())
                ? DsrEvidenceService.SUB_FINAL_RESPONSE : subFolder;
        String original = file.getOriginalFilename() == null ? "document" : file.getOriginalFilename();
        evidenceService.storeFile(caseId, folder, original, file);
        eventService.record(c, "FILE_UPLOADED", c.getStatus(), c.getStatus(),
                "Uploaded " + original + " to " + folder, actor, actorRole, false);
        return toAdminDto(c, true);
    }

    @Transactional(readOnly = true)
    public DsrFilePayload loadSupportingFile(String caseId) {
        DsrCase c = require(caseId);
        if (c.getSupportingFilePath() == null || c.getSupportingFilePath().isBlank()) {
            throw new NoSuchElementException("No supporting file for case " + caseId);
        }
        try {
            Path path = Paths.get(c.getSupportingFilePath());
            byte[] data = Files.readAllBytes(path);
            String name = path.getFileName().toString();
            String contentType = Files.probeContentType(path);
            Resource resource = new ByteArrayResource(data);
            return new DsrFilePayload(resource, name,
                    contentType != null ? contentType : "application/octet-stream");
        } catch (IOException e) {
            throw new IllegalStateException("Failed reading supporting file for case " + caseId, e);
        }
    }

    /** Lists every file in the case's local evidence library, grouped by subfolder. */
    @Transactional(readOnly = true)
    public List<DsrEvidenceFileDto> listEvidenceFiles(String caseId) {
        DsrCase c = require(caseId);
        String root = c.getEvidenceFolderPath();
        if (root == null || root.isBlank()) {
            return List.of();
        }
        Path caseFolder = Paths.get(root);
        if (!Files.isDirectory(caseFolder)) {
            return List.of();
        }
        List<DsrEvidenceFileDto> files = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(caseFolder)) {
            walk.filter(Files::isRegularFile).forEach(p -> files.add(toEvidenceDto(caseFolder, p)));
        } catch (IOException e) {
            log.error("Failed listing evidence files for case {}", caseId, e);
        }
        files.sort(Comparator.comparing(DsrEvidenceFileDto::getRelativePath));
        return files;
    }

    /**
     * Streams a single evidence file by its path relative to the case folder.
     * Guards against path traversal — the resolved path must stay inside the case folder.
     */
    @Transactional(readOnly = true)
    public DsrFilePayload loadEvidenceFile(String caseId, String relativePath) {
        DsrCase c = require(caseId);
        String root = c.getEvidenceFolderPath();
        if (root == null || root.isBlank()) {
            throw new NoSuchElementException("No evidence folder for case " + caseId);
        }
        Path caseFolder = Paths.get(root).toAbsolutePath().normalize();
        Path target = caseFolder.resolve(relativePath).toAbsolutePath().normalize();
        if (!target.startsWith(caseFolder)) {
            throw new SecurityException("Invalid evidence path");
        }
        if (!Files.isRegularFile(target)) {
            throw new NoSuchElementException("Evidence file not found: " + relativePath);
        }
        try {
            byte[] data = Files.readAllBytes(target);
            String contentType = Files.probeContentType(target);
            return new DsrFilePayload(new ByteArrayResource(data), target.getFileName().toString(),
                    contentType != null ? contentType : "application/octet-stream");
        } catch (IOException e) {
            throw new IllegalStateException("Failed reading evidence file " + relativePath, e);
        }
    }

    // ----- helpers -------------------------------------------------------------

    private DsrEvidenceFileDto toEvidenceDto(Path caseFolder, Path file) {
        Path rel = caseFolder.relativize(file);
        Path parent = rel.getParent();
        long size = 0;
        String modified = null;
        try {
            size = Files.size(file);
            modified = Files.getLastModifiedTime(file).toInstant().toString();
        } catch (IOException ignored) {
            // metadata best-effort
        }
        return DsrEvidenceFileDto.builder()
                .folder(parent != null ? parent.toString().replace("\\", "/") : "")
                .name(file.getFileName().toString())
                .relativePath(rel.toString().replace("\\", "/"))
                .sizeBytes(size)
                .lastModified(modified)
                .build();
    }

    private DsrCase require(String caseId) {
        return dsrCaseRepository.findByCaseId(caseId)
                .orElseThrow(() -> new NoSuchElementException("DSR case not found: " + caseId));
    }

    private boolean isOpen(DsrCase c) {
        return c.getStatus() != DsrCase.CaseStatus.CLOSED
                && c.getStatus() != DsrCase.CaseStatus.REJECTED;
    }

    private boolean isOverdue(DsrCase c, LocalDateTime now) {
        return isOpen(c) && c.getSlaDeadline() != null && c.getSlaDeadline().isBefore(now);
    }

    private boolean isTerminal(DsrCase.CaseStatus s) {
        return s == DsrCase.CaseStatus.RESPONSE_SENT
                || s == DsrCase.CaseStatus.PARTIALLY_FULFILLED
                || s == DsrCase.CaseStatus.REJECTED
                || s == DsrCase.CaseStatus.CLOSED;
    }

    private <E extends Enum<E>> E parse(Class<E> type, String value) {
        try {
            return Enum.valueOf(type,
                    value.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_'));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid value '" + value + "' for " + type.getSimpleName());
        }
    }

    private DsrAdminCaseDto toAdminDto(DsrCase c, boolean withTimeline) {
        LocalDateTime now = LocalDateTime.now();
        return DsrAdminCaseDto.builder()
                .caseId(c.getCaseId())
                .investorUniqueCode(c.getInvestorUniqueCode())
                .requestType(c.getRequestType() != null ? c.getRequestType().name() : null)
                .jurisdiction(c.getJurisdiction() != null ? c.getJurisdiction().name() : null)
                .dataArea(c.getDataArea())
                .spRelationFlag(c.getSpRelationFlag() != null ? c.getSpRelationFlag().name() : null)
                .requestDescription(c.getRequestDescription())
                .requesterName(c.getRequesterName())
                .requesterEmail(c.getRequesterEmail())
                .requesterPhone(c.getRequesterPhone())
                .requesterRole(c.getRequesterRole())
                .status(c.getStatus() != null ? c.getStatus().name() : null)
                .investorStatus(statusMapper.toInvestorStatus(c.getStatus()))
                .actionRequired(statusMapper.isActionRequired(c.getStatus()))
                .slaOverdue(isOverdue(c, now))
                .assignedTo(c.getAssignedTo())
                .verificationStatus(c.getVerificationStatus() != null ? c.getVerificationStatus().name() : null)
                .verificationMethod(c.getVerificationMethod())
                .decision(c.getDecision() != null ? c.getDecision().name() : null)
                .finalOutcome(c.getFinalOutcome())
                .resolutionNotes(c.getResolutionNotes())
                .hasSupportingFile(c.getSupportingFilePath() != null && !c.getSupportingFilePath().isBlank())
                .evidenceFolderPath(c.getEvidenceFolderPath())
                .submittedAt(fmt(c.getCreatedAt()))
                .slaDeadline(fmt(c.getSlaDeadline()))
                .resolvedAt(fmt(c.getResolvedAt()))
                .updatedAt(fmt(c.getModifiedAt()))
                .timeline(withTimeline ? eventService.fullTimeline(c.getId()) : null)
                .build();
    }

    private void sendTransitionEmail(DsrCase c, DsrCase.CaseStatus status) {
        try {
            String caseId = safe(c.getCaseId());
            String subject;
            String body;
            switch (status) {
                case ACKNOWLEDGED -> {
                    subject = "We have received your data rights request - " + caseId;
                    body = "<p>Dear " + safe(c.getRequesterName()) + ",</p>"
                            + "<p>We have received your data rights request. Your reference number is "
                            + caseId + ".</p>"
                            + "<p>We may need to verify your identity or clarify the scope of your request "
                            + "before processing it. Facilon will review your request in accordance with "
                            + "applicable data protection laws and retention requirements.</p>"
                            + signature();
                }
                case VERIFICATION_PENDING -> {
                    subject = "Verification required for your data rights request - " + caseId;
                    body = "<p>Dear " + safe(c.getRequesterName()) + ",</p>"
                            + "<p>Before we can process your request (" + caseId + "), we need to verify "
                            + "your identity. Please open your request in the DSR Centre and respond.</p>"
                            + track() + signature();
                }
                case CLARIFICATION_PENDING -> {
                    subject = "Clarification required for your data rights request - " + caseId;
                    body = "<p>Dear " + safe(c.getRequesterName()) + ",</p>"
                            + "<p>We need additional information to continue processing your request ("
                            + caseId + "). Please open your request and respond to the clarification.</p>"
                            + track() + signature();
                }
                case RESPONSE_SENT, PARTIALLY_FULFILLED -> {
                    subject = "Response to your data rights request - " + caseId;
                    body = "<p>Dear " + safe(c.getRequesterName()) + ",</p>"
                            + "<p>A response to your data rights request (" + caseId + ") is now available "
                            + "in the Privacy &amp; Data Rights Centre.</p>"
                            + track() + signature();
                }
                case REJECTED -> {
                    subject = "Response to your data rights request - " + caseId;
                    body = "<p>Dear " + safe(c.getRequesterName()) + ",</p>"
                            + "<p>We have reviewed your data rights request (" + caseId + "). "
                            + "Please open your request in the DSR Centre to view the outcome.</p>"
                            + track() + signature();
                }
                case CLOSED -> {
                    subject = "Your data rights request has been closed - " + caseId;
                    body = "<p>Dear " + safe(c.getRequesterName()) + ",</p>"
                            + "<p>Your data rights request (" + caseId + ") has been closed. "
                            + "You can view the outcome in the Privacy &amp; Data Rights Centre.</p>"
                            + track() + signature();
                }
                default -> {
                    return;
                }
            }
            emailService.sendHtmlMessage(c.getRequesterEmail(), subject, body);
        } catch (Exception e) {
            log.error("Failed sending DSR transition email for case {} -> {}", c.getCaseId(), status, e);
        }
    }

    private String track() {
        return "<p>You can track this request in the Privacy &amp; Data Rights Centre after login.</p>";
    }

    private String signature() {
        return "<p>Regards,<br/>Privacy Team<br/>Facilon Services Private Limited</p>";
    }

    private String fmt(LocalDateTime v) {
        return v == null ? null : v.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private String safe(String v) {
        if (v == null) {
            return "";
        }
        return v.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
