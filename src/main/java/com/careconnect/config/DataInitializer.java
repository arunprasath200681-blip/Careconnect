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
                "Arun Kumar (Clinic Administrator)",
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
                "MBBS, MD, DM (Cardiology) - AIIMS New Delhi",
                15,
                1000.0, // ₹1,000 Consultation Fee
                "Senior Interventional Cardiologist specializing in preventive heart health, hypertension management, and ECG/Echocardiogram evaluations.",
                "Mon, Tue, Wed, Fri",
                "09:30 AM, 10:30 AM, 11:30 AM, 02:30 PM, 04:00 PM",
                "https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=400&auto=format&fit=crop&q=80"
        );

        // Doctor 2: Neurology (NIMHANS Bangalore)
        Doctor d2 = new Doctor(
                docUser2,
                "Dr. Suresh Menon",
                "Neurology",
                "MBBS, MD, DM (Neurology) - NIMHANS Bangalore",
                14,
                1200.0, // ₹1,200 Consultation Fee
                "Neurologist with extensive clinical expertise in chronic migraines, nerve disorders, stroke rehabilitation, and cognitive health.",
                "Mon, Wed, Thu",
                "10:00 AM, 11:30 AM, 02:00 PM, 04:30 PM",
                "https://images.unsplash.com/photo-1537368910025-700350fe46c7?w=400&auto=format&fit=crop&q=80"
        );

        // Doctor 3: Dermatology (CMC Vellore)
        Doctor d3 = new Doctor(
                docUser3,
                "Dr. Priya Sharma",
                "Dermatology",
                "MBBS, MD (Dermatology) - CMC Vellore",
                10,
                750.0, // ₹750 Consultation Fee
                "Consultant Dermatologist specializing in allergic skin rashes, acne scar reduction, clinical eczema, and hair fall management.",
                "Tue, Thu, Fri, Sat",
                "09:30 AM, 11:00 AM, 01:30 PM, 03:30 PM, 05:00 PM",
                "https://images.unsplash.com/photo-1594824813590-79888981f7c3?w=400&auto=format&fit=crop&q=80"
        );

        // Doctor 4: Orthopedics (Madras Medical College)
        Doctor d4 = new Doctor(
                docUser4,
                "Dr. Arvind Swaminathan",
                "Orthopedics",
                "MBBS, MS (Ortho), Fellowship in Joint Replacement - MMC Chennai",
                16,
                900.0, // ₹900 Consultation Fee
                "Orthopedic surgeon specializing in sports knee injuries, spondylosis, joint pain therapies, and fracture trauma management.",
                "Mon, Tue, Thu",
                "09:00 AM, 10:30 AM, 02:30 PM, 04:00 PM",
                "https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=400&auto=format&fit=crop&q=80"
        );

        // Doctor 5: Pediatrics (PGIMER Chandigarh)
        Doctor d5 = new Doctor(
                docUser5,
                "Dr. Kavitha Raman",
                "Pediatrics",
                "MBBS, MD (Pediatrics) - PGIMER Chandigarh",
                11,
                650.0, // ₹650 Consultation Fee
                "Senior Pediatrician dedicated to infant nutrition, routine childhood vaccination programs, seasonal flu care, and child wellness.",
                "Mon, Tue, Wed, Thu, Fri",
                "09:30 AM, 11:00 AM, 12:30 PM, 02:30 PM, 04:00 PM",
                "https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=400&auto=format&fit=crop&q=80"
        );

        // Doctor 6: General Medicine (KMC Manipal)
        Doctor d6 = new Doctor(
                docUser6,
                "Dr. Amit Verma",
                "General Medicine",
                "MBBS, MD (Internal Medicine) - KMC Manipal",
                9,
                500.0, // ₹500 Consultation Fee
                "General physician focusing on diabetes care, seasonal viral fevers, hypertension, thyroid disorders, and routine health checkups.",
                "Mon, Tue, Wed, Thu, Fri, Sat",
                "08:30 AM, 10:00 AM, 11:30 AM, 02:00 PM, 03:30 PM, 05:00 PM",
                "https://images.unsplash.com/photo-1579684385127-1ef15d508118?w=400&auto=format&fit=crop&q=80"
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
        Appointment a1 = new Appointment(patient1, d1, today.minusDays(3), "10:30 AM", "Mild chest heaviness during morning brisk walk and palpitations.");
        a1.setStatus(AppointmentStatus.COMPLETED);
        a1.setDoctorNotes("Blood pressure 130/84 mmHg. Resting ECG normal sinus rhythm. Mild exertion fatigue, advised lifestyle modification.");
        a1.setPrescription("1. Tab. Ecosprin 75mg - 1 tablet once daily after lunch for 30 days.\n2. Tab. Telma 40mg - 1 tablet early morning before breakfast.\n3. Routine Lipid Profile & HbA1c test after 4 weeks.");
        appointmentRepository.save(a1);

        // Past Completed Consultation 2 (Ananya Patel with Dr. Priya Sharma)
        Appointment a2 = new Appointment(patient2, d3, today.minusDays(5), "09:30 AM", "Seasonal skin itching and allergic redness on arms due to heat.");
        a2.setStatus(AppointmentStatus.COMPLETED);
        a2.setDoctorNotes("Contact allergic dermatitis. Mild erythema observed, no secondary bacterial infection.");
        a2.setPrescription("1. Tab. Allegra 120mg - 1 tablet daily at night for 5 days.\n2. Calamine lotion - Apply twice daily on affected area.\n3. Avoid harsh scented soaps for 1 week.");
        appointmentRepository.save(a2);

        // Upcoming Confirmed 1 (Rahul Sharma with Dr. Amit Verma)
        Appointment a3 = new Appointment(patient1, d6, today.plusDays(1), "10:00 AM", "Annual routine health check-up, fasting blood sugar review.");
        a3.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(a3);

        // Upcoming Confirmed 2 (Vikram Sundaram with Dr. Arvind Swaminathan)
        Appointment a4 = new Appointment(patient3, d4, today.plusDays(2), "09:00 AM", "Right knee joint pain and swelling following badminton match.");
        a4.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(a4);

        // Upcoming Confirmed 3 (Ananya Patel with Dr. Rajesh Iyer)
        Appointment a5 = new Appointment(patient2, d1, today.plusDays(3), "02:30 PM", "Routine follow-up consultation for family cardiac history evaluation.");
        a5.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(a5);

        System.out.println(">>> CareConnect Indian Demo Data Successfully Initialized! <<<");
    }
}
