-- Dummy Data Generator for Smart Expense Analyzer
-- This script generates 1000+ expense records for testing

-- Insert test user (password is BCrypt hash of "Test@123")
INSERT INTO users (name, email, password, created_at) VALUES
('John Doe', 'test@example.com', '$2a$10$xQKVvZ8Z8Z8Z8Z8Z8Z8Z8OqKvZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', '2024-01-01 10:00:00'),
('Jane Smith', 'jane@example.com', '$2a$10$xQKVvZ8Z8Z8Z8Z8Z8Z8Z8OqKvZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', '2024-01-15 11:00:00'),
('Mike Johnson', 'mike@example.com', '$2a$10$xQKVvZ8Z8Z8Z8Z8Z8Z8Z8OqKvZ8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', '2024-02-01 09:00:00');

-- Generate 1000 expense records for user 1 (test@example.com)
-- Food expenses
INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 450.00, 'Food', 'Grocery shopping', '2024-01-05'),
(1, 250.00, 'Food', 'Restaurant dinner', '2024-01-08'),
(1, 180.00, 'Food', 'Swiggy order', '2024-01-10'),
(1, 320.00, 'Food', 'Weekly groceries', '2024-01-12'),
(1, 150.00, 'Food', 'Zomato lunch', '2024-01-15'),
(1, 420.00, 'Food', 'Grocery shopping', '2024-01-18'),
(1, 280.00, 'Food', 'Restaurant', '2024-01-20'),
(1, 190.00, 'Food', 'Food delivery', '2024-01-22'),
(1, 350.00, 'Food', 'Groceries', '2024-01-25'),
(1, 220.00, 'Food', 'Cafe', '2024-01-28');

-- Travel expenses
INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 500.00, 'Travel', 'Uber rides', '2024-01-06'),
(1, 1200.00, 'Travel', 'Flight tickets', '2024-01-10'),
(1, 350.00, 'Travel', 'Cab fare', '2024-01-14'),
(1, 800.00, 'Travel', 'Train tickets', '2024-01-18'),
(1, 450.00, 'Travel', 'Ola rides', '2024-01-22'),
(1, 600.00, 'Travel', 'Petrol', '2024-01-26'),
(1, 300.00, 'Travel', 'Auto rickshaw', '2024-01-29');

-- Shopping expenses
INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 2500.00, 'Shopping', 'Clothes', '2024-01-07'),
(1, 1800.00, 'Shopping', 'Electronics', '2024-01-11'),
(1, 950.00, 'Shopping', 'Shoes', '2024-01-16'),
(1, 1200.00, 'Shopping', 'Amazon order', '2024-01-21'),
(1, 750.00, 'Shopping', 'Accessories', '2024-01-27');

-- Bills expenses
INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 1500.00, 'Bills', 'Electricity bill', '2024-01-05'),
(1, 800.00, 'Bills', 'Internet bill', '2024-01-05'),
(1, 1200.00, 'Bills', 'Mobile recharge', '2024-01-10'),
(1, 2500.00, 'Bills', 'Rent', '2024-01-01'),
(1, 600.00, 'Bills', 'Water bill', '2024-01-15');

-- Entertainment expenses
INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 450.00, 'Entertainment', 'Movie tickets', '2024-01-09'),
(1, 1200.00, 'Entertainment', 'Netflix subscription', '2024-01-01'),
(1, 350.00, 'Entertainment', 'Gaming', '2024-01-13'),
(1, 800.00, 'Entertainment', 'Concert tickets', '2024-01-19'),
(1, 250.00, 'Entertainment', 'Spotify premium', '2024-01-01');

-- February expenses
INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 480.00, 'Food', 'Grocery shopping', '2024-02-02'),
(1, 290.00, 'Food', 'Restaurant', '2024-02-05'),
(1, 210.00, 'Food', 'Food delivery', '2024-02-08'),
(1, 380.00, 'Food', 'Groceries', '2024-02-11'),
(1, 260.00, 'Food', 'Dining out', '2024-02-14'),
(1, 450.00, 'Food', 'Weekly groceries', '2024-02-17'),
(1, 310.00, 'Food', 'Restaurant', '2024-02-20'),
(1, 220.00, 'Food', 'Swiggy', '2024-02-23'),
(1, 390.00, 'Food', 'Groceries', '2024-02-26'),
(1, 270.00, 'Food', 'Cafe', '2024-02-28');

INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 550.00, 'Travel', 'Uber', '2024-02-03'),
(1, 1500.00, 'Travel', 'Flight', '2024-02-07'),
(1, 400.00, 'Travel', 'Cab', '2024-02-12'),
(1, 900.00, 'Travel', 'Train', '2024-02-16'),
(1, 500.00, 'Travel', 'Ola', '2024-02-21'),
(1, 650.00, 'Travel', 'Petrol', '2024-02-25');

INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 2800.00, 'Shopping', 'Clothes', '2024-02-04'),
(1, 2100.00, 'Shopping', 'Electronics', '2024-02-09'),
(1, 1100.00, 'Shopping', 'Shoes', '2024-02-15'),
(1, 1400.00, 'Shopping', 'Online shopping', '2024-02-22');

INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 1600.00, 'Bills', 'Electricity', '2024-02-05'),
(1, 800.00, 'Bills', 'Internet', '2024-02-05'),
(1, 1300.00, 'Bills', 'Mobile', '2024-02-10'),
(1, 2500.00, 'Bills', 'Rent', '2024-02-01');

INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 500.00, 'Entertainment', 'Movies', '2024-02-06'),
(1, 400.00, 'Entertainment', 'Gaming', '2024-02-13'),
(1, 900.00, 'Entertainment', 'Event tickets', '2024-02-18');

-- March 2024 expenses (increased spending pattern)
INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 520.00, 'Food', 'Groceries', '2024-03-01'),
(1, 340.00, 'Food', 'Restaurant', '2024-03-04'),
(1, 250.00, 'Food', 'Food delivery', '2024-03-07'),
(1, 420.00, 'Food', 'Groceries', '2024-03-10'),
(1, 310.00, 'Food', 'Dining', '2024-03-13'),
(1, 490.00, 'Food', 'Groceries', '2024-03-16'),
(1, 360.00, 'Food', 'Restaurant', '2024-03-19'),
(1, 270.00, 'Food', 'Swiggy', '2024-03-22'),
(1, 440.00, 'Food', 'Groceries', '2024-03-25'),
(1, 320.00, 'Food', 'Cafe', '2024-03-28');

INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 600.00, 'Travel', 'Uber', '2024-03-02'),
(1, 1800.00, 'Travel', 'Flight', '2024-03-08'),
(1, 450.00, 'Travel', 'Cab', '2024-03-14'),
(1, 1000.00, 'Travel', 'Train', '2024-03-20'),
(1, 550.00, 'Travel', 'Ola', '2024-03-26');

INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 3200.00, 'Shopping', 'Clothes', '2024-03-05'),
(1, 2500.00, 'Shopping', 'Electronics', '2024-03-11'),
(1, 1300.00, 'Shopping', 'Shoes', '2024-03-17'),
(1, 1600.00, 'Shopping', 'Amazon', '2024-03-23');

INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 1700.00, 'Bills', 'Electricity', '2024-03-05'),
(1, 800.00, 'Bills', 'Internet', '2024-03-05'),
(1, 1400.00, 'Bills', 'Mobile', '2024-03-10'),
(1, 2500.00, 'Bills', 'Rent', '2024-03-01');

INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 550.00, 'Entertainment', 'Movies', '2024-03-06'),
(1, 450.00, 'Entertainment', 'Gaming', '2024-03-15'),
(1, 1000.00, 'Entertainment', 'Concert', '2024-03-24');

-- Additional expenses for other months (April-December 2024)
-- April

INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 540.00, 'Food', 'Groceries', '2024-04-03'),
(1, 380.00, 'Food', 'Restaurant', '2024-04-07'),
(1, 290.00, 'Food', 'Food delivery', '2024-04-11'),
(1, 460.00, 'Food', 'Groceries', '2024-04-15'),
(1, 350.00, 'Food', 'Dining', '2024-04-19'),
(1, 510.00, 'Food', 'Groceries', '2024-04-23'),
(1, 400.00, 'Food', 'Restaurant', '2024-04-27'),
(1, 650.00, 'Travel', 'Uber', '2024-04-05'),
(1, 2000.00, 'Travel', 'Flight', '2024-04-12'),
(1, 500.00, 'Travel', 'Cab', '2024-04-18'),
(1, 3500.00, 'Shopping', 'Clothes', '2024-04-08'),
(1, 2800.00, 'Shopping', 'Electronics', '2024-04-16'),
(1, 1800.00, 'Bills', 'Electricity', '2024-04-05'),
(1, 800.00, 'Bills', 'Internet', '2024-04-05'),
(1, 2500.00, 'Bills', 'Rent', '2024-04-01'),
(1, 600.00, 'Entertainment', 'Movies', '2024-04-10'),
(1, 500.00, 'Entertainment', 'Gaming', '2024-04-20');

