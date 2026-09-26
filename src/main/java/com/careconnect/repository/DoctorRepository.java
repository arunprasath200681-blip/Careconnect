package com.careconnect.repository;

import com.careconnect.entity.Doctor;
import com.careconnect.entity.Role;
import com.careconnect.entity.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class DoctorRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String BASE_SELECT = 
            "SELECT d.id AS d_id, d.user_id, d.name AS d_name, d.specialization, d.qualification, " +
            "d.experience_years, d.consultation_fee, d.bio, d.available_days, d.time_slots, d.image_url, " +
            "u.id AS u_id, u.full_name AS u_name, u.email AS u_email, u.password AS u_pwd, " +
            "u.phone AS u_phone, u.role AS u_role, u.created_at AS u_created_at " +
            "FROM doctors d " +
            "LEFT JOIN users u ON d.user_id = u.id ";

    private final RowMapper<Doctor> doctorRowMapper = (rs, rowNum) -> {
        Doctor d = new Doctor();
        d.setId(rs.getLong("d_id"));
        d.setName(rs.getString("d_name"));
        d.setSpecialization(rs.getString("specialization"));
        d.setQualification(rs.getString("qualification"));
        d.setExperienceYears(rs.getInt("experience_years"));
        d.setConsultationFee(rs.getDouble("consultation_fee"));
        d.setBio(rs.getString("bio"));
        d.setAvailableDays(rs.getString("available_days"));
        d.setTimeSlots(rs.getString("time_slots"));
        d.setImageUrl(rs.getString("image_url"));

        long userId = rs.getLong("u_id");
        if (!rs.wasNull()) {
            User u = new User();
            u.setId(userId);
            u.setFullName(rs.getString("u_name"));
            u.setEmail(rs.getString("u_email"));
            u.setPassword(rs.getString("u_pwd"));
            u.setPhone(rs.getString("u_phone"));
            u.setRole(Role.valueOf(rs.getString("u_role")));
            Timestamp ts = rs.getTimestamp("u_created_at");
            if (ts != null) {
                u.setCreatedAt(ts.toLocalDateTime());
            }
            d.setUser(u);
        }
        return d;
    };

    public DoctorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Doctor> findAll() {
        String sql = BASE_SELECT + "ORDER BY d.id ASC";
        return jdbcTemplate.query(sql, doctorRowMapper);
    }

    public Optional<Doctor> findById(Long id) {
        String sql = BASE_SELECT + "WHERE d.id = ?";
        try {
            Doctor doc = jdbcTemplate.queryForObject(sql, doctorRowMapper, id);
            return Optional.ofNullable(doc);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Doctor> findBySpecializationIgnoreCase(String specialization) {
        String sql = BASE_SELECT + "WHERE LOWER(d.specialization) = LOWER(?) ORDER BY d.id ASC";
        return jdbcTemplate.query(sql, doctorRowMapper, specialization);
    }

    public List<Doctor> searchDoctors(String query) {
        String param = "%" + query.toLowerCase() + "%";
        String sql = BASE_SELECT + "WHERE LOWER(d.name) LIKE ? OR LOWER(d.specialization) LIKE ? ORDER BY d.id ASC";
        return jdbcTemplate.query(sql, doctorRowMapper, param, param);
    }

    public Optional<Doctor> findByUserId(Long userId) {
        String sql = BASE_SELECT + "WHERE d.user_id = ?";
        try {
            Doctor doc = jdbcTemplate.queryForObject(sql, doctorRowMapper, userId);
            return Optional.ofNullable(doc);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<String> findDistinctSpecializations() {
        String sql = "SELECT DISTINCT specialization FROM doctors ORDER BY specialization ASC";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public Doctor save(Doctor doctor) {
        if (doctor.getId() == null) {
            String sql = "INSERT INTO doctors (user_id, name, specialization, qualification, " +
                    "experience_years, consultation_fee, bio, available_days, time_slots, image_url) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                if (doctor.getUser() != null && doctor.getUser().getId() != null) {
                    ps.setLong(1, doctor.getUser().getId());
                } else {
                    ps.setNull(1, Types.BIGINT);
                }
                ps.setString(2, doctor.getName());
                ps.setString(3, doctor.getSpecialization());
                ps.setString(4, doctor.getQualification());
                ps.setInt(5, doctor.getExperienceYears() != null ? doctor.getExperienceYears() : 0);
                ps.setDouble(6, doctor.getConsultationFee() != null ? doctor.getConsultationFee() : 0.0);
                ps.setString(7, doctor.getBio());
                ps.setString(8, doctor.getAvailableDays());
                ps.setString(9, doctor.getTimeSlots());
                ps.setString(10, doctor.getImageUrl());
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key != null) {
                doctor.setId(key.longValue());
            }
        } else {
            String sql = "UPDATE doctors SET user_id = ?, name = ?, specialization = ?, qualification = ?, " +
                    "experience_years = ?, consultation_fee = ?, bio = ?, available_days = ?, " +
                    "time_slots = ?, image_url = ? WHERE id = ?";
            Long userId = (doctor.getUser() != null) ? doctor.getUser().getId() : null;
            jdbcTemplate.update(sql, userId, doctor.getName(), doctor.getSpecialization(), doctor.getQualification(),
                    doctor.getExperienceYears(), doctor.getConsultationFee(), doctor.getBio(), doctor.getAvailableDays(),
                    doctor.getTimeSlots(), doctor.getImageUrl(), doctor.getId());
        }
        return doctor;
    }

    public void delete(Doctor doctor) {
        if (doctor != null && doctor.getId() != null) {
            deleteById(doctor.getId());
        }
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM doctors WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM doctors";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }
}
