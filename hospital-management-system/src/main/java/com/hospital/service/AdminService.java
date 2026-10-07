package com.hospital.service;

import com.hospital.dto.Dtos.*;
import com.hospital.dto.Mapper;
import com.hospital.entity.*;
import com.hospital.exception.BusinessException;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest req) {
        if (departmentRepository.existsByNameIgnoreCase(req.name())) {
            throw new BusinessException("Department already exists");
        }
        Department d = departmentRepository.save(new Department(req.name()));
        return new DepartmentResponse(d.getId(), d.getName());
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> listDepartments() {
        return departmentRepository.findAll().stream()
                .map(d -> new DepartmentResponse(d.getId(), d.getName())).toList();
    }

    @Transactional
    public DoctorResponse createDoctor(CreateDoctorRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new BusinessException("Username already taken");
        }
        Department dept = departmentRepository.findById(req.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + req.departmentId()));

        Doctor d = new Doctor();
        d.setFullName(req.fullName());
        d.setSpecialization(req.specialization());
        d.setPhone(req.phone());
        d.setConsultationFee(req.consultationFee());
        d.setDepartment(dept);
        d.setUser(new AppUser(req.username(), passwordEncoder.encode(req.password()), Role.DOCTOR));
        return Mapper.toResponse(doctorRepository.save(d));
    }

    @Transactional(readOnly = true)
    public StatsResponse stats() {
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (AppointmentStatus s : AppointmentStatus.values()) byStatus.put(s.name(), 0L);
        appointmentRepository.countByStatus().forEach(r -> byStatus.put(r.getStatus().name(), r.getTotal()));
        return new StatsResponse(doctorRepository.count(), patientRepository.count(),
                appointmentRepository.count(), byStatus);
    }
}