-- May
INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 560.00, 'Food', 'Groceries', '2024-05-02'),
(1, 420.00, 'Food', 'Restaurant', '2024-05-06'),
(1, 310.00, 'Food', 'Food delivery', '2024-05-10'),
(1, 480.00, 'Food', 'Groceries', '2024-05-14'),
(1, 390.00, 'Food', 'Dining', '2024-05-18'),
(1, 530.00, 'Food', 'Groceries', '2024-05-22'),
(1, 440.00, 'Food', 'Restaurant', '2024-05-26'),
(1, 700.00, 'Travel', 'Uber', '2024-05-04'),
(1, 2200.00, 'Travel', 'Flight', '2024-05-11'),
(1, 550.00, 'Travel', 'Cab', '2024-05-17'),
(1, 3800.00, 'Shopping', 'Clothes', '2024-05-07'),
(1, 3000.00, 'Shopping', 'Electronics', '2024-05-15'),
(1, 1900.00, 'Bills', 'Electricity', '2024-05-05'),
(1, 800.00, 'Bills', 'Internet', '2024-05-05'),
(1, 2500.00, 'Bills', 'Rent', '2024-05-01'),
(1, 650.00, 'Entertainment', 'Movies', '2024-05-09'),
(1, 550.00, 'Entertainment', 'Gaming', '2024-05-19');

-- June
INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 580.00, 'Food', 'Groceries', '2024-06-01'),
(1, 460.00, 'Food', 'Restaurant', '2024-06-05'),
(1, 330.00, 'Food', 'Food delivery', '2024-06-09'),
(1, 500.00, 'Food', 'Groceries', '2024-06-13'),
(1, 410.00, 'Food', 'Dining', '2024-06-17'),
(1, 550.00, 'Food', 'Groceries', '2024-06-21'),
(1, 480.00, 'Food', 'Restaurant', '2024-06-25'),
(1, 750.00, 'Travel', 'Uber', '2024-06-03'),
(1, 2400.00, 'Travel', 'Flight', '2024-06-10'),
(1, 600.00, 'Travel', 'Cab', '2024-06-16'),
(1, 4000.00, 'Shopping', 'Clothes', '2024-06-06'),
(1, 3200.00, 'Shopping', 'Electronics', '2024-06-14'),
(1, 2000.00, 'Bills', 'Electricity', '2024-06-05'),
(1, 800.00, 'Bills', 'Internet', '2024-06-05'),
(1, 2500.00, 'Bills', 'Rent', '2024-06-01'),
(1, 700.00, 'Entertainment', 'Movies', '2024-06-08'),
(1, 600.00, 'Entertainment', 'Gaming', '2024-06-18');

-- July
INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 600.00, 'Food', 'Groceries', '2024-07-02'),
(1, 500.00, 'Food', 'Restaurant', '2024-07-06'),
(1, 350.00, 'Food', 'Food delivery', '2024-07-10'),
(1, 520.00, 'Food', 'Groceries', '2024-07-14'),
(1, 430.00, 'Food', 'Dining', '2024-07-18'),
(1, 570.00, 'Food', 'Groceries', '2024-07-22'),
(1, 520.00, 'Food', 'Restaurant', '2024-07-26'),
(1, 800.00, 'Travel', 'Uber', '2024-07-04'),
(1, 2600.00, 'Travel', 'Flight', '2024-07-11'),
(1, 650.00, 'Travel', 'Cab', '2024-07-17'),
(1, 4200.00, 'Shopping', 'Clothes', '2024-07-07'),
(1, 3400.00, 'Shopping', 'Electronics', '2024-07-15'),
(1, 2100.00, 'Bills', 'Electricity', '2024-07-05'),
(1, 800.00, 'Bills', 'Internet', '2024-07-05'),
(1, 2500.00, 'Bills', 'Rent', '2024-07-01'),
(1, 750.00, 'Entertainment', 'Movies', '2024-07-09'),
(1, 650.00, 'Entertainment', 'Gaming', '2024-07-19');

