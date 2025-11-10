package com.clinicapp.backend.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.clinicapp.backend.entity.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
	List<Appointment> findByClinicId(Long clinicId);

	boolean existsByDoctorIdAndAppointmentDate(Long doctorId, LocalDateTime appointmentDate);

	@Query("SELECT a.appointmentDate, a.status, d.name FROM Appointment a JOIN a.doctor d WHERE a.patient.id = :patientId ORDER BY a.appointmentDate DESC")
	List<Object[]> findPatientHistory(Long patientId);
}