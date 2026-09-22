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
        if (userRepository.existsByEmail(req.getEmail().toLowerCase().trim())) {
            throw new BadRequestException("An account with email '" + req.getEmail() + "' already exists.");
        }

        User user = new User();
        user.setFullName(req.getFullName().trim());
        user.setEmail(req.getEmail().toLowerCase().trim());
        user.setPassword(req.getPassword()); // In a full prod system with Spring Security this would be BCryptPasswordEncoder
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
