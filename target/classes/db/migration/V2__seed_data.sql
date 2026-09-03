-- Seed data for local development / demo / testing

INSERT IGNORE INTO gyms (id, name, registration_number, contact_email, contact_phone, description, created_at, updated_at) VALUES
(1, 'FitZone Gyms', 'REG-1001', 'contact@fitzone.example', '+1-555-0100', 'A premium chain of fitness centers.', NOW(), NOW()),
(2, 'PowerHouse Fitness', 'REG-1002', 'info@powerhouse.example', '+1-555-0200', 'Strength and conditioning specialists.', NOW(), NOW());

INSERT IGNORE INTO branches (id, gym_id, name, address, city, state, country, opening_time, closing_time, capacity, facilities, manager_name, manager_contact, status, created_at, updated_at) VALUES
(1, 1, 'FitZone Downtown', '123 Main St', 'Springfield', 'IL', 'USA', '06:00:00', '22:00:00', 150, 'Pool, Sauna, Free Weights', 'Alice Manager', '+1-555-0101', 'ACTIVE', NOW(), NOW()),
(2, 1, 'FitZone Uptown', '456 Oak Ave', 'Springfield', 'IL', 'USA', '05:00:00', '23:00:00', 100, 'Cardio Zone, Yoga Studio', 'Bob Manager', '+1-555-0102', 'ACTIVE', NOW(), NOW()),
(3, 2, 'PowerHouse Central', '789 Elm St', 'Shelbyville', 'IL', 'USA', '06:00:00', '21:00:00', 80, 'CrossFit Box, Free Weights', 'Carol Manager', '+1-555-0201', 'ACTIVE', NOW(), NOW());

INSERT IGNORE INTO members (id, first_name, last_name, email, phone, date_of_birth, address, emergency_contact_name, emergency_contact_phone, branch_id, status, created_at, updated_at) VALUES
(1, 'John', 'Doe', 'john.doe@example.com', '+1-555-1001', '1990-05-15', '12 Elm St, Springfield', 'Jane Doe', '+1-555-1002', 1, 'ACTIVE', NOW(), NOW()),
(2, 'Emily', 'Clark', 'emily.clark@example.com', '+1-555-1003', '1988-11-02', '34 Pine St, Springfield', 'Mark Clark', '+1-555-1004', 1, 'ACTIVE', NOW(), NOW()),
(3, 'Michael', 'Brown', 'michael.brown@example.com', '+1-555-1005', '1995-02-20', '56 Maple Ave, Springfield', 'Lisa Brown', '+1-555-1006', 2, 'ACTIVE', NOW(), NOW()),
(4, 'Sarah', 'Wilson', 'sarah.wilson@example.com', '+1-555-1007', '1992-07-08', '78 Cedar Rd, Shelbyville', 'Tom Wilson', '+1-555-1008', 3, 'SUSPENDED', NOW(), NOW()),
(5, 'David', 'Lee', 'david.lee@example.com', '+1-555-1009', '1985-09-30', '90 Birch Blvd, Shelbyville', 'Amy Lee', '+1-555-1010', 3, 'ACTIVE', NOW(), NOW());

INSERT IGNORE INTO trainers (id, first_name, last_name, email, phone, specialization, certifications, experience_years, branch_id, status, created_at, updated_at) VALUES
(1, 'Chris', 'Taylor', 'chris.taylor@example.com', '+1-555-2001', 'Strength Training', 'NASM-CPT', 8, 1, 'ACTIVE', NOW(), NOW()),
(2, 'Anna', 'Martinez', 'anna.martinez@example.com', '+1-555-2002', 'Yoga & Flexibility', 'RYT-200', 5, 2, 'ACTIVE', NOW(), NOW()),
(3, 'Robert', 'Johnson', 'robert.johnson@example.com', '+1-555-2003', 'CrossFit', 'CF-L2', 10, 3, 'ACTIVE', NOW(), NOW());

INSERT IGNORE INTO membership_plans (id, name, type, duration_days, price, description, active, created_at, updated_at) VALUES
(1, 'Monthly Basic', 'MONTHLY', 30, 39.99, 'Access to gym facilities for one month.', TRUE, NOW(), NOW()),
(2, 'Quarterly Plus', 'QUARTERLY', 90, 109.99, 'Quarterly plan with class access.', TRUE, NOW(), NOW()),
(3, 'Half-Yearly Pro', 'HALF_YEARLY', 182, 199.99, 'Six months with personal training credits.', TRUE, NOW(), NOW()),
(4, 'Yearly Elite', 'YEARLY', 365, 349.99, 'Full year all-access membership.', TRUE, NOW(), NOW());

