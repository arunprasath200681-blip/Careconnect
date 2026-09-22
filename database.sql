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
(1, 'Arun Kumar (Clinic Administrator)', 'admin@careconnect.in', 'admin123', '+91 98765 43210', 'ADMIN', NOW()),
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
(1, 5, 'Dr. Rajesh Iyer', 'Cardiology', 'MBBS, MD, DM (Cardiology) - AIIMS New Delhi', 15, 1000.0, 
 'Senior Interventional Cardiologist specializing in preventive heart health, hypertension management, and ECG/Echocardiogram evaluations.', 
 'Mon, Tue, Wed, Fri', '09:30 AM, 10:30 AM, 11:30 AM, 02:30 PM, 04:00 PM', 
 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=400&auto=format&fit=crop&q=80'),

(2, 6, 'Dr. Suresh Menon', 'Neurology', 'MBBS, MD, DM (Neurology) - NIMHANS Bangalore', 14, 1200.0, 
 'Neurologist with extensive clinical expertise in chronic migraines, nerve disorders, stroke rehabilitation, and cognitive health.', 
 'Mon, Wed, Thu', '10:00 AM, 11:30 AM, 02:00 PM, 04:30 PM', 
 'https://images.unsplash.com/photo-1537368910025-700350fe46c7?w=400&auto=format&fit=crop&q=80'),

(3, 7, 'Dr. Priya Sharma', 'Dermatology', 'MBBS, MD (Dermatology) - CMC Vellore', 10, 750.0, 
 'Consultant Dermatologist specializing in allergic skin rashes, acne scar reduction, clinical eczema, and hair fall management.', 
 'Tue, Thu, Fri, Sat', '09:30 AM, 11:00 AM, 01:30 PM, 03:30 PM, 05:00 PM', 
 'https://images.unsplash.com/photo-1594824813590-79888981f7c3?w=400&auto=format&fit=crop&q=80'),

(4, 8, 'Dr. Arvind Swaminathan', 'Orthopedics', 'MBBS, MS (Ortho), Fellowship in Joint Replacement - MMC Chennai', 16, 900.0, 
 'Orthopedic surgeon specializing in sports knee injuries, spondylosis, joint pain therapies, and fracture trauma management.', 
 'Mon, Tue, Thu', '09:00 AM, 10:30 AM, 02:30 PM, 04:00 PM', 
 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=400&auto=format&fit=crop&q=80'),

(5, 9, 'Dr. Kavitha Raman', 'Pediatrics', 'MBBS, MD (Pediatrics) - PGIMER Chandigarh', 11, 650.0, 
 'Senior Pediatrician dedicated to infant nutrition, routine childhood vaccination programs, seasonal flu care, and child wellness.', 
 'Mon, Tue, Wed, Thu, Fri', '09:30 AM, 11:00 AM, 12:30 PM, 02:30 PM, 04:00 PM', 
 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=400&auto=format&fit=crop&q=80'),

(6, 10, 'Dr. Amit Verma', 'General Medicine', 'MBBS, MD (Internal Medicine) - KMC Manipal', 9, 500.0, 
 'General physician focusing on diabetes care, seasonal viral fevers, hypertension, thyroid disorders, and routine health checkups.', 
 'Mon, Tue, Wed, Thu, Fri, Sat', '08:30 AM, 10:00 AM, 11:30 AM, 02:00 PM, 03:30 PM, 05:00 PM', 
 'https://images.unsplash.com/photo-1579684385127-1ef15d508118?w=400&auto=format&fit=crop&q=80');

-- 8. Insert Appointments
INSERT INTO appointments (id, patient_id, doctor_id, appointment_date, time_slot, symptoms, status, doctor_notes, prescription, created_at) VALUES
(1, 2, 1, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '10:30 AM', 
 'Mild chest heaviness during morning brisk walk.', 'COMPLETED', 
 'BP 130/84 mmHg, Resting ECG normal sinus rhythm.', 
 'Tab. Ecosprin 75mg OD, Tab. Telma 40mg OD, Lipid Profile test.', NOW()),

(2, 3, 3, DATE_SUB(CURDATE(), INTERVAL 5 DAY), '09:30 AM', 
 'Seasonal skin itching and allergic redness on arms.', 'COMPLETED', 
 'Contact allergic dermatitis, mild erythema.', 
 'Tab. Allegra 120mg OD at night, Calamine lotion.', NOW()),

(3, 2, 6, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '10:00 AM', 
 'Annual routine health check-up, fasting blood sugar review.', 'CONFIRMED', NULL, NULL, NOW()),

(4, 4, 4, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '09:00 AM', 
 'Right knee joint pain and swelling following badminton match.', 'CONFIRMED', NULL, NULL, NOW()),

(5, 3, 1, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '02:30 PM', 
 'Routine follow-up consultation for family cardiac history evaluation.', 'CONFIRMED', NULL, NULL, NOW());

