package com.hospital.controller;

import com.hospital.dto.Dtos.*;
import com.hospital.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/departments")
    public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createDepartment(req));
    }

    @GetMapping("/departments")
    public List<DepartmentResponse> departments() {
        return adminService.listDepartments();
    }

    @PostMapping("/doctors")
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody CreateDoctorRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createDoctor(req));
    }

    @GetMapping("/stats")
    public StatsResponse stats() {
        return adminService.stats();
    }
}
