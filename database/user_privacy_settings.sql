-- Add privacy settings columns to users table
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS email_notifications BOOLEAN DEFAULT TRUE,
ADD COLUMN IF NOT EXISTS show_sensitive_info BOOLEAN DEFAULT TRUE;

-- Update existing users to have default values
UPDATE users 
SET email_notifications = TRUE, show_sensitive_info = TRUE 
WHERE email_notifications IS NULL OR show_sensitive_info IS NULL;
