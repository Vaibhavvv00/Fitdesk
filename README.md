# FitDesk 🏋️‍♂️

**A Gym & Fitness Studio Management System** built with Spring Boot, Spring Security (JWT), and MySQL — with a lightweight HTML/CSS/JS frontend for managing members, trainers, plans, payments, and attendance.

---

## ✨ Features

- 🔐 **Secure Authentication** — JWT-based login/auth system
- 👥 **Member Management** — add, update, and track gym members
- 🏋️ **Trainer Management** — manage trainer records and assignments
- 📋 **Membership Plans** — create and manage subscription plans
- 💳 **Payment Tracking** — record and monitor member payments
- 📅 **Attendance Tracking** — log and view member attendance
- 📊 **Dashboard** — overview of key gym metrics
- 🌱 **Data Seeder** — preloads sample/initial data on startup

---

## 🛠️ Tech Stack

**Backend**
- Java 21
- Spring Boot 3.2.5 (Web, Data JPA, Security)
- MySQL (via MySQL Connector/J)
- JWT (`jjwt`) for stateless authentication
- Lombok
- Maven

**Frontend**
- HTML, CSS, JavaScript (served as static resources)

---

## 📂 Project Structure

```
fitdesk/
├── sql/
│   └── schema.sql                     # Database schema
├── src/
│   ├── main/
│   │   ├── java/com/fitdesk/
│   │   │   ├── config/
│   │   │   │   ├── DataSeeder.java        # Seeds initial data
│   │   │   │   ├── JwtAuthFilter.java     # JWT request filter
│   │   │   │   ├── JwtUtil.java           # JWT generation/validation
│   │   │   │   ├── SecurityConfig.java    # Spring Security config
│   │   │   │   └── WebConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AttendanceController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── DashboardController.java
│   │   │   │   ├── MemberController.java
│   │   │   │   ├── PaymentController.java
│   │   │   │   ├── PlanController.java
│   │   │   │   └── TrainerController.java
│   │   │   ├── entity/
│   │   │   │   ├── AdminUser.java
│   │   │   │   ├── Attendance.java
│   │   │   │   ├── Member.java
│   │   │   │   ├── Payment.java
│   │   │   │   ├── Plan.java
│   │   │   │   └── Trainer.java
│   │   │   ├── repository/
│   │   │   │   ├── AdminUserRepository.java
│   │   │   │   ├── AttendanceRepository.java
│   │   │   │   ├── MemberRepository.java
│   │   │   │   ├── PaymentRepository.java
│   │   │   │   ├── PlanRepository.java
│   │   │   │   └── TrainerRepository.java
│   │   │   ├── service/
│   │   │   └── FitDeskApplication.java
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/style.css
│   │       │   ├── js/
│   │       │   ├── index.html
│   │       │   ├── dashboard.html
│   │       │   ├── members.html
│   │       │   ├── trainers.html
│   │       │   ├── plans.html
│   │       │   ├── payments.html
│   │       │   └── attendance.html
│   │       └── application.properties
├── pom.xml
└── README.md
```

---

## ⚙️ Prerequisites

- [Java 21 JDK](https://adoptium.net/)
- [Maven](https://maven.apache.org/download.cgi)
- [MySQL Server](https://dev.mysql.com/downloads/mysql/)
- IntelliJ IDEA (recommended) or any Java IDE

---

## 🚀 Getting Started

### 1. Clone the repository
```bash
https://github.com/Vaibhavvv00/Fitdesk
cd fitdesk
```

### 2. Create the database
```sql
CREATE DATABASE fitdesk;
```

Import the schema (optional, if not using auto-DDL):
```bash
mysql -u your_username -p fitdesk < sql/schema.sql
```

### 3. Configure application properties
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/fitdesk
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=your_jwt_secret_key
```

### 4. Build the project
```bash
mvn clean install
```

### 5. Run the application
```bash
mvn spring-boot:run
```
Or run the packaged JAR:
```bash
java -jar target/fitdesk-1.0.0.jar
```

The app will be available at:
```
http://localhost:8080
```

---

## 🔑 Authentication

FitDesk uses **JWT-based authentication** via `AuthController`. After logging in, include the returned token in the `Authorization` header for protected requests:

```
Authorization: Bearer <your_token_here>
```

`JwtAuthFilter` and `JwtUtil` handle token validation and parsing on each request, secured through `SecurityConfig`.

---

## 🧩 Core Modules

| Module         | Controller              | Entity        | Repository              |
|----------------|--------------------------|---------------|--------------------------|
| Authentication | `AuthController`         | `AdminUser`   | `AdminUserRepository`    |
| Members        | `MemberController`       | `Member`      | `MemberRepository`       |
| Trainers       | `TrainerController`      | `Trainer`     | `TrainerRepository`      |
| Plans          | `PlanController`         | `Plan`        | `PlanRepository`         |
| Payments       | `PaymentController`      | `Payment`     | `PaymentRepository`      |
| Attendance     | `AttendanceController`   | `Attendance`  | `AttendanceRepository`   |
| Dashboard      | `DashboardController`    | —             | —                        |

---

## 🖥️ Frontend Pages

Static pages served from `src/main/resources/static/`:

- `index.html` — Login / landing page
- `dashboard.html` — Overview dashboard
- `members.html` — Member management
- `trainers.html` — Trainer management
- `plans.html` — Membership plans
- `payments.html` — Payment records
- `attendance.html` — Attendance logs

---

## 🌱 Sample Data

On startup, `DataSeeder` populates the database with initial sample records (e.g., default admin user, sample plans) so the app is usable immediately after setup.

---

## 📄 License

This project is available for educational and personal use.

---

## 👤 Author

Built by Vaibhav.
