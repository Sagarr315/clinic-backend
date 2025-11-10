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

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private DoctorRepository doctorRepo;

	@Autowired
	private ReceptionistRepository recepRepo;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// SUPER ADMIN FIX
		if ("superadmin@system.com".equals(email)) {
			return User.builder().username("superadmin@system.com")
					.password("$2a$10$c35YCctm29tfOkYZKpSJiu5WgFCYnV7d3.n2Ffd00vmX93009IeAq") // Super@123
					.authorities(new SimpleGrantedAuthority("ROLE_SUPERADMIN")).build();
		}

		Doctor doctor = doctorRepo.findByEmail(email).orElse(null);
		if (doctor != null) {
			return User.builder().username(doctor.getEmail()).password(doctor.getPassword())
					.authorities(new SimpleGrantedAuthority(doctor.getRole())).build();
		}

		Receptionist rec = recepRepo.findByEmail(email).orElse(null);
		if (rec != null) {
			return User.builder().username(rec.getEmail()).password(rec.getPassword())
					.authorities(new SimpleGrantedAuthority(rec.getRole())).build();
		}

		throw new UsernameNotFoundException("User not found with email: " + email);
	}
}