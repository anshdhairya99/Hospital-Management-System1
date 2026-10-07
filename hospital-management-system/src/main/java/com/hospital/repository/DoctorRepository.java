package com.hospital.repository;

import com.hospital.entity.Doctor;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /** @EntityGraph fetches the department in the same query and avoids the N+1 problem. */
    @EntityGraph(attributePaths = "department")
    List<Doctor> findBySpecializationContainingIgnoreCase(String specialization);

    @EntityGraph(attributePaths = "department")
    List<Doctor> findAllBy();

    Optional<Doctor> findByUserUsername(String username);

    /** Pessimistic write lock on the doctor row: serialises concurrent bookings for the same doctor. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Doctor d where d.id = :id")
    Optional<Doctor> findByIdForUpdate(@Param("id") Long id);
}
