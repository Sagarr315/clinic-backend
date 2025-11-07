package com.clinicapp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clinicapp.backend.entity.Receptionist;

public interface ReceptionistRepository extends JpaRepository<Receptionist, Long>{

}
