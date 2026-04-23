package com.facilon.app.integration.ocr;

/**
 * Thin client for the ORCReader microservice (§3.5 of KYC_DOCUMENT_PLAN.md).
 * ORCReader is the only OCR path for Facilon - there is no in-process OCR.
 */
public interface OcrClient {

    /**
     * Submit a file to ORCReader for field extraction.
     *
     * @param fileBytes           raw file bytes (PDF/JPG/PNG)
     * @param contentType         MIME type passed as the multipart part's Content-Type
     * @param filename            original filename (helps provider disambiguation)
     * @param expectedDocumentType hint like "PAN_CARD" / "PASSPORT"; currently unused by
     *                             ORCReader but reserved for future provider-specific prompting.
     * @return structured extraction result. Never null; on error {@link OcrExtractionResult#error()}
     *         is populated and {@link OcrExtractionResult#fields()} is empty.
     */
    OcrExtractionResult extract(byte[] fileBytes, String contentType, String filename,
                                String expectedDocumentType);
}
