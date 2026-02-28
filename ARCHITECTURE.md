mvn # System Architecture Documentation
## Hospital Appointment Booking System

---

## Executive Summary

This document provides a comprehensive overview of the Hospital Appointment Booking System, a mid-sized healthcare provider solution built using **Spring Boot** framework. The system enables secure patient registration, authentication, doctor browsing by specialty, and complete appointment management capabilities.

---

## 1. System Architecture Overview

### 1.1 Architectural Pattern
The system follows a **layered architecture** with clear separation of concerns:

```
┌────────────────────────────────────────────────┐
│          PRESENTATION LAYER                    │
│   (Thymeleaf Templates + Web Controllers)     │
└────────────────────────────────────────────────┘
                    ↕
┌────────────────────────────────────────────────┐
│           SECURITY LAYER                       │
│   (Spring Security + Authentication)           │
└────────────────────────────────────────────────┘
                    ↕
┌────────────────────────────────────────────────┐
│           SERVICE LAYER                        │
│   (Business Logic + Validation)                │
└────────────────────────────────────────────────┘
                    ↕
┌────────────────────────────────────────────────┐
│           REPOSITORY LAYER                     │
│   (Data Access + Queries)                      │
└────────────────────────────────────────────────┘
                    ↕
┌────────────────────────────────────────────────┐
│           DATA LAYER                           │
│   (JPA Entities + Database)                    │
└────────────────────────────────────────────────┘
```

### 1.2 Technology Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| Framework | Spring Boot 3.2.0 | Application backbone |
| Web | Spring MVC | Request handling |
| Security | Spring Security 6 | Authentication & Authorization |
| Persistence | Spring Data JPA | Data access |
| ORM | Hibernate | Object-relational mapping |
| View | Thymeleaf | Server-side templating |
| Database | H2 / MySQL | Data storage |
| Build | Maven | Dependency management |

---

## 2. Component Details

### 2.1 Model Layer (Entities)

#### **User Entity**
```java
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Email @Column(unique = true)
    private String email;
    
    private String password; // BCrypt encrypted
    
    @ElementCollection
    private Set<String> roles; // ROLE_PATIENT, etc.
    
    @OneToOne(mappedBy = "user")
    private Patient patient;
}
```

**Key Annotations:**
- `@Entity`: Marks as JPA entity
- `@Table`: Specifies table name
- `@Column`: Column constraints
- `@ElementCollection`: For Set<String> roles
- `@OneToOne`: Bidirectional relationship

#### **Patient Entity**
```java
@Entity
@Table(name = "patients")
public class Patient {
    @Id @GeneratedValue
    private Long id;
    
    private String firstName, lastName;
    private LocalDate dateOfBirth;
    
    @Pattern(regexp = "^[0-9]{10}$")
    private String phoneNumber;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToMany(mappedBy = "patient")
    private List<Appointment> appointments;
}
```

**Relationships:**
- One-to-One with User
- One-to-Many with Appointments

#### **Doctor Entity**
```java
@Entity
@Table(name = "doctors")
public class Doctor {
    @Id @GeneratedValue
    private Long id;
    
    private String firstName, lastName;
    
    @Column(unique = true)
    private String licenseNumber;
    
    private Integer yearsOfExperience;
    private boolean available;
    
    @ManyToOne
    @JoinColumn(name = "specialty_id")
    private Specialty specialty;
    
    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments;
}
```

**Relationships:**
- Many-to-One with Specialty
- One-to-Many with Appointments

#### **Specialty Entity**
```java
@Entity
@Table(name = "specialties")
public class Specialty {
    @Id @GeneratedValue
    private Long id;
    
    @Column(unique = true)
    private String name;
    
    private String description;
    
    @OneToMany(mappedBy = "specialty")
    private List<Doctor> doctors;
}
```

#### **Appointment Entity**
```java
@Entity
@Table(name = "appointments")
public class Appointment {
    @Id @GeneratedValue
    private Long id;
    
    @NotNull
    private LocalDateTime appointmentDateTime;
    
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;
    
    private String reason, notes;
    
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    
    @PrePersist @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

**Status Enum:**
```java
enum AppointmentStatus {
    SCHEDULED, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW
}
```

### 2.2 Repository Layer

#### **UserRepository**
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
```

**Purpose:** User authentication and registration queries

