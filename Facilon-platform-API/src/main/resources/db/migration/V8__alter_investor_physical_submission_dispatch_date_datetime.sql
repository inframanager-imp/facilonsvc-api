-- Widen dispatch_date from DATE to DATETIME so the time captured by the
-- HTML datetime-local input (yyyy-MM-dd'T'HH:mm) is preserved. Matches
-- Laravel's Y-m-d H:i:s behavior on the same column.
ALTER TABLE `investor_physical_submission`
    MODIFY COLUMN `dispatch_date` DATETIME NULL DEFAULT NULL;
