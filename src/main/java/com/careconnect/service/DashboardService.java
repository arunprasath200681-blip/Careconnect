package com.careconnect.service;

import com.careconnect.dto.AppointmentResponse;
import com.careconnect.dto.DashboardSummaryDto;
import com.careconnect.entity.Appointment;
import com.careconnect.entity.AppointmentStatus;
import com.careconnect.entity.Doctor;
import com.careconnect.entity.Role;
import com.careconnect.repository.AppointmentRepository;
import com.careconnect.repository.DoctorRepository;
import com.careconnect.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public DashboardService(UserRepository userRepository,
                            DoctorRepository doctorRepository,
                            AppointmentRepository appointmentRepository) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public DashboardSummaryDto getAdminDashboardSummary() {
        DashboardSummaryDto dto = new DashboardSummaryDto();

        long patientsCount = userRepository.countByRole(Role.PATIENT);
        long doctorsCount = doctorRepository.count();
        long totalAppts = appointmentRepository.count();

        long pending = appointmentRepository.countByStatus(AppointmentStatus.PENDING);
        long confirmed = appointmentRepository.countByStatus(AppointmentStatus.CONFIRMED);
        long completed = appointmentRepository.countByStatus(AppointmentStatus.COMPLETED);
        long cancelled = appointmentRepository.countByStatus(AppointmentStatus.CANCELLED);

        // Calculate revenue from CONFIRMED and COMPLETED visits
        List<Appointment> allAppointments = appointmentRepository.findAll();
        double revenue = allAppointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CONFIRMED || a.getStatus() == AppointmentStatus.COMPLETED)
                .mapToDouble(a -> a.getDoctor() != null ? a.getDoctor().getConsultationFee() : 0.0)
                .sum();

        // Specialization breakdown
        List<Doctor> allDoctors = doctorRepository.findAll();
        Map<String, Long> specStats = allDoctors.stream()
                .collect(Collectors.groupingBy(Doctor::getSpecialization, Collectors.counting()));

        // Recent appointments
        List<AppointmentResponse> recent = appointmentRepository.findTop15ByOrderByCreatedAtDesc()
                .stream()
                .map(AppointmentResponse::fromEntity)
                .collect(Collectors.toList());

        dto.setTotalPatients(patientsCount);
        dto.setTotalDoctors(doctorsCount);
        dto.setTotalAppointments(totalAppts);
        dto.setPendingAppointments(pending);
        dto.setConfirmedAppointments(confirmed);
        dto.setCompletedAppointments(completed);
        dto.setCancelledAppointments(cancelled);
        dto.setEstimatedRevenue(revenue);
        dto.setSpecializationStats(specStats);
        dto.setRecentAppointments(recent);

        return dto;
    }
}
