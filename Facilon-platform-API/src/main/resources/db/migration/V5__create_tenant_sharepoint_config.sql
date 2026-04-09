-- Tenant-specific SharePoint configuration
-- Stores OAuth credentials and site details for SharePoint document operations per tenant

CREATE TABLE IF NOT EXISTS tenant_sharepoint_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL UNIQUE,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- SharePoint site details
    site_hostname VARCHAR(512) COMMENT 'SharePoint site hostname (e.g., yourtenant.sharepoint.com)',
    site_path VARCHAR(512) DEFAULT '/sites/FacilonInvestor' COMMENT 'SharePoint site path',
    document_library VARCHAR(256) COMMENT 'Document library name (e.g., Documents)',
    
    -- Azure AD credentials for Graph API
    tenant_id_azure VARCHAR(256) COMMENT 'Azure tenant ID (GUID)',
    client_id VARCHAR(256) COMMENT 'Azure AD application (client) ID',
    client_secret TEXT COMMENT 'Azure AD application client secret',
    
    -- Audit fields
    created_by VARCHAR(256),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(256),
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign key constraint
    CONSTRAINT fk_tenant_sharepoint_tenant 
        FOREIGN KEY (tenant_id) REFERENCES tenant(tenant_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Tenant-specific SharePoint document storage configuration';

-- Index for faster tenant lookups
CREATE INDEX idx_tenant_sharepoint_tenant_id ON tenant_sharepoint_config(tenant_id);
CREATE INDEX idx_tenant_sharepoint_enabled ON tenant_sharepoint_config(enabled);
