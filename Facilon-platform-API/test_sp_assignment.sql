-- TEST SP ASSIGNMENT FLOW
-- This simulates what happens when SP assigns a Service Agent to an Investor

-- Step 1: Find your investor ID
SELECT investor_id, CONCAT(first_name, ' ', last_name) as name, email, investor_unique_code
FROM investor
WHERE email = 'YOUR_INVESTOR_EMAIL@example.com'  -- Replace with actual investor email
  AND tenant_id = 1;

-- Step 2: Find the Service Agent ID
SELECT id, full_name, email, agent_code
FROM service_agents
WHERE email = 'YOUR_SERVICE_AGENT_EMAIL@example.com'  -- Replace with actual SA email
  AND tenant_id = 1;

-- Step 3: Create a PENDING delegation (simulating SP assignment)
-- Replace the IDs below with actual values from Steps 1 & 2
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
    status,
    assigned_by_sp_id,
    notes,
    tenant_id,
    created_by,
    created_at
) VALUES (
    123,                        -- Replace with investor_id from Step 1
    456,                        -- Replace with service_agent.id from Step 2
    'FULL_ONBOARDING',          -- Scope
    1,                          -- can_view_profile = true
    1,                          -- can_edit_kyc = true
    1,                          -- can_upload_documents = true
    1,                          -- can_submit_forms = true
    CURDATE(),                  -- valid_from = today
    DATE_ADD(CURDATE(), INTERVAL 6 MONTH),  -- valid_to = 6 months from now
    0,                          -- is_active = false (will be true after investor accepts)
    'PENDING',                  -- status = PENDING (waiting for investor consent)
    999,                        -- assigned_by_sp_id (SP user ID who made the assignment)
    'Assigned by SP for onboarding assistance',
    1,                          -- tenant_id
    'SYSTEM',                   -- created_by
    NOW()                       -- created_at
);

-- Step 4: Verify the delegation was created
SELECT 
    d.id,
    d.status,
    d.is_active,
    i.email as investor_email,
    sa.full_name as service_agent_name,
    d.scope,
    d.valid_from,
    d.valid_to
FROM investor_service_agent_delegations d
JOIN investor i ON d.investor_id = i.investor_id
JOIN service_agents sa ON d.service_agent_id = sa.id
WHERE d.status = 'PENDING'
  AND d.tenant_id = 1
ORDER BY d.created_at DESC
LIMIT 5;

-- Expected Result:
-- - Delegation created with status = 'PENDING' and is_active = 0
-- - Investor will see this in their dashboard as a pending assignment
-- - Service Agent will NOT see this investor yet (only after investor accepts)

-- =======================
-- WHAT HAPPENS NEXT:
-- =======================
-- 1. Investor logs in to http://localhost:3000/investor/dashboard
-- 2. Investor sees YELLOW WARNING BANNER at top with SA details
-- 3. Investor clicks "Accept & Give Consent"
-- 4. Frontend calls: POST /api/clients/me/delegations/{id}/accept
-- 5. Backend updates: status='ACTIVE', is_active=1, consent_given_at=NOW()
-- 6. Service Agent logs in and sees investor in their dashboard immediately!
