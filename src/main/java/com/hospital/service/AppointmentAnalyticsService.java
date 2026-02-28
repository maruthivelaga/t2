package com.hospital.service;

import com.hospital.dto.*;
import com.hospital.model.Appointment;
import com.hospital.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating comprehensive appointment analytics reports
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentAnalyticsService {
    
    private final AppointmentRepository appointmentRepository;
    
    /**
     * Generate comprehensive analytics report for all appointments
     * @return Complete analytics report
     */
    public AppointmentAnalyticsDto generateAnalyticsReport() {
        AppointmentAnalyticsDto analytics = new AppointmentAnalyticsDto();
        
        // 1. Basic Statistics
        analytics.setTotalRecordCount(appointmentRepository.count());
        
        Appointment oldest = appointmentRepository.findOldestAppointment();
        Appointment latest = appointmentRepository.findLatestAppointment();
        
        analytics.setOldestRecordTimestamp(oldest != null ? oldest.getCreatedAt() : null);
        analytics.setLatestRecordTimestamp(latest != null ? latest.getCreatedAt() : null);
        
        analytics.setActiveAppointmentsCount(appointmentRepository.countActiveAppointments());
        analytics.setInactiveAppointmentsCount(appointmentRepository.countInactiveAppointments());
        
        // 2. Distribution by Status
        analytics.setStatusDistribution(generateStatusDistribution());
        
        // 3. Distribution by Doctor
        analytics.setDoctorDistribution(generateDoctorDistribution());
        
        // 4. Numeric Aggregates (Consultation Fee)
        analytics.setFeeAggregates(generateFeeAggregates());
        
        // 5. Ageing Analysis
        analytics.setAgeingAnalysis(generateAgeingAnalysis());
        
        // 6. Top Records
        analytics.setTop5NewestRecords(getTop5NewestRecords());
        analytics.setTop5HighestFeeRecords(getTop5HighestFeeRecords());
        
        return analytics;
    }
    
    /**
     * Generate distribution report by status
     */
    private List<CategoryDistributionDto> generateStatusDistribution() {
        List<Appointment> allAppointments = appointmentRepository.findAll();
        long total = allAppointments.size();
        
        Map<Appointment.AppointmentStatus, Long> statusCounts = allAppointments.stream()
                .collect(Collectors.groupingBy(Appointment::getStatus, Collectors.counting()));
        
        return statusCounts.entrySet().stream()
                .map(entry -> new CategoryDistributionDto(
                        entry.getKey().toString(),
                        entry.getValue(),
                        total > 0 ? (entry.getValue() * 100.0 / total) : 0.0
                ))
                .sorted(Comparator.comparing(CategoryDistributionDto::getCount).reversed())
                .collect(Collectors.toList());
    }
    
    /**
     * Generate distribution report by doctor
     */
    private List<CategoryDistributionDto> generateDoctorDistribution() {
        List<Appointment> allAppointments = appointmentRepository.findAll();
        long total = allAppointments.size();
        
        Map<String, Long> doctorCounts = allAppointments.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getDoctor().getFullName(),
                        Collectors.counting()
                ));
        
        return doctorCounts.entrySet().stream()
                .map(entry -> new CategoryDistributionDto(
                        entry.getKey(),
                        entry.getValue(),
                        total > 0 ? (entry.getValue() * 100.0 / total) : 0.0
                ))
                .sorted(Comparator.comparing(CategoryDistributionDto::getCount).reversed())
                .collect(Collectors.toList());
    }
    
    /**
     * Generate numeric aggregates for consultation fee
     */
    private NumericAggregatesDto generateFeeAggregates() {
        List<Object[]> result = appointmentRepository.getConsultationFeeAggregates();
        
        if (result == null || result.isEmpty() || result.get(0) == null) {
            return new NumericAggregatesDto(0.0, 0.0, 0.0, 0.0, 0L);
        }
        
        Object[] aggregates = result.get(0);
        
        Double avg = aggregates[0] != null ? ((Number) aggregates[0]).doubleValue() : 0.0;
        Double min = aggregates[1] != null ? ((Number) aggregates[1]).doubleValue() : 0.0;
        Double max = aggregates[2] != null ? ((Number) aggregates[2]).doubleValue() : 0.0;
        Double sum = aggregates[3] != null ? ((Number) aggregates[3]).doubleValue() : 0.0;
        Long count = aggregates[4] != null ? ((Number) aggregates[4]).longValue() : 0L;
        
        return new NumericAggregatesDto(avg, min, max, sum, count);
    }
    
    /**
     * Generate ageing analysis - bucket records by time since creation
     * Buckets: 0-2 days, 3-7 days, 8-14 days, 14+ days
     */
    private List<AgeingBucketDto> generateAgeingAnalysis() {
        List<Appointment> allAppointments = appointmentRepository.findAll();
        long total = allAppointments.size();
        LocalDateTime now = LocalDateTime.now();
        
        Map<String, Long> ageingBuckets = new LinkedHashMap<>();
        ageingBuckets.put("0-2 days", 0L);
        ageingBuckets.put("3-7 days", 0L);
        ageingBuckets.put("8-14 days", 0L);
        ageingBuckets.put("14+ days", 0L);
        
        for (Appointment appointment : allAppointments) {
            if (appointment.getCreatedAt() != null) {
                long daysOld = ChronoUnit.DAYS.between(appointment.getCreatedAt(), now);
                
                if (daysOld <= 2) {
                    ageingBuckets.merge("0-2 days", 1L, Long::sum);
                } else if (daysOld <= 7) {
                    ageingBuckets.merge("3-7 days", 1L, Long::sum);
                } else if (daysOld <= 14) {
                    ageingBuckets.merge("8-14 days", 1L, Long::sum);
                } else {
                    ageingBuckets.merge("14+ days", 1L, Long::sum);
                }
            }
        }
        
        return ageingBuckets.entrySet().stream()
                .map(entry -> new AgeingBucketDto(
                        entry.getKey(),
                        entry.getValue(),
                        total > 0 ? (entry.getValue() * 100.0 / total) : 0.0
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * Get top 5 newest appointments
     */
    private List<AppointmentSummaryDto> getTop5NewestRecords() {
        List<Appointment> newest = appointmentRepository.findTop5Newest();
        LocalDateTime now = LocalDateTime.now();
        
        return newest.stream()
                .map(a -> {
                    Long daysOld = a.getCreatedAt() != null ? 
                            ChronoUnit.DAYS.between(a.getCreatedAt(), now) : null;
                    
                    return new AppointmentSummaryDto(
                            a.getId(),
                            a.getAppointmentDateTime(),
                            a.getStatus().toString(),
                            a.getPatient().getFullName(),
                            a.getDoctor().getFullName(),
                            a.getConsultationFee(),
                            a.getCreatedAt(),
                            daysOld
                    );
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get top 5 appointments with highest consultation fee
     */
    private List<AppointmentSummaryDto> getTop5HighestFeeRecords() {
        List<Appointment> highestFee = appointmentRepository.findTop5HighestFee();
        LocalDateTime now = LocalDateTime.now();
        
        return highestFee.stream()
                .map(a -> {
                    Long daysOld = a.getCreatedAt() != null ? 
                            ChronoUnit.DAYS.between(a.getCreatedAt(), now) : null;
                    
                    return new AppointmentSummaryDto(
                            a.getId(),
                            a.getAppointmentDateTime(),
                            a.getStatus().toString(),
                            a.getPatient().getFullName(),
                            a.getDoctor().getFullName(),
                            a.getConsultationFee(),
                            a.getCreatedAt(),
                            daysOld
                    );
                })
                .collect(Collectors.toList());
    }
}
