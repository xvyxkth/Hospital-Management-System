package com.hospital.billing.client;

import com.hospital.billing.dto.AppointmentDto;
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
public class AppointmentServiceClient {
    
    @Qualifier("appointmentWebClient")
    private final WebClient appointmentWebClient;
    
    public AppointmentDto getAppointmentById(Long appointmentId) {
        log.debug("Fetching appointment with ID: {}", appointmentId);
        
        try {
            ApiResponse<AppointmentDto> response = appointmentWebClient
                    .get()
                    .uri("/{id}", appointmentId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<AppointmentDto>>() {})
                    .block();
            
            return response != null ? response.getData() : null;
        } catch (Exception e) {
            log.error("Error fetching appointment with ID {}: {}", appointmentId, e.getMessage());
            throw new RuntimeException("Failed to fetch appointment details: " + e.getMessage());
        }
    }
}
