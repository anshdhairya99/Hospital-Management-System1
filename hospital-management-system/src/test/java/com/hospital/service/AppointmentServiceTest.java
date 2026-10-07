package com.hospital.service;

import com.hospital.dto.Dtos.AppointmentResponse;
import com.hospital.dto.Dtos.BookAppointmentRequest;
import com.hospital.entity.*;
import com.hospital.exception.BusinessException;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock AppointmentRepository appointmentRepository;
    @Mock DoctorRepository doctorRepository;
    @Mock PatientRepository patientRepository;
    @InjectMocks AppointmentService service;

    Patient patient;
    Doctor doctor;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(10L);
        patient.setFullName("Alice");
        patient.setUser(new AppUser("alice", "x", Role.PATIENT));

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFullName("Dr. Smith");
        doctor.setUser(new AppUser("dr.smith", "x", Role.DOCTOR));
    }

    @Test
    void book_succeeds_whenSlotIsFree() {
        LocalDateTime time = LocalDateTime.now().plusDays(1);
        when(patientRepository.findByUserUsername("alice")).thenReturn(Optional.of(patient));
        when(doctorRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.countConflicts(eq(1L), any(), any())).thenReturn(0L);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        AppointmentResponse res = service.book("alice", new BookAppointmentRequest(1L, time, "Chest pain"));

        assertEquals(AppointmentStatus.PENDING, res.status());
        assertEquals("Dr. Smith", res.doctorName());
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void book_fails_whenDoctorHasConflict() {
        LocalDateTime time = LocalDateTime.now().plusDays(1);
        when(patientRepository.findByUserUsername("alice")).thenReturn(Optional.of(patient));
        when(doctorRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.countConflicts(eq(1L), any(), any())).thenReturn(1L);

        assertThrows(BusinessException.class,
                () -> service.book("alice", new BookAppointmentRequest(1L, time, "Checkup")));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void book_fails_whenTimeInPast() {
        when(patientRepository.findByUserUsername("alice")).thenReturn(Optional.of(patient));
        when(doctorRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(doctor));

        assertThrows(BusinessException.class, () -> service.book("alice",
                new BookAppointmentRequest(1L, LocalDateTime.now().minusHours(1), "Late")));
    }

    @Test
    void updateStatus_rejectsOtherDoctor() {
        Appointment a = new Appointment();
        a.setId(5L);
        a.setDoctor(doctor);
        a.setPatient(patient);
        when(appointmentRepository.findById(5L)).thenReturn(Optional.of(a));

        assertThrows(AccessDeniedException.class,
                () -> service.updateStatus(5L, "dr.other", AppointmentStatus.CONFIRMED));
    }

    @Test
    void updateStatus_rejectsIllegalTransition() {
        Appointment a = new Appointment();
        a.setId(5L);
        a.setDoctor(doctor);
        a.setPatient(patient);       // status = PENDING
        when(appointmentRepository.findById(5L)).thenReturn(Optional.of(a));

        assertThrows(BusinessException.class,
                () -> service.updateStatus(5L, "dr.smith", AppointmentStatus.COMPLETED));
    }

    @Test
    void patientCanCancelOwnAppointment() {
        Appointment a = new Appointment();
        a.setId(5L);
        a.setDoctor(doctor);
        a.setPatient(patient);
        when(appointmentRepository.findById(5L)).thenReturn(Optional.of(a));

        AppointmentResponse res = service.cancel(5L, "alice");
        assertEquals(AppointmentStatus.CANCELLED, res.status());
    }
}
