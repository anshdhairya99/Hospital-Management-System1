package com.hospital.controller;

import com.hospital.dto.Dtos.DoctorResponse;
import com.hospital.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    /** Any logged-in user can search doctors, e.g. GET /api/doctors?specialization=cardio */
    @GetMapping
    public List<DoctorResponse> search(@RequestParam(required = false) String specialization) {
        return doctorService.search(specialization);
    }
}