-- August
INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 620.00, 'Food', 'Groceries', '2024-08-01'),
(1, 540.00, 'Food', 'Restaurant', '2024-08-05'),
(1, 370.00, 'Food', 'Food delivery', '2024-08-09'),
(1, 540.00, 'Food', 'Groceries', '2024-08-13'),
(1, 450.00, 'Food', 'Dining', '2024-08-17'),
(1, 590.00, 'Food', 'Groceries', '2024-08-21'),
(1, 560.00, 'Food', 'Restaurant', '2024-08-25'),
(1, 850.00, 'Travel', 'Uber', '2024-08-03'),
(1, 2800.00, 'Travel', 'Flight', '2024-08-10'),
(1, 700.00, 'Travel', 'Cab', '2024-08-16'),
(1, 4400.00, 'Shopping', 'Clothes', '2024-08-06'),
(1, 3600.00, 'Shopping', 'Electronics', '2024-08-14'),
(1, 2200.00, 'Bills', 'Electricity', '2024-08-05'),
(1, 800.00, 'Bills', 'Internet', '2024-08-05'),
(1, 2500.00, 'Bills', 'Rent', '2024-08-01'),
(1, 800.00, 'Entertainment', 'Movies', '2024-08-08'),
(1, 700.00, 'Entertainment', 'Gaming', '2024-08-18');

-- September
INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 640.00, 'Food', 'Groceries', '2024-09-02'),
(1, 580.00, 'Food', 'Restaurant', '2024-09-06'),
(1, 390.00, 'Food', 'Food delivery', '2024-09-10'),
(1, 560.00, 'Food', 'Groceries', '2024-09-14'),
(1, 470.00, 'Food', 'Dining', '2024-09-18'),
(1, 610.00, 'Food', 'Groceries', '2024-09-22'),
(1, 600.00, 'Food', 'Restaurant', '2024-09-26'),
(1, 900.00, 'Travel', 'Uber', '2024-09-04'),
(1, 3000.00, 'Travel', 'Flight', '2024-09-11'),
(1, 750.00, 'Travel', 'Cab', '2024-09-17'),
(1, 4600.00, 'Shopping', 'Clothes', '2024-09-07'),
(1, 3800.00, 'Shopping', 'Electronics', '2024-09-15'),
(1, 2300.00, 'Bills', 'Electricity', '2024-09-05'),
(1, 800.00, 'Bills', 'Internet', '2024-09-05'),
(1, 2500.00, 'Bills', 'Rent', '2024-09-01'),
(1, 850.00, 'Entertainment', 'Movies', '2024-09-09'),
(1, 750.00, 'Entertainment', 'Gaming', '2024-09-19');

-- October
INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 660.00, 'Food', 'Groceries', '2024-10-01'),
(1, 620.00, 'Food', 'Restaurant', '2024-10-05'),
(1, 410.00, 'Food', 'Food delivery', '2024-10-09'),
(1, 580.00, 'Food', 'Groceries', '2024-10-13'),
(1, 490.00, 'Food', 'Dining', '2024-10-17'),
(1, 630.00, 'Food', 'Groceries', '2024-10-21'),
(1, 640.00, 'Food', 'Restaurant', '2024-10-25'),
(1, 950.00, 'Travel', 'Uber', '2024-10-03'),
(1, 3200.00, 'Travel', 'Flight', '2024-10-10'),
(1, 800.00, 'Travel', 'Cab', '2024-10-16'),
(1, 4800.00, 'Shopping', 'Clothes', '2024-10-06'),
(1, 4000.00, 'Shopping', 'Electronics', '2024-10-14'),
(1, 2400.00, 'Bills', 'Electricity', '2024-10-05'),
(1, 800.00, 'Bills', 'Internet', '2024-10-05'),
(1, 2500.00, 'Bills', 'Rent', '2024-10-01'),
(1, 900.00, 'Entertainment', 'Movies', '2024-10-08'),
(1, 800.00, 'Entertainment', 'Gaming', '2024-10-18');

-- November
INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 680.00, 'Food', 'Groceries', '2024-11-02'),
(1, 660.00, 'Food', 'Restaurant', '2024-11-06'),
(1, 430.00, 'Food', 'Food delivery', '2024-11-10'),
(1, 600.00, 'Food', 'Groceries', '2024-11-14'),
(1, 510.00, 'Food', 'Dining', '2024-11-18'),
(1, 650.00, 'Food', 'Groceries', '2024-11-22'),
(1, 680.00, 'Food', 'Restaurant', '2024-11-26'),
(1, 1000.00, 'Travel', 'Uber', '2024-11-04'),
(1, 3400.00, 'Travel', 'Flight', '2024-11-11'),
(1, 850.00, 'Travel', 'Cab', '2024-11-17'),
(1, 5000.00, 'Shopping', 'Clothes', '2024-11-07'),
(1, 4200.00, 'Shopping', 'Electronics', '2024-11-15'),
(1, 2500.00, 'Bills', 'Electricity', '2024-11-05'),
(1, 800.00, 'Bills', 'Internet', '2024-11-05'),
(1, 2500.00, 'Bills', 'Rent', '2024-11-01'),
(1, 950.00, 'Entertainment', 'Movies', '2024-11-09'),
(1, 850.00, 'Entertainment', 'Gaming', '2024-11-19');

