package com.clinicapp.backend.services;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.clinicapp.backend.entity.Doctor;
import com.clinicapp.backend.entity.DoctorSchedule;
import com.clinicapp.backend.repositories.DoctorRepository;
import com.clinicapp.backend.repositories.DoctorScheduleRepository;

@Service
public class DoctorScheduleService {

	private final DoctorScheduleRepository scheduleRepo;
	private final DoctorRepository doctorRepo;

	public DoctorScheduleService(DoctorScheduleRepository scheduleRepo, DoctorRepository doctorRepo) {
		this.scheduleRepo = scheduleRepo;
		this.doctorRepo = doctorRepo;
	}

	public DoctorSchedule createSchedule(Long doctorId, String dayOfWeek, String startTime, String endTime) {
		if (doctorId == null) {
			throw new RuntimeException("Doctor ID is required");
		}

		Doctor doctor = doctorRepo.findById(doctorId)
				.orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));

		DoctorSchedule schedule = new DoctorSchedule();
		schedule.setDoctor(doctor);
		schedule.setDayOfWeek(dayOfWeek);
		schedule.setStartTime(LocalTime.parse(startTime));
		schedule.setEndTime(LocalTime.parse(endTime));

		return scheduleRepo.save(schedule);
	}

	public List<DoctorSchedule> getSchedulesByDoctor(Long doctorId) {
		if (doctorId == null) {
			throw new RuntimeException("Doctor ID cannot be null");
		}
		return scheduleRepo.findByDoctorId(doctorId);
	}

	public DoctorSchedule updateSchedule(Long id, String dayOfWeek, String startTime, String endTime) {
		if (id == null) {
			throw new RuntimeException("Schedule ID cannot be null");
		}

		DoctorSchedule schedule = scheduleRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Schedule not found with ID: " + id));

		if (dayOfWeek != null)
			schedule.setDayOfWeek(dayOfWeek);
		if (startTime != null)
			schedule.setStartTime(LocalTime.parse(startTime));
		if (endTime != null)
			schedule.setEndTime(LocalTime.parse(endTime));

		return scheduleRepo.save(schedule);
	}

	public void deleteSchedule(Long id) {
		if (id == null) {
			throw new RuntimeException("Schedule ID cannot be null");
		}

		if (!scheduleRepo.existsById(id)) {
			throw new RuntimeException("Schedule not found with ID: " + id);
		}
		scheduleRepo.deleteById(id);
	}
}