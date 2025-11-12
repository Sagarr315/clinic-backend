package com.clinicapp.backend.services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.clinicapp.backend.entity.Appointment;
import com.clinicapp.backend.entity.Bill;
import com.clinicapp.backend.entity.Clinic;
import com.clinicapp.backend.repositories.BillRepository;

@Service
public class BillingService {
    private final BillRepository billRepository;

    public BillingService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public Bill generateBill(Appointment appointment) {
        if (!"COMPLETED".equals(appointment.getStatus())) {
            throw new RuntimeException("Cannot generate bill for non-completed appointment");
        }

        Clinic clinic = appointment.getClinic();
        double consultationFee = clinic.getConsultationFee();
        double tax = consultationFee * clinic.getTaxRate();
        double totalAmount = consultationFee + tax;

        Bill bill = new Bill();
        bill.setAppointment(appointment);
        bill.setClinic(clinic);
        bill.setConsultationFee(consultationFee);
        bill.setTax(tax);
        bill.setDiscount(0.0);
        bill.setTotalAmount(totalAmount);
        bill.setStatus("PENDING");

        return billRepository.save(bill);
    }

    public Bill applyDiscount(Long billId, double discount) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        if (discount < 0) {
            throw new RuntimeException("Discount cannot be negative");
        }

        if (discount > bill.getConsultationFee()) {
            throw new RuntimeException("Discount cannot exceed consultation fee");
        }

        bill.setDiscount(discount);
        bill.setTotalAmount(bill.getConsultationFee() + bill.getTax() - discount);

        return billRepository.save(bill);
    }

    public Bill updatePaymentStatus(Long billId, String status) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        if (!isValidPaymentStatus(status)) {
            throw new RuntimeException("Invalid payment status: " + status);
        }

        bill.setStatus(status);
        return billRepository.save(bill);
    }

    public List<Bill> getBillsByClinic(Long clinicId) {
        return billRepository.findByClinicId(clinicId);
    }

    public List<Bill> getBillsByPatient(Long patientId) {
        return billRepository.findByAppointmentPatientId(patientId);
    }

    public List<Bill> getPendingBills() {
        return billRepository.findByStatus("PENDING");
    }

    private boolean isValidPaymentStatus(String status) {
        return "PENDING".equals(status) || "PAID".equals(status) || "CANCELLED".equals(status);
    }
}