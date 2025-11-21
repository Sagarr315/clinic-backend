package com.clinicapp.backend.controllers;

import org.springframework.web.bind.annotation.*;
import com.clinicapp.backend.entity.Patient;
import com.clinicapp.backend.services.PatientService;
import com.clinicapp.backend.repositories.PatientRepository;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService patientService;
    private final PatientRepository patientRepo;

    public PatientController(PatientService patientService, PatientRepository patientRepo) {
        this.patientService = patientService;
        this.patientRepo = patientRepo;
    }

    // Get all patients for current clinic ONLY
    @GetMapping
    public List<Patient> getPatientsByClinic() {
        return patientService.getPatientsByCurrentClinic();
    }

    // FIXED: Search in current clinic ONLY - no clinicId parameter
    @GetMapping("/search")
    public List<Patient> searchPatients(@RequestParam String query) {
        return patientService.searchPatients(query);
    }

    // FIXED: Get history only for patients in current clinic
    @GetMapping("/{patientId}/history")
    public Object getPatientHistory(@PathVariable Long patientId) {
        return patientService.getPatientHistory(patientId);
    }
}