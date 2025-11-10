package com.clinicapp.backend.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicapp.backend.entity.Appointment;
import com.clinicapp.backend.services.AppointmentService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentStatusController {

	private final AppointmentService appointmentService;

	public AppointmentStatusController(AppointmentService appointmentService) {
		this.appointmentService = appointmentService;
	}

	@PutMapping("/{id}/status")
	public Appointment updateStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
		return appointmentService.updateStatus(id, request.getStatus());
	}

	@PutMapping("/{id}/reschedule")
	public Appointment reschedule(@PathVariable Long id, @RequestBody RescheduleRequest request) {
		return appointmentService.reschedule(id, request.getNewDateTime());
	}

	@DeleteMapping("/{id}")
	public String cancelAppointment(@PathVariable Long id) {
		appointmentService.cancelAppointment(id);
		return "Appointment cancelled successfully";
	}

	public static class StatusRequest {
		private String status;

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
	}

	public static class RescheduleRequest {
		private String newDateTime;

		public String getNewDateTime() {
			return newDateTime;
		}

		public void setNewDateTime(String newDateTime) {
			this.newDateTime = newDateTime;
		}
	}
}