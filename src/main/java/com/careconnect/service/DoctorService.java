package com.careconnect.service;

import com.careconnect.dto.DoctorRequest;
import com.careconnect.entity.Doctor;
import com.careconnect.entity.Role;
import com.careconnect.entity.User;
import com.careconnect.exception.BadRequestException;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.DoctorRepository;
import com.careconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public DoctorService(DoctorRepository doctorRepository, UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + id));
    }

    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        if (specialization == null || specialization.isBlank() || specialization.equalsIgnoreCase("all")) {
            return doctorRepository.findAll();
        }
        return doctorRepository.findBySpecializationIgnoreCase(specialization);
    }

    public List<Doctor> searchDoctors(String query) {
        if (query == null || query.isBlank()) {
            return doctorRepository.findAll();
        }
        return doctorRepository.searchDoctors(query.trim());
    }

    public List<String> getSpecializations() {
        return doctorRepository.findDistinctSpecializations();
    }

    private void validateDoctorRequest(DoctorRequest req) {
        if (req == null) {
            throw new BadRequestException("Doctor details cannot be empty.");
        }
        if (req.getName() == null || req.getName().trim().isEmpty()) {
            throw new BadRequestException("Doctor name is required.");
        }
        if (req.getSpecialization() == null || req.getSpecialization().trim().isEmpty()) {
            throw new BadRequestException("Specialization is required.");
        }
        if (req.getQualification() == null || req.getQualification().trim().isEmpty()) {
            throw new BadRequestException("Qualification is required.");
        }
        if (req.getExperienceYears() == null || req.getExperienceYears() < 0) {
            throw new BadRequestException("Valid years of experience is required.");
        }
        if (req.getConsultationFee() == null || req.getConsultationFee() <= 0) {
            throw new BadRequestException("Consultation fee must be greater than 0.");
        }
        if (req.getAvailableDays() == null || req.getAvailableDays().trim().isEmpty()) {
            throw new BadRequestException("Available days are required.");
        }
        if (req.getTimeSlots() == null || req.getTimeSlots().trim().isEmpty()) {
            throw new BadRequestException("Time slots are required.");
        }
    }

    @Transactional
    public Doctor createDoctor(DoctorRequest req) {
        validateDoctorRequest(req);

        User user = null;
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            String email = req.getEmail().toLowerCase().trim();
            user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = new User(
                        req.getName(),
                        email,
                        "doctor123",
                        "000-000-0000",
                        Role.DOCTOR
                    );
                return userRepository.save(newUser);
            });
        }

        String img = req.getImageUrl();
        if (img == null || img.isBlank()) {
            img = "https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=300&h=300&fit=crop&crop=faces";
        }

        Doctor doctor = new Doctor(
                user,
                req.getName().trim(),
                req.getSpecialization().trim(),
                req.getQualification().trim(),
                req.getExperienceYears(),
                req.getConsultationFee(),
                req.getBio(),
                req.getAvailableDays().trim(),
                req.getTimeSlots().trim(),
                img
        );

        return doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor updateDoctor(Long id, DoctorRequest req) {
        validateDoctorRequest(req);
        Doctor doctor = getDoctorById(id);
        doctor.setName(req.getName().trim());
        doctor.setSpecialization(req.getSpecialization().trim());
        doctor.setQualification(req.getQualification().trim());
        doctor.setExperienceYears(req.getExperienceYears());
        doctor.setConsultationFee(req.getConsultationFee());
        doctor.setBio(req.getBio());
        doctor.setAvailableDays(req.getAvailableDays().trim());
        doctor.setTimeSlots(req.getTimeSlots().trim());
        if (req.getImageUrl() != null && !req.getImageUrl().isBlank()) {
            doctor.setImageUrl(req.getImageUrl());
        }
        return doctorRepository.save(doctor);
    }

    @Transactional
    public void deleteDoctor(Long id) {
        Doctor doctor = getDoctorById(id);
        doctorRepository.delete(doctor);
    }
}
