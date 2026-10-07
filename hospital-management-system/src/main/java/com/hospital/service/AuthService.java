package com.hospital.service;

import com.hospital.dto.Dtos.*;
import com.hospital.entity.AppUser;
import com.hospital.entity.Patient;
import com.hospital.entity.Role;
import com.hospital.exception.BusinessException;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.UserRepository;
import com.hospital.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /** Public self-registration is only for patients. Doctors are created by an admin. */
    @Transactional
    public AuthResponse registerPatient(RegisterRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new BusinessException("Username already taken");
        }
        Patient p = new Patient();
        p.setFullName(req.fullName());
        p.setAge(req.age());
        p.setGender(req.gender());
        p.setPhone(req.phone());
        p.setAddress(req.address());
        p.setBloodGroup(req.bloodGroup());
        p.setUser(new AppUser(req.username(), passwordEncoder.encode(req.password()), Role.PATIENT));
        patientRepository.save(p);   // cascade persists the user too
        return new AuthResponse(jwtService.generateToken(req.username(), Role.PATIENT.name()),
                req.username(), Role.PATIENT);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        AppUser user = userRepository.findByUsername(req.username())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return new AuthResponse(jwtService.generateToken(user.getUsername(), user.getRole().name()),
                user.getUsername(), user.getRole());
    }
}
