package com.clinicapp.backend.controllers;

import org.springframework.web.bind.annotation.*;
import com.clinicapp.backend.entity.Clinic;
import com.clinicapp.backend.repositories.ClinicRepository;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clinics")
public class ClinicController {
    private final ClinicRepository clinicRepo;

    public ClinicController(ClinicRepository clinicRepo) {
        this.clinicRepo = clinicRepo;
    }

    @GetMapping("/public")
    public List<Clinic> getAllPublicClinics() {
        return clinicRepo.findAll();

    }

    @GetMapping("/subdomain/{subdomain}")
    public Clinic getClinicBySubdomain(@PathVariable String subdomain) {
        Optional<Clinic> clinic = clinicRepo.findBySubdomain(subdomain);
        return clinic.orElseThrow(() -> new RuntimeException("Clinic not found"));
    }
}