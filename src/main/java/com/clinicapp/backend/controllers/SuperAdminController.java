package com.clinicapp.backend.controllers;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.clinicapp.backend.entity.Clinic;
import com.clinicapp.backend.entity.Doctor;
import com.clinicapp.backend.repositories.ClinicRepository;
import com.clinicapp.backend.repositories.DoctorRepository;

import java.util.List;

@RestController
@RequestMapping("/api/superadmin")
public class SuperAdminController {

    private final ClinicRepository clinicRepo;
    private final DoctorRepository doctorRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public SuperAdminController(ClinicRepository clinicRepo, DoctorRepository doctorRepo) {
        this.clinicRepo = clinicRepo;
        this.doctorRepo = doctorRepo;
    }

    @PostMapping("/create-clinic")
    public String createClinic(@RequestBody CreateClinicRequest req) {
        Clinic clinic = new Clinic();
        clinic.setName(req.getClinicName());
        clinic.setEmail(req.getClinicEmail());
        clinic.setAddress(req.getClinicAddress());
        clinic.setSubscriptionPlan(req.getSubscriptionPlan());
        clinic.setConsultationFee(req.getConsultationFee());
        clinic.setTaxRate(req.getTaxRate());
        clinic.setSubdomain(req.getSubdomain());
        clinicRepo.save(clinic);

        Doctor admin = new Doctor();
        admin.setName(req.getAdminName());
        admin.setEmail(req.getAdminEmail());
        admin.setPassword(encoder.encode(req.getAdminPassword()));
        admin.setRole("ROLE_ADMIN_DOCTOR");
        admin.setClinic(clinic);
        doctorRepo.save(admin);

        return "Clinic + Admin created for " + req.getClinicName();
    }

    @GetMapping("/clinics")
    public List<Clinic> getAllClinics() {
        return clinicRepo.findAll();
    }

    public static class CreateClinicRequest {
        private String clinicName;
        private String clinicEmail;
        private String clinicAddress;
        private String subscriptionPlan;
        private Double consultationFee;
        private Double taxRate;
        private String adminName;
        private String adminEmail;
        private String adminPassword;
        private String subdomain;

        public String getClinicName() {
            return clinicName;
        }

        public void setClinicName(String clinicName) {
            this.clinicName = clinicName;
        }

        public String getClinicEmail() {
            return clinicEmail;
        }

        public void setClinicEmail(String clinicEmail) {
            this.clinicEmail = clinicEmail;
        }

        public String getClinicAddress() {
            return clinicAddress;
        }


        public void setClinicAddress(String clinicAddress) {
            this.clinicAddress = clinicAddress;
        }

        public String getSubscriptionPlan() {
            return subscriptionPlan;
        }

        public void setSubscriptionPlan(String subscriptionPlan) {
            this.subscriptionPlan = subscriptionPlan;
        }

        public Double getConsultationFee() {
            return consultationFee;
        }

        public void setConsultationFee(Double consultationFee) {
            this.consultationFee = consultationFee;
        }

        public Double getTaxRate() {
            return taxRate;
        }

        public void setTaxRate(Double taxRate) {
            this.taxRate = taxRate;
        }

        public String getAdminName() {
            return adminName;
        }

        public void setAdminName(String adminName) {
            this.adminName = adminName;
        }

        public String getAdminEmail() {
            return adminEmail;
        }

        public void setAdminEmail(String adminEmail) {
            this.adminEmail = adminEmail;
        }

        public String getAdminPassword() {
            return adminPassword;
        }

        public void setAdminPassword(String adminPassword) {
            this.adminPassword = adminPassword;
        }

        public String getSubdomain() {
            return subdomain;
        }

        public void setSubdomain(String subdomain) {
            this.subdomain = subdomain;
        }
    }
}