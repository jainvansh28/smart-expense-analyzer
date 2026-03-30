-- Add anomaly detection columns to expenses table
ALTER TABLE expenses 
ADD COLUMN is_anomaly BOOLEAN DEFAULT FALSE,
ADD COLUMN anomaly_message VARCHAR(255);

-- Create index for faster anomaly queries
CREATE INDEX idx_expenses_anomaly ON expenses(is_anomaly);
