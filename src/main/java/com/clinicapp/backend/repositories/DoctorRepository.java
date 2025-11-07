package com.clinicapp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clinicapp.backend.entity.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

}
