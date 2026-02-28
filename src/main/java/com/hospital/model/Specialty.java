package com.hospital.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Specialty entity representing medical specialties
 * e.g., Cardiology, Neurology, Pediatrics, etc.
 */
@Entity
@Table(name = "specialties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Specialty {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Specialty name is required")
    @Column(unique = true, nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @OneToMany(mappedBy = "specialty", cascade = CascadeType.ALL)
    private List<Doctor> doctors = new ArrayList<>();
}
