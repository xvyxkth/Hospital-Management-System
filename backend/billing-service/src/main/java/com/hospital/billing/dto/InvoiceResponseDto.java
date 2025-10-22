package com.hospital.billing.dto;

import com.hospital.billing.model.Invoice.InvoiceStatus;
import com.hospital.billing.model.Invoice.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponseDto {
    
    private Long id;
    private Long patientId;
    private String patientName;
    private Long appointmentId;
    private String appointmentDate;
    private String invoiceNumber;
    private BigDecimal consultationFee;
    private BigDecimal medicationCharges;
    private BigDecimal testCharges;
    private BigDecimal otherCharges;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private InvoiceStatus status;
    private PaymentMethod paymentMethod;
    private String notes;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
