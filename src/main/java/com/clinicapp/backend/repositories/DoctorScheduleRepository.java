package com.clinicapp.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinicapp.backend.entity.DoctorSchedule;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
	List<DoctorSchedule> findByDoctorId(Long doctorId);

	List<DoctorSchedule> findByDoctorIdAndDayOfWeek(Long doctorId, String dayOfWeek);
}