package com.hospital.service;

import com.hospital.dto.Dtos.AppointmentResponse;
import com.hospital.dto.Dtos.BookAppointmentRequest;
import com.hospital.dto.Mapper;
import com.hospital.entity.*;
import com.hospital.exception.BusinessException;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    static final int SLOT_MINUTES = 30;

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    /**
     * Books a slot. The doctor row is locked (PESSIMISTIC_WRITE) so two patients can not
     * book overlapping slots for the same doctor at the same time.
     */
    @Transactional
    public AppointmentResponse book(String patientUsername, BookAppointmentRequest req) {
        Patient patient = patientRepository.findByUserUsername(patientUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));
        Doctor doctor = doctorRepository.findByIdForUpdate(req.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + req.doctorId()));

        if (!req.appointmentTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException("Appointment time must be in the future");
        }

        long conflicts = appointmentRepository.countConflicts(doctor.getId(),
                req.appointmentTime().minusMinutes(SLOT_MINUTES),
                req.appointmentTime().plusMinutes(SLOT_MINUTES));
        if (conflicts > 0) {
            throw new BusinessException("Doctor is not available at the selected time");
        }

        Appointment a = new Appointment();
        a.setDoctor(doctor);
        a.setPatient(patient);
        a.setAppointmentTime(req.appointmentTime());
        a.setReason(req.reason());
        a.setStatus(AppointmentStatus.PENDING);
        return Mapper.toResponse(appointmentRepository.save(a));
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> forPatient(String username, Pageable pageable) {
        Patient p = patientRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));
        return appointmentRepository.findByPatientId(p.getId(), pageable).map(Mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> forDoctor(String username, Pageable pageable) {
        Doctor d = doctorRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));
        return appointmentRepository.findByDoctorId(d.getId(), pageable).map(Mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> all(Pageable pageable) {
        return appointmentRepository.findAll(pageable).map(Mapper::toResponse);
    }

    /** Doctor confirms / completes / cancels an appointment that belongs to them. */
    @Transactional
    public AppointmentResponse updateStatus(Long id, String doctorUsername, AppointmentStatus next) {
        Appointment a = find(id);
        if (!a.getDoctor().getUser().getUsername().equals(doctorUsername)) {
            throw new AccessDeniedException("Not your appointment");
        }
        return Mapper.toResponse(transition(a, next));
    }

    /** Patient cancels their own appointment. */
    @Transactional
    public AppointmentResponse cancel(Long id, String patientUsername) {
        Appointment a = find(id);
        if (!a.getPatient().getUser().getUsername().equals(patientUsername)) {
            throw new AccessDeniedException("Not your appointment");
        }
        return Mapper.toResponse(transition(a, AppointmentStatus.CANCELLED));
    }

    private Appointment transition(Appointment a, AppointmentStatus next) {
        if (!a.getStatus().canMoveTo(next)) {
            throw new BusinessException("Cannot change status from " + a.getStatus() + " to " + next);
        }
        a.setStatus(next);   // dirty checking flushes the update on commit; @Version guards concurrency
        return a;
    }

    private Appointment find(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + id));
    }
}
