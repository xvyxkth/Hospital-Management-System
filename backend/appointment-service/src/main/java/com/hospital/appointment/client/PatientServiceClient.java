package com.hospital.appointment.client;

import com.hospital.appointment.dto.PatientDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class PatientServiceClient {
    
    private final WebClient webClient;
    
    public PatientServiceClient(@Qualifier("patientWebClient") WebClient webClient) {
        this.webClient = webClient;
    }
    
    public PatientDto getPatientById(Long patientId) {
        log.debug("Fetching patient details from patient-service for ID: {}", patientId);
        
        try {
            return webClient.get()
                    .uri("/{id}", patientId)
                    .retrieve()
                    .bodyToMono(ApiResponseWrapper.class)
                    .map(response -> response.getData())
                    .block();
        } catch (Exception e) {
            log.error("Error fetching patient details for ID: {}", patientId, e);
            throw new RuntimeException("Patient with ID " + patientId + " not found or service unavailable");
        }
    }
    
    // Wrapper class to match the ApiResponse structure
    private static class ApiResponseWrapper {
        private boolean success;
        private String message;
        private PatientDto data;
        
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public PatientDto getData() {
            return data;
        }
        
        public void setData(PatientDto data) {
            this.data = data;
        }
    }
}
