package com.clinicapp.backend.services;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder; //  Use interface
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
    private final PasswordEncoder passwordEncoder; //  Use injected encoder

    public AuthService(DoctorRepository doctorRepo, ReceptionistRepository receptionistRepo,
                       ClinicRepository clinicRepo, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) { //  Add PasswordEncoder
        this.doctorRepo = doctorRepo;
        this.receptionistRepo = receptionistRepo;
        this.clinicRepo = clinicRepo;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder; //  Use injected encoder
    }

    //  USED BY ADMIN ONLY - Register new doctor
    public AuthResponse register(RegisterRequest request) {
        if (doctorRepo.findByEmailAndClinicId(request.getEmail(), request.getClinicId()).isPresent()) {
            throw new RuntimeException("Doctor already exists in this clinic");
        }

        Doctor doctor = new Doctor();
        doctor.setName(request.getName());
        doctor.setEmail(request.getEmail());
        doctor.setPassword(passwordEncoder.encode(request.getPassword())); //  Use injected encoder
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

    //  USED BY ADMIN ONLY - Register new receptionist
    public AuthResponse registerReceptionist(RegisterRequest request) {
        if (receptionistRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Receptionist already exists");
        }

        Receptionist receptionist = new Receptionist();
        receptionist.setName(request.getName());
        receptionist.setEmail(request.getEmail());
        receptionist.setPassword(passwordEncoder.encode(request.getPassword())); //  Use injected encoder
        receptionist.setRole("ROLE_RECEPTIONIST");

        Clinic clinic = clinicRepo.findById(request.getClinicId())
                .orElseThrow(() -> new RuntimeException("Clinic not found"));
        receptionist.setClinic(clinic);

        receptionistRepo.save(receptionist);
        String token = jwtUtils.generateToken(receptionist.getEmail(), receptionist.getRole(), clinic.getId());

        return new AuthResponse(token, receptionist.getRole(), clinic.getId());
    }

    //  PUBLIC - Login for all users
    public AuthResponse login(LoginRequest request) {
        // Try Doctor first
        Optional<Doctor> doctorOpt = doctorRepo.findByEmail(request.getEmail());
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            if (passwordEncoder.matches(request.getPassword(), doctor.getPassword())) { //  Use injected encoder
                String token = jwtUtils.generateToken(doctor.getEmail(), doctor.getRole(),
                        doctor.getClinic() != null ? doctor.getClinic().getId() : null);
                return new AuthResponse(token, doctor.getRole(),
                        doctor.getClinic() != null ? doctor.getClinic().getId() : null);
            }
        }

        // Try Receptionist
        Optional<Receptionist> receptionistOpt = receptionistRepo.findByEmail(request.getEmail());
        if (receptionistOpt.isPresent()) {
            Receptionist receptionist = receptionistOpt.get();
            if (passwordEncoder.matches(request.getPassword(), receptionist.getPassword())) { //  Use injected encoder
                String token = jwtUtils.generateToken(receptionist.getEmail(), receptionist.getRole(),
                        receptionist.getClinic().getId());
                return new AuthResponse(token, receptionist.getRole(), receptionist.getClinic().getId());
            }
        }

        throw new RuntimeException("Invalid email or password");
    }
}