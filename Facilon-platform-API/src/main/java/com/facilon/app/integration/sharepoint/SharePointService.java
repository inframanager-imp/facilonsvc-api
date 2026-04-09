package com.facilon.app.integration.sharepoint;

import com.facilon.app.model.TenantSharePointConfig;
import com.facilon.app.service.TenantSharePointConfigService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


/**
 * SharePoint document operations via Microsoft Graph API.
 * Configuration loaded from database (tenant_sharepoint_config table).
 * Upload, download, delete files in SharePoint Online.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SharePointService {

    private static final String GRAPH_BASE = "https://graph.microsoft.com/v1.0";
    private final RestTemplate restTemplate;

    private final AzureGraphTokenProvider tokenProvider;
    private final TenantSharePointConfigService sharePointConfigService;

    /**
     * Create a folder in SharePoint document library.
     * @param folderName Name of the folder (e.g., "Investor Application Form_ABC123")
     * @return Folder ID
     */
    public String createFolder(String folderName) {
        String token = tokenProvider.getGraphToken();
        String siteId = resolveSiteId(token);
        String driveId = resolveDriveId(token, siteId);

        String createFolderUrl = GRAPH_BASE + "/sites/" + siteId + "/drives/" + driveId + "/root/children";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        java.util.Map<String, Object> folderBody = new java.util.HashMap<>();
        folderBody.put("name", folderName);
        folderBody.put("folder", new java.util.HashMap<>());
        folderBody.put("@microsoft.graph.conflictBehavior", "rename");

        HttpEntity<java.util.Map<String, Object>> entity = new HttpEntity<>(folderBody, headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(createFolderUrl, HttpMethod.POST, entity, JsonNode.class);

        if (response.getBody() == null || !response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("SharePoint folder creation failed");
        }

        String folderId = response.getBody().path("id").asText(null);
        log.info("Created SharePoint folder: {} (id: {})", folderName, folderId);
        return folderId;
    }

    /**
     * Upload file to a specific folder in SharePoint.
     * @param folderId Folder ID from createFolder()
     * @param fileName File name
     * @param content File bytes
     * @return SharePoint folder URL
     */
    public String uploadFileToFolder(String folderId, String fileName, byte[] content) {
        String token = tokenProvider.getGraphToken();
        String siteId = resolveSiteId(token);
        String driveId = resolveDriveId(token, siteId);

        String uploadUrl = GRAPH_BASE + "/sites/" + siteId + "/drives/" + driveId + "/items/" + folderId + ":/" + fileName + ":/content";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        HttpEntity<byte[]> entity = new HttpEntity<>(content, headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(uploadUrl, HttpMethod.PUT, entity, JsonNode.class);

        if (response.getBody() == null || !response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("SharePoint file upload failed");
        }

        String webUrl = response.getBody().path("webUrl").asText(null);
        log.info("Uploaded file {} to SharePoint folder {}", fileName, folderId);
        
        if (webUrl != null && webUrl.contains("/")) {
            int lastSlash = webUrl.lastIndexOf("/");
            return webUrl.substring(0, lastSlash);
        }
        return webUrl;
    }

    /**
     * Upload file to SharePoint. Path is relative to document library root.
     *
     * @param folderPath e.g. "KYC/INV202602101234"
     * @param fileName   e.g. "PASSPORT_123.pdf"
     * @param content    file bytes
     * @return webUrl of uploaded file (stored in KycDocuments.documentUrl for reference)
     */
    public String uploadFile(String folderPath, String fileName, byte[] content) {
        String token = tokenProvider.getGraphToken();

        String siteId = resolveSiteId(token);
        String driveId = resolveDriveId(token, siteId);
        String itemPath = (folderPath != null && !folderPath.isEmpty() ? folderPath + "/" : "") + fileName;

        String uploadUrl = GRAPH_BASE + "/sites/" + siteId + "/drives/" + driveId + "/root:/" + itemPath + ":/content";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        HttpEntity<byte[]> entity = new HttpEntity<>(content, headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(uploadUrl, HttpMethod.PUT, entity, JsonNode.class);

        if (response.getBody() == null || !response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("SharePoint upload failed");
        }

        String webUrl = response.getBody().path("webUrl").asText(null);
        String itemId = response.getBody().path("id").asText(null);
        log.info("Uploaded to SharePoint: {}", itemId);
        return itemId != null ? itemId : webUrl;
    }

    /**
     * Download file from SharePoint by drive item ID.
     */
    public byte[] downloadFile(String driveItemId) {
        String token = tokenProvider.getGraphToken();
        String siteId = resolveSiteId(token);
        String driveId = resolveDriveId(token, siteId);

        String downloadUrl = GRAPH_BASE + "/sites/" + siteId + "/drives/" + driveId + "/items/" + driveItemId + "/content";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                downloadUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                byte[].class
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("SharePoint download failed");
        }
        return response.getBody();
    }

    /**
     * Download file from SharePoint by URL.
     * Laravel: route('download.sharepoint.doc', ['url' => urlencode($onboardDocUrl)])
     * 
     * @param sharePointUrl Full SharePoint URL (e.g., "https://facilon.sharepoint.com/sites/CRM/ss_investordocuments/FolderName")
     * @return File bytes
     */
    public byte[] downloadFileByUrl(String sharePointUrl) {
        if (sharePointUrl == null || sharePointUrl.isEmpty()) {
            throw new IllegalArgumentException("SharePoint URL is required");
        }

        log.info("Downloading file from SharePoint URL: {}", sharePointUrl);

        String token = tokenProvider.getGraphToken();
        String siteId = resolveSiteId(token);
        String driveId = resolveDriveId(token, siteId);

        // Extract the path from the SharePoint URL
        // Example: https://facilon.sharepoint.com/sites/CRM/ss_investordocuments/Account Opening Booklet_ABC123
        // We need to get the relative path after the document library root
        String relativePath = extractRelativePath(sharePointUrl);
        
        if (relativePath == null || relativePath.isEmpty()) {
            throw new IllegalArgumentException("Could not extract valid path from SharePoint URL: " + sharePointUrl);
        }

        // Encode the path for Graph API
        String encodedPath = relativePath.replace(" ", "%20");
        
        // First, try to get the item to see if it's a folder or file
        String itemUrl = GRAPH_BASE + "/sites/" + siteId + "/drives/" + driveId + "/root:/" + encodedPath;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        
        try {
            // Get item metadata to check if it's a folder
            ResponseEntity<JsonNode> itemResponse = restTemplate.exchange(
                    itemUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    JsonNode.class
            );
            
            if (itemResponse.getBody() != null && itemResponse.getBody().has("folder")) {
                // It's a folder - get the first file in it
                String childrenUrl = itemUrl + ":/children";
                ResponseEntity<JsonNode> childrenResponse = restTemplate.exchange(
                        childrenUrl,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        JsonNode.class
                );
                
                if (childrenResponse.getBody() != null && childrenResponse.getBody().has("value")) {
                    JsonNode children = childrenResponse.getBody().path("value");
                    if (children.size() > 0) {
                        // Get the first file (not folder)
                        for (JsonNode child : children) {
                            if (!child.has("folder")) {
                                String itemId = child.path("id").asText();
                                log.info("Found file in folder, downloading item ID: {}", itemId);
                                return downloadFile(itemId);
                            }
                        }
                    }
                }
                throw new RuntimeException("No files found in SharePoint folder: " + sharePointUrl);
            }
            
            // It's a file - download its content
            String downloadUrl = itemUrl + ":/content";
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    downloadUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    byte[].class
            );
            
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("SharePoint download failed for URL: " + sharePointUrl);
            }
            
            log.info("Successfully downloaded file from SharePoint: {} bytes", response.getBody().length);
            return response.getBody();
            
        } catch (Exception e) {
            log.error("Failed to download from SharePoint URL {}: {}", sharePointUrl, e.getMessage());
            throw new RuntimeException("Failed to download file from SharePoint: " + e.getMessage());
        }
    }

    /**
     * Extract relative path from SharePoint URL.
     * Example: "https://facilon.sharepoint.com/sites/CRM/ss_investordocuments/Account Opening Booklet_ABC123"
     * Returns: "ss_investordocuments/Account Opening Booklet_ABC123"
     */
    private String extractRelativePath(String sharePointUrl) {
        if (sharePointUrl == null || !sharePointUrl.contains("://")) {
            return sharePointUrl;
        }

        try {
            // Find the position after "/sites/SiteName/"
            int sitesIndex = sharePointUrl.indexOf("/sites/");
            if (sitesIndex == -1) {
                // Try direct path
                int domainEnd = sharePointUrl.indexOf("/", 8); // Skip "https://"
                if (domainEnd != -1) {
                    return sharePointUrl.substring(domainEnd + 1);
                }
                return null;
            }

            // Get everything after "/sites/SiteName/"
            String afterSites = sharePointUrl.substring(sitesIndex + 7); // "/sites/"
            int nextSlash = afterSites.indexOf("/");
            if (nextSlash == -1) {
                return null;
            }

            // Return path after site name
            return afterSites.substring(nextSlash + 1);
        } catch (Exception e) {
            log.error("Error extracting path from SharePoint URL: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Delete file from SharePoint by drive item ID.
     */
    public void deleteFile(String driveItemId) {
        String token = tokenProvider.getGraphToken();
        String siteId = resolveSiteId(token);
        String driveId = resolveDriveId(token, siteId);

        String deleteUrl = GRAPH_BASE + "/sites/" + siteId + "/drives/" + driveId + "/items/" + driveItemId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        try {
            restTemplate.exchange(deleteUrl, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
            log.info("Deleted from SharePoint: {}", driveItemId);
        } catch (Exception e) {
            log.warn("SharePoint delete failed (item may already be gone): {}", e.getMessage());
        }
    }

    private String resolveSiteId(String token) {
        TenantSharePointConfig config = sharePointConfigService.getConfigForCurrentTenantOrDefault();
        if (config == null) {
            throw new RuntimeException("SharePoint configuration not found in database");
        }

        String hostname = config.getSiteHostname();
        String path = config.getSitePath() != null ? config.getSitePath() : "/sites/FacilonInvestor";

        if (hostname == null || hostname.isEmpty()) {
            throw new RuntimeException("SharePoint site hostname not configured in database");
        }

        String url = GRAPH_BASE + "/sites/" + hostname + ":" + path;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
        if (response.getBody() == null) {
            throw new RuntimeException("Failed to resolve SharePoint site");
        }
        return response.getBody().path("id").asText();
    }

    private String resolveDriveId(String token, String siteId) {
        String url = GRAPH_BASE + "/sites/" + siteId + "/drives";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
        if (response.getBody() == null) {
            throw new RuntimeException("Failed to resolve SharePoint drive");
        }

        JsonNode drives = response.getBody().path("value");
        if (drives.isEmpty()) {
            throw new RuntimeException("No drives found for SharePoint site");
        }

        // Use the default (first) document library
        return drives.get(0).path("id").asText();
    }

}
