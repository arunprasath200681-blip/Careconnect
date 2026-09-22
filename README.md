# 🏥 CareConnect - Smart Clinic Appointment & Patient Scheduling Portal

[![Java](https://img.shields.io/badge/Java-22%20%7C%2017-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Data JPA](https://img.shields.io/badge/Spring%20Data-JPA-6DB33F?style=for-the-badge&logo=hibernate&logoColor=white)](https://spring.io/projects/spring-data-jpa)
[![Database](https://img.shields.io/badge/Database-H2%20%2F%20MySQL%208-00758F?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Frontend](https://img.shields.io/badge/Frontend-HTML5%20%2F%20TailwindCSS%20%2F%20JavaScript-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)](https://tailwindcss.com/)

**CareConnect** is an enterprise-grade full-stack clinic appointment scheduling web application built with **Java 22, Spring Boot 3, Spring Data JPA/Hibernate, Relational SQL, and Modern JavaScript**. It is engineered with clean layered architecture, Role-Based Access Control (RBAC), database concurrency constraints, digital medical prescription issuance, and real-time clinic analytics in **Indian Rupees (₹ INR)**.

---

## 🚀 Key Highlights for Fresher Interviews

* **Layered Enterprise Architecture:** Clean separation of concerns across `Controller` $\rightarrow$ `Service` $\rightarrow$ `Repository` $\rightarrow$ `Entity` $\rightarrow$ `Database`.
* **Role-Based Access Control (RBAC):** Three distinct user personas:
  * 🧑‍💼 **Patient (Rahul Sharma):** Browse specialists, check real-time availability, book OPD slots, view history, cancel visits, and view/print digital prescriptions.
  * 🩺 **Doctor (Dr. Rajesh Iyer):** View daily schedule and patient queue, review reported symptoms, update consultation status, and write diagnosis notes and Rx prescriptions.
  * 🛡️ **Administrator (Arun Kumar):** Clinic overview dashboard with financial KPIs (in ₹), appointment status metrics (Chart.js), master appointment logs, and doctor directory management.
* **Database Concurrency & Conflict Prevention:** Strict unique relational constraint `UNIQUE(doctor_id, appointment_date, time_slot)` preventing double-booking at both the business-logic layer and the database-schema layer.
* **Out-of-the-Box Execution:** Pre-configured with an embedded H2 database and auto-seeded with 6 certified doctors from premier Indian medical institutes (AIIMS, CMC Vellore, NIMHANS, Madras Medical College, PGIMER, KMC), demo patients, and appointments. Works on any machine with `mvn spring-boot:run` without requiring external database installation, and easily switches to MySQL with 1 configuration line.

---

## 🛠️ Technology Stack

| Layer | Technology |
| :--- | :--- |
| **Backend Framework** | **Spring Boot 3.3.4 (Java 22 / 17)** |
| **Persistence & ORM** | **Spring Data JPA & Hibernate 6** |
| **Validation** | **Jakarta Bean Validation (`@Valid`, `@NotNull`, `@NotBlank`)** |
| **Database** | **Embedded H2** (Default) + **MySQL 8.x** (Profile included) |
| **Frontend** | **HTML5, CSS3, Tailwind CSS (CDN), FontAwesome 6, Chart.js** |
| **Client-Server Comm** | **Asynchronous JavaScript (`fetch` API) & RESTful JSON endpoints** |
| **Currency** | **Indian Rupees (₹ INR)** |
| **Build Tool** | **Apache Maven 3.9+** |

---

## 📂 Project Architecture

```
d:\project
├── pom.xml                                 # Maven dependencies & build configuration
├── schema.sql                              # Production SQL DDL schema with indexes
├── README.md                               # Project documentation & interview guide
├── .vscode/                                # Visual Studio Code 1-click Run & Debug configuration
│   ├── launch.json                         # F5 Run configuration
│   ├── tasks.json                          # Maven build tasks
│   ├── extensions.json                     # Recommended Java & Spring extensions
│   └── settings.json                       # Java compiler preferences
└── src
    └── main
        ├── java/com/careconnect
        │   ├── CareConnectApplication.java # Spring Boot entry point
        │   ├── config
        │   │   ├── DataInitializer.java    # Seeds Indian doctors, patients, and bookings
        │   │   └── WebConfig.java          # Static resource handlers & CORS config
        │   ├── controller
        │   │   ├── AuthController.java     # Register, Login, User profile
        │   │   ├── DoctorController.java   # Doctor search, availability, admin CRUD
        │   │   ├── AppointmentController.java # Booking, queue, status, prescriptions
        │   │   └── DashboardController.java # KPI stats & analytics data
        │   ├── dto                         # Clean Request & Response DTOs
        │   ├── entity                      # JPA Database Entities (User, Doctor, Appointment)
        │   ├── exception                   # Centralized REST Exception Handling (@RestControllerAdvice)
        │   ├── repository                  # Spring Data JPA Repositories
        │   └── service                     # Business Logic Layer
        └── resources
            ├── application.properties      # Active H2 configuration & server settings
            ├── application-mysql.properties# Pre-configured MySQL 8 profile
            └── static                      # Modern Web Application Assets
                ├── index.html              # Responsive single-page web portal (Tailwind CSS)
                ├── css/style.css           # Glassmorphism, animations, print styling
                └── js/app.js               # Frontend controller & INR formatting
```

---

## ⚡ Quick Start & Running in VS Code

1. Open **VS Code** $\rightarrow$ **`File`** > **`Open Folder...`** $\rightarrow$ Select **`D:\project`**.
2. Install the **Extension Pack for Java** (by Microsoft) in Extensions (`Ctrl+Shift+X`).
3. Press **`F5`** or click **`Run`** above `main()` in [`CareConnectApplication.java`](file:///d:/project/src/main/java/com/careconnect/CareConnectApplication.java).
4. Open your browser and go to **[http://localhost:8080](http://localhost:8080)**.

---

## 🔑 Pre-Seeded Demo Credentials (Indian Context)

You can use the **Quick 1-Click Role Switcher** at the top bar of the webpage:

| Role | Name | Email | Password | Consultation Fee / Access |
| :--- | :--- | :--- | :--- | :--- |
| **Patient** | Rahul Sharma | `rahul@gmail.com` | `patient123` | Books OPD slots, views history, prints prescriptions |
| **Doctor** | Dr. Rajesh Iyer (Cardiology) | `dr.rajesh@careconnect.in` | `doctor123` | Consultation Fee: ₹1,000 &bull; Issues Rx prescriptions |
| **Doctor** | Dr. Suresh Menon (Neurology) | `dr.suresh@careconnect.in` | `doctor123` | Consultation Fee: ₹1,200 &bull; Issues Rx prescriptions |
| **Doctor** | Dr. Priya Sharma (Dermatology) | `dr.priya@careconnect.in` | `doctor123` | Consultation Fee: ₹750 &bull; Issues Rx prescriptions |
| **Doctor** | Dr. Arvind Swaminathan (Ortho) | `dr.arvind@careconnect.in` | `doctor123` | Consultation Fee: ₹900 &bull; Issues Rx prescriptions |
| **Doctor** | Dr. Kavitha Raman (Pediatrics) | `dr.kavitha@careconnect.in` | `doctor123` | Consultation Fee: ₹650 &bull; Issues Rx prescriptions |
| **Doctor** | Dr. Amit Verma (General Med) | `dr.amit@careconnect.in` | `doctor123` | Consultation Fee: ₹500 &bull; Issues Rx prescriptions |
| **Admin** | Arun Kumar | `admin@careconnect.in` | `admin123` | Views revenue KPIs (₹), Chart.js graphs, registers doctors |

---

## 🎯 How to Explain This Project in an Interview

When asked: *"Tell me about a project you've built."*

1. **Problem Statement:**
   > *"I built CareConnect, a full-stack clinic appointment scheduling portal tailored for healthcare clinics to eliminate long OPD waiting lines, automate doctor slot booking, and store digital prescriptions."*
2. **Architecture:**
   > *"I structured the backend using Spring Boot 3 following the Controller-Service-Repository pattern with Spring Data JPA. For the data layer, I designed a normalized relational database schema with compound unique constraints to prevent double-booking."*
3. **Key Technical Challenge:**
   > *"Preventing concurrent slot conflicts was a key focus. If two patients attempt to book the same doctor at the same date and time, the application verifies slot availability in the service layer and enforces a database constraint `UNIQUE(doctor_id, appointment_date, time_slot)` to reject duplicates with a clean 400 Bad Request error handled centrally by `@RestControllerAdvice`."*
4. **Localization & Usability:**
   > *"The frontend features clean role-based dashboards for Patients, Doctors, and Administrators with fees modeled in Indian Rupees (₹), dynamic time slots, and a printable prescription layout."*
