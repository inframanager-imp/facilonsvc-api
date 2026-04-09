package com.facilon.app.module.client.service;

import com.facilon.app.integration.sharepoint.SharePointService;
import com.facilon.app.module.client.dto.DocumentDto;
import com.facilon.app.module.client.dto.DocumentUploadResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Service for investor document management
 * TODO: Migrate to database persistence (JPA entities + repositories)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClientDocumentService {

    private final SharePointService sharePointService;
    private final DocumentNotificationService notificationService;
    
    // In-memory storage (TODO: migrate to DB)
    private final Map<Long, DocumentDto> documentsById = new ConcurrentHashMap<>();
    private final Map<String, List<Long>> documentsByInvestor = new ConcurrentHashMap<>();
    private final Map<String, String> investorEmails = new ConcurrentHashMap<>(); // investorCode -> email
    private final Map<String, String> investorNames = new ConcurrentHashMap<>(); // investorCode -> name
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "jpg", "jpeg", "png", "doc", "docx"
    );
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    
    /**
     * Upload document for investor
     */
    public DocumentUploadResponseDto uploadDocument(
            String investorCode,
            String documentType,
            MultipartFile file
    ) {
        try {
            // Validate file
            String validationError = validateFile(file);
            if (validationError != null) {
                return DocumentUploadResponseDto.error(file.getOriginalFilename(), validationError);
            }
            
            // Generate unique filename
            String originalFileName = file.getOriginalFilename();
            String extension = getFileExtension(originalFileName);
            String uniqueFileName = generateUniqueFileName(investorCode, documentType, extension);
            
            // Upload to SharePoint
            String folderPath = "KYC/" + investorCode;
            String sharePointItemId = sharePointService.uploadFile(
                    folderPath,
                    uniqueFileName,
                    file.getBytes()
            );
            
            // Create document metadata
            Long documentId = idGenerator.getAndIncrement();
            DocumentDto document = new DocumentDto();
            document.setId(documentId);
            document.setDocumentType(documentType);
            document.setFileName(uniqueFileName);
            document.setOriginalFileName(originalFileName);
            document.setFileExtension(extension);
            document.setFileSize(file.getSize());
            document.setSharePointItemId(sharePointItemId);
            document.setUploadedBy(investorCode);
            document.setUploadedAt(LocalDateTime.now());
            document.setStatus("UPLOADED");
            
            // Store metadata
            documentsById.put(documentId, document);
            documentsByInvestor.computeIfAbsent(investorCode, k -> new ArrayList<>()).add(documentId);
            
            log.info("Document uploaded: {} for investor: {}", uniqueFileName, investorCode);
            
            // Send email notification
            sendUploadNotification(investorCode, document);
            
            return DocumentUploadResponseDto.success(documentId, originalFileName, documentType);
            
        } catch (IOException e) {
            log.error("Failed to upload document", e);
            return DocumentUploadResponseDto.error(file.getOriginalFilename(), "File upload failed: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during upload", e);
            return DocumentUploadResponseDto.error(file.getOriginalFilename(), "Upload failed: " + e.getMessage());
        }
    }
    
    /**
     * Get all documents for investor
     */
    public List<DocumentDto> getInvestorDocuments(String investorCode) {
        List<Long> docIds = documentsByInvestor.getOrDefault(investorCode, Collections.emptyList());
        return docIds.stream()
                .map(documentsById::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(DocumentDto::getUploadedAt).reversed())
                .collect(Collectors.toList());
    }
    
    /**
     * Get all documents from all investors (Admin function)
     */
    public List<DocumentDto> getAllDocuments() {
        return documentsById.values().stream()
                .sorted(Comparator.comparing(DocumentDto::getUploadedAt).reversed())
                .collect(Collectors.toList());
    }
    
    /**
     * Get document by ID
     */
    public Optional<DocumentDto> getDocumentById(Long documentId) {
        return Optional.ofNullable(documentsById.get(documentId));
    }
    
    /**
     * Download document content
     */
    public byte[] downloadDocument(Long documentId) {
        DocumentDto document = documentsById.get(documentId);
        if (document == null) {
            throw new RuntimeException("Document not found: " + documentId);
        }
        
        return sharePointService.downloadFile(document.getSharePointItemId());
    }
    
    /**
     * Delete document
     */
    public boolean deleteDocument(Long documentId, String investorCode) {
        DocumentDto document = documentsById.get(documentId);
        if (document == null || !document.getUploadedBy().equals(investorCode)) {
            return false;
        }
        
        try {
            // Delete from SharePoint
            sharePointService.deleteFile(document.getSharePointItemId());
            
            // Delete metadata
            documentsById.remove(documentId);
            List<Long> investorDocs = documentsByInvestor.get(investorCode);
            if (investorDocs != null) {
                investorDocs.remove(documentId);
            }
            
            log.info("Document deleted: {} for investor: {}", documentId, investorCode);
            return true;
        } catch (Exception e) {
            log.error("Failed to delete document", e);
            return false;
        }
    }
    
    /**
     * Verify/Reject document (Admin function)
     */
    public boolean verifyDocument(Long documentId, String status, String remarks, String adminUsername) {
        DocumentDto document = documentsById.get(documentId);
        if (document == null) {
            return false;
        }
        
        String investorCode = document.getUploadedBy();
        document.setStatus(status); // VERIFIED or REJECTED
        document.setRemarks(remarks);
        document.setVerifiedBy(adminUsername);
        document.setVerifiedAt(LocalDateTime.now());
        
        log.info("Document {} {} by {}", documentId, status, adminUsername);
        
        // Send email notification
        if ("VERIFIED".equals(status)) {
            sendVerifiedNotification(investorCode, document);
            checkAndNotifyAllVerified(investorCode);
        } else if ("REJECTED".equals(status)) {
            sendRejectedNotification(investorCode, document);
        }
        
        return true;
    }
    
    // ============= Backward Compatibility Methods for ClientKycService =============
    
    /**
     * Upload file - backward compatible method for ClientKycService
     */
    public String uploadFile(MultipartFile file, String investorCode, String documentType) {
        try {
            // Generate unique filename
            String originalFileName = file.getOriginalFilename();
            String extension = getFileExtension(originalFileName);
            String uniqueFileName = generateUniqueFileName(investorCode, documentType, extension);
            
            // Upload to SharePoint
            String folderPath = "KYC/" + investorCode;
            String sharePointItemId = sharePointService.uploadFile(
                    folderPath,
                    uniqueFileName,
                    file.getBytes()
            );
            
            // Return SharePoint URL
            return "sharepoint://" + sharePointItemId;
        } catch (Exception e) {
            log.error("Failed to upload file", e);
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }
    
    /**
     * Check if URL is SharePoint URL
     */
    public static boolean isSharePointUrl(String url) {
        return url != null && url.startsWith("sharepoint://");
    }
    
    /**
     * Download file by URL or SharePoint ID
     */
    public byte[] downloadFile(String urlOrId) {
        try {
            String sharePointId = urlOrId;
            if (isSharePointUrl(urlOrId)) {
                sharePointId = urlOrId.replace("sharepoint://", "");
            }
            return sharePointService.downloadFile(sharePointId);
        } catch (Exception e) {
            log.error("Failed to download file: {}", urlOrId, e);
            throw new RuntimeException("File download failed: " + e.getMessage());
        }
    }
    
    // Helper methods
    
    private String validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            return "File is empty";
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            return "File size exceeds 10 MB limit";
        }
        
        String extension = getFileExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            return "Invalid file type. Allowed: " + String.join(", ", ALLOWED_EXTENSIONS);
        }
        
        return null; // Valid
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
    
    private String generateUniqueFileName(String investorCode, String documentType, String extension) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return String.format("%s_%s_%s.%s", investorCode, documentType, timestamp, extension);
    }
    
    // Email notification helpers
    
    private void sendUploadNotification(String investorCode, DocumentDto document) {
        String email = investorEmails.get(investorCode);
        String name = investorNames.getOrDefault(investorCode, "Investor");
        if (email != null) {
            notificationService.sendDocumentUploadedNotification(email, name, document);
        }
    }
    
    private void sendVerifiedNotification(String investorCode, DocumentDto document) {
        String email = investorEmails.get(investorCode);
        String name = investorNames.getOrDefault(investorCode, "Investor");
        if (email != null) {
            notificationService.sendDocumentVerifiedNotification(email, name, document);
        }
    }
    
    private void sendRejectedNotification(String investorCode, DocumentDto document) {
        String email = investorEmails.get(investorCode);
        String name = investorNames.getOrDefault(investorCode, "Investor");
        if (email != null) {
            notificationService.sendDocumentRejectedNotification(email, name, document);
        }
    }
    
    private void checkAndNotifyAllVerified(String investorCode) {
        List<Long> docIds = documentsByInvestor.getOrDefault(investorCode, Collections.emptyList());
        boolean allVerified = docIds.stream()
                .map(documentsById::get)
                .filter(Objects::nonNull)
                .allMatch(doc -> "VERIFIED".equals(doc.getStatus()));
        
        if (allVerified && !docIds.isEmpty()) {
            String email = investorEmails.get(investorCode);
            String name = investorNames.getOrDefault(investorCode, "Investor");
            if (email != null) {
                notificationService.sendAllDocumentsVerifiedNotification(email, name);
            }
        }
    }
    
    // Public methods to set investor details (called during registration/login)
    
    public void setInvestorEmail(String investorCode, String email) {
        investorEmails.put(investorCode, email);
    }
    
    public void setInvestorName(String investorCode, String name) {
        investorNames.put(investorCode, name);
    }
    
    public void setInvestorDetails(String investorCode, String email, String name) {
        investorEmails.put(investorCode, email);
        investorNames.put(investorCode, name);
    }
}
