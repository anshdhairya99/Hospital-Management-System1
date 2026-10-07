package com.hospital.controller;

import com.hospital.dto.Dtos.PrescriptionRequest;
import com.hospital.dto.Dtos.PrescriptionResponse;
import com.hospital.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<PrescriptionResponse> create(@Valid @RequestBody PrescriptionRequest req,
                                                       Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prescriptionService.create(auth.getName(), req));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('PATIENT')")
    public List<PrescriptionResponse> mine(Authentication auth) {
        return prescriptionService.forPatient(auth.getName());
    }

    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyRole('DOCTOR','PATIENT')")
    public PrescriptionResponse byAppointment(@PathVariable Long appointmentId, Authentication auth) {
        return prescriptionService.byAppointment(appointmentId, auth.getName());
    }
}
