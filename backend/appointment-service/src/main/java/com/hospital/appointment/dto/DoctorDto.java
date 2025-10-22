package com.hospital.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String specialization;
    private Boolean isAvailable;
    
    public String getFullName() {
        return "Dr. " + firstName + " " + lastName;
    }
}
