-- ===================================================================
-- CareConnect - Clinic Appointment & Patient Scheduling Database Schema
-- Database: MySQL 8.x
-- ===================================================================

CREATE DATABASE IF NOT EXISTS careconnect_db;
USE careconnect_db;

-- 1. Users Table (Patients, Doctors, Administrators)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    role VARCHAR(20) NOT NULL, -- PATIENT, DOCTOR, ADMIN
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 2. Doctors Table
CREATE TABLE IF NOT EXISTS doctors (
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

-- 3. Appointments Table
CREATE TABLE IF NOT EXISTS appointments (
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

-- Indexes for Fast Query Performance
CREATE INDEX idx_appointments_patient ON appointments(patient_id);
CREATE INDEX idx_appointments_doctor ON appointments(doctor_id);
CREATE INDEX idx_appointments_date ON appointments(appointment_date);
CREATE INDEX idx_doctors_specialization ON doctors(specialization);

-- ===================================================================
-- Handy Clean Views for MySQL Command Line (Clean readable tables)
-- ===================================================================

CREATE OR REPLACE VIEW v_doctors AS 
  SELECT id, name, specialization, consultation_fee AS fee_inr, experience_years, available_days 
  FROM doctors;

CREATE OR REPLACE VIEW v_appointments AS 
  SELECT a.id, u.full_name AS patient_name, d.name AS doctor_name, a.appointment_date, a.time_slot, a.status 
  FROM appointments a 
  JOIN users u ON a.patient_id = u.id 
  JOIN doctors d ON a.doctor_id = d.id;

CREATE OR REPLACE VIEW v_users AS 
  SELECT id, full_name, email, role, phone 
  FROM users;
