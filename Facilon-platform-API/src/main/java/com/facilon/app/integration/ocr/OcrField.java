package com.facilon.app.integration.ocr;

public record OcrField(
        String name,
        String value,
        double confidence,
        int page
) {
}
