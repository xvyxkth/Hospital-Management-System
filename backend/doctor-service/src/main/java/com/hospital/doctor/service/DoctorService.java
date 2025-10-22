package com.hospital.doctor.service;

import com.hospital.common.exception.ResourceNotFoundException;
import com.hospital.doctor.dto.DoctorRequestDto;
import com.hospital.doctor.dto.DoctorResponseDto;
import com.hospital.doctor.model.Doctor;
import com.hospital.doctor.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {
    
    private final DoctorRepository doctorRepository;
    
    @Transactional
    @CachePut(value = "doctors", key = "#result.id")
    public DoctorResponseDto createDoctor(DoctorRequestDto requestDto) {
        log.info("Creating new doctor: {} {}", requestDto.getFirstName(), requestDto.getLastName());
        
        // Check for duplicate email
        doctorRepository.findByEmail(requestDto.getEmail()).ifPresent(d -> {
            throw new IllegalArgumentException("Doctor with email " + requestDto.getEmail() + " already exists");
        });
        
        // Check for duplicate license number
        doctorRepository.findByLicenseNumber(requestDto.getLicenseNumber()).ifPresent(d -> {
            throw new IllegalArgumentException("Doctor with license number " + requestDto.getLicenseNumber() + " already exists");
        });
        
        Doctor doctor = new Doctor();
        mapDtoToEntity(requestDto, doctor);
        
        Doctor savedDoctor = doctorRepository.save(doctor);
        log.info("Doctor created successfully with ID: {}", savedDoctor.getId());
        
        return new DoctorResponseDto(savedDoctor);
    }
    
    @Transactional(readOnly = true)
    // Removed @Cacheable to avoid lazy initialization issues with availableDays collection
    public DoctorResponseDto getDoctorById(Long id) {
        log.debug("Fetching doctor with ID: {}", id);
        Doctor doctor = doctorRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", id));
        return new DoctorResponseDto(doctor);
    }
    
    @Transactional(readOnly = true)
    public List<DoctorResponseDto> getAllDoctors() {
        log.debug("Fetching all active doctors");
        return doctorRepository.findAllActive().stream()
                .map(DoctorResponseDto::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<DoctorResponseDto> getDoctorsBySpecialization(String specialization) {
        log.debug("Fetching doctors with specialization: {}", specialization);
        return doctorRepository.findBySpecialization(specialization).stream()
                .map(DoctorResponseDto::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<DoctorResponseDto> getDoctorsByDepartment(String department) {
        log.debug("Fetching doctors in department: {}", department);
        return doctorRepository.findByDepartment(department).stream()
                .map(DoctorResponseDto::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<DoctorResponseDto> getAvailableDoctors() {
        log.debug("Fetching available doctors");
        return doctorRepository.findAvailableDoctors().stream()
                .map(DoctorResponseDto::new)
                .collect(Collectors.toList());
    }
    
    @Transactional
    @CachePut(value = "doctors", key = "#id")
    public DoctorResponseDto updateDoctor(Long id, DoctorRequestDto requestDto) {
        log.info("Updating doctor with ID: {}", id);
        
        Doctor doctor = doctorRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", id));
        
        // Check for duplicate email (excluding current doctor)
        doctorRepository.findByEmail(requestDto.getEmail()).ifPresent(d -> {
            if (!d.getId().equals(id)) {
                throw new IllegalArgumentException("Doctor with email " + requestDto.getEmail() + " already exists");
            }
        });
        
        // Check for duplicate license number (excluding current doctor)
        doctorRepository.findByLicenseNumber(requestDto.getLicenseNumber()).ifPresent(d -> {
            if (!d.getId().equals(id)) {
                throw new IllegalArgumentException("Doctor with license number " + requestDto.getLicenseNumber() + " already exists");
            }
        });
        
        mapDtoToEntity(requestDto, doctor);
        Doctor updatedDoctor = doctorRepository.save(doctor);
        
        log.info("Doctor updated successfully with ID: {}", id);
        return new DoctorResponseDto(updatedDoctor);
    }
    
    @Transactional
    @CacheEvict(value = "doctors", key = "#id")
    public void deleteDoctor(Long id) {
        log.info("Soft deleting doctor with ID: {}", id);
        
        Doctor doctor = doctorRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", id));
        
        doctor.setDeletedAt(LocalDateTime.now());
        doctor.setIsAvailable(false);
        doctorRepository.save(doctor);
        
        log.info("Doctor soft deleted successfully with ID: {}", id);
    }
    
    @Transactional
    @CachePut(value = "doctors", key = "#id")
    public DoctorResponseDto updateAvailability(Long id, Boolean isAvailable) {
        log.info("Updating availability for doctor with ID: {} to {}", id, isAvailable);
        
        Doctor doctor = doctorRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", id));
        
        doctor.setIsAvailable(isAvailable);
        Doctor updatedDoctor = doctorRepository.save(doctor);
        
        log.info("Doctor availability updated successfully");
        return new DoctorResponseDto(updatedDoctor);
    }
    
    @Transactional(readOnly = true)
    public List<DoctorResponseDto> searchDoctors(String search) {
        log.debug("Searching doctors with query: {}", search);
        return doctorRepository.searchDoctors(search).stream()
                .map(DoctorResponseDto::new)
                .collect(Collectors.toList());
    }
    
    private void mapDtoToEntity(DoctorRequestDto dto, Doctor doctor) {
        doctor.setFirstName(dto.getFirstName());
        doctor.setLastName(dto.getLastName());
        doctor.setEmail(dto.getEmail());
        doctor.setPhone(dto.getPhone());
        doctor.setLicenseNumber(dto.getLicenseNumber());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setQualification(dto.getQualification());
        doctor.setExperienceYears(dto.getExperienceYears());
        doctor.setConsultationFee(dto.getConsultationFee());
        doctor.setDepartment(dto.getDepartment());
        doctor.setRoomNumber(dto.getRoomNumber());
        doctor.setAvailableDays(dto.getAvailableDays());
        doctor.setStartTime(dto.getStartTime());
        doctor.setEndTime(dto.getEndTime());
        doctor.setIsAvailable(dto.getIsAvailable());
    }
}
