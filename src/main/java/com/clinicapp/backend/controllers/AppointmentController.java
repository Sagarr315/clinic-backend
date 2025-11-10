package com.clinicapp.backend.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicapp.backend.entity.Appointment;
import com.clinicapp.backend.services.AppointmentService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

	private final AppointmentService appointmentService;

	public AppointmentController(AppointmentService appointmentService) {
		this.appointmentService = appointmentService;
	}

	@GetMapping("/slots/{doctorId}/{date}")
	public List<LocalDateTime> getAvailableSlots(@PathVariable Long doctorId, @PathVariable String date) {
		return appointmentService.generateSlots(doctorId, LocalDate.parse(date));
	}

	@PostMapping("/book")
	public Appointment bookAppointment(@RequestBody AppointmentRequest req) {
		return appointmentService.bookAppointment(req.getDoctorId(), req.getClinicId(), req.getName(), req.getContact(),
				req.getGender(), req.getAge(), req.getAppointmentDate());
	}

	@GetMapping("/clinic/{clinicId}")
	public List<Appointment> getAppointmentsByClinic(@PathVariable Long clinicId) {
		return appointmentService.getAppointmentsByClinic(clinicId);
	}

	public static class AppointmentRequest {
		private Long doctorId;
		private Long clinicId;
		private String name;
		private String contact;
		private String gender;
		private Integer age;
		private LocalDateTime appointmentDate;

		public Long getDoctorId() {
			return doctorId;
		}

		public void setDoctorId(Long doctorId) {
			this.doctorId = doctorId;
		}

		public Long getClinicId() {
			return clinicId;
		}

		public void setClinicId(Long clinicId) {
			this.clinicId = clinicId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getContact() {
			return contact;
		}

		public void setContact(String contact) {
			this.contact = contact;
		}

		public String getGender() {
			return gender;
		}

		public void setGender(String gender) {
			this.gender = gender;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}

		public LocalDateTime getAppointmentDate() {
			return appointmentDate;
		}

		public void setAppointmentDate(LocalDateTime appointmentDate) {
			this.appointmentDate = appointmentDate;
		}
	}
}
