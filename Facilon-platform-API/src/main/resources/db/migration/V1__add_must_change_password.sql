-- Add must_change_password column to authorized_user table
-- This column tracks if user needs to change their password (e.g., after receiving temporary password)

ALTER TABLE authorized_user 
ADD COLUMN must_change_password BOOLEAN DEFAULT FALSE;

-- Update existing users with temporary passwords if needed
-- COMMENT: This is a placeholder - adjust based on your logic to identify users with temporary passwords
-- UPDATE authorized_user SET must_change_password = FALSE WHERE must_change_password IS NULL;
