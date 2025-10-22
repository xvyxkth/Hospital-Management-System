package com.hospital.doctor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;
    
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(name = "phone", nullable = false, length = 15)
    private String phone;
    
    @Column(name = "license_number", unique = true, nullable = false, length = 50)
    private String licenseNumber;
    
    @Column(name = "specialization", nullable = false, length = 100)
    private String specialization;
    
    @Column(name = "qualification", columnDefinition = "TEXT")
    private String qualification;
    
    @Column(name = "experience_years")
    private Integer experienceYears;
    
    @Column(name = "consultation_fee")
    private Double consultationFee;
    
    @Column(name = "department", length = 100)
    private String department;
    
    @Column(name = "room_number", length = 20)
    private String roomNumber;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "doctor_availability", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "day_of_week")
    private Set<String> availableDays = new HashSet<>();
    
    @Column(name = "start_time", length = 10)
    private String startTime;  // Format: "09:00"
    
    @Column(name = "end_time", length = 10)
    private String endTime;    // Format: "17:00"
    
    @Column(name = "is_available")
    private Boolean isAvailable = true;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