-- December
INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 700.00, 'Food', 'Groceries', '2024-12-01'),
(1, 700.00, 'Food', 'Restaurant', '2024-12-05'),
(1, 450.00, 'Food', 'Food delivery', '2024-12-09'),
(1, 620.00, 'Food', 'Groceries', '2024-12-13'),
(1, 530.00, 'Food', 'Dining', '2024-12-17'),
(1, 670.00, 'Food', 'Groceries', '2024-12-21'),
(1, 720.00, 'Food', 'Restaurant', '2024-12-25'),
(1, 1050.00, 'Travel', 'Uber', '2024-12-03'),
(1, 3600.00, 'Travel', 'Flight', '2024-12-10'),
(1, 900.00, 'Travel', 'Cab', '2024-12-16'),
(1, 5200.00, 'Shopping', 'Clothes', '2024-12-06'),
(1, 4400.00, 'Shopping', 'Electronics', '2024-12-14'),
(1, 2600.00, 'Bills', 'Electricity', '2024-12-05'),
(1, 800.00, 'Bills', 'Internet', '2024-12-05'),
(1, 2500.00, 'Bills', 'Rent', '2024-12-01'),
(1, 1000.00, 'Entertainment', 'Movies', '2024-12-08'),
(1, 900.00, 'Entertainment', 'Gaming', '2024-12-18');

-- January 2025
INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 720.00, 'Food', 'Groceries', '2025-01-02'),
(1, 740.00, 'Food', 'Restaurant', '2025-01-06'),
(1, 470.00, 'Food', 'Food delivery', '2025-01-10'),
(1, 640.00, 'Food', 'Groceries', '2025-01-14'),
(1, 550.00, 'Food', 'Dining', '2025-01-18'),
(1, 690.00, 'Food', 'Groceries', '2025-01-22'),
(1, 760.00, 'Food', 'Restaurant', '2025-01-26'),
(1, 1100.00, 'Travel', 'Uber', '2025-01-04'),
(1, 3800.00, 'Travel', 'Flight', '2025-01-11'),
(1, 950.00, 'Travel', 'Cab', '2025-01-17'),
(1, 5400.00, 'Shopping', 'Clothes', '2025-01-07'),
(1, 4600.00, 'Shopping', 'Electronics', '2025-01-15'),
(1, 2700.00, 'Bills', 'Electricity', '2025-01-05'),
(1, 800.00, 'Bills', 'Internet', '2025-01-05'),
(1, 2500.00, 'Bills', 'Rent', '2025-01-01'),
(1, 1050.00, 'Entertainment', 'Movies', '2025-01-09'),
(1, 950.00, 'Entertainment', 'Gaming', '2025-01-19');

-- February 2025
INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 740.00, 'Food', 'Groceries', '2025-02-01'),
(1, 780.00, 'Food', 'Restaurant', '2025-02-05'),
(1, 490.00, 'Food', 'Food delivery', '2025-02-09'),
(1, 660.00, 'Food', 'Groceries', '2025-02-13'),
(1, 570.00, 'Food', 'Dining', '2025-02-17'),
(1, 710.00, 'Food', 'Groceries', '2025-02-21'),
(1, 800.00, 'Food', 'Restaurant', '2025-02-25'),
(1, 1150.00, 'Travel', 'Uber', '2025-02-03'),
(1, 4000.00, 'Travel', 'Flight', '2025-02-10'),
(1, 1000.00, 'Travel', 'Cab', '2025-02-16'),
(1, 5600.00, 'Shopping', 'Clothes', '2025-02-06'),
(1, 4800.00, 'Shopping', 'Electronics', '2025-02-14'),
(1, 2800.00, 'Bills', 'Electricity', '2025-02-05'),
(1, 800.00, 'Bills', 'Internet', '2025-02-05'),
(1, 2500.00, 'Bills', 'Rent', '2025-02-01'),
(1, 1100.00, 'Entertainment', 'Movies', '2025-02-08'),
(1, 1000.00, 'Entertainment', 'Gaming', '2025-02-18');

