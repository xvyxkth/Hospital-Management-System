package com.hospital.appointment.controller;

import com.hospital.appointment.dto.AppointmentRequestDto;
import com.hospital.appointment.dto.AppointmentResponseDto;
import com.hospital.appointment.model.Appointment;
import com.hospital.appointment.service.AppointmentService;
import com.hospital.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    
    private final AppointmentService appointmentService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> createAppointment(
            @Valid @RequestBody AppointmentRequestDto requestDto) {
        AppointmentResponseDto response = appointmentService.createAppointment(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Appointment created successfully", response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> getAppointmentById(@PathVariable Long id) {
        AppointmentResponseDto response = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<AppointmentResponseDto>>> getAllAppointments() {
        List<AppointmentResponseDto> response = appointmentService.getAllAppointments();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<AppointmentResponseDto>>> getAppointmentsByPatient(
            @PathVariable Long patientId) {
        List<AppointmentResponseDto> response = appointmentService.getAppointmentsByPatient(patientId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<ApiResponse<List<AppointmentResponseDto>>> getAppointmentsByDoctor(
            @PathVariable Long doctorId) {
        List<AppointmentResponseDto> response = appointmentService.getAppointmentsByDoctor(doctorId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/date/{date}")
    public ResponseEntity<ApiResponse<List<AppointmentResponseDto>>> getAppointmentsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AppointmentResponseDto> response = appointmentService.getAppointmentsByDate(date);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<AppointmentResponseDto>>> getAppointmentsByStatus(
            @PathVariable Appointment.AppointmentStatus status) {
        List<AppointmentResponseDto> response = appointmentService.getAppointmentsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam Appointment.AppointmentStatus status) {
        AppointmentResponseDto response = appointmentService.updateAppointmentStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Appointment status updated", response));
    }
    
    @PatchMapping("/{id}/medical-details")
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> updateMedicalDetails(
            @PathVariable Long id,
            @RequestBody Map<String, String> details) {
        String diagnosis = details.get("diagnosis");
        String prescription = details.get("prescription");
        AppointmentResponseDto response = appointmentService.updateAppointmentDetails(id, diagnosis, prescription);
        return ResponseEntity.ok(ApiResponse.success("Medical details updated", response));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.ok(ApiResponse.success("Appointment deleted successfully", null));
    }
}
