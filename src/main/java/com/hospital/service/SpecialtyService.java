package com.hospital.service;

import com.hospital.model.Specialty;
import com.hospital.repository.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Specialty-related business logic
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SpecialtyService {
    
    private final SpecialtyRepository specialtyRepository;
    
    /**
     * Get all specialties
     * @return list of all specialties
     */
    public List<Specialty> getAllSpecialties() {
        return specialtyRepository.findAll();
    }
    
    /**
     * Get specialty by ID
     * @param id specialty ID
     * @return Optional containing specialty if found
     */
    public Optional<Specialty> getSpecialtyById(Long id) {
        return specialtyRepository.findById(id);
    }
    
    /**
     * Get specialty by name
     * @param name specialty name
     * @return Optional containing specialty if found
     */
    public Optional<Specialty> getSpecialtyByName(String name) {
        return specialtyRepository.findByName(name);
    }
}
