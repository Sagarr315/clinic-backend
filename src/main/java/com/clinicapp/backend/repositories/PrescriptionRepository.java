package com.clinicapp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clinicapp.backend.entity.Prescription;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    Optional<Prescription> findByAppointmentId(Long appointmentId);
}
