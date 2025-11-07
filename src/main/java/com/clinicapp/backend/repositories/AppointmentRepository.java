package com.clinicapp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clinicapp.backend.entity.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment ,Long>{

}
