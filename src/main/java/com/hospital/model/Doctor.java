package com.hospital.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Doctor entity representing doctor information
 * Many-to-one relationship with Specialty
 * One-to-many relationship with Appointments
 */
@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @NotBlank(message = "License number is required")
    @Column(name = "license_number", unique = true, nullable = false)
    private String licenseNumber;
    
    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;
    
    @Column(length = 1000)
    private String qualifications;
    
    @Column(nullable = false)
    private boolean available = true;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id")
    private Specialty specialty;
    
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Appointment> appointments = new ArrayList<>();
    
    public String getFullName() {
        return "Dr. " + firstName + " " + lastName;
    }
}
