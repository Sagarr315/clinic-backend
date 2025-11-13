package com.clinicapp.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinicapp.backend.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
	Optional<Patient> findByContact(String contact);

	List<Patient> findByNameContainingOrContactContaining(String name, String contact);

    Long countByClinicId(Long clinicId);
}