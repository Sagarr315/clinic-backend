package com.clinicapp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clinicapp.backend.entity.Patient;


public interface PatientRepository extends JpaRepository<Patient ,Long> {

}