#### **DoctorRepository**
```java
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findBySpecialtyId(Long specialtyId);
    
    @Query("SELECT d FROM Doctor d WHERE d.specialty.id = :specialtyId AND d.available = true")
    List<Doctor> findAvailableDoctorsBySpecialty(@Param("specialtyId") Long specialtyId);
    
    List<Doctor> findByAvailableTrue();
}
```

**Purpose:** Doctor filtering and availability queries

#### **AppointmentRepository**
```java
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId " +
           "AND a.appointmentDateTime > :currentDateTime " +
           "AND a.status IN ('SCHEDULED', 'CONFIRMED')")
    List<Appointment> findUpcomingAppointmentsByPatient(...);
    
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentDateTime = :appointmentDateTime " +
           "AND a.status != 'CANCELLED'")
    boolean existsByDoctorIdAndAppointmentDateTime(...);
}
```

**Purpose:** Complex appointment queries and conflict detection

### 2.3 Service Layer

#### **PatientService**
```java
@Service
@Transactional
public class PatientService {
    
    public Patient registerPatient(PatientRegistrationDto dto) {
        // Validate username and email uniqueness
        // Encode password with BCrypt
        // Create User entity with ROLE_PATIENT
        // Create Patient entity
        // Save with cascade
        return patient;
    }
}
```

**Key Features:**
- Transaction management
- Password encoding
- Validation logic
- Entity creation

#### **AppointmentService**
```java
@Service
@Transactional
public class AppointmentService {
    
    public Appointment bookAppointment(AppointmentRequestDto dto, String username) {
        // Validate patient exists
        // Validate doctor availability
        // Validate future date
        // Check for time slot conflicts
        // Create and save appointment
        return appointment;
    }
    
    public void cancelAppointment(Long id, String username) {
        // Verify ownership
        // Validate cancellation allowed
        // Update status to CANCELLED
    }
}
```

**Validation Rules:**
1. Doctor must be available
2. Appointment must be in the future
3. Doctor must not have another appointment at that time
4. Patient must own the appointment to cancel

### 2.4 Controller Layer

#### **RegistrationController**
```java
@Controller
public class RegistrationController {
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationDto", new PatientRegistrationDto());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerPatient(@Valid @ModelAttribute PatientRegistrationDto dto,
                                  BindingResult result) {
        if (result.hasErrors()) {
            return "register";
        }
        patientService.registerPatient(dto);
        return "redirect:/login";
    }
}
```

**Features:**
- Form binding with `@ModelAttribute`
- Validation with `@Valid`
- Error handling with `BindingResult`
- Flash messages with `RedirectAttributes`

#### **AppointmentController**
```java
@Controller
@RequestMapping("/appointments")
public class AppointmentController {
    
    @GetMapping("/book")
    public String showBookingForm(@RequestParam(required = false) Long specialtyId) {
        // Load specialties and doctors
        // Filter by specialty if provided
    }
    
    @PostMapping("/book")
    public String bookAppointment(@Valid AppointmentRequestDto dto,
                                  Authentication auth) {
        // Get current user
        // Book appointment
        // Redirect with success message
    }
    
    @PostMapping("/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id, 
                                   Authentication auth) {
        // Verify ownership
        // Cancel appointment
    }
}
```

### 2.5 Security Layer

#### **SecurityConfig**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/register", "/login").permitAll()
                .requestMatchers("/patient/**").hasRole("PATIENT")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/patient/dashboard")
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout=true")
            );
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**Security Features:**
- Role-based authorization
- Custom login page
- Session management
- CSRF protection
- Password encryption

#### **CustomUserDetailsService**
```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            mapRolesToAuthorities(user.getRoles())
        );
    }
}
```

### 2.6 View Layer (Thymeleaf)

#### **Dashboard Template**
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
<head>
    <title>Dashboard</title>
</head>
<body>
    <div th:each="appointment : ${upcomingAppointments}">
        <span th:text="${appointment.doctor.fullName}"></span>
        <span th:text="${#temporals.format(appointment.appointmentDateTime, 'dd-MM-yyyy')}"></span>
    </div>
    
    <div sec:authorize="hasRole('PATIENT')">
        Patient-only content
    </div>
