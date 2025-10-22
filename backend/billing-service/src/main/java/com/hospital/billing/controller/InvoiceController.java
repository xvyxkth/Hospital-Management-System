package com.hospital.billing.controller;

import com.hospital.billing.dto.InvoiceRequestDto;
import com.hospital.billing.dto.InvoiceResponseDto;
import com.hospital.billing.dto.PaymentRequestDto;
import com.hospital.billing.model.Invoice.InvoiceStatus;
import com.hospital.billing.service.InvoiceService;
import com.hospital.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Slf4j
public class InvoiceController {
    
    private final InvoiceService invoiceService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceResponseDto>> createInvoice(
            @Valid @RequestBody InvoiceRequestDto requestDto) {
        log.info("Creating invoice for appointment: {}", requestDto.getAppointmentId());
        InvoiceResponseDto response = invoiceService.createInvoice(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Invoice created successfully", response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceResponseDto>> getInvoiceById(@PathVariable Long id) {
        InvoiceResponseDto response = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(ApiResponse.success("Operation successful", response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<InvoiceResponseDto>>> getAllInvoices() {
        List<InvoiceResponseDto> invoices = invoiceService.getAllInvoices();
        return ResponseEntity.ok(ApiResponse.success("Operation successful", invoices));
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<InvoiceResponseDto>>> getInvoicesByPatientId(
            @PathVariable Long patientId) {
        List<InvoiceResponseDto> invoices = invoiceService.getInvoicesByPatientId(patientId);
        return ResponseEntity.ok(ApiResponse.success("Operation successful", invoices));
    }
    
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<ApiResponse<InvoiceResponseDto>> getInvoiceByAppointmentId(
            @PathVariable Long appointmentId) {
        InvoiceResponseDto response = invoiceService.getInvoiceByAppointmentId(appointmentId);
        return ResponseEntity.ok(ApiResponse.success("Operation successful", response));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<InvoiceResponseDto>>> getInvoicesByStatus(
            @PathVariable InvoiceStatus status) {
        List<InvoiceResponseDto> invoices = invoiceService.getInvoicesByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Operation successful", invoices));
    }
    
    @PostMapping("/{id}/payment")
    public ResponseEntity<ApiResponse<InvoiceResponseDto>> addPayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentRequestDto paymentDto) {
        log.info("Adding payment to invoice ID: {}", id);
        InvoiceResponseDto response = invoiceService.addPayment(id, paymentDto);
        return ResponseEntity.ok(ApiResponse.success("Payment added successfully", response));
    }
    
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<InvoiceResponseDto>> cancelInvoice(@PathVariable Long id) {
        log.info("Cancelling invoice ID: {}", id);
        InvoiceResponseDto response = invoiceService.cancelInvoice(id);
        return ResponseEntity.ok(ApiResponse.success("Invoice cancelled successfully", response));
    }
    
    @PatchMapping("/{id}/refund")
    public ResponseEntity<ApiResponse<InvoiceResponseDto>> refundInvoice(@PathVariable Long id) {
        log.info("Refunding invoice ID: {}", id);
        InvoiceResponseDto response = invoiceService.refundInvoice(id);
        return ResponseEntity.ok(ApiResponse.success("Invoice refunded successfully", response));
    }
}
