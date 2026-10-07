package com.hospital.service;

import com.hospital.dto.Dtos.PatientResponse;
import com.hospital.dto.Mapper;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    @Transactional(readOnly = true)
    public PatientResponse me(String username) {
        return patientRepository.findByUserUsername(username).map(Mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));
    }

    @Transactional(readOnly = true)
    public PatientResponse getById(Long id) {
        return patientRepository.findById(id).map(Mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<PatientResponse> list(Pageable pageable) {
        return patientRepository.findAll(pageable).map(Mapper::toResponse);
    }
}
