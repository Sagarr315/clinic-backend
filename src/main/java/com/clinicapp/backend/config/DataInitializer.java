package com.clinicapp.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.clinicapp.backend.entity.Doctor;
import com.clinicapp.backend.repositories.DoctorRepository;

@Component
public class DataInitializer implements CommandLineRunner {

	private final DoctorRepository doctorRepo;
	private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	public DataInitializer(DoctorRepository doctorRepo) {
		this.doctorRepo = doctorRepo;
	}

	@Override
	public void run(String... args) throws Exception {
		if (doctorRepo.findByEmail("superadmin@system.com").isEmpty()) {
			Doctor admin = new Doctor();
			admin.setName("System Super Admin");
			admin.setEmail("superadmin@system.com");
			admin.setPassword(encoder.encode("Super@123"));
			admin.setRole("ROLE_SUPERADMIN");
			admin.setClinic(null);
			doctorRepo.save(admin);
			System.out.println(" Super Admin seeded: superadmin@system.com / Super@123");
		}
	}
}
