package com.careconnect.repository;

import com.careconnect.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findBySpecializationIgnoreCase(String specialization);

    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.specialization) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Doctor> searchDoctors(@Param("query") String query);

    Optional<Doctor> findByUserId(Long userId);

    @Query("SELECT DISTINCT d.specialization FROM Doctor d ORDER BY d.specialization ASC")
    List<String> findDistinctSpecializations();
}
