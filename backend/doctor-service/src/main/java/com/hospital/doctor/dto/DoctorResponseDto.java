package com.hospital.doctor.dto;

import com.hospital.doctor.model.Doctor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponseDto {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String licenseNumber;
    private String specialization;
    private String qualification;
    private Integer experienceYears;
    private Double consultationFee;
    private String department;
    private String roomNumber;
    private Set<String> availableDays;
    private String startTime;
    private String endTime;
    private Boolean isAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public DoctorResponseDto(Doctor doctor) {
        this.id = doctor.getId();
        this.firstName = doctor.getFirstName();
        this.lastName = doctor.getLastName();
        this.email = doctor.getEmail();
        this.phone = doctor.getPhone();
        this.licenseNumber = doctor.getLicenseNumber();
        this.specialization = doctor.getSpecialization();
        this.qualification = doctor.getQualification();
        this.experienceYears = doctor.getExperienceYears();
        this.consultationFee = doctor.getConsultationFee();
        this.department = doctor.getDepartment();
        this.roomNumber = doctor.getRoomNumber();
        this.availableDays = doctor.getAvailableDays();
        this.startTime = doctor.getStartTime();
        this.endTime = doctor.getEndTime();
        this.isAvailable = doctor.getIsAvailable();
        this.createdAt = doctor.getCreatedAt();
        this.updatedAt = doctor.getUpdatedAt();
    }
}
