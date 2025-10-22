package com.hospital.doctor.repository;

import com.hospital.doctor.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    @Query("SELECT d FROM Doctor d WHERE d.deletedAt IS NULL")
    List<Doctor> findAllActive();
    
    @Query("SELECT d FROM Doctor d WHERE d.id = :id AND d.deletedAt IS NULL")
    Optional<Doctor> findActiveById(Long id);
    
    @Query("SELECT d FROM Doctor d WHERE d.email = :email AND d.deletedAt IS NULL")
    Optional<Doctor> findByEmail(String email);
    
    @Query("SELECT d FROM Doctor d WHERE d.licenseNumber = :licenseNumber AND d.deletedAt IS NULL")
    Optional<Doctor> findByLicenseNumber(String licenseNumber);
    
    @Query("SELECT d FROM Doctor d WHERE d.specialization = :specialization AND d.deletedAt IS NULL")
    List<Doctor> findBySpecialization(@Param("specialization") String specialization);
    
    @Query("SELECT d FROM Doctor d WHERE d.department = :department AND d.deletedAt IS NULL")
    List<Doctor> findByDepartment(@Param("department") String department);
    
    @Query("SELECT d FROM Doctor d WHERE d.isAvailable = true AND d.deletedAt IS NULL")
    List<Doctor> findAvailableDoctors();
    
    @Query("SELECT d FROM Doctor d WHERE " +
           "(LOWER(d.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.specialization) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.department) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "d.deletedAt IS NULL")
    List<Doctor> searchDoctors(@Param("search") String search);
}
