-- ============================================================================
-- ASSIGN USER TO SERVICE_AGENT ROLE
-- ============================================================================

-- Step 1: Find your user
SELECT 'Finding your user...' AS step;
SELECT 
    id AS user_id,
    email_id,
    login_id,
    first_name,
    last_name
FROM authorized_user
WHERE email_id = 'YOUR_EMAIL@example.com'  -- REPLACE WITH YOUR EMAIL
   OR login_id = 'YOUR_LOGIN_ID';          -- OR REPLACE WITH YOUR LOGIN ID
   
-- Step 2: Check current role assignments for this user
SELECT 'Current role assignments for this user...' AS step;
SELECT 
    au.id AS user_id,
    au.email_id,
    ug.group_name,
    rl.label AS role_name,
    al.authority_name
FROM authorized_user au
LEFT JOIN user_group_mapping ugm ON au.id = ugm.authorized_user_id
LEFT JOIN user_group ug ON ugm.group_id = ug.group_id
LEFT JOIN group_role gr ON ug.group_id = gr.group_id
LEFT JOIN role_list rl ON gr.role_list_id = rl.role_list_id
LEFT JOIN role_authority ra ON rl.role_list_id = ra.role_list_id
LEFT JOIN authority_list al ON ra.authority_list_id = al.authority_list_id
WHERE au.email_id = 'YOUR_EMAIL@example.com'  -- REPLACE WITH YOUR EMAIL
ORDER BY ug.group_name, al.authority_name;

-- ============================================================================
-- ASSIGN USER TO SERVICE_AGENT GROUP
-- Replace <USER_ID> with the ID from Step 1
-- ============================================================================

INSERT INTO user_group_mapping (authorized_user_id, group_id)
VALUES (
    <USER_ID>,  -- REPLACE with your user ID from Step 1
    (SELECT group_id FROM user_group WHERE group_name = 'SERVICE_AGENT')
)
ON DUPLICATE KEY UPDATE authorized_user_id = authorized_user_id;

-- ============================================================================
-- VERIFICATION: Check if user now has SERVICE_AGENT role
-- ============================================================================

SELECT 'Verifying assignment...' AS step;
SELECT 
    au.id AS user_id,
    au.email_id,
    ug.group_name,
    rl.label AS role_name,
    COUNT(DISTINCT al.authority_name) AS authority_count
FROM authorized_user au
JOIN user_group_mapping ugm ON au.id = ugm.authorized_user_id
JOIN user_group ug ON ugm.group_id = ug.group_id
JOIN group_role gr ON ug.group_id = gr.group_id
JOIN role_list rl ON gr.role_list_id = rl.role_list_id
LEFT JOIN role_authority ra ON rl.role_list_id = ra.role_list_id
LEFT JOIN authority_list al ON ra.authority_list_id = al.authority_list_id
WHERE au.id = <USER_ID>  -- REPLACE with your user ID
GROUP BY au.id, au.email_id, ug.group_name, rl.label;

-- If the above shows SERVICE_AGENT role with 7+ authorities, you're ready!
-- Now RE-LOGIN to get a fresh JWT token with the new authorities.
