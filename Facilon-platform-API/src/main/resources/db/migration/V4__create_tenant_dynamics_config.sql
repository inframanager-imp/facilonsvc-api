-- Tenant-specific Dynamics 365 (Dataverse) configuration
-- Stores OAuth credentials for Dynamics CRM API access per tenant

CREATE TABLE IF NOT EXISTS tenant_dynamics_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL UNIQUE,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Azure AD credentials for Dynamics API
    tenant_id_azure VARCHAR(256) COMMENT 'Azure tenant ID (GUID)',
    client_id VARCHAR(256) COMMENT 'Azure AD application (client) ID',
    client_secret TEXT COMMENT 'Azure AD application client secret',
    
    -- Dynamics instance URL
    resource VARCHAR(512) COMMENT 'Dynamics resource URL (e.g., https://orgname.crm8.dynamics.com)',
    
    -- Audit fields
    created_by VARCHAR(256),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(256),
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign key constraint
    CONSTRAINT fk_tenant_dynamics_tenant 
        FOREIGN KEY (tenant_id) REFERENCES tenant(tenant_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Tenant-specific Dynamics 365 (Dataverse) API configuration';

-- Index for faster tenant lookups
CREATE INDEX idx_tenant_dynamics_tenant_id ON tenant_dynamics_config(tenant_id);
CREATE INDEX idx_tenant_dynamics_enabled ON tenant_dynamics_config(enabled);
