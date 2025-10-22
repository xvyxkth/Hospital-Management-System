package com.hospital.patient.repository;

import com.hospital.patient.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    @Query("SELECT p FROM Patient p WHERE p.deletedAt IS NULL")
    List<Patient> findAllActive();
    
    @Query("SELECT p FROM Patient p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Patient> findActiveById(Long id);
    
    @Query("SELECT p FROM Patient p WHERE p.email = :email AND p.deletedAt IS NULL")
    Optional<Patient> findByEmail(String email);
    
    @Query("SELECT p FROM Patient p WHERE p.phone = :phone AND p.deletedAt IS NULL")
    Optional<Patient> findByPhone(String phone);
    
    @Query("SELECT p FROM Patient p WHERE " +
           "(LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "p.phone LIKE CONCAT('%', :search, '%')) AND " +
           "p.deletedAt IS NULL")
    List<Patient> searchPatients(String search);
}
