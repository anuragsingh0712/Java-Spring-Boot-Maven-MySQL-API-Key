-- Gym Management and Fitness Services - initial schema

CREATE TABLE api_keys (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    key_hash VARCHAR(128) NOT NULL,
    name VARCHAR(120) NOT NULL,
    role VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NULL,
    last_used_at TIMESTAMP NULL,
    CONSTRAINT uq_api_keys_key_hash UNIQUE (key_hash)
) ENGINE=InnoDB;

CREATE TABLE gyms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    registration_number VARCHAR(60) NULL,
    contact_email VARCHAR(150) NULL,
    contact_phone VARCHAR(30) NULL,
    description VARCHAR(500) NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_gyms_registration_number UNIQUE (registration_number)
) ENGINE=InnoDB;

CREATE TABLE branches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    gym_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    address VARCHAR(250) NULL,
    city VARCHAR(100) NULL,
    state VARCHAR(100) NULL,
    country VARCHAR(100) NULL,
    opening_time TIME NULL,
    closing_time TIME NULL,
    capacity INT NOT NULL,
    facilities VARCHAR(500) NULL,
    manager_name VARCHAR(150) NULL,
    manager_contact VARCHAR(60) NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_branches_gym FOREIGN KEY (gym_id) REFERENCES gyms (id),
    CONSTRAINT chk_branches_capacity CHECK (capacity > 0)
) ENGINE=InnoDB;

CREATE INDEX idx_branches_gym_id ON branches (gym_id);
CREATE INDEX idx_branches_status ON branches (status);

CREATE TABLE members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(30) NULL,
    date_of_birth DATE NULL,
    address VARCHAR(250) NULL,
    emergency_contact_name VARCHAR(150) NULL,
    emergency_contact_phone VARCHAR(30) NULL,
    branch_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_members_email UNIQUE (email),
    CONSTRAINT fk_members_branch FOREIGN KEY (branch_id) REFERENCES branches (id)
) ENGINE=InnoDB;

CREATE INDEX idx_members_branch_id ON members (branch_id);
CREATE INDEX idx_members_status ON members (status);

CREATE TABLE trainers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(30) NULL,
    specialization VARCHAR(150) NULL,
    certifications VARCHAR(500) NULL,
    experience_years INT NULL,
    branch_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_trainers_email UNIQUE (email),
    CONSTRAINT fk_trainers_branch FOREIGN KEY (branch_id) REFERENCES branches (id)
) ENGINE=InnoDB;

CREATE INDEX idx_trainers_branch_id ON trainers (branch_id);
CREATE INDEX idx_trainers_status ON trainers (status);

CREATE TABLE membership_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    type VARCHAR(30) NOT NULL,
    duration_days INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    description VARCHAR(500) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT chk_membership_plans_duration CHECK (duration_days > 0),
    CONSTRAINT chk_membership_plans_price CHECK (price >= 0)
) ENGINE=InnoDB;

CREATE TABLE memberships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    start_date DATE NULL,
    end_date DATE NULL,
    status VARCHAR(30) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    pause_start_date DATE NULL,
    pause_end_date DATE NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_memberships_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT fk_memberships_plan FOREIGN KEY (plan_id) REFERENCES membership_plans (id)
) ENGINE=InnoDB;

CREATE INDEX idx_memberships_member_id ON memberships (member_id);
CREATE INDEX idx_memberships_status ON memberships (status);

CREATE TABLE workout_programs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500) NULL,
    level VARCHAR(30) NOT NULL,
    trainer_id BIGINT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_workout_programs_trainer FOREIGN KEY (trainer_id) REFERENCES trainers (id)
) ENGINE=InnoDB;

