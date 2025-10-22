package com.hospital.patient.dto;

import com.hospital.patient.model.Patient;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequestDto {
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone must be 10-15 digits")
    private String phone;
    
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    @NotNull(message = "Gender is required")
    private Patient.Gender gender;
    
    private String address;
    
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Blood group must be valid (e.g., A+, B-, O+)")
    private String bloodGroup;
    
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Emergency contact must be 10-15 digits")
    private String emergencyContact;
    
    private String medicalHistory;
    
    private String allergies;
}
