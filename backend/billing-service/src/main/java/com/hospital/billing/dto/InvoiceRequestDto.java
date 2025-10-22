package com.hospital.billing.dto;

import com.hospital.billing.model.Invoice.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequestDto {
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;
    
    @NotNull(message = "Consultation fee is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Consultation fee must be greater than 0")
    private BigDecimal consultationFee;
    
    @DecimalMin(value = "0.0", message = "Medication charges must be non-negative")
    private BigDecimal medicationCharges = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Test charges must be non-negative")
    private BigDecimal testCharges = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Other charges must be non-negative")
    private BigDecimal otherCharges = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Discount must be non-negative")
    private BigDecimal discount = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Tax must be non-negative")
    private BigDecimal tax = BigDecimal.ZERO;
    
    private String notes;
}
