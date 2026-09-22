package com.careconnect.config;

import com.careconnect.entity.*;
import com.careconnect.repository.AppointmentRepository;
import com.careconnect.repository.DoctorRepository;
import com.careconnect.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public DataInitializer(UserRepository userRepository,
                           DoctorRepository doctorRepository,
                           AppointmentRepository appointmentRepository) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // Data already seeded
        }

        // 1. Seed Clinic Administrator
        User admin = new User(
                "Arun Kumar",
                "admin@careconnect.in",
                "admin123",
                "+91 98765 43210",
                Role.ADMIN
        );
        userRepository.save(admin);

        // 2. Seed Indian Patients
        User patient1 = new User(
                "Rahul Sharma",
                "rahul@gmail.com",
                "patient123",
                "+91 98401 12345",
                Role.PATIENT
        );
        User patient2 = new User(
                "Ananya Patel",
                "ananya@gmail.com",
                "patient123",
                "+91 98402 23456",
                Role.PATIENT
        );
        User patient3 = new User(
                "Vikram Sundaram",
                "vikram@gmail.com",
                "patient123",
                "+91 98403 34567",
                Role.PATIENT
        );
        userRepository.save(patient1);
        userRepository.save(patient2);
        userRepository.save(patient3);

        // 3. Seed Indian Doctors & Accounts
        User docUser1 = new User("Dr. Rajesh Iyer", "dr.rajesh@careconnect.in", "doctor123", "+91 98111 02001", Role.DOCTOR);
        User docUser2 = new User("Dr. Suresh Menon", "dr.suresh@careconnect.in", "doctor123", "+91 98111 02002", Role.DOCTOR);
        User docUser3 = new User("Dr. Priya Sharma", "dr.priya@careconnect.in", "doctor123", "+91 98111 02003", Role.DOCTOR);
        User docUser4 = new User("Dr. Arvind Swaminathan", "dr.arvind@careconnect.in", "doctor123", "+91 98111 02004", Role.DOCTOR);
        User docUser5 = new User("Dr. Kavitha Raman", "dr.kavitha@careconnect.in", "doctor123", "+91 98111 02005", Role.DOCTOR);
        User docUser6 = new User("Dr. Amit Verma", "dr.amit@careconnect.in", "doctor123", "+91 98111 02006", Role.DOCTOR);

        userRepository.save(docUser1);
        userRepository.save(docUser2);
        userRepository.save(docUser3);
        userRepository.save(docUser4);
        userRepository.save(docUser5);
        userRepository.save(docUser6);

        // Doctor 1: Cardiology (AIIMS Delhi)
        Doctor d1 = new Doctor(
                docUser1,
                "Dr. Rajesh Iyer",
                "Cardiology",
                "MD (AIIMS)",
                15,
                1000.0,
                "Cardiologist",
                "Mon-Fri",
                "09:30-16:00",
                "img/d1.svg"
        );

        // Doctor 2: Neurology (NIMHANS Bangalore)
        Doctor d2 = new Doctor(
                docUser2,
                "Dr. Suresh Menon",
                "Neurology",
                "MD (NIMHANS)",
                14,
                1200.0,
                "Neurologist",
                "Mon,Wed,Thu",
                "10:00-16:30",
                "img/d2.svg"
        );

        // Doctor 3: Dermatology (CMC Vellore)
        Doctor d3 = new Doctor(
                docUser3,
                "Dr. Priya Sharma",
                "Dermatology",
                "MD (CMC)",
                10,
                750.0,
                "Dermatologist",
                "Tue,Thu,Sat",
                "09:30-17:00",
                "img/d3.svg"
        );

        // Doctor 4: Orthopedics (MMC Chennai)
        Doctor d4 = new Doctor(
                docUser4,
                "Dr. Arvind Swaminathan",
                "Orthopedics",
                "MS (MMC)",
                16,
                900.0,
                "Orthopedic",
                "Mon,Tue,Thu",
                "09:00-16:00",
                "img/d4.svg"
        );

        // Doctor 5: Pediatrics (PGIMER Chandigarh)
        Doctor d5 = new Doctor(
                docUser5,
                "Dr. Kavitha Raman",
                "Pediatrics",
                "MD (PGIMER)",
                11,
                650.0,
                "Pediatrician",
                "Mon-Fri",
                "09:30-16:00",
                "img/d5.svg"
        );

        // Doctor 6: General Medicine (KMC Manipal)
        Doctor d6 = new Doctor(
                docUser6,
                "Dr. Amit Verma",
                "General Medicine",
                "MD (KMC)",
                9,
                500.0,
                "Physician",
                "Mon-Sat",
                "08:30-17:00",
                "img/d6.svg"
        );

        doctorRepository.save(d1);
        doctorRepository.save(d2);
        doctorRepository.save(d3);
        doctorRepository.save(d4);
        doctorRepository.save(d5);
        doctorRepository.save(d6);

        // 4. Seed Appointments with Indian Clinical Context
        LocalDate today = LocalDate.now();

        // Past Completed Consultation 1 (Rahul Sharma with Dr. Rajesh Iyer)
        Appointment a1 = new Appointment(patient1, d1, today.minusDays(3), "10:30 AM", "Chest heaviness");
        a1.setStatus(AppointmentStatus.COMPLETED);
        a1.setDoctorNotes("ECG normal");
        a1.setPrescription("Ecosprin 75mg");
        appointmentRepository.save(a1);

        // Past Completed Consultation 2 (Ananya Patel with Dr. Priya Sharma)
        Appointment a2 = new Appointment(patient2, d3, today.minusDays(5), "09:30 AM", "Skin rash & allergy");
        a2.setStatus(AppointmentStatus.COMPLETED);
        a2.setDoctorNotes("Mild dermatitis");
        a2.setPrescription("Tab Allegra 120mg");
        appointmentRepository.save(a2);

        // Upcoming Confirmed 1 (Rahul Sharma with Dr. Amit Verma)
        Appointment a3 = new Appointment(patient1, d6, today.plusDays(1), "10:00 AM", "Routine checkup");
        a3.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(a3);

        // Upcoming Confirmed 2 (Vikram Sundaram with Dr. Arvind Swaminathan)
        Appointment a4 = new Appointment(patient3, d4, today.plusDays(2), "09:00 AM", "Knee joint pain");
        a4.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(a4);

        // Upcoming Confirmed 3 (Ananya Patel with Dr. Rajesh Iyer)
        Appointment a5 = new Appointment(patient2, d1, today.plusDays(3), "02:30 PM", "Cardiac follow-up");
        a5.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(a5);

        System.out.println(">>> CareConnect Indian Demo Data Successfully Initialized! <<<");
    }
}