CREATE TABLE exercises (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    workout_program_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    category VARCHAR(100) NULL,
    sets INT NULL,
    reps INT NULL,
    duration_seconds INT NULL,
    order_index INT NULL,
    CONSTRAINT fk_exercises_program FOREIGN KEY (workout_program_id) REFERENCES workout_programs (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_exercises_program_id ON exercises (workout_program_id);

CREATE TABLE workout_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    workout_program_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    assigned_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    progress_notes VARCHAR(1000) NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_workout_assignments_program FOREIGN KEY (workout_program_id) REFERENCES workout_programs (id),
    CONSTRAINT fk_workout_assignments_member FOREIGN KEY (member_id) REFERENCES members (id)
) ENGINE=InnoDB;

CREATE INDEX idx_workout_assignments_member_id ON workout_assignments (member_id);

CREATE TABLE fitness_classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    class_type VARCHAR(100) NULL,
    branch_id BIGINT NOT NULL,
    trainer_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    capacity INT NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_fitness_classes_branch FOREIGN KEY (branch_id) REFERENCES branches (id),
    CONSTRAINT fk_fitness_classes_trainer FOREIGN KEY (trainer_id) REFERENCES trainers (id),
    CONSTRAINT chk_fitness_classes_capacity CHECK (capacity > 0)
) ENGINE=InnoDB;

CREATE INDEX idx_fitness_classes_branch_id ON fitness_classes (branch_id);
CREATE INDEX idx_fitness_classes_trainer_id ON fitness_classes (trainer_id);

CREATE TABLE class_registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fitness_class_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    registered_at TIMESTAMP NOT NULL,
    waitlist_position INT NULL,
    idempotency_key VARCHAR(150) NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_class_registrations_class FOREIGN KEY (fitness_class_id) REFERENCES fitness_classes (id),
    CONSTRAINT fk_class_registrations_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT uq_class_registrations_class_member UNIQUE (fitness_class_id, member_id)
) ENGINE=InnoDB;

CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    trainer_id BIGINT NOT NULL,
    branch_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status VARCHAR(30) NOT NULL,
    notes VARCHAR(1000) NULL,
    idempotency_key VARCHAR(150) NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_appointments_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT fk_appointments_trainer FOREIGN KEY (trainer_id) REFERENCES trainers (id),
    CONSTRAINT fk_appointments_branch FOREIGN KEY (branch_id) REFERENCES branches (id)
) ENGINE=InnoDB;

CREATE INDEX idx_appointments_trainer_time ON appointments (trainer_id, start_time, end_time);
CREATE INDEX idx_appointments_member_time ON appointments (member_id, start_time, end_time);

CREATE TABLE attendances (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    branch_id BIGINT NOT NULL,
    type VARCHAR(30) NOT NULL,
    reference_id BIGINT NULL,
    check_in_time TIMESTAMP NOT NULL,
    check_out_time TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_attendances_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT fk_attendances_branch FOREIGN KEY (branch_id) REFERENCES branches (id)
) ENGINE=InnoDB;

CREATE INDEX idx_attendances_member_id ON attendances (member_id);
CREATE INDEX idx_attendances_branch_id ON attendances (branch_id);

CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    purpose VARCHAR(30) NOT NULL,
    reference_id BIGINT NULL,
    status VARCHAR(30) NOT NULL,
    idempotency_key VARCHAR(150) NULL,
    transaction_ref VARCHAR(150) NULL,
    refunded_amount DECIMAL(10,2) NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_payments_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT uq_payments_idempotency_key UNIQUE (idempotency_key),
    CONSTRAINT chk_payments_amount CHECK (amount > 0)
) ENGINE=InnoDB;

CREATE INDEX idx_payments_member_id ON payments (member_id);
CREATE INDEX idx_payments_status ON payments (status);

CREATE TABLE refunds (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    reason VARCHAR(500) NULL,
    status VARCHAR(30) NOT NULL,
    idempotency_key VARCHAR(150) NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_refunds_payment FOREIGN KEY (payment_id) REFERENCES payments (id),
    CONSTRAINT chk_refunds_amount CHECK (amount > 0)
) ENGINE=InnoDB;

CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NULL,
    type VARCHAR(40) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_notifications_member FOREIGN KEY (member_id) REFERENCES members (id)
) ENGINE=InnoDB;

CREATE INDEX idx_notifications_member_id ON notifications (member_id);
