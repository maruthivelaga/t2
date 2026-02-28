package com.hospital.repository;

import com.hospital.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Appointment entity
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    /**
     * Find all appointments for a patient
     * @param patientId the patient ID
     * @return List of appointments
     */
    List<Appointment> findByPatientId(Long patientId);
    
    /**
     * Find all appointments for a doctor
     * @param doctorId the doctor ID
     * @return List of appointments
     */
    List<Appointment> findByDoctorId(Long doctorId);
    
    /**
     * Find appointments by status
     * @param status the appointment status
     * @return List of appointments with the given status
     */
    List<Appointment> findByStatus(Appointment.AppointmentStatus status);
    
    /**
     * Find all appointments for a patient by status
     * @param patientId the patient ID
     * @param status the appointment status
     * @return List of appointments
     */
    List<Appointment> findByPatientIdAndStatus(Long patientId, Appointment.AppointmentStatus status);
    
    /**
     * Find appointments for a doctor on a specific date
     * @param doctorId the doctor ID
     * @param startDateTime start of the date range
     * @param endDateTime end of the date range
     * @return List of appointments
     */
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentDateTime BETWEEN :startDateTime AND :endDateTime " +
           "AND a.status != 'CANCELLED'")
    List<Appointment> findDoctorAppointmentsByDateRange(
            @Param("doctorId") Long doctorId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
    
    /**
     * Find upcoming appointments for a patient
     * @param patientId the patient ID
     * @param currentDateTime current date and time
     * @return List of upcoming appointments
     */
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId " +
           "AND a.appointmentDateTime > :currentDateTime " +
           "AND a.status IN ('SCHEDULED', 'CONFIRMED') " +
           "ORDER BY a.appointmentDateTime ASC")
    List<Appointment> findUpcomingAppointmentsByPatient(
            @Param("patientId") Long patientId,
            @Param("currentDateTime") LocalDateTime currentDateTime
    );
    
    /**
     * Check if doctor is available at a specific time
     * @param doctorId the doctor ID
     * @param appointmentDateTime the time to check
     * @return true if doctor has an appointment at that time
     */
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentDateTime = :appointmentDateTime " +
           "AND a.status != 'CANCELLED'")
    boolean existsByDoctorIdAndAppointmentDateTime(
            @Param("doctorId") Long doctorId,
            @Param("appointmentDateTime") LocalDateTime appointmentDateTime
    );
    
    // ============= Analytics Queries =============
    
    /**
     * Find appointment with oldest creation timestamp
     */
    @Query("SELECT a FROM Appointment a ORDER BY a.createdAt ASC LIMIT 1")
    Appointment findOldestAppointment();
    
    /**
     * Find appointment with latest creation timestamp
     */
    @Query("SELECT a FROM Appointment a ORDER BY a.createdAt DESC LIMIT 1")
    Appointment findLatestAppointment();
    
    /**
     * Count active appointments (SCHEDULED, CONFIRMED)
     */
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status IN ('SCHEDULED', 'CONFIRMED')")
    Long countActiveAppointments();
    
    /**
     * Count inactive appointments (CANCELLED, COMPLETED, NO_SHOW)
     */
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status IN ('CANCELLED', 'COMPLETED', 'NO_SHOW')")
    Long countInactiveAppointments();
    
    /**
     * Find top 5 newest appointments
     */
    @Query("SELECT a FROM Appointment a ORDER BY a.createdAt DESC LIMIT 5")
    List<Appointment> findTop5Newest();
    
    /**
     * Find top 5 appointments with highest consultation fee
     */
    @Query("SELECT a FROM Appointment a WHERE a.consultationFee IS NOT NULL ORDER BY a.consultationFee DESC LIMIT 5")
    List<Appointment> findTop5HighestFee();
    
    /**
     * Get numeric aggregates for consultation fee
     */
    @Query("SELECT AVG(a.consultationFee), MIN(a.consultationFee), MAX(a.consultationFee), SUM(a.consultationFee), COUNT(a) " +
           "FROM Appointment a WHERE a.consultationFee IS NOT NULL")
    List<Object[]> getConsultationFeeAggregates();
    
    /**
     * Count appointments created within date range
     */
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    Long countAppointmentsCreatedBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
