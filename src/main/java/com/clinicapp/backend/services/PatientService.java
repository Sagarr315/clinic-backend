package com.clinicapp.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clinicapp.backend.entity.Patient;
import com.clinicapp.backend.repositories.AppointmentRepository;
import com.clinicapp.backend.repositories.PatientRepository;

@Service
public class PatientService {

	private final PatientRepository patientRepo;
	private final AppointmentRepository appointmentRepo;

	public PatientService(PatientRepository patientRepo, AppointmentRepository appointmentRepo) {
		this.patientRepo = patientRepo;
		this.appointmentRepo = appointmentRepo;
	}

	public List<Patient> searchPatients(String query) {
		if (query == null || query.trim().isEmpty()) {
			throw new RuntimeException("Search query cannot be empty");
		}
		return patientRepo.findByNameContainingOrContactContaining(query, query);
	}

	public List<Object[]> getPatientHistory(Long patientId) {
		if (patientId == null) {
			throw new RuntimeException("Patient ID cannot be null");
		}
		return appointmentRepo.findPatientHistory(patientId);
	}

	public Patient updatePatient(Long id, Patient patientDetails) {
		if (id == null) {
			throw new RuntimeException("Patient ID cannot be null");
		}

		Patient patient = patientRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Patient not found with ID: " + id));

		if (patientDetails.getName() != null)
			patient.setName(patientDetails.getName());
		if (patientDetails.getContact() != null)
			patient.setContact(patientDetails.getContact());
		if (patientDetails.getGender() != null)
			patient.setGender(patientDetails.getGender());
		if (patientDetails.getAge() != null)
			patient.setAge(patientDetails.getAge());

		return patientRepo.save(patient);
	}
}