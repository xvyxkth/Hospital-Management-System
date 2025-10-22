package com.hospital.appointment.service;

import com.hospital.appointment.client.DoctorServiceClient;
import com.hospital.appointment.client.PatientServiceClient;
import com.hospital.appointment.dto.*;
import com.hospital.appointment.model.Appointment;
import com.hospital.appointment.repository.AppointmentRepository;
import com.hospital.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final PatientServiceClient patientServiceClient;
    private final DoctorServiceClient doctorServiceClient;
    
    @Transactional
    public AppointmentResponseDto createAppointment(AppointmentRequestDto requestDto) {
        log.info("Creating appointment for patient {} with doctor {}", 
                requestDto.getPatientId(), requestDto.getDoctorId());
        
        // Validate patient exists
        PatientDto patient = patientServiceClient.getPatientById(requestDto.getPatientId());
        log.debug("Patient validated: {}", patient.getFullName());
        
        // Validate doctor exists and is available
        DoctorDto doctor = doctorServiceClient.getDoctorById(requestDto.getDoctorId());
        log.debug("Doctor validated: {}", doctor.getFullName());
        
        if (!doctor.getIsAvailable()) {
            throw new IllegalArgumentException("Doctor is not available for appointments");
        }
        
        // Check for conflicting appointments
        appointmentRepository.findConflictingAppointment(
                requestDto.getDoctorId(),
                requestDto.getAppointmentDate(),
                requestDto.getAppointmentTime()
        ).ifPresent(a -> {
            throw new IllegalArgumentException(
                    "Doctor already has an appointment at " + 
                    requestDto.getAppointmentTime() + " on " + 
                    requestDto.getAppointmentDate()
            );
        });
        
        Appointment appointment = new Appointment();
        appointment.setPatientId(requestDto.getPatientId());
        appointment.setDoctorId(requestDto.getDoctorId());
        appointment.setAppointmentDate(requestDto.getAppointmentDate());
        appointment.setAppointmentTime(requestDto.getAppointmentTime());
        appointment.setReason(requestDto.getReason());
        appointment.setNotes(requestDto.getNotes());
        appointment.setStatus(requestDto.getStatus() != null ? 
                requestDto.getStatus() : Appointment.AppointmentStatus.SCHEDULED);
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment created successfully with ID: {}", savedAppointment.getId());
        
        return enrichAppointmentResponse(savedAppointment, patient, doctor);
    }
    
    @Transactional(readOnly = true)
    public AppointmentResponseDto getAppointmentById(Long id) {
        log.debug("Fetching appointment with ID: {}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
        
        return enrichAppointmentResponse(appointment);
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAllAppointments() {
        log.debug("Fetching all appointments");
        return appointmentRepository.findAll().stream()
                .map(this::enrichAppointmentResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAppointmentsByPatient(Long patientId) {
        log.debug("Fetching appointments for patient: {}", patientId);
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(this::enrichAppointmentResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAppointmentsByDoctor(Long doctorId) {
        log.debug("Fetching appointments for doctor: {}", doctorId);
        return appointmentRepository.findByDoctorId(doctorId).stream()
                .map(this::enrichAppointmentResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAppointmentsByDate(LocalDate date) {
        log.debug("Fetching appointments for date: {}", date);
        return appointmentRepository.findByAppointmentDate(date).stream()
                .map(this::enrichAppointmentResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAppointmentsByStatus(Appointment.AppointmentStatus status) {
        log.debug("Fetching appointments with status: {}", status);
        return appointmentRepository.findByStatus(status).stream()
                .map(this::enrichAppointmentResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public AppointmentResponseDto updateAppointmentStatus(Long id, Appointment.AppointmentStatus status) {
        log.info("Updating appointment {} status to {}", id, status);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
        
        appointment.setStatus(status);
        
        if (status == Appointment.AppointmentStatus.CANCELLED) {
            appointment.setCancelledAt(LocalDateTime.now());
        } else if (status == Appointment.AppointmentStatus.COMPLETED) {
            appointment.setCompletedAt(LocalDateTime.now());
        }
        
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment status updated successfully");
        
        return enrichAppointmentResponse(updatedAppointment);
    }
    
    @Transactional
    public AppointmentResponseDto updateAppointmentDetails(Long id, String diagnosis, String prescription) {
        log.info("Updating appointment {} medical details", id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
        
        appointment.setDiagnosis(diagnosis);
        appointment.setPrescription(prescription);
        
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment medical details updated successfully");
        
        return enrichAppointmentResponse(updatedAppointment);
    }
    
    @Transactional
    public void deleteAppointment(Long id) {
        log.info("Deleting appointment with ID: {}", id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
        
        appointmentRepository.delete(appointment);
        log.info("Appointment deleted successfully");
    }
    
    private AppointmentResponseDto enrichAppointmentResponse(Appointment appointment) {
        try {
            PatientDto patient = patientServiceClient.getPatientById(appointment.getPatientId());
            DoctorDto doctor = doctorServiceClient.getDoctorById(appointment.getDoctorId());
            return enrichAppointmentResponse(appointment, patient, doctor);
        } catch (Exception e) {
            log.warn("Could not enrich appointment response: {}", e.getMessage());
            return new AppointmentResponseDto(appointment);
        }
    }
    
    private AppointmentResponseDto enrichAppointmentResponse(
            Appointment appointment, PatientDto patient, DoctorDto doctor) {
        AppointmentResponseDto response = new AppointmentResponseDto(appointment);
        response.setPatientName(patient.getFullName());
        response.setDoctorName(doctor.getFullName());
        response.setDoctorSpecialization(doctor.getSpecialization());
        return response;
    }
}
