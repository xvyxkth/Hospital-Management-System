package com.hospital.billing.repository;

import com.hospital.billing.model.Invoice;
import com.hospital.billing.model.Invoice.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    List<Invoice> findByPatientId(Long patientId);
    
    Optional<Invoice> findByAppointmentId(Long appointmentId);
    
    List<Invoice> findByStatus(InvoiceStatus status);
    
    List<Invoice> findByPatientIdAndStatus(Long patientId, InvoiceStatus status);
    
    boolean existsByAppointmentId(Long appointmentId);
}
