package com.facilon.app.module.client.service;

import com.facilon.app.integration.dynamics.DynamicsCrmService;
import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.module.client.model.*;
import com.facilon.app.module.client.repository.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfGenerationService {

        private final InvestorRepository investorRepository;
        private final UserPersonalInformationRepository personalInfoRepository;
        private final UserPassportDetailsRepository passportDetailsRepository;
        private final InvestorBankDetailsRepository bankDetailsRepository;
        private final InvestorContactDetailsRepository contactDetailsRepository;
        private final InvestorTaxInformationRepository taxInformationRepository;
        private final InvestorResidentialStatusRepository residentialStatusRepository;
        private final InvestorNominationRepository nominationRepository;
        private final InvestorRiskProfileRepository riskProfileRepository;

        @Autowired(required = false)
        private FreeMarkerConfigurer freeMarkerConfigurer;

        // Fresh Dataverse lookups during PDF render (Laravel AdminController::pdf_view L541).
        // Both are optional so PDF generation still works when Dataverse / intro-temp data
        // is unavailable (e.g. unit tests, self-registered investors without a broker).
        @Autowired(required = false)
        private DynamicsCrmService dynamicsCrmService;
        @Autowired(required = false)
        private IntroInvestorTempRepository introInvestorTempRepository;

        private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
        private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
        private static final Font LABEL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.DARK_GRAY);
        private static final Font VALUE_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);

        /**
         * Generate investor information PDF
         * Aligned with Laravel: /user-pdf-view/{id}
         */
        public byte[] generateInvestorPdf(Long investorId) {
                try {
                        Investor investor = investorRepository.findById(investorId)
                                        .orElseThrow(() -> new RuntimeException("Investor not found: " + investorId));

                        UserPersonalInformation personalInfo = personalInfoRepository
                                        .findByInvestorUniqueId(investor.getUniqueCode())
                                        .orElse(null);

                        UserPassportDetails passportDetails = passportDetailsRepository
                                        .findByInvestorUniqueCode(investor.getUniqueCode())
                                        .orElse(null);

                        AuthorizedUser user = investor.getAuthorizedUser();
                        String fullName = "N/A";
                        String email = "N/A";
                        String mobile = "N/A";

                        if (user != null) {
                                fullName = (user.getFirstName() != null ? user.getFirstName() : "") + " "
                                                + (user.getLastName() != null ? user.getLastName() : "");
                                fullName = fullName.trim();
                                email = user.getEmailId() != null ? user.getEmailId() : "N/A";
                                mobile = user.getMobilePhone() != null ? user.getMobilePhone() : "N/A";
                        } else if (personalInfo != null) {
                                fullName = (personalInfo.getInvestorFirstName() != null
                                                ? personalInfo.getInvestorFirstName()
                                                : "") + " " +
                                                (personalInfo.getInvestorLastName() != null
                                                                ? personalInfo.getInvestorLastName()
                                                                : "");
                                fullName = fullName.trim();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
                        PdfWriter.getInstance(document, baos);

                        document.open();

                        // Add title
                        Paragraph title = new Paragraph("Investor Information", TITLE_FONT);
                        title.setAlignment(Element.ALIGN_CENTER);
                        title.setSpacingAfter(20);
                        document.add(title);

                        // Add investor details
                        document.add(createSection("Basic Information"));
                        document.add(createInfoTable(new String[][] {
                                        { "Unique Code", investor.getUniqueCode() != null ? investor.getUniqueCode()
                                                        : "N/A" },
                                        { "Full Name", fullName },
                                        { "Email", email },
                                        { "Mobile Phone", mobile },
                                        { "Date of Birth",
                                                        personalInfo != null && personalInfo.getUserDob() != null
                                                                        ? personalInfo.getUserDob().format(
                                                                                        DateTimeFormatter.ofPattern(
                                                                                                        "dd-MMM-yyyy"))
                                                                        : "N/A" },
                                        { "Gender", personalInfo != null && personalInfo.getInvestorGender() != null
                                                        ? personalInfo.getInvestorGender()
                                                        : "N/A" },
                                        { "Investor Type",
                                                        investor.getInvestorType() != null ? investor.getInvestorType()
                                                                        : "N/A" },
                                        { "Verify Status",
                                                        investor.getVerifyStatus() != null
                                                                        ? investor.getVerifyStatus().toString()
                                                                        : "N/A" }
                        }));

                        // Add personal information if available
                        if (personalInfo != null) {
                                document.add(createSection("Personal Details"));
                                document.add(createInfoTable(new String[][] {
                                                { "Address Line 1",
                                                                personalInfo.getAddressLine1() != null
                                                                                ? personalInfo.getAddressLine1()
                                                                                : "N/A" },
                                                { "Address Line 2",
                                                                personalInfo.getAddressLine2() != null
                                                                                ? personalInfo.getAddressLine2()
                                                                                : "N/A" },
                                                { "City", personalInfo.getUserCity() != null
                                                                ? personalInfo.getUserCity()
                                                                : "N/A" },
                                                { "State", personalInfo.getUserState() != null
                                                                ? personalInfo.getUserState()
                                                                : "N/A" },
                                                { "Postal Code", personalInfo.getUserZipCode() != null
                                                                ? personalInfo.getUserZipCode()
                                                                : "N/A" },
                                                { "Nationality",
                                                                personalInfo.getUserCountry() != null
                                                                                ? personalInfo.getUserCountry()
                                                                                : "N/A" },
                                                { "PAN Number", personalInfo.getUserPanNo() != null
                                                                ? personalInfo.getUserPanNo()
                                                                : "N/A" }
                                }));
                        }

                        // Add Marital Status & Maiden Name Section
                        if (personalInfo != null && personalInfo.getMaritalStatus() != null) {
                                document.add(createSection("Marital Status & Maiden Name"));
                                java.util.List<String[]> maritalData = new java.util.ArrayList<>();
                                maritalData.add(new String[] { "Marital Status", personalInfo.getMaritalStatus() });

                                // Add maiden name if any field is present
                                String maidenName = buildFullName(
                                                personalInfo.getMaidenTitle(),
                                                personalInfo.getMaidenName(),
                                                personalInfo.getMaidenMiddleName(),
                                                personalInfo.getMaidenLastName());
                                if (!maidenName.equals("N/A")) {
                                        maritalData.add(new String[] { "Maiden Name", maidenName });
                                }
                                document.add(createInfoTable(maritalData.toArray(new String[0][])));
                        }

                        // Add Birth Details Section
                        if (personalInfo != null && (personalInfo.getCityOfDob() != null
                                        || personalInfo.getCountryDob() != null)) {
                                document.add(createSection("Birth Details"));
                                document.add(createInfoTable(new String[][] {
                                                { "City of Birth",
                                                                personalInfo.getCityOfDob() != null
                                                                                ? personalInfo.getCityOfDob()
                                                                                : "N/A" },
                                                { "Country of Birth",
                                                                personalInfo.getCountryDob() != null
                                                                                ? personalInfo.getCountryDob()
                                                                                : "N/A" }
                                }));
                        }

                        // Add Father's Details Section
                        if (personalInfo != null) {
                                String fatherName = buildFullName(
                                                personalInfo.getFatherNameTitle(),
                                                personalInfo.getFathersFirstName(),
                                                personalInfo.getFathersMiddleName(),
                                                personalInfo.getFathersLastName());
                                if (!fatherName.equals("N/A")) {
                                        document.add(createSection("Father's Details"));
                                        document.add(createInfoTable(new String[][] {
                                                        { "Father's Full Name", fatherName }
                                        }));
                                }
                        }

                        // Add Mother's Details Section
                        if (personalInfo != null) {
                                String motherName = buildFullName(
                                                personalInfo.getMotherNameTitle(),
                                                personalInfo.getMotherFirstName(),
                                                personalInfo.getMotherMiddleName(),
                                                personalInfo.getMotherLastName());
                                if (!motherName.equals("N/A")) {
                                        document.add(createSection("Mother's Details"));
                                        document.add(createInfoTable(new String[][] {
                                                        { "Mother's Full Name", motherName }
                                        }));
                                }
                        }

                        // Add Spouse Details Section (only if married)
                        if (personalInfo != null && personalInfo.getMaritalStatus() != null &&
                                        !personalInfo.getMaritalStatus().equalsIgnoreCase("Single")) {
                                String spouseName = buildFullName(
                                                personalInfo.getSpouseNameTitle(),
                                                personalInfo.getSpouseName(),
                                                personalInfo.getSpouseMiddleName(),
                                                personalInfo.getSpouseLastName());
                                if (!spouseName.equals("N/A")) {
                                        document.add(createSection("Spouse Details"));
                                        java.util.List<String[]> spouseData = new java.util.ArrayList<>();
                                        spouseData.add(new String[] { "Spouse Name", spouseName });
                                        if (personalInfo.getSpouseMaidenName() != null) {
                                                spouseData.add(new String[] { "Spouse Maiden Name",
                                                                personalInfo.getSpouseMaidenName() });
                                        }
                                        document.add(createInfoTable(spouseData.toArray(new String[0][])));
                                }
                        }

                        // Add Passport Information Section
                        if (passportDetails != null) {
                                document.add(createSection("Passport Information"));
                                java.util.List<String[]> passportData = new java.util.ArrayList<>();

                                if (passportDetails.getPassportNumber() != null) {
                                        passportData.add(new String[] { "Passport Number",
                                                        passportDetails.getPassportNumber() });
                                }
                                if (passportDetails.getPassportNationality() != null) {
                                        passportData.add(new String[] { "Nationality",
                                                        passportDetails.getPassportNationality() });
                                }
                                if (passportDetails.getPassportIssueDate() != null) {
                                        passportData.add(new String[] { "Issue Date",
                                                        passportDetails.getPassportIssueDate().format(
                                                                        DateTimeFormatter.ofPattern("dd-MMM-yyyy")) });
                                }
                                if (passportDetails.getPassportExpiryDate() != null) {
                                        passportData.add(new String[] { "Expiry Date",
                                                        passportDetails.getPassportExpiryDate().format(
                                                                        DateTimeFormatter.ofPattern("dd-MMM-yyyy")) });
                                }
                                if (passportDetails.getPassportPlaceOfIssue() != null) {
                                        passportData.add(new String[] { "Place of Issue",
                                                        passportDetails.getPassportPlaceOfIssue() });
                                }
                                if (passportDetails.getPassportCountryOfIssue() != null) {
                                        passportData.add(new String[] { "Country of Issue",
                                                        passportDetails.getPassportCountryOfIssue() });
                                }
                                if (passportDetails.getPassportDateNonResident() != null) {
                                        passportData.add(new String[] { "Date Became Non-Resident",
                                                        passportDetails.getPassportDateNonResident().format(
                                                                        DateTimeFormatter.ofPattern("dd-MMM-yyyy")) });
                                }
                                if (passportDetails.getPassportNoYearsAbroad() != null) {
                                        passportData.add(new String[] { "Number of Years Abroad",
                                                        passportDetails.getPassportNoYearsAbroad().toString() });
                                }

                                if (!passportData.isEmpty()) {
                                        document.add(createInfoTable(passportData.toArray(new String[0][])));
                                }
                        }

                        // Add footer
                        Paragraph footer = new Paragraph("\n\nGenerated on: " +
                                        java.time.LocalDateTime.now()
                                                        .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss")),
                                        new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY));
                        footer.setAlignment(Element.ALIGN_CENTER);
                        document.add(footer);

                        document.close();

                        log.info("PDF generated successfully for investor: {}", investorId);
                        return baos.toByteArray();
                } catch (DocumentException e) {
                        log.error("Error generating PDF for investor: {}", investorId, e);
                        throw new RuntimeException("Failed to generate PDF", e);
                }
        }

        /**
         * Generate investor PDF by current user ID
         */
        public byte[] generateInvestorPdfByUserId(Long userId) {
                Investor investor = investorRepository.findByAuthorizedUser_Id(userId)
                                .orElseThrow(() -> new RuntimeException("Investor not found for user: " + userId));
                return generateInvestorPdf(investor.getId());
        }

        /**
         * Generate investor print preview HTML by current user ID
         */
        public String generatePrintPreviewHtmlByUserId(Long userId) {
                Investor investor = investorRepository.findByAuthorizedUser_Id(userId)
                                .orElseThrow(() -> new RuntimeException("Investor not found for user: " + userId));
                return generatePrintPreviewHtml(investor.getId());
        }

        /**
         * Generate investor information print preview HTML
         * Aligned with Laravel: /user-information-print-preview/{id}
         */
        public String generatePrintPreviewHtml(Long investorId) {
                Investor investor = investorRepository.findById(investorId)
                                .orElseThrow(() -> new RuntimeException("Investor not found: " + investorId));

                UserPersonalInformation personalInfo = personalInfoRepository
                                .findByInvestorUniqueId(investor.getUniqueCode())
                                .orElse(null);

                UserPassportDetails passportDetails = passportDetailsRepository
                                .findByInvestorUniqueCode(investor.getUniqueCode())
                                .orElse(null);

                InvestorBankDetails bankDetails = bankDetailsRepository
                                .findByInvestorUniqueIdAndIsPrimaryTrue(investor.getUniqueCode())
                                .orElse(bankDetailsRepository.findFirstByInvestorUniqueId(investor.getUniqueCode()).orElse(null));
                InvestorContactDetails contactDetails = contactDetailsRepository
                                .findByInvestorUniqueId(investor.getUniqueCode()).orElse(null);
                InvestorTaxInformation taxInfo = taxInformationRepository
                                .findByInvestorUniqueId(investor.getUniqueCode()).orElse(null);
                InvestorResidentialStatus residentialStatus = residentialStatusRepository
                                .findByInvestorUniqueId(investor.getUniqueCode()).orElse(null);
                java.util.List<InvestorNomination> nominations = nominationRepository
                                .findByInvestorUniqueId(investor.getUniqueCode());
                InvestorRiskProfile riskProfile = riskProfileRepository
                                .findByInvestorUniqueId(investor.getUniqueCode()).orElse(null);

                AuthorizedUser user = investor.getAuthorizedUser();
                String fullName = "N/A";
                String email = "N/A";
                String mobile = "N/A";

                if (user != null) {
                        fullName = (user.getFirstName() != null ? user.getFirstName() : "") + " "
                                        + (user.getLastName() != null ? user.getLastName() : "");
                        fullName = fullName.trim();
                        email = user.getEmailId() != null ? user.getEmailId() : "N/A";
                        mobile = user.getMobilePhone() != null ? user.getMobilePhone() : "N/A";
                } else if (personalInfo != null) {
                        fullName = (personalInfo.getInvestorFirstName() != null ? personalInfo.getInvestorFirstName()
                                        : "") + " " +
                                        (personalInfo.getInvestorLastName() != null ? personalInfo.getInvestorLastName()
                                                        : "");
                        fullName = fullName.trim();
                }

                StringBuilder html = new StringBuilder();
                html.append("<!DOCTYPE html><html><head>");
                html.append("<meta charset='UTF-8'>");
                html.append("<title>Investor Information - ").append(fullName).append("</title>");
                html.append("<script src='https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js'></script>");
                html.append("<style>");
                html.append("body { font-family: Arial, sans-serif; margin: 40px; padding-top: 80px; }");
                html.append("h1 { color: #333; border-bottom: 2px solid #007bff; padding-bottom: 10px; }");
                html.append("h2 { color: #555; margin-top: 30px; border-bottom: 1px solid #ddd; padding-bottom: 5px; }");
                html.append("table { width: 100%; border-collapse: collapse; margin-top: 15px; }");
                html.append("td { padding: 10px; border: 1px solid #ddd; }");
                html.append("td:first-child { font-weight: bold; background-color: #f8f9fa; width: 30%; }");
                html.append(".footer { margin-top: 40px; text-align: center; color: #888; font-size: 12px; }");
                html.append(".button-container { position: fixed; top: 20px; right: 20px; z-index: 1000; background: rgba(255,255,255,0.97); padding: 18px 20px; border-radius: 14px; box-shadow: 0 10px 30px rgba(0,0,0,0.18); display: flex; flex-direction: column; gap: 14px; }");
                html.append(".btn { background: linear-gradient(135deg, #be1717 0%, #a01515 100%); color: white; border: none; padding: 12px 26px; border-radius: 10px; font-size: 15px; font-weight: 600; cursor: pointer; text-decoration: none; display: inline-block; text-align: center; min-width: 190px; }");
                html.append(".btn:hover { transform: translateY(-4px); box-shadow: 0 10px 25px rgba(190,23,23,0.45); }");
                html.append(".btn:disabled { opacity: 0.6; cursor: not-allowed; }");
                html.append("@media print { .button-container { display: none; } body { padding-top: 20px; margin: 20px; } }");
                html.append("</style></head><body>");
                
                // Add button container at the top
                html.append("<div class='button-container'>");
                html.append("<button class='btn' onclick='downloadPDF()'>Download PDF</button>");
                html.append("<button class='btn' onclick='submitProfile()'>Submit Profile</button>");
                html.append("</div>");
                
                html.append("<div id='content'>");

                // Title
                html.append("<h1>Investor Information</h1>");

                // Basic Information
                html.append("<h2>Basic Information</h2>");
                html.append("<table>");
                html.append("<tr><td>Unique Code</td><td>")
                                .append(investor.getUniqueCode() != null ? investor.getUniqueCode() : "N/A")
                                .append("</td></tr>");
                html.append("<tr><td>Full Name</td><td>").append(fullName).append("</td></tr>");
                html.append("<tr><td>Email</td><td>").append(email).append("</td></tr>");
                html.append("<tr><td>Mobile Phone</td><td>").append(mobile).append("</td></tr>");
                html.append("<tr><td>Date of Birth</td><td>").append(
                                personalInfo != null && personalInfo.getUserDob() != null
                                                ? personalInfo.getUserDob()
                                                                .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
                                                : "N/A")
                                .append("</td></tr>");
                html.append("<tr><td>Gender</td><td>")
                                .append(personalInfo != null && personalInfo.getInvestorGender() != null
                                                ? personalInfo.getInvestorGender()
                                                : "N/A")
                                .append("</td></tr>");
                html.append("<tr><td>Investor Type</td><td>")
                                .append(investor.getInvestorType() != null ? investor.getInvestorType() : "N/A")
                                .append("</td></tr>");
                html.append("<tr><td>Verify Status</td><td>")
                                .append(investor.getVerifyStatus() != null ? investor.getVerifyStatus().toString()
                                                : "N/A")
                                .append("</td></tr>");
                html.append("</table>");

                // Personal Details
                if (personalInfo != null) {
                        html.append("<h2>Personal Details</h2>");
                        html.append("<table>");
                        String fullNameWithTitle = buildFullName(
                                        personalInfo.getTitle() != null ? personalInfo.getTitle().toString() : null,
                                        personalInfo.getInvestorFirstName(),
                                        personalInfo.getInvestorMiddleName(),
                                        personalInfo.getInvestorLastName());
                        if (!"N/A".equals(fullNameWithTitle)) {
                                html.append("<tr><td>Name</td><td>").append(escapeHtml(fullNameWithTitle)).append("</td></tr>");
                        }
                        appendRowIfPresent(html, "Gender", personalInfo.getInvestorGender());
                        appendRowIfPresent(html, "Citizenship", personalInfo.getCitizenship());
                        appendRowIfPresent(html, "Address Line 1", personalInfo.getAddressLine1());
                        appendRowIfPresent(html, "Address Line 2", personalInfo.getAddressLine2());
                        appendRowIfPresent(html, "Address Line 3", personalInfo.getAddressLine3());
                        appendRowIfPresent(html, "City", personalInfo.getUserCity());
                        appendRowIfPresent(html, "State", personalInfo.getUserState());
                        appendRowIfPresent(html, "Postal/Zip Code", personalInfo.getUserZipCode());
                        appendRowIfPresent(html, "Country", personalInfo.getUserCountry());
                        appendRowIfPresent(html, "PAN Number", personalInfo.getUserPanNo());
                        html.append("</table>");
                }

                // Marital Status & Maiden Name Section
                if (personalInfo != null && personalInfo.getMaritalStatus() != null) {
                        html.append("<h2>Marital Status & Maiden Name</h2>");
                        html.append("<table>");
                        html.append("<tr><td>Marital Status</td><td>").append(personalInfo.getMaritalStatus())
                                        .append("</td></tr>");

                        String maidenName = buildFullName(
                                        personalInfo.getMaidenTitle(),
                                        personalInfo.getMaidenName(),
                                        personalInfo.getMaidenMiddleName(),
                                        personalInfo.getMaidenLastName());
                        if (!maidenName.equals("N/A")) {
                                html.append("<tr><td>Maiden Name</td><td>").append(maidenName).append("</td></tr>");
                        }
                        html.append("</table>");
                }

                // Birth Details Section
                if (personalInfo != null
                                && (personalInfo.getCityOfDob() != null || personalInfo.getCountryDob() != null)) {
                        html.append("<h2>Birth Details</h2>");
                        html.append("<table>");
                        html.append("<tr><td>City of Birth</td><td>")
                                        .append(personalInfo.getCityOfDob() != null ? personalInfo.getCityOfDob()
                                                        : "N/A")
                                        .append("</td></tr>");
                        html.append("<tr><td>Country of Birth</td><td>")
                                        .append(personalInfo.getCountryDob() != null ? personalInfo.getCountryDob()
                                                        : "N/A")
                                        .append("</td></tr>");
                        html.append("</table>");
                }

                // Father's Details Section
                if (personalInfo != null) {
                        String fatherName = buildFullName(
                                        personalInfo.getFatherNameTitle(),
                                        personalInfo.getFathersFirstName(),
                                        personalInfo.getFathersMiddleName(),
                                        personalInfo.getFathersLastName());
                        if (!fatherName.equals("N/A")) {
                                html.append("<h2>Father's Details</h2>");
                                html.append("<table>");
                                html.append("<tr><td>Father's Full Name</td><td>").append(fatherName)
                                                .append("</td></tr>");
                                html.append("</table>");
                        }
                }

                // Mother's Details Section
                if (personalInfo != null) {
                        String motherName = buildFullName(
                                        personalInfo.getMotherNameTitle(),
                                        personalInfo.getMotherFirstName(),
                                        personalInfo.getMotherMiddleName(),
                                        personalInfo.getMotherLastName());
                        if (!motherName.equals("N/A")) {
                                html.append("<h2>Mother's Details</h2>");
                                html.append("<table>");
                                html.append("<tr><td>Mother's Full Name</td><td>").append(motherName)
                                                .append("</td></tr>");
                                html.append("</table>");
                        }
                }

                // Spouse Details Section
                if (personalInfo != null && personalInfo.getMaritalStatus() != null &&
                                !personalInfo.getMaritalStatus().equalsIgnoreCase("Single")) {
                        String spouseName = buildFullName(
                                        personalInfo.getSpouseNameTitle(),
                                        personalInfo.getSpouseName(),
                                        personalInfo.getSpouseMiddleName(),
                                        personalInfo.getSpouseLastName());
                        if (!spouseName.equals("N/A")) {
                                html.append("<h2>Spouse Details</h2>");
                                html.append("<table>");
                                html.append("<tr><td>Spouse Name</td><td>").append(spouseName).append("</td></tr>");
                                if (personalInfo.getSpouseMaidenName() != null) {
                                        html.append("<tr><td>Spouse Maiden Name</td><td>")
                                                        .append(personalInfo.getSpouseMaidenName())
                                                        .append("</td></tr>");
                                }
                                html.append("</table>");
                        }
                }

                // Passport Information Section
                if (passportDetails != null) {
                        html.append("<h2>Passport Information</h2>");
                        html.append("<table>");

                        if (passportDetails.getPassportNumber() != null) {
                                html.append("<tr><td>Passport Number</td><td>")
                                                .append(passportDetails.getPassportNumber()).append("</td></tr>");
                        }
                        if (passportDetails.getPassportNationality() != null) {
                                html.append("<tr><td>Nationality</td><td>")
                                                .append(passportDetails.getPassportNationality()).append("</td></tr>");
                        }
                        if (passportDetails.getPassportIssueDate() != null) {
                                html.append("<tr><td>Issue Date</td><td>")
                                                .append(passportDetails.getPassportIssueDate()
                                                                .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")))
                                                .append("</td></tr>");
                        }
                        if (passportDetails.getPassportExpiryDate() != null) {
                                html.append("<tr><td>Expiry Date</td><td>")
                                                .append(passportDetails.getPassportExpiryDate()
                                                                .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")))
                                                .append("</td></tr>");
                        }
                        if (passportDetails.getPassportPlaceOfIssue() != null) {
                                html.append("<tr><td>Place of Issue</td><td>")
                                                .append(passportDetails.getPassportPlaceOfIssue()).append("</td></tr>");
                        }
                        if (passportDetails.getPassportCountryOfIssue() != null) {
                                html.append("<tr><td>Country of Issue</td><td>")
                                                .append(passportDetails.getPassportCountryOfIssue())
                                                .append("</td></tr>");
                        }
                        if (passportDetails.getPassportDateNonResident() != null) {
                                html.append("<tr><td>Date Became Non-Resident</td><td>")
                                                .append(passportDetails.getPassportDateNonResident()
                                                                .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")))
                                                .append("</td></tr>");
                        }
                        if (passportDetails.getPassportNoYearsAbroad() != null) {
                                html.append("<tr><td>Number of Years Abroad</td><td>")
                                                .append(passportDetails.getPassportNoYearsAbroad().toString())
                                                .append("</td></tr>");
                        }

                        html.append("</table>");
                }

                // Bank Details
                if (bankDetails != null) {
                        html.append("<h2>Bank Details</h2>");
                        html.append("<table>");
                        appendRowIfPresent(html, "Account Type", bankDetails.getSettlementAccountType());
                        appendRowIfPresent(html, "Bank Name", bankDetails.getBankName());
                        appendRowIfPresent(html, "Branch Name", bankDetails.getBranchName());
                        appendRowIfPresent(html, "Bank Address", bankDetails.getBankAddress());
                        appendRowIfPresent(html, "Account Holder Name", bankDetails.getAccountHolderName());
                        appendRowIfPresent(html, "Account Number", bankDetails.getAccountNumber());
                        appendRowIfPresent(html, "IFSC Code", bankDetails.getIfscCode());
                        appendRowIfPresent(html, "SWIFT Code", bankDetails.getSwiftCode());
                        appendRowIfPresent(html, "RBI Approval", bankDetails.getRbiApproval());
                        appendRowIfPresent(html, "RBI Approval Order Number", bankDetails.getRbiApprovalOrderNumber());
                        appendRowIfPresent(html, "RBI Approval Date", bankDetails.getRbiApprovalDate());
                        html.append("</table>");
                }

                // Residential Status
                if (residentialStatus != null) {
                        html.append("<h2>Residential Status</h2>");
                        html.append("<table>");
                        appendRowIfPresent(html, "Residential Status", residentialStatus.getResidentialStatus());
                        appendRowIfPresent(html, "Residence Address", residentialStatus.getResidenceAddress());
                        appendRowIfPresent(html, "Residence City", residentialStatus.getResidenceCity());
                        appendRowIfPresent(html, "Residence State", residentialStatus.getResidenceState());
                        appendRowIfPresent(html, "Residence Postal Code", residentialStatus.getResidencePostalCode());
                        appendRowIfPresent(html, "Residence Phone", residentialStatus.getResidencePhone());
                        appendRowIfPresent(html, "Residence Since", residentialStatus.getResidenceSince());
                        appendRowIfPresent(html, "OCI Card No", residentialStatus.getUserOciCardNo());
                        appendRowIfPresent(html, "OCI Issue Date", residentialStatus.getUserOciIssueDate());
                        appendRowIfPresent(html, "OCI Valid Upto", residentialStatus.getUserOciValidUpto());
                        appendRowIfPresent(html, "Type of Proof", residentialStatus.getUserTypeOfProof());
                        appendRowIfPresent(html, "Visa Type", residentialStatus.getUserVisaType());
                        appendRowIfPresent(html, "Visa Number", residentialStatus.getUserVisaNumber());
                        appendRowIfPresent(html, "Visa Issuer Date", residentialStatus.getUserVisaIssuerDate());
                        appendRowIfPresent(html, "Visa Expiry Date", residentialStatus.getUserVisaExpiryDate());
                        appendRowIfPresent(html, "Politically Exposed", residentialStatus.getIsPoliticallyExposed() != null ? residentialStatus.getIsPoliticallyExposed().toString() : null);
                        html.append("</table>");
                }

                // Tax Information
                if (taxInfo != null) {
                        html.append("<h2>Tax Information</h2>");
                        html.append("<table>");
                        appendRowIfPresent(html, "PAN Number", taxInfo.getPanNumber());
                        appendRowIfPresent(html, "Tax Country", taxInfo.getTaxCountry() != null ? taxInfo.getTaxCountry().toString() : null);
                        appendRowIfPresent(html, "Tax Identification Number", taxInfo.getTaxIdentificationNumber());
                        appendRowIfPresent(html, "Tax Identification Number Type", taxInfo.getTaxIdentificationNumberType());
                        appendRowIfPresent(html, "Tax Residency Status", taxInfo.getTaxResidencyStatus());
                        appendRowIfPresent(html, "GST Number", taxInfo.getGstNumber());
                        appendRowIfPresent(html, "Income Source", taxInfo.getIncomeSource());
                        appendRowIfPresent(html, "Annual Income", taxInfo.getAnnualIncome());
                        appendRowIfPresent(html, "FATCA Status", taxInfo.getFatcaStatus());
                        appendRowIfPresent(html, "CRS Declaration", taxInfo.getCrsDeclaration());
                        appendRowIfPresent(html, "US Citizen", taxInfo.getUsCitizen() != null ? taxInfo.getUsCitizen().toString() : null);
                        appendRowIfPresent(html, "US Resident", taxInfo.getUsResident() != null ? taxInfo.getUsResident().toString() : null);
                        appendRowIfPresent(html, "Tax Residency Certificate No", taxInfo.getTaxResidencyCertificateNo());
                        appendRowIfPresent(html, "Tax Residency Certificate Date", taxInfo.getTaxResidencyCertificateDate());
                        html.append("</table>");
                }

                // Contact Details - Residential
                if (contactDetails != null) {
                        html.append("<h2>Contact Details - Residential</h2>");
                        html.append("<table>");
                        appendRowIfPresent(html, "Email", contactDetails.getEmailAddress());
                        appendRowIfPresent(html, "Mobile Number", contactDetails.getMobileNumber());
                        appendRowIfPresent(html, "Alternate Mobile", contactDetails.getAlternateMobile());
                        appendRowIfPresent(html, "WhatsApp Number", contactDetails.getWhatsappNumber());
                        appendRowIfPresent(html, "Landline Number", contactDetails.getLandlineNumber());
                        appendRowIfPresent(html, "Address Line 1", contactDetails.getAddressLine1());
                        appendRowIfPresent(html, "Address Line 2", contactDetails.getAddressLine2());
                        appendRowIfPresent(html, "Address Line 3", contactDetails.getAddressLine3());
                        appendRowIfPresent(html, "City", contactDetails.getUserCity());
                        appendRowIfPresent(html, "State", contactDetails.getUserState());
                        appendRowIfPresent(html, "Country", contactDetails.getUserCountry());
                        appendRowIfPresent(html, "Postal/Zip Code", contactDetails.getUserZipCode());
                        appendRowIfPresent(html, "Preferred Contact Method", contactDetails.getPreferredContactMethod());
                        appendRowIfPresent(html, "Preferred Contact Time", contactDetails.getPreferredContactTime());
                        html.append("</table>");
                        if (contactDetails.getCorrAddressLine1() != null || contactDetails.getCorrAddressLine2() != null
                                        || contactDetails.getCorrUserCity() != null) {
                                html.append("<h2>Contact Details - Correspondence</h2>");
                                html.append("<table>");
                                appendRowIfPresent(html, "Address Line 1", contactDetails.getCorrAddressLine1());
                                appendRowIfPresent(html, "Address Line 2", contactDetails.getCorrAddressLine2());
                                appendRowIfPresent(html, "Address Line 3", contactDetails.getCorrAddressLine3());
                                appendRowIfPresent(html, "City", contactDetails.getCorrUserCity());
                                appendRowIfPresent(html, "State", contactDetails.getCorrUserState());
                                appendRowIfPresent(html, "Country", contactDetails.getCorrUserCountry());
                                appendRowIfPresent(html, "Postal/Zip Code", contactDetails.getCorrUserZipCode());
                                html.append("</table>");
                        }
                }

                // Nomination
                if (nominations != null && !nominations.isEmpty()) {
                        html.append("<h2>Nomination</h2>");
                        int idx = 1;
                        for (InvestorNomination nom : nominations) {
                                String nomineeName = buildFullName(null, nom.getNomineeFirstName(), nom.getNomineeMiddleName(), nom.getNomineeLastName());
                                if ("N/A".equals(nomineeName) && nom.getNomineeEmail() == null && nom.getNomineeMobile() == null) continue;
                                html.append("<h3>Nominee ").append(idx).append("</h3>");
                                html.append("<table>");
                                appendRowIfPresent(html, "Name", nomineeName);
                                appendRowIfPresent(html, "Relationship", nom.getRelationship());
                                appendRowIfPresent(html, "Date of Birth", nom.getDateOfBirth() != null ? nom.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) : null);
                                appendRowIfPresent(html, "Email", nom.getNomineeEmail());
                                appendRowIfPresent(html, "Mobile", nom.getNomineeMobile());
                                appendRowIfPresent(html, "Allocation %", nom.getAllocationPercentage() != null ? nom.getAllocationPercentage().toString() : null);
                                appendRowIfPresent(html, "Document Type", nom.getNomineeDocType());
                                appendRowIfPresent(html, "Document No", nom.getNomineeDocNo());
                                appendRowIfPresent(html, "Is Minor", nom.getIsMinor() != null ? nom.getIsMinor().toString() : null);
                                appendRowIfPresent(html, "Guardian Name", nom.getGuardianName());
                                appendRowIfPresent(html, "Guardian DOB", nom.getGuardianDob() != null ? nom.getGuardianDob().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) : null);
                                appendRowIfPresent(html, "Guardian Mobile", nom.getGuardianMobile());
                                appendRowIfPresent(html, "Guardian PAN", nom.getGuardianPanNo());
                                html.append("</table>");
                                idx++;
                        }
                }

                // Other Information / Risk Profile
                if (riskProfile != null) {
                        html.append("<h2>Other Information / Risk Profile</h2>");
                        html.append("<table>");
                        appendRowIfPresent(html, "Source of Funds", riskProfile.getSourceOfFunds());
                        appendRowIfPresent(html, "Source of Wealth", riskProfile.getSourceOfWealth());
                        appendRowIfPresent(html, "Educational Qualification", riskProfile.getEducationalQualification());
                        appendRowIfPresent(html, "Gross/Annual Income", riskProfile.getAnnualIncome());
                        appendRowIfPresent(html, "Net Worth", riskProfile.getNetWorth());
                        appendRowIfPresent(html, "Occupation", riskProfile.getOccupation());
                        appendRowIfPresent(html, "Line of Business", riskProfile.getLineOfBusiness());
                        appendRowIfPresent(html, "Nature of Organisation", riskProfile.getNatureOfOrganisation());
                        appendRowIfPresent(html, "Investment Experience (Years)", riskProfile.getInvestmentExperienceYears() != null ? riskProfile.getInvestmentExperienceYears().toString() : null);
                        appendRowIfPresent(html, "Investment Experience In", riskProfile.getInvestmentExperienceIn());
                        appendRowIfPresent(html, "Risk Tolerance", riskProfile.getRiskTolerance());
                        appendRowIfPresent(html, "Investment Horizon", riskProfile.getInvestmentHorizon());
                        appendRowIfPresent(html, "Investment Objective", riskProfile.getInvestmentObjective());
                        appendRowIfPresent(html, "Market Knowledge", riskProfile.getMarketKnowledge());
                        appendRowIfPresent(html, "Loss Comfort Level", riskProfile.getLossComfortLevel());
                        appendRowIfPresent(html, "Politically Exposed", riskProfile.getPolExposed() != null ? riskProfile.getPolExposed().toString() : null);
                        appendRowIfPresent(html, "Politically Exposed Related", riskProfile.getPolExposedRelated() != null ? riskProfile.getPolExposedRelated().toString() : null);
                        html.append("</table>");
                }

                // Footer
                html.append("<div class='footer'>");
                html.append("Generated on: ")
                                .append(java.time.LocalDateTime.now()
                                                .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss")));
                html.append("</div>");

                html.append("</div>"); // Close content div

                // Add JavaScript for PDF generation and submission
                html.append("<script>");
                html.append("function downloadPDF() {");
                html.append("  const button = event.target;");
                html.append("  button.disabled = true;");
                html.append("  button.textContent = 'Generating...';");
                html.append("  const element = document.getElementById('content');");
                html.append("  const opt = { margin: 10, filename: 'Investor_Information.pdf', image: { type: 'jpeg', quality: 0.92 },");
                html.append("    html2canvas: { scale: 2, useCORS: true, letterRendering: true },");
                html.append("    jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' }");
                html.append("  };");
                html.append("  html2pdf().set(opt).from(element).save().then(() => {");
                html.append("    button.disabled = false;");
                html.append("    button.textContent = 'Download PDF';");
                html.append("  }).catch(() => {");
                html.append("    button.disabled = false;");
                html.append("    button.textContent = 'Download PDF';");
                html.append("    alert('Error generating PDF. Please try again.');");
                html.append("  });");
                html.append("}");
                html.append("function submitProfile() {");
                html.append("  if (confirm('Are you sure you want to submit your complete profile? This will mark your profile as complete for review.')) {");
                html.append("    const token = localStorage.getItem('authToken') || sessionStorage.getItem('authToken');");
                html.append("    if (!token) { alert('Session expired. Please login again.'); window.close(); return; }");
                html.append("    const button = event.target;");
                html.append("    button.disabled = true;");
                html.append("    button.textContent = 'Submitting...';");
                html.append("    fetch(window.location.origin + '/api/clients/profile/final-submit', {");
                html.append("      method: 'POST',");
                html.append("      headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + token },");
                html.append("      body: JSON.stringify({ informationCorrectConsent: true, legalCapacityConsent: true, modificationAwarenessConsent: true })");
                html.append("    }).then(response => {");
                html.append("      if (response.ok) { ");
                html.append("        alert('Profile submitted successfully! Your application is now under review.'); ");
                html.append("        if (window.opener) { window.opener.location.reload(); }");
                html.append("        window.close(); ");
                html.append("      } else { ");
                html.append("        return response.json().then(err => { throw new Error(err.message || 'Failed to submit'); });");
                html.append("      }");
                html.append("    }).catch(err => { ");
                html.append("      alert('Error: ' + err.message); ");
                html.append("      button.disabled = false;");
                html.append("      button.textContent = 'Submit Profile';");
                html.append("    });");
                html.append("  }");
                html.append("}");
                html.append("</script>");

                html.append("</body></html>");

                log.info("Print preview HTML generated for investor: {}", investorId);
                return html.toString();
        }

        /**
         * Helper method to create section header
         */
        private Paragraph createSection(String title) throws DocumentException {
                Paragraph section = new Paragraph(title, HEADER_FONT);
                section.setSpacingBefore(15);
                section.setSpacingAfter(10);
                return section;
        }

        /**
         * Helper method to create information table
         */
        private PdfPTable createInfoTable(String[][] data) throws DocumentException {
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                table.setWidths(new int[] { 30, 70 });
                table.setSpacingAfter(10);

                for (String[] row : data) {
                        PdfPCell labelCell = new PdfPCell(new Phrase(row[0], LABEL_FONT));
                        labelCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        labelCell.setPadding(8);
                        labelCell.setBorder(Rectangle.BOX);
                        table.addCell(labelCell);

                        PdfPCell valueCell = new PdfPCell(new Phrase(row[1], VALUE_FONT));
                        valueCell.setPadding(8);
                        valueCell.setBorder(Rectangle.BOX);
                        table.addCell(valueCell);
                }

                return table;
        }

        /**
         * Helper method to build full name from components
         */
        private String buildFullName(String title, String firstName, String middleName, String lastName) {
                StringBuilder fullName = new StringBuilder();
                if (title != null && !title.trim().isEmpty()) {
                        fullName.append(title.trim()).append(" ");
                }
                if (firstName != null && !firstName.trim().isEmpty()) {
                        fullName.append(firstName.trim()).append(" ");
                }
                if (middleName != null && !middleName.trim().isEmpty()) {
                        fullName.append(middleName.trim()).append(" ");
                }
                if (lastName != null && !lastName.trim().isEmpty()) {
                        fullName.append(lastName.trim());
                }
                String result = fullName.toString().trim();
                return result.isEmpty() ? "N/A" : result;
        }

        /**
         * Append a table row only if value is present (not null/empty)
         */
        private void appendRowIfPresent(StringBuilder html, String label, String value) {
                if (value != null && !value.trim().isEmpty()) {
                        html.append("<tr><td>").append(escapeHtml(label)).append("</td><td>")
                                        .append(escapeHtml(value)).append("</td></tr>");
                }
        }

        private String escapeHtml(String s) {
                if (s == null) return "";
                return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
        }

        /**
         * Generate comprehensive investor information PDF for final submission.
         * Aligns with Laravel: user-print-preview-final.blade.php (8 sections)
         */
        public byte[] generateFinalSubmissionPdf(Long investorId) {
                try {
                        Investor investor = investorRepository.findById(investorId)
                                        .orElseThrow(() -> new RuntimeException("Investor not found: " + investorId));

                        UserPersonalInformation personalInfo = personalInfoRepository
                                        .findByInvestorUniqueId(investor.getUniqueCode()).orElse(null);
                        UserPassportDetails passportDetails = passportDetailsRepository
                                        .findByInvestorUniqueCode(investor.getUniqueCode()).orElse(null);
                        InvestorBankDetails bankDetails = bankDetailsRepository
                                        .findByInvestorUniqueIdAndIsPrimaryTrue(investor.getUniqueCode())
                                        .orElse(bankDetailsRepository.findFirstByInvestorUniqueId(investor.getUniqueCode())
                                                        .orElse(null));
                        InvestorContactDetails contactDetails = contactDetailsRepository
                                        .findByInvestorUniqueId(investor.getUniqueCode()).orElse(null);
                        InvestorTaxInformation taxInfo = taxInformationRepository
                                        .findByInvestorUniqueId(investor.getUniqueCode()).orElse(null);
                        InvestorResidentialStatus residentialStatus = residentialStatusRepository
                                        .findByInvestorUniqueId(investor.getUniqueCode()).orElse(null);
                        java.util.List<InvestorNomination> nominations = nominationRepository
                                        .findByInvestorUniqueId(investor.getUniqueCode());
                        InvestorRiskProfile riskProfile = riskProfileRepository
                                        .findByInvestorUniqueId(investor.getUniqueCode()).orElse(null);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
                        PdfWriter.getInstance(document, baos);
                        document.open();

                        // Title
                        Paragraph title = new Paragraph("Investor Application Form", TITLE_FONT);
                        title.setAlignment(Element.ALIGN_CENTER);
                        title.setSpacingAfter(20);
                        document.add(title);

                        // Section 1: Personal Details
                        if (personalInfo != null) {
                                document.add(createSection("1. Personal Details"));
                                java.util.List<String[]> data = new java.util.ArrayList<>();
                                String name = buildFullName(personalInfo.getTitle() != null ? personalInfo.getTitle().toString() : null,
                                                personalInfo.getInvestorFirstName(), personalInfo.getInvestorMiddleName(),
                                                personalInfo.getInvestorLastName());
                                if (!"N/A".equals(name)) data.add(new String[] { "Full Name", name });
                                if (personalInfo.getInvestorGender() != null) data.add(new String[] { "Gender", personalInfo.getInvestorGender() });
                                if (personalInfo.getMaritalStatus() != null) data.add(new String[] { "Marital Status", personalInfo.getMaritalStatus() });
                                if (personalInfo.getUserDob() != null) data.add(new String[] { "Date of Birth",
                                                personalInfo.getUserDob().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) });
                                if (personalInfo.getCitizenship() != null) data.add(new String[] { "Citizenship", personalInfo.getCitizenship() });
                                if (personalInfo.getUserPanNo() != null) data.add(new String[] { "PAN", personalInfo.getUserPanNo() });
                                if (!data.isEmpty()) document.add(createInfoTable(data.toArray(new String[0][])));
                        }

                        // Section 2: Bank Details
                        if (bankDetails != null) {
                                document.add(createSection("2. Bank Details"));
                                java.util.List<String[]> data = new java.util.ArrayList<>();
                                if (bankDetails.getSettlementAccountType() != null) data.add(new String[] { "Account Type", bankDetails.getSettlementAccountType() });
                                if (bankDetails.getAccountHolderName() != null) data.add(new String[] { "Account Holder", bankDetails.getAccountHolderName() });
                                if (bankDetails.getAccountNumber() != null) data.add(new String[] { "Account Number", bankDetails.getAccountNumber() });
                                if (bankDetails.getIfscCode() != null) data.add(new String[] { "IFSC Code", bankDetails.getIfscCode() });
                                if (bankDetails.getBankName() != null) data.add(new String[] { "Bank Name", bankDetails.getBankName() });
                                if (!data.isEmpty()) document.add(createInfoTable(data.toArray(new String[0][])));
                        }

                        // Section 3: Passport Details
                        if (passportDetails != null) {
                                document.add(createSection("3. Passport/Identity Details"));
                                java.util.List<String[]> data = new java.util.ArrayList<>();
                                if (passportDetails.getDocumentType() != null) data.add(new String[] { "Document Type", passportDetails.getDocumentType() });
                                if (passportDetails.getPassportNumber() != null) data.add(new String[] { "Number", passportDetails.getPassportNumber() });
                                if (passportDetails.getPassportNationality() != null) data.add(new String[] { "Nationality", passportDetails.getPassportNationality() });
                                if (!data.isEmpty()) document.add(createInfoTable(data.toArray(new String[0][])));
                        }

                        // Section 4: Residential Status
                        if (residentialStatus != null) {
                                document.add(createSection("4. Residential Status"));
                                java.util.List<String[]> data = new java.util.ArrayList<>();
                                if (residentialStatus.getUserOciCardNo() != null) data.add(new String[] { "OCI Card No", residentialStatus.getUserOciCardNo() });
                                if (residentialStatus.getUserTypeOfProof() != null) data.add(new String[] { "Type of Proof", residentialStatus.getUserTypeOfProof() });
                                if (!data.isEmpty()) document.add(createInfoTable(data.toArray(new String[0][])));
                        }

                        // Section 5: Tax Information
                        if (taxInfo != null) {
                                document.add(createSection("5. Tax Information"));
                                java.util.List<String[]> data = new java.util.ArrayList<>();
                                if (taxInfo.getPanNumber() != null) data.add(new String[] { "PAN", taxInfo.getPanNumber() });
                                if (taxInfo.getFatcaStatus() != null) data.add(new String[] { "FATCA Status", taxInfo.getFatcaStatus() });
                                if (!data.isEmpty()) document.add(createInfoTable(data.toArray(new String[0][])));
                        }

                        // Section 6: Contact Details
                        if (contactDetails != null) {
                                document.add(createSection("6. Contact Details"));
                                java.util.List<String[]> data = new java.util.ArrayList<>();
                                if (contactDetails.getEmailAddress() != null) data.add(new String[] { "Email", contactDetails.getEmailAddress() });
                                if (contactDetails.getMobileNumber() != null) data.add(new String[] { "Mobile", contactDetails.getMobileNumber() });
                                if (contactDetails.getUserCity() != null) data.add(new String[] { "City", contactDetails.getUserCity() });
                                if (!data.isEmpty()) document.add(createInfoTable(data.toArray(new String[0][])));
                        }

                        // Section 7: Nomination
                        if (nominations != null && !nominations.isEmpty()) {
                                document.add(createSection("7. Nomination"));
                                for (InvestorNomination nom : nominations) {
                                        String name = buildFullName(null, nom.getNomineeFirstName(), nom.getNomineeMiddleName(), nom.getNomineeLastName());
                                        if (!"N/A".equals(name)) {
                                                java.util.List<String[]> data = new java.util.ArrayList<>();
                                                data.add(new String[] { "Nominee Name", name });
                                                if (nom.getRelationship() != null) data.add(new String[] { "Relationship", nom.getRelationship() });
                                                document.add(createInfoTable(data.toArray(new String[0][])));
                                        }
                                }
                        }

                        // Section 8: Risk Profile
                        if (riskProfile != null) {
                                document.add(createSection("8. Risk Profile"));
                                java.util.List<String[]> data = new java.util.ArrayList<>();
                                if (riskProfile.getSourceOfFunds() != null) data.add(new String[] { "Source of Funds", riskProfile.getSourceOfFunds() });
                                if (riskProfile.getOccupation() != null) data.add(new String[] { "Occupation", riskProfile.getOccupation() });
                                if (!data.isEmpty()) document.add(createInfoTable(data.toArray(new String[0][])));
                        }

                        // Footer
                        Paragraph footer = new Paragraph("\nGenerated on: " +
                                        java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss")),
                                        new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY));
                        footer.setAlignment(Element.ALIGN_CENTER);
                        document.add(footer);

                        document.close();
                        log.info("Final submission PDF generated for investor: {}", investorId);
                        return baos.toByteArray();
                } catch (DocumentException e) {
                        log.error("Error generating final PDF: {}", e.getMessage(), e);
                        throw new RuntimeException("Failed to generate PDF", e);
                }
        }

        public byte[] generateFinalSubmissionPdfByUniqueCode(String uniqueCode) {
                Investor investor = investorRepository.findByUniqueCode(uniqueCode)
                                .orElseThrow(() -> new RuntimeException("Investor not found: " + uniqueCode));
                return generateFinalSubmissionPdf(investor.getId());
        }

        /**
         * Generate investor PDF using FreeMarker template - Laravel Blade equivalent
         * Uses Flying Saucer to convert HTML to PDF
         */
        public byte[] generateInvestorPdfFromTemplate(Long investorId) {
                try {
                        String html = generatePrintPreviewHtmlFromTemplate(investorId);
                        return convertHtmlToPdf(html);
                } catch (Exception e) {
                        log.error("Error generating PDF from template: {}", e.getMessage(), e);
                        throw new RuntimeException("Failed to generate PDF from template", e);
                }
        }

        /**
         * Generate print preview HTML using FreeMarker template - Laravel Blade equivalent
         */
        public String generatePrintPreviewHtmlFromTemplate(Long investorId) {
                try {
                        Map<String, Object> templateData = prepareTemplateData(investorId);
                        return processTemplate("pdf/investor-profile.ftl", templateData);
                } catch (Exception e) {
                        log.error("Error generating HTML from template: {}", e.getMessage(), e);
                        throw new RuntimeException("Failed to generate HTML from template", e);
                }
        }

        /**
         * Generate investor PDF using FreeMarker template by user ID
         */
        public byte[] generateInvestorPdfFromTemplateByUserId(Long userId) {
                Investor investor = investorRepository.findByAuthorizedUser_Id(userId)
                                .orElseThrow(() -> new RuntimeException("Investor not found for user: " + userId));
                return generateInvestorPdfFromTemplate(investor.getId());
        }

        /**
         * Generate print preview HTML using FreeMarker template by user ID
         */
        public String generatePrintPreviewHtmlFromTemplateByUserId(Long userId) {
                Investor investor = investorRepository.findByAuthorizedUser_Id(userId)
                                .orElseThrow(() -> new RuntimeException("Investor not found for user: " + userId));
                return generatePrintPreviewHtmlFromTemplate(investor.getId());
        }

        /**
         * Prepare template data for FreeMarker - matches Laravel Blade template structure
         */
        private Map<String, Object> prepareTemplateData(Long investorId) {
                Investor investor = investorRepository.findById(investorId)
                                .orElseThrow(() -> new RuntimeException("Investor not found: " + investorId));

                String uniqueCode = investor.getUniqueCode();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                // Personal Information
                UserPersonalInformation personalInfo = personalInfoRepository
                                .findByInvestorUniqueId(uniqueCode).orElse(null);
                Map<String, Object> personalInfoMap = new HashMap<>();
                if (personalInfo != null) {
                        personalInfoMap.put("title", personalInfo.getTitle() != null ? personalInfo.getTitle().toString() : "");
                        personalInfoMap.put("investorFirstName", personalInfo.getInvestorFirstName() != null ? personalInfo.getInvestorFirstName() : "");
                        personalInfoMap.put("investorMiddleName", personalInfo.getInvestorMiddleName() != null ? personalInfo.getInvestorMiddleName() : "");
                        personalInfoMap.put("investorLastName", personalInfo.getInvestorLastName() != null ? personalInfo.getInvestorLastName() : "");
                        personalInfoMap.put("investorGender", personalInfo.getInvestorGender());
                        personalInfoMap.put("maritalStatus", personalInfo.getMaritalStatus());
                        personalInfoMap.put("maidenTitle", personalInfo.getMaidenTitle());
                        personalInfoMap.put("maidenName", personalInfo.getMaidenName());
                        personalInfoMap.put("maidenMiddleName", personalInfo.getMaidenMiddleName());
                        personalInfoMap.put("maidenLastName", personalInfo.getMaidenLastName());
                        personalInfoMap.put("userDob", personalInfo.getUserDob() != null ? dateFormatter.format(personalInfo.getUserDob()) : "N/A");
                        personalInfoMap.put("cityOfDob", personalInfo.getCityOfDob());
                        personalInfoMap.put("countryDob", personalInfo.getCountryDob());
                        personalInfoMap.put("citizenship", personalInfo.getCitizenship());
                        personalInfoMap.put("countryOfResidence", personalInfo.getCountryOfResidence());
                        personalInfoMap.put("userPanNo", personalInfo.getUserPanNo());
                        personalInfoMap.put("fatherNameTitle", personalInfo.getFatherNameTitle());
                        personalInfoMap.put("fathersFirstName", personalInfo.getFathersFirstName());
                        personalInfoMap.put("fathersMiddleName", personalInfo.getFathersMiddleName());
                        personalInfoMap.put("fathersLastName", personalInfo.getFathersLastName());
                        personalInfoMap.put("motherNameTitle", personalInfo.getMotherNameTitle());
                        personalInfoMap.put("motherFirstName", personalInfo.getMotherFirstName());
                        personalInfoMap.put("motherMiddleName", personalInfo.getMotherMiddleName());
                        personalInfoMap.put("motherLastName", personalInfo.getMotherLastName());
                }

                // Bank Details
                InvestorBankDetails bankDetails = bankDetailsRepository
                                .findByInvestorUniqueIdAndIsPrimaryTrue(uniqueCode)
                                .orElse(bankDetailsRepository.findFirstByInvestorUniqueId(uniqueCode).orElse(null));
                Map<String, Object> bankDetailsMap = new HashMap<>();
                if (bankDetails != null) {
                        bankDetailsMap.put("accountType", bankDetails.getAccountType());
                        bankDetailsMap.put("rbiApproval", bankDetails.getRbiApproval());
                        bankDetailsMap.put("rbiApprovalOrderNumber", bankDetails.getRbiApprovalOrderNumber());
                        // Note: rbiApprovalDate is stored as String, no formatting needed
                        bankDetailsMap.put("rbiApprovalDate", bankDetails.getRbiApprovalDate() != null ? bankDetails.getRbiApprovalDate() : "");
                        bankDetailsMap.put("accountHolderName", bankDetails.getAccountHolderName());
                        bankDetailsMap.put("bankName", bankDetails.getBankName());
                        bankDetailsMap.put("branchName", bankDetails.getBranchName());
                        bankDetailsMap.put("bankAddress", bankDetails.getBankAddress());
                        bankDetailsMap.put("accountNumber", bankDetails.getAccountNumber());
                        bankDetailsMap.put("ifscCode", bankDetails.getIfscCode());
                }

                // Passport Details
                UserPassportDetails passportDetails = passportDetailsRepository
                                .findByInvestorUniqueCode(uniqueCode).orElse(null);
                Map<String, Object> passportDetailsMap = new HashMap<>();
                if (passportDetails != null) {
                        passportDetailsMap.put("nationality", passportDetails.getPassportNationality());
                        passportDetailsMap.put("passportNumber", passportDetails.getPassportNumber());
                        passportDetailsMap.put("dateOfIssue", passportDetails.getPassportIssueDate() != null ? dateFormatter.format(passportDetails.getPassportIssueDate()) : "");
                        passportDetailsMap.put("placeOfIssue", passportDetails.getPassportPlaceOfIssue());
                        passportDetailsMap.put("validUpto", passportDetails.getPassportExpiryDate() != null ? dateFormatter.format(passportDetails.getPassportExpiryDate()) : "");
                        passportDetailsMap.put("becomingNonResidentDate", passportDetails.getPassportDateNonResident() != null ? dateFormatter.format(passportDetails.getPassportDateNonResident()) : "");
                        passportDetailsMap.put("yearsAbroad", passportDetails.getPassportNoYearsAbroad());
                }

                // Residential Status
                InvestorResidentialStatus residentialStatus = residentialStatusRepository
                                .findByInvestorUniqueId(uniqueCode).orElse(null);
                Map<String, Object> residentialStatusMap = new HashMap<>();
                if (residentialStatus != null) {
                        // Note: InvestorResidentialStatus stores dates as Strings, no formatting needed
                        residentialStatusMap.put("ociCardNo", residentialStatus.getUserOciCardNo());
                        residentialStatusMap.put("ociIssueDate", residentialStatus.getUserOciIssueDate());
                        residentialStatusMap.put("ociValidUpto", residentialStatus.getUserOciValidUpto());
                        residentialStatusMap.put("typeOfProof", residentialStatus.getUserTypeOfProof());
                        residentialStatusMap.put("visaType", residentialStatus.getUserVisaType());
                        residentialStatusMap.put("visaNumber", residentialStatus.getUserVisaNumber());
                        residentialStatusMap.put("visaIssuerDate", residentialStatus.getUserVisaDateOfIssue());
                        residentialStatusMap.put("visaExpiryDate", residentialStatus.getUserVisaValidUpto());
                } else if (personalInfo != null) {
                        // Fallback to personal info if residential status not available
                        residentialStatusMap.put("ociCardNo", personalInfo.getUserOciCardNo());
                        residentialStatusMap.put("ociIssueDate", personalInfo.getUserOciIssueDate() != null ? dateFormatter.format(personalInfo.getUserOciIssueDate()) : "");
                        residentialStatusMap.put("ociValidUpto", personalInfo.getUserOciValidUpto() != null ? dateFormatter.format(personalInfo.getUserOciValidUpto()) : "");
                        residentialStatusMap.put("visaType", personalInfo.getUserVisaType());
                        residentialStatusMap.put("visaNumber", personalInfo.getUserVisaNumber());
                        residentialStatusMap.put("visaIssuerDate", personalInfo.getUserVisaIssuerDate() != null ? dateFormatter.format(personalInfo.getUserVisaIssuerDate()) : "");
                        residentialStatusMap.put("visaExpiryDate", personalInfo.getUserVisaExpiryDate() != null ? dateFormatter.format(personalInfo.getUserVisaExpiryDate()) : "");
                }

                // Tax Information
                InvestorTaxInformation taxInfo = taxInformationRepository
                                .findByInvestorUniqueId(uniqueCode).orElse(null);
                Map<String, Object> taxInfoMap = new HashMap<>();
                if (taxInfo != null) {
                        taxInfoMap.put("currentCountryResidenceForTax", taxInfo.getTaxCountry());
                        taxInfoMap.put("taxIdentificationNumber", taxInfo.getTaxIdentificationNumber());
                        taxInfoMap.put("taxIdentificationNumberType", taxInfo.getTaxIdentificationNumberType());
                        taxInfoMap.put("taxResidencyCertificateNo", taxInfo.getTaxResidencyCertificateNo());
                        taxInfoMap.put("taxResidencyCertificateDate", taxInfo.getTaxResidencyCertificateDate());
                        taxInfoMap.put("usPersonFatca", taxInfo.getFatcaStatus());
                }

                // Contact Details
                InvestorContactDetails contactDetails = contactDetailsRepository
                                .findByInvestorUniqueId(uniqueCode).orElse(null);
                Map<String, Object> contactDetailsMap = new HashMap<>();
                if (contactDetails != null) {
                        contactDetailsMap.put("email", contactDetails.getEmailAddress());
                        contactDetailsMap.put("mobileNo", contactDetails.getMobileNumber());
                        contactDetailsMap.put("addressLine1", contactDetails.getAddressLine1());
                        contactDetailsMap.put("addressLine2", contactDetails.getAddressLine2());
                        contactDetailsMap.put("addressLine3", contactDetails.getAddressLine3());
                        contactDetailsMap.put("userCity", contactDetails.getUserCity());
                        contactDetailsMap.put("userState", contactDetails.getUserState());
                        contactDetailsMap.put("userCountry", contactDetails.getUserCountry());
                        contactDetailsMap.put("userZipCode", contactDetails.getUserZipCode());
                        contactDetailsMap.put("corrAddressLine1", contactDetails.getCorrAddressLine1());
                        contactDetailsMap.put("corrAddressLine2", contactDetails.getCorrAddressLine2());
                        contactDetailsMap.put("corrAddressLine3", contactDetails.getCorrAddressLine3());
                        contactDetailsMap.put("corrUserCity", contactDetails.getCorrUserCity());
                        contactDetailsMap.put("corrUserState", contactDetails.getCorrUserState());
                        contactDetailsMap.put("corrUserCountry", contactDetails.getCorrUserCountry());
                        contactDetailsMap.put("corrUserZipCode", contactDetails.getCorrUserZipCode());
                }

                // Nomination Details
                java.util.List<InvestorNomination> nominations = nominationRepository
                                .findByInvestorUniqueId(uniqueCode);
                java.util.List<Map<String, Object>> nominationsList = nominations.stream().map(nomination -> {
                        Map<String, Object> nominationMap = new HashMap<>();
                        String nomineeName = (nomination.getNomineeFirstName() != null ? nomination.getNomineeFirstName() : "") + " " + 
                                           (nomination.getNomineeLastName() != null ? nomination.getNomineeLastName() : "");
                        nominationMap.put("nomineeName", nomineeName.trim());
                        nominationMap.put("relationship", nomination.getRelationship());
                        nominationMap.put("percentageShare", nomination.getAllocationPercentage());
                        nominationMap.put("mobileNo", nomination.getNomineeMobile());
                        nominationMap.put("panNo", nomination.getNomineeDocNo());
                        nominationMap.put("dateOfBirth", nomination.getDateOfBirth() != null ? dateFormatter.format(nomination.getDateOfBirth()) : "");
                        nominationMap.put("guardianName", nomination.getGuardianName());
                        nominationMap.put("guardianDocType", nomination.getGuardianDocType());
                        nominationMap.put("guardianDocNo", nomination.getGuardianDocNo());
                        return nominationMap;
                }).collect(Collectors.toList());

                // Risk Profile
                InvestorRiskProfile riskProfile = riskProfileRepository
                                .findByInvestorUniqueId(uniqueCode).orElse(null);
                Map<String, Object> riskProfileMap = new HashMap<>();
                if (riskProfile != null) {
                        riskProfileMap.put("sourceOfFunds", riskProfile.getSourceOfFunds());
                        riskProfileMap.put("sourceOfWealth", riskProfile.getSourceOfWealth());
                        riskProfileMap.put("educationalQualification", riskProfile.getEducationalQualification());
                        riskProfileMap.put("grossIncome", riskProfile.getAnnualIncome());
                        riskProfileMap.put("netWorth", riskProfile.getNetWorth());
                        riskProfileMap.put("occupation", riskProfile.getOccupation());
                        riskProfileMap.put("lineOfBusiness", riskProfile.getLineOfBusiness());
                        riskProfileMap.put("natureOfOrganisation", riskProfile.getNatureOfOrganisation());
                        riskProfileMap.put("politicallyExposed", riskProfile.getPolExposed() != null ? riskProfile.getPolExposed().toString() : "NO");
                        riskProfileMap.put("relatedToPoliticallyExposed", riskProfile.getPolExposedRelated() != null ? riskProfile.getPolExposedRelated().toString() : "NO");
                        riskProfileMap.put("foreignExchangeMoneyChanger", riskProfile.getMoneyChangeService() != null ? riskProfile.getMoneyChangeService().toString() : "NO");
                        riskProfileMap.put("gamingGamblingLottery", riskProfile.getGamblingService() != null ? riskProfile.getGamblingService().toString() : "NO");
                        riskProfileMap.put("moneyLendingPawning", riskProfile.getPawningService() != null ? riskProfile.getPawningService().toString() : "NO");
                        riskProfileMap.put("violationOfSecuritiesLaws", riskProfile.getInstanceViolation() != null ? riskProfile.getInstanceViolation().toString() : "NO");
                        riskProfileMap.put("investmentExperienceYears", riskProfile.getInvestmentExperienceYears());
                        riskProfileMap.put("investmentExperienceIn", riskProfile.getInvestmentExperienceIn());
                }

                // Build investor map
                Map<String, Object> investorMap = new HashMap<>();
                investorMap.put("uniqueCode", investor.getUniqueCode());
                investorMap.put("investorType", investor.getInvestorType());
                investorMap.put("registerAs", investor.getRegisterAs());

                // Broker section — fresh Dataverse fetch (Laravel AdminController::pdf_view L541).
                // Resolves the current broker-firm name at PDF-render time so the document
                // reflects any Dynamics-side edits that have not yet propagated into
                // master_brokers via the nightly sync.
                Map<String, Object> brokerMap = resolveBrokerForPdf(uniqueCode);

                // Build template data map
                Map<String, Object> templateData = new HashMap<>();
                templateData.put("investor", investorMap);
                templateData.put("personalInfo", personalInfoMap);
                templateData.put("bankDetails", bankDetailsMap);
                templateData.put("passportDetails", passportDetailsMap);
                templateData.put("residentialStatus", residentialStatusMap);
                templateData.put("taxInfo", taxInfoMap);
                templateData.put("contactDetails", contactDetailsMap);
                templateData.put("nominations", nominationsList);
                templateData.put("riskProfile", riskProfileMap);
                templateData.put("broker", brokerMap);

                return templateData;
        }

        /**
         * Resolve the broker's display name for the PDF by calling Dataverse live,
         * mirroring Laravel's {@code AdminController::pdf_view} (L541) which does a
         * {@code GET /ss_brokers?$filter=ss_brokerid eq '<guid>'} every time the PDF
         * is generated — picking up any recent Dynamics-side name changes without
         * waiting for the nightly master sync.
         *
         * <p>Broker GUID is read from the introduced-investor session
         * ({@code intro_investor_temp.ss_broker_value}).  Self-registered investors
         * without a broker yield an empty map.
         *
         * <p>Both {@link DynamicsCrmService} and {@link IntroInvestorTempRepository}
         * are optional autowires — missing either simply returns an empty map so
         * PDF generation continues without broker information.
         */
        private Map<String, Object> resolveBrokerForPdf(String uniqueCode) {
                Map<String, Object> brokerMap = new HashMap<>();
                if (dynamicsCrmService == null || introInvestorTempRepository == null) {
                        return brokerMap;
                }
                IntroInvestorTemp intro = introInvestorTempRepository
                                .findByUniqueCodeDb(uniqueCode)
                                .orElse(null);
                if (intro == null || intro.getSsBrokerValue() == null
                                || intro.getSsBrokerValue().isBlank()) {
                        return brokerMap;
                }

                String brokerGuid = intro.getSsBrokerValue();
                brokerMap.put("brokerId", brokerGuid);
                brokerMap.put("serviceProviderType", intro.getServiceProviderType());

                try {
                        String name = dynamicsCrmService.fetchBrokerName(brokerGuid);
                        if (name != null && !name.isBlank()) {
                                brokerMap.put("name", name);
                        }
                } catch (Exception e) {
                        log.warn("PDF broker-name fetch failed for {}: {}", brokerGuid, e.getMessage());
                }
                return brokerMap;
        }

        /**
         * Process FreeMarker template with data
         */
        private String processTemplate(String templateName, Map<String, Object> data) throws IOException, TemplateException {
                if (freeMarkerConfigurer == null) {
                        throw new RuntimeException("FreeMarker not configured");
                }
                
                Configuration configuration = freeMarkerConfigurer.getConfiguration();
                Template template = configuration.getTemplate(templateName);
                StringWriter stringWriter = new StringWriter();
                template.process(data, stringWriter);
                return stringWriter.toString();
        }

        /**
         * Convert HTML to PDF using Flying Saucer - Laravel DomPDF equivalent
         */
        private byte[] convertHtmlToPdf(String html) {
                try {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        ITextRenderer renderer = new ITextRenderer();
                        renderer.setDocumentFromString(html);
                        renderer.layout();
                        renderer.createPDF(outputStream);
                        return outputStream.toByteArray();
                } catch (Exception e) {
                        log.error("Error converting HTML to PDF: {}", e.getMessage(), e);
                        throw new RuntimeException("Failed to convert HTML to PDF", e);
                }
        }

}
