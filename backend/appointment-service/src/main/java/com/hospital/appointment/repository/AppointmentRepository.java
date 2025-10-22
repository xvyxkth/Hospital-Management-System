package com.hospital.appointment.repository;

import com.hospital.appointment.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    List<Appointment> findByPatientId(Long patientId);
    
    List<Appointment> findByDoctorId(Long doctorId);
    
    List<Appointment> findByAppointmentDate(LocalDate date);
    
    @Query("SELECT a FROM Appointment a WHERE a.status = :status")
    List<Appointment> findByStatus(@Param("status") Appointment.AppointmentStatus status);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId AND a.appointmentDate = :date")
    List<Appointment> findByDoctorIdAndDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);
    
    @Query("SELECT a FROM Appointment a WHERE a.patientId = :patientId AND a.appointmentDate = :date")
    List<Appointment> findByPatientIdAndDate(@Param("patientId") Long patientId, @Param("date") LocalDate date);
    
    @Query("SELECT a FROM Appointment a WHERE " +
           "a.doctorId = :doctorId AND " +
           "a.appointmentDate = :date AND " +
           "a.appointmentTime = :time AND " +
           "a.status NOT IN ('CANCELLED', 'NO_SHOW')")
    Optional<Appointment> findConflictingAppointment(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("time") LocalTime time);
    
    @Query("SELECT a FROM Appointment a WHERE " +
           "a.appointmentDate >= :startDate AND " +
           "a.appointmentDate <= :endDate")
    List<Appointment> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT a FROM Appointment a WHERE " +
           "a.doctorId = :doctorId AND " +
           "a.appointmentDate >= :startDate AND " +
           "a.appointmentDate <= :endDate")
    List<Appointment> findByDoctorIdAndDateRange(
            @Param("doctorId") Long doctorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
