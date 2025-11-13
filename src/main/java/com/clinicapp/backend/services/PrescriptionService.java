package com.clinicapp.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import com.clinicapp.backend.entity.*;
import com.clinicapp.backend.repositories.*;
import java.util.List;

@Service
public class PrescriptionService {
    private final PrescriptionRepository prescriptionRepo;
    private final MedicineRepository medicineRepo;
    private final AppointmentRepository appointmentRepo;
    private final PDFGeneratorService pdfGeneratorService;
    private final DoctorRepository doctorRepo;

    public PrescriptionService(PrescriptionRepository prescriptionRepo,
                               MedicineRepository medicineRepo,
                               AppointmentRepository appointmentRepo,
                               PDFGeneratorService pdfGeneratorService,
                               DoctorRepository doctorRepo) {
        this.prescriptionRepo = prescriptionRepo;
        this.medicineRepo = medicineRepo;
        this.appointmentRepo = appointmentRepo;
        this.pdfGeneratorService = pdfGeneratorService;
        this.doctorRepo = doctorRepo;
    }

    public Prescription createPrescription(PrescriptionRequest request) {
        Appointment appointment = appointmentRepo.findById(request.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!"COMPLETED".equals(appointment.getStatus())) {
            throw new RuntimeException("Can only create prescription for completed appointments");
        }

        Prescription prescription = new Prescription();
        prescription.setAppointment(appointment);
        prescription.setClinic(appointment.getClinic());
        prescription.setDoctor(appointment.getDoctor());
        prescription.setPatient(appointment.getPatient());
        prescription.setDiagnosis(request.getDiagnosis());
        prescription.setNotes(request.getNotes());
        prescription.setFollowUpDate(request.getFollowUpDate());

        for (MedicineRequest medRequest : request.getMedicines()) {
            Medicine medicine = new Medicine();
            medicine.setName(medRequest.getName());
            medicine.setDosage(medRequest.getDosage());
            medicine.setFrequency(medRequest.getFrequency());
            medicine.setDuration(medRequest.getDuration());
            medicine.setInstructions(medRequest.getInstructions());
            prescription.addMedicine(medicine);
        }

        Prescription savedPrescription = prescriptionRepo.save(prescription);

        String filePath = pdfGeneratorService.generatePrescriptionPDF(savedPrescription);
        savedPrescription.setFilePath(filePath);

        return prescriptionRepo.save(savedPrescription);
    }

    public Prescription getPrescriptionByAppointment(Long appointmentId) {
        Prescription prescription = prescriptionRepo.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        checkPrescriptionAccess(prescription);

        return prescription;
    }

    public byte[] downloadPrescriptionPDF(Long prescriptionId) {
        Prescription prescription = prescriptionRepo.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        checkPrescriptionAccess(prescription);

        return pdfGeneratorService.getPrescriptionPDF(prescription);
    }

    private void checkPrescriptionAccess(Prescription prescription) {
        String currentUserRole = getCurrentUserRole();

        if (currentUserRole.equals("DOCTOR")) {
            // Regular doctor - can only access their own prescriptions
            Doctor currentDoctor = getCurrentDoctor();
            if (!prescription.getDoctor().getId().equals(currentDoctor.getId())) {
                throw new RuntimeException("You can only access your own prescriptions");
            }
        }
        else if (currentUserRole.equals("ROLE_ADMIN_DOCTOR")) {
            // Admin doctor - can access ALL prescriptions in their clinic

        }
        else if (!currentUserRole.equals("ROLE_RECEPTIONIST")) {
            throw new RuntimeException("Unauthorized access to prescriptions");
        }
    }

    private String getCurrentUserRole() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getAuthorities().iterator().next().getAuthority();
        }
        return principal.toString();
    }

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

    public static class PrescriptionRequest {
        private Long appointmentId;
        private String diagnosis;
        private String notes;
        private java.time.LocalDate followUpDate;
        private List<MedicineRequest> medicines;

        public Long getAppointmentId() { return appointmentId; }
        public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

        public String getDiagnosis() { return diagnosis; }
        public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }

        public java.time.LocalDate getFollowUpDate() { return followUpDate; }
        public void setFollowUpDate(java.time.LocalDate followUpDate) { this.followUpDate = followUpDate; }

        public List<MedicineRequest> getMedicines() { return medicines; }
        public void setMedicines(List<MedicineRequest> medicines) { this.medicines = medicines; }
    }

    public static class MedicineRequest {
        private String name;
        private String dosage;
        private String frequency;
        private String duration;
        private String instructions;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDosage() { return dosage; }
        public void setDosage(String dosage) { this.dosage = dosage; }

        public String getFrequency() { return frequency; }
        public void setFrequency(String frequency) { this.frequency = frequency; }

        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }

        public String getInstructions() { return instructions; }
        public void setInstructions(String instructions) { this.instructions = instructions; }
    }
}