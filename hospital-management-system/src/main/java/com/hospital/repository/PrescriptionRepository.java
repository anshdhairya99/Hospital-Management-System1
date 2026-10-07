package com.hospital.repository;

import com.hospital.entity.Prescription;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    boolean existsByAppointmentId(Long appointmentId);

    Optional<Prescription> findByAppointmentId(Long appointmentId);

    @EntityGraph(attributePaths = {"appointment", "appointment.doctor", "appointment.patient"})
    List<Prescription> findByAppointmentPatientIdOrderByCreatedAtDesc(Long patientId);
}
