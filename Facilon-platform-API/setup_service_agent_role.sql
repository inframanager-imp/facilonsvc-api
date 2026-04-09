-- ============================================================================
-- SERVICE AGENT ROLE SETUP & VERIFICATION SCRIPT
-- ============================================================================

-- Step 1: Verify SERVICE_AGENT group exists
SELECT 'Checking SERVICE_AGENT group...' AS step;
SELECT * FROM user_group WHERE group_name = 'SERVICE_AGENT';

-- Step 2: Verify SERVICE_AGENT role exists
SELECT 'Checking SERVICE_AGENT role...' AS step;
SELECT * FROM role_list WHERE label = 'SERVICE_AGENT';

-- Step 3: Verify group-to-role link exists
SELECT 'Checking group-role mapping...' AS step;
SELECT 
    ug.group_id,
    ug.group_name,
    rl.role_list_id,
    rl.label AS role_name
FROM user_group ug
JOIN group_role gr ON ug.group_id = gr.group_id
JOIN role_list rl ON gr.role_list_id = rl.role_list_id
WHERE ug.group_name = 'SERVICE_AGENT';

-- Step 4: Verify authorities exist
SELECT 'Checking SERVICE_AGENT authorities...' AS step;
SELECT authority_list_id, authority_name, description 
FROM authority_list 
WHERE authority_name LIKE 'SA_%'
ORDER BY authority_name;

-- Step 5: Verify role-to-authority mappings
SELECT 'Checking role-authority mappings...' AS step;
SELECT 
    rl.label AS role_name,
    al.authority_name,
    al.description
FROM role_list rl
JOIN role_authority ra ON rl.role_list_id = ra.role_list_id
JOIN authority_list al ON ra.authority_list_id = al.authority_list_id
WHERE rl.label = 'SERVICE_AGENT'
ORDER BY al.authority_name;

-- ============================================================================
-- If any of the above checks are EMPTY, run the setup below:
-- ============================================================================

-- CREATE SERVICE_AGENT GROUP (if doesn't exist)
INSERT IGNORE INTO user_group (group_name, description, is_active, tenant_id, created_at, created_by)
VALUES ('SERVICE_AGENT', 'Service Agent / Client Executive', true, 1, NOW(), 'SYSTEM');

-- CREATE SERVICE_AGENT ROLE (if doesn't exist)
INSERT IGNORE INTO role_list (label, is_active, tenant_id, created_at, created_by)
VALUES ('SERVICE_AGENT', true, 1, NOW(), 'SYSTEM');

-- LINK GROUP TO ROLE (if doesn't exist)
INSERT IGNORE INTO group_role (group_id, role_list_id)
SELECT 
    (SELECT group_id FROM user_group WHERE group_name = 'SERVICE_AGENT'),
    (SELECT role_list_id FROM role_list WHERE label = 'SERVICE_AGENT')
WHERE NOT EXISTS (
    SELECT 1 FROM group_role gr
    WHERE gr.group_id = (SELECT group_id FROM user_group WHERE group_name = 'SERVICE_AGENT')
    AND gr.role_list_id = (SELECT role_list_id FROM role_list WHERE label = 'SERVICE_AGENT')
);

-- LINK ROLE TO AUTHORITIES (if not already linked)
INSERT IGNORE INTO role_authority (role_list_id, authority_list_id)
SELECT 
    (SELECT role_list_id FROM role_list WHERE label = 'SERVICE_AGENT'),
    al.authority_list_id
FROM authority_list al
WHERE al.authority_name LIKE 'SA_%'
AND NOT EXISTS (
    SELECT 1 FROM role_authority ra
    WHERE ra.role_list_id = (SELECT role_list_id FROM role_list WHERE label = 'SERVICE_AGENT')
    AND ra.authority_list_id = al.authority_list_id
);

-- ============================================================================
-- VERIFICATION COMPLETE
-- Now check your user assignment below:
-- ============================================================================
