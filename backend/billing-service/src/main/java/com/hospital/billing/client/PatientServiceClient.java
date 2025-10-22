package com.hospital.billing.client;

import com.hospital.billing.dto.PatientDto;
import com.hospital.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class PatientServiceClient {
    
    @Qualifier("patientWebClient")
    private final WebClient patientWebClient;
    
    public PatientDto getPatientById(Long patientId) {
        log.debug("Fetching patient with ID: {}", patientId);
        
        try {
            ApiResponse<PatientDto> response = patientWebClient
                    .get()
                    .uri("/{id}", patientId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<PatientDto>>() {})
                    .block();
            
            return response != null ? response.getData() : null;
        } catch (Exception e) {
            log.error("Error fetching patient with ID {}: {}", patientId, e.getMessage());
            throw new RuntimeException("Failed to fetch patient details: " + e.getMessage());
        }
    }
}
