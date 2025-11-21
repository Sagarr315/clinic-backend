package com.clinicapp.backend.services;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.clinicapp.backend.config.JwtUtils;
import com.clinicapp.backend.dto.AuthResponse;
import com.clinicapp.backend.dto.LoginRequest;
import com.clinicapp.backend.dto.RegisterRequest;
import com.clinicapp.backend.entity.Clinic;
import com.clinicapp.backend.entity.Doctor;
import com.clinicapp.backend.entity.Receptionist;
import com.clinicapp.backend.repositories.ClinicRepository;
import com.clinicapp.backend.repositories.DoctorRepository;
import com.clinicapp.backend.repositories.ReceptionistRepository;

@Service
public class AuthService {

    private final DoctorRepository doctorRepo;
    private final ReceptionistRepository receptionistRepo;
    private final ClinicRepository clinicRepo;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            DoctorRepository doctorRepo,
            ReceptionistRepository receptionistRepo,
            ClinicRepository clinicRepo,
            JwtUtils jwtUtils,
            PasswordEncoder passwordEncoder
    ) {
        this.doctorRepo = doctorRepo;
        this.receptionistRepo = receptionistRepo;
        this.clinicRepo = clinicRepo;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    // Register Doctor (Admin feature)
    public AuthResponse register(RegisterRequest request) {
        if (doctorRepo.findByEmailAndClinicId(request.getEmail(), request.getClinicId()).isPresent()) {
            throw new RuntimeException("Doctor already exists in this clinic");
        }

        Doctor doctor = new Doctor();
        doctor.setName(request.getName());
        doctor.setEmail(request.getEmail());
        doctor.setPassword(passwordEncoder.encode(request.getPassword()));
        doctor.setRole(request.getRole());

        if (request.getClinicId() != null) {
            Clinic clinic = clinicRepo.findById(request.getClinicId())
                    .orElseThrow(() -> new RuntimeException("Clinic not found"));
            doctor.setClinic(clinic);
        }

        Doctor savedDoctor = doctorRepo.save(doctor);

        Clinic clinic = savedDoctor.getClinic();
        String subdomain = (clinic != null) ? clinic.getSubdomain() : null;

        String token = jwtUtils.generateToken(
                doctor.getEmail(),
                doctor.getRole(),
                clinic != null ? clinic.getId() : null
        );

        return new AuthResponse(
                token,
                doctor.getRole(),
                clinic != null ? clinic.getId() : null,
                savedDoctor.getId(),
                subdomain
        );
    }

    // Login doctor or receptionist
    public AuthResponse login(LoginRequest request) {

        // Doctor login
        Optional<Doctor> doctorOpt = doctorRepo.findByEmail(request.getEmail());
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();

            if (passwordEncoder.matches(request.getPassword(), doctor.getPassword())) {

                Clinic clinic = doctor.getClinic();
                String subdomain = (clinic != null) ? clinic.getSubdomain() : null;

                String token = jwtUtils.generateToken(
                        doctor.getEmail(),
                        doctor.getRole(),
                        clinic != null ? clinic.getId() : null
                );

                return new AuthResponse(
                        token,
                        doctor.getRole(),
                        clinic != null ? clinic.getId() : null,
                        doctor.getId(),
                        subdomain
                );
            }
        }

        // Receptionist login
        Optional<Receptionist> recOpt = receptionistRepo.findByEmail(request.getEmail());
        if (recOpt.isPresent()) {
            Receptionist r = recOpt.get();

            if (passwordEncoder.matches(request.getPassword(), r.getPassword())) {

                Clinic clinic = r.getClinic();
                String subdomain = clinic.getSubdomain();

                String token = jwtUtils.generateToken(
                        r.getEmail(),
                        r.getRole(),
                        clinic.getId()
                );

                return new AuthResponse(
                        token,
                        r.getRole(),
                        clinic.getId(),
                        r.getId(),
                        subdomain
                );
            }
        }

        throw new RuntimeException("Invalid email or password");
    }

}
