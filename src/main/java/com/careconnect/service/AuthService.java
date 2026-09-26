package com.careconnect.service;

import com.careconnect.dto.AuthRequest;
import com.careconnect.dto.AuthResponse;
import com.careconnect.dto.RegisterRequest;
import com.careconnect.entity.Doctor;
import com.careconnect.entity.Role;
import com.careconnect.entity.User;
import com.careconnect.exception.BadRequestException;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.DoctorRepository;
import com.careconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;

    public AuthService(UserRepository userRepository, DoctorRepository doctorRepository) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (req == null) {
            throw new BadRequestException("Registration request cannot be empty.");
        }
        if (req.getFullName() == null || req.getFullName().trim().isEmpty()) {
            throw new BadRequestException("Full name is required.");
        }
        if (req.getEmail() == null || req.getEmail().trim().isEmpty() || !req.getEmail().contains("@")) {
            throw new BadRequestException("A valid email address is required.");
        }
        if (req.getPassword() == null || req.getPassword().length() < 4) {
            throw new BadRequestException("Password must have at least 4 characters.");
        }
        if (req.getPhone() == null || req.getPhone().trim().isEmpty()) {
            throw new BadRequestException("Phone number is required.");
        }

        String normalizedEmail = req.getEmail().toLowerCase().trim();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("An account with email '" + req.getEmail() + "' already exists.");
        }

        User user = new User();
        user.setFullName(req.getFullName().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(req.getPassword());
        user.setPhone(req.getPhone().trim());
        user.setRole(req.getRole() != null ? req.getRole() : Role.PATIENT);

        User savedUser = userRepository.save(user);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                null,
                "Registration successful! Welcome to CareConnect."
        );
    }

    public AuthResponse login(AuthRequest req) {
        if (req == null || req.getEmail() == null || req.getEmail().trim().isEmpty() ||
            req.getPassword() == null || req.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Email and password are required.");
        }

        String email = req.getEmail().toLowerCase().trim();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid email or password."));

        if (!user.getPassword().equals(req.getPassword())) {
            throw new BadRequestException("Invalid email or password.");
        }

        Long doctorId = null;
        if (user.getRole() == Role.DOCTOR) {
            Optional<Doctor> doc = doctorRepository.findByUserId(user.getId());
            if (doc.isPresent()) {
                doctorId = doc.get().getId();
            }
        }

        return new AuthResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                doctorId,
                "Login successful! Welcome back, " + user.getFullName() + "."
        );
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }
}
