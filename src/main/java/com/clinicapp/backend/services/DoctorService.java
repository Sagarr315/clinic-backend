package com.clinicapp.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import com.clinicapp.backend.entity.Doctor;
import com.clinicapp.backend.repositories.DoctorRepository;
import java.util.List;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepo;

    public DoctorService(DoctorRepository doctorRepo) {
        this.doctorRepo = doctorRepo;
    }

    public List<Doctor> getDoctorsByCurrentClinic() {
        Long clinicId = getCurrentClinicId();
        return doctorRepo.findByClinicId(clinicId);
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepo.findAll();
    }

    private Long getCurrentClinicId() {
        // Get clinic ID from JWT token or security context
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (details instanceof Long) {
            return (Long) details;
        }
        throw new RuntimeException("Clinic ID not found in security context");
    }
}