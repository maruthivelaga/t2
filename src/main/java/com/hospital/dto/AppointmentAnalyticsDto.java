package com.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for comprehensive appointment analytics report
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentAnalyticsDto {
    
    // Basic statistics
    private Long totalRecordCount;
    private LocalDateTime oldestRecordTimestamp;
    private LocalDateTime latestRecordTimestamp;
    private Long activeAppointmentsCount;    // SCHEDULED, CONFIRMED
    private Long inactiveAppointmentsCount;  // CANCELLED, COMPLETED, NO_SHOW
    
    // Distribution by status
    private List<CategoryDistributionDto> statusDistribution;
    
    // Distribution by doctor
    private List<CategoryDistributionDto> doctorDistribution;
    
    // Numeric aggregates (consultation fee)
    private NumericAggregatesDto feeAggregates;
    
    // Ageing analysis
    private List<AgeingBucketDto> ageingAnalysis;
    
    // Top records
    private List<AppointmentSummaryDto> top5NewestRecords;
    private List<AppointmentSummaryDto> top5HighestFeeRecords;
}
