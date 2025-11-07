package com.clinicapp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clinicapp.backend.entity.Clinic;

public interface ClinicRepository extends JpaRepository<Clinic, Long> {
}
