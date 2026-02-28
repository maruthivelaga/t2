package com.hospital.service;

import com.hospital.dto.AppointmentRequestDto;
import com.hospital.dto.AppointmentResponseDto;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for Appointment-related business logic
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    
    /**
     * Book a new appointment
     * @param requestDto appointment request data
     * @param username patient username
     * @return created Appointment entity
     * @throws RuntimeException if validation fails
     */
    public Appointment bookAppointment(AppointmentRequestDto requestDto, String username) {
        log.info("Booking appointment for patient: {}", username);
        
        // Get patient
        Patient patient = patientRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        // Get doctor
        Doctor doctor = doctorRepository.findById(requestDto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        // Validate doctor is available
        if (!doctor.isAvailable()) {
            throw new RuntimeException("Doctor is not available");
        }
        
        // Validate appointment is in the future
        if (requestDto.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot book appointments in the past");
        }
        
        // Check if doctor already has an appointment at this time
        if (appointmentRepository.existsByDoctorIdAndAppointmentDateTime(
                requestDto.getDoctorId(), 
                requestDto.getAppointmentDateTime())) {
            throw new RuntimeException("Doctor is not available at this time");
        }
        
        // Create appointment
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDateTime(requestDto.getAppointmentDateTime());
        appointment.setReason(requestDto.getReason());
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment booked successfully with ID: {}", savedAppointment.getId());
        
        return savedAppointment;
    }
    
    /**
     * Cancel an appointment
     * @param appointmentId appointment ID
     * @param username patient username
     * @throws RuntimeException if appointment not found or unauthorized
     */
    public void cancelAppointment(Long appointmentId, String username) {
        log.info("Cancelling appointment ID: {} for patient: {}", appointmentId, username);
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        // Verify the appointment belongs to the patient
        if (!appointment.getPatient().getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized to cancel this appointment");
        }
        
        // Check if appointment can be cancelled
        if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Appointment is already cancelled");
        }
        
        if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel completed appointment");
        }
        
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
        
        log.info("Appointment cancelled successfully");
    }
    
    /**
     * Get all appointments for a patient
     * @param username patient username
     * @return list of appointments
     */
    @Transactional(readOnly = true)
    public List<Appointment> getPatientAppointments(String username) {
        Patient patient = patientRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        return appointmentRepository.findByPatientId(patient.getId());
    }
    
    /**
     * Get upcoming appointments for a patient
     * @param username patient username
     * @return list of upcoming appointments
     */
    @Transactional(readOnly = true)
    public List<Appointment> getUpcomingAppointments(String username) {
        Patient patient = patientRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        return appointmentRepository.findUpcomingAppointmentsByPatient(
                patient.getId(), 
                LocalDateTime.now()
        );
    }
    
    /**
     * Get appointment by ID
     * @param id appointment ID
     * @return Optional containing appointment if found
     */
    @Transactional(readOnly = true)
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }
    
    /**
     * Convert Appointment entity to AppointmentResponseDto
     * @param appointment Appointment entity
     * @return AppointmentResponseDto
     */
    public AppointmentResponseDto convertToDto(Appointment appointment) {
        AppointmentResponseDto dto = new AppointmentResponseDto();
        dto.setId(appointment.getId());
        dto.setAppointmentDateTime(appointment.getAppointmentDateTime());
        dto.setStatus(appointment.getStatus().name());
        dto.setReason(appointment.getReason());
        dto.setNotes(appointment.getNotes());
        dto.setPatientName(appointment.getPatient().getFullName());
        dto.setDoctorName(appointment.getDoctor().getFullName());
        dto.setSpecialty(appointment.getDoctor().getSpecialty() != null ? 
                appointment.getDoctor().getSpecialty().getName() : "N/A");
        dto.setCreatedAt(appointment.getCreatedAt());
        
        return dto;
    }
    
    /**
     * Convert list of appointments to DTOs
     * @param appointments list of Appointment entities
     * @return list of AppointmentResponseDtos
     */
    public List<AppointmentResponseDto> convertToDtoList(List<Appointment> appointments) {
        return appointments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
