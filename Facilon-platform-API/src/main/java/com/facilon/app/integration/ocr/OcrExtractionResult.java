package com.facilon.app.integration.ocr;

import java.util.Collections;
import java.util.List;

/**
 * Response envelope from ORCReader's POST /api/kyc/extract.
 * Mirrors DocumentExtractionResponse (see ORCReader project).
 */
public record OcrExtractionResult(
        String runId,
        String detectedType,
        List<OcrField> fields,
        String modelVersion,
        Long processingTimeMs,
        String error
) {
    public static OcrExtractionResult failure(String error) {
        return new OcrExtractionResult(null, null, Collections.emptyList(), null, null, error);
    }

    public boolean isSuccess() {
        return error == null || error.isBlank();
    }
}
