package com.clinicapp.backend.services;

import org.springframework.stereotype.Service;
import com.clinicapp.backend.entity.Prescription;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class PDFGeneratorService {
    private final String UPLOAD_DIR = "prescriptions/";

    public PDFGeneratorService() {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String generatePrescriptionPDF(Prescription prescription) {
        String fileName = "prescription_" + prescription.getId() + ".pdf";
        String filePath = UPLOAD_DIR + fileName;

        try (FileOutputStream fos = new FileOutputStream(filePath);
             PdfWriter writer = new PdfWriter(fos);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            addPrescriptionContent(document, prescription);
            return filePath;
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage());
        }
    }

    public byte[] getPrescriptionPDF(Prescription prescription) {
        try {
            return Files.readAllBytes(Paths.get(prescription.getFilePath()));
        } catch (Exception e) {
            throw new RuntimeException("Error reading PDF: " + e.getMessage());
        }
    }

    private void addPrescriptionContent(Document document, Prescription prescription) {
        // Simple text-based PDF without advanced formatting
        Paragraph clinicHeader = new Paragraph(prescription.getClinic().getName())
                .setBold().setFontSize(18);
        document.add(clinicHeader);

        Paragraph clinicAddress = new Paragraph(prescription.getClinic().getAddress())
                .setFontSize(10);
        document.add(clinicAddress);

        document.add(new Paragraph(" "));

        // Doctor Info
        document.add(new Paragraph("DOCTOR: " + prescription.getDoctor().getName()).setBold());
        document.add(new Paragraph("Specialization: " +
                (prescription.getDoctor().getSpecialization() != null ?
                        prescription.getDoctor().getSpecialization() : "General")));

        document.add(new Paragraph(" "));

        // Patient Info
        document.add(new Paragraph("PATIENT: " + prescription.getPatient().getName()).setBold());
        document.add(new Paragraph("Age: " + prescription.getPatient().getAge() +
                " | Gender: " + prescription.getPatient().getGender()));
        document.add(new Paragraph("Contact: " + prescription.getPatient().getContact()));

        document.add(new Paragraph(" "));

        // Prescription Details
        document.add(new Paragraph("PRESCRIPTION DETAILS").setBold());
        document.add(new Paragraph("Date: " + prescription.getPrescriptionDate()));
        document.add(new Paragraph("Diagnosis: " + prescription.getDiagnosis()));

        if (prescription.getNotes() != null && !prescription.getNotes().isEmpty()) {
            document.add(new Paragraph("Notes: " + prescription.getNotes()));
        }

        if (prescription.getFollowUpDate() != null) {
            document.add(new Paragraph("Follow-up Date: " + prescription.getFollowUpDate()));
        }

        document.add(new Paragraph(" "));

        // Medicines
        document.add(new Paragraph("PRESCRIBED MEDICINES").setBold());
        for (var medicine : prescription.getMedicines()) {
            document.add(new Paragraph("• " + medicine.getName() + " - " + medicine.getDosage()));
            document.add(new Paragraph("  Frequency: " + medicine.getFrequency() +
                    ", Duration: " + medicine.getDuration()));
            document.add(new Paragraph("  Instructions: " + medicine.getInstructions()));
            document.add(new Paragraph(" "));
        }

        // Signature
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Doctor's Signature: _________________"));
        document.add(new Paragraph(prescription.getDoctor().getName()).setBold());
    }
}