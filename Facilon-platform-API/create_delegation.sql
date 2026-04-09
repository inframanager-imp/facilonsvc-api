-- ============================================================================
-- CREATE DELEGATION (Assign Investor to Service Agent)
-- ============================================================================
-- This is how an investor "assigns" a service agent to help them.
-- The investor grants permission for the SA to access their data.
-- ============================================================================

-- Step 1: Find the Service Agent
SELECT 'Finding Service Agent...' AS step;
SELECT 
    sa.id AS service_agent_id,
    sa.agent_code,
    sa.full_name,
    sa.email,
    au.email_id AS user_email
FROM service_agents sa
JOIN authorized_user au ON sa.authorized_user_id = au.id
WHERE sa.agent_code = 'SA-0001'  -- REPLACE with your agent code
   OR sa.email = 'agent@example.com';  -- OR agent email

-- Step 2: Find the Investor
SELECT 'Finding Investor...' AS step;
SELECT 
    i.investor_id,
    i.email,
    i.first_name,
    i.last_name,
    au.id AS authorized_user_id
FROM investor i
LEFT JOIN authorized_user au ON au.email_id = i.email
WHERE i.email = 'investor@example.com'  -- REPLACE with investor email
   OR i.investor_id = <INVESTOR_ID>;    -- OR investor ID

-- ============================================================================
-- CREATE THE DELEGATION
-- Replace values with IDs from Steps 1 and 2
-- ============================================================================

INSERT INTO investor_service_agent_delegations (
    investor_id,
    service_agent_id,
    scope,
    can_view_profile,
    can_edit_kyc,
    can_upload_documents,
    can_submit_forms,
    valid_from,
    valid_to,
    is_active,
    granted_at,
    granted_by,
    tenant_id,
    created_at,
    created_by
)
VALUES (
    <INVESTOR_ID>,           -- From Step 2
    <SERVICE_AGENT_ID>,      -- From Step 1
    'FULL_ONBOARDING',       -- Options: CKYC_ONLY, ONBOARDING_ONLY, FULL_ONBOARDING, VIEW_ONLY
    true,                    -- can_view_profile
    true,                    -- can_edit_kyc
    true,                    -- can_upload_documents
    true,                    -- can_submit_forms
    NOW(),                   -- valid_from
    '2026-12-31 23:59:59',   -- valid_to
    true,                    -- is_active
    NOW(),                   -- granted_at
    'INVESTOR',              -- granted_by
    1,                       -- tenant_id
    NOW(),                   -- created_at
    'SYSTEM'                 -- created_by
);

-- ============================================================================
-- VERIFY THE DELEGATION
-- ============================================================================

SELECT 'Verifying delegation...' AS step;
SELECT 
    d.delegation_id,
    i.email AS investor_email,
    i.first_name AS investor_name,
    sa.agent_code,
    sa.full_name AS agent_name,
    d.scope,
    d.can_view_profile,
    d.can_edit_kyc,
    d.can_upload_documents,
    d.can_submit_forms,
    d.valid_from,
    d.valid_to,
    d.is_active
FROM investor_service_agent_delegations d
JOIN investor i ON d.investor_id = i.investor_id
JOIN service_agents sa ON d.service_agent_id = sa.id
WHERE d.is_active = true
ORDER BY d.created_at DESC;

-- ============================================================================
-- SCOPE EXPLANATION:
-- ============================================================================
-- CKYC_ONLY        → Agent can only access: KYC, Personal Info, Tax Info
-- ONBOARDING_ONLY  → Agent can only access: Bank, Nomination, Risk Profile, Onboarding
-- FULL_ONBOARDING  → Agent can access: ALL modules
-- VIEW_ONLY        → Agent can access: ALL modules but READ-ONLY (no edits)
-- ============================================================================
