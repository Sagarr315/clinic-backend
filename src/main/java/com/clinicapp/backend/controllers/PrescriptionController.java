package com.clinicapp.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.clinicapp.backend.services.PrescriptionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {
    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping
    public ResponseEntity<?> createPrescription(@RequestBody PrescriptionService.PrescriptionRequest request) {
        try {
            var prescription = prescriptionService.createPrescription(request);
            return ResponseEntity.ok(prescription);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating prescription: " + e.getMessage());
        }
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> getPrescriptionByAppointment(@PathVariable Long appointmentId) {
        try {
            var prescription = prescriptionService.getPrescriptionByAppointment(appointmentId);
            return ResponseEntity.ok(prescription);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Prescription not found: " + e.getMessage());
        }
    }

    @GetMapping("/{prescriptionId}/download")
    public ResponseEntity<?> downloadPrescription(@PathVariable Long prescriptionId) {
        try {
            byte[] pdfBytes = prescriptionService.downloadPrescriptionPDF(prescriptionId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "prescription.pdf");

            return new ResponseEntity<>(pdfBytes, headers, 200);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}