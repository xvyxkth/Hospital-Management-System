package com.hospital.billing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long patientId;
    
    @Column(nullable = false)
    private Long appointmentId;
    
    @Column(nullable = false, unique = true)
    private String invoiceNumber;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal consultationFee;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal medicationCharges = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal testCharges = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal otherCharges = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balanceAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    private LocalDateTime paidAt;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum InvoiceStatus {
        PENDING,
        PARTIALLY_PAID,
        PAID,
        CANCELLED,
        REFUNDED
    }
    
    public enum PaymentMethod {
        CASH,
        CREDIT_CARD,
        DEBIT_CARD,
        UPI,
        NET_BANKING,
        INSURANCE
    }
    
    // Helper method to calculate total
    public void calculateTotal() {
        BigDecimal subtotal = consultationFee
                .add(medicationCharges)
                .add(testCharges)
                .add(otherCharges);
        
        BigDecimal afterDiscount = subtotal.subtract(discount);
        this.totalAmount = afterDiscount.add(tax);
        this.balanceAmount = totalAmount.subtract(paidAmount);
        
        // Update status based on payment
        if (balanceAmount.compareTo(BigDecimal.ZERO) == 0) {
            this.status = InvoiceStatus.PAID;
        } else if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.status = InvoiceStatus.PARTIALLY_PAID;
        } else {
            this.status = InvoiceStatus.PENDING;
        }
    }
}
