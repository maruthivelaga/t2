package com.hospital.config;

import com.hospital.model.*;
import com.hospital.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Data initialization component
 * Populates the database with sample data on application startup
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final SpecialtyRepository specialtyRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing sample data...");
        
        // Check if data already exists
        if (specialtyRepository.count() > 0) {
            log.info("Sample data already exists. Skipping initialization.");
            return;
        }
        
        // Create Specialties
        Specialty cardiology = createSpecialty("Cardiology", "Heart and cardiovascular system specialists");
        Specialty neurology = createSpecialty("Neurology", "Brain and nervous system specialists");
        Specialty pediatrics = createSpecialty("Pediatrics", "Child healthcare specialists");
        Specialty orthopedics = createSpecialty("Orthopedics", "Bone and joint specialists");
        Specialty dermatology = createSpecialty("Dermatology", "Skin condition specialists");
        
        log.info("Created {} specialties", specialtyRepository.count());
        
        // Create Doctors
        createDoctor("Sarah", "Johnson", "sarah.johnson@hospital.com", "1234567890", 
                "LIC001", 15, "MD, FACC - Harvard Medical School", cardiology);
        createDoctor("Michael", "Chen", "michael.chen@hospital.com", "1234567891", 
                "LIC002", 12, "MD, PhD - Johns Hopkins University", neurology);
        createDoctor("Emily", "Williams", "emily.williams@hospital.com", "1234567892", 
                "LIC003", 8, "MD, FAAP - Stanford Medical School", pediatrics);
        createDoctor("David", "Brown", "david.brown@hospital.com", "1234567893", 
                "LIC004", 20, "MD, FAAOS - Yale School of Medicine", orthopedics);
        createDoctor("Lisa", "Martinez", "lisa.martinez@hospital.com", "1234567894", 
                "LIC005", 10, "MD, FAAD - Mayo Clinic", dermatology);
        createDoctor("James", "Anderson", "james.anderson@hospital.com", "1234567895", 
                "LIC006", 18, "MD, FACC - Columbia University", cardiology);
        
        log.info("Created {} doctors", doctorRepository.count());
        
        // Create sample patient user
        User patientUser = new User();
        patientUser.setUsername("patient1");
        patientUser.setEmail("patient1@example.com");
        patientUser.setPassword(passwordEncoder.encode("password123"));
        patientUser.setEnabled(true);
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_PATIENT");
        patientUser.setRoles(roles);
        
        Patient patient = new Patient();
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setDateOfBirth(LocalDate.of(1990, 5, 15));
        patient.setGender("Male");
        patient.setPhoneNumber("9876543210");
        patient.setAddress("123 Main Street, City, State 12345");
        patient.setUser(patientUser);
        
        patientUser.setPatient(patient);
        userRepository.save(patientUser);
        
        log.info("Created sample patient: username='patient1', password='password123'");
        
        // Create sample appointments with various statuses and fees
        createSampleAppointments();
        
        log.info("=== Sample Data Initialized Successfully ===");
        log.info("Login with: username='patient1', password='password123'");
    }
    
    private Specialty createSpecialty(String name, String description) {
        Specialty specialty = new Specialty();
        specialty.setName(name);
        specialty.setDescription(description);
        return specialtyRepository.save(specialty);
    }
    
    private Doctor createDoctor(String firstName, String lastName, String email, 
                               String phone, String license, int experience, 
                               String qualifications, Specialty specialty) {
        Doctor doctor = new Doctor();
        doctor.setFirstName(firstName);
        doctor.setLastName(lastName);
        doctor.setEmail(email);
        doctor.setPhoneNumber(phone);
        doctor.setLicenseNumber(license);
        doctor.setYearsOfExperience(experience);
        doctor.setQualifications(qualifications);
        doctor.setAvailable(true);
        doctor.setSpecialty(specialty);
        return doctorRepository.save(doctor);
    }
    
    private void createSampleAppointments() {
        Patient patient = patientRepository.findAll().get(0);
        java.util.List<Doctor> doctors = doctorRepository.findAll();
        
        if (patient == null || doctors.isEmpty()) {
            log.warn("Cannot create sample appointments: no patient or doctors found");
            return;
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // Create appointments with different statuses and fees
        // Appointment 1 - Recent, Scheduled
        createAppointment(patient, doctors.get(0), now.plusDays(5), 
                Appointment.AppointmentStatus.SCHEDULED, "Regular checkup", 500.0, now.minusDays(1));
        
        // Appointment 2 - Recent, Confirmed
        createAppointment(patient, doctors.get(1), now.plusDays(3), 
                Appointment.AppointmentStatus.CONFIRMED, "Follow-up consultation", 800.0, now.minusDays(1));
        
        // Appointment 3 - Few days old, Completed
        createAppointment(patient, doctors.get(2), now.minusDays(2), 
                Appointment.AppointmentStatus.COMPLETED, "Annual physical exam", 1200.0, now.minusDays(5));
        
        // Appointment 4 - Week old, Scheduled
        createAppointment(patient, doctors.get(3), now.plusDays(7), 
                Appointment.AppointmentStatus.SCHEDULED, "Specialist consultation", 1500.0, now.minusDays(6));
        
        // Appointment 5 - Week old, Cancelled
        createAppointment(patient, doctors.get(4), now.plusDays(2), 
                Appointment.AppointmentStatus.CANCELLED, "Routine checkup", 600.0, now.minusDays(7));
        
        // Appointment 6 - Old, Completed
        createAppointment(patient, doctors.get(0), now.minusDays(20), 
                Appointment.AppointmentStatus.COMPLETED, "Emergency consultation", 2000.0, now.minusDays(25));
        
        // Appointment 7 - Old, No Show
        createAppointment(patient, doctors.get(1), now.minusDays(18), 
                Appointment.AppointmentStatus.NO_SHOW, "Missed appointment", 700.0, now.minusDays(20));
        
        // Appointment 8 - Two weeks old, Completed
        createAppointment(patient, doctors.get(2), now.minusDays(10), 
                Appointment.AppointmentStatus.COMPLETED, "Blood test review", 400.0, now.minusDays(12));
        
        // Appointment 9 - Recent, Confirmed
        createAppointment(patient, doctors.get(3), now.plusDays(10), 
                Appointment.AppointmentStatus.CONFIRMED, "Surgery consultation", 2500.0, now.minusDays(2));
        
        // Appointment 10 - Recent, Scheduled
        createAppointment(patient, doctors.get(4), now.plusDays(1), 
                Appointment.AppointmentStatus.SCHEDULED, "Skin examination", 900.0, now.minusDays(1));
        
        // Appointment 11 - Old, Completed
        createAppointment(patient, doctors.get(5), now.minusDays(30), 
                Appointment.AppointmentStatus.COMPLETED, "Cardiac evaluation", 1800.0, now.minusDays(35));
        
        // Appointment 12 - Recent, Scheduled
        createAppointment(patient, doctors.get(0), now.plusDays(14), 
                Appointment.AppointmentStatus.SCHEDULED, "Follow-up visit", 550.0, now.minusHours(12));
        
        // Appointment 13 - Week old, Completed
        createAppointment(patient, doctors.get(1), now.minusDays(8), 
                Appointment.AppointmentStatus.COMPLETED, "Neurological exam", 1100.0, now.minusDays(10));
        
        // Appointment 14 - Two weeks old, Cancelled
        createAppointment(patient, doctors.get(2), now.minusDays(5), 
                Appointment.AppointmentStatus.CANCELLED, "Vaccination", 300.0, now.minusDays(13));
        
        // Appointment 15 - Very recent
        createAppointment(patient, doctors.get(3), now.plusDays(20), 
                Appointment.AppointmentStatus.CONFIRMED, "Orthopedic assessment", 1600.0, now.minusHours(6));
        
        log.info("Created {} sample appointments with consultation fees", appointmentRepository.count());
    }
    
    private void createAppointment(Patient patient, Doctor doctor, LocalDateTime appointmentDateTime,
                                  Appointment.AppointmentStatus status, String reason, 
                                  Double consultationFee, LocalDateTime createdAt) {
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDateTime(appointmentDateTime);
        appointment.setStatus(status);
        appointment.setReason(reason);
        appointment.setConsultationFee(consultationFee);
        appointment.setCreatedAt(createdAt);
        appointment.setUpdatedAt(createdAt);
        appointmentRepository.save(appointment);
    }
}
