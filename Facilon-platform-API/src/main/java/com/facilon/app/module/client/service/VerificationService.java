package com.facilon.app.module.client.service;

import com.facilon.app.integration.dynamics.DynamicsCrmService;
import com.facilon.app.integration.graphemail.GraphEmailService;
import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.module.client.model.master.MasterAccounts;
import com.facilon.app.module.client.model.master.MasterPlans;
import com.facilon.app.module.client.model.master.MasterProducts;
import com.facilon.app.module.client.dto.PhysicalSubmissionDto;
import com.facilon.app.module.client.dto.VerificationAppointmentDto;
import com.facilon.app.module.client.dto.VerificationStatusDto;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.model.IntroInvestorTemp;
import com.facilon.app.module.client.model.InvestorPhysicalSubmission;
import com.facilon.app.module.client.model.KycDocuments;
import com.facilon.app.module.client.model.UserPersonalInformation;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.module.client.repository.IntroInvestorTempRepository;
import com.facilon.app.module.client.repository.InvestorPhysicalSubmissionRepository;
import com.facilon.app.module.client.repository.KycDocumentsRepository;
import com.facilon.app.module.client.repository.MasterAccountsRepository;
import com.facilon.app.module.client.repository.MasterPlansRepository;
import com.facilon.app.module.client.repository.MasterProductsRepository;
import com.facilon.app.module.client.repository.UserPersonalInformationRepository;
import com.facilon.app.repository.AuthorizedUserRepository;
import com.facilon.app.util.EmailTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationService {

    private final InvestorRepository investorRepository;
    private final UserPersonalInformationRepository personalInformationRepository;
    private final InvestorPhysicalSubmissionRepository physicalSubmissionRepository;
    private final IntroInvestorTempRepository introInvestorTempRepository;
    private final DynamicsCrmService dynamicsCrmService; // For real-time Dataverse sync
    private final AuthorizedUserRepository authorizedUserRepository;
    private final MasterAccountsRepository masterAccountsRepository;
    private final KycDocumentsRepository kycDocumentsRepository;
    private final MasterProductsRepository masterProductsRepository;
    private final MasterPlansRepository masterPlansRepository;
    private final EmailTemplateLoader emailTemplateLoader;
    private final FreeMarkerConfigurer freeMarkerConfigurer;
    
    @Autowired(required = false) // Optional - only if email service is enabled
    private GraphEmailService graphEmailService;
    // TODO: Inject verification_appointments repository if separate table exists

    @Transactional
    public void submitPhysicalDocuments(String uniqueCode, PhysicalSubmissionDto dto) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Recording physical submission for investor: {}", uniqueCode);

        // Check if submission already exists
        InvestorPhysicalSubmission existingSubmission = physicalSubmissionRepository
                .findByInvestorUniqueId(uniqueCode)
                .orElse(null);

        if (existingSubmission != null) {
            // Update existing submission
            existingSubmission.setPhysicalSubmission(dto.getPhysicalSubmission());
            existingSubmission.setCourierName(dto.getCourierName());
            if (dto.getDispatchDate() != null) {
                existingSubmission.setDispatchDate(LocalDate.parse(dto.getDispatchDate()));
            }
            existingSubmission.setAwbNumber(dto.getAwbNumber());
            physicalSubmissionRepository.save(existingSubmission);
        } else {
            // Create new submission
            InvestorPhysicalSubmission submission = InvestorPhysicalSubmission.builder()
                    .investorUniqueId(uniqueCode)
                    .physicalSubmission(dto.getPhysicalSubmission())
                    .courierName(dto.getCourierName())
                    .dispatchDate(dto.getDispatchDate() != null ? LocalDate.parse(dto.getDispatchDate()) : null)
                    .awbNumber(dto.getAwbNumber())
                    .build();
            physicalSubmissionRepository.save(submission);
        }

        log.info("Physical submission recorded successfully");
        
        // Send email notification if courier is selected
        if ("courier".equalsIgnoreCase(dto.getPhysicalSubmission()) && graphEmailService != null) {
            try {
                sendCourierDispatchEmail(investor, dto);
            } catch (Exception e) {
                log.error("Failed to send courier dispatch email for investor {}: {}", uniqueCode, e.getMessage(), e);
                // Don't fail the transaction if email fails
            }
        }
    }
    
    /**
     * Send courier dispatch email to service provider
     */
    private void sendCourierDispatchEmail(Investor investor, PhysicalSubmissionDto dto) {
        log.info("Sending courier dispatch email for investor: {}", investor.getUniqueCode());
        
        // Get authorized user for investor (via relationship)
        AuthorizedUser user = investor.getAuthorizedUser();
        if (user == null) {
            throw new RuntimeException("User not found for investor");
        }
        
        // Get intro investor temp to get broker and product details
        IntroInvestorTemp intro = introInvestorTempRepository.findByUniqueCodeDb(investor.getUniqueCode())
                .orElseThrow(() -> new RuntimeException("Intro investor not found"));
        
        // Get broker/service provider details
        MasterAccounts broker = masterAccountsRepository.findFirstBySsBrokerValue(intro.getSsBrokerValue())
                .orElseThrow(() -> new RuntimeException("Service provider not found"));
        
        if (broker.getEmailAddress1() == null || broker.getEmailAddress1().isBlank()) {
            throw new RuntimeException("Service provider email not found");
        }
        
        // Build investor full name (simple - no middle name in AuthorizedUser)
        String investorFullName = buildFullName(user);
        
        // Format dispatch date for email display (d/m/Y H:i format like Laravel)
        String dispatchDateFormatted = dto.getDispatchDate() != null
                ? LocalDate.parse(dto.getDispatchDate()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "";
        
        // Prepare email template variables
        Map<String, String> variables = new HashMap<>();
        variables.put("serviceProviderName", broker.getName() != null ? broker.getName() : "Service Provider");
        variables.put("investorName", investorFullName);
        variables.put("entityType", intro.getInvestorRegisterAs() != null ? intro.getInvestorRegisterAs() : "Individual");
        variables.put("courierName", dto.getCourierName() != null ? dto.getCourierName() : "");
        variables.put("awbNumber", dto.getAwbNumber() != null ? dto.getAwbNumber() : "");
        variables.put("dispatchDate", dispatchDateFormatted);
        
        // Load and process email template
        String emailContent = emailTemplateLoader.processTemplate("courier-dispatch.html", variables);
        
        // Send email
        String subject = String.format("Investor Documents Dispatched – AWB %s – %s", 
                dto.getAwbNumber(), investorFullName);
        
        boolean sent = graphEmailService.sendEmail(
                broker.getEmailAddress1(),
                subject,
                emailContent,
                null,
                null
        );
        
        if (sent) {
            log.info("✅ Courier dispatch email sent successfully to: {}", broker.getEmailAddress1());
        } else {
            log.error("❌ Failed to send courier dispatch email to: {}", broker.getEmailAddress1());
        }
    }
    
    /**
     * Build full name from authorized user (first + last only, no middle name in AuthorizedUser)
     */
    private String buildFullName(AuthorizedUser user) {
        String fullName = "";
        if (user.getFirstName() != null && !user.getFirstName().isBlank()) {
            fullName += user.getFirstName();
        }
        if (user.getLastName() != null && !user.getLastName().isBlank()) {
            if (!fullName.isEmpty()) fullName += " ";
            fullName += user.getLastName();
        }
        return fullName.isBlank() ? "Investor" : fullName.trim();
    }

    @Transactional
    public void scheduleVerificationAppointment(String uniqueCode, VerificationAppointmentDto dto) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Scheduling verification appointment for investor: {}", uniqueCode);

        // TODO: Save to verification_appointments table
        // VerificationAppointment appointment = VerificationAppointment.builder()
        // .investorId(investor.getId())
        // .appointmentDate(LocalDate.parse(dto.getAppointmentDate()))
        // .appointmentTime(dto.getAppointmentTime())
        // .verificationType(dto.getVerificationType())
        // .location(dto.getLocation())
        // .verifierName(dto.getVerifierName())
        // .meetingLink(dto.getMeetingLink())
        // .notes(dto.getNotes())
        // .status("scheduled")
        // .build();
        // verificationAppointmentRepository.save(appointment);

        // TODO: Send notification email/SMS

        log.info("Verification appointment scheduled successfully");
    }

    @Transactional
    public void updateAppointmentStatus(String uniqueCode, Long appointmentId, String status) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Updating appointment status for investor: {}, appointmentId: {}, status: {}",
                uniqueCode, appointmentId, status);

        // TODO: Update appointment status in database
        // VerificationAppointment appointment = verificationAppointmentRepository
        // .findByIdAndInvestorId(appointmentId, investor.getId())
        // .orElseThrow(() -> new RuntimeException("Appointment not found"));
        // appointment.setStatus(status);
        // verificationAppointmentRepository.save(appointment);

        log.info("Appointment status updated successfully");
    }

    @Transactional
    public void completeVerification(String uniqueCode, String verifiedBy) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Completing verification for investor: {}, verifiedBy: {}", uniqueCode, verifiedBy);

        // TODO: Update investor verification status
        // investor.setVerificationStatus("verified");
        // investor.setVerifiedBy(verifiedBy);
        // investor.setVerifiedAt(LocalDateTime.now());
        // investorRepository.save(investor);

        // TODO: Send verification completion notification

        log.info("Verification completed successfully");
    }

    /**
     * Loads verification UI state. Syncs from Dataverse first (Laravel inperson_verification_show parity).
     * Dataverse calls are outside a DB transaction; {@code save} on personal info runs in its own transaction.
     */
    public VerificationStatusDto getVerificationStatus(String uniqueCode) {
        Investor investor = getInvestorByUniqueCode(uniqueCode);

        log.info("Fetching verification status for investor: {}", uniqueCode);

        // Sync from Dataverse first (like Laravel - lines 1645-1683)
        try {
            syncVerificationStatusFromDataverse(uniqueCode, investor);
        } catch (Exception e) {
            log.warn("⚠️ Failed to sync verification status from Dataverse: {}. Using local data.", e.getMessage());
            // Continue with local data
        }

        // Get personal information for verification status
        UserPersonalInformation personalInfo = personalInformationRepository
                .findByInvestorUniqueId(uniqueCode)
                .orElse(null);

        // Get physical submission status
        InvestorPhysicalSubmission physicalSubmission = physicalSubmissionRepository
                .findByInvestorUniqueId(uniqueCode)
                .orElse(null);

        // Build physical submission info
        VerificationStatusDto.PhysicalSubmissionInfo submissionInfo = VerificationStatusDto.PhysicalSubmissionInfo
                .builder()
                .submitted(physicalSubmission != null)
                .submittedAt(physicalSubmission != null && physicalSubmission.getCreatedAt() != null
                        ? physicalSubmission.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        : null)
                .trackingNumber(physicalSubmission != null ? physicalSubmission.getAwbNumber() : null)
                .status(physicalSubmission != null ? "submitted" : "pending")
                .physicalSubmission(physicalSubmission != null ? physicalSubmission.getPhysicalSubmission() : null)
                .courierName(physicalSubmission != null ? physicalSubmission.getCourierName() : null)
                .dispatchDate(physicalSubmission != null && physicalSubmission.getDispatchDate() != null
                        ? physicalSubmission.getDispatchDate().toString()
                        : null)
                .build();

        // Build appointment info (placeholder - can be enhanced with dedicated appointment table)
        VerificationStatusDto.VerificationAppointmentInfo appointmentInfo = VerificationStatusDto.VerificationAppointmentInfo
                .builder()
                .scheduled(false) // TODO: Implement appointment tracking if needed
                .status("not_scheduled")
                .build();

        // Determine current status based on verification done flag
        String currentStatus = "pending";
        String verifiedBy = null;
        String verifiedAt = null;
        
        if (personalInfo != null && personalInfo.getSsVerificationDone() != null) {
            if ("1".equals(personalInfo.getSsVerificationDone())) {
                currentStatus = "completed";
                verifiedBy = personalInfo.getSsVerificationDoneBy();
                verifiedAt = personalInfo.getSsVerificationDateandTime() != null
                        ? personalInfo.getSsVerificationDateandTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        : null;
            } else if ("2".equals(personalInfo.getSsVerificationDone())) {
                currentStatus = "pending";
            }
        }

        // Determine next steps
        String nextSteps = switch (currentStatus) {
            case "completed" -> "Verification complete! Proceed to account opening.";
            case "in_progress" -> "Your verification is being processed. Please wait for confirmation.";
            default -> "Schedule an in-person verification appointment to complete your onboarding.";
        };

        return VerificationStatusDto.builder()
                .currentStatus(currentStatus)
                .physicalSubmission(submissionInfo)
                .appointment(appointmentInfo)
                .verifiedBy(verifiedBy)
                .verifiedAt(verifiedAt)
                .nextSteps(nextSteps)
                .build();
    }

    /**
     * Sync verification status from Dataverse on page load (aligns with Laravel).
     * Laravel: InnerPageController.php lines 1645-1683
     * 
     * Flow:
     * 1. Fetch from ss_investors (investor-level verification)
     * 2. Fetch from ss_investorproducts (product-level verification with details)
     * 3. Update local user_personal_information table
     */
    private void syncVerificationStatusFromDataverse(String uniqueCode, Investor investor) {
        log.info("🔄 Syncing verification status from Dataverse for: {}", uniqueCode);
        
        // Check if Dataverse service is available
        if (dynamicsCrmService == null) {
            log.warn("DynamicsCrmService not available - skipping Dataverse sync");
            return;
        }
        
        // Get intro_investor_temp for broker/product GUIDs
        IntroInvestorTemp introInvestor = introInvestorTempRepository
                .findByUniqueCodeDb(uniqueCode)
                .orElse(null);
        
        if (introInvestor == null) {
            log.warn("intro_investor_temp not found for uniqueCode: {}", uniqueCode);
            return;
        }
        
        String investorGuid = resolveSsInvestorGuid(investor, introInvestor);
        String brokerId = introInvestor.getSsBrokerValue();
        String productId = introInvestor.getSsProductValue();
        
        if (investorGuid == null || investorGuid.isBlank()) {
            log.warn("Missing ss_investor GUID (dv_investor_ss_id / intro_investorid) for Dataverse sync");
            return;
        }
        
        // First check ss_investors (like Laravel line 1646-1655)
        Map<String, Object> investorData = dynamicsCrmService.fetchInvestorByGuid(investorGuid);
        if (investorData != null) {
            log.info("📊 ss_investors verification: {}", investorData.get("ss_verificationdone"));
        }
        
        // Then check ss_investorproducts for detailed verification info (Laravel lines 1658-1673)
        Map<String, Object> productData = null;
        if (brokerId != null && !brokerId.isBlank() && productId != null && !productId.isBlank()) {
            productData = dynamicsCrmService.fetchInvestorProduct(investorGuid, brokerId, productId);
        } else {
            log.warn("Missing broker/product GUIDs — skipping ss_investorproducts; will fall back to ss_investors only");
        }
        
        Boolean verificationDone;
        String verifiedBy;
        String verifiedDateStr;
        
        if (productData != null && !productData.isEmpty()) {
            verificationDone = parseVerificationDone(productData.get("ss_verificationdone"));
            verifiedBy = productData.get("ss_verificationdoneby") != null
                    ? String.valueOf(productData.get("ss_verificationdoneby")) : null;
            verifiedDateStr = productData.get("ss_verificationdateandtime") != null
                    ? String.valueOf(productData.get("ss_verificationdateandtime")) : null;
        } else if (investorData != null) {
            // Laravel only updates local DB from product row; if missing, still align status from investor
            verificationDone = parseVerificationDone(investorData.get("ss_verificationdone"));
            verifiedBy = null;
            verifiedDateStr = null;
            log.info("Using ss_investors only for verification flag (no ss_investorproducts row)");
        } else {
            log.warn("No investor or product data from Dataverse — leaving local verification fields unchanged");
            return;
        }
        
        // Map boolean to status code (Laravel logic)
        final String status;
        if (verificationDone == null) {
            status = "0"; // Unknown
        } else if (verificationDone) {
            status = "1"; // Verified
        } else {
            status = "2"; // Not verified
        }
        
        final LocalDateTime verifiedDateTime = parseDataverseDateTime(verifiedDateStr);
        
        // Update local user_personal_information table (Laravel lines 1677-1683)
        personalInformationRepository.findByInvestorUniqueId(uniqueCode)
                .ifPresent(personalInfo -> {
                    personalInfo.setSsVerificationDone(status);
                    personalInfo.setSsVerificationDoneBy(verifiedBy);
                    personalInfo.setSsVerificationDateandTime(verifiedDateTime);
                    personalInformationRepository.save(personalInfo);
                    
                    log.info("✅ Synced verification status from Dataverse: status={}, by={}, date={}", 
                            status, verifiedBy, verifiedDateTime);
                });
    }

    private Investor getInvestorByUniqueCode(String uniqueCode) {
        return investorRepository.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new RuntimeException("Investor not found with unique code: " + uniqueCode));
    }

    private static final Pattern DATAVERSE_GUID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    /**
     * Laravel uses intro_investor_temp.intro_investorid for ss_investor OData keys; platform stores GUID in investor.dv_investor_ss_id.
     */
    private String resolveSsInvestorGuid(Investor investor, IntroInvestorTemp intro) {
        String fromInvestor = investor.getDvInvestorSsId();
        if (fromInvestor != null && DATAVERSE_GUID_PATTERN.matcher(fromInvestor.trim()).matches()) {
            return fromInvestor.trim();
        }
        String fromIntro = intro.getIntroInvestorId();
        if (fromIntro != null && DATAVERSE_GUID_PATTERN.matcher(fromIntro.trim()).matches()) {
            return fromIntro.trim();
        }
        if (fromInvestor != null && !fromInvestor.isBlank()) {
            return fromInvestor.trim();
        }
        return fromIntro != null ? fromIntro.trim() : null;
    }

    private Boolean parseVerificationDone(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Boolean b) {
            return b;
        }
        if (raw instanceof Number n) {
            int v = n.intValue();
            if (v == 1) {
                return true;
            }
            if (v == 0) {
                return false;
            }
            return null;
        }
        String s = String.valueOf(raw).trim().toLowerCase();
        if ("true".equals(s) || "1".equals(s) || "yes".equals(s)) {
            return true;
        }
        if ("false".equals(s) || "0".equals(s) || "no".equals(s)) {
            return false;
        }
        return null;
    }

    private LocalDateTime parseDataverseDateTime(String verifiedDateStr) {
        if (verifiedDateStr == null || verifiedDateStr.isBlank()) {
            return null;
        }
        String trimmed = verifiedDateStr.trim();
        try {
            return OffsetDateTime.parse(trimmed).toLocalDateTime();
        } catch (Exception ignored) {
            // continue
        }
        try {
            return LocalDateTime.parse(trimmed, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception ignored) {
            // continue
        }
        try {
            return LocalDateTime.parse(trimmed, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            log.warn("Failed to parse verification date: {}", verifiedDateStr);
            return null;
        }
    }
    
    /**
     * Generate HTML document checklist for physical submission using FreeMarker template
     * Matches Laravel's investor-document-checklist.blade.php format exactly
     */
    public String generateDocumentChecklistHtml(String uniqueCode) {
        log.info("Generating document checklist for investor: {}", uniqueCode);
        
        try {
            Investor investor = getInvestorByUniqueCode(uniqueCode);
            
            // Get authorized user via relationship
            AuthorizedUser user = investor.getAuthorizedUser();
            if (user == null) {
                throw new RuntimeException("User not found for investor");
            }
            
            // Get intro investor temp
            IntroInvestorTemp intro = introInvestorTempRepository.findByUniqueCodeDb(uniqueCode)
                    .orElseThrow(() -> new RuntimeException("Intro investor not found"));
            
            // Get personal information for address and middle name details
            UserPersonalInformation personalInfo = personalInformationRepository.findByInvestorUniqueId(uniqueCode)
                    .orElse(null);
            
            // Get broker details
            MasterAccounts broker = masterAccountsRepository.findFirstBySsBrokerValue(intro.getSsBrokerValue())
                    .orElseThrow(() -> new RuntimeException("Service provider not found"));
            
            // Get product details
            MasterProducts product = intro.getSsProductValue() != null
                    ? masterProductsRepository.findBySsProductId(intro.getSsProductValue()).orElse(null)
                    : null;
            
            // Get KYC documents
            List<KycDocuments> documents = kycDocumentsRepository.findByInvestorUniqueIdAndDeletedAtIsNull(uniqueCode);
            
            // Prepare data model for FreeMarker template
            Map<String, Object> model = new HashMap<>();
            
            // User details (use personalInfo for complete name including middle name)
            if (personalInfo != null) {
                model.put("userFirstName", personalInfo.getInvestorFirstName() != null ? personalInfo.getInvestorFirstName() : user.getFirstName());
                model.put("userMiddleName", personalInfo.getInvestorMiddleName() != null ? personalInfo.getInvestorMiddleName() : "");
                model.put("userLastName", personalInfo.getInvestorLastName() != null ? personalInfo.getInvestorLastName() : user.getLastName());
                model.put("userAddressLine1", personalInfo.getAddressLine1() != null ? personalInfo.getAddressLine1() : "");
                model.put("userAddressLine2", personalInfo.getAddressLine2() != null ? personalInfo.getAddressLine2() : "");
                model.put("userCity", personalInfo.getUserCity() != null ? personalInfo.getUserCity() : "");
            } else {
                model.put("userFirstName", user.getFirstName() != null ? user.getFirstName() : "");
                model.put("userMiddleName", "");
                model.put("userLastName", user.getLastName() != null ? user.getLastName() : "");
                model.put("userAddressLine1", "");
                model.put("userAddressLine2", "");
                model.put("userCity", "");
            }
            
            model.put("userEmail", user.getEmailId() != null ? user.getEmailId() : "");
            model.put("userMobile", user.getMobilePhone() != null ? user.getMobilePhone() : "");
            
            // Service provider details
            model.put("serviceProviderName", broker.getName() != null ? broker.getName() : "");
            model.put("serviceProviderEmail", broker.getEmailAddress1() != null ? broker.getEmailAddress1() : "");
            model.put("serviceProviderPhone", broker.getTelephone1() != null ? broker.getTelephone1() : "");
            
            // Product details
            model.put("productName", product != null && product.getSsName() != null ? product.getSsName() : "");
            
            // Documents list - convert to map for FreeMarker
            List<Map<String, Object>> documentsList = documents.stream().map(doc -> {
                Map<String, Object> docMap = new HashMap<>();
                docMap.put("documentType", doc.getDocumentType() != null ? doc.getDocumentType() : "");
                docMap.put("uploadType", doc.getUploadType() != null ? doc.getUploadType() : 1);
                return docMap;
            }).collect(Collectors.toList());
            model.put("documents", documentsList);
            
            // Process FreeMarker template
            Configuration freeMarkerConfig = freeMarkerConfigurer.getConfiguration();
            Template template = freeMarkerConfig.getTemplate("investor-document-checklist.ftl");
            
            StringWriter stringWriter = new StringWriter();
            template.process(model, stringWriter);
            
            String html = stringWriter.toString();
            log.info("✅ Document checklist generated successfully for investor: {}", uniqueCode);
            return html;
            
        } catch (Exception e) {
            log.error("❌ Error generating document checklist: {}", e.getMessage(), e);
            return generateErrorHtml(e.getMessage());
        }
    }
    
    /**
     * Generate error HTML when checklist generation fails
     */
    private String generateErrorHtml(String errorMessage) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Error</title>" +
                "<style>body{font-family:Arial,sans-serif;margin:50px;text-align:center;}" +
                "h1{color:#be1717;}p{color:#666;}</style></head><body>" +
                "<h1>Error Generating Checklist</h1>" +
                "<p>" + errorMessage + "</p>" +
                "<p>Please contact support@facilonservices.com for assistance.</p>" +
                "</body></html>";
    }
}
