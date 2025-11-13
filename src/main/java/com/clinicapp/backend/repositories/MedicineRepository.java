package com.clinicapp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clinicapp.backend.entity.Medicine;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
}