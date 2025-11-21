package com.clinicapp.backend.controllers;

import com.clinicapp.backend.entity.Doctor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.clinicapp.backend.repositories.DoctorRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.clinicapp.backend.services.AnalyticsService;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;
    private final DoctorRepository doctorRepo;

    public AnalyticsController(AnalyticsService analyticsService,DoctorRepository doctorRepo) {
        this.analyticsService = analyticsService;
        this.doctorRepo = doctorRepo;
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getClinicSummary(@RequestParam Long clinicId) {
        try {
            Map<String, Object> summary = analyticsService.getClinicSummary(clinicId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching analytics: " + e.getMessage());
        }
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getDoctorPerformance(@PathVariable Long doctorId) {
        try {
            // Get current user role and ID
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String role = auth.getAuthorities().iterator().next().getAuthority();
            String username = auth.getName();

            // If DOCTOR, check if accessing own data
            if (role.equals("ROLE_DOCTOR")) {
                Doctor doctor = doctorRepo.findByEmail(username)
                        .orElseThrow(() -> new RuntimeException("Doctor not found"));
                if (!doctor.getId().equals(doctorId)) {
                    return ResponseEntity.status(403).body("Access denied");
                }
            }

            Map<String, Object> performance = analyticsService.getDoctorPerformance(doctorId);
            return ResponseEntity.ok(performance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}