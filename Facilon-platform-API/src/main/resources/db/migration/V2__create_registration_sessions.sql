-- Create registration_sessions table for tracking public investor registration flow
CREATE TABLE IF NOT EXISTS registration_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    unique_code VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    register_as INT NOT NULL COMMENT '1=Individual, 2=Legal Entity',
    otp_verified BOOLEAN NOT NULL DEFAULT FALSE,
    consent_given BOOLEAN NOT NULL DEFAULT FALSE,
    registration_completed BOOLEAN NOT NULL DEFAULT FALSE,
    current_step INT COMMENT '1=Email/OTP, 2=Details, 3=Completed',
    expires_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    tenant_id VARCHAR(50) NOT NULL,
    INDEX idx_unique_code (unique_code),
    INDEX idx_email (email),
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_registration_completed (registration_completed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Tracks temporary registration sessions for public investor onboarding';
