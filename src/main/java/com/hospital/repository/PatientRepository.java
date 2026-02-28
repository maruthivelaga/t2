package com.hospital.repository;

import com.hospital.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Patient entity
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    /**
     * Find patient by user ID
     * @param userId the user ID to search for
     * @return Optional containing the patient if found
     */
    Optional<Patient> findByUserId(Long userId);
    
    /**
     * Find patient by phone number
     * @param phoneNumber the phone number to search for
     * @return Optional containing the patient if found
     */
    Optional<Patient> findByPhoneNumber(String phoneNumber);
    
    /**
     * Find patient by user's username
     * @param username the username to search for
     * @return Optional containing the patient if found
     */
    @Query("SELECT p FROM Patient p WHERE p.user.username = :username")
    Optional<Patient> findByUsername(String username);
}
