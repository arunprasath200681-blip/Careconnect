-- ===================================================================
-- CareConnect - Complete Master Database Setup Script
-- Database: MySQL 8.x
-- Drops any old database/tables and sets up fresh clean schema + data
-- ===================================================================

-- 1. Reset Database
DROP DATABASE IF EXISTS careconnect_db;
CREATE DATABASE careconnect_db;
USE careconnect_db;

-- 2. Create Users Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    role VARCHAR(20) NOT NULL, -- PATIENT, DOCTOR, ADMIN
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 3. Create Doctors Table
CREATE TABLE doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    name VARCHAR(120) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    qualification VARCHAR(100) NOT NULL,
    experience_years INT NOT NULL,
    consultation_fee DOUBLE NOT NULL,
    bio VARCHAR(1000),
    available_days VARCHAR(100) NOT NULL,
    time_slots VARCHAR(500) NOT NULL,
    image_url VARCHAR(500),
    CONSTRAINT fk_doctor_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 4. Create Appointments Table with Compound Unique Constraint
CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    time_slot VARCHAR(30) NOT NULL,
    symptoms VARCHAR(1000),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, CONFIRMED, COMPLETED, CANCELLED
    doctor_notes VARCHAR(1500),
    prescription VARCHAR(1500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    CONSTRAINT uq_doctor_slot UNIQUE (doctor_id, appointment_date, time_slot)
);

-- 5. Create Performance Indexes
CREATE INDEX idx_appointments_patient ON appointments(patient_id);
CREATE INDEX idx_appointments_doctor ON appointments(doctor_id);
CREATE INDEX idx_appointments_date ON appointments(appointment_date);
CREATE INDEX idx_doctors_specialization ON doctors(specialization);

-- 6. Insert All Seed Users
INSERT INTO users (id, full_name, email, password, phone, role, created_at) VALUES
(1, 'Arun Kumar', 'admin@careconnect.in', 'admin123', '+91 98765 43210', 'ADMIN', NOW()),
(2, 'Rahul Sharma', 'rahul@gmail.com', 'patient123', '+91 98401 12345', 'PATIENT', NOW()),
(3, 'Ananya Patel', 'ananya@gmail.com', 'patient123', '+91 98402 23456', 'PATIENT', NOW()),
(4, 'Vikram Sundaram', 'vikram@gmail.com', 'patient123', '+91 98403 34567', 'PATIENT', NOW()),
(5, 'Dr. Rajesh Iyer', 'dr.rajesh@careconnect.in', 'doctor123', '+91 98111 02001', 'DOCTOR', NOW()),
(6, 'Dr. Suresh Menon', 'dr.suresh@careconnect.in', 'doctor123', '+91 98111 02002', 'DOCTOR', NOW()),
(7, 'Dr. Priya Sharma', 'dr.priya@careconnect.in', 'doctor123', '+91 98111 02003', 'DOCTOR', NOW()),
(8, 'Dr. Arvind Swaminathan', 'dr.arvind@careconnect.in', 'doctor123', '+91 98111 02004', 'DOCTOR', NOW()),
(9, 'Dr. Kavitha Raman', 'dr.kavitha@careconnect.in', 'doctor123', '+91 98111 02005', 'DOCTOR', NOW()),
(10, 'Dr. Amit Verma', 'dr.amit@careconnect.in', 'doctor123', '+91 98111 02006', 'DOCTOR', NOW());

-- 7. Insert All Seed Doctors (With Indian Colleges & INR Fees)
INSERT INTO doctors (id, user_id, name, specialization, qualification, experience_years, consultation_fee, bio, available_days, time_slots, image_url) VALUES
(1, 5, 'Dr. Rajesh Iyer', 'Cardiology', 'MD (AIIMS)', 15, 1000.0, 'Cardiologist', 'Mon-Fri', '09:30-16:00', 'img/d1.svg'),
(2, 6, 'Dr. Suresh Menon', 'Neurology', 'MD (NIMHANS)', 14, 1200.0, 'Neurologist', 'Mon,Wed,Thu', '10:00-16:30', 'img/d2.svg'),
(3, 7, 'Dr. Priya Sharma', 'Dermatology', 'MD (CMC)', 10, 750.0, 'Dermatologist', 'Tue,Thu,Sat', '09:30-17:00', 'img/d3.svg'),
(4, 8, 'Dr. Arvind Swaminathan', 'Orthopedics', 'MS (MMC)', 16, 900.0, 'Orthopedic', 'Mon,Tue,Thu', '09:00-16:00', 'img/d4.svg'),
(5, 9, 'Dr. Kavitha Raman', 'Pediatrics', 'MD (PGIMER)', 11, 650.0, 'Pediatrician', 'Mon-Fri', '09:30-16:00', 'img/d5.svg'),
(6, 10, 'Dr. Amit Verma', 'General Medicine', 'MD (KMC)', 9, 500.0, 'Physician', 'Mon-Sat', '08:30-17:00', 'img/d6.svg');

-- 8. Insert Appointments
INSERT INTO appointments (id, patient_id, doctor_id, appointment_date, time_slot, symptoms, status, doctor_notes, prescription, created_at) VALUES
(1, 2, 1, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '10:30 AM', 'Chest heaviness', 'COMPLETED', 'ECG normal', 'Ecosprin 75mg', NOW()),
(2, 3, 3, DATE_SUB(CURDATE(), INTERVAL 5 DAY), '09:30 AM', 'Skin rash & allergy', 'COMPLETED', 'Mild dermatitis', 'Tab Allegra 120mg', NOW()),
(3, 2, 6, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '10:00 AM', 'Routine checkup', 'CONFIRMED', NULL, NULL, NOW()),
(4, 4, 4, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '09:00 AM', 'Knee joint pain', 'CONFIRMED', NULL, NULL, NOW()),
(5, 3, 1, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '02:30 PM', 'Cardiac follow-up', 'CONFIRMED', NULL, NULL, NOW());

