# Hospital Appointment Booking System

A comprehensive web-based hospital appointment management system built with **Spring Boot**, **Spring Security**, **JPA/Hibernate**, and **Thymeleaf**. This system enables patients to securely register, browse doctors by specialty, and book/manage appointments.

---

## 📋 Table of Contents

- [Features](#features)
- [System Architecture](#system-architecture)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Database Design](#database-design)
- [Setup & Installation](#setup--installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Security Configuration](#security-configuration)
- [Data Flow](#data-flow)

---

## ✨ Features

### Patient Features
- **Secure Registration**: Register with username, email, password, and personal details
- **Authentication**: Login with encrypted password (BCrypt)
- **Browse Doctors**: View available doctors filtered by medical specialty
- **View Doctor Profiles**: See detailed information about doctors including qualifications and experience
- **Book Appointments**: Schedule appointments with preferred doctors
- **Manage Appointments**: View all appointments and upcoming appointments
- **Cancel Appointments**: Cancel scheduled or confirmed appointments
- **Personal Dashboard**: View appointment summary and quick actions

### System Features
- **Role-Based Access Control**: Patient role with specific permissions
- **Session Management**: Secure session handling with Spring Security
- **Data Validation**: Server-side validation for all inputs
- **Responsive UI**: Modern, user-friendly interface with Thymeleaf
- **Appointment Conflict Prevention**: Prevents double-booking of doctors
- **Database Persistence**: JPA/Hibernate for data management

---

## 🏗 System Architecture

### Layered Architecture

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│    (Thymeleaf Views + Controllers)      │
├─────────────────────────────────────────┤
│         Security Layer                  │
│    (Spring Security + Authentication)   │
├─────────────────────────────────────────┤
│         Service Layer                   │
│    (Business Logic + Transactions)      │
├─────────────────────────────────────────┤
│         Repository Layer                │
│    (Spring Data JPA + CRUD)             │
├─────────────────────────────────────────┤
│         Data Layer                      │
│    (JPA Entities + H2/MySQL Database)   │
└─────────────────────────────────────────┘
```

### Components

#### 1. **Model Layer (Entities)**
- `User`: Authentication and user account information
- `Patient`: Patient profile and medical information
- `Doctor`: Doctor profile, qualifications, and specialty
- `Specialty`: Medical specialties (Cardiology, Neurology, etc.)
- `Appointment`: Appointment booking details and status

#### 2. **Repository Layer**
- `UserRepository`: User CRUD operations and queries
- `PatientRepository`: Patient data access
- `DoctorRepository`: Doctor queries including availability filtering
- `SpecialtyRepository`: Specialty management
- `AppointmentRepository`: Complex appointment queries and booking validation

#### 3. **Service Layer**
- `PatientService`: Patient registration and management
- `DoctorService`: Doctor browsing and filtering
- `SpecialtyService`: Specialty operations
- `AppointmentService`: Appointment booking, cancellation, and validation
- `CustomUserDetailsService`: Spring Security integration

#### 4. **Controller Layer**
- `HomeController`: Public pages (home, login)
- `RegistrationController`: Patient registration
- `PatientController`: Patient dashboard and profile
- `DoctorController`: Browse and view doctors
- `AppointmentController`: Book, view, and cancel appointments

#### 5. **View Layer**
- Thymeleaf templates with responsive CSS
- Server-side rendering with Spring Security integration
- Form validation and error handling

---

## 🛠 Technologies Used

### Backend
- **Java 17**: Core programming language
- **Spring Boot 3.2.0**: Application framework
- **Spring MVC**: Web layer
- **Spring Security 6**: Authentication and authorization
- **Spring Data JPA**: Data persistence
- **Hibernate**: ORM framework
- **Bean Validation**: Input validation

### Frontend
- **Thymeleaf**: Server-side template engine
- **HTML5/CSS3**: Modern responsive design
- **Thymeleaf Spring Security**: Security integration in views

### Database
- **H2 Database**: In-memory database (development)
- **MySQL**: Production-ready database (configured)

### Build Tool
- **Maven**: Dependency management and build automation

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/hospital/
│   │   ├── HospitalAppointmentSystemApplication.java  # Main application
│   │   ├── config/
│   │   │   └── DataInitializer.java                   # Sample data loader
│   │   ├── controller/                                # Controllers
│   │   │   ├── HomeController.java
│   │   │   ├── RegistrationController.java
│   │   │   ├── PatientController.java
│   │   │   ├── DoctorController.java
│   │   │   └── AppointmentController.java
│   │   ├── dto/                                       # Data Transfer Objects
│   │   │   ├── PatientRegistrationDto.java
│   │   │   ├── LoginDto.java
│   │   │   ├── AppointmentRequestDto.java
│   │   │   ├── AppointmentResponseDto.java
│   │   │   └── DoctorDto.java
│   │   ├── model/                                     # JPA Entities
│   │   │   ├── User.java
│   │   │   ├── Patient.java
│   │   │   ├── Doctor.java
│   │   │   ├── Specialty.java
│   │   │   └── Appointment.java
│   │   ├── repository/                                # Data Access Layer
│   │   │   ├── UserRepository.java
│   │   │   ├── PatientRepository.java
│   │   │   ├── DoctorRepository.java
│   │   │   ├── SpecialtyRepository.java
│   │   │   └── AppointmentRepository.java
│   │   ├── security/                                  # Security Configuration
│   │   │   ├── SecurityConfig.java
│   │   │   └── CustomUserDetailsService.java
│   │   └── service/                                   # Business Logic
│   │       ├── PatientService.java
│   │       ├── DoctorService.java
│   │       ├── SpecialtyService.java
│   │       └── AppointmentService.java
│   └── resources/
│       ├── application.properties                     # Configuration
│       └── templates/                                 # Thymeleaf Views
│           ├── home.html
│           ├── login.html
│           ├── register.html
│           ├── access-denied.html
│           ├── patient/
│           │   ├── dashboard.html
│           │   └── profile.html
│           ├── appointments/
│           │   ├── list.html
│           │   ├── book.html
│           │   └── details.html
│           └── doctors/
│               ├── browse.html
│               └── details.html
└── pom.xml                                            # Maven dependencies
```

---

## 🗄 Database Design

### Entity Relationships

```
User (1) ←→ (1) Patient
Patient (1) ←→ (*) Appointment
Doctor (1) ←→ (*) Appointment
Specialty (1) ←→ (*) Doctor
```

### Key Entities

#### User
- `id` (PK)
- `username` (unique)
- `email` (unique)
- `password` (encrypted)
- `roles` (Set<String>)
- `enabled` (boolean)

#### Patient
- `id` (PK)
- `firstName`, `lastName`
- `dateOfBirth`, `gender`
- `phoneNumber`, `address`
- `medicalHistory`
- `user_id` (FK → User)

#### Doctor
- `id` (PK)
- `firstName`, `lastName`
- `email`, `phoneNumber`
- `licenseNumber` (unique)
- `yearsOfExperience`
- `qualifications`
- `available` (boolean)
- `specialty_id` (FK → Specialty)

#### Specialty
- `id` (PK)
- `name` (unique)
- `description`

#### Appointment
- `id` (PK)
- `appointmentDateTime`
- `status` (ENUM)
- `reason`, `notes`
- `patient_id` (FK → Patient)
- `doctor_id` (FK → Doctor)

---

## 🚀 Setup & Installation

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Installation Steps

1. **Clone or extract the project**
   ```bash
   cd t2
   ```

2. **Configure Database** (Optional - uses H2 by default)
   
   For MySQL, edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/hospitaldb
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
   ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - Application: http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console
     - JDBC URL: `jdbc:h2:mem:hospitaldb`
     - Username: `sa`
     - Password: (leave empty)

---

## 📖 Usage

### Sample Credentials

The application comes with pre-loaded sample data:

**Patient Account:**
- Username: `patient1`
- Password: `password123`

**Available Doctors:**
- Dr. Sarah Johnson (Cardiology)
- Dr. Michael Chen (Neurology)
- Dr. Emily Williams (Pediatrics)
- Dr. David Brown (Orthopedics)
- Dr. Lisa Martinez (Dermatology)
- Dr. James Anderson (Cardiology)

### User Flow

1. **Register** as a new patient at `/register`
2. **Login** with your credentials at `/login`
3. **Browse Doctors** by specialty at `/doctors`
4. **View Doctor Details** and availability
5. **Book Appointment** with preferred date/time
6. **View Dashboard** to see upcoming appointments
7. **Manage Appointments** - view details or cancel

---

## 🔒 Security Configuration

### Authentication
- **Password Encoding**: BCrypt with strength 10
- **Session Management**: Cookie-based sessions
- **CSRF Protection**: Enabled for all POST requests

### Authorization Rules

| URL Pattern | Access |
|------------|--------|
| `/`, `/home`, `/register`, `/login` | Public |
| `/patient/**`, `/appointments/**` | ROLE_PATIENT |
| `/doctors` | Authenticated users |
| `/h2-console/**` | Development only |

### Security Features
- Password strength validation (min 6 characters)
- Username uniqueness check
- Email validation
- XSS protection via Thymeleaf escaping
- Session timeout handling

---

## 🔄 Data Flow

### Patient Registration Flow
```
User Input → RegistrationController
    ↓
Validation (Bean Validation)
    ↓
PatientService.registerPatient()
    ↓
Password Encoding (BCrypt)
    ↓
Create User + Patient entities
    ↓
UserRepository.save() (cascade to Patient)
    ↓
Redirect to Login
```

### Appointment Booking Flow
```
Patient selects Doctor & DateTime
    ↓
AppointmentController.bookAppointment()
    ↓
AppointmentService.bookAppointment()
    ↓
Validations:
  - Doctor availability
  - Future date check
  - Time slot availability
    ↓
Create Appointment entity
    ↓
AppointmentRepository.save()
    ↓
Redirect to Appointments List
```

### Browse Doctors Flow
```
Patient accesses /doctors
    ↓
Optional: Filter by Specialty
    ↓
DoctorController.browseDoctors()
    ↓
DoctorService.getAvailableDoctorsBySpecialty()
    ↓
DoctorRepository query
    ↓
Render doctors/browse.html with doctor list
```

---

## 🎨 Key Features Implementation

### 1. Secure Registration & Login
- **Annotations Used**: `@PrePersist`, `@Valid`, `@NotBlank`
- **Security**: BCryptPasswordEncoder, UserDetailsService
- **Validation**: Bean Validation with custom error messages

### 2. Browse Doctors by Specialty
- **Query**: Custom JPQL in `DoctorRepository`
- **Filtering**: Dynamic specialty-based filtering
- **Mapping**: Entity to DTO conversion

### 3. Appointment Booking
- **Validation**: Business logic in `AppointmentService`
- **Conflict Prevention**: Database query for time slot check
- **Transaction**: `@Transactional` annotation

### 4. Cancel Appointments
- **Authorization**: Verify ownership before cancellation
- **Status Management**: Enum-based status tracking
- **Audit**: Timestamp tracking with `@PreUpdate`

---

## 📝 Notes

### Development Mode
- Uses H2 in-memory database (data resets on restart)
- SQL logging enabled for debugging
- H2 console accessible for database inspection

### Production Considerations
- Switch to MySQL/PostgreSQL
- Disable H2 console
- Configure proper session timeout
- Enable HTTPS
- Add rate limiting
- Implement email notifications
- Add appointment reminders

---

## 🎓 Architecture Highlights

### Design Patterns Used
- **MVC Pattern**: Clear separation of concerns
- **Repository Pattern**: Data access abstraction
- **DTO Pattern**: Data transfer between layers
- **Dependency Injection**: Spring's IoC container
- **Builder Pattern**: Lombok's @Data and @Builder

### SOLID Principles
- **Single Responsibility**: Each class has one purpose
- **Open/Closed**: Services are extensible
- **Dependency Inversion**: Depend on abstractions (interfaces)

### Best Practices
- Transaction management with `@Transactional`
- Proper exception handling
- Logging with SLF4J
- Input validation at multiple layers
- Responsive UI design

---

## 👨‍💻 Author

**Hospital Management Team**  
Academic Java Project - Hospital Appointment Booking System

---

## 📄 License

This project is developed for educational purposes as part of an Advanced Java Programming course.

---

## 🙏 Acknowledgments

- Spring Framework Team
- Thymeleaf Community
- Java Community

---

**Made with ❤️ using Spring Boot**
