package com.hospital.controller;

import com.hospital.dto.Dtos.PatientResponse;
import com.hospital.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public PatientResponse me(Authentication auth) {
        return patientService.me(auth.getName());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Page<PatientResponse> list(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return patientService.list(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public PatientResponse get(@PathVariable Long id) {
        return patientService.getById(id);
    }
}
