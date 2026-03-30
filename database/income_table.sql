-- Income table for tracking salary and extra income
CREATE TABLE income (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    type VARCHAR(20) NOT NULL, -- 'salary' or 'extra'
    description TEXT,
    date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_date (user_id, date),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Add some sample income data
INSERT INTO income (user_id, amount, type, description, date) VALUES
(1, 50000.00, 'salary', 'Monthly Salary - January', '2024-01-01'),
(1, 5000.00, 'extra', 'Freelance Project', '2024-01-15'),
(1, 50000.00, 'salary', 'Monthly Salary - February', '2024-02-01'),
(1, 3000.00, 'extra', 'Bonus', '2024-02-10'),
(1, 50000.00, 'salary', 'Monthly Salary - March', '2024-03-01');
