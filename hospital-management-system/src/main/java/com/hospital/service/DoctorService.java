package com.hospital.service;

import com.hospital.dto.Dtos.DoctorResponse;
import com.hospital.dto.Mapper;
import com.hospital.entity.Doctor;
import com.hospital.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    @Transactional(readOnly = true)
    public List<DoctorResponse> search(String specialization) {
        List<Doctor> doctors = (specialization == null || specialization.isBlank())
                ? doctorRepository.findAllBy()
                : doctorRepository.findBySpecializationContainingIgnoreCase(specialization);
        return doctors.stream().map(Mapper::toResponse).toList();
    }
}
