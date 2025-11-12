package com.clinicapp.backend.services;

import org.springframework.stereotype.Service;
import com.clinicapp.backend.repositories.PatientRepository;
import com.clinicapp.backend.repositories.AppointmentRepository;
import com.clinicapp.backend.repositories.BillRepository;
import java.util.HashMap;
import java.util.Map;

@Service
public class AnalyticsService {
    private final PatientRepository patientRepo;
    private final AppointmentRepository appointmentRepo;
    private final BillRepository billRepo;

    public AnalyticsService(PatientRepository patientRepo,
                            AppointmentRepository appointmentRepo, BillRepository billRepo) {
        this.patientRepo = patientRepo;
        this.appointmentRepo = appointmentRepo;
        this.billRepo = billRepo;
    }

    public Map<String, Object> getClinicSummary(Long clinicId) {
        Map<String, Object> summary = new HashMap<>();

        Long totalPatients = patientRepo.countByClinicId(clinicId);
        Long totalAppointments = appointmentRepo.countByClinicId(clinicId);
        Long completedAppointments = appointmentRepo.countByClinicIdAndStatus(clinicId, "COMPLETED");

        Double totalRevenue = billRepo.getTotalRevenueByClinicId(clinicId);
        Double pendingRevenue = billRepo.getPendingRevenueByClinicId(clinicId);

        summary.put("totalPatients", totalPatients);
        summary.put("totalAppointments", totalAppointments);
        summary.put("completedAppointments", completedAppointments);
        summary.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
        summary.put("pendingRevenue", pendingRevenue != null ? pendingRevenue : 0.0);

        return summary;
    }

    public Map<String, Object> getDoctorPerformance(Long doctorId) {
        Map<String, Object> performance = new HashMap<>();

        Long totalAppointments = appointmentRepo.countByDoctorId(doctorId);
        Long completedAppointments = appointmentRepo.countByDoctorIdAndStatus(doctorId, "COMPLETED");
        Double doctorRevenue = billRepo.getRevenueByDoctorId(doctorId);

        double completionRate = totalAppointments > 0 ?
                (completedAppointments.doubleValue() / totalAppointments.doubleValue()) * 100 : 0;

        performance.put("totalAppointments", totalAppointments);
        performance.put("completedAppointments", completedAppointments);
        performance.put("completionRate", Math.round(completionRate * 100.0) / 100.0);
        performance.put("revenueGenerated", doctorRevenue != null ? doctorRevenue : 0.0);

        return performance;
    }
}