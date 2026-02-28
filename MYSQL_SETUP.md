# MySQL Database Setup Guide

## Prerequisites
- MySQL Server 8.0 or higher installed on your system
- MySQL running on default port 3306

## Setup Instructions

### 1. Install MySQL (if not already installed)

**Windows:**
- Download MySQL Installer from: https://dev.mysql.com/downloads/installer/
- Run the installer and select "MySQL Server"
- Set root password during installation (default in config: "root")

**macOS:**
```bash
brew install mysql
brew services start mysql
```

**Linux:**
```bash
sudo apt-get update
sudo apt-get install mysql-server
sudo systemctl start mysql
```

### 2. Create Database

The application is configured to auto-create the database, but you can also create it manually:

**Option A: Auto-create (Recommended)**
- The database will be created automatically when you run the application
- This is enabled by the `createDatabaseIfNotExist=true` parameter in the connection URL

**Option B: Manual creation**
```sql
-- Login to MySQL
mysql -u root -p

-- Create database
CREATE DATABASE hospitaldb;

-- Verify
SHOW DATABASES;

-- Exit
EXIT;
```

### 3. Configure Database Credentials

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hospitaldb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

**Important:** Change `your_mysql_password` to your actual MySQL root password!

### 4. Verify MySQL is Running

**Windows:**
```cmd
sc query MySQL80
```

**macOS/Linux:**
```bash
sudo systemctl status mysql
# or
ps aux | grep mysql
```

### 5. Start the Application

```bash
mvn spring-boot:run
```

### 6. Verify Database Creation

```sql
mysql -u root -p

USE hospitaldb;

SHOW TABLES;
-- Should show: appointments, doctors, patients, specialties, user_roles, users

-- View sample data
SELECT * FROM specialties;
SELECT * FROM doctors;
SELECT * FROM users;
```

## Current Configuration

The application is configured with:
- **Host:** localhost
- **Port:** 3306
- **Database:** hospitaldb
- **Username:** root
- **Password:** root (⚠️ Change this!)
- **Hibernate DDL:** update (preserves data between restarts)

## Troubleshooting

### Connection Refused Error
```
Error: Communications link failure
```
**Solution:** Make sure MySQL is running:
```bash
# Windows
net start MySQL80

# macOS
brew services start mysql

# Linux
sudo systemctl start mysql
```

### Access Denied Error
```
Error: Access denied for user 'root'@'localhost'
```
**Solution:** 
1. Verify your password in application.properties
2. Reset MySQL root password if needed:
```bash
mysql -u root -p
ALTER USER 'root'@'localhost' IDENTIFIED BY 'newpassword';
FLUSH PRIVILEGES;
```

### Database Not Found
```
Error: Unknown database 'hospitaldb'
```
**Solution:** The `createDatabaseIfNotExist=true` parameter should handle this, but if it doesn't work:
```sql
CREATE DATABASE hospitaldb;
```

### Port Already in Use
```
Error: Port 3306 is already in use
```
**Solution:** Check if another MySQL instance is running or change the port in application.properties

## Production Recommendations

1. **Change default password:**
   ```properties
   spring.datasource.password=${DB_PASSWORD:root}
   ```
   Set environment variable: `DB_PASSWORD=your_secure_password`

2. **Use connection pooling:**
   ```properties
   spring.datasource.hikari.maximum-pool-size=10
   spring.datasource.hikari.minimum-idle=5
   ```

3. **Enable SSL (production):**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/hospitaldb?useSSL=true
   ```

4. **Change ddl-auto to validate:**
   ```properties
   spring.jpa.hibernate.ddl-auto=validate
   ```
   Use Flyway or Liquibase for database migrations

5. **Create separate database user:**
   ```sql
   CREATE USER 'hospitalapp'@'localhost' IDENTIFIED BY 'secure_password';
   GRANT ALL PRIVILEGES ON hospitaldb.* TO 'hospitalapp'@'localhost';
   FLUSH PRIVILEGES;
   ```

## Database Schema

The following tables will be created automatically:

- `users` - User authentication information
- `user_roles` - User role assignments
- `patients` - Patient profile data
- `specialties` - Medical specialties
- `doctors` - Doctor information
- `appointments` - Appointment bookings

## Sample Data

The application automatically populates sample data on startup:
- 5 medical specialties
- 6 doctors across different specialties
- 1 test patient (username: patient1, password: password123)

To disable sample data loading, comment out or remove the `DataInitializer` class.

## Backup & Restore

**Backup:**
```bash
mysqldump -u root -p hospitaldb > hospitaldb_backup.sql
```

**Restore:**
```bash
mysql -u root -p hospitaldb < hospitaldb_backup.sql
```

## Useful MySQL Commands

```sql
-- Show all databases
SHOW DATABASES;

-- Use hospitaldb
USE hospitaldb;

-- Show all tables
SHOW TABLES;

-- Describe table structure
DESCRIBE users;
DESCRIBE appointments;

-- Count records
SELECT COUNT(*) FROM appointments;

-- View recent appointments
SELECT * FROM appointments ORDER BY created_at DESC LIMIT 10;

-- Check doctor availability
SELECT d.first_name, d.last_name, s.name as specialty, d.available 
FROM doctors d 
JOIN specialties s ON d.specialty_id = s.id;
```

---

**Need Help?** Check the main [README.md](README.md) for more information.
