package com.facilon.app.module.dsr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One file inside a case's local evidence library. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DsrEvidenceFileDto {

    /** Evidence subfolder, e.g. "01_Request", "02_Acknowledgement", "09_Final_Response". */
    private String folder;
    private String name;
    /** Path relative to the case evidence folder, e.g. "01_Request/DSR-2026-000001_Request_Submission_2026-06-02.pdf". */
    private String relativePath;
    private long sizeBytes;
    private String lastModified;
}
