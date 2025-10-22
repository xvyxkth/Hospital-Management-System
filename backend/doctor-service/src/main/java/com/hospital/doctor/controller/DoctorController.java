package com.hospital.doctor.controller;

import com.hospital.common.dto.ApiResponse;
import com.hospital.doctor.dto.DoctorRequestDto;
import com.hospital.doctor.dto.DoctorResponseDto;
import com.hospital.doctor.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {
    
    private final DoctorService doctorService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<DoctorResponseDto>> createDoctor(
            @Valid @RequestBody DoctorRequestDto requestDto) {
        DoctorResponseDto response = doctorService.createDoctor(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Doctor created successfully", response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorResponseDto>> getDoctorById(@PathVariable Long id) {
        DoctorResponseDto response = doctorService.getDoctorById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<DoctorResponseDto>>> getAllDoctors() {
        List<DoctorResponseDto> response = doctorService.getAllDoctors();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<ApiResponse<List<DoctorResponseDto>>> getDoctorsBySpecialization(
            @PathVariable String specialization) {
        List<DoctorResponseDto> response = doctorService.getDoctorsBySpecialization(specialization);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/department/{department}")
    public ResponseEntity<ApiResponse<List<DoctorResponseDto>>> getDoctorsByDepartment(
            @PathVariable String department) {
        List<DoctorResponseDto> response = doctorService.getDoctorsByDepartment(department);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<DoctorResponseDto>>> getAvailableDoctors() {
        List<DoctorResponseDto> response = doctorService.getAvailableDoctors();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorResponseDto>> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorRequestDto requestDto) {
        DoctorResponseDto response = doctorService.updateDoctor(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Doctor updated successfully", response));
    }
    
    @PatchMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<DoctorResponseDto>> updateAvailability(
            @PathVariable Long id,
            @RequestParam Boolean isAvailable) {
        DoctorResponseDto response = doctorService.updateAvailability(id, isAvailable);
        return ResponseEntity.ok(ApiResponse.success("Doctor availability updated", response));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.ok(ApiResponse.success("Doctor deleted successfully", null));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DoctorResponseDto>>> searchDoctors(
            @RequestParam String query) {
        List<DoctorResponseDto> response = doctorService.searchDoctors(query);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
