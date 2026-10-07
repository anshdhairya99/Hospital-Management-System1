package com.hospital.config;

import com.hospital.entity.*;
import com.hospital.repository.DepartmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/** Seeds an admin account, a few departments and one demo doctor on first start. */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder encoder;

    @Value("${app.admin.username}") private String adminUsername;
    @Value("${app.admin.password}") private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        if (!userRepository.existsByUsername(adminUsername)) {
            userRepository.save(new AppUser(adminUsername, encoder.encode(adminPassword), Role.ADMIN));
        }
        for (String name : List.of("Cardiology", "Neurology", "Orthopedics", "Pediatrics")) {
            if (!departmentRepository.existsByNameIgnoreCase(name)) {
                departmentRepository.save(new Department(name));
            }
        }
        if (!userRepository.existsByUsername("dr.smith")) {
            Doctor d = new Doctor();
            d.setFullName("Dr. John Smith");
            d.setSpecialization("Cardiologist");
            d.setPhone("9876543210");
            d.setConsultationFee(new BigDecimal("500.00"));
            d.setDepartment(departmentRepository.findByName("Cardiology").orElseThrow());
            d.setUser(new AppUser("dr.smith", encoder.encode("doctor123"), Role.DOCTOR));
            doctorRepository.save(d);
        }
    }
}
