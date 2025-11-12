package com.clinicapp.backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder; //  Use interface
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicapp.backend.dto.UserDTO;
import com.clinicapp.backend.entity.Clinic;
import com.clinicapp.backend.entity.Doctor;
import com.clinicapp.backend.entity.Receptionist;
import com.clinicapp.backend.repositories.ClinicRepository;
import com.clinicapp.backend.repositories.DoctorRepository;
import com.clinicapp.backend.repositories.ReceptionistRepository;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final DoctorRepository doctorRepo;
    private final ReceptionistRepository receptionistRepo;
    private final ClinicRepository clinicRepo;
    private final PasswordEncoder passwordEncoder; //  Use interface

    public AdminController(DoctorRepository doctorRepo, ReceptionistRepository receptionistRepo,
                           ClinicRepository clinicRepo, PasswordEncoder passwordEncoder) { //  Use interface
        this.doctorRepo = doctorRepo;
        this.receptionistRepo = receptionistRepo;
        this.clinicRepo = clinicRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // Add this new endpoint for updating clinic pricing
    @PutMapping("/clinic/{clinicId}/pricing")
    public ResponseEntity<?> updateClinicPricing(@PathVariable Long clinicId, @RequestBody PricingRequest request) {
        try {
            if (request.getConsultationFee() <= 0) {
                return ResponseEntity.badRequest().body("Consultation fee must be positive");
            }
            if (request.getTaxRate() < 0 || request.getTaxRate() > 1) {
                return ResponseEntity.badRequest().body("Tax rate must be between 0 and 1");
            }

            Clinic clinic = clinicRepo.findById(clinicId)
                    .orElseThrow(() -> new RuntimeException("Clinic not found"));

            clinic.setConsultationFee(request.getConsultationFee());
            clinic.setTaxRate(request.getTaxRate());

            Clinic updatedClinic = clinicRepo.save(clinic);
            return ResponseEntity.ok(updatedClinic);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating pricing: " + e.getMessage());
        }
    }

    // Add this method to your AdminController class
    @PostMapping("/add-doctor")
    public ResponseEntity<?> addDoctor(@RequestBody UserDTO userDTO) {
        try {
            // Validate input
            if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Password is required");
            }

            if (userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required");
            }

            // Check if doctor already exists in this clinic
            if (doctorRepo.findByEmailAndClinicId(userDTO.getEmail(), userDTO.getClinicId()).isPresent()) {
                return ResponseEntity.badRequest().body("Doctor with this email already exists in this clinic");
            }

            // Create and save doctor
            Doctor doctor = new Doctor();
            doctor.setName(userDTO.getName());
            doctor.setEmail(userDTO.getEmail());
            doctor.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            doctor.setRole("ROLE_DOCTOR"); //  Fixed role format

            Clinic clinic = clinicRepo.findById(userDTO.getClinicId())
                    .orElseThrow(() -> new RuntimeException("Clinic not found"));
            doctor.setClinic(clinic);

            Doctor savedDoctor = doctorRepo.save(doctor);

            // Remove password from response
            savedDoctor.setPassword(null);

            return ResponseEntity.ok(savedDoctor);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating doctor: " + e.getMessage());
        }
    }
    @PostMapping("/add-receptionist")
    public ResponseEntity<?> addReceptionist(@RequestBody UserDTO userDTO) {
        try {
            // Validate input
            if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Password is required");
            }

            if (userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required");
            }

            // Check if receptionist already exists
            if (receptionistRepo.findByEmail(userDTO.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body("Receptionist with this email already exists");
            }

            // Create and save receptionist
            Receptionist receptionist = new Receptionist();
            receptionist.setName(userDTO.getName());
            receptionist.setEmail(userDTO.getEmail());
            receptionist.setPassword(passwordEncoder.encode(userDTO.getPassword())); //  Use injected encoder
            receptionist.setRole("ROLE_RECEPTIONIST");

            Clinic clinic = clinicRepo.findById(userDTO.getClinicId())
                    .orElseThrow(() -> new RuntimeException("Clinic not found"));
            receptionist.setClinic(clinic);

            Receptionist savedReceptionist = receptionistRepo.save(receptionist);

            // Remove password from response
            savedReceptionist.setPassword(null);

            return ResponseEntity.ok(savedReceptionist);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating receptionist: " + e.getMessage());
        }
    }

    @GetMapping("/doctors/clinic/{clinicId}")
    public List<Doctor> getDoctorsByClinic(@PathVariable Long clinicId) {
        return doctorRepo.findByClinicId(clinicId);
    }

    // Inner class for pricing request
    public static class PricingRequest {
        private double consultationFee;
        private double taxRate;

        public double getConsultationFee() { return consultationFee; }
        public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }

        public double getTaxRate() { return taxRate; }
        public void setTaxRate(double taxRate) { this.taxRate = taxRate; }
    }
}
