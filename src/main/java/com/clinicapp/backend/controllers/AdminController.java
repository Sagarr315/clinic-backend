package com.clinicapp.backend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicapp.backend.entity.Doctor;
import com.clinicapp.backend.repositories.DoctorRepository;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

	private final DoctorRepository doctorRepo;

	public AdminController(DoctorRepository doctorRepo) {
		this.doctorRepo = doctorRepo;
	}

	@GetMapping("/doctors/clinic/{clinicId}")
	public List<Doctor> getDoctorsByClinic(@PathVariable Long clinicId) {
		return doctorRepo.findByClinicId(clinicId);
	}
}