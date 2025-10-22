package com.hospital.appointment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    
    @Value("${services.patient.url}")
    private String patientServiceUrl;
    
    @Value("${services.doctor.url}")
    private String doctorServiceUrl;
    
    @Bean(name = "patientWebClient")
    public WebClient patientWebClient() {
        return WebClient.builder()
                .baseUrl(patientServiceUrl)
                .build();
    }
    
    @Bean(name = "doctorWebClient")
    public WebClient doctorWebClient() {
        return WebClient.builder()
                .baseUrl(doctorServiceUrl)
                .build();
    }
}
