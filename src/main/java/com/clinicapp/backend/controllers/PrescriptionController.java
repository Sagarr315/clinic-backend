package com.clinicapp.backend.controllers;

import com.clinicapp.backend.entity.Prescription;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.clinicapp.backend.services.PrescriptionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

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
            System.out.println("=== DEBUG: Creating prescription ===");
            System.out.println("Appointment ID: " + request.getAppointmentId());
            System.out.println("Diagnosis: " + request.getDiagnosis());
            System.out.println("Medicines count: " + (request.getMedicines() != null ? request.getMedicines().size() : 0));

            var prescription = prescriptionService.createPrescription(request);
            return ResponseEntity.ok(prescription);
        } catch (Exception e) {
            System.out.println("=== DEBUG: ERROR ===");
            e.printStackTrace(); // This will show the exact error
            return ResponseEntity.badRequest().body("Error creating prescription: " + e.getMessage());
        }
    }
    @GetMapping
    public List<Prescription> getAllPrescriptions() {
        return prescriptionService.getAllPrescriptions();
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