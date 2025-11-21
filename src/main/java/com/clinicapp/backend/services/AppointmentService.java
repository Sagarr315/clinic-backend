package com.clinicapp.backend.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.clinicapp.backend.entity.Appointment;
import com.clinicapp.backend.entity.Clinic;
import com.clinicapp.backend.entity.Doctor;
import com.clinicapp.backend.entity.DoctorSchedule;
import com.clinicapp.backend.entity.Patient;
import com.clinicapp.backend.entity.Receptionist;
import com.clinicapp.backend.repositories.AppointmentRepository;
import com.clinicapp.backend.repositories.ClinicRepository;
import com.clinicapp.backend.repositories.DoctorRepository;
import com.clinicapp.backend.repositories.DoctorScheduleRepository;
import com.clinicapp.backend.repositories.PatientRepository;
import com.clinicapp.backend.repositories.ReceptionistRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final DoctorRepository doctorRepo;
    private final ClinicRepository clinicRepo;
    private final PatientRepository patientRepo;
    private final DoctorScheduleRepository doctorScheduleRepo;
    private final BillingService billingService;
    private final ReceptionistRepository receptionistRepo;

    public AppointmentService(AppointmentRepository appointmentRepo, DoctorRepository doctorRepo,
                              ClinicRepository clinicRepo, PatientRepository patientRepo,
                              DoctorScheduleRepository doctorScheduleRepo, BillingService billingService,
                              ReceptionistRepository receptionistRepo) {
        this.appointmentRepo = appointmentRepo;
        this.doctorRepo = doctorRepo;
        this.clinicRepo = clinicRepo;
        this.patientRepo = patientRepo;
        this.doctorScheduleRepo = doctorScheduleRepo;
        this.billingService = billingService;
        this.receptionistRepo = receptionistRepo;
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
    // Returns appointments only for the specified doctor ID
    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepo.findByDoctorId(doctorId);
    }

    public Appointment updateStatus(Long appointmentId, String status) {
        if (appointmentId == null) {
            throw new RuntimeException("Appointment ID cannot be null");
        }

        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + appointmentId));

        String currentUserRole = getCurrentUserRole();

        // Receptionist can update from BOOKED to CONFIRMED or CANCELLED
        if ("ROLE_RECEPTIONIST".equals(currentUserRole)) {
            if (!appointment.getStatus().equals("BOOKED") ||
                    (!status.equals("CONFIRMED") && !status.equals("CANCELLED"))) {
                throw new RuntimeException("Receptionist can only confirm or cancel booked appointments");
            }
        }
        // Doctor can only update their own appointments from CONFIRMED to COMPLETED or CANCELLED
        else if ("ROLE_DOCTOR".equals(currentUserRole)) {
            Doctor currentDoctor = getCurrentDoctor();
            if (!appointment.getDoctor().getId().equals(currentDoctor.getId())) {
                throw new RuntimeException("You can only update appointments assigned to you");
            }
            if (!appointment.getStatus().equals("CONFIRMED") ||
                    (!status.equals("COMPLETED") && !status.equals("CANCELLED"))) {
                throw new RuntimeException("Doctor can only complete or cancel confirmed appointments");
            }
        } else {
            throw new RuntimeException("Unauthorized to update appointment status");
        }

        if (!isValidStatusTransition(appointment.getStatus(), status)) {
            throw new RuntimeException("Invalid status transition from " + appointment.getStatus() + " to " + status);
        }

        appointment.setStatus(status);
        Appointment savedAppointment = appointmentRepo.save(appointment);

        // Generate bill automatically when appointment is completed
        if ("COMPLETED".equals(status)) {
            billingService.generateBill(savedAppointment);
        }

        return savedAppointment;
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

    // Get current logged-in doctor
    private Doctor getCurrentDoctor() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return doctorRepo.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    // Get current user role
    private String getCurrentUserRole() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getAuthorities().iterator().next().getAuthority();
        }
        return principal.toString();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }
    // Add this method to your existing AppointmentService
    public List<Object[]> getPatientAppointmentHistory(Long patientId) {
        return appointmentRepo.findPatientHistory(patientId);
    }
}