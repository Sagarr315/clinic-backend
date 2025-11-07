package com.clinicapp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clinicapp.backend.entity.Prescription;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
}
