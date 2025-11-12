package com.clinicapp.backend.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import com.clinicapp.backend.entity.Bill;

public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByClinicId(Long clinicId);

    List<Bill> findByAppointmentPatientId(Long patientId);

    List<Bill> findByStatus(String status);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b WHERE b.clinic.id = :clinicId AND b.status = 'PAID'")
    Double getTotalRevenueByClinicId(@Param("clinicId") Long clinicId);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b WHERE b.clinic.id = :clinicId AND b.status = 'PENDING'")
    Double getPendingRevenueByClinicId(@Param("clinicId") Long clinicId);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b WHERE b.appointment.doctor.id = :doctorId AND b.status = 'PAID'")
    Double getRevenueByDoctorId(@Param("doctorId") Long doctorId);
}