</body>
</html>
```

**Thymeleaf Features:**
- Expression language: `${variable}`
- Iteration: `th:each`
- Conditionals: `th:if`
- Formatting: `${#temporals.format()}`
- Security: `sec:authorize`

---

## 3. Data Flow Diagrams

### 3.1 Patient Registration Flow

```
┌─────────────┐
│   Browser   │
└──────┬──────┘
       │ 1. GET /register
       ↓
┌─────────────────────┐
│ RegistrationController│
└──────┬──────────────┘
       │ 2. Return registration form
       ↓
┌─────────────┐
│   Browser   │ (User fills form)
└──────┬──────┘
       │ 3. POST /register
       ↓
┌─────────────────────┐
│ RegistrationController│ (@Valid validation)
└──────┬──────────────┘
       │ 4. Call service
       ↓
┌─────────────────┐
│ PatientService  │
└──────┬──────────┘
       │ 5. Validate & encode password
       ↓
┌─────────────────┐
│ UserRepository  │
└──────┬──────────┘
       │ 6. Save to database
       ↓
┌─────────────┐
│  Database   │
└─────────────┘
```

### 3.2 Appointment Booking Flow

```
┌─────────────┐
│   Patient   │
└──────┬──────┘
       │ 1. Select doctor & time
       ↓
┌───────────────────────┐
│ AppointmentController │
└──────┬────────────────┘
       │ 2. Validate request
       ↓
┌───────────────────┐
│ AppointmentService│
└──────┬────────────┘
       │ 3. Business validations:
       │    - Doctor available?
       │    - Future date?
       │    - Time slot free?
       ↓
┌────────────────────────┐
│ AppointmentRepository  │
└──────┬─────────────────┘
       │ 4. Check conflicts
       │ 5. Save appointment
       ↓
┌─────────────┐
│  Database   │
└─────────────┘
```

---

## 4. Database Schema

### 4.1 Entity Relationship Diagram

```
┌──────────┐        ┌──────────┐
│   User   │ 1────1 │  Patient │
└──────────┘        └────┬─────┘
                         │ 1
                         │
                         │ *
                    ┌────┴─────────┐
                    │ Appointment  │
                    └────┬─────────┘
                         │ *
                         │
                         │ 1
                    ┌────┴──────┐
                    │  Doctor   │
                    └────┬──────┘
                         │ *
                         │
                         │ 1
                    ┌────┴──────┐
                    │ Specialty │
                    └───────────┘
```

### 4.2 Table Structures

```sql
-- Users Table
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Patients Table
CREATE TABLE patients (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(10),
    phone_number VARCHAR(10),
    address VARCHAR(500),
    medical_history TEXT,
    user_id BIGINT UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Specialties Table
CREATE TABLE specialties (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(1000)
);

-- Doctors Table
CREATE TABLE doctors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    license_number VARCHAR(50) UNIQUE NOT NULL,
    years_of_experience INT,
    qualifications VARCHAR(1000),
    available BOOLEAN DEFAULT true,
    specialty_id BIGINT,
    FOREIGN KEY (specialty_id) REFERENCES specialties(id)
);

-- Appointments Table
CREATE TABLE appointments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    appointment_date_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    reason VARCHAR(1000),
    notes VARCHAR(2000),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);
```

---

## 5. Key Design Decisions

### 5.1 Why Layered Architecture?
- **Separation of Concerns**: Each layer has specific responsibility
- **Maintainability**: Changes in one layer don't affect others
- **Testability**: Layers can be tested independently
- **Reusability**: Services can be reused by multiple controllers

### 5.2 Why DTOs?
- **Decoupling**: Separate API contracts from database structure
- **Security**: Don't expose internal entity structure
- **Validation**: Centralized input validation
- **Flexibility**: Different views can have different DTOs

### 5.3 Security Design
- **Password Hashing**: BCrypt for secure password storage
- **Role-Based Access**: Granular permission control
- **Session Management**: Secure session handling
- **CSRF Protection**: Prevent cross-site request forgery

### 5.4 Transaction Management
- **@Transactional**: Automatic rollback on exceptions
- **Read-Only Transactions**: Performance optimization
- **Isolation Levels**: Default READ_COMMITTED

---

## 6. Deployment Considerations

### 6.1 Development Environment
- H2 in-memory database
- Auto-create schema
- SQL logging enabled
- DevTools for hot reload

### 6.2 Production Recommendations
- Switch to MySQL/PostgreSQL
- Connection pooling (HikariCP)
- Proper logging levels
- Environment-specific properties
- HTTPS enforcement
- Rate limiting
- Health monitoring

---

## 7. Conclusion

This Hospital Appointment Booking System demonstrates a well-architected Spring Boot application with:

✅ Clear separation of concerns  
✅ Proper security implementation  
✅ Comprehensive data validation  
✅ Efficient database design  
✅ User-friendly interface  
✅ Scalable architecture  

The system is production-ready with appropriate modifications for a production database and security hardening.
