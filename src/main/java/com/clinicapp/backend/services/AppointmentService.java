package com.clinicapp.backend.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.clinicapp.backend.entity.Appointment;
import com.clinicapp.backend.entity.Clinic;
import com.clinicapp.backend.entity.Doctor;
import com.clinicapp.backend.entity.DoctorSchedule;
import com.clinicapp.backend.entity.Patient;
import com.clinicapp.backend.repositories.AppointmentRepository;
import com.clinicapp.backend.repositories.ClinicRepository;
import com.clinicapp.backend.repositories.DoctorRepository;
import com.clinicapp.backend.repositories.DoctorScheduleRepository;
import com.clinicapp.backend.repositories.PatientRepository;

@Service
public class AppointmentService {

	private final AppointmentRepository appointmentRepo;
	private final DoctorRepository doctorRepo;
	private final ClinicRepository clinicRepo;
	private final PatientRepository patientRepo;
	private final DoctorScheduleRepository doctorScheduleRepo;

	public AppointmentService(AppointmentRepository appointmentRepo, DoctorRepository doctorRepo,
			ClinicRepository clinicRepo, PatientRepository patientRepo, DoctorScheduleRepository doctorScheduleRepo) {
		this.appointmentRepo = appointmentRepo;
		this.doctorRepo = doctorRepo;
		this.clinicRepo = clinicRepo;
		this.patientRepo = patientRepo;
		this.doctorScheduleRepo = doctorScheduleRepo;
	}

	public List<LocalDateTime> generateSlots(Long doctorId, LocalDate date) {
		if (doctorId == null || date == null) {
			throw new RuntimeException("Doctor ID and date are required");
		}

		String dayOfWeek = date.getDayOfWeek().toString();
		List<DoctorSchedule> schedules = doctorScheduleRepo.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek);

		if (schedules.isEmpty()) {
			throw new RuntimeException("Doctor not available on " + dayOfWeek);
		}

		DoctorSchedule schedule = schedules.get(0);
		LocalTime start = schedule.getStartTime();
		LocalTime end = schedule.getEndTime();
		int slotMinutes = 15;

		List<LocalDateTime> slots = new ArrayList<>();
		LocalDateTime time = date.atTime(start);

		while (!time.isAfter(date.atTime(end))) {
			if (!appointmentRepo.existsByDoctorIdAndAppointmentDate(doctorId, time)) {
				slots.add(time);
			}
			time = time.plusMinutes(slotMinutes);
		}
		return slots;
	}

	public Appointment bookAppointment(Long doctorId, Long clinicId, String name, String contact, String gender,
			Integer age, LocalDateTime appointmentDate) {

		if (appointmentRepo.existsByDoctorIdAndAppointmentDate(doctorId, appointmentDate)) {
			throw new RuntimeException("This slot is already booked!");
		}

		Doctor doctor = doctorRepo.findById(doctorId).orElseThrow(() -> new RuntimeException("Doctor not found"));
		Clinic clinic = clinicRepo.findById(clinicId).orElseThrow(() -> new RuntimeException("Clinic not found"));

		Patient patient = patientRepo.findByContact(contact).orElseGet(() -> {
			Patient newPatient = new Patient();
			newPatient.setName(name);
			newPatient.setContact(contact);
			newPatient.setGender(gender);
			newPatient.setAge(age);
			newPatient.setClinic(clinic);
			return patientRepo.save(newPatient);
		});

		Appointment appointment = new Appointment();
		appointment.setDoctor(doctor);
		appointment.setClinic(clinic);
		appointment.setPatient(patient);
		appointment.setAppointmentDate(appointmentDate);
		appointment.setStatus("BOOKED");

		return appointmentRepo.save(appointment);
	}

	public List<Appointment> getAppointmentsByClinic(Long clinicId) {
		return appointmentRepo.findByClinicId(clinicId);
	}

	public Appointment updateStatus(Long appointmentId, String status) {
		if (appointmentId == null) {
			throw new RuntimeException("Appointment ID cannot be null");
		}

		Appointment appointment = appointmentRepo.findById(appointmentId)
				.orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + appointmentId));

		if (!isValidStatusTransition(appointment.getStatus(), status)) {
			throw new RuntimeException("Invalid status transition from " + appointment.getStatus() + " to " + status);
		}

		appointment.setStatus(status);
		return appointmentRepo.save(appointment);
	}

	public Appointment reschedule(Long appointmentId, String newDateTime) {
		if (appointmentId == null) {
			throw new RuntimeException("Appointment ID cannot be null");
		}

		Appointment appointment = appointmentRepo.findById(appointmentId)
				.orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + appointmentId));

		LocalDateTime newDate = LocalDateTime.parse(newDateTime);

		if (appointmentRepo.existsByDoctorIdAndAppointmentDate(appointment.getDoctor().getId(), newDate)) {
			throw new RuntimeException("New slot is already booked");
		}

		appointment.setAppointmentDate(newDate);
		return appointmentRepo.save(appointment);
	}

	public void cancelAppointment(Long appointmentId) {
		if (appointmentId == null) {
			throw new RuntimeException("Appointment ID cannot be null");
		}

		Appointment appointment = appointmentRepo.findById(appointmentId)
				.orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + appointmentId));

		appointment.setStatus("CANCELLED");
		appointmentRepo.save(appointment);
	}

	private boolean isValidStatusTransition(String currentStatus, String newStatus) {
		if (currentStatus.equals("BOOKED")) {
			return newStatus.equals("CONFIRMED") || newStatus.equals("CANCELLED");
		} else if (currentStatus.equals("CONFIRMED")) {
			return newStatus.equals("COMPLETED") || newStatus.equals("CANCELLED");
		} else if (currentStatus.equals("COMPLETED") || currentStatus.equals("CANCELLED")) {
			return false;
		}
		return true;
	}
}