package com.careconnect.service;

import com.careconnect.dto.AppointmentRequest;
import com.careconnect.dto.AppointmentResponse;
import com.careconnect.dto.PrescriptionRequest;
import com.careconnect.entity.Appointment;
import com.careconnect.entity.AppointmentStatus;
import com.careconnect.entity.Doctor;
import com.careconnect.entity.Role;
import com.careconnect.entity.User;
import com.careconnect.exception.BadRequestException;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.AppointmentRepository;
import com.careconnect.repository.DoctorRepository;
import com.careconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AppointmentResponse bookAppointment(AppointmentRequest req) {
        if (req.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot book appointments for past dates.");
        }

        User patient = userRepository.findById(req.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + req.getPatientId()));

        Doctor doctor = doctorRepository.findById(req.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + req.getDoctorId()));

        // Check if doctor is already booked at that slot and date (and not cancelled)
        Optional<Appointment> existingBooking = appointmentRepository
                .findByDoctorIdAndAppointmentDateAndTimeSlot(doctor.getId(), req.getAppointmentDate(), req.getTimeSlot().trim());

        if (existingBooking.isPresent() && existingBooking.get().getStatus() != AppointmentStatus.CANCELLED) {
            throw new BadRequestException("The time slot '" + req.getTimeSlot() + "' on " 
                    + req.getAppointmentDate() + " is already booked for Dr. " + doctor.getName() 
                    + ". Please select another available time slot.");
        }

        Appointment appointment = new Appointment(
                patient,
                doctor,
                req.getAppointmentDate(),
                req.getTimeSlot().trim(),
                req.getSymptoms().trim()
        );
        appointment.setStatus(AppointmentStatus.CONFIRMED);

        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentResponse.fromEntity(saved);
    }

    public Map<String, Object> getDoctorAvailability(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));

        List<String> allSlots = Arrays.stream(doctor.getTimeSlots().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        List<String> bookedSlots = appointmentRepository.findBookedSlots(doctorId, date);

        List<Map<String, Object>> slotStatuses = new ArrayList<>();
        for (String slot : allSlots) {
            Map<String, Object> item = new HashMap<>();
            item.put("slot", slot);
            item.put("available", !bookedSlots.contains(slot));
            slotStatuses.add(item);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("doctorId", doctor.getId());
        response.put("doctorName", doctor.getName());
        response.put("date", date);
        response.put("availableDays", doctor.getAvailableDays());
        response.put("slots", slotStatuses);
        return response;
    }

    public List<AppointmentResponse> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByAppointmentDateDescTimeSlotDesc(patientId)
                .stream()
                .map(AppointmentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponse> getDoctorAppointments(Long doctorId) {
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateAscTimeSlotAsc(doctorId)
                .stream()
                .map(AppointmentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Appointment::getAppointmentDate).reversed())
                .map(AppointmentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse updateStatus(Long appointmentId, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        appointment.setStatus(status);
        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentResponse.fromEntity(saved);
    }

    @Transactional
    public AppointmentResponse addPrescription(Long appointmentId, PrescriptionRequest req) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        if (req.getDoctorNotes() != null) {
            appointment.setDoctorNotes(req.getDoctorNotes().trim());
        }
        appointment.setPrescription(req.getPrescription().trim());
        appointment.setStatus(AppointmentStatus.COMPLETED);

        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentResponse.fromEntity(saved);
    }

    @Transactional
    public AppointmentResponse cancelAppointment(Long appointmentId, Long userId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Only the patient who booked it, the assigned doctor, or an admin can cancel
        boolean isPatient = appointment.getPatient().getId().equals(userId);
        boolean isDoctor = appointment.getDoctor().getUser() != null && appointment.getDoctor().getUser().getId().equals(userId);
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isPatient && !isDoctor && !isAdmin) {
            throw new BadRequestException("You do not have permission to cancel this appointment.");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentResponse.fromEntity(saved);
    }
}
