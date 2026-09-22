package com.careconnect.repository;

import com.careconnect.entity.Appointment;
import com.careconnect.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientIdOrderByAppointmentDateDescTimeSlotDesc(Long patientId);

    List<Appointment> findByDoctorIdOrderByAppointmentDateAscTimeSlotAsc(Long doctorId);

    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate appointmentDate);

    Optional<Appointment> findByDoctorIdAndAppointmentDateAndTimeSlot(Long doctorId, LocalDate appointmentDate, String timeSlot);

    long countByStatus(AppointmentStatus status);

    List<Appointment> findTop15ByOrderByCreatedAtDesc();

    @Query("SELECT a.timeSlot FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.status != 'CANCELLED'")
    List<String> findBookedSlots(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentDate = :date")
    long countAppointmentsForDate(@Param("date") LocalDate date);
}
