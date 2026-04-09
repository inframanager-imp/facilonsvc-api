-- Remove document_library column from tenant_sharepoint_config
-- The system will always use the first/default document library on the SharePoint site

ALTER TABLE tenant_sharepoint_config
DROP COLUMN IF EXISTS document_library;
