package com.hospital.service;

import com.hospital.dto.Dtos.PrescriptionRequest;
import com.hospital.dto.Dtos.PrescriptionResponse;
import com.hospital.dto.Mapper;
import com.hospital.entity.*;
import com.hospital.exception.BusinessException;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public PrescriptionResponse create(String doctorUsername, PrescriptionRequest req) {
        Doctor doctor = doctorRepository.findByUserUsername(doctorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));
        Appointment appt = appointmentRepository.findById(req.appointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + req.appointmentId()));

        if (!appt.getDoctor().getId().equals(doctor.getId())) {
            throw new AccessDeniedException("Not your appointment");
        }
        if (appt.getStatus() == AppointmentStatus.PENDING || appt.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessException("Prescription needs a CONFIRMED or COMPLETED appointment");
        }
        if (prescriptionRepository.existsByAppointmentId(appt.getId())) {
            throw new BusinessException("A prescription already exists for this appointment");
        }

        Prescription p = new Prescription();
        p.setAppointment(appt);
        p.setDiagnosis(req.diagnosis());
        p.setMedicines(req.medicines());
        p.setNotes(req.notes());
        return Mapper.toResponse(prescriptionRepository.save(p));
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> forPatient(String patientUsername) {
        Patient patient = patientRepository.findByUserUsername(patientUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));
        return prescriptionRepository.findByAppointmentPatientIdOrderByCreatedAtDesc(patient.getId())
                .stream().map(Mapper::toResponse).toList();
    }

    /** Only the treating doctor or the patient of that appointment can read the prescription. */
    @Transactional(readOnly = true)
    public PrescriptionResponse byAppointment(Long appointmentId, String username) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + appointmentId));
        boolean isDoctor = appt.getDoctor().getUser().getUsername().equals(username);
        boolean isPatient = appt.getPatient().getUser().getUsername().equals(username);
        if (!isDoctor && !isPatient) {
            throw new AccessDeniedException("You can not view this prescription");
        }
        return prescriptionRepository.findByAppointmentId(appointmentId).map(Mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No prescription for appointment " + appointmentId));
    }
}
