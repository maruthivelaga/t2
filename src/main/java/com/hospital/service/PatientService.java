package com.hospital.service;

import com.hospital.dto.PatientRegistrationDto;
import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Service layer for Patient-related business logic
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PatientService {
    
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Register a new patient
     * @param registrationDto patient registration data
     * @return created Patient entity
     * @throws RuntimeException if username or email already exists
     */
    public Patient registerPatient(PatientRegistrationDto registrationDto) {
        log.info("Registering new patient with username: {}", registrationDto.getUsername());
        
        // Check if username already exists
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Create User entity
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setEnabled(true);
        
        // Set default role
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_PATIENT");
        user.setRoles(roles);
        
        // Create Patient entity
        Patient patient = new Patient();
        patient.setFirstName(registrationDto.getFirstName());
        patient.setLastName(registrationDto.getLastName());
        patient.setDateOfBirth(registrationDto.getDateOfBirth());
        patient.setGender(registrationDto.getGender());
        patient.setPhoneNumber(registrationDto.getPhoneNumber());
        patient.setAddress(registrationDto.getAddress());
        patient.setUser(user);
        
        user.setPatient(patient);
        
        // Save user (cascade will save patient)
        userRepository.save(user);
        
        log.info("Patient registered successfully: {}", patient.getFullName());
        return patient;
    }
    
    /**
     * Get patient by ID
     * @param id patient ID
     * @return Optional containing patient if found
     */
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }
    
    /**
     * Get patient by username
     * @param username username
     * @return Optional containing patient if found
     */
    public Optional<Patient> getPatientByUsername(String username) {
        return patientRepository.findByUsername(username);
    }
    
    /**
     * Get patient by user ID
     * @param userId user ID
     * @return Optional containing patient if found
     */
    public Optional<Patient> getPatientByUserId(Long userId) {
        return patientRepository.findByUserId(userId);
    }
    
    /**
     * Update patient information
     * @param patient patient entity to update
     * @return updated patient
     */
    public Patient updatePatient(Patient patient) {
        return patientRepository.save(patient);
    }
}
