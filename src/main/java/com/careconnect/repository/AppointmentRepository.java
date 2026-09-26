package com.careconnect.repository;

import com.careconnect.entity.Appointment;
import com.careconnect.entity.AppointmentStatus;
import com.careconnect.entity.Doctor;
import com.careconnect.entity.Role;
import com.careconnect.entity.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class AppointmentRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String BASE_SELECT =
            "SELECT a.id AS a_id, a.patient_id, a.doctor_id, a.appointment_date, a.time_slot, " +
            "a.symptoms, a.status, a.doctor_notes, a.prescription, a.created_at AS a_created_at, " +
            "p.id AS p_id, p.full_name AS p_name, p.email AS p_email, p.password AS p_pwd, " +
            "p.phone AS p_phone, p.role AS p_role, p.created_at AS p_created_at, " +
            "d.id AS d_id, d.user_id AS d_user_id, d.name AS d_name, d.specialization AS d_specialization, " +
            "d.qualification AS d_qualification, d.experience_years AS d_exp, d.consultation_fee AS d_fee, " +
            "d.bio AS d_bio, d.available_days AS d_days, d.time_slots AS d_slots, d.image_url AS d_img, " +
            "du.id AS du_id, du.full_name AS du_name, du.email AS du_email, du.password AS du_pwd, " +
            "du.phone AS du_phone, du.role AS du_role, du.created_at AS du_created_at " +
            "FROM appointments a " +
            "JOIN users p ON a.patient_id = p.id " +
            "JOIN doctors d ON a.doctor_id = d.id " +
            "LEFT JOIN users du ON d.user_id = du.id ";

    private final RowMapper<Appointment> appointmentRowMapper = (rs, rowNum) -> {
        Appointment a = new Appointment();
        a.setId(rs.getLong("a_id"));
        a.setAppointmentDate(rs.getDate("appointment_date").toLocalDate());
        a.setTimeSlot(rs.getString("time_slot"));
        a.setSymptoms(rs.getString("symptoms"));
        a.setStatus(AppointmentStatus.valueOf(rs.getString("status")));
        a.setDoctorNotes(rs.getString("doctor_notes"));
        a.setPrescription(rs.getString("prescription"));
        Timestamp aCreated = rs.getTimestamp("a_created_at");
        if (aCreated != null) {
            a.setCreatedAt(aCreated.toLocalDateTime());
        }

        // Patient
        User patient = new User();
        patient.setId(rs.getLong("p_id"));
        patient.setFullName(rs.getString("p_name"));
        patient.setEmail(rs.getString("p_email"));
        patient.setPassword(rs.getString("p_pwd"));
        patient.setPhone(rs.getString("p_phone"));
        patient.setRole(Role.valueOf(rs.getString("p_role")));
        Timestamp pCreated = rs.getTimestamp("p_created_at");
        if (pCreated != null) {
            patient.setCreatedAt(pCreated.toLocalDateTime());
        }
        a.setPatient(patient);

        // Doctor
        Doctor doctor = new Doctor();
        doctor.setId(rs.getLong("d_id"));
        doctor.setName(rs.getString("d_name"));
        doctor.setSpecialization(rs.getString("d_specialization"));
        doctor.setQualification(rs.getString("d_qualification"));
        doctor.setExperienceYears(rs.getInt("d_exp"));
        doctor.setConsultationFee(rs.getDouble("d_fee"));
        doctor.setBio(rs.getString("d_bio"));
        doctor.setAvailableDays(rs.getString("d_days"));
        doctor.setTimeSlots(rs.getString("d_slots"));
        doctor.setImageUrl(rs.getString("d_img"));

        long duId = rs.getLong("du_id");
        if (!rs.wasNull()) {
            User docUser = new User();
            docUser.setId(duId);
            docUser.setFullName(rs.getString("du_name"));
            docUser.setEmail(rs.getString("du_email"));
            docUser.setPassword(rs.getString("du_pwd"));
            docUser.setPhone(rs.getString("du_phone"));
            docUser.setRole(Role.valueOf(rs.getString("du_role")));
            doctor.setUser(docUser);
        }
        a.setDoctor(doctor);

        return a;
    };

    public AppointmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Appointment save(Appointment appointment) {
        if (appointment.getId() == null) {
            String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, time_slot, " +
                    "symptoms, status, doctor_notes, prescription, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            LocalDateTime created = appointment.getCreatedAt() != null ? appointment.getCreatedAt() : LocalDateTime.now();
            appointment.setCreatedAt(created);

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, appointment.getPatient().getId());
                ps.setLong(2, appointment.getDoctor().getId());
                ps.setDate(3, Date.valueOf(appointment.getAppointmentDate()));
                ps.setString(4, appointment.getTimeSlot());
                ps.setString(5, appointment.getSymptoms());
                ps.setString(6, appointment.getStatus() != null ? appointment.getStatus().name() : AppointmentStatus.PENDING.name());
                ps.setString(7, appointment.getDoctorNotes());
                ps.setString(8, appointment.getPrescription());
                ps.setTimestamp(9, Timestamp.valueOf(created));
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key != null) {
                appointment.setId(key.longValue());
            }
        } else {
            String sql = "UPDATE appointments SET patient_id = ?, doctor_id = ?, appointment_date = ?, " +
                    "time_slot = ?, symptoms = ?, status = ?, doctor_notes = ?, prescription = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    appointment.getPatient().getId(),
                    appointment.getDoctor().getId(),
                    Date.valueOf(appointment.getAppointmentDate()),
                    appointment.getTimeSlot(),
                    appointment.getSymptoms(),
                    appointment.getStatus().name(),
                    appointment.getDoctorNotes(),
                    appointment.getPrescription(),
                    appointment.getId());
        }
        return appointment;
    }

    public Optional<Appointment> findById(Long id) {
        String sql = BASE_SELECT + "WHERE a.id = ?";
        try {
            Appointment appt = jdbcTemplate.queryForObject(sql, appointmentRowMapper, id);
            return Optional.ofNullable(appt);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Appointment> findAll() {
        String sql = BASE_SELECT + "ORDER BY a.appointment_date DESC, a.time_slot DESC";
        return jdbcTemplate.query(sql, appointmentRowMapper);
    }

    public List<Appointment> findByPatientIdOrderByAppointmentDateDescTimeSlotDesc(Long patientId) {
        String sql = BASE_SELECT + "WHERE a.patient_id = ? ORDER BY a.appointment_date DESC, a.time_slot DESC";
        return jdbcTemplate.query(sql, appointmentRowMapper, patientId);
    }

    public List<Appointment> findByDoctorIdOrderByAppointmentDateAscTimeSlotAsc(Long doctorId) {
        String sql = BASE_SELECT + "WHERE a.doctor_id = ? ORDER BY a.appointment_date ASC, a.time_slot ASC";
        return jdbcTemplate.query(sql, appointmentRowMapper, doctorId);
    }

    public List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate appointmentDate) {
        String sql = BASE_SELECT + "WHERE a.doctor_id = ? AND a.appointment_date = ? ORDER BY a.time_slot ASC";
        return jdbcTemplate.query(sql, appointmentRowMapper, doctorId, Date.valueOf(appointmentDate));
    }

    public Optional<Appointment> findByDoctorIdAndAppointmentDateAndTimeSlot(Long doctorId, LocalDate appointmentDate, String timeSlot) {
        String sql = BASE_SELECT + "WHERE a.doctor_id = ? AND a.appointment_date = ? AND a.time_slot = ?";
        try {
            Appointment appt = jdbcTemplate.queryForObject(sql, appointmentRowMapper, doctorId, Date.valueOf(appointmentDate), timeSlot);
            return Optional.ofNullable(appt);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public long countByStatus(AppointmentStatus status) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE status = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, status.name());
        return count != null ? count : 0L;
    }

    public List<Appointment> findTop15ByOrderByCreatedAtDesc() {
        String sql = BASE_SELECT + "ORDER BY a.created_at DESC LIMIT 15";
        return jdbcTemplate.query(sql, appointmentRowMapper);
    }

    public List<String> findBookedSlots(Long doctorId, LocalDate date) {
        String sql = "SELECT time_slot FROM appointments WHERE doctor_id = ? AND appointment_date = ? AND status != 'CANCELLED'";
        return jdbcTemplate.queryForList(sql, String.class, doctorId, Date.valueOf(date));
    }

    public long countAppointmentsForDate(LocalDate date) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE appointment_date = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, Date.valueOf(date));
        return count != null ? count : 0L;
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM appointments";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }
}
