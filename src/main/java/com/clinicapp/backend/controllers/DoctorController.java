package com.clinicapp.backend.controllers;

import org.springframework.web.bind.annotation.*;
import com.clinicapp.backend.entity.Doctor;
import com.clinicapp.backend.services.DoctorService;
import com.clinicapp.backend.repositories.DoctorRepository;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorRepository doctorRepo;

    public DoctorController(DoctorService doctorService, DoctorRepository doctorRepo) {
        this.doctorService = doctorService;
        this.doctorRepo = doctorRepo;
    }

    @GetMapping
    public List<Doctor> getDoctorsByClinic() {
        return doctorService.getDoctorsByCurrentClinic();
    }

    // NEW: Add clinicId parameter endpoint
    @GetMapping("/clinic/{clinicId}")
    public List<Doctor> getDoctorsByClinicId(@PathVariable Long clinicId) {
        return doctorRepo.findByClinicId(clinicId);
    }
}