package com.hospital.billing.service;

import com.hospital.billing.client.AppointmentServiceClient;
import com.hospital.billing.client.PatientServiceClient;
import com.hospital.billing.dto.*;
import com.hospital.billing.model.Invoice;
import com.hospital.billing.model.Invoice.InvoiceStatus;
import com.hospital.billing.repository.InvoiceRepository;
import com.hospital.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {
    
    private final InvoiceRepository invoiceRepository;
    private final PatientServiceClient patientServiceClient;
    private final AppointmentServiceClient appointmentServiceClient;
    
    @Transactional
    public InvoiceResponseDto createInvoice(InvoiceRequestDto requestDto) {
        log.info("Creating invoice for appointment ID: {}", requestDto.getAppointmentId());
        
        // Check if invoice already exists for this appointment
        if (invoiceRepository.existsByAppointmentId(requestDto.getAppointmentId())) {
            throw new IllegalArgumentException("Invoice already exists for appointment ID: " + requestDto.getAppointmentId());
        }
        
        // Fetch patient details
        PatientDto patient = patientServiceClient.getPatientById(requestDto.getPatientId());
        if (patient == null) {
            throw new ResourceNotFoundException("Patient", "id", requestDto.getPatientId());
        }
        
        // Fetch appointment details
        AppointmentDto appointment = appointmentServiceClient.getAppointmentById(requestDto.getAppointmentId());
        if (appointment == null) {
            throw new ResourceNotFoundException("Appointment", "id", requestDto.getAppointmentId());
        }
        
        // Create invoice
        Invoice invoice = new Invoice();
        invoice.setPatientId(requestDto.getPatientId());
        invoice.setAppointmentId(requestDto.getAppointmentId());
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setConsultationFee(requestDto.getConsultationFee());
        invoice.setMedicationCharges(requestDto.getMedicationCharges());
        invoice.setTestCharges(requestDto.getTestCharges());
        invoice.setOtherCharges(requestDto.getOtherCharges());
        invoice.setDiscount(requestDto.getDiscount());
        invoice.setTax(requestDto.getTax());
        invoice.setNotes(requestDto.getNotes());
        
        // Calculate total
        invoice.calculateTotal();
        
        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice created successfully with ID: {}", savedInvoice.getId());
        
        return enrichInvoiceResponse(savedInvoice, patient, appointment);
    }
    
    @Transactional(readOnly = true)
    public InvoiceResponseDto getInvoiceById(Long id) {
        log.debug("Fetching invoice with ID: {}", id);
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
        
        return enrichInvoiceResponse(invoice);
    }
    
    @Transactional(readOnly = true)
    public List<InvoiceResponseDto> getAllInvoices() {
        log.debug("Fetching all invoices");
        return invoiceRepository.findAll().stream()
                .map(this::enrichInvoiceResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<InvoiceResponseDto> getInvoicesByPatientId(Long patientId) {
        log.debug("Fetching invoices for patient ID: {}", patientId);
        return invoiceRepository.findByPatientId(patientId).stream()
                .map(this::enrichInvoiceResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public InvoiceResponseDto getInvoiceByAppointmentId(Long appointmentId) {
        log.debug("Fetching invoice for appointment ID: {}", appointmentId);
        Invoice invoice = invoiceRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "appointmentId", appointmentId));
        
        return enrichInvoiceResponse(invoice);
    }
    
    @Transactional(readOnly = true)
    public List<InvoiceResponseDto> getInvoicesByStatus(InvoiceStatus status) {
        log.debug("Fetching invoices with status: {}", status);
        return invoiceRepository.findByStatus(status).stream()
                .map(this::enrichInvoiceResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public InvoiceResponseDto addPayment(Long invoiceId, PaymentRequestDto paymentDto) {
        log.info("Adding payment of {} to invoice ID: {}", paymentDto.getAmount(), invoiceId);
        
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));
        
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalArgumentException("Invoice is already fully paid");
        }
        
        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot add payment to cancelled invoice");
        }
        
        // Validate payment amount
        BigDecimal newPaidAmount = invoice.getPaidAmount().add(paymentDto.getAmount());
        if (newPaidAmount.compareTo(invoice.getTotalAmount()) > 0) {
            throw new IllegalArgumentException("Payment amount exceeds total amount");
        }
        
        // Update payment details
        invoice.setPaidAmount(newPaidAmount);
        invoice.setPaymentMethod(paymentDto.getPaymentMethod());
        
        // Update notes if provided
        if (paymentDto.getNotes() != null && !paymentDto.getNotes().isEmpty()) {
            String existingNotes = invoice.getNotes() != null ? invoice.getNotes() + "\n" : "";
            invoice.setNotes(existingNotes + paymentDto.getNotes());
        }
        
        // Recalculate total (which also updates status)
        invoice.calculateTotal();
        
        // Set paid timestamp if fully paid
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            invoice.setPaidAt(LocalDateTime.now());
        }
        
        Invoice updatedInvoice = invoiceRepository.save(invoice);
        log.info("Payment added successfully. Invoice status: {}", updatedInvoice.getStatus());
        
        return enrichInvoiceResponse(updatedInvoice);
    }
    
    @Transactional
    public InvoiceResponseDto cancelInvoice(Long invoiceId) {
        log.info("Cancelling invoice ID: {}", invoiceId);
        
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));
        
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalArgumentException("Cannot cancel a paid invoice. Please process a refund instead.");
        }
        
        invoice.setStatus(InvoiceStatus.CANCELLED);
        Invoice cancelledInvoice = invoiceRepository.save(invoice);
        
        log.info("Invoice cancelled successfully");
        return enrichInvoiceResponse(cancelledInvoice);
    }
    
    @Transactional
    public InvoiceResponseDto refundInvoice(Long invoiceId) {
        log.info("Refunding invoice ID: {}", invoiceId);
        
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));
        
        if (invoice.getStatus() != InvoiceStatus.PAID) {
            throw new IllegalArgumentException("Only paid invoices can be refunded");
        }
        
        invoice.setStatus(InvoiceStatus.REFUNDED);
        invoice.setPaidAmount(BigDecimal.ZERO);
        invoice.setBalanceAmount(invoice.getTotalAmount());
        invoice.setPaidAt(null);
        
        Invoice refundedInvoice = invoiceRepository.save(invoice);
        
        log.info("Invoice refunded successfully");
        return enrichInvoiceResponse(refundedInvoice);
    }
    
    private InvoiceResponseDto enrichInvoiceResponse(Invoice invoice) {
        PatientDto patient = patientServiceClient.getPatientById(invoice.getPatientId());
        AppointmentDto appointment = appointmentServiceClient.getAppointmentById(invoice.getAppointmentId());
        return enrichInvoiceResponse(invoice, patient, appointment);
    }
    
    private InvoiceResponseDto enrichInvoiceResponse(Invoice invoice, PatientDto patient, AppointmentDto appointment) {
        return InvoiceResponseDto.builder()
                .id(invoice.getId())
                .patientId(invoice.getPatientId())
                .patientName(patient != null ? patient.getFirstName() + " " + patient.getLastName() : null)
                .appointmentId(invoice.getAppointmentId())
                .appointmentDate(appointment != null ? appointment.getAppointmentDate() : null)
                .invoiceNumber(invoice.getInvoiceNumber())
                .consultationFee(invoice.getConsultationFee())
                .medicationCharges(invoice.getMedicationCharges())
                .testCharges(invoice.getTestCharges())
                .otherCharges(invoice.getOtherCharges())
                .discount(invoice.getDiscount())
                .tax(invoice.getTax())
                .totalAmount(invoice.getTotalAmount())
                .paidAmount(invoice.getPaidAmount())
                .balanceAmount(invoice.getBalanceAmount())
                .status(invoice.getStatus())
                .paymentMethod(invoice.getPaymentMethod())
                .notes(invoice.getNotes())
                .paidAt(invoice.getPaidAt())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build();
    }
    
    private String generateInvoiceNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "INV-" + timestamp;
    }
}
