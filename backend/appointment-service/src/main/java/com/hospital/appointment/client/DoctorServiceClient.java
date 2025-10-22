package com.hospital.appointment.client;

import com.hospital.appointment.dto.DoctorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class DoctorServiceClient {
    
    private final WebClient webClient;
    
    public DoctorServiceClient(@Qualifier("doctorWebClient") WebClient webClient) {
        this.webClient = webClient;
    }
    
    public DoctorDto getDoctorById(Long doctorId) {
        log.debug("Fetching doctor details from doctor-service for ID: {}", doctorId);
        
        try {
            return webClient.get()
                    .uri("/{id}", doctorId)
                    .retrieve()
                    .bodyToMono(ApiResponseWrapper.class)
                    .map(response -> response.getData())
                    .block();
        } catch (Exception e) {
            log.error("Error fetching doctor details for ID: {}", doctorId, e);
            throw new RuntimeException("Doctor with ID " + doctorId + " not found or service unavailable");
        }
    }
    
    // Wrapper class to match the ApiResponse structure
    private static class ApiResponseWrapper {
        private boolean success;
        private String message;
        private DoctorDto data;
        
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
        
        public DoctorDto getData() {
            return data;
        }
        
        public void setData(DoctorDto data) {
            this.data = data;
        }
    }
}
