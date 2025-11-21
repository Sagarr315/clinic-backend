package com.clinicapp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clinicapp.backend.entity.Prescription;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    Optional<Prescription> findByAppointmentId(Long appointmentId);
    List<Prescription> findByDoctorId(Long doctorId);

    List<Prescription> findByClinicId(Long clinicId);
    List<Prescription> findByDoctorIdAndClinicId(Long doctorId, Long clinicId);
}
