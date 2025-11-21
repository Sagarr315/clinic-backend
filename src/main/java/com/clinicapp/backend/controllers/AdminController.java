package com.clinicapp.backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private final PasswordEncoder passwordEncoder;

    public AdminController(DoctorRepository doctorRepo, ReceptionistRepository receptionistRepo,
                           ClinicRepository clinicRepo, PasswordEncoder passwordEncoder) {
        this.doctorRepo = doctorRepo;
        this.receptionistRepo = receptionistRepo;
        this.clinicRepo = clinicRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // ==================== DOCTOR ENDPOINTS ====================

    @PostMapping("/add-doctor")
    public ResponseEntity<?> addDoctor(@RequestBody UserDTO userDTO) {
        try {
            if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Password is required");
            }
            if (userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required");
            }

            if (doctorRepo.findByEmailAndClinicId(userDTO.getEmail(), userDTO.getClinicId()).isPresent()) {
                return ResponseEntity.badRequest().body("Doctor with this email already exists in this clinic");
            }

            Doctor doctor = new Doctor();
            doctor.setName(userDTO.getName());
            doctor.setEmail(userDTO.getEmail());
            doctor.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            doctor.setRole("ROLE_DOCTOR");
            doctor.setSpecialization(userDTO.getSpecialization());

            Clinic clinic = clinicRepo.findById(userDTO.getClinicId())
                    .orElseThrow(() -> new RuntimeException("Clinic not found"));
            doctor.setClinic(clinic);

            Doctor savedDoctor = doctorRepo.save(doctor);
            savedDoctor.setPassword(null);
            return ResponseEntity.ok(savedDoctor);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating doctor: " + e.getMessage());
        }
    }

    @PutMapping("/doctors/{id}")
    public ResponseEntity<?> updateDoctor(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            Doctor doctor = doctorRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));

            // Update fields
            if (userDTO.getName() != null) doctor.setName(userDTO.getName());
            if (userDTO.getEmail() != null) doctor.setEmail(userDTO.getEmail());
            if (userDTO.getSpecialization() != null) doctor.setSpecialization(userDTO.getSpecialization());

            // Update password only if provided
            if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) {
                doctor.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }

            Doctor updatedDoctor = doctorRepo.save(doctor);
            updatedDoctor.setPassword(null);
            return ResponseEntity.ok(updatedDoctor);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating doctor: " + e.getMessage());
        }
    }

    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long id) {
        try {
            Doctor doctor = doctorRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));

            doctorRepo.delete(doctor);
            return ResponseEntity.ok("Doctor deleted successfully");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting doctor: " + e.getMessage());
        }
    }

    @GetMapping("/doctors/clinic/{clinicId}")
    public List<Doctor> getDoctorsByClinic(@PathVariable Long clinicId) {
        return doctorRepo.findByClinicId(clinicId);
    }

    // ==================== RECEPTIONIST ENDPOINTS ====================

    @PostMapping("/add-receptionist")
    public ResponseEntity<?> addReceptionist(@RequestBody UserDTO userDTO) {
        try {
            if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Password is required");
            }
            if (userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required");
            }

            if (receptionistRepo.findByEmail(userDTO.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body("Receptionist with this email already exists");
            }

            Receptionist receptionist = new Receptionist();
            receptionist.setName(userDTO.getName());
            receptionist.setEmail(userDTO.getEmail());
            receptionist.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            receptionist.setRole("ROLE_RECEPTIONIST");

            Clinic clinic = clinicRepo.findById(userDTO.getClinicId())
                    .orElseThrow(() -> new RuntimeException("Clinic not found"));
            receptionist.setClinic(clinic);

            Receptionist savedReceptionist = receptionistRepo.save(receptionist);
            savedReceptionist.setPassword(null);
            return ResponseEntity.ok(savedReceptionist);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating receptionist: " + e.getMessage());
        }
    }

    @PutMapping("/receptionists/{id}")
    public ResponseEntity<?> updateReceptionist(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            Receptionist receptionist = receptionistRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Receptionist not found"));

            // Update fields
            if (userDTO.getName() != null) receptionist.setName(userDTO.getName());
            if (userDTO.getEmail() != null) receptionist.setEmail(userDTO.getEmail());

            // Update password only if provided
            if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) {
                receptionist.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }

            Receptionist updatedReceptionist = receptionistRepo.save(receptionist);
            updatedReceptionist.setPassword(null);
            return ResponseEntity.ok(updatedReceptionist);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating receptionist: " + e.getMessage());
        }
    }

    @DeleteMapping("/receptionists/{id}")
    public ResponseEntity<?> deleteReceptionist(@PathVariable Long id) {
        try {
            Receptionist receptionist = receptionistRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Receptionist not found"));

            receptionistRepo.delete(receptionist);
            return ResponseEntity.ok("Receptionist deleted successfully");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting receptionist: " + e.getMessage());
        }
    }

    @GetMapping("/receptionists/clinic/{clinicId}")
    public List<Receptionist> getReceptionistsByClinic(@PathVariable Long clinicId) {
        return receptionistRepo.findByClinicId(clinicId);
    }

    // ==================== CLINIC PRICING ====================

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

    // ==================== DTO CLASSES ====================

    public static class PricingRequest {
        private double consultationFee;
        private double taxRate;

        public double getConsultationFee() { return consultationFee; }
        public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }
        public double getTaxRate() { return taxRate; }
        public void setTaxRate(double taxRate) { this.taxRate = taxRate; }
    }
}