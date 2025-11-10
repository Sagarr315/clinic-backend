package com.clinicapp.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinicapp.backend.entity.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
	Optional<Doctor> findByEmail(String email);

	Optional<Doctor> findByEmailAndClinicId(String email, Long clinicId);

	List<Doctor> findByClinicId(Long clinicId);
}