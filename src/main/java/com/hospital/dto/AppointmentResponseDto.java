package com.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for appointment response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDto {
    
    private Long id;
    private LocalDateTime appointmentDateTime;
    private String status;
    private String reason;
    private String notes;
    private String patientName;
    private String doctorName;
    private String specialty;
    private LocalDateTime createdAt;
}
