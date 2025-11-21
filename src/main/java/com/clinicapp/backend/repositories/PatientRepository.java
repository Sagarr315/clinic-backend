package com.clinicapp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clinicapp.backend.entity.Patient;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByContact(String contact);
    Long countByClinicId(Long clinicId);
    List<Patient> findByClinicId(Long clinicId);
    Optional<Patient> findByIdAndClinicId(Long id, Long clinicId);
    // ==== ADDED ONLY THIS SEARCH METHOD ====
    @Query("SELECT p FROM Patient p WHERE p.clinic.id = :clinicId AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "p.contact LIKE CONCAT('%', :query, '%'))")
    List<Patient> searchByClinicAndQuery(@Param("clinicId") Long clinicId,
                                         @Param("query") String query);
    // ==== END OF ADDED METHOD ====
}