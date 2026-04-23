package com.facilon.app.module.client.model;

public enum KycValidationStatus {
    PENDING,
    VALID,
    DISCREPANCY,
    EXPIRED,
    EXPIRES_SOON,
    OCR_FAILED,
    NOT_UPLOADED,
    PENDING_REVIEW;

    public String asDbString() {
        return name();
    }
}
