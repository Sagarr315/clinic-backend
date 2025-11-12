package com.clinicapp.backend.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.clinicapp.backend.entity.Bill;

public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByClinicId(Long clinicId);
    List<Bill> findByAppointmentPatientId(Long patientId);
    List<Bill> findByStatus(String status);
}