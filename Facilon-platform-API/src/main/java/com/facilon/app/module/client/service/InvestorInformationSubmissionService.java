package com.facilon.app.module.client.service;

import com.facilon.app.integration.dynamics.DynamicsCrmService;
import com.facilon.app.integration.sharepoint.SharePointService;
import com.facilon.app.module.client.model.Investor;
import com.facilon.app.module.client.model.IntroInvestorTemp;
import com.facilon.app.module.client.model.UserPersonalInformation;
import com.facilon.app.module.client.repository.IntroInvestorTempRepository;
import com.facilon.app.module.client.repository.InvestorRepository;
import com.facilon.app.module.client.repository.UserPersonalInformationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestorInformationSubmissionService {

    private final PdfGenerationService pdfGenerationService;
    private final SharePointService sharePointService;
    private final DynamicsCrmService dynamicsCrmService;
    private final InvestorNotificationService notificationService;
    private final InvestorRepository investorRepository;
    private final UserPersonalInformationRepository personalInfoRepository;
    private final IntroInvestorTempRepository introInvestorTempRepository;

    @Value("${dataverse.sharepoint.parent-location-id:112fd6a2-12f1-f011-8406-7ced8d285638}")
    private String parentLocationId;

    @Value("${dataverse.sharepoint.site-collection-id:43de59de-265e-f011-877b-7c1e5232a375}")
    private String siteCollectionId;

    @Value("${sharepoint.investor-documents.base-url:https://facilon.sharepoint.com/sites/CRM/ss_investordocuments}")
    private String sharePointBaseUrl;

    @Transactional
    public void submitInvestorInformation(String uniqueCode) {
        log.info("=== Starting Final Submission for investor: {} ===", uniqueCode);

        try {
            Investor investor = investorRepository.findByUniqueCode(uniqueCode)
                    .orElseThrow(() -> new RuntimeException("Investor not found: " + uniqueCode));

            UserPersonalInformation personalInfo = personalInfoRepository.findByInvestorUniqueId(uniqueCode)
                    .orElseThrow(() -> new RuntimeException("Personal information not found: " + uniqueCode));

            IntroInvestorTemp introTemp = introInvestorTempRepository.findByUniqueCodeDb(uniqueCode)
                    .orElseThrow(() -> new RuntimeException("Investor assignment data not found: " + uniqueCode));

            // Generate PDF
            log.info("Generating PDF for investor {}", uniqueCode);
            byte[] pdfBytes = pdfGenerationService.generateFinalSubmissionPdfByUniqueCode(uniqueCode);
            log.info("PDF generated, size: {} bytes", pdfBytes.length);

            // Fetch document master ID
            String brokerGuid = introTemp.getSsBrokerValue();
            if (brokerGuid == null || brokerGuid.isBlank()) {
                throw new RuntimeException("Broker GUID not found");
            }

            log.info("Fetching document master ID");
            String documentMasterId = dynamicsCrmService.fetchDocumentMasterIdForInvestorInfo(brokerGuid);
            if (documentMasterId == null) {
                throw new RuntimeException("Document master ID not found");
            }

            // Fetch investor document record
            String investorGuid = introTemp.getIntroInvestorId();
            if (investorGuid == null || investorGuid.isBlank()) {
                throw new RuntimeException("Investor GUID not found");
            }

            log.info("Fetching investor document record");
            Map<String, String> investorDoc = dynamicsCrmService.fetchInvestorDocumentRecord(
                    investorGuid, brokerGuid, documentMasterId);
            if (investorDoc == null) {
                throw new RuntimeException("Investor document record not found");
            }

            String investorDocumentId = investorDoc.get("ss_investordocumentsid");
            String documentName = investorDoc.get("ss_name");
            
            // Create folder name
            String folderName = documentName + "_" + investorDocumentId.replace("-", "").toUpperCase();
            log.info("SharePoint folder name: {}", folderName);

            // Create SharePoint folder
            log.info("Creating SharePoint folder");
            String folderId = sharePointService.createFolder(folderName);

            // Upload PDF
            String pdfFileName = "Investor_Application_Status1.pdf";
            log.info("Uploading PDF to SharePoint");
            sharePointService.uploadFileToFolder(folderId, pdfFileName, pdfBytes);
            
            String fullDocumentUrl = sharePointBaseUrl + "/" + folderName;
            log.info("PDF uploaded: {}", fullDocumentUrl);

            // Update local database
            log.info("Updating local database");
            personalInfo.setFinalSubmitStatus(1);
            personalInfo.setFinalSubmit(1);
            personalInfo.setPersonalInfoTab("1,2,3,4,5,6,7,8,9");
            personalInfo.setUpdatedAt(LocalDateTime.now());
            personalInfoRepository.save(personalInfo);

            // Update Dataverse document
            log.info("Updating Dataverse document record");
            dynamicsCrmService.updateInvestorDocumentUrl(investorDocumentId, fullDocumentUrl);

            // Create SharePoint location
            log.info("Creating SharePoint document location");
            dynamicsCrmService.createSharePointDocumentLocation(
                    investorDocumentId, folderName, parentLocationId, siteCollectionId);

            // Send email notification using template 20-final-submission.html
            try {
                log.info("Sending final submission confirmation email");
                String recipientEmail = investor.getAuthorizedUser().getEmailId();
                String investorName = investor.getAuthorizedUser().getFirstName();
                if (investor.getAuthorizedUser().getLastName() != null) {
                    investorName += " " + investor.getAuthorizedUser().getLastName();
                }
                
                notificationService.sendFinalSubmissionEmail(
                    recipientEmail,
                    investorName,
                    uniqueCode
                );
                log.info("Final submission email sent to: {}", recipientEmail);
            } catch (Exception e) {
                log.error("Failed to send final submission email: {}", e.getMessage());
            }

            log.info("=== Final Submission Completed for {} ===", uniqueCode);

        } catch (Exception e) {
            log.error("Final submission failed for {}: {}", uniqueCode, e.getMessage(), e);
            throw new RuntimeException("Final submission failed: " + e.getMessage(), e);
        }
    }
}
