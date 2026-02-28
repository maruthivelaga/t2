package com.hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Hospital Appointment Booking System
 * 
 * This Spring Boot application provides a comprehensive hospital appointment
 * management system with the following features:
 * 
 * 1. Patient Registration & Authentication
 *    - Secure user registration with encrypted passwords
 *    - Spring Security integration for authentication
 *    - Role-based access control
 * 
 * 2. Doctor Management
 *    - Browse doctors by specialty
 *    - View doctor profiles and availability
 *    - Filter doctors by medical specialty
 * 
 * 3. Appointment Booking
 *    - Book appointments with available doctors
 *    - Select appointment date and time
 *    - View appointment history
 *    - Cancel appointments
 * 
 * Architecture:
 * - Model Layer: JPA entities with relationships
 * - Repository Layer: Spring Data JPA repositories
 * - Service Layer: Business logic and transaction management
 * - Controller Layer: MVC controllers for web pages
 * - View Layer: Thymeleaf templates with responsive design
 * - Security Layer: Spring Security with custom UserDetailsService
 * 
 * Database: H2 (in-memory for development), MySQL support for production
 * 
 * @author Hospital Management Team
 * @version 1.0.0
 */
@SpringBootApplication
public class HospitalAppointmentSystemApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(HospitalAppointmentSystemApplication.class, args);
        System.out.println("\n" +
                "=======================================================\n" +
                "  Hospital Appointment System Started Successfully!  \n" +
                "=======================================================\n" +
                "  Application: Hospital Appointment Booking System   \n" +
                "  URL:         http://localhost:8080                 \n" +
                "  Database:    MySQL (hospitaldb)                    \n" +
                "=======================================================\n");
    }
}
