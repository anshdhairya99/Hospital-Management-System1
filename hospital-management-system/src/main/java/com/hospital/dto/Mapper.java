package com.hospital.dto;

import com.hospital.dto.Dtos.*;
import com.hospital.entity.*;

/** Entity -> DTO mapping. Call inside a transaction (lazy associations are touched here). */
public final class Mapper {

    private Mapper() {}

    public static DoctorResponse toResponse(Doctor d) {
        return new DoctorResponse(d.getId(), d.getFullName(), d.getSpecialization(), d.getPhone(),
                d.getConsultationFee(), d.getDepartment().getName());
    }

    public static PatientResponse toResponse(Patient p) {
        return new PatientResponse(p.getId(), p.getFullName(), p.getAge(), p.getGender(), p.getPhone(),
                p.getAddress(), p.getBloodGroup());
    }

    public static AppointmentResponse toResponse(Appointment a) {
        return new AppointmentResponse(a.getId(), a.getDoctor().getId(), a.getDoctor().getFullName(),
                a.getPatient().getId(), a.getPatient().getFullName(), a.getAppointmentTime(),
                a.getStatus(), a.getReason());
    }

    public static PrescriptionResponse toResponse(Prescription p) {
        Appointment a = p.getAppointment();
        return new PrescriptionResponse(p.getId(), a.getId(), a.getDoctor().getFullName(),
                a.getPatient().getFullName(), p.getDiagnosis(), p.getMedicines(), p.getNotes(),
                p.getCreatedAt());
    }
}
