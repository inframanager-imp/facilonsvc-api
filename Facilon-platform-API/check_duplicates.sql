-- Check for duplicate delegations for the same investor-service agent pair
SELECT 
    d.id,
    d.investor_id,
    d.service_agent_id,
    sa.full_name as service_agent_name,
    sa.agent_code,
    d.status,
    d.is_active,
    d.valid_from,
    d.valid_to,
    d.created_at,
    d.modified_at
FROM investor_service_agent_delegations d
JOIN service_agents sa ON d.service_agent_id = sa.id
WHERE d.tenant_id = 1
ORDER BY d.investor_id, d.service_agent_id, d.created_at DESC;

-- Find specific duplicates (same investor + same service agent)
SELECT 
    investor_id,
    service_agent_id,
    COUNT(*) as count,
    GROUP_CONCAT(CONCAT('ID:', id, ' Status:', status, ' Active:', is_active) SEPARATOR ' | ') as delegations
FROM investor_service_agent_delegations
WHERE tenant_id = 1
GROUP BY investor_id, service_agent_id
HAVING COUNT(*) > 1;
