package com.facilon.app.module.dsr.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * Phase-1 local Evidence Library. Creates, per DSR case, the same folder skeleton the
 * SOP defines for SharePoint - but on the local filesystem under {@code app.dsr.upload-dir}.
 * Paths map 1:1 to a SharePoint library when that is adopted in a later phase.
 *
 * <pre>
 * &lt;root&gt;/Data Subject Requests/&lt;year&gt;/&lt;caseId&gt;/
 *     01_Request 02_Acknowledgement 03_Verification 04_Controller_Processor_Assessment
 *     05_Data_Search_Notes 06_Service_Provider_Correspondence 07_Legal_Retention_Review
 *     08_Response_Approval 09_Final_Response 10_Closure_Note
 * </pre>
 */
@Service
@Slf4j
public class DsrEvidenceService {

    public static final String SUB_REQUEST = "01_Request";
    public static final String SUB_ACKNOWLEDGEMENT = "02_Acknowledgement";
    public static final String SUB_FINAL_RESPONSE = "09_Final_Response";

    private static final List<String> SUBFOLDERS = List.of(
            SUB_REQUEST,
            "02_Acknowledgement",
            "03_Verification",
            "04_Controller_Processor_Assessment",
            "05_Data_Search_Notes",
            "06_Service_Provider_Correspondence",
            "07_Legal_Retention_Review",
            "08_Response_Approval",
            SUB_FINAL_RESPONSE,
            "10_Closure_Note"
    );

    @Value("${app.dsr.upload-dir:uploads/dsr}")
    private String dsrUploadDir;

    /**
     * Creates the full evidence skeleton for a case and returns the case folder path
     * (the {@code <caseId>} directory). Best-effort: never throws.
     */
    public String createCaseFolder(String caseId) {
        try {
            Path caseFolder = caseFolder(caseId);
            for (String sub : SUBFOLDERS) {
                Files.createDirectories(caseFolder.resolve(sub));
            }
            return normalize(caseFolder);
        } catch (IOException e) {
            log.error("Failed creating evidence folder for case {}", caseId, e);
            return null;
        }
    }

    /**
     * Stores an uploaded file into a case subfolder (e.g. {@code 01_Request},
     * {@code 09_Final_Response}) and returns the stored path.
     */
    public String storeFile(String caseId, String subFolder, String fileName, MultipartFile file) {
        try {
            Path target = caseFolder(caseId).resolve(subFolder);
            Files.createDirectories(target);
            Path dest = target.resolve(sanitize(fileName));
            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
            return normalize(dest);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store evidence file for case " + caseId, e);
        }
    }

    /** Writes raw bytes (e.g. a generated PDF) into a case subfolder; returns the stored path. */
    public String storeBytes(String caseId, String subFolder, String fileName, byte[] data) {
        try {
            Path target = caseFolder(caseId).resolve(subFolder);
            Files.createDirectories(target);
            Path dest = target.resolve(sanitize(fileName));
            Files.write(dest, data);
            return normalize(dest);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store evidence bytes for case " + caseId, e);
        }
    }

    private Path caseFolder(String caseId) {
        String year = String.valueOf(LocalDate.now().getYear());
        return Paths.get(dsrUploadDir, "Data Subject Requests", year, caseId);
    }

    private String sanitize(String name) {
        String safe = (name == null || name.isBlank()) ? "file" : name;
        return safe.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private String normalize(Path p) {
        return p.toString().replace("\\", "/");
    }

    public String getExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int idx = fileName.lastIndexOf('.');
        if (idx < 0 || idx == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(idx + 1).toLowerCase(Locale.ROOT);
    }
}
