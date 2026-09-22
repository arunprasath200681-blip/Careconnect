package com.careconnect.dto;

import jakarta.validation.constraints.NotBlank;

public class PrescriptionRequest {

    private String doctorNotes;

    @NotBlank(message = "Prescription cannot be blank")
    private String prescription;

    public PrescriptionRequest() {
    }

    public PrescriptionRequest(String doctorNotes, String prescription) {
        this.doctorNotes = doctorNotes;
        this.prescription = prescription;
    }

    public String getDoctorNotes() {
        return doctorNotes;
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }
}
