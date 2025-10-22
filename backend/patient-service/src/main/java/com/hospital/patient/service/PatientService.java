package com.hospital.patient.service;

import com.hospital.common.exception.ResourceNotFoundException;
import com.hospital.patient.dto.PatientRequestDto;
import com.hospital.patient.dto.PatientResponseDto;
import com.hospital.patient.model.Patient;
import com.hospital.patient.repository.PatientRepository;
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
public class PatientService {
    
    private final PatientRepository patientRepository;
    
    @Transactional
    @CachePut(value = "patients", key = "#result.id")
    public PatientResponseDto createPatient(PatientRequestDto requestDto) {
        log.info("Creating new patient: {} {}", requestDto.getFirstName(), requestDto.getLastName());
        
        // Check for duplicate email
        if (requestDto.getEmail() != null && !requestDto.getEmail().isEmpty()) {
            patientRepository.findByEmail(requestDto.getEmail()).ifPresent(p -> {
                throw new IllegalArgumentException("Patient with email " + requestDto.getEmail() + " already exists");
            });
        }
        
        // Check for duplicate phone
        patientRepository.findByPhone(requestDto.getPhone()).ifPresent(p -> {
            throw new IllegalArgumentException("Patient with phone " + requestDto.getPhone() + " already exists");
        });
        
        Patient patient = new Patient();
        mapDtoToEntity(requestDto, patient);
        
        Patient savedPatient = patientRepository.save(patient);
        log.info("Patient created successfully with ID: {}", savedPatient.getId());
        
        return new PatientResponseDto(savedPatient);
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "patients", key = "#id")
    public PatientResponseDto getPatientById(Long id) {
        log.debug("Fetching patient with ID: {}", id);
        Patient patient = patientRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));
        return new PatientResponseDto(patient);
    }
    
    @Transactional(readOnly = true)
    public List<PatientResponseDto> getAllPatients() {
        log.debug("Fetching all active patients");
        return patientRepository.findAllActive().stream()
                .map(PatientResponseDto::new)
                .collect(Collectors.toList());
    }
    
    @Transactional
    @CachePut(value = "patients", key = "#id")
    public PatientResponseDto updatePatient(Long id, PatientRequestDto requestDto) {
        log.info("Updating patient with ID: {}", id);
        
        Patient patient = patientRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));
        
        // Check for duplicate email (excluding current patient)
        if (requestDto.getEmail() != null && !requestDto.getEmail().isEmpty()) {
            patientRepository.findByEmail(requestDto.getEmail()).ifPresent(p -> {
                if (!p.getId().equals(id)) {
                    throw new IllegalArgumentException("Patient with email " + requestDto.getEmail() + " already exists");
                }
            });
        }
        
        // Check for duplicate phone (excluding current patient)
        patientRepository.findByPhone(requestDto.getPhone()).ifPresent(p -> {
            if (!p.getId().equals(id)) {
                throw new IllegalArgumentException("Patient with phone " + requestDto.getPhone() + " already exists");
            }
        });
        
        mapDtoToEntity(requestDto, patient);
        Patient updatedPatient = patientRepository.save(patient);
        
        log.info("Patient updated successfully with ID: {}", id);
        return new PatientResponseDto(updatedPatient);
    }
    
    @Transactional
    @CacheEvict(value = "patients", key = "#id")
    public void deletePatient(Long id) {
        log.info("Soft deleting patient with ID: {}", id);
        
        Patient patient = patientRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));
        
        patient.setDeletedAt(LocalDateTime.now());
        patientRepository.save(patient);
        
        log.info("Patient soft deleted successfully with ID: {}", id);
    }
    
    @Transactional(readOnly = true)
    public List<PatientResponseDto> searchPatients(String search) {
        log.debug("Searching patients with query: {}", search);
        return patientRepository.searchPatients(search).stream()
                .map(PatientResponseDto::new)
                .collect(Collectors.toList());
    }
    
    private void mapDtoToEntity(PatientRequestDto dto, Patient patient) {
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setEmail(dto.getEmail());
        patient.setPhone(dto.getPhone());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setGender(dto.getGender());
        patient.setAddress(dto.getAddress());
        patient.setBloodGroup(dto.getBloodGroup());
        patient.setEmergencyContact(dto.getEmergencyContact());
        patient.setMedicalHistory(dto.getMedicalHistory());
        patient.setAllergies(dto.getAllergies());
    }
}
