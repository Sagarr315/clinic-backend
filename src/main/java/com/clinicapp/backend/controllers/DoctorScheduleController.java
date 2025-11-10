package com.clinicapp.backend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicapp.backend.entity.DoctorSchedule;
import com.clinicapp.backend.services.DoctorScheduleService;

@RestController
@RequestMapping("/api/doctor-schedules")
public class DoctorScheduleController {

	private final DoctorScheduleService scheduleService;

	public DoctorScheduleController(DoctorScheduleService scheduleService) {
		this.scheduleService = scheduleService;
	}

	@PostMapping
	public DoctorSchedule createSchedule(@RequestBody ScheduleRequest request) {
		return scheduleService.createSchedule(request.getDoctorId(), request.getDayOfWeek(), request.getStartTime(),
				request.getEndTime());
	}

	@GetMapping("/doctor/{doctorId}")
	public List<DoctorSchedule> getDoctorSchedules(@PathVariable Long doctorId) {
		return scheduleService.getSchedulesByDoctor(doctorId);
	}

	@PutMapping("/{id}")
	public DoctorSchedule updateSchedule(@PathVariable Long id, @RequestBody ScheduleRequest request) {
		return scheduleService.updateSchedule(id, request.getDayOfWeek(), request.getStartTime(), request.getEndTime());
	}

	@DeleteMapping("/{id}")
	public String deleteSchedule(@PathVariable Long id) {
		scheduleService.deleteSchedule(id);
		return "Schedule deleted successfully";
	}

	public static class ScheduleRequest {
		private Long doctorId;
		private String dayOfWeek;
		private String startTime;
		private String endTime;

		public Long getDoctorId() {
			return doctorId;
		}

		public void setDoctorId(Long doctorId) {
			this.doctorId = doctorId;
		}

		public String getDayOfWeek() {
			return dayOfWeek;
		}

		public void setDayOfWeek(String dayOfWeek) {
			this.dayOfWeek = dayOfWeek;
		}

		public String getStartTime() {
			return startTime;
		}

		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}

		public String getEndTime() {
			return endTime;
		}

		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}
	}
}