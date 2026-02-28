package com.hospital.repository;

import com.hospital.model.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Specialty entity
 */
@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    
    /**
     * Find specialty by name
     * @param name the specialty name to search for
     * @return Optional containing the specialty if found
     */
    Optional<Specialty> findByName(String name);
    
    /**
     * Check if specialty name exists
     * @param name the specialty name to check
     * @return true if specialty exists
     */
    boolean existsByName(String name);
}
