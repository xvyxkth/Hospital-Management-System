package com.hospital.appointment.dto;

import com.hospital.appointment.model.Appointment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDto {
    
    private Long id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialization;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private Appointment.AppointmentStatus status;
    private String reason;
    private String notes;
    private String diagnosis;
    private String prescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime completedAt;
    
    public AppointmentResponseDto(Appointment appointment) {
        this.id = appointment.getId();
        this.patientId = appointment.getPatientId();
        this.doctorId = appointment.getDoctorId();
        this.appointmentDate = appointment.getAppointmentDate();
        this.appointmentTime = appointment.getAppointmentTime();
        this.status = appointment.getStatus();
        this.reason = appointment.getReason();
        this.notes = appointment.getNotes();
        this.diagnosis = appointment.getDiagnosis();
        this.prescription = appointment.getPrescription();
        this.createdAt = appointment.getCreatedAt();
        this.updatedAt = appointment.getUpdatedAt();
        this.cancelledAt = appointment.getCancelledAt();
        this.completedAt = appointment.getCompletedAt();
    }
}
