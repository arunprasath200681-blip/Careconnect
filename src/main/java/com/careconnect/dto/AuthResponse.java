package com.careconnect.dto;

import com.careconnect.entity.Role;

public class AuthResponse {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private Long doctorId; // populated if the user is a DOCTOR
    private String message;

    public AuthResponse() {
    }

    public AuthResponse(Long id, String fullName, String email, Role role, Long doctorId, String message) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.doctorId = doctorId;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
