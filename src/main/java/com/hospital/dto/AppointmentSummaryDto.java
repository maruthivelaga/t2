package com.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for appointment summary in top records
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentSummaryDto {
    private Long id;
    private LocalDateTime appointmentDateTime;
    private String status;
    private String patientName;
    private String doctorName;
    private Double consultationFee;
    private LocalDateTime createdAt;
    private Long daysOld;
}
