-- CLEANUP: Delete old REVOKED/REJECTED/EXPIRED delegations
-- This keeps only PENDING and ACTIVE delegations in the system

-- Preview what will be deleted (run this first to be safe)
SELECT 
    d.id,
    sa.full_name as service_agent_name,
    d.status,
    d.is_active,
    d.revoked_at,
    d.created_at
FROM investor_service_agent_delegations d
JOIN service_agents sa ON d.service_agent_id = sa.id
WHERE d.tenant_id = 1
  AND (
    d.status IN ('REVOKED', 'REJECTED') OR
    (d.is_active = 0 AND d.status != 'PENDING')
  )
ORDER BY d.created_at DESC;

-- Uncomment to execute cleanup (only after reviewing above)
-- DELETE FROM investor_service_agent_delegations
-- WHERE tenant_id = 1
--   AND (
--     status IN ('REVOKED', 'REJECTED') OR
--     (is_active = 0 AND status != 'PENDING')
--   );