INSERT IGNORE INTO memberships (id, member_id, plan_id, start_date, end_date, status, price, created_at, updated_at) VALUES
(1, 1, 1, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'ACTIVE', 39.99, NOW(), NOW()),
(2, 2, 2, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 90 DAY), 'ACTIVE', 109.99, NOW(), NOW()),
(3, 5, 4, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 365 DAY), 'ACTIVE', 349.99, NOW(), NOW());

INSERT IGNORE INTO workout_programs (id, name, description, level, trainer_id, created_at, updated_at) VALUES
(1, 'Beginner Full Body', 'A full body program for newcomers.', 'BEGINNER', 1, NOW(), NOW()),
(2, 'Advanced Strength', 'High intensity strength program.', 'ADVANCED', 3, NOW(), NOW());

INSERT IGNORE INTO exercises (id, workout_program_id, name, category, sets, reps, duration_seconds, order_index) VALUES
(1, 1, 'Push Ups', 'Chest', 3, 12, NULL, 1),
(2, 1, 'Bodyweight Squats', 'Legs', 3, 15, NULL, 2),
(3, 2, 'Deadlift', 'Back', 5, 5, NULL, 1),
(4, 2, 'Bench Press', 'Chest', 5, 5, NULL, 2);

INSERT IGNORE INTO workout_assignments (id, workout_program_id, member_id, assigned_date, status, progress_notes, created_at, updated_at) VALUES
(1, 1, 1, CURDATE(), 'ASSIGNED', 'Just started.', NOW(), NOW()),
(2, 2, 5, CURDATE(), 'IN_PROGRESS', 'Week 2 of program.', NOW(), NOW());

INSERT IGNORE INTO fitness_classes (id, name, class_type, branch_id, trainer_id, start_time, end_time, capacity, status, created_at, updated_at) VALUES
(1, 'Morning Yoga', 'Yoga', 2, 2, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 1 DAY) + INTERVAL 1 HOUR, 20, 'SCHEDULED', NOW(), NOW()),
(2, 'CrossFit Bootcamp', 'CrossFit', 3, 3, DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 2 DAY) + INTERVAL 1 HOUR, 15, 'SCHEDULED', NOW(), NOW());

INSERT IGNORE INTO class_registrations (id, fitness_class_id, member_id, status, registered_at, waitlist_position, created_at, updated_at) VALUES
(1, 1, 2, 'REGISTERED', NOW(), NULL, NOW(), NOW()),
(2, 2, 5, 'REGISTERED', NOW(), NULL, NOW(), NOW());

INSERT IGNORE INTO appointments (id, member_id, trainer_id, branch_id, start_time, end_time, status, notes, created_at, updated_at) VALUES
(1, 1, 1, 1, DATE_ADD(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 3 DAY) + INTERVAL 1 HOUR, 'REQUESTED', 'Initial assessment session.', NOW(), NOW());

INSERT IGNORE INTO attendances (id, member_id, branch_id, type, check_in_time, check_out_time, created_at, updated_at) VALUES
(1, 1, 1, 'GYM_VISIT', DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW());

INSERT IGNORE INTO payments (id, member_id, amount, currency, purpose, reference_id, status, transaction_ref, created_at, updated_at) VALUES
(1, 1, 39.99, 'USD', 'MEMBERSHIP_PURCHASE', 1, 'SUCCESS', 'TXN-SEED-0001', NOW(), NOW()),
(2, 2, 109.99, 'USD', 'MEMBERSHIP_PURCHASE', 2, 'SUCCESS', 'TXN-SEED-0002', NOW(), NOW()),
(3, 5, 349.99, 'USD', 'MEMBERSHIP_PURCHASE', 3, 'SUCCESS', 'TXN-SEED-0003', NOW(), NOW());

INSERT IGNORE INTO notifications (id, member_id, type, message, is_read, created_at, updated_at) VALUES
(1, 1, 'PAYMENT_SUCCESS', 'Your payment of 39.99 USD was successful.', FALSE, NOW(), NOW()),
(2, 2, 'MEMBERSHIP_RENEWAL', 'Your membership has been activated.', FALSE, NOW(), NOW()),
(3, 5, 'CLASS_REGISTRATION', 'You are registered for CrossFit Bootcamp.', FALSE, NOW(), NOW());
