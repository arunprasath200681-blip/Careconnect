# 🏥 CareConnect - Clinic Appointment & Patient Scheduling System

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Data JPA](https://img.shields.io/badge/Spring%20Data-JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-data-jpa)
[![MySQL](https://img.shields.io/badge/Database-MySQL%208.0-00758F?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Frontend](https://img.shields.io/badge/Frontend-HTML5%20%7C%20CSS3%20%7C%20JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)](https://developer.mozilla.org/en-US/docs/Web/JavaScript)

## 🚀 Deploy
The application can be deployed using Docker or any cloud platform that supports Java. A quick one‑click deploy is available on Render:

[![Deploy on Render](https://render.com/images/deploy-button.svg)](https://render.com/deploy?repo=https://github.com/arunprasath200681-blip/Careconnect)

**CareConnect** is a full‑stack healthcare clinic appointment management portal built strictly using **HTML, CSS, JavaScript, Java, Spring Boot, and MySQL**. It features layered enterprise architecture, Role‑Based Access Control (RBAC), database concurrency constraints, digital medical prescription issuance, and real‑time clinic analytics in **Indian Rupees (₹ INR)**.

---

## 🛠️ Core Technology Stack

| Layer | Technology | Description |
| :--- | :--- | :--- |
| **Backend Language** | **Java (v21)** | Core business logic, object‑oriented domain models, and validation |
| **Backend Framework** | **Spring Boot (v3.3.4)** | Layered MVC architecture (`Controller`, `Service`, `Repository`) |
| **Data Access / ORM** | **Spring Data JPA & JDBC** | Relational mapping, repositories, transactional queries, and database connectivity |
| **Database** | **MySQL 8.0** | Relational database (`careconnect_db`), foreign keys & unique constraints |
| **Frontend** | **HTML, CSS, JavaScript** | Semantic structure, custom responsive design, and asynchronous Fetch API |
| **Currency** | **Indian Rupees (₹ INR)** | Authentic Indian clinic consultation fees |

---

## 🚀 Key Highlights for Fresher Interviews

* **Layered Enterprise Architecture:** Clean separation of concerns across:
  $$\text{Controller} \longrightarrow \text{Service} \longrightarrow \text{Repository} \longrightarrow \text{Entity} \longrightarrow \text{MySQL Database}$$
* **Role‑Based Access Control (RBAC):** Three distinct user personas:
  * 🧑‍💼 **Patient** – Browse specialists, check real‑time availability, book OPD slots, view history, cancel visits, and view/print digital prescriptions.
  * 🩺 **Doctor** – View daily schedule and patient queue, review symptoms, update consultation status, write diagnosis notes and Rx prescriptions.
  * 🛡️ **Administrator** – Clinic overview dashboard with financial KPIs (₹), appointment status metrics (Chart.js), master appointment logs, and doctor directory management.
* **Database Concurrency & Conflict Prevention:** Enforced compound unique constraint `UNIQUE(doctor_id, appointment_date, time_slot)` preventing double‑booking at both service and database layers.
* **Auto Data Seeding:** Seeds 6 certified Indian doctors, demo patients, and appointments into MySQL on first boot.

---

## 📂 Project Structure

```
 d:\project
 ├── pom.xml                                 # Maven dependencies (Spring Web, JPA, MySQL)
 ├── schema.sql                              # Pure MySQL 8.x schema DDL script
 ├── README.md                               # Project documentation & interview guide
 ├── .vscode/                                # VS Code 1‑click Run & Debug configuration
 │   ├── launch.json                         # F5 execution config
 │   ├── tasks.json                          # Maven build tasks
 │   ├── extensions.json                     # Recommended extensions
 │   └── settings.json                       # Java compiler preferences
 └── src
     └── main
         ├── java/com/careconnect
         │   ├── CareConnectApplication.java # Spring Boot entry point
         │   ├── config
         │   │   ├── DataInitializer.java    # Seeds doctors, patients, bookings
         │   │   └── WebConfig.java          # Static resource handlers & CORS
         │   ├── controller
         │   │   ├── AuthController.java     # Register, login, profile APIs
         │   │   ├── DoctorController.java   # Doctor search, slot availability, admin CRUD
         │   │   ├── AppointmentController.java # Booking, queue, status, prescriptions
         │   │   └── DashboardController.java # KPI stats & analytics
         │   ├── dto                         # Request & response DTOs
         │   ├── entity                      # JPA entities (User, Doctor, Appointment)
         │   ├── exception                   # Centralized REST exception handling
         │   ├── repository                  # Spring Data JPA repositories
         │   └── service                     # Business logic layer
         └── resources
             ├── application.properties      # MySQL connection configuration
             └── static                      # Frontend assets
                 ├── index.html              # Pure HTML5 interface
                 ├── css/style.css           # Custom CSS styling & status badges
                 └── js/app.js               # Pure JavaScript with fetch API
```

---

## ⚡ How to Run in Visual Studio Code

### 1. Configure MySQL Database
Make sure your MySQL server is running. In `src/main/resources/application.properties`, verify your credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/careconnect_db?createDatabaseIfNotExists=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=Arun@2006
```

### 2. Run the Application in VS Code
* Open **`D:\project`** in VS Code.
* Open `src/main/java/com/careconnect/CareConnectApplication.java`.
* Click the **▶ Run** button above `public static void main` (or press **F5**).
* Or use the terminal:
```powershell
mvn spring-boot:run
```

### 3. Open in Browser
* **Web Portal:** [http://localhost:8080](http://localhost:8080)

---

## 🔑 Pre‑Seeded Demo Credentials (Indian Context)

| Role | Name | Email | Password | Notes |
| :--- | :--- | :--- | :--- | :--- |
| **Patient** | Rahul Sharma | `rahul@gmail.com` | `patient123` | Books OPD slots, views history, prints prescriptions |
| **Doctor** | Dr. Rajesh Iyer (Cardiology) | `dr.rajesh@careconnect.in` | `doctor123` | Fee: ₹1,000 • AIIMS New Delhi • Issues Rx |
| **Doctor** | Dr. Suresh Menon (Neurology) | `dr.suresh@careconnect.in` | `doctor123` | Fee: ₹1,200 • NIMHANS Bangalore • Issues Rx |
| **Doctor** | Dr. Priya Sharma (Dermatology) | `dr.priya@careconnect.in` | `doctor123` | Fee: ₹750 • CMC Vellore • Issues Rx |
| **Doctor** | Dr. Arvind Swaminathan (Ortho) | `dr.arvind@careconnect.in` | `doctor123` | Fee: ₹900 • MMC Chennai • Issues Rx |
| **Doctor** | Dr. Kavitha Raman (Pediatrics) | `dr.kavitha@careconnect.in` | `doctor123` | Fee: ₹650 • PGIMER Chandigarh • Issues Rx |
| **Doctor** | Dr. Amit Verma (General Med) | `dr.amit@careconnect.in` | `doctor123` | Fee: ₹500 • KMC Manipal • Issues Rx |
| **Admin** | Arun Kumar | `admin@careconnect.in` | `admin123` | Views revenue KPIs (₹), Chart.js graphs, registers doctors |

---

## 🎯 How to Explain This Project in an Interview

When asked *"Tell me about your Java project."*:
1. **Tech Stack Overview:** *"I built CareConnect using a focused, industry‑standard stack: Java 21 and Spring Boot for the backend RESTful service layer, MySQL for relational persistence, and HTML5, CSS3, JavaScript on the frontend."*
2. **Architecture:** *"I followed a clean layered architecture: Controllers handle HTTP requests, Services contain business logic, Repositories interface with MySQL via Spring Data JPA, and the frontend consumes JSON APIs with modern `fetch` calls."*
3. **Database Design & Concurrency:** *"I designed normalized tables with foreign keys and indexes. To prevent double‑booking, I enforced a compound unique constraint on `(doctor_id, appointment_date, time_slot)`, reinforced by service‑level validation."*
