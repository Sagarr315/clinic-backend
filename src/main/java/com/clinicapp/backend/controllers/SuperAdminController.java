package com.clinicapp.backend.controllers;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicapp.backend.entity.Clinic;
import com.clinicapp.backend.entity.Doctor;
import com.clinicapp.backend.repositories.ClinicRepository;
import com.clinicapp.backend.repositories.DoctorRepository;

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

	// @PreAuthorize("hasRole('SUPERADMIN')")
	@PostMapping("/create-clinic")
	public String createClinic(@RequestBody CreateClinicRequest req) {
		// 1 Create and save clinic
		Clinic clinic = new Clinic();
		clinic.setName(req.getClinicName());
		clinic.setEmail(req.getClinicEmail());
		clinic.setAddress(req.getClinicAddress());
		clinicRepo.save(clinic);

		// 2 Create and save admin doctor
		Doctor admin = new Doctor();
		admin.setName(req.getAdminName());
		admin.setEmail(req.getAdminEmail());
		admin.setPassword(encoder.encode(req.getAdminPassword()));
		admin.setRole("ROLE_ADMIN_DOCTOR");
		admin.setClinic(clinic);
		doctorRepo.save(admin);

		return " Clinic + Admin created for " + req.getClinicName();
	}

	// Inner DTO class
	public static class CreateClinicRequest {
		private String clinicName;
		private String clinicEmail;
		private String clinicAddress;
		private String adminName;
		private String adminEmail;
		private String adminPassword;

		// Getters and Setters
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
	}
}
