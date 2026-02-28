package com.hospital.repository;

import com.hospital.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Doctor entity
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    /**
     * Find doctor by license number
     * @param licenseNumber the license number to search for
     * @return Optional containing the doctor if found
     */
    Optional<Doctor> findByLicenseNumber(String licenseNumber);
    
    /**
     * Find all doctors by specialty ID
     * @param specialtyId the specialty ID to search for
     * @return List of doctors in that specialty
     */
    List<Doctor> findBySpecialtyId(Long specialtyId);
    
    /**
     * Find all available doctors by specialty ID
     * @param specialtyId the specialty ID to search for
     * @return List of available doctors in that specialty
     */
    @Query("SELECT d FROM Doctor d WHERE d.specialty.id = :specialtyId AND d.available = true")
    List<Doctor> findAvailableDoctorsBySpecialty(@Param("specialtyId") Long specialtyId);
    
    /**
     * Find all available doctors
     * @return List of all available doctors
     */
    List<Doctor> findByAvailableTrue();
}
