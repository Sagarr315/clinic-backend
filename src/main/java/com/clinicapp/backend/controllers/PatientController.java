package com.clinicapp.backend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinicapp.backend.entity.Patient;
import com.clinicapp.backend.services.PatientService;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

	private final PatientService patientService;

	public PatientController(PatientService patientService) {
		this.patientService = patientService;
	}

	// NEW: Patient registration (public endpoint)
	@PostMapping("/register")
	public Patient registerPatient(@RequestBody PatientRegistrationRequest request) {
		// This will be handled by the service
		// For now, return a simple response
		Patient patient = new Patient();
		patient.setName(request.getName());
		patient.setContact(request.getContact());
		patient.setGender(request.getGender());
		patient.setAge(request.getAge());
		// Note: Clinic should be set based on context or request
		return patient;
	}

	@GetMapping("/search")
	public List<Patient> searchPatients(@RequestParam String query) {
		return patientService.searchPatients(query);
	}

	@GetMapping("/{id}/history")
	public List<Object[]> getPatientHistory(@PathVariable Long id) {
		return patientService.getPatientHistory(id);
	}

	@PutMapping("/{id}")
	public Patient updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
		return patientService.updatePatient(id, patient);
	}

	// NEW: Patient registration DTO
	public static class PatientRegistrationRequest {
		private String name;
		private String contact;
		private String gender;
		private Integer age;
		private Long clinicId;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getContact() {
			return contact;
		}

		public void setContact(String contact) {
			this.contact = contact;
		}

		public String getGender() {
			return gender;
		}

		public void setGender(String gender) {
			this.gender = gender;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}

		public Long getClinicId() {
			return clinicId;
		}

		public void setClinicId(Long clinicId) {
			this.clinicId = clinicId;
		}
	}
}