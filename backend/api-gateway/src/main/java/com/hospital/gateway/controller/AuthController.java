package com.hospital.gateway.controller;

import com.hospital.gateway.dto.AuthRequest;
import com.hospital.gateway.dto.AuthResponse;
import com.hospital.gateway.util.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final JwtUtil jwtUtil;
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    // In-memory user store for demo (in production, use a database)
    private static final Map<String, String> USERS = new HashMap<>();
    
    static {
        // Pre-populate with demo users
        USERS.put("admin", "admin123");
        USERS.put("doctor", "doctor123");
        USERS.put("patient", "patient123");
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());
        
        // Validate credentials
        if (!USERS.containsKey(request.getUsername()) || 
            !USERS.get(request.getUsername()).equals(request.getPassword())) {
            log.warn("Invalid credentials for user: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid username or password"));
        }
        
        // Generate JWT token
        String token = generateToken(request.getUsername());
        
        log.info("Login successful for user: {}", request.getUsername());
        
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUsername(request.getUsername());
        response.setExpiresIn(expiration);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        log.info("Registration attempt for user: {}", request.getUsername());
        
        // Check if user already exists
        if (USERS.containsKey(request.getUsername())) {
            log.warn("User already exists: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Username already exists"));
        }
        
        // Register user
        USERS.put(request.getUsername(), request.getPassword());
        
        // Generate JWT token
        String token = generateToken(request.getUsername());
        
        log.info("Registration successful for user: {}", request.getUsername());
        
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUsername(request.getUsername());
        response.setExpiresIn(expiration);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "Invalid authorization header"));
        }
        
        String token = authHeader.substring(7);
        boolean isValid = jwtUtil.validateToken(token);
        
        if (isValid) {
            String username = jwtUtil.getUsernameFromToken(token);
            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "username", username
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "Invalid or expired token"));
        }
    }
    
    private String generateToken(String username) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }
}
