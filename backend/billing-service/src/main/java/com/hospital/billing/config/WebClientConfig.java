package com.hospital.billing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    
    @Value("${services.patient.url}")
    private String patientServiceUrl;
    
    @Value("${services.appointment.url}")
    private String appointmentServiceUrl;
    
    @Bean(name = "patientWebClient")
    public WebClient patientWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(patientServiceUrl)
                .build();
    }
    
    @Bean(name = "appointmentWebClient")
    public WebClient appointmentWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(appointmentServiceUrl)
                .build();
    }
}
