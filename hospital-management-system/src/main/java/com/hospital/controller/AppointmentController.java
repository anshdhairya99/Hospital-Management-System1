package com.hospital.controller;

import com.hospital.dto.Dtos.AppointmentResponse;
import com.hospital.dto.Dtos.BookAppointmentRequest;
import com.hospital.entity.AppointmentStatus;
import com.hospital.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentResponse> book(@Valid @RequestBody BookAppointmentRequest req,
                                                    Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.book(auth.getName(), req));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR')")
    public Page<AppointmentResponse> mine(Authentication auth,
                                          @PageableDefault(size = 10, sort = "appointmentTime") Pageable pageable) {
        boolean doctor = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));
        return doctor ? appointmentService.forDoctor(auth.getName(), pageable)
                      : appointmentService.forPatient(auth.getName(), pageable);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<AppointmentResponse> all(@PageableDefault(size = 10, sort = "appointmentTime") Pageable pageable) {
        return appointmentService.all(pageable);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('DOCTOR')")
    public AppointmentResponse updateStatus(@PathVariable Long id, @RequestParam AppointmentStatus status,
                                            Authentication auth) {
        return appointmentService.updateStatus(id, auth.getName(), status);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PATIENT')")
    public AppointmentResponse cancel(@PathVariable Long id, Authentication auth) {
        return appointmentService.cancel(id, auth.getName());
    }
}
