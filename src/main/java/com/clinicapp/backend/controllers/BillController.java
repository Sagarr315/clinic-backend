package com.clinicapp.backend.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.clinicapp.backend.entity.Bill;
import com.clinicapp.backend.services.BillingService;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillingService billingService;

    public BillController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping("/clinic/{clinicId}")
    public List<Bill> getBillsByClinic(@PathVariable Long clinicId) {
        return billingService.getBillsByClinic(clinicId);
    }

    @GetMapping("/patient/{patientId}")
    public List<Bill> getBillsByPatient(@PathVariable Long patientId) {
        return billingService.getBillsByPatient(patientId);
    }

    @PutMapping("/{billId}/status")
    public ResponseEntity<?> updatePaymentStatus(@PathVariable Long billId, @RequestBody PaymentStatusRequest request) {
        try {
            Bill updatedBill = billingService.updatePaymentStatus(billId, request.getStatus());
            return ResponseEntity.ok(updatedBill);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating payment status: " + e.getMessage());
        }
    }

    @PutMapping("/{billId}/discount")
    public ResponseEntity<?> applyDiscount(@PathVariable Long billId, @RequestBody DiscountRequest request) {
        try {
            if (request.getDiscount() < 0) {
                return ResponseEntity.badRequest().body("Discount cannot be negative");
            }

            Bill updatedBill = billingService.applyDiscount(billId, request.getDiscount());
            return ResponseEntity.ok(updatedBill);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error applying discount: " + e.getMessage());
        }
    }

    public static class PaymentStatusRequest {
        private String status;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class DiscountRequest {
        private double discount;

        public double getDiscount() { return discount; }
        public void setDiscount(double discount) { this.discount = discount; }
    }
}