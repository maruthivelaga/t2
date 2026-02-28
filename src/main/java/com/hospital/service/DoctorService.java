package com.hospital.service;

import com.hospital.dto.DoctorDto;
import com.hospital.model.Doctor;
import com.hospital.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for Doctor-related business logic
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DoctorService {
    
    private final DoctorRepository doctorRepository;
    
    /**
     * Get all doctors
     * @return list of all doctors
     */
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }
    
    /**
     * Get doctor by ID
     * @param id doctor ID
     * @return Optional containing doctor if found
     */
    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }
    
    /**
     * Get all available doctors
     * @return list of available doctors
     */
    public List<Doctor> getAvailableDoctors() {
        return doctorRepository.findByAvailableTrue();
    }
    
    /**
     * Get doctors by specialty
     * @param specialtyId specialty ID
     * @return list of doctors in that specialty
     */
    public List<Doctor> getDoctorsBySpecialty(Long specialtyId) {
        return doctorRepository.findBySpecialtyId(specialtyId);
    }
    
    /**
     * Get available doctors by specialty
     * @param specialtyId specialty ID
     * @return list of available doctors in that specialty
     */
    public List<Doctor> getAvailableDoctorsBySpecialty(Long specialtyId) {
        return doctorRepository.findAvailableDoctorsBySpecialty(specialtyId);
    }
    
    /**
     * Convert Doctor entity to DoctorDto
     * @param doctor Doctor entity
     * @return DoctorDto
     */
    public DoctorDto convertToDto(Doctor doctor) {
        DoctorDto dto = new DoctorDto();
        dto.setId(doctor.getId());
        dto.setFirstName(doctor.getFirstName());
        dto.setLastName(doctor.getLastName());
        dto.setFullName(doctor.getFullName());
        dto.setEmail(doctor.getEmail());
        dto.setPhoneNumber(doctor.getPhoneNumber());
        dto.setLicenseNumber(doctor.getLicenseNumber());
        dto.setYearsOfExperience(doctor.getYearsOfExperience());
        dto.setQualifications(doctor.getQualifications());
        dto.setAvailable(doctor.isAvailable());
        
        if (doctor.getSpecialty() != null) {
            dto.setSpecialtyName(doctor.getSpecialty().getName());
            dto.setSpecialtyId(doctor.getSpecialty().getId());
        }
        
        return dto;
    }
    
    /**
     * Convert list of doctors to DTOs
     * @param doctors list of Doctor entities
     * @return list of DoctorDtos
     */
    public List<DoctorDto> convertToDtoList(List<Doctor> doctors) {
        return doctors.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
