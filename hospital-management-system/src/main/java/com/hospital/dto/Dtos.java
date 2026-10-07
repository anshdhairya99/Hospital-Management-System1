package com.hospital.dto;

import com.hospital.entity.AppointmentStatus;
import com.hospital.entity.Gender;
import com.hospital.entity.Role;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/** All request/response payloads as immutable records. Entities are never exposed through the API. */
public final class Dtos {

    private Dtos() {}

    // ---------- Auth ----------
    public record RegisterRequest(
            @NotBlank @Size(min = 4, max = 30) String username,
            @NotBlank @Size(min = 6, max = 60) String password,
            @NotBlank String fullName,
            @Min(0) @Max(120) int age,
            @NotNull Gender gender,
            @NotBlank @Pattern(regexp = "^[0-9+\\- ]{7,15}$", message = "invalid phone number") String phone,
            String address,
            String bloodGroup) {}

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

    public record AuthResponse(String token, String username, Role role) {}

    // ---------- Admin ----------
    public record DepartmentRequest(@NotBlank String name) {}

    public record DepartmentResponse(Long id, String name) {}

    public record CreateDoctorRequest(
            @NotBlank @Size(min = 4, max = 30) String username,
            @NotBlank @Size(min = 6, max = 60) String password,
            @NotBlank String fullName,
            @NotBlank String specialization,
            String phone,
            @NotNull @DecimalMin("0.0") BigDecimal consultationFee,
            @NotNull Long departmentId) {}

    public record StatsResponse(long doctors, long patients, long appointments,
                                Map<String, Long> appointmentsByStatus) {}

    // ---------- Domain ----------
    public record DoctorResponse(Long id, String fullName, String specialization, String phone,
                                 BigDecimal consultationFee, String department) {}

    public record PatientResponse(Long id, String fullName, int age, Gender gender, String phone,
                                  String address, String bloodGroup) {}

    public record BookAppointmentRequest(
            @NotNull Long doctorId,
            @NotNull @Future LocalDateTime appointmentTime,
            @NotBlank String reason) {}

    public record AppointmentResponse(Long id, Long doctorId, String doctorName, Long patientId,
                                      String patientName, LocalDateTime appointmentTime,
                                      AppointmentStatus status, String reason) {}

    public record PrescriptionRequest(
            @NotNull Long appointmentId,
            @NotBlank String diagnosis,
            @NotBlank String medicines,
            String notes) {}

    public record PrescriptionResponse(Long id, Long appointmentId, String doctorName, String patientName,
                                       String diagnosis, String medicines, String notes,
                                       LocalDateTime issuedAt) {}

    public record ApiError(int status, String error, String message, LocalDateTime timestamp) {}
}
