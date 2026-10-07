package com.hospital.repository;

import com.hospital.entity.Appointment;
import com.hospital.entity.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    interface StatusCount {
        AppointmentStatus getStatus();
        Long getTotal();
    }

    @Override
    @EntityGraph(attributePaths = {"doctor", "patient"})
    Page<Appointment> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"doctor", "patient"})
    Page<Appointment> findByPatientId(Long patientId, Pageable pageable);

    @EntityGraph(attributePaths = {"doctor", "patient"})
    Page<Appointment> findByDoctorId(Long doctorId, Pageable pageable);

    /** Counts non-cancelled appointments of a doctor inside the (start, end) window = slot conflict check. */
    @Query("""
           select count(a) from Appointment a
           where a.doctor.id = :doctorId
             and a.status <> com.hospital.entity.AppointmentStatus.CANCELLED
             and a.appointmentTime > :start and a.appointmentTime < :end
           """)
    long countConflicts(@Param("doctorId") Long doctorId,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

    @Query("select a.status as status, count(a) as total from Appointment a group by a.status")
    List<StatusCount> countByStatus();
}
