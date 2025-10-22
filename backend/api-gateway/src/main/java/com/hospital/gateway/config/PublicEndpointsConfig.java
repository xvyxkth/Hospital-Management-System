package com.hospital.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "public")
@Data
public class PublicEndpointsConfig {
    private List<String> endpoints;
}
