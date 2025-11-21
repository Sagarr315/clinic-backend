package com.clinicapp.backend.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.clinicapp.backend.entity.Appointment;
import org.springframework.data.repository.query.Param;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
	List<Appointment> findByClinicId(Long clinicId);

	boolean existsByDoctorIdAndAppointmentDate(Long doctorId, LocalDateTime appointmentDate);

    // Add this method to your AppointmentRepository
    @Query("SELECT a.appointmentDate, a.status, d.name FROM Appointment a JOIN a.doctor d WHERE a.patient.id = :patientId AND a.clinic.id = :clinicId ORDER BY a.appointmentDate DESC")
    List<Object[]> findPatientHistoryByClinic(@Param("patientId") Long patientId, @Param("clinicId") Long clinicId);
    // Your existing method - keep this
    @Query("SELECT a.appointmentDate, a.status, d.name FROM Appointment a JOIN a.doctor d WHERE a.patient.id = :patientId ORDER BY a.appointmentDate DESC")
    List<Object[]> findPatientHistory(@Param("patientId") Long patientId);
    Long countByClinicId(Long clinicId);
    Long countByClinicIdAndStatus(Long clinicId, String status);
    Long countByDoctorId(Long doctorId);
    Long countByDoctorIdAndStatus(Long doctorId, String status);
    // Custom query to find appointments by doctor ID
    List<Appointment> findByDoctorId(Long doctorId);
}