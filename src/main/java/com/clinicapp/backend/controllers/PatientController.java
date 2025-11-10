package com.clinicapp.backend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}