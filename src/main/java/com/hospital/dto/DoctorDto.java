package com.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for doctor information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDto {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String licenseNumber;
    private Integer yearsOfExperience;
    private String qualifications;
    private boolean available;
    private String specialtyName;
    private Long specialtyId;
}
