package com.clinicapp.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.clinicapp.backend.entity.Doctor;
import com.clinicapp.backend.entity.Receptionist;
import com.clinicapp.backend.repositories.DoctorRepository;
import com.clinicapp.backend.repositories.ReceptionistRepository;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ReceptionistRepository receptionistRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Check doctors first
        Doctor doctor = doctorRepository.findByEmail(email)
                .orElse(null);

        if (doctor != null) {
            // DON'T remove ROLE_ prefix - keep it as is
            return new User(
                    doctor.getEmail(),
                    doctor.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(doctor.getRole()))
            );
        }

        // Check receptionists
        Receptionist receptionist = receptionistRepository.findByEmail(email)
                .orElse(null);

        if (receptionist != null) {
            // DON'T remove ROLE_ prefix - keep it as is
            return new User(
                    receptionist.getEmail(),
                    receptionist.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(receptionist.getRole()))
            );
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}