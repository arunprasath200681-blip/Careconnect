-- ===================================================================
-- CareConnect - Sample Data Insertions Script
-- Database: MySQL 8.x
-- ===================================================================

USE careconnect_db;

-- Disable foreign key checks for clean insertion
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE appointments;
TRUNCATE TABLE doctors;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. INSERT USERS (Clinic Admin, Patients, and Doctors)
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

-- 2. INSERT DOCTORS (With Indian institutes, qualifications, and ₹ INR fees)
INSERT INTO doctors (id, user_id, name, specialization, qualification, experience_years, consultation_fee, bio, available_days, time_slots, image_url) VALUES
(1, 5, 'Dr. Rajesh Iyer', 'Cardiology', 'MD (AIIMS)', 15, 1000.0, 'Cardiologist', 'Mon-Fri', '09:30-16:00', 'img/d1.svg'),
(2, 6, 'Dr. Suresh Menon', 'Neurology', 'MD (NIMHANS)', 14, 1200.0, 'Neurologist', 'Mon,Wed,Thu', '10:00-16:30', 'img/d2.svg'),
(3, 7, 'Dr. Priya Sharma', 'Dermatology', 'MD (CMC)', 10, 750.0, 'Dermatologist', 'Tue,Thu,Sat', '09:30-17:00', 'img/d3.svg'),
(4, 8, 'Dr. Arvind Swaminathan', 'Orthopedics', 'MS (MMC)', 16, 900.0, 'Orthopedic', 'Mon,Tue,Thu', '09:00-16:00', 'img/d4.svg'),
(5, 9, 'Dr. Kavitha Raman', 'Pediatrics', 'MD (PGIMER)', 11, 650.0, 'Pediatrician', 'Mon-Fri', '09:30-16:00', 'img/d5.svg'),
(6, 10, 'Dr. Amit Verma', 'General Medicine', 'MD (KMC)', 9, 500.0, 'Physician', 'Mon-Sat', '08:30-17:00', 'img/d6.svg');

-- 3. INSERT APPOINTMENTS (Sample past completed & upcoming visits)
INSERT INTO appointments (id, patient_id, doctor_id, appointment_date, time_slot, symptoms, status, doctor_notes, prescription, created_at) VALUES
(1, 2, 1, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '10:30 AM', 'Chest heaviness', 'COMPLETED', 'ECG normal', 'Ecosprin 75mg', NOW()),
(2, 3, 3, DATE_SUB(CURDATE(), INTERVAL 5 DAY), '09:30 AM', 'Skin rash & allergy', 'COMPLETED', 'Mild dermatitis', 'Tab Allegra 120mg', NOW()),
(3, 2, 6, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '10:00 AM', 'Routine checkup', 'CONFIRMED', NULL, NULL, NOW()),
(4, 4, 4, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '09:00 AM', 'Knee joint pain', 'CONFIRMED', NULL, NULL, NOW()),
(5, 3, 1, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '02:30 PM', 'Cardiac follow-up', 'CONFIRMED', NULL, NULL, NOW());
