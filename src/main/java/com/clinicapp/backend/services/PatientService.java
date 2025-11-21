package com.clinicapp.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import com.clinicapp.backend.entity.Patient;
import com.clinicapp.backend.repositories.PatientRepository;
import com.clinicapp.backend.repositories.AppointmentRepository;
import java.util.List;

@Service
public class PatientService {
    private final PatientRepository patientRepo;
    private final AppointmentRepository appointmentRepo;

    public PatientService(PatientRepository patientRepo, AppointmentRepository appointmentRepo) {
        this.patientRepo = patientRepo;
        this.appointmentRepo = appointmentRepo;
    }

    public List<Patient> getPatientsByCurrentClinic() {
        Long clinicId = getCurrentClinicId();
        return patientRepo.findByClinicId(clinicId);
    }

    public List<Patient> getAllPatients() {
        Long clinicId = getCurrentClinicId();
        return patientRepo.findByClinicId(clinicId);
    }

    public List<Patient> searchPatients(String query) {
        Long clinicId = getCurrentClinicId();
        return patientRepo.searchByClinicAndQuery(clinicId, query);
    }

    public Object getPatientHistory(Long patientId) {
        Long clinicId = getCurrentClinicId();

        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        if (!patient.getClinic().getId().equals(clinicId)) {
            throw new RuntimeException("Patient not found in your clinic");
        }

        List<Object[]> appointmentHistory = appointmentRepo.findPatientHistory(patientId);

        return java.util.Map.of(
                "patient", patient,
                "appointments", appointmentHistory,
                "message", "Patient history retrieved successfully"
        );
    }

    private Long getCurrentClinicId() {
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (details instanceof Long) {
            return (Long) details; // THIS LINE WILL NOW WORK AFTER JWT FILTER FIX
        }
        throw new RuntimeException("Clinic ID not found in security context");
    }
}