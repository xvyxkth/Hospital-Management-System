package com.hospital.gateway.config;

import com.hospital.gateway.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    
    @Autowired
    private AuthenticationFilter authenticationFilter;
    
    @Value("${PATIENT_SERVICE_URL:http://localhost:8081}")
    private String patientServiceUrl;
    
    @Value("${DOCTOR_SERVICE_URL:http://localhost:8082}")
    private String doctorServiceUrl;
    
    @Value("${APPOINTMENT_SERVICE_URL:http://localhost:8083}")
    private String appointmentServiceUrl;
    
    @Value("${BILLING_SERVICE_URL:http://localhost:8084}")
    private String billingServiceUrl;
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Patient Service Routes with Auth
                .route("patient-service", r -> r
                        .path("/api/v1/patients/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri(patientServiceUrl))
                
                // Doctor Service Routes with Auth
                .route("doctor-service", r -> r
                        .path("/api/v1/doctors/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri(doctorServiceUrl))
                
                // Appointment Service Routes with Auth
                .route("appointment-service", r -> r
                        .path("/api/v1/appointments/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri(appointmentServiceUrl))
                
                // Billing Service Routes with Auth
                .route("billing-service", r -> r
                        .path("/api/v1/invoices/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri(billingServiceUrl))
                
                // Auth Service Routes (no auth required)
                .route("auth-service", r -> r
                        .path("/api/v1/auth/**")
                        .uri("http://localhost:8080"))
                
                .build();
    }
}
