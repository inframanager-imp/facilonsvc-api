-- Migration: Create tenant_graph_email_config table
-- Purpose: Store Microsoft Graph API email configuration per tenant

CREATE TABLE IF NOT EXISTS tenant_graph_email_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    tenant_id_azure VARCHAR(256) NOT NULL COMMENT 'Azure AD Tenant ID (GUID)',
    client_id VARCHAR(256) NOT NULL COMMENT 'Azure App Registration Client ID',
    client_secret TEXT NOT NULL COMMENT 'Azure App Registration Client Secret',
    sender_email VARCHAR(256) NOT NULL COMMENT 'Default sender email address (must be valid M365 mailbox)',
    sender_name VARCHAR(256) DEFAULT 'Facilon Platform' COMMENT 'Display name for sender',
    max_retries INT DEFAULT 3 COMMENT 'Number of retry attempts for failed sends',
    retry_delay_ms BIGINT DEFAULT 1000 COMMENT 'Delay between retry attempts in milliseconds',
    connection_timeout INT DEFAULT 30000 COMMENT 'Connection timeout in milliseconds',
    read_timeout INT DEFAULT 30000 COMMENT 'Read timeout in milliseconds',
    debug_mode BOOLEAN DEFAULT FALSE COMMENT 'Enable detailed logging',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    modified_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modified_by VARCHAR(255),
    CONSTRAINT fk_graph_email_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(tenant_id),
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Microsoft Graph API email configuration per tenant. Requires Azure AD App with Mail.Send permissions.';