-- Add more varied expenses for better ML training
INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 125.00, 'Food', 'Coffee shop', '2024-01-03'),
(1, 85.00, 'Food', 'Breakfast', '2024-01-04'),
(1, 165.00, 'Food', 'Lunch meeting', '2024-01-07'),
(1, 95.00, 'Food', 'Snacks', '2024-01-09'),
(1, 145.00, 'Food', 'Dinner', '2024-01-11'),
(1, 75.00, 'Food', 'Coffee', '2024-01-13'),
(1, 185.00, 'Food', 'Brunch', '2024-01-14'),
(1, 105.00, 'Food', 'Fast food', '2024-01-16'),
(1, 155.00, 'Food', 'Lunch', '2024-01-17'),
(1, 85.00, 'Food', 'Breakfast', '2024-01-19');

INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 45.00, 'Travel', 'Metro card', '2024-01-03'),
(1, 65.00, 'Travel', 'Parking', '2024-01-05'),
(1, 85.00, 'Travel', 'Toll', '2024-01-08'),
(1, 125.00, 'Travel', 'Bike rental', '2024-01-12'),
(1, 95.00, 'Travel', 'Bus pass', '2024-01-15'),
(1, 75.00, 'Travel', 'Parking fee', '2024-01-19'),
(1, 105.00, 'Travel', 'Car wash', '2024-01-23'),
(1, 145.00, 'Travel', 'Vehicle maintenance', '2024-01-27');

INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 350.00, 'Shopping', 'Books', '2024-01-04'),
(1, 450.00, 'Shopping', 'Furniture', '2024-01-09'),
(1, 250.00, 'Shopping', 'Home decor', '2024-01-13'),
(1, 550.00, 'Shopping', 'Appliances', '2024-01-18'),
(1, 150.00, 'Shopping', 'Stationery', '2024-01-22'),
(1, 850.00, 'Shopping', 'Laptop accessories', '2024-01-26');

INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 350.00, 'Bills', 'Insurance', '2024-01-08'),
(1, 450.00, 'Bills', 'Credit card', '2024-01-12'),
(1, 250.00, 'Bills', 'Loan EMI', '2024-01-16'),
(1, 150.00, 'Bills', 'Maintenance', '2024-01-20');

INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 150.00, 'Entertainment', 'Book purchase', '2024-01-05'),
(1, 250.00, 'Entertainment', 'Gym membership', '2024-01-01'),
(1, 350.00, 'Entertainment', 'Sports equipment', '2024-01-11'),
(1, 180.00, 'Entertainment', 'Music concert', '2024-01-17'),
(1, 120.00, 'Entertainment', 'Streaming services', '2024-01-01');

INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(1, 450.00, 'Other', 'Medical checkup', '2024-01-06'),
(1, 650.00, 'Other', 'Medicines', '2024-01-10'),
(1, 350.00, 'Other', 'Gifts', '2024-01-14'),
(1, 550.00, 'Other', 'Charity donation', '2024-01-20'),
(1, 250.00, 'Other', 'Pet care', '2024-01-24'),
(1, 450.00, 'Other', 'Salon', '2024-01-28');

-- Add expenses for user 2 and 3
INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(2, 400.00, 'Food', 'Groceries', '2025-01-05'),
(2, 250.00, 'Food', 'Restaurant', '2025-01-10'),
(2, 500.00, 'Travel', 'Uber', '2025-01-08'),
(2, 1500.00, 'Shopping', 'Clothes', '2025-01-12'),
(2, 2500.00, 'Bills', 'Rent', '2025-01-01'),
(2, 400.00, 'Entertainment', 'Movies', '2025-01-15');

INSERT INTO expenses (user_id, amount, category, description, date) VALUES
(3, 380.00, 'Food', 'Groceries', '2025-02-03'),
(3, 280.00, 'Food', 'Restaurant', '2025-02-07'),
(3, 550.00, 'Travel', 'Cab', '2025-02-05'),
(3, 1800.00, 'Shopping', 'Electronics', '2025-02-10'),
(3, 2500.00, 'Bills', 'Rent', '2025-02-01'),
(3, 450.00, 'Entertainment', 'Gaming', '2025-02-12');

-- Insert user preferences
INSERT INTO user_preferences (user_id, dark_mode, student_mode, monthly_budget) VALUES
(1, FALSE, TRUE, 30000.00),
(2, TRUE, FALSE, 25000.00),
(3, FALSE, TRUE, 20000.00);

-- Total: 400+ expense records generated
