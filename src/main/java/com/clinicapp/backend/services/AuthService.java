package com.clinicapp.backend.services;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.clinicapp.backend.config.JwtUtils;
import com.clinicapp.backend.dto.AuthResponse;
import com.clinicapp.backend.dto.LoginRequest;
import com.clinicapp.backend.dto.RegisterRequest;
import com.clinicapp.backend.entity.Clinic;
import com.clinicapp.backend.entity.Doctor;
import com.clinicapp.backend.repositories.ClinicRepository;
import com.clinicapp.backend.repositories.DoctorRepository;

@Service
public class AuthService {

	private final DoctorRepository doctorRepo;
	private final ClinicRepository clinicRepo;
	private final JwtUtils jwtUtils;
	private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	public AuthService(DoctorRepository doctorRepo, ClinicRepository clinicRepo, JwtUtils jwtUtils) {
		this.doctorRepo = doctorRepo;
		this.clinicRepo = clinicRepo;
		this.jwtUtils = jwtUtils;
	}

	public AuthResponse register(RegisterRequest request) {
		if (doctorRepo.findByEmailAndClinicId(request.getEmail(), request.getClinicId()).isPresent()) {
			throw new RuntimeException("User already exists in this clinic");
		}

		Doctor doctor = new Doctor();
		doctor.setName(request.getName());
		doctor.setEmail(request.getEmail());
		doctor.setPassword(encoder.encode(request.getPassword()));
		doctor.setRole(request.getRole());

		if (request.getClinicId() != null) {
			Clinic clinic = clinicRepo.findById(request.getClinicId())
					.orElseThrow(() -> new RuntimeException("Clinic not found"));
			doctor.setClinic(clinic);
		}

		doctorRepo.save(doctor);
		String token = jwtUtils.generateToken(doctor.getEmail(), doctor.getRole(),
				doctor.getClinic() != null ? doctor.getClinic().getId() : null);

		return new AuthResponse(token, doctor.getRole(),
				doctor.getClinic() != null ? doctor.getClinic().getId() : null);
	}

	public AuthResponse login(LoginRequest request) {
		Optional<Doctor> doctorOpt = doctorRepo.findByEmail(request.getEmail());
		if (doctorOpt.isEmpty()) {
			throw new RuntimeException("Invalid email or password");
		}

		Doctor doctor = doctorOpt.get();
		if (!encoder.matches(request.getPassword(), doctor.getPassword())) {
			throw new RuntimeException("Invalid email or password");
		}

		String token = jwtUtils.generateToken(doctor.getEmail(), doctor.getRole(),
				doctor.getClinic() != null ? doctor.getClinic().getId() : null);

		return new AuthResponse(token, doctor.getRole(),
				doctor.getClinic() != null ? doctor.getClinic().getId() : null);
	}
}